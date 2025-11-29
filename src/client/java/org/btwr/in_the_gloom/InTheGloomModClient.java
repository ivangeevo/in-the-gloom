package org.btwr.in_the_gloom;

import net.fabricmc.api.ClientModInitializer;
import org.btwr.shared_library.event.EventHUDInitialized;

public class InTheGloomModClient implements ClientModInitializer {

    private static final ModPenalties penalties = new ModPenalties();

    @Override
    public void onInitializeClient() {
        // Initialize penalties
        EventHUDInitialized.register(penalties);
    }

}