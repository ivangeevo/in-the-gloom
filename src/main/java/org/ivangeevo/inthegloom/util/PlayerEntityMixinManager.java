package org.ivangeevo.inthegloom.util;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.ivangeevo.inthegloom.InTheGloomMod;

public class PlayerEntityMixinManager implements GloomEffectsConstants {

    private static final PlayerEntityMixinManager instance = new PlayerEntityMixinManager();

    private final EntityAttributeModifier GLOOM_MOVEMENT_SPEED_MODIFIER = new EntityAttributeModifier(
            Identifier.of(InTheGloomMod.MOD_ID, "gloom_movement_speed_modifier"),
            -0.35f,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    private final EntityAttributeModifier GLOOM_BREAKING_SPEED_MODIFIER = new EntityAttributeModifier(
            Identifier.of(InTheGloomMod.MOD_ID, "gloom_breaking_speed_modifier"),
            -0.50f,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );


    private PlayerEntityMixinManager() {}

    public static PlayerEntityMixinManager getInstance()
    {
        return instance;
    }

    public void onServerTick(ServerPlayerEntity player) {
        updateSpeedAttributes(player);
    }

    private void updateSpeedAttributes(PlayerEntity player) {
        EntityAttributeInstance entityAttributeInstance2;
        EntityAttributeInstance entityAttributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (entityAttributeInstance != null) {
            if (GloomUtil.isInGloom(player)) {
                entityAttributeInstance.updateModifier(GLOOM_MOVEMENT_SPEED_MODIFIER);
            } else {
                entityAttributeInstance.removeModifier(GLOOM_MOVEMENT_SPEED_MODIFIER);
            }
        }

        if ((entityAttributeInstance2 = player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)) != null) {
            if (GloomUtil.isInGloom(player)) {
                entityAttributeInstance2.updateModifier(GLOOM_BREAKING_SPEED_MODIFIER);
            } else {
                entityAttributeInstance2.removeModifier(GLOOM_BREAKING_SPEED_MODIFIER);
            }
        }
    }
}
