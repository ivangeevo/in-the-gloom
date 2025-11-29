package org.btwr.in_the_gloom.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.btwr.in_the_gloom.InTheGloomMod;
import org.btwr.in_the_gloom.event.ModEvents;
import org.btwr.shared_library.data.EntityAttachmentBase;

public class ModDataAttachments {

    public static final AttachmentType<PlayerGloomData> PLAYER_GLOOM = AttachmentRegistry.create(
            Identifier.of(InTheGloomMod.MOD_ID, "player_gloom"),
            builder -> builder
                    .initializer(() -> new PlayerGloomData(0, 0, 0, 1))
                    .persistent(PlayerGloomData.CODEC)
                    .syncWith(PlayerGloomData.PACKET_CODEC, AttachmentSyncPredicate.targetOnly())
    );

    public static void register() {
        InTheGloomMod.LOGGER.info("Registering {} attachments", InTheGloomMod.MOD_ID);

        ModEvents.LIVING_TICK.add(livingEntity -> {
            if (livingEntity instanceof PlayerEntity player) {
                tickAndSyncGloomData(PLAYER_GLOOM, player);
            }
        });
    }

    private static <T extends Entity, A extends EntityAttachmentBase<T>> void tickAndSync(AttachmentType<A> type, LivingEntity entity) {
        A attachment = entity.getAttachedOrCreate(type);
        attachment.tick((T) entity);
        if (attachment.isDirty()) {
            entity.setAttached(type, attachment);
            attachment.markDirty();
        }
    }

    private static void tickAndSyncGloomData(AttachmentType<PlayerGloomData> type, PlayerEntity player) {
        var attachment = player.getAttachedOrCreate(type);
        attachment.tick(player);
        if (attachment.isDirty()) {
            player.setAttached(type, attachment);
            attachment.markClean();
        }
    }

}