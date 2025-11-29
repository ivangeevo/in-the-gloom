package org.btwr.in_the_gloom.mixin.added;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.in_the_gloom.util.GloomEffectsConstants;
import org.btwr.in_the_gloom.util.GloomUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityAddedMixin extends PlayerEntity implements GloomEffectsConstants {

    public ServerPlayerEntityAddedMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void btwr$updateGloomState() {
        if (isAlive()) {
            if (GloomUtil.isInGloom(this) && !this.isCreative()) {
                btwr$setInGloomCounter(btwr$getInGloomCounter() + 1);

                if (btwr$getGloomLevel() == 0 || (btwr$getInGloomCounter() > GLOOM_COUNTER_BETWEEN_STATE_CHANGES && btwr$getGloomLevel() < 3))
                {
                    btwr$setGloomLevel(btwr$getGloomLevel() + 1);
                    btwr$setInGloomCounter(0);
                }

                if (btwr$getGloomLevel() >= 3) {
                    if (getWorld().getTime() % 80L == 0L) {
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 180, 1, true, false));
                    }

                    float counterProgress = (float) btwr$getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

                    if (counterProgress > 1.0F) {
                        counterProgress = 1.0F;
                    }

                    float gloomBiteChance = minimumGloomBiteChance + (maximumGloomBiteChance - minimumGloomBiteChance) * counterProgress;

                    if (getRandom().nextFloat() < gloomBiteChance) {
                        if (damage(getDamageSources().generic(), 1.0F)) {
                            if (getHealth() <= 0.0F)
                            {
                                BlockPos soundPos = getBlockPos();
                                this.getWorld().playSound(this, soundPos, SoundEvents.ENTITY_PLAYER_BURP,
                                        SoundCategory.PLAYERS, 1.0F, getRandom().nextFloat() * 0.4F + 0.7F);
                            }
                        }
                    }
                }
            }
            else
            {
                btwr$setGloomLevel(0);
                btwr$setInGloomCounter(0);
            }
        }
    }

}