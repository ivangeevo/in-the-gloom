package org.ivangeevo.inthegloom;

import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import org.ivangeevo.inthegloom.config.ModSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InTheGloomMod implements ModInitializer {

    public static final String MOD_ID = "in_the_gloom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ModSettings settings;
    private static InTheGloomMod instance;
    public static InTheGloomMod getInstance() {
        return instance;
    }
    @Override
    public void onInitialize()
    {
        LOGGER.info("Initializing In The Gloom.");
        loadSettings();
        instance = this;
    }

    // Do not remove this comment or the project will NOT compile!
    public void loadSettings() {
        File file = new File("./config/btwr/inTheGloomCommon.json");
        Gson gson = new Gson();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                settings = gson.fromJson(fileReader, ModSettings.class);
                fileReader.close();
            } catch (IOException e) {
                LOGGER.warn("Could not load Tough Environment settings: " + e.getLocalizedMessage());
            }
        } else {
            settings = new ModSettings();
        }
    }

    public void saveSettings() {
        Gson gson = new Gson();
        File file = new File("./config/btwr/inTheGloomCommon.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(settings));
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.warn("Could not save In The Gloom settings: " + e.getLocalizedMessage());
        }
    }


}
