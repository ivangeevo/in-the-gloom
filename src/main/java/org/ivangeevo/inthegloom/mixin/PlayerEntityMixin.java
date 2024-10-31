package org.ivangeevo.inthegloom.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.ivangeevo.inthegloom.entity.interfaces.PlayerEntityAdded;
import org.ivangeevo.inthegloom.util.GloomEffectsManager;
import org.ivangeevo.inthegloom.util.GloomUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements GloomEffectsConstants, PlayerEntityAdded
{
    private static TrackedData<Byte> GLOOM_LEVEL = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);

    @Unique int previousGloomLevel = 0;
    @Unique int inGloomCounter = 0;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void customData(DataTracker.Builder builder, CallbackInfo ci)
    {
        builder.add(GLOOM_LEVEL, (byte) 0);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomData(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putInt("fcGloomLevel", getGloomLevel());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomData(NbtCompound nbt, CallbackInfo ci)
    {
        if ( nbt.contains( "fcGloomLevel" ) )
        {
            setGloomLevel(nbt.getInt("fcGloomLevel"));
        }
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    private void redirectGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir)
    {
        cir.setReturnValue(
                GloomEffectsManager.getInstance().applyGloomExhaustionModifier((PlayerEntity) (Object)this, cir)
        );
    }

    @Inject(method = "getMovementSpeed", at = @At("TAIL"), cancellable = true)
    public void modifyMovementSpeed(CallbackInfoReturnable<Float> cir)
    {
        cir.setReturnValue(
                GloomEffectsManager.getInstance().applyGloomExhaustionModifier((PlayerEntity) (Object)this, cir)
        );
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci)
    {
        updateGloomState();
    }

    @Override
    public void updateGloomState() {
        int iGloomLevel = this.getGloomLevel();

        if (this.getPreviousGloomLevel() != iGloomLevel) {
            setInGloomCounter(0);
            setPreviousGloomLevel(iGloomLevel);

            if (iGloomLevel == 3) {
                playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 1.0F, 1.0F);
            }
        }

        if (iGloomLevel > 0) {
            setInGloomCounter(getInGloomCounter() + 1);

            float fCounterProgress = (float) getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

            if (fCounterProgress > 1.0F) {
                fCounterProgress = 1.0F;
            }

            BlockPos pos = getBlockPos();
            int skyLightLevel = getWorld().getLightLevel(LightType.SKY, pos);
            int blockLightLevel = getWorld().getLightLevel(LightType.BLOCK, pos);

            // Get the moon phase
            int moonPhase = ((LunarWorldView) getWorld()).getMoonPhase();

            // Check if it's nighttime
            long timeOfDay = getWorld().getTimeOfDay() % 24000;
            boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

            // Set gloom threshold for moon phase
            int gloomMoonPhaseThreshold = 3; // Trigger gloom on moon phases 3 and 4 (darker nights)

            // Determine gloom conditions
            boolean isUnderground = skyLightLevel == 0 && blockLightLevel < 1;
            boolean isOutsideOnDarkNight = isNight && skyLightLevel == 15 && moonPhase >= gloomMoonPhaseThreshold && blockLightLevel == 0;

            // Check if in gloom conditions
            if (GloomUtil.isInGloom((PlayerEntity)(Object)this)) {
                float fCaveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * fCounterProgress;
                float fCaveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * fCounterProgress;

                if (iGloomLevel > 1) {
                    float fGrowlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * fCounterProgress;
                    float fGrowlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * fCounterProgress;

                    if (iGloomLevel > 2) {
                        // Insert effects here for when the player is getting bit
                    } else {
                        if (getRandom().nextFloat() < fGrowlSoundChance) {
                            GloomEffectsManager.getInstance().playSoundInRandomDirection((PlayerEntity) (Object) this,
                                    SoundEvents.ENTITY_WOLF_GROWL, fGrowlSoundVolume,
                                    (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                        }
                    }
                }

                if (this.getRandom().nextFloat() < fCaveSoundChance) {
                    GloomEffectsManager.getInstance().playSoundInRandomDirection((PlayerEntity) (Object) this,
                            SoundEvents.AMBIENT_CAVE.value(), fCaveSoundVolume,
                            0.5F + this.getRandom().nextFloat(), 5D);
                }
            }
        }
    }


    @Override
    public void setInGloomCounter(int newValue) {
        this.inGloomCounter = newValue;
    }

    @Override
    public int getInGloomCounter() {
        return inGloomCounter;
    }

    @Override
    public int getGloomLevel()
    {
        return this.getDataTracker().get(GLOOM_LEVEL);
    }

    @Override
    public void setGloomLevel(int newValue)
    {
        this.getDataTracker().set(GLOOM_LEVEL,(byte) newValue);
    }

    @Override
    public void setPreviousGloomLevel(int newValue) {
        this.previousGloomLevel = newValue;
    }

    @Override
    public int getPreviousGloomLevel() {
        return previousGloomLevel;
    }

}