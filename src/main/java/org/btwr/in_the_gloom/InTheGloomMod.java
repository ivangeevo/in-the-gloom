package org.btwr.in_the_gloom;

import net.fabricmc.api.ModInitializer;
import org.btwr.in_the_gloom.config.InTheGloomConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InTheGloomMod implements ModInitializer {

    public static final String MOD_ID = "in_the_gloom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static InTheGloomMod instance;
    public static InTheGloomMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing In The Gloom.");
        instance = this;

        InTheGloomConfig.register();
    }

}
