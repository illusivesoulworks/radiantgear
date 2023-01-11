package com.illusivesoulworks.radiantgear.platform.services;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.function.Function;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LambDynamicLightsTrinkets implements ILambDynamicLights {

  @Override
  public boolean hasAccessories(LivingEntity livingEntity) {
    return TrinketsApi.getTrinketComponent(livingEntity).isPresent();
  }

  @Override
  public int getLuminance(Entity entity, Function<ItemStack, Integer> stackLuminance) {

    if (entity instanceof LivingEntity livingEntity) {
      return TrinketsApi.getTrinketComponent(livingEntity).map(trinketComponent -> {
        int luminance = 0;
        for (Tuple<SlotReference, ItemStack> slotReferenceItemStackTuple : trinketComponent.getAllEquipped()) {
          luminance = Math.max(luminance, stackLuminance.apply(slotReferenceItemStackTuple.getB()));
        }
        return luminance;
      }).orElse(0);
    }
    return 0;
  }
}
