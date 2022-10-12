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

package top.theillusivec4.curiouslights;

import java.util.Objects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curiouslights.arsnouveau.ArsNouveauModule;
import top.theillusivec4.curiouslights.dynamiclights.DynamicLightsModule;
import top.theillusivec4.curiouslights.dynamiclightsreforged.DLReforgedModule;

@Mod(CuriousLights.MOD_ID)
public class CuriousLights {

  public static final String MOD_ID = "curiouslights";
  public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

  private static boolean isDynamicLightsLoaded = false;
  private static boolean isDLReforgedLoaded = false;
  private static boolean isArsNouveauLoaded = false;

  public CuriousLights() {
    ModList modList = ModList.get();
    isDynamicLightsLoaded = modList.isLoaded("dynamiclights");
    isDLReforgedLoaded = modList.isLoaded("dynamiclightsreforged");
    isArsNouveauLoaded = modList.isLoaded("ars_nouveau");
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    ModLoadingContext context = ModLoadingContext.get();
    context.registerExtensionPoint(IExtensionPoint.DisplayTest.class,
        () -> new IExtensionPoint.DisplayTest(() -> getRemoteVersion(context),
            (incoming, isNetwork) -> acceptsServer(context, incoming)));
  }

  private void setup(final FMLCommonSetupEvent evt) {

    if (isDynamicLightsLoaded) {
      DynamicLightsModule.setup();
    }
  }

  private void clientSetup(final FMLClientSetupEvent evt) {

    if (isDLReforgedLoaded) {
      DLReforgedModule.setup();
    }

    if (isArsNouveauLoaded) {
      ArsNouveauModule.setup();
    }
  }

  private String getRemoteVersion(ModLoadingContext context) {

    if (isDynamicLightsLoaded) {
      return context.getActiveContainer().getModInfo().getVersion().toString();
    }
    return NetworkConstants.IGNORESERVERONLY;
  }

  private boolean acceptsServer(ModLoadingContext context, String incoming) {

    if (isDynamicLightsLoaded) {
      return Objects.equals(incoming,
          context.getActiveContainer().getModInfo().getVersion().toString());
    }
    return true;
  }
}
