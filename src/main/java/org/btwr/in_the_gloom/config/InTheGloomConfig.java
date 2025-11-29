package org.btwr.in_the_gloom.config;

import org.btwr.in_the_gloom.InTheGloomMod;
import org.btwr.shared_library.api.config.ConfigBuilder;
import org.btwr.shared_library.api.config.ConfigGroup;
import org.btwr.shared_library.api.config.ConfigSetting;
import org.btwr.shared_library.api.config.TomlConfigManager;

public class InTheGloomConfig {

    /** Replace with your MOD_ID for easy adaptation **/
    private static final String MOD_ID = InTheGloomMod.MOD_ID;

    public static final ConfigGroup CONFIG;

    /** Call this method in your mod initializer so the class can initialize **/
    public static void register() {}

    public static final ConfigSetting<Boolean> overworldGloom =
            ConfigBuilder.booleanSetting("overworldGloom")
                    .defaultValue(true)
                    .comment("Enable Gloom effects in The Overworld")
                    .build();

    public static final ConfigSetting<Boolean> theNetherGloom =
            ConfigBuilder.booleanSetting("theNetherGloom")
                    .defaultValue(true)
                    .comment("Enable Gloom effects in The Nether")
                    .build();

    public static final ConfigSetting<Boolean> theEndGloom =
            ConfigBuilder.booleanSetting("theEndGloom")
                    .defaultValue(true)
                    .comment("Enable Gloom effects in The End")
                    .build();

    static {
        CONFIG = new ConfigGroup(String.format("%s/%s_common.toml", MOD_ID, MOD_ID));
        CONFIG.add(overworldGloom);
        CONFIG.add(theNetherGloom);
        CONFIG.add(theEndGloom);
        TomlConfigManager.registerGroup(CONFIG); // auto init/load/save
    }

}