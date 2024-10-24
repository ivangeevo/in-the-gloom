package org.ivangeevo.inthegloom.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.ivangeevo.inthegloom.util.GloomEffectsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements GloomEffectsConstants
{
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract boolean isCreative();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
    {
        super(world, pos, yaw, gameProfile);
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci)
    {
        updateGloomState();
    }

    @Override
    public void updateGloomState()
    {
        if (isAlive())
        {
            if (GloomEffectsManager.getInstance().isInGloom(this) && !this.isCreative())
            {
                setInGloomCounter(getInGloomCounter() + 1);

                if (getGloomLevel() == 0 || (getInGloomCounter() > GLOOM_COUNTER_BETWEEN_STATE_CHANGES && getGloomLevel() < 3))
                {
                    setGloomLevel(getGloomLevel() + 1);
                    setInGloomCounter(0);
                }

                if (getGloomLevel() >= 3)
                {
                    if (getWorld().getTime() % 80L == 0L)
                    {
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 180, 1, true, false));
                    }

                    float counterProgress = (float) getInGloomCounter() / (float) GLOOM_COUNTER_BETWEEN_STATE_CHANGES;

                    if (counterProgress > 1.0F)
                    {
                        counterProgress = 1.0F;
                    }

                    float gloomBiteChance = minimumGloomBiteChance + (maximumGloomBiteChance - minimumGloomBiteChance) * counterProgress;

                    if (getRandom().nextFloat() < gloomBiteChance)
                    {
                        if (damage(getDamageSources().generic(), 1.0F))
                        {
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
                setGloomLevel(0);
                setInGloomCounter(0);
            }
        }

        System.out.println("Gloom Level: " + getGloomLevel() + ", In Gloom Counter: " + getInGloomCounter());
        System.out.println("Is In Gloom: " + GloomEffectsManager.getInstance().isInGloom(this));
    }

}

