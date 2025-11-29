package org.btwr.in_the_gloom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.btwr.in_the_gloom.data.ModDataAttachments;
import org.btwr.shared_library.gui.hud.HUDInitializeListener;
import org.btwr.shared_library.gui.hud.PenaltyDisplayManager;
import org.btwr.in_the_gloom.util.GloomUtil;

public class ModPenalties implements HUDInitializeListener {

    @Override
    public void init(MinecraftClient client, PenaltyDisplayManager dm) {
        dm.addPenalty(new PenaltyDisplayManager.Penalty(
                // Priority
                PenaltyDisplayManager.GLOOM_PRIORITY,
                // Text conditions
                () -> {
                    // Get player
                    PlayerEntity player = client.player;
                    if (player == null) return "";
                    var gloomData = player.getAttached(ModDataAttachments.PLAYER_GLOOM);
                    if (gloomData != null) {
                        // Get gloom level
                        int gloomLevel = gloomData.getGloomLevel();
                        switch (gloomLevel) {
                            case 1 -> {
                                return "penalty_text.in_the_gloom.gloom";
                            }
                            case 2 -> {
                                return "penalty_text.in_the_gloom.dread";
                            }
                            case 3 -> {
                                return "penalty_text.in_the_gloom.terror";
                            }
                        }
                    }
                    return "";
                },
                // Draw conditions
                () -> {
                    PlayerEntity player = client.player;
                    if (player == null) return false;
                    return (GloomUtil.isInGloom(player));
                }
        ));
    }

}