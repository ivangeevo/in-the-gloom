package org.ivangeevo.inthegloom.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class GloomUtil {

    private static final double[] MOON_BRIGHTNESS_BY_PHASE = new double[]{1.25D, 0.875, 0.75D, 0.5D, 0D, 0.5D, 0.75D, 1.25D};

    public static boolean isInGloom(PlayerEntity player)
    {
        // Skip if the player is in creative mode or has night vision
        if (!player.isCreative() && !player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            World world = player.getWorld();
            BlockPos pos = player.getBlockPos();

            // Calculate the sun brightness considering moon phase
            float sunBrightness = computeOverworldSunBrightnessWithMoonPhases(world);

            // Get skylight and block light levels
            int skylight = world.getLightLevel(LightType.SKY, pos);
            int blockLight = Math.max(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.BLOCK, pos.up()));

            boolean isNaturallyDark = sunBrightness < 0.2D || skylight < 4;

            return isNaturallyDark && blockLight < 1;
        }

        return false;
    }

    private static float computeOverworldSunBrightnessWithMoonPhases(World world)
    {
        long worldTime = world.getTimeOfDay() - 12000L; // Offset world time
        if (worldTime < 0L) {
            worldTime = 0L; // Ensure non-negative
        }

        int moonPhase = (int) (worldTime / 24000L) % 8; // Determine moon phase
        double moonBrightness = MOON_BRIGHTNESS_BY_PHASE[moonPhase]; // Get brightness from phase

        float celestialAngle = world.getSkyAngle(1F); // Get celestial angle for sun position
        float sunInvertedBrightness = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        sunInvertedBrightness = MathHelper.clamp(sunInvertedBrightness, 0.0F, 1.0F);

        double sunBrightness = 1.0D - sunInvertedBrightness;

        // Apply weather effects
        double rainBrightnessModifier = 1.0D - (world.getRainGradient(1F) * 5.0F / 16.0D);
        double stormBrightnessModifier = 1.0D - (world.getThunderGradient(1F) * 5.0F / 16.0D);
        sunBrightness *= rainBrightnessModifier * stormBrightnessModifier;

        // Minimum brightness threshold
        double minBrightness = 0.2D * moonBrightness * rainBrightnessModifier * stormBrightnessModifier;
        if (minBrightness < 0.05D) {
            minBrightness = 0D;
        }

        return (float) (sunBrightness * (1D - minBrightness) + minBrightness);
    }

    public static void playSoundInRandomDirection(PlayerEntity player, SoundEvent soundEvent, float volume, float pitch, double distance)
    {
        double x = player.getBlockPos().getX();
        double y = player.getBlockPos().getY();
        double z = player.getBlockPos().getZ();
        double randomYaw = player.getRandom().nextDouble() * 360D;

        x += -MathHelper.sin((float) randomYaw) * distance;
        z += MathHelper.cos((float) randomYaw) * distance;

        BlockPos soundPos = new BlockPos((int) x, (int) y, (int) z);
        player.getWorld().playSound(player, soundPos, soundEvent, SoundCategory.PLAYERS, volume, pitch);
    }

}
