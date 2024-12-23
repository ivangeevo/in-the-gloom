package org.ivangeevo.inthegloom.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.inthegloom.InTheGloomMod;
import org.ivangeevo.inthegloom.config.ModSettings;
import org.spongepowered.asm.mixin.Unique;

public class GloomUtil {

    @Unique
    private static ModSettings configChecker = InTheGloomMod.getInstance().settings;

    public static boolean isInGloom(PlayerEntity player) {

        if (!player.isCreative() && !player.isSpectator() && canGetGloom(player)) {
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

    private static boolean canGetGloom(PlayerEntity player) {
        return !player.hasStatusEffect(StatusEffects.NIGHT_VISION)
                && getGloomEnabledDimensions(player.getWorld().getDimensionEntry());
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
        if (regType.matchesId(DimensionTypes.THE_END_ID) && configChecker.isTheEndGloom()){
            return true;
        }

        if (regType.matchesId(DimensionTypes.THE_NETHER_ID) && configChecker.isNetherGloom()){
            return true;
        }

        return regType.matchesId(DimensionTypes.OVERWORLD_ID) && configChecker.isOverworldGloom();

    }
}
