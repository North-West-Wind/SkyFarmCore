package ml.northwestwind.skyfarm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.packet.message.CVoteActivateParaboxPacket;
import ml.northwestwind.skyfarm.packet.message.CVoteDeactivateParaboxPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class VoteCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("vote").then(
                Commands.literal("paraboxActivate")
                .then(Commands.argument("yesNo", BoolArgumentType.bool()))
                .executes(VoteCommand::voteParaboxActivate)
        ).then(
                Commands.literal("paraboxDeactivate")
                .then(Commands.argument("yesNo", BoolArgumentType.bool()))
                .executes(VoteCommand::voteParaboxDeactivate)
        ));
    }

    private static int voteParaboxActivate(CommandContext<CommandSource> context) {
        boolean voteFor = BoolArgumentType.getBool(context, "yesNo");
        SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(voteFor));
        return 1;
    }

    private static int voteParaboxDeactivate(CommandContext<CommandSource> context) {
        boolean voteFor = BoolArgumentType.getBool(context, "yesNo");
        SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(voteFor));
        return 1;
    }
}
