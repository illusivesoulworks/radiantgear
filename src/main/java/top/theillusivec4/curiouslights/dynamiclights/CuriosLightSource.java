/*
 * Copyright (C) 2022 C4
 *
 * This file is part of Curious Lights.
 *
 * Curious Lights is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curious Lights is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Curious Lights.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.curiouslights.dynamiclights;

import atomicstryker.dynamiclights.server.DynamicLights;
import atomicstryker.dynamiclights.server.GsonConfig;
import atomicstryker.dynamiclights.server.IDynamicLightSource;
import atomicstryker.dynamiclights.server.ItemConfigHelper;
import atomicstryker.dynamiclights.server.modules.LightConfig;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@SuppressWarnings("unused")
public class CuriosLightSource {

  private static final Logger LOGGER = LogManager.getLogger();
  private static ItemConfigHelper itemsMap;
  private static ItemConfigHelper notWaterProofItems;
  private final Map<Player, CuriosLightSourceContainer> playerLightsMap = new HashMap<>();

  @SubscribeEvent
  public void serverStartEvent(ServerAboutToStartEvent evt) {
    LightConfig defaultConfig = new LightConfig();
    String torchString = ItemConfigHelper.fromItemStack(new ItemStack(Blocks.TORCH));
    defaultConfig.getItemsList().add(torchString);
    defaultConfig.getItemsList()
        .add(ItemConfigHelper.fromItemStack(new ItemStack(Blocks.GLOWSTONE)));
    defaultConfig.getNotWaterProofList().add(torchString);
    MinecraftServer server = evt.getServer();
    File configFile = new File(server.getFile(""),
        File.separatorChar + "config" + File.separatorChar + "dynamiclights_selflight.cfg");
    try {
      LightConfig config =
          GsonConfig.loadConfigWithDefault(LightConfig.class, configFile, defaultConfig);
      if (config == null) {
        throw new UnsupportedOperationException(
            "CuriosLightSource failed parsing config file somehow...");
      }
      itemsMap = new ItemConfigHelper(config.getItemsList(), LOGGER);
      notWaterProofItems = new ItemConfigHelper(config.getNotWaterProofList(), LOGGER);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SubscribeEvent
  public void playerTick(TickEvent.PlayerTickEvent evt) {

    if (evt.side != LogicalSide.SERVER) {
      return;
    }
    Player player = evt.player;

    if (player.isAlive()) {
      CuriosLightSourceContainer curiosLightSourceContainer = playerLightsMap.get(player);

      if (curiosLightSourceContainer == null) {
        LOGGER.trace("built new CuriosLightSourceContainer for player {}", player);
        curiosLightSourceContainer = new CuriosLightSourceContainer(player);
        playerLightsMap.put(player, curiosLightSourceContainer);
      }
      int prevLight = curiosLightSourceContainer.lightLevel;
      curiosLightSourceContainer.lightLevel = 0;
      CuriosLightSourceContainer finalCuriosLightSourceContainer = curiosLightSourceContainer;
      CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(curios -> {
        for (int i = 0; i < curios.getSlots(); i++) {
          ItemStack stack = curios.getStackInSlot(i);

          if (!checkPlayerWater(player) || !notWaterProofItems.contains(stack)) {
            finalCuriosLightSourceContainer.lightLevel =
                Math.max(finalCuriosLightSourceContainer.lightLevel, getLightFromItemStack(stack));
          }
        }
      });

      if (prevLight != 0 && curiosLightSourceContainer.lightLevel != prevLight) {
        curiosLightSourceContainer.lightLevel = 0;
      }

      if (!curiosLightSourceContainer.enabled && curiosLightSourceContainer.lightLevel > 0) {
        enableLight(curiosLightSourceContainer);
      } else if (curiosLightSourceContainer.enabled && curiosLightSourceContainer.lightLevel < 1) {
        disableLight(curiosLightSourceContainer);
      }
    } else {
      CuriosLightSourceContainer curiosLightSourceContainer = playerLightsMap.get(evt.player);

      if (curiosLightSourceContainer != null) {
        disableLight(curiosLightSourceContainer);
        playerLightsMap.remove(curiosLightSourceContainer.player);
      }
    }
  }

  private boolean checkPlayerWater(Player pPlayer) {

    if (pPlayer.isInWater()) {
      int x = Mth.floor(pPlayer.getX() + 0.5D);
      int y = Mth.floor(pPlayer.getY() + pPlayer.getEyeHeight());
      int z = Mth.floor(pPlayer.getZ() + 0.5D);
      BlockState is = pPlayer.level.getBlockState(new BlockPos(x, y, z));
      return is.getMaterial().isLiquid();
    }
    return false;
  }

  private int getLightFromItemStack(ItemStack stack) {

    if (itemsMap.contains(stack)) {
      return 15;
    }
    return 0;
  }

  private void enableLight(CuriosLightSourceContainer container) {
    DynamicLights.addLightSource(container);
    container.enabled = true;
  }

  private void disableLight(CuriosLightSourceContainer container) {
    DynamicLights.removeLightSource(container);
    container.enabled = false;
  }

  public static class CuriosLightSourceContainer implements IDynamicLightSource {

    public int lightLevel;
    public boolean enabled;
    public Player player;

    CuriosLightSourceContainer(Player player) {
      this.player = player;
      this.lightLevel = 0;
      this.enabled = false;
    }

    @Override
    public Entity getAttachmentEntity() {
      return player;
    }

    @Override
    public int getLightLevel() {
      return lightLevel;
    }
  }
}
