package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.misc.Utils;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void serverStarted(final FMLServerStartedEvent event) {
        Backups.INSTANCE.init();
    }

    @SubscribeEvent
    public static void serverStopped(final FMLServerStoppedEvent event) {
        if (SkyblockData.shouldRestore) {
            Backups.INSTANCE.restore(event.getServer());
            SkyblockData.shouldRestore = false;
        }
        try {
            Utils.overrideServerConfig(event.getServer());
        } catch (IOException ignored) { }
    }
}
