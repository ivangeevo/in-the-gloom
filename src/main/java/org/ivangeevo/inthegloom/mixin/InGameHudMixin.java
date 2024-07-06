package org.ivangeevo.inthegloom.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("HEAD"))
    private void injectedRender(DrawContext context, float tickDelta, CallbackInfo ci)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && !player.getAbilities().creativeMode)
        {
            int lightLevel = player.getWorld().getLightLevel(player.getBlockPos());

            String statusText = "";

            if (lightLevel <= 0)
            {
                statusText = "Gloom";
            }

            if (!statusText.isEmpty())
            {
                renderDarknessStatusText(context, statusText);
            }

        }
    }

    @Unique
    private void renderDarknessStatusText(DrawContext context, String text)
    {
        TextRenderer textRenderer = getTextRenderer();
        Text statusText = Text.translatable(text);

        // Calculate the position of the hunger bar
        int hungerBarX = this.scaledWidth / 2 + 91;
        int hungerBarY = this.scaledHeight - 39;

        // Adjust the X and Y positions to render above the hunger bar
        int textX = hungerBarX - (textRenderer.getWidth(statusText) / 2) - 20; // 20 pixels to the left of the hunger bar
        int textY = hungerBarY - 20; // Adjust the Y position to be above the hunger bar

        // Draw the status text
        context.drawText(textRenderer, statusText, textX, textY, 0xFFFFFFFF, true);
    }

}
