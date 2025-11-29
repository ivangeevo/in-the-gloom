package org.btwr.in_the_gloom.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.btwr.in_the_gloom.InTheGloomMod;

public class InTheGloomConfigGUI {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent).setTitle(Text.translatable("title.in_the_gloom.config"));
        //builder.setSavingRunnable(() -> { InTheGloomMod.getInstance().saveSettings(); });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.in_the_gloom.category.general"));

        // Client Settings
        general.addEntry(entryBuilder.startTextDescription(Text.translatable("config.in_the_gloom.text.clientSettingsText")).build());
        // Example for empty config settings text, enable if you have no client settings
        general.addEntry(entryBuilder.startTextDescription(Text.translatable("config.in_the_gloom.text.emptyClientConfigText")).build());

        // Server Settings
        general.addEntry(entryBuilder.startTextDescription(Text.translatable("config.in_the_gloom.text.serverSettingsText")).build());
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.in_the_gloom.text.serverSettingsNoAccessText"))
                .build()
        );

        return builder.build();
    }

}