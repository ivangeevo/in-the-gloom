package org.ivangeevo.inthegloom.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.ivangeevo.inthegloom.util.GloomEffectsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
        implements GloomEffectsConstants
{
    @Unique
    float currentGloomFOVMultiplier = 1F;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci)
    {
        //updateGloomState();
    }

    /**
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

            int lightLevel = getWorld().getLightLevel(getBlockPos());

            if (lightLevel <= 0) {
                float fCaveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * fCounterProgress;
                float fCaveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * fCounterProgress;

                if (iGloomLevel > 1) {
                    float fGrowlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * fCounterProgress;
                    float fGrowlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * fCounterProgress;

                    if (iGloomLevel > 2) {
                        // Insert effects here for when the player is getting bit
                    } else {
                        if (getRandom().nextFloat() < fGrowlSoundChance) {
                            GloomEffectsManager.getInstance().playSoundInRandomDirection(this, SoundEvents.ENTITY_WOLF_GROWL, fGrowlSoundVolume, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                        }
                    }
                }

                if (this.getRandom().nextFloat() < fCaveSoundChance) {
                    GloomEffectsManager.getInstance().playSoundInRandomDirection(this, SoundEvents.AMBIENT_CAVE.value(), fCaveSoundVolume, 0.5F + this.getRandom().nextFloat(), 5D);
                }
            }
        }
    }
    **/
}
