/*
 * Copyright (C) 2022 Illusive Soulworks
 *
 * Radiant Gear is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Radiant Gear is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Radiant Gear.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.radiantgear;

import com.illusivesoulworks.radiantgear.integration.arsnouveau.ArsNouveauModule;
import com.illusivesoulworks.radiantgear.integration.dynamiclights.DynamicLightsModule;
import com.illusivesoulworks.radiantgear.integration.dynamiclightsreforged.DLReforgedModule;
import com.illusivesoulworks.radiantgear.integration.embeddiumplus.EmbeddiumPlusModule;
import com.illusivesoulworks.radiantgear.integration.ryoamiclights.RyoamicModule;
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
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@Mod(RadiantGearConstants.MOD_ID)
public class RadiantGearForgeMod {

  private static boolean isDynamicLightsLoaded = false;
  private static boolean isDLReforgedLoaded = false;
  private static boolean isArsNouveauLoaded = false;
  private static boolean isRyoamicLoaded = false;
  private static boolean isEmbeddiumPlusLoaded = false;

  public RadiantGearForgeMod() {
    ModList modList = ModList.get();
    isDynamicLightsLoaded = modList.isLoaded("dynamiclights");
    isDLReforgedLoaded = modList.isLoaded("dynamiclightsreforged");
    isArsNouveauLoaded = modList.isLoaded("ars_nouveau");
    isRyoamicLoaded = modList.isLoaded("ryoamiclights");
    isSodiumDynamicLights = modList.isLoaded("sodiumdynamiclights");
    // Embeddium++ removed their dynamic lighting in 1.2.4 but this mod should still be able to load
    if (modList.isLoaded("embeddiumplus")) {
      DefaultArtifactVersion maxVersion = new DefaultArtifactVersion("1.2.4");
      DefaultArtifactVersion currentVersion =
          new DefaultArtifactVersion(modList.getModFileById("embeddiumplus").versionString());
      isEmbeddiumPlusLoaded = currentVersion.compareTo(maxVersion) < 0;
    }
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

    if (isRyoamicLoaded) {
      RyoamicModule.setup();
    }

    if (isEmbeddiumPlusLoaded) {
      EmbeddiumPlusModule.setup();
    }

    if (isArsNouveauLoaded) {
      ArsNouveauModule.setup();
    }

    if (isSodiumDynamicLights) {
      SDLModule.setup();
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