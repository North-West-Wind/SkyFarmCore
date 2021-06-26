package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void serverStarted(final FMLServerStartedEvent event) {
        Backups.INSTANCE.init();

        SkyFarmConfig.loadServerConfig(FMLPaths.CONFIGDIR.get().resolve("skyfarm-server.toml").toString());
    }

    @SubscribeEvent
    public static void serverStopped(final FMLServerStoppedEvent event) {
        if (SkyblockData.shouldRestore) {
            Backups.INSTANCE.restore(event.getServer());
            SkyblockData.shouldRestore = false;
        }
    }
}
