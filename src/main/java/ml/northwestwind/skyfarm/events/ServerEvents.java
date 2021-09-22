package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.config.SkyFarmAsteroidsConfig;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.misc.backup.Backups;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void serverStarted(final FMLServerStartedEvent event) {
        Backups.INSTANCE.init();

        SkyFarmConfig.loadServerConfig(FMLPaths.CONFIGDIR.get().resolve("skyfarm-server.toml").toString());
        SkyFarmAsteroidsConfig.readConfig();
    }

    @SubscribeEvent
    public static void serverStopped(final FMLServerStoppedEvent event) {
        if (SkyblockData.shouldRestore) {
            Backups.INSTANCE.restore(event.getServer());
            SkyblockData.shouldRestore = false;
        }
    }

    @SubscribeEvent
    public static void gameStageAdded(final GameStageEvent.Added event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;
        MinecraftServer server = player.getServer();
        if (server == null) return;
        SkyblockData.get(server.overworld()).addStage(event.getStageName());
    }

    @SubscribeEvent
    public static void gameStageRemoved(final GameStageEvent.Removed event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide) return;
        MinecraftServer server = player.getServer();
        if (server == null) return;
        SkyblockData.get(server.overworld()).removeStage(event.getStageName());
    }

    @SubscribeEvent
    public static void serverTick(final TickEvent.ServerTickEvent event) {
        SkyblockData data = SkyblockData.get(ServerLifecycleHooks.getCurrentServer().overworld());
        data.tick();
    }
}
