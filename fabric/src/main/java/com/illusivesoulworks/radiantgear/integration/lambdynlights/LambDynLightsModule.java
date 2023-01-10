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
              TrinketsApi.getTrinketComponent(livingEntity1).ifPresent(actualTrinketComponent -> {
                for (Tuple<SlotReference, ItemStack> slotReferenceItemStackTuple : actualTrinketComponent.getAllEquipped()) {
                  ItemStack stack = slotReferenceItemStackTuple.getB();
                  luminance.set(Math.max(luminance.get(),
                      LambDynLights.getLuminanceFromItemStack(stack, livingEntity1.isInWater())));
                }
              });
              return luminance.get();
            }
            return 0;
          });
        });
      }
    }
  }
}
