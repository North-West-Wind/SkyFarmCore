package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// For compatibility
@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class TagEvents {
    private static boolean areTagsLoaded = false;

    @SubscribeEvent
    public static void tagsUpdated(final TagsUpdatedEvent event) {
        areTagsLoaded = true;
    }

    @SubscribeEvent
    public static void playerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        areTagsLoaded = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void getBurnFuelTime(final FurnaceFuelBurnTimeEvent event) {
        if (!areTagsLoaded) event.setCanceled(true);
    }
}
