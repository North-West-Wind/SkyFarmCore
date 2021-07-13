package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.client.screen.ParaboxScreen;
import ml.northwestwind.skyfarm.common.registries.tile.renderer.NaturalEvaporatorRenderer;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.misc.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        SkyFarmConfig.loadClientConfig(FMLPaths.CONFIGDIR.get().resolve("skyfarm-client.toml").toString());
        ClientRegistry.bindTileEntityRenderer(RegistryEvents.TileEntityTypes.NATURAL_EVAPORATOR, NaturalEvaporatorRenderer::new);
        //ClientRegistry.bindTileEntityRenderer(RegistryEvents.TileEntityTypes.PARABOX, ParaboxRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(RegistryEvents.EntityTypes.COMPACT_BRICK, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        ScreenManager.register(RegistryEvents.ContainerTypes.PARABOX, ParaboxScreen::new);

        KeyBindings.register();
        RenderTypeLookup.setRenderLayer(RegistryEvents.Blocks.PARABOX, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(RegistryEvents.Blocks.VOID_GENERATOR, RenderType.cutoutMipped());
    }

    @SubscribeEvent
    public static void itemColors(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, tint) -> {
            if (tint == 0) return -1;
            if (!stack.hasTag()) return -1;
            String type = stack.getTag().getString("Type");
            if (!SkyFarm.BEE_TYPES.containsKey(type)) return -1;
            return SkyFarm.BEE_TYPES.get(type);
        }, RegistryEvents.Items.MUTATION_POLLEN);
    }

    @SubscribeEvent
    public static void preTextureStitch(final TextureStitchEvent.Pre event) {
        event.addSprite(new ResourceLocation(SkyFarm.MOD_ID, "gui/shifters"));
    }
}
