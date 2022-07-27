package top.theillusivec4.curiouslights.dynamiclightsreforged;

import net.minecraftforge.common.MinecraftForge;

public class DynamicLightsReforgedModule {
    public static void setup() {
        MinecraftForge.EVENT_BUS.register(new ReforgedCuriosLightSource());
    }
}
