package org.ivangeevo.inthegloom.client;

import btwr.btwr_sl.lib.event.EventHUDInitialized;
import net.fabricmc.api.ClientModInitializer;

public class InTheGloomModClient implements ClientModInitializer {
    private static final ModPenalties penalties = new ModPenalties();

    @Override
    public void onInitializeClient() {
        // Initialize penalties
        EventHUDInitialized.register(penalties);
    }
}
