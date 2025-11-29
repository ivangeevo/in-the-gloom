package org.btwr.in_the_gloom.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.in_the_gloom.util.GloomEffectsConstants;
import org.btwr.in_the_gloom.util.PlayerEntityMixinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements GloomEffectsConstants {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci) {
        this.btwr$updateGloomState();

        PlayerEntityMixinManager.getInstance().onServerTick((ServerPlayerEntity)(Object)this);
    }

}