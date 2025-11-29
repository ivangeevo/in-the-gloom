package org.ivangeevo.inthegloom.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.util.GloomEffectsConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements GloomEffectsConstants {

    @Unique float currentGloomFOVMultiplier = 1F;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float applyGloomFovMultiplier(float f) {
        float gloomMultiplier = updateGloomFOVMultiplier();
        return f * gloomMultiplier;
    }

    @Unique
    private float updateGloomFOVMultiplier() {
        int gloomLevel = btwr$getGloomLevel();

        if (gloomLevel == 0) {
            currentGloomFOVMultiplier -= GLOOM_FOV_MULTIPLIER_DELTA_OUT_PER_TICK;

            if (currentGloomFOVMultiplier < 1F ) {
                currentGloomFOVMultiplier = 1F;
            }

        }
        else {
            currentGloomFOVMultiplier += GLOOM_FOV_MULTIPLIER_DELTA_IN_PER_TICK;

            if (currentGloomFOVMultiplier > MAXIMUM_GLOOM_FOV_MULTIPLIER) {
                currentGloomFOVMultiplier = MAXIMUM_GLOOM_FOV_MULTIPLIER;
            }
        }

        return currentGloomFOVMultiplier;
    }

}