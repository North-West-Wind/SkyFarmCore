package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

public class PointsCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("points").requires(
                commandSource -> commandSource.hasPermission(2)
        ).then(
                Commands.literal("add")
                        .then(Commands.argument("point", IntegerArgumentType.integer()).executes(PointsCommand::addPoint)
                                .then(Commands.argument("team", StringArgumentType.string()).executes(context -> addPoint(context, StringArgumentType.getString(context, "team")))))
        ).then(
                Commands.literal("set")
                        .then(Commands.argument("point", IntegerArgumentType.integer()).executes(PointsCommand::setPoint)
                                .then(Commands.argument("team", StringArgumentType.string()).executes(context -> setPoint(context, StringArgumentType.getString(context, "team")))))
        ));
    }

    private static int addPoint(CommandContext<CommandSource> context) {
        return addPoint(context, null);
    }

    private static int addPoint(CommandContext<CommandSource> context, String team) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        int point = IntegerArgumentType.getInteger(context, "point");
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        boolean globalStage = SkyFarmConfig.GLOBAL_STAGE.get();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (!globalStage && team == null) team = data.getTeam(player.getUUID());
        if (!globalStage && team != null && data.hasTeam(team)) {
            data.addTeamPoint(team, point);
            player.getServer().getPlayerList().getPlayers().stream().filter(p -> team.equals(data.getTeam(p.getUUID())))
                    .forEach(p -> p.sendMessage(new TranslationTextComponent("points.gain", point)
                                    .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                            ChatType.SYSTEM, Util.NIL_UUID));
        } else {
            data.addGlobalPoint(point);
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("points.gain", point)
                            .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                    ChatType.SYSTEM, Util.NIL_UUID);
        }
        data.setDirty();
        return 2;
    }

    private static int setPoint(CommandContext<CommandSource> context) {
        return setPoint(context, null);
    }

    private static int setPoint(CommandContext<CommandSource> context, String team) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        int point = IntegerArgumentType.getInteger(context, "point");
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        boolean globalStage = SkyFarmConfig.GLOBAL_STAGE.get();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (!globalStage && team == null) team = data.getTeam(player.getUUID());
        if (!globalStage && team != null && data.hasTeam(team)) {
            data.setTeamPoint(team, point);
            player.getServer().getPlayerList().getPlayers().stream().filter(p -> team.equals(data.getTeam(p.getUUID())))
                    .forEach(p -> p.sendMessage(new TranslationTextComponent("points.set", point)
                                    .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                            ChatType.SYSTEM, Util.NIL_UUID));
        } else {
            data.setGlobalPoint(point);
            player.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("points.set", point)
                            .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                    ChatType.SYSTEM, Util.NIL_UUID);
        }
        data.setGlobalPoint(point);
        data.setDirty();
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastMessage(new TranslationTextComponent("points.set", point)
                        .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                ChatType.SYSTEM, Util.NIL_UUID);
        return 2;
    }
}
