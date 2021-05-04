package ml.northwestwind.skyfarm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(SkyFarm.MOD_ID)
public class SkyFarm {
    public static final String MOD_ID = "skyfarm";
    public static final Map<String, Integer> BEE_TYPES = Maps.newHashMap();

    public SkyFarm() {
        createFolderIfAbsent("./skyfarm");
        createFolderIfAbsent("./skyfarm/backups");

        getResourcefulBeeConfig();
    }

    public static class SkyFarmItemGroup extends ItemGroup {
        public static final SkyFarmItemGroup INSTANCE = new SkyFarmItemGroup();

        public SkyFarmItemGroup() {
            super(MOD_ID);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR);
        }
    }

    private static void createFolderIfAbsent(String path) {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) folder.mkdir();
    }

    private static void getResourcefulBeeConfig() {
        File beesFolder = new File("./config/resourcefulbees/bees");
        if (!beesFolder.exists()) return;
        LogManager.getLogger().info("Found resourcefulbees/bees folder");
        BEE_TYPES.clear();
        try (Stream<Path> walk = Files.walk(Paths.get(beesFolder.getAbsolutePath()))) {
            JsonParser parser = new JsonParser();
            for (Path path : walk.collect(Collectors.toList())) {
                if (!path.toFile().isFile() || !path.getFileName().toString().endsWith(".json")) continue;
                JsonObject obj = (JsonObject) parser.parse(new FileReader(path.toFile()));
                JsonObject colorData = obj.getAsJsonObject("ColorData");
                String hexColor = colorData.get("honeycombColor").getAsString().replace("#", "");
                BEE_TYPES.put(path.getFileName().toString().replace(".json", "").toLowerCase(), Integer.parseInt(hexColor, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
