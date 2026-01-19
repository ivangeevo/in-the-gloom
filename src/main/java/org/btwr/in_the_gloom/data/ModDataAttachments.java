package org.btwr.in_the_gloom.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.btwr.in_the_gloom.InTheGloomMod;
import org.btwr.shared_library.api.event.BTWREvents;

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

        BTWREvents.LIVING_TICK.add(livingEntity -> {
            if (livingEntity instanceof PlayerEntity player) {
                tickAndSyncGloomData(PLAYER_GLOOM, player);
            }
        });
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