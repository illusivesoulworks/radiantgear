package com.illusivesoulworks.radiantgear.integration.lambdynlights;

import com.illusivesoulworks.radiantgear.RadiantGearConstants;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LambDynLightsModule {

  private static final Set<EntityType<?>> PROCESSED = new HashSet<>();

  public static void setup() {
    ClientEntityEvents.ENTITY_LOAD.register(LambDynLightsModule::entityJoin);
  }

  private static void entityJoin(Entity entity, ClientLevel serverLevel) {

    if (entity instanceof LivingEntity livingEntity) {
      EntityType<?> type = livingEntity.getType();

      if (!PROCESSED.contains(type)) {
        PROCESSED.add(type);
        TrinketsApi.getTrinketComponent(livingEntity).ifPresent(trinketComponent -> {
          RadiantGearConstants.LOG.debug("Registering trinket lights for " + type);
          DynamicLightHandlers.registerDynamicLightHandler(type, entity1 -> {
            if (entity1 instanceof LivingEntity livingEntity1) {
              AtomicInteger luminance = new AtomicInteger(0);
              for (Tuple<SlotReference, ItemStack> slotReferenceItemStackTuple : trinketComponent.getAllEquipped()) {
                ItemStack stack = slotReferenceItemStackTuple.getB();
                luminance.set(Math.max(luminance.get(),
                    LambDynLights.getLuminanceFromItemStack(stack, livingEntity1.isInWater())));
              }
              return luminance.get();
            }
            return 0;
          });
        });
      }
    }
  }
}
