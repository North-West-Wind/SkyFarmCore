package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.world.generators.SkyblockChunkGenerator;
import ml.northwestwind.skyfarm.common.world.generators.SkyblockNetherChunkGenerator;
import ml.northwestwind.skyfarm.misc.CuriosStuff;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {
    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        SkyblockChunkGenerator.init();
        SkyblockNetherChunkGenerator.init();
        SkyFarmPacketHandler.registerPackets();

        if (ModList.get().isLoaded("curios")) CuriosStuff.sendIMC();
    }
}
