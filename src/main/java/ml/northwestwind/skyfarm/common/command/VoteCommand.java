package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.packet.message.SPleaseSendParaboxPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class VoteCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("vote").then(
                Commands.literal("paraboxActivate")
                .then(Commands.argument("yesNo", BoolArgumentType.bool()).executes(VoteCommand::voteParaboxActivate))
        ).then(
                Commands.literal("paraboxDeactivate")
                .then(Commands.argument("yesNo", BoolArgumentType.bool()).executes(VoteCommand::voteParaboxDeactivate))
        ));
    }

    private static int voteParaboxActivate(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        if (player.getServer() == null) return 0;
        boolean voteFor = BoolArgumentType.getBool(context, "yesNo");
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPleaseSendParaboxPacket(true, voteFor));
        return 2;
    }

    private static int voteParaboxDeactivate(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        if (player.getServer() == null) return 0;
        boolean voteFor = BoolArgumentType.getBool(context, "yesNo");
        SkyFarmPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SPleaseSendParaboxPacket(false, voteFor));
        return 2;
    }
}
