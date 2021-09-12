package ml.northwestwind.skyfarm.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkyFarmAsteroidsConfig {
    private static final String ASTEROID_CONFIG_FILE = SkyFarm.MOD_ID + "-mining.json";
    private static final Set<ResourceLocation> BLOCKS = Sets.newHashSet();
    private static final Map<ResourceLocation, Integer> WEIGHTS = Maps.newHashMap();
    private static int defaultWeight = 1;

    public static void readConfig() {
        File asteroidConfig = new File(FMLPaths.CONFIGDIR.get().toString() + File.separator + ASTEROID_CONFIG_FILE);
        try {
            if (!asteroidConfig.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(asteroidConfig));
                writer.write("{}");
                writer.close();
            }
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(new FileReader(asteroidConfig));
            if (json.has("defaultWeight")) defaultWeight = json.get("defaultWeight").getAsInt();
            if (json.has("ores")) json.getAsJsonArray("ores").forEach(ore -> BLOCKS.add(new ResourceLocation(ore.getAsString())));
            if (json.has("weightOverrides")) {
                JsonObject overrides = json.getAsJsonObject("weightOverrides");
                overrides.entrySet().forEach(entry -> WEIGHTS.put(new ResourceLocation(entry.getKey()), entry.getValue().getAsInt()));
            }
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to read Asteroid Configs");
            e.printStackTrace();
        }
    }

    public static Map<ResourceLocation, Integer> getWeights() {
        return WEIGHTS;
    }

    public static Set<ResourceLocation> getBlocks() {
        return BLOCKS;
    }

    public static int getDefaultWeight() {
        return defaultWeight;
    }
}
