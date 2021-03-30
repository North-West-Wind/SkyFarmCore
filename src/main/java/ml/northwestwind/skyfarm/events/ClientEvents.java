package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.discord.Discord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class ClientEvents {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void playerJoin(final EntityJoinWorldEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if (player == null) return;
        Entity entity = event.getEntity();
        if (!entity.getUUID().equals(player.getUUID())) return;
        if (minecraft.getConnection() == null) Discord.updateRichPresence("Singleplayer", ModList.get().getMods().size() + " Mods Loaded", new Discord.DiscordImage("sky_farm", "Farming in the Sky"), new Discord.DiscordImage("singleplayer", ""));
        else Discord.updateRichPresence("Multiplayer", ModList.get().getMods().size() + " Mods Loaded", new Discord.DiscordImage("sky_farm", "Farming in the Sky"), new Discord.DiscordImage("multiplayer", "But with friends"));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void menuOpened(final GuiOpenEvent event) {
        Screen screen = event.getGui();
        if (screen == null) return;
        if (screen instanceof MainMenuScreen || screen instanceof MultiplayerScreen) {
            Discord.updateRichPresence("Main Menu", ModList.get().getMods().size() + " Mods Loaded", new Discord.DiscordImage("sky_farm", ""), null);
        }
    }
}
