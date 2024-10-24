package org.ivangeevo.inthegloom.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ivangeevo.inthegloom.GloomEffectsConstants;
import org.ivangeevo.inthegloom.util.GloomEffectsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements GloomEffectsConstants
{
    @Unique
    float currentGloomFOVMultiplier = 1F;

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }


    @Inject(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getActiveItem()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void injectedGloomFOV(CallbackInfoReturnable<Float> cir)
    {
        float f = 1.0f;
        if (this.getAbilities().flying) {
            f *= 1.1f;
        }
        if (this.getAbilities().getWalkSpeed() == 0.0f || Float.isNaN(f *= ((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / this.getAbilities().getWalkSpeed() + 1.0f) / 2.0f) || Float.isInfinite(f)) {
            f = 1.0f;
        }
        // INJECTED:
        f *= updateGloomFOVMultiplier();
        // END INJECTED
        ItemStack itemStack = this.getActiveItem();
        if (this.isUsingItem()) {
            if (itemStack.isOf(Items.BOW)) {
                int i = this.getItemUseTime();
                float g = (float)i / 20.0f;
                g = g > 1.0f ? 1.0f : (g *= g);
                f *= 1.0f - g * 0.15f;
            } else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && this.isUsingSpyglass()) {
                cir.setReturnValue( 0.1f );
            }
        }

        cir.setReturnValue( MathHelper.lerp(MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), 1.0f, f) );
    }

    @Unique
    private float updateGloomFOVMultiplier()
    {
        int iGloomLevel = getGloomLevel();

        if ( iGloomLevel == 0 )
        {
            currentGloomFOVMultiplier -= GLOOM_FOV_MULTIPLIER_DELTA_OUT_PER_TICK;

            if (currentGloomFOVMultiplier < 1F )
            {
                currentGloomFOVMultiplier = 1F;
            }
        }
        else
        {
            currentGloomFOVMultiplier += GLOOM_FOV_MULTIPLIER_DELTA_IN_PER_TICK;

            if (currentGloomFOVMultiplier > MAXIMUM_GLOOM_FOV_MULTIPLIER)
            {
                currentGloomFOVMultiplier = MAXIMUM_GLOOM_FOV_MULTIPLIER;
            }
        }

        return currentGloomFOVMultiplier;
    }



}
