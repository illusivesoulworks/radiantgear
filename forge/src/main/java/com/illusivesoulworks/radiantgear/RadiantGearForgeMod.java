package com.illusivesoulworks.radiantgear;

import com.illusivesoulworks.radiantgear.integration.arsnouveau.ArsNouveauModule;
import com.illusivesoulworks.radiantgear.integration.dynamiclights.DynamicLightsModule;
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

@Mod(RadiantGearConstants.MOD_ID)
public class RadiantGearForgeMod {

  private static boolean isDynamicLightsLoaded = false;
  private static boolean isArsNouveauLoaded = false;

  public RadiantGearForgeMod() {
    ModList modList = ModList.get();
    isDynamicLightsLoaded = modList.isLoaded("dynamiclights");
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