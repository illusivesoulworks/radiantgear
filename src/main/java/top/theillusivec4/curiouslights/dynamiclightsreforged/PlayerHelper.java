package top.theillusivec4.curiouslights.dynamiclightsreforged;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public abstract class PlayerHelper extends Player {
    private static final Logger LOGGER = LogManager.getLogger();

    public PlayerHelper(Level level, BlockPos blockPos, float yaw, GameProfile gameProfile) {
        super(level, blockPos, yaw, gameProfile);
    }

    public static void setLuminance(Player player, int newLuminance) {
        try {
            String fieldName = "lambdynlights$luminance";
            for (Field f : Player.class.getDeclaredFields()) {
                if (fieldName.equals(f.getName())) {
                    if (f.trySetAccessible()) {
                        f.setInt(player, newLuminance);
                    } else {
                        LOGGER.error(String.format("PlayerHelper unable to make %s accessible", fieldName));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.throwing(e);
        }
    }
}
