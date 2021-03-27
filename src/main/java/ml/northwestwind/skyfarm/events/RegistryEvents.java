package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.world.SkyblockWorldType;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(SkyFarm.MOD_ID)
public class RegistryEvents {
    @SubscribeEvent
    public static void registerWorldType(final RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register(SkyblockWorldType.INSTANCE.setRegistryName("skyfarm"));
    }
}
