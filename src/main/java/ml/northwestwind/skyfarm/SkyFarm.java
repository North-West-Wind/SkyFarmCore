package ml.northwestwind.skyfarm;

import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;

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
            return new ItemStack(RegistryEvents.Blocks.NATURAL_EVAPORATOR);
        }
    }
}
