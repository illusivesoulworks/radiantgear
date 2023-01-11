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

import com.illusivesoulworks.radiantgear.client.BaseLambDynLightsModule;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import java.util.function.Function;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class LambDynLightsModule extends BaseLambDynLightsModule {

  public static void setup() {
    LambDynLightsModule module = new LambDynLightsModule();
    ClientEntityEvents.ENTITY_LOAD.register(module::registerEntity);
  }

  @Override
  protected int getLuminance(ItemStack stack, boolean isInWater) {
    return LambDynLights.getLuminanceFromItemStack(stack, isInWater);
  }

  @Override
  protected void registerDynamicLightHandler(EntityType<?> type,
                                             Function<Entity, Integer> handler) {
    DynamicLightHandlers.registerDynamicLightHandler(type, handler::apply);
  }
}
