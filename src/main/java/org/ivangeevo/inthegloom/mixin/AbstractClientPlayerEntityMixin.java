package org.ivangeevo.inthegloom.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.util.GloomEffectsConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements GloomEffectsConstants
{
    @Unique
    float currentGloomFOVMultiplier = 1F;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyVariable(
            method = "getFovMultiplier",
            at = @At(value = "STORE", ordinal = 0), // Targeting the first calculation of 'f'
            ordinal = 0
    )
    private float applyGloomFovMultiplier(float f) {
        float gloomMultiplier = updateGloomFOVMultiplier();
        return f * gloomMultiplier;
    }

    /**
    @Inject(method = "getFovMultiplier", at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerAbilities;getWalkSpeed()F",
                    shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getWalkSpeed()F", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getActiveItem()Lnet/minecraft/item/ItemStack;")),
            cancellable = true)
    private void injectBetween(CallbackInfoReturnable<Float> cir, @Local float f) {
        f *= updateGloomFOVMultiplier();
        cir.setReturnValue(f);
    }
     **/

    @Unique
    private float updateGloomFOVMultiplier() {
        int iGloomLevel = getGloomLevel();

        if ( iGloomLevel == 0 ) {
            currentGloomFOVMultiplier -= GLOOM_FOV_MULTIPLIER_DELTA_OUT_PER_TICK;

            if (currentGloomFOVMultiplier < 1F ) {
                currentGloomFOVMultiplier = 1F;
            }

        } else {
            currentGloomFOVMultiplier += GLOOM_FOV_MULTIPLIER_DELTA_IN_PER_TICK;

            if (currentGloomFOVMultiplier > MAXIMUM_GLOOM_FOV_MULTIPLIER) {
                currentGloomFOVMultiplier = MAXIMUM_GLOOM_FOV_MULTIPLIER;
            }
        }

        return currentGloomFOVMultiplier;
    }



}
