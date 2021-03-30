package ml.northwestwind.skyfarm;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

@Mod(SkyFarm.MOD_ID)
public class SkyFarm {
    public static final String MOD_ID = "skyfarm";

    public static class SkyFarmItemGroup extends ItemGroup {
        public static final SkyFarmItemGroup INSTANCE = new SkyFarmItemGroup();

        public SkyFarmItemGroup() {
            super(MOD_ID);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryEvents.SkyFarmBlocks.NATURAL_EVAPORATOR);
        }
    }
}
