package com.illusivesoulworks.radiantgear.integration.sodiumdynamiclights;

import com.illusivesoulworks.radiantgear.client.BaseLambDynLightsModule;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import java.util.function.Function;

public class SDLModule extends BaseLambDynLightsModule {
    public static void setup() {
        SDLModule module = new SDLModule();
        MinecraftForge.EVENT_BUS.addListener(module::entityJoin);
    }

    private void entityJoin(final EntityJoinLevelEvent evt) {
        this.registerEntity(evt.getEntity(), evt.getLevel());
    }

    @Override
    protected int getLuminance(ItemStack stack, boolean isInWater) {
        return SodiumDynamicLights.getLuminanceFromItemStack(stack, isInWater);
    }

    @Override
    protected void registerDynamicLightHandler(EntityType<?> type,
                                               Function<Entity, Integer> handler) {
        DynamicLightHandlers.registerDynamicLightHandler(type, handler::apply);
    }
}
