package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class RenderEvents {
    @OnlyIn(Dist.CLIENT)
    //@SubscribeEvent
    public static void renderFog(final EntityViewRenderEvent.FogColors event) {
        Minecraft client = Minecraft.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;
        World world = player.getCommandSenderWorld();
        Biome biome = world.getBiome(player.blockPosition());
        Color color = new Color(biome.getSkyColor());
        LogManager.getLogger().info(String.format("r: %f, g: %f, b: %f", event.getRed(), event.getGreen(), event.getBlue()));
        event.setRed(color.getRed() / 256f);
        event.setBlue(color.getBlue() / 256f);
        event.setGreen(color.getGreen() / 256f);
    }

    @SubscribeEvent
    public static void renderGameOverlay(final RenderGameOverlayEvent event) {
        if (!event.getType().equals(RenderGameOverlayEvent.ElementType.AIR)) return;
        event.setCanceled(true);
    }
}
