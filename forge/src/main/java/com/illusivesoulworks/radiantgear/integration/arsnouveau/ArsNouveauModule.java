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

package com.illusivesoulworks.radiantgear.integration.arsnouveau;

import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.illusivesoulworks.radiantgear.RadiantGearConstants;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class ArsNouveauModule {

  private static final Set<EntityType<?>> PROCESSED = new HashSet<>();

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(ArsNouveauModule::entityJoin);
  }

  private static void entityJoin(final EntityJoinLevelEvent evt) {

    if (evt.getLevel().isClientSide() && evt.getEntity() instanceof LivingEntity livingEntity) {
      EntityType<?> type = livingEntity.getType();

      if (!PROCESSED.contains(type) &&
          CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).isPresent()) {
        PROCESSED.add(type);
        RadiantGearConstants.LOG.debug("Registering curio lights for " + type);
        LightManager.register(type, entity -> {
          if (entity instanceof LivingEntity livingEntity1) {
            AtomicInteger luminance = new AtomicInteger(0);
            CuriosApi.getCuriosHelper().getEquippedCurios(livingEntity1).ifPresent(items -> {
              for (int i = 0; i < items.getSlots(); i++) {
                luminance.set(Math.max(luminance.get(),
                    DynamLightUtil.fromItemLike(items.getStackInSlot(i).getItem())));
              }
            });
            return luminance.get();
          }
          return 0;
        });
      }
    }
  }
}
