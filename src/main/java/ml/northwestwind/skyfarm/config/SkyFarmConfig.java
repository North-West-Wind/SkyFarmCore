package ml.northwestwind.skyfarm.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SkyFarmConfig {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER;

    public static ForgeConfigSpec.BooleanValue HIDE_ADVANCEMENT, ALLOW_SEEK_ISLAND, GLOBAL_STAGE;
    public static ForgeConfigSpec.IntValue ISLAND_OFFSET;

    static {
        init();
        SERVER = SERVER_BUILDER.build();
    }

    public static void loadServerConfig(String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        SERVER.setConfig(file);
    }

    private static void init() {
        HIDE_ADVANCEMENT = SERVER_BUILDER.comment("Don't show non-Sky Farm advancements?").define("hide_adv", true);
        ALLOW_SEEK_ISLAND = SERVER_BUILDER.comment("Allow new player to seek position for their island? (Disables island offset)").define("allow_seek", false);
        ISLAND_OFFSET = SERVER_BUILDER.comment("The distance between each island").defineInRange("island_offset", 8000, 10, 16000);
        GLOBAL_STAGE = SERVER_BUILDER.comment("Use same stage progress for all players?").define("global_stage", true);
    }

    public static void setHideAdvancement(boolean enabled) {
        HIDE_ADVANCEMENT.set(enabled);
        HIDE_ADVANCEMENT.save();
    }
}
