/*
 * Copyright (C) 2022 C4
 *
 * This file is part of Curious Lights.
 *
 * Curious Lights is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curious Lights is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Curious Lights.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.curiouslights.arsnouveau;

import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiouslights.CuriousLights;

public class ArsNouveauModule {

  private static final Set<EntityType<?>> PROCESSED = new HashSet<>();

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(ArsNouveauModule::entityJoin);
  }

  private static void entityJoin(final EntityJoinWorldEvent evt) {

    if (evt.getWorld().isClientSide() && evt.getEntity() instanceof LivingEntity livingEntity) {
      EntityType<?> type = livingEntity.getType();

      if (!PROCESSED.contains(type) &&
          CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).isPresent()) {
        PROCESSED.add(type);
        CuriousLights.LOG.debug("Registering curio lights for " + type);
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
