package top.theillusivec4.curiouslights.dynamiclightsreforged;

import dev.lambdaurora.lambdynlights.DynamicLightSource;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSources;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;


public class ReforgedCuriosLightSource {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent evt) {
        if (evt.side != LogicalSide.CLIENT) {
            return;
        }

        Player player = evt.player;
        DynamicLightSource playerLight = (DynamicLightSource) player;

        if (player.isAlive()) {

            int prevLight = playerLight.tdv$getLuminance();
            boolean isUnderwater = checkPlayerWater(player);

            MutableInt newLight = new MutableInt(0);

            CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(
                curios -> {
                    for (int i = 0; i < curios.getSlots(); i++) {
                        ItemStack stack = curios.getStackInSlot(i);

                        if (!stack.isEmpty()) {
                            newLight.setValue(
                                    Math.max(newLight.getValue(),
                                            getLightFromItemStack(stack, isUnderwater)
                                    )
                            );
                        }
                    }
                }
            );

            if (prevLight != 0 && newLight.getValue() != prevLight) {
                newLight.setValue(0);
            }

            PlayerHelper.setLuminance(player, newLight.getValue());

            if (!playerLight.tdv$isDynamicLightEnabled() && newLight.getValue() > 0) {
                playerLight.tdv$setDynamicLightEnabled(true);
            } else if (playerLight.tdv$isDynamicLightEnabled() && newLight.getValue() < 1) {
                playerLight.tdv$setDynamicLightEnabled(false);
            }
        } else {
            playerLight.tdv$setDynamicLightEnabled(false);
        }
    }

    private boolean checkPlayerWater(Player player) {
        return player.isEyeInFluid(FluidTags.WATER);
    }

    private int getLightFromItemStack(ItemStack stack, boolean isUnderwater) {
        return ItemLightSources.getLuminance(stack, isUnderwater);
    }

}
