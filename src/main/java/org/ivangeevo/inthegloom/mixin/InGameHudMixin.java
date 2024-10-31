package org.ivangeevo.inthegloom.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.LunarWorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("HEAD"))
    private void injectedRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && !player.getAbilities().creativeMode) {
            // Get the position and world
            BlockPos pos = player.getBlockPos();
            int skyLightLevel = player.getWorld().getLightLevel(LightType.SKY, pos);
            int blockLightLevel = player.getWorld().getLightLevel(LightType.BLOCK, pos);

            // Get the moon phase
            int moonPhase = ((LunarWorldView) player.getWorld()).getMoonPhase();

            // Check if it's nighttime
            long timeOfDay = player.getWorld().getTimeOfDay() % 24000;
            boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

            // Set the gloom threshold for moon phase
            int gloomMoonPhaseThreshold = 3; // Trigger gloom on moon phases 3 and 4 (darker nights)

            // Conditions for gloom
            boolean isUnderground = skyLightLevel == 0 && blockLightLevel < 1;
            boolean isOutsideOnDarkNight = isNight && skyLightLevel == 15 && moonPhase >= gloomMoonPhaseThreshold && blockLightLevel == 0;

            // Status text variable
            String statusText = "";

            // Check gloom conditions
            if (isUnderground || isOutsideOnDarkNight) {
                statusText = "Gloom";
            }

            // Render the status text if not empty
            if (!statusText.isEmpty()) {
                renderDarknessStatusText(context, statusText);
            }
        }
    }

    @Unique
    private void renderDarknessStatusText(DrawContext context, String text) {
        TextRenderer textRenderer = getTextRenderer();
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
