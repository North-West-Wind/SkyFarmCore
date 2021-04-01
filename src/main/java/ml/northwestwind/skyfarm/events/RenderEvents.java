package ml.northwestwind.skyfarm.events;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.world.SkyblockChunkGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, value = Dist.CLIENT)
public class RenderEvents {
    //@SubscribeEvent
    public static void renderFogColors(final EntityViewRenderEvent.FogColors event) {
        Minecraft client = Minecraft.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;
        World world = player.getCommandSenderWorld();
        Biome biome = world.getBiome(player.blockPosition());
        Color color = new Color(biome.getSkyColor());
        event.setRed(color.getRed() / 256f);
        event.setBlue(color.getBlue() / 256f);
        event.setGreen(color.getGreen() / 256f);
    }

    //@SubscribeEvent
    public static void renderFogDensity(final EntityViewRenderEvent.FogDensity event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if (player == null) return;
        FluidState fluid = event.getInfo().getFluidInCamera();
        if (!fluid.isEmpty()) return;
        event.setDensity(0.001f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        event.setCanceled(true);
    }
}
