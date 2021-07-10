package ml.northwestwind.skyfarm.events;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.command.HideAdvancementCommand;
import ml.northwestwind.skyfarm.common.command.PointsCommand;
import ml.northwestwind.skyfarm.common.command.StageCommand;
import ml.northwestwind.skyfarm.common.command.VoteCommand;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class CommandEvents {
    @SubscribeEvent
    public static void registerCommand(final RegisterCommandsEvent event) {
        VoteCommand.registerCommand(event.getDispatcher());
        PointsCommand.registerCommand(event.getDispatcher());
        HideAdvancementCommand.registerCommand(event.getDispatcher(), event.getEnvironment().equals(Commands.EnvironmentType.INTEGRATED));
        StageCommand.registerCommand(event.getDispatcher(), event.getEnvironment().equals(Commands.EnvironmentType.INTEGRATED));
    }
}
