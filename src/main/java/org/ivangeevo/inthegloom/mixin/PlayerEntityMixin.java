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
public abstract class PlayerEntityMixin extends LivingEntity implements GloomEffectsConstants, PlayerEntityAdded
{
    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract float getMovementSpeed();

    @Unique private final PlayerEntity playerEntity = (PlayerEntity)(Object)this;

    @Unique private static TrackedData<Byte> GLOOM_LEVEL = DataTracker.registerData(PlayerEntityMixin.class, TrackedDataHandlerRegistry.BYTE);

    @Unique int previousGloomLevel = 0;
    @Unique int inGloomCounter = 0;

    @Unique private final PlayerEntityMixinManager effectsManager = PlayerEntityMixinManager.getInstance();

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
        nbt.putInt("fcGloomLevel", getGloomLevel());
        nbt.putInt("fcGloomCounter", inGloomCounter);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains( "fcGloomLevel" )) {
            setGloomLevel(nbt.getInt("fcGloomLevel"));
        }

        if (nbt.contains("fcGloomCounter")) {
            setInGloomCounter(nbt.getInt("fcGloomCounter"));
        }
    }


    @Override
    public void updateGloomState()
    {

        int iGloomLevel = this.getGloomLevel();

        if (this.getPreviousGloomLevel() != iGloomLevel)
        {
            setInGloomCounter(0);
            setPreviousGloomLevel(iGloomLevel);

            if (iGloomLevel == 3)
            {
                playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 1.0F, 1.0F);
            }
        }

        if (iGloomLevel > 0)
        {
            setInGloomCounter(getInGloomCounter() + 1);

            float fCounterProgress = (float) getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

            if (fCounterProgress > 1.0F) {
                fCounterProgress = 1.0F;
            }

            // Check if in gloom conditions
            if (isInGloom((PlayerEntity)(Object)this))
            {
                float fCaveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * fCounterProgress;
                float fCaveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * fCounterProgress;

                if (iGloomLevel > 1)
                {
                    float fGrowlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * fCounterProgress;
                    float fGrowlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * fCounterProgress;

                    if (iGloomLevel > 2) {
                        // Insert effects here for when the player is getting bit
                    }
                    else
                    {
                        if (getRandom().nextFloat() < fGrowlSoundChance)
                        {
                            GloomUtil.playSoundInRandomDirection(playerEntity,
                                    SoundEvents.ENTITY_WOLF_GROWL, fGrowlSoundVolume,
                                    (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                        }
                    }
                }

                if (this.getRandom().nextFloat() < fCaveSoundChance)
                {
                    GloomUtil.playSoundInRandomDirection(playerEntity,
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