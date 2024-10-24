package org.ivangeevo.inthegloom.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Predicate;

// TODO: Move gloom logic here to clear up the PlayerEntity mixin class.
public class GloomEffectsManager implements GloomEffectsConstants
{
    private static final GloomEffectsManager instance = new GloomEffectsManager();

    private GloomEffectsManager() {}

    public static GloomEffectsManager getInstance()
    {
        return instance;
    }

    public float speedMultiplier = 1.0f;


    public void playSoundInRandomDirection(PlayerEntity player, SoundEvent soundEvent, float fVolume, float fPitch, double dDistance)
    {
        double dXPos = player.getBlockPos().getX();
        double dYPos = player.getBlockPos().getY();
        double dZPos = player.getBlockPos().getZ();

        double dRandomYaw = player.getRandom().nextDouble();

        double dXOffset = (double)-MathHelper.sin( (float)( dRandomYaw * 360D  ) ) * dDistance;
        double dZOffset = (double)MathHelper.cos( (float)( dRandomYaw * 360D ) ) * dDistance;

        dXPos += dXOffset;
        dZPos += dZOffset;

        BlockPos soundPos = new BlockPos((int) dXPos, (int) dYPos, (int) dZPos);

        player.getWorld().playSound(null, soundPos, soundEvent, SoundCategory.PLAYERS, fVolume, fPitch );
    }

    // helper method to add exhaustion(debuffs to movement, break speed & attack damage)
    public float applyGloomExhaustionModifier(PlayerEntity player,CallbackInfoReturnable<Float> cir)
    {
        float originalSpeed = cir.getReturnValue(); // Get original speed
        float speedMultiplier = 1.0f;

        int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());

        if (lightLevel <= 0)
        {
            speedMultiplier *= 0.5f; // speed is halved at light level 0
        }

        return originalSpeed * speedMultiplier;
    }

    @Unique
    public boolean isInGloom(PlayerEntity player)
    {
        // Check if player is in creative mode
        if (!player.isCreative())
        {
            // Check if the player has night vision
            if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION) && player.getWorld().getDimensionEntry().matchesId(DimensionTypes.OVERWORLD_ID))
            {
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();

                // Get the sky light level and block light level
                int skylightLevel = world.getLightLevel(LightType.SKY, pos);
                int blockLightLevel = world.getLightLevel(LightType.BLOCK, pos);

                // Compute sun brightness (you might need to adjust this depending on your implementation)
                float sunBrightness = computeOverworldSunBrightnessWithMoonPhases(world);

                // Determine gloom conditions
                boolean isGloomy = sunBrightness < 0.02F && skylightLevel < 8; // You can adjust the threshold

                // If there is no skylight at all, consider it gloomy
                return isGloomy || (blockLightLevel < 1); // Use a threshold for block light
            }
        }

        return false; // Default to not in gloom
    }


    static private final double[] moonBrightnessByPhase = new double[] {1.25D, 0.875, 0.75D, 0.5D, 0D, 0.5D, 0.75D, 1.25D};


    public float computeOverworldSunBrightnessWithMoonPhases(World world)
    {
        // slight modified version of regular moon phase equation so that phase switches over at noon to avoid sudden jump in lighting at dawn
        long lOffsetWorldTime = world.getTime() - 12000L;

        if ( lOffsetWorldTime < 0L )
        {
            lOffsetWorldTime = 0L;
        }

        int iMoonPhase = (int)( ( lOffsetWorldTime / 24000L ) % 8L );
        double dMoonBrightness = moonBrightnessByPhase[iMoonPhase];

        float fCelestialAngle = world.getSkyAngleRadians(1F);

        // slight modifcation from vanilla calc so that gloom starts to set in on moonless nights the moment the sun drops beneath the horizon, and is removed at the moment of rise
        //float fSunInvertedBrightness = 1.0F - (MathHelper.cos(fCelestialAngle * (float)Math.PI * 2.0F) * 2.0F + 0.2F);
        float fSunInvertedBrightness = 1.0F - ( ( MathHelper.cos( fCelestialAngle * (float)Math.PI * 2.0F ) * 2.0F ) + 0.25F );

        if ( fSunInvertedBrightness < 0.0F)
        {
            fSunInvertedBrightness = 0.0F;
        }
        else if ( fSunInvertedBrightness > 1.0F)
        {
            fSunInvertedBrightness = 1.0F;
        }

        double dSunBrightness = 1.0D - fSunInvertedBrightness;

        double dRainBrightnessModifier = ( 1.0D - (double)( world.getRainGradient( 1F ) * 5.0F ) / 16.0D );
        double dStormBrightnessModifier = ( 1.0D - (double)( world.getThunderGradient(1F) * 5.0F ) / 16.0D );

        dSunBrightness = dSunBrightness * dRainBrightnessModifier * dStormBrightnessModifier;

        double dMinBrightness = 0.2D;

        dMinBrightness *= dMoonBrightness * dRainBrightnessModifier * dStormBrightnessModifier;

        // clamp at a value that causes the world to descend into gloom on a new moon during a storm
        if ( dMinBrightness < 0.05D )
        {
            dMinBrightness = 0D;
        }

        return (float)( dSunBrightness * ( 1D - dMinBrightness ) + dMinBrightness );
    }



}
