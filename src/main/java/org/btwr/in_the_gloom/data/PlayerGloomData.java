package org.btwr.in_the_gloom.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundEvents;
import org.btwr.in_the_gloom.util.GloomEffectsConstants;
import org.btwr.in_the_gloom.util.GloomUtil;
import org.btwr.shared_library.api.data.EntityAttachmentBase;

public class PlayerGloomData implements EntityAttachmentBase<PlayerEntity>, GloomEffectsConstants {

    int gloomLevel;
    int previousGloomLevel;
    int inGloomCounter;

    float currentGloomFOVMultiplier = 1F;

    boolean dirty = false;

    public PlayerGloomData(int gloomLevel, int previousGloomLevel, int inGloomCounter, float currentGloomFOVMultiplier) {
        this.gloomLevel = gloomLevel;
        this.previousGloomLevel = previousGloomLevel;
        this.inGloomCounter = inGloomCounter;
        this.currentGloomFOVMultiplier = currentGloomFOVMultiplier;
    }

    public static final Codec<PlayerGloomData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("gloomLevel").forGetter(PlayerGloomData::getGloomLevel),
                    Codec.INT.fieldOf("previousGloomLevel").forGetter(PlayerGloomData::getPreviousGloomLevel),
                    Codec.INT.fieldOf("inGloomCounter").forGetter(PlayerGloomData::getInGloomCounter),
                    Codec.FLOAT.fieldOf("currentGloomFOVMultiplier").forGetter(PlayerGloomData::getCurrentGloomFOVMultiplier)
            ).apply(instance, PlayerGloomData::new)
    );

    public static PacketCodec<ByteBuf, PlayerGloomData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public int getGloomLevel() {
        return gloomLevel;
    }

    public int getPreviousGloomLevel() {
        return previousGloomLevel;
    }

    public int getInGloomCounter() {
        return inGloomCounter;
    }

    public float getCurrentGloomFOVMultiplier() {
        return currentGloomFOVMultiplier;
    }

    @Override
    public void tick(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            playGloomSounds(player);
            updateGloomFOVMultiplier();
            return;
        }

        if (!player.isAlive()) return;

        if (GloomUtil.isInGloom(player) && !player.isCreative()) {
            // Increment counter
            inGloomCounter += 1;

            // Progress gloom level if necessary
            if (gloomLevel == 0 || (inGloomCounter > GLOOM_COUNTER_BETWEEN_STATE_CHANGES && gloomLevel < 3)) {
                gloomLevel += 1;
                inGloomCounter = 0;
                markDirty();
            }

            // Level 3 effects
            if (gloomLevel >= 3) {
                // Nausea every 4 seconds (80 ticks)
                if (player.getWorld().getTime() % 80L == 0L) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 180, 1, true, false));
                }

                // Chance to take damage
                float counterProgress = Math.min(1.0F, (float) inGloomCounter / GLOOM_COUNTER_BETWEEN_STATE_CHANGES);
                float gloomBiteChance = minimumGloomBiteChance + (maximumGloomBiteChance - minimumGloomBiteChance) * counterProgress;

                if (player.getRandom().nextFloat() < gloomBiteChance) {
                    player.damage(player.getDamageSources().generic(), 1.0F);
                }
            }
        }
        else {
            // Player left gloom area: reset state
            if (gloomLevel != 0 || inGloomCounter != 0) {
                gloomLevel = 0;
                inGloomCounter = 0;
                markDirty();
            }
        }
    }

    public float updateGloomFOVMultiplier() {
        if (gloomLevel == 0) {
            currentGloomFOVMultiplier -= GLOOM_FOV_MULTIPLIER_DELTA_OUT_PER_TICK;

            if (currentGloomFOVMultiplier < 1F) {
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

    private void playGloomSounds(PlayerEntity player) {
        if (previousGloomLevel != gloomLevel) {
            inGloomCounter = 0;
            previousGloomLevel = gloomLevel;
            markDirty();

            if (gloomLevel == 3) {
                player.playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 1.0F, 1.0F);
            }
        }

        if (gloomLevel > 0) {
            inGloomCounter += 1;
            markDirty();

            float counterProgress = (float) inGloomCounter / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

            if (counterProgress > 1.0F) {
                counterProgress = 1.0F;
            }

            // general cave sounds
            float caveSoundChance = MAXIMUM_GLOOM_CAVE_SOUND_CHANCE;
            float caveSoundVolume = MAXIMUM_GLOOM_CAVE_SOUND_VOLUME;

            if (gloomLevel > 1) {
                // growls
                float growlSoundChance = MAXIMUM_GLOOM_GROWL_SOUND_CHANCE;
                float growlSoundVolume = MAXIMUM_GLOOM_GROWL_SOUND_VOLUME;

                if (gloomLevel > 2) {
                    // insert effects here for when the player is getting bit
                }
                else {
                    growlSoundChance = MINIMUM_GLOOM_GROWL_SOUND_CHANCE + (MAXIMUM_GLOOM_GROWL_SOUND_CHANCE - MINIMUM_GLOOM_GROWL_SOUND_CHANCE) * counterProgress;
                    growlSoundVolume = MINIMUM_GLOOM_GROWL_SOUND_VOLUME + (MAXIMUM_GLOOM_GROWL_SOUND_VOLUME - MINIMUM_GLOOM_GROWL_SOUND_VOLUME) * counterProgress;
                }

                if (player.getRandom().nextFloat() < growlSoundChance) {
                    GloomUtil.playSoundInRandomDirection(player, SoundEvents.ENTITY_WOLF_GROWL, growlSoundVolume, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.05F + 0.55F, 5D);
                }
            }
            else {
                caveSoundChance = MINIMUM_GLOOM_CAVE_SOUND_CHANCE + (MAXIMUM_GLOOM_CAVE_SOUND_CHANCE - MINIMUM_GLOOM_CAVE_SOUND_CHANCE) * counterProgress;
                caveSoundVolume = MINIMUM_GLOOM_CAVE_SOUND_VOLUME + (MAXIMUM_GLOOM_CAVE_SOUND_VOLUME - MINIMUM_GLOOM_CAVE_SOUND_VOLUME) * counterProgress;
            }

            if (player.getRandom().nextFloat() < caveSoundChance) {
                GloomUtil.playSoundInRandomDirection(player, SoundEvents.AMBIENT_CAVE.value(), caveSoundVolume, 0.5F + player.getRandom().nextFloat(), 5D);
            }
        }
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        dirty = false;
    }

}