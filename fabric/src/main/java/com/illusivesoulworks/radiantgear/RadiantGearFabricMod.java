package com.illusivesoulworks.radiantgear;

import com.illusivesoulworks.radiantgear.integration.lambdynlights.LambDynLightsModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RadiantGearFabricMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    if (FabricLoader.getInstance().isModLoaded("lambdynlights")) {
      LambDynLightsModule.setup();
    }
  }
}
