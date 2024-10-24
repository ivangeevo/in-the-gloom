package org.ivangeevo.inthegloom.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        if (!player.isCreative())
        {
            if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION) /** && player.getWorld().getDimensionKey() == DimensionTypes.OVERWORLD **/)
            {
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();

                int skylightLevel = world.getLightLevel(LightType.SKY, pos);

                float sunBrightness = world.getSkyAngle(1.0F);

                return sunBrightness < 0.02F && skylightLevel < 8;
            }
        }

        return false;
    }


}
