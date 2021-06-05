package ml.northwestwind.skyfarm.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SkyFarmConfig {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder(), SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT, SERVER;

    public static ForgeConfigSpec.BooleanValue GOG_SKYBOX, HIDE_ADVANCEMENT;

    static {
        init();
        CLIENT = CLIENT_BUILDER.build();
        SERVER = SERVER_BUILDER.build();
    }

    public static void loadClientConfig(String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        CLIENT.setConfig(file);
    }

    public static void loadServerConfig(String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        SERVER.setConfig(file);
    }

    private static void init() {
        GOG_SKYBOX = CLIENT_BUILDER.comment("Whether or not to enable the Garden of Glass skybox").define("gog_skybox", true);

        HIDE_ADVANCEMENT = SERVER_BUILDER.comment("Whether or not to show non-Sky Farm advancements").define("hide_adv", true);
    }

    public static void setGogSkybox(boolean enabled) {
        GOG_SKYBOX.set(enabled);
        GOG_SKYBOX.save();
    }

    public static void setHideAdvancement(boolean enabled) {
        HIDE_ADVANCEMENT.set(enabled);
        HIDE_ADVANCEMENT.save();
    }
}
