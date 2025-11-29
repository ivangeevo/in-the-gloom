package org.btwr.in_the_gloom.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.in_the_gloom.data.ModDataAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/**
 * Mixin priority needs to be set high, otherwise there may be
 * issues with mods that modify getFovMultiplier as well. This
 * should run last when possible
 */
@Mixin(value = AbstractClientPlayerEntity.class, priority = 99999)
abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @ModifyVariable(method = "getFovMultiplier", at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    private float applyGloomFovModifier(float originalFov) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        var gloomData = player.getAttached(ModDataAttachments.PLAYER_GLOOM);
        if (gloomData == null) return originalFov;

        // Let the tick method update the current multiplier gradually
        float currentMultiplier = gloomData.updateGloomFOVMultiplier();

        // Apply gloom multiplier on top of the current FOV
        return originalFov * currentMultiplier;
    }

}