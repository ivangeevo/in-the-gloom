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
                    int gloomLevel = player.btwr$getGloomLevel();
                    switch (gloomLevel) {
                        case 1 -> {
                            return "penalty.in_the_gloom.gloom";
                        }
                        case 2 -> {
                            return "penalty.in_the_gloom.dread";
                        }
                        case 3 -> {
                            return "penalty.in_the_gloom.terror";
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