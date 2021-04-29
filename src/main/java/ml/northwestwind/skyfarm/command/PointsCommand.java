package ml.northwestwind.skyfarm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.world.data.SkyblockData;
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
                .then(Commands.argument("point", IntegerArgumentType.integer()))
                .executes(PointsCommand::addPoint)
        ).then(
                Commands.literal("set")
                .then(Commands.argument("point", IntegerArgumentType.integer()))
                .executes(PointsCommand::setPoint)
        ));
    }

    private static int addPoint(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        int point = IntegerArgumentType.getInteger(context, "point");
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.addPoint(point);
        data.setDirty();
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastMessage(new TranslationTextComponent("points.gain", point, data.getPoint())
                .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                ChatType.SYSTEM, Util.NIL_UUID);
        return point;
    }

    private static int setPoint(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 0;
        int point = IntegerArgumentType.getInteger(context, "point");
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.setPoint(point);
        data.setDirty();
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastMessage(new TranslationTextComponent("points.set", point)
                .setStyle(Style.EMPTY.applyFormat(TextFormatting.GOLD)),
                ChatType.SYSTEM, Util.NIL_UUID);
        return 1;
    }
}
