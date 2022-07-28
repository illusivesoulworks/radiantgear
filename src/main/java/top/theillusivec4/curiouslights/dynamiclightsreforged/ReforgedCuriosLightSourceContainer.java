package top.theillusivec4.curiouslights.dynamiclightsreforged;

import dev.lambdaurora.lambdynlights.DynamicLightSource;
import dev.lambdaurora.lambdynlights.LambDynLights;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ReforgedCuriosLightSourceContainer implements DynamicLightSource {

    public int lightLevel;
    public int lastLightLevel;
    public Player player;
    public DynamicLightSource playerAsDLS;

    private double lambdynlights$prevX;
    private double lambdynlights$prevY;
    private double lambdynlights$prevZ;
    private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

    ReforgedCuriosLightSourceContainer(Player player) {
        this.player = player;
        this.playerAsDLS = (DynamicLightSource) this.player;
        this.lightLevel = 0;
        this.lastLightLevel = 0;
    }

    @Override
    public double tdv$getDynamicLightX() {
        return playerAsDLS.tdv$getDynamicLightX();
    }

    @Override
    public double tdv$getDynamicLightY() {
        return playerAsDLS.tdv$getDynamicLightY();
    }

    @Override
    public double tdv$getDynamicLightZ() {
        return playerAsDLS.tdv$getDynamicLightZ();
    }

    @Override
    public Level tdv$getDynamicLightWorld() {
        return playerAsDLS.tdv$getDynamicLightWorld();
    }

    @Override
    public void tdv$resetDynamicLight() {
        this.lastLightLevel = 0;
    }

    @Override
    public int tdv$getLuminance() {
        return lightLevel;
    }

    @Override
    public void tdv$dynamicLightTick() {
        // nop, handled elsewhere
    }

    @Override
    public boolean tdv$shouldUpdateDynamicLight() {
        return playerAsDLS.tdv$shouldUpdateDynamicLight();
    }

    @Override
    public boolean tdv$lambdynlights$updateDynamicLight(@NotNull LevelRenderer renderer) {
        if (!this.tdv$shouldUpdateDynamicLight()) {
            return false;
        }

        double deltaX = this.tdv$getDynamicLightX() - this.lambdynlights$prevX;
        double deltaY = this.tdv$getDynamicLightY() - this.lambdynlights$prevY;
        double deltaZ = this.tdv$getDynamicLightZ() - this.lambdynlights$prevZ;

        int luminance = this.tdv$getLuminance();

        if (Math.abs(deltaX) > 0.1D || Math.abs(deltaY) > 0.1D || Math.abs(deltaZ) > 0.1D || luminance != this.lastLightLevel) {
            this.lambdynlights$prevX = this.tdv$getDynamicLightX();
            this.lambdynlights$prevY = this.tdv$getDynamicLightY();
            this.lambdynlights$prevZ = this.tdv$getDynamicLightZ();
            this.lastLightLevel = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                var entityChunkPos = this.player.chunkPosition();
                var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, SectionPos.posToSectionCoord(this.player.getEyeY()), entityChunkPos.z);

                LambDynLights.scheduleChunkRebuild(renderer, chunkPos);
                LambDynLights.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);

                var directionX = (this.player.blockPosition().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
                var directionY = (Mth.fastFloor(this.player.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                var directionZ = (this.player.blockPosition().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX); // X
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ); // XZ
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite()); // Z
                    } else {
                        chunkPos.move(directionZ.getOpposite()); // origin
                        chunkPos.move(directionY); // Y
                    }
                    LambDynLights.scheduleChunkRebuild(renderer, chunkPos);
                    LambDynLights.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
                }
            }

            // Schedules the rebuild of removed chunks.
            this.tdv$lambdynlights$scheduleTrackedChunksRebuild(renderer);
            // Update tracked lit chunks.
            this.lambdynlights$trackedLitChunkPos = newPos;
            return true;
        }

        return false;
    }

    @Override
    public void tdv$lambdynlights$scheduleTrackedChunksRebuild(@NotNull LevelRenderer levelRenderer) {
        playerAsDLS.tdv$lambdynlights$scheduleTrackedChunksRebuild(levelRenderer);
    }
}
