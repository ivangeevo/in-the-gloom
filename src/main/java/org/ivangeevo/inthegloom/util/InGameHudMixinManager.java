package org.ivangeevo.inthegloom.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

public class InGameHudMixinManager
{
    private static final InGameHudMixinManager INSTANCE = new InGameHudMixinManager();

    private InGameHudMixinManager() {}

    public static InGameHudMixinManager getInstance() { return INSTANCE; }


    public void setGloomStatusText(DrawContext context, TextRenderer textRenderer, PlayerEntity player)
    {
        if (player != null) {
            // Check if the player is in gloom using the GloomUtil class
            boolean isInGloom = GloomUtil.isInGloom(player);

            // Status text variable
            String statusText = getString(player, isInGloom);

            // Render the status text if not empty
            if (!statusText.isEmpty()) {
                renderDarknessStatusText(context, textRenderer, statusText);
            }
        }
    }

    private static @NotNull String getString(PlayerEntity player, boolean isInGloom) {
        String statusText = "";

        int gloomLevel = player.getGloomLevel();

        // Check if the player is in gloom
        if (isInGloom)
        {
            if (gloomLevel == 1)
            {
                statusText = "Gloom";
            }
            else if (gloomLevel == 2)
            {
                statusText = "Dread";
            }
            else if (gloomLevel == 3)
            {
                statusText = "Terror";
            }
        }
        return statusText;
    }

    @Unique
    private void renderDarknessStatusText(DrawContext context, TextRenderer textRenderer, String text) {
        Text statusText = Text.translatable(text);

        // Calculate the position of the hunger bar
        int hungerBarX = context.getScaledWindowWidth() / 2 + 91;
        int hungerBarY = context.getScaledWindowHeight() - 39;

        // Adjust the X and Y positions to render above the hunger bar
        int textX = hungerBarX - (textRenderer.getWidth(statusText) / 2) - 20; // 20 pixels to the left of the hunger bar
        int textY = hungerBarY - 20; // Adjust the Y position to be above the hunger bar

        // Draw the status text
        context.drawText(textRenderer, statusText, textX, textY, 0xFFFFFFFF, true);
    }

}
