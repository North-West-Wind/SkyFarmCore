package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.container.ParaboxContainer;
import ml.northwestwind.skyfarm.misc.KeyBindings;
import ml.northwestwind.skyfarm.screen.ParaboxScreen;
import ml.northwestwind.skyfarm.tile.renderer.NaturalEvaporatorRenderer;
import ml.northwestwind.skyfarm.tile.renderer.ParaboxRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(RegistryEvents.TileEntityTypes.NATURAL_EVAPORATOR, NaturalEvaporatorRenderer::new);
        //ClientRegistry.bindTileEntityRenderer(RegistryEvents.TileEntityTypes.PARABOX, ParaboxRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.EntityTypes.COMPACT_BRICK, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        ScreenManager.register(RegistryEvents.ContainerTypes.PARABOX, ParaboxScreen::new);

        KeyBindings.register();
    }
}
