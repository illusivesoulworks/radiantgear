package com.illusivesoulworks.radiantgear.platform.services;

import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class LambDynamicLightsCurios implements ILambDynamicLights {

  @Override
  public boolean hasAccessories(LivingEntity livingEntity) {
    return CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).isPresent();
  }

  @Override
  public int getLuminance(Entity entity, Function<ItemStack, Integer> stackLuminance) {

    if (entity instanceof LivingEntity livingEntity) {
      return CuriosApi.getCuriosHelper().getEquippedCurios(livingEntity).map(items -> {
        int luminance = 0;
        for (int i = 0; i < items.getSlots(); i++) {
          luminance = Math.max(luminance, stackLuminance.apply(items.getStackInSlot(i)));
        }
        return luminance;
      }).orElse(0);
    }
    return 0;
  }
}
