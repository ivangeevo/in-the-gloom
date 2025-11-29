package org.ivangeevo.inthegloom.mixin.added;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.entity.interfaces.PlayerEntityAdded;
import org.ivangeevo.inthegloom.util.GloomEffectsConstants;
import org.ivangeevo.inthegloom.util.GloomUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.ivangeevo.inthegloom.util.GloomUtil.isInGloom;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAddedMixin extends LivingEntity implements GloomEffectsConstants, PlayerEntityAdded {

    @Unique private final PlayerEntity playerEntity = (PlayerEntity)(Object)this;
    @Unique private static final TrackedData<Byte> GLOOM_LEVEL = DataTracker.registerData(PlayerEntityAddedMixin.class, TrackedDataHandlerRegistry.BYTE);
    @Unique int previousGloomLevel = 0;
    @Unique int inGloomCounter = 0;

    protected PlayerEntityAddedMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void btwr$updateGloomState() {
        int gloomLevel = this.btwr$getGloomLevel();

        if (this.btwr$getPreviousGloomLevel() != gloomLevel) {
            btwr$setInGloomCounter(0);
            btwr$setPreviousGloomLevel(gloomLevel);

            if (gloomLevel == 3) {
                playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 1.0F, 1.0F);
            }
        }

        if (gloomLevel > 0) {
            btwr$setInGloomCounter(btwr$getInGloomCounter() + 1);

            float counterProgress = (float) btwr$getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

            if (counterProgress > 1.0F) {
                counterProgress = 1.0F;
            }

            // Check if in gloom conditions
            if (isInGloom((PlayerEntity)(Object)this)) {
                float fCaveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * counterProgress;
                float fCaveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * counterProgress;

                if (gloomLevel > 1) {
                    float fGrowlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * counterProgress;
                    float fGrowlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * counterProgress;

                    if (gloomLevel > 2) {
                        // Insert effects here for when the player is getting bit
                    }
                    else {
                        if (this.getRandom().nextFloat() < fGrowlSoundChance) {
                            GloomUtil.playSoundInRandomDirection(playerEntity,
                                    SoundEvents.ENTITY_WOLF_GROWL, fGrowlSoundVolume,
                                    (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                        }
                    }
                }

                if (this.getRandom().nextFloat() < fCaveSoundChance) {
                    GloomUtil.playSoundInRandomDirection(playerEntity,
                            SoundEvents.AMBIENT_CAVE.value(), fCaveSoundVolume,
                            0.5F + this.getRandom().nextFloat(), 5D);
                }
            }
        }
    }

    @Override
    public void btwr$setInGloomCounter(int newValue) {
        this.inGloomCounter = newValue;
    }

    @Override
    public int btwr$getInGloomCounter() {
        return inGloomCounter;
    }

    @Override
    public int btwr$getGloomLevel()
    {
        return this.getDataTracker().get(GLOOM_LEVEL);
    }

    @Override
    public void btwr$setGloomLevel(int newValue)
    {
        this.getDataTracker().set(GLOOM_LEVEL,(byte) newValue);
    }

    @Override
    public void btwr$setPreviousGloomLevel(int newValue) {
        this.previousGloomLevel = newValue;
    }

    @Override
    public int btwr$getPreviousGloomLevel() {
        return previousGloomLevel;
    }

}