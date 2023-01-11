package com.illusivesoulworks.radiantgear.client;

import com.illusivesoulworks.radiantgear.RadiantGearConstants;
import com.illusivesoulworks.radiantgear.platform.Services;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class BaseLambDynLightsModule {

  private final Set<EntityType<?>> processed = new HashSet<>();

  protected abstract int getLuminance(ItemStack stack, boolean isInWater);

  protected abstract void registerDynamicLightHandler(EntityType<?> type,
                                                      Function<Entity, Integer> handler);

  public void registerEntity(Entity entity, Level level) {

    if (level.isClientSide() && entity instanceof LivingEntity livingEntity) {
      EntityType<?> type = livingEntity.getType();

      if (!this.processed.contains(type)) {
        this.processed.add(type);

        if (Services.LAMBDYNAMIC.hasAccessories(livingEntity)) {
          RadiantGearConstants.LOG.debug("Registering dynamic accessory lights for " + type);
          this.registerDynamicLightHandler(type,
              entity1 -> Services.LAMBDYNAMIC.getLuminance(entity1,
                  (stack) -> this.getLuminance(stack, entity1.isInWater())));
        }
      }
    }
  }
}
