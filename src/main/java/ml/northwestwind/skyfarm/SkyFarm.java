package ml.northwestwind.skyfarm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Mod(SkyFarm.MOD_ID)
public class SkyFarm {
    public static final String MOD_ID = "skyfarm";

    static {
        createFolderIfAbsent("./skyfarm");
        createFolderIfAbsent("./skyfarm/backups");
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
}
