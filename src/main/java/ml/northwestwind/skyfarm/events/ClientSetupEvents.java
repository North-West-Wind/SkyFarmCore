package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.discord.Discord;
import ml.northwestwind.skyfarm.tile.renderer.NaturalEvaporatorRenderer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(RegistryEvents.SkyFarmTileEntityTypes.NATURAL_EVAPORATOR, NaturalEvaporatorRenderer::new);
        Discord.startup();
        Discord.updateRichPresence("Starting game...", "Mods are loading...", new Discord.DiscordImage("sky_farm", ""), null);
    }
}
