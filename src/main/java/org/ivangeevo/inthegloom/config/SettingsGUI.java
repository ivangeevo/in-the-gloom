package org.ivangeevo.inthegloom.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.ivangeevo.inthegloom.InTheGloomMod;

public class SettingsGUI
{
    static ModSettings settingsCommon = InTheGloomMod.getInstance().settings;
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.in_the_gloom.config"));
        builder.setSavingRunnable(() -> { InTheGloomMod.getInstance().saveSettings(); });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.in_the_gloom.category.general"));

        /** General Category **/
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.in_the_gloom.overworld_gloom"), settingsCommon.overworldGloom)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.overworldGloom = newValue)
                .setTooltip(Text.translatable("config.in_the_gloom.tooltip.overworld_gloom"))
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.in_the_gloom.the_nether_gloom"), settingsCommon.theNetherGloom)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.theNetherGloom = newValue)
                .setTooltip(Text.translatable("config.in_the_gloom.tooltip.the_nether_gloom"))
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.in_the_gloom.the_end_gloom"), settingsCommon.theEndGloom)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> settingsCommon.theEndGloom = newValue)
                .setTooltip(Text.translatable("config.in_the_gloom.tooltip.the_end_gloom"))
                .build());


        return builder.build();
    }

}
