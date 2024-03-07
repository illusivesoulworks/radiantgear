package com.illusivesoulworks.radiantgear.integration.ryoamiclights;

import com.illusivesoulworks.radiantgear.RadiantGearConstants;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.thinkingstudio.ryoamiclights.RyoamicLights;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;
import top.theillusivec4.curios.api.CuriosApi;

public class RyoamicModule {

  private static final Set<EntityType<?>> PROCESSED = new HashSet<>();

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(RyoamicModule::entityJoin);
  }

  private static void entityJoin(final EntityJoinLevelEvent evt) {

    if (evt.getLevel().isClientSide() && evt.getEntity() instanceof LivingEntity livingEntity) {
      EntityType<?> type = livingEntity.getType();

      if (!PROCESSED.contains(type) &&
          CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).isPresent()) {
        PROCESSED.add(type);
        RadiantGearConstants.LOG.debug("Registering curio lights for " + type);
        DynamicLightHandlers.registerDynamicLightHandler(type, entity -> {
          if (entity instanceof LivingEntity livingEntity1) {
            AtomicInteger luminance = new AtomicInteger(0);
            CuriosApi.getCuriosHelper().getEquippedCurios(livingEntity1).ifPresent(items -> {
              for (int i = 0; i < items.getSlots(); i++) {
                luminance.set(Math.max(luminance.get(),
                    RyoamicLights.getLuminanceFromItemStack(items.getStackInSlot(i),
                        livingEntity1.isUnderWater())));
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
