package org.btwr.in_the_gloom.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;
import org.btwr.in_the_gloom.util.GloomEffectsConstants;
import org.btwr.in_the_gloom.util.GloomUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements GloomEffectsConstants {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci) {
        btwr$updateGloomState();
    }

    @Override
    public void btwr$updateGloomState() {
        int gloomLevel = this.btwr$getGloomLevel();

        if (this.btwr$getPreviousGloomLevel() != btwr$getGloomLevel()) {
            btwr$setInGloomCounter(0);
            btwr$setPreviousGloomLevel(gloomLevel);

            if (gloomLevel == 3) {
                this.playSound(SoundEvents.ENTITY_ENDERMAN_STARE,1.0F, 1.0F);
            }
        }

        if (gloomLevel > 0) {
            btwr$setInGloomCounter(btwr$getInGloomCounter() + 1);

            float counterProgress = (float) btwr$getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

            if (counterProgress > 1.0F) {
                counterProgress = 1.0F;
            }

            // general cave sounds
            float fCaveSoundChance = MAXIMUM_GLOOM_CAVE_SOUND_CHANCE;
            float fCaveSoundVolume = MAXIMUM_GLOOM_CAVE_SOUND_VOLUME;

            if (gloomLevel > 1) {
                // growls
                float fGrowlSoundChance = MAXIMUM_GLOOM_GROWL_SOUND_CHANCE;
                float fGrowlSoundVolume = MAXIMUM_GLOOM_GROWL_SOUND_VOLUME;

                if (gloomLevel > 2) {
                    // insert effects here for when the player is getting bit
                }
                else {
                    fGrowlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * counterProgress;
                    fGrowlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * counterProgress;
                }

                if (this.getRandom().nextFloat() < fGrowlSoundChance) {
                    GloomUtil.playSoundInRandomDirection(this, SoundEvents.ENTITY_WOLF_GROWL, fGrowlSoundVolume, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                }
            }
            else {
                fCaveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * counterProgress;
                fCaveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * counterProgress;
            }

            if (this.getRandom().nextFloat() < fCaveSoundChance) {
                GloomUtil.playSoundInRandomDirection(this, SoundEvents.AMBIENT_CAVE.value(), fCaveSoundVolume, 0.5F + this.getRandom().nextFloat(), 5D);
            }
        }
    }

}