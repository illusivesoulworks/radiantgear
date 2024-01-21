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

package com.illusivesoulworks.radiantgear.integration.embeddiumplus;

import com.illusivesoulworks.radiantgear.client.BaseLambDynLightsModule;

import me.srrapero720.embeddiumplus.foundation.dynlights.DynLightsPlus;
import me.srrapero720.embeddiumplus.foundation.dynlights.DynLightsHandlers;

import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class EmbeddiumPlusModule extends BaseLambDynLightsModule {

    public static void setup() {
        EmbeddiumPlusModule module = new EmbeddiumPlusModule();
        MinecraftForge.EVENT_BUS.addListener(module::entityJoin);
    }

    private void entityJoin(final EntityJoinLevelEvent evt) {
        this.registerEntity(evt.getEntity(), evt.getLevel());
    }

    @Override
    protected int getLuminance(ItemStack stack, boolean isInWater) {
        return DynLightsPlus.getLuminanceFromItemStack(stack, isInWater);
    }

    @Override
    protected void registerDynamicLightHandler(EntityType<?> type,
                                               Function<Entity, Integer> handler) {
        DynLightsHandlers.registerDynamicLightHandler(type, handler::apply);
    }
}
