package org.ivangeevo.inthegloom.client;

import btwr.btwr_sl.lib.gui.HUDInitializeListener;
import btwr.btwr_sl.lib.gui.PenaltyDisplayManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.ivangeevo.inthegloom.util.GloomUtil;

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

                    // Get gloom level
                    int gloomLevel = player.getGloomLevel();
                    switch (gloomLevel) {
                        case 1 -> {
                            return "Gloom";
                        }
                        case 2 -> {
                            return "Dread";
                        }
                        case 3 -> {
                            return "Terror";
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
