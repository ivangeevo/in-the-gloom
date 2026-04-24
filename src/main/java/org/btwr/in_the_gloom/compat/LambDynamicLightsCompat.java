package org.btwr.in_the_gloom.compat;


import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

public class LambDynamicLightsCompat implements DynamicLightsInitializer {

    public static ItemLightSourceManager itemLightSourceManager;

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        itemLightSourceManager = context.itemLightSourceManager();
    }

    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {}

    public static boolean playerHoldsLambRegisteredLight(PlayerEntity player) {
        // Check if LambDynamicLights is loaded at all
        if (!FabricLoader.getInstance().isModLoaded("lambdynlights")) return false;

        var manager = itemLightSourceManager;
        if (manager == null) return false;

        boolean submerged = player.isSubmergedInWater();

        for (var stack : player.getHandItems()) {
            if (!stack.isEmpty() && manager.getLuminance(stack, submerged) > 0) return true;
        }
        for (var stack : player.getArmorItems()) {
            if (!stack.isEmpty() && manager.getLuminance(stack, submerged) > 0) return true;
        }
        return false;
    }

}
