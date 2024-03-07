package com.illusivesoulworks.radiantgear.integration.ryoamiclights;

import com.illusivesoulworks.radiantgear.client.BaseLambDynLightsModule;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.thinkingstudio.ryoamiclights.RyoamicLights;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;

public class RyoamicModule extends BaseLambDynLightsModule {

  public static void setup() {
    RyoamicModule module = new RyoamicModule();
    MinecraftForge.EVENT_BUS.addListener(module::entityJoin);
  }

  private void entityJoin(final EntityJoinLevelEvent evt) {
    this.registerEntity(evt.getEntity(), evt.getLevel());
  }

  @Override
  protected int getLuminance(ItemStack stack, boolean isInWater) {
    return RyoamicLights.getLuminanceFromItemStack(stack, isInWater);
  }

  @Override
  protected void registerDynamicLightHandler(EntityType<?> type,
                                             Function<Entity, Integer> handler) {
    DynamicLightHandlers.registerDynamicLightHandler(type, handler::apply);
  }
}
