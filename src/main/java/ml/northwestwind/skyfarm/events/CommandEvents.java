package ml.northwestwind.skyfarm.events;

import com.mojang.brigadier.CommandDispatcher;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.command.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyFarm.MOD_ID)
public class CommandEvents {
    @SubscribeEvent
    public static void registerCommand(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        boolean integrated = event.getEnvironment().equals(Commands.EnvironmentType.INTEGRATED);
        VoteCommand.registerCommand(dispatcher);
        PointsCommand.registerCommand(dispatcher);
        StageCommand.registerCommand(dispatcher, integrated);
        IslandCommand.registerCommand(dispatcher);
        SFTeamCommand.registerCommand(dispatcher);
        DebugCommand.registerCommand(dispatcher, integrated);
    }
}
