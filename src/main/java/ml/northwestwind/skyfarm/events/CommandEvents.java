package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.command.HideAdvancementCommand;
import ml.northwestwind.skyfarm.command.PointsCommand;
import ml.northwestwind.skyfarm.command.SkyboxCommand;
import ml.northwestwind.skyfarm.command.VoteCommand;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CommandEvents {
    @Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
    public static class Common {
        @SubscribeEvent
        public static void registerCommand(final RegisterCommandsEvent event) {
            VoteCommand.registerCommand(event.getDispatcher());
            PointsCommand.registerCommand(event.getDispatcher());
            HideAdvancementCommand.registerCommand(event.getDispatcher(), event.getEnvironment().equals(Commands.EnvironmentType.INTEGRATED));
        }
    }

    @Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void registerCommand(final RegisterCommandsEvent event) {
            SkyboxCommand.registerCommand(event.getDispatcher());
        }
    }
}
