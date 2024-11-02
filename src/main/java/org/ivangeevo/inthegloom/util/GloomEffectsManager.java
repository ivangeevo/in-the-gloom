package org.ivangeevo.inthegloom.util;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
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

        player.getWorld().playSound(player, soundPos, soundEvent, SoundCategory.PLAYERS, fVolume, fPitch );
    }

    // TODO: Fix exhaustion modifier not applicable in modded environments.// in the dev it's fine
    //  fix making the player slow down in gloom
    // Helper method to add exhaustion (debuffs to movement, break speed & attack damage)
    public float applyGloomExhaustionModifier(PlayerEntity player, CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue(); // Get original speed
        float speedMultiplier = 1.0f;

        // Check if the player is in gloom conditions
        boolean isInGloom = GloomUtil.isInGloom(player);

        // Apply the gloom speed modifier if the player is not creative and in gloom conditions
        if (!player.isCreative() && isInGloom) {
            speedMultiplier *= 0.5f; // speed is halved in gloom conditions
        }
        return originalSpeed * speedMultiplier;
    }


}
