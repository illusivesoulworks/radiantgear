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

package com.illusivesoulworks.radiantgear.integration.lucent;

import com.illusivesoulworks.radiantgear.RadiantGearConstants;
import com.legacy.lucent.api.EntityBrightness;
import com.legacy.lucent.api.plugin.ILucentPlugin;
import com.legacy.lucent.api.plugin.LucentPlugin;
import com.legacy.lucent.api.registry.ItemLightingRegistry;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

@LucentPlugin
@OnlyIn(Dist.CLIENT)
public class LucentModule implements ILucentPlugin {

  @Override
  public String ownerModID() {
    return RadiantGearConstants.MOD_ID;
  }

  @Nullable
  @Override
  public List<String> requiredMods() {
    return Collections.singletonList("curios");
  }

  @Override
  public void getEntityLightLevel(final EntityBrightness entityBrightness) {
    Entity entity = entityBrightness.getEntity();
    AtomicInteger curiosLightLevel = new AtomicInteger();

    if (entity instanceof LivingEntity livingEntity) {
      CuriosApi.getCuriosHelper().getEquippedCurios(livingEntity).ifPresent(curios -> {

        for (int i = 0; i < curios.getSlots(); i++) {
          ItemStack stack = curios.getStackInSlot(i);
          curiosLightLevel.set(Math.max(curiosLightLevel.get(), ItemLightingRegistry.get(stack)));
        }
      });
    }
    entityBrightness.setLightLevel(curiosLightLevel.get());
  }
}
