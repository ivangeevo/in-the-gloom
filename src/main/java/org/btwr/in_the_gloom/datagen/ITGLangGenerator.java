package org.btwr.in_the_gloom.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.in_the_gloom.InTheGloomMod;

import java.util.concurrent.CompletableFuture;

public class ITGLangGenerator extends FabricLanguageProvider {

    public ITGLangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        this.generateConfigTranslations(tb);
    }

    private void generateConfigTranslations(TranslationBuilder tb) {
        this.addConfigMenuDefaults(tb);
        this.addConfigMenuTitle("In The Gloom Configuration Menu", tb);
        this.addConfigCategory("general", "General", tb);
        //this.addConfig("overworldGloom", "Overworld Gloom", tb);
        //this.addConfig("theNetherGloom", "The Nether Gloom", tb);
        //this.addConfig("theEndGloom", "The End Gloom", tb);
        //this.addConfigTooltip("overworldGloom", "Enable Gloom effects in The Overworld", tb);
        //this.addConfigTooltip("theNetherGloom", "Enable Gloom effects in The Nether", tb);
        //this.addConfigTooltip("theEndGloom", "Enable Gloom effects in The End", tb);

        this.addPenaltyText("gloom", "Gloom", tb);
        this.addPenaltyText("dread", "Dread", tb);
        this.addPenaltyText("terror", "Terror", tb);
    }

    private void addConfigMenuDefaults(TranslationBuilder tb) {
        this.addSimpleText("clientSettingsText", "Client Settings:", tb);
        this.addSimpleText("emptyClientConfigText", "§eNote:§r There are currently no client config settings.", tb);
        this.addSimpleText("serverSettingsText", "Server Settings:", tb);
        this.addSimpleText("serverSettingsNoAccessText", "§eNote:§r Server settings are not accessible in menus." +
                "\nThey can only be changed by editing the config file manually and require a world reload to take effect.", tb
        );
    }

    private void addItemGroup(String entryPath, String translation, TranslationBuilder tb) {
        tb.add("itemgroup." + entryPath, translation);
    }

    private void addConfigMenuTitle(String translation, TranslationBuilder tb) {
        tb.add("title." + InTheGloomMod.MOD_ID + ".config", translation);
    }

    private void addPenaltyText(String path, String translation, TranslationBuilder tb) {
        tb.add("penalty_text." + InTheGloomMod.MOD_ID + "." + path, translation);
    }

    private void addConfigCategory(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "category." + path, translation);
    }

    private void addSimpleText(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "text." + path, translation);
    }

    private void addConfig(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + path, translation);
    }

    private void addConfigTooltip(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "tooltip." + path, translation);
    }

    private String configBasePath() {
        return "config." + InTheGloomMod.MOD_ID + ".";
    }

}