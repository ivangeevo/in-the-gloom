package org.ivangeevo.inthegloom;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public interface GloomEffectsConstants
{

    int GLOOM_COUNTER_BETWEEN_STATE_CHANGES = 1200; // 1 minute
    float MINIMUM_GLOOM_CAVE_SOUND_CHANCE = 0.01F; // 1/5 seconds
    float MAXIMUM_GLOOM_CAVE_SOUND_CHANCE = 0.05F; // 1/second
    float MINIMUM_GLOOM_CAVE_SOUND_VOLUME = 0.1F;
    float MAXIMUM_GLOOM_CAVE_SOUND_VOLUME = 4.0F;
    float MINIMUM_GLOOM_GROWL_SOUND_CHANCE = 0.01F; // 1/5 seconds
    float MAXIMUM_GLOOM_GROWL_SOUND_CHANCE = 0.05F; // 1/second
    float MINIMUM_GLOOM_GROWL_SOUND_VOLUME = 0.1F;
    float MAXIMUM_GLOOM_GROWL_SOUND_VOLUME = 4.0F;
    float MAXIMUM_GLOOM_FOV_MULTIPLIER = 1.5F;
    float GLOOM_FOV_MULTIPLIER_TIME_FOR_TRANSITION_IN = 10.0F; // in seconds
    float GLOOM_FOV_MULTIPLIER_TIME_FOR_TRANSITION_OUT = 2.0F; // in seconds
    float GLOOM_FOV_MULTIPLIER_DELTA_IN_PER_TICK = ((MAXIMUM_GLOOM_FOV_MULTIPLIER - 1.0F ) / 20F ) / GLOOM_FOV_MULTIPLIER_TIME_FOR_TRANSITION_IN;
    float GLOOM_FOV_MULTIPLIER_DELTA_OUT_PER_TICK = ((MAXIMUM_GLOOM_FOV_MULTIPLIER - 1.0F ) / 20F ) /
            GLOOM_FOV_MULTIPLIER_TIME_FOR_TRANSITION_OUT;

    float minimumGloomBiteChance = 0.01F;
    float maximumGloomBiteChance = 0.05F; // 1/second


}
