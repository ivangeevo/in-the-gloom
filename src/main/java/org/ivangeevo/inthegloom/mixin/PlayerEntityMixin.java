package org.ivangeevo.inthegloom.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.util.GloomEffectsConstants;
import org.ivangeevo.inthegloom.entity.interfaces.PlayerEntityAdded;
import org.ivangeevo.inthegloom.util.GloomUtil;
import org.ivangeevo.inthegloom.util.PlayerEntityMixinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.ivangeevo.inthegloom.util.GloomUtil.isInGloom;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements GloomEffectsConstants, PlayerEntityAdded {

    @Unique private static final TrackedData<Byte> GLOOM_LEVEL = DataTracker.registerData(PlayerEntityMixin.class, TrackedDataHandlerRegistry.BYTE);
    @Unique int inGloomCounter = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void customData(DataTracker.Builder builder, CallbackInfo ci)
    {
        builder.add(GLOOM_LEVEL, (byte) 0);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("fcGloomLevel", btwr$getGloomLevel());
        nbt.putInt("fcGloomCounter", inGloomCounter);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains( "fcGloomLevel" )) {
            btwr$setGloomLevel(nbt.getInt("fcGloomLevel"));
        }

        if (nbt.contains("fcGloomCounter")) {
            btwr$setInGloomCounter(nbt.getInt("fcGloomCounter"));
        }
    }

}