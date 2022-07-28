package top.theillusivec4.curiouslights.dynamiclightsreforged;

import dev.lambdaurora.lambdynlights.api.item.ItemLightSources;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;


public class ReforgedCuriosLightSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Player, ReforgedCuriosLightSourceContainer> playerLightsMap =
            new HashMap<>();

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent evt) {
        if (evt.side != LogicalSide.CLIENT) {
            return;
        }

        Player player = evt.player;
        ReforgedCuriosLightSourceContainer lightSourceContainer = playerLightsMap.get(player);

        if (player.isAlive()) {
            if (lightSourceContainer == null) {
                LOGGER.trace("built new ReforgedCuriosLightSourceContainer for player {}", player);
                lightSourceContainer = new ReforgedCuriosLightSourceContainer(player);
                playerLightsMap.put(player, lightSourceContainer);
            }

            int prevLight = lightSourceContainer.lightLevel;
            boolean isUnderwater = checkPlayerWater(player);
            lightSourceContainer.lightLevel = 0;
            ReforgedCuriosLightSourceContainer finalLightSourceContainer = lightSourceContainer;

            CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(
                curios -> {
                    for (int i = 0; i < curios.getSlots(); i++) {
                        ItemStack stack = curios.getStackInSlot(i);

                        if (!stack.isEmpty()) {
                            finalLightSourceContainer.lightLevel =
                                Math.max(finalLightSourceContainer.lightLevel,
                                        getLightFromItemStack(stack, isUnderwater)
                                );
                        }
                    }
                }
            );

            if (prevLight != 0 && lightSourceContainer.lightLevel != prevLight) {
                lightSourceContainer.lightLevel = 0;
            }

            if (!lightSourceContainer.tdv$isDynamicLightEnabled() && lightSourceContainer.lightLevel > 0) {
                lightSourceContainer.tdv$setDynamicLightEnabled(true);
            } else if (lightSourceContainer.tdv$isDynamicLightEnabled() && lightSourceContainer.lightLevel < 1) {
                lightSourceContainer.tdv$setDynamicLightEnabled(false);
            }
        } else {
            if (lightSourceContainer != null) {
                playerLightsMap.remove(player);
            }
        }
    }

    private boolean checkPlayerWater(Player player) {
        return player.isEyeInFluid(FluidTags.WATER);
    }

    private int getLightFromItemStack(ItemStack stack, boolean isUnderwater) {
        return ItemLightSources.getLuminance(stack, isUnderwater);
    }

}
