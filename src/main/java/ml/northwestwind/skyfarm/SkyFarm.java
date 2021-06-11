package ml.northwestwind.skyfarm;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(SkyFarm.MOD_ID)
public class SkyFarm {
    public static final String MOD_ID = "skyfarm";
    public static final Map<String, Integer> BEE_TYPES = Maps.newHashMap();
    public static final Logger LOGGER = LogManager.getLogger();

    public SkyFarm() {
        createFolderIfAbsent("./skyfarm");
        createFolderIfAbsent("./skyfarm/backups");

        getResourcefulBeeConfig();
        updateScripts();
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

    private static void updateScripts() {
        try {
            String scriptVer = null;
            File f = new File("./script_version");
            if (f.exists()) {
                Scanner localScanner = new Scanner(f);
                if (localScanner.hasNext()) scriptVer = localScanner.next();
            }
            URL url = new URL("https://raw.githubusercontent.com/North-West-Wind/SkyFarmEssential/main/scripts/script_version");
            Scanner s = new Scanner(url.openStream());
            if (!s.hasNext()) return;
            String ver = s.next();
            if (scriptVer == null || Utils.isVersionGreater(ver, scriptVer)) {
                FileWriter writer = new FileWriter("./script_version");
                writer.write(ver);
                writer.close();
                String name = Utils.downloadFile("https://raw.githubusercontent.com/North-West-Wind/SkyFarmEssential/main/scripts/scripts.zip", ".");
                Utils.unzip(name, ".");
                File file = new File(name);
                if (file.exists()) file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
