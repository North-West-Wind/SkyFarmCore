package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// For compatibility
@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class WorldEvents {
    private static boolean isWorldLoaded;

    @SubscribeEvent
    public static void worldLoad(final WorldEvent.Load event) {
        isWorldLoaded = true;
    }

    @SubscribeEvent
    public static void worldUnload(final WorldEvent.Unload event) {
        isWorldLoaded = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void getBurnFuelTime(final FurnaceFuelBurnTimeEvent event) {
        if (!isWorldLoaded) event.setCanceled(true);
    }
}
