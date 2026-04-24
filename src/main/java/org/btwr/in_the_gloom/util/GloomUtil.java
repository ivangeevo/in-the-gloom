package org.btwr.in_the_gloom.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.btwr.in_the_gloom.compat.LambDynamicLightsCompat;
import org.btwr.in_the_gloom.config.InTheGloomConfig;

public class GloomUtil {

    public static boolean isInGloom(PlayerEntity player) {
        if (!canGetGloom(player)) return false;

        World world = player.getWorld();

        if (!getGloomEnabledDimensions(world.getDimensionEntry())) return false;

        BlockPos pos = player.getBlockPos();

        float sunBrightness = computeOverworldSunBrightnessWithMoonPhases(world);

        float brightnessHere = computeBrightness(world, pos, sunBrightness);
        float brightnessAbove = computeBrightness(world, pos.up(), sunBrightness);

        return Math.max(brightnessHere, brightnessAbove) < 0.001F;
    }

    private static boolean canGetGloom(PlayerEntity player) {
        boolean isCreativeOrSpectator = player.isCreative() && player.isSpectator();
        boolean isHoldingDynamicLightItem = LambDynamicLightsCompat.playerHoldsLambRegisteredLight(player);
        boolean hasNightVision = player.hasStatusEffect(StatusEffects.NIGHT_VISION);
        boolean hasGloomInDimension = getGloomEnabledDimensions(player.getWorld().getDimensionEntry());

        return !isCreativeOrSpectator && !isHoldingDynamicLightItem && !hasNightVision && hasGloomInDimension;
    }

    private static float computeOverworldSunBrightnessWithMoonPhases(World world) {
        long worldTime = world.getTimeOfDay() - 12000L; // Offset world time
        if (worldTime < 0L) {
            worldTime = 0L; // Ensure non-negative
        }

        int moonPhase = (int) (worldTime / 24000L) % 8;

        // Adjust moon brightness to 0 on new moon
        double moonBrightness = moonPhase == 4 ? 0D : 1.0D;

        float celestialAngle = world.getSkyAngle(1F); // Get celestial angle for sun position
        float sunInvertedBrightness = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        sunInvertedBrightness = MathHelper.clamp(sunInvertedBrightness, 0.0F, 1.0F);

        double sunBrightness = 1.0D - sunInvertedBrightness;

        // Minimum brightness threshold
        double minBrightness = 0.2D * moonBrightness;
        if (minBrightness < 0.05D) {
            minBrightness = 0D;
        }

        return (float) (sunBrightness * (1D - minBrightness) + minBrightness);
    }

    // Approximate combined brightness
    private static float computeBrightness(World world, BlockPos pos, float sunBrightness) {
        float blockLight = world.getLightLevel(LightType.BLOCK, pos) / 15f;
        float skyLight = world.getLightLevel(LightType.SKY, pos) / 15f;

        skyLight *= sunBrightness;

        return Math.max(blockLight, skyLight);
    }

    public static void playSoundInRandomDirection(PlayerEntity player, SoundEvent soundEvent, float volume, float pitch, double distance) {
        double x = player.getBlockPos().getX();
        double y = player.getBlockPos().getY();
        double z = player.getBlockPos().getZ();
        double randomYaw = player.getRandom().nextDouble() * 360D;

        x += -MathHelper.sin((float) randomYaw) * distance;
        z += MathHelper.cos((float) randomYaw) * distance;

        BlockPos soundPos = new BlockPos((int) x, (int) y, (int) z);
        player.getWorld().playSound(player, soundPos, soundEvent, SoundCategory.PLAYERS, volume, pitch);
    }

    private static boolean getGloomEnabledDimensions(RegistryEntry<DimensionType> regType) {
        if (regType.matchesId(DimensionTypes.THE_END_ID) && InTheGloomConfig.theEndGloom.get()){
            return true;
        }

        if (regType.matchesId(DimensionTypes.THE_NETHER_ID) && InTheGloomConfig.theNetherGloom.get()){
            return true;
        }

        return regType.matchesId(DimensionTypes.OVERWORLD_ID) && InTheGloomConfig.overworldGloom.get();

    }

}