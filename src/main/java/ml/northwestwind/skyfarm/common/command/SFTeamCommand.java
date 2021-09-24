package ml.northwestwind.skyfarm.common.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SFTeamCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sfteam")
                .then(Commands.literal("create").then(Commands.argument("name", StringArgumentType.string()).executes(SFTeamCommand::createTeam)))
                .then(Commands.literal("dismiss").executes(SFTeamCommand::dismissTeam).then(Commands.literal("true").executes(context -> dismissTeam(context, true))))
                .then(Commands.literal("join").then(Commands.argument("name", StringArgumentType.string()).executes(SFTeamCommand::joinTeam)))
                .then(Commands.literal("leave").executes(SFTeamCommand::leaveTeam))
                .then(Commands.literal("accept").then(Commands.argument("player", EntityArgument.player()).executes(SFTeamCommand::acceptPlayer)))
                .then(Commands.literal("deny").then(Commands.argument("player", EntityArgument.player()).executes(SFTeamCommand::denyPlayer)))
        );
    }

    private static int createTeam(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (data.isInTeam(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.hasTeam").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        String name = StringArgumentType.getString(context, "name");
        if (data.hasTeam(name)) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.teamExists").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        IStageData stageData = GameStageHelper.getPlayerData(player);
        data.createTeam(player.getUUID(), name, stageData == null ? Sets.newHashSet() : new HashSet<>(stageData.getStages()));
        data.setDirty();
        player.sendMessage(new TranslationTextComponent("info.skyfarm.teamCreated", new StringTextComponent(name).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        return 4;
    }

    private static int dismissTeam(CommandContext<CommandSource> context) {
        return dismissTeam(context, false);
    }

    private static int dismissTeam(CommandContext<CommandSource> context, boolean confirm) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (!data.isInTeam(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.noTeam").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        String team = data.getTeamFromCreator(player.getUUID());
        if (team == null) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.notCreator").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        if (!confirm) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.deleteTeam", new StringTextComponent(context.getInput() + " true").withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
            return 4;
        }
        Collection<UUID> uuids = data.removeTeam(team);
        data.setDirty();
        uuids.forEach(uuid -> {
            ServerPlayerEntity teamPlayer = player.getServer().getPlayerList().getPlayer(uuid);
            if (teamPlayer != null) teamPlayer.sendMessage(new TranslationTextComponent("info.skyfarm.teamDismissed", new StringTextComponent(team).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        });
        return 5;
    }

    private static int joinTeam(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (data.isInTeam(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.hasTeam").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        if (data.hasRequest(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.hasRequest").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        String name = StringArgumentType.getString(context, "name");
        if (!data.hasTeam(name)) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.teamNotExists").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 4;
        }
        data.requestTeam(player.getUUID(), name);
        data.setDirty();
        player.sendMessage(new TranslationTextComponent("info.skyfarm.requested", new StringTextComponent(name).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        return 5;
    }

    private static int leaveTeam(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (data.hasRequest(player.getUUID())) {
            String name = data.cancelRequest(player.getUUID());
            data.setDirty();
            player.sendMessage(new TranslationTextComponent("info.skyfarm.requestCancelled", new StringTextComponent(name).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        if (!data.isInTeam(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.noTeam").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        String team = data.getTeamFromCreator(player.getUUID());
        if (team != null) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.isCreator").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 4;
        }
        String name = data.leaveTeam(player.getUUID());
        data.setDirty();
        player.sendMessage(new TranslationTextComponent("info.skyfarm.teamLeft", new StringTextComponent(name).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        return 5;
    }

    private static int acceptPlayer(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        String team = data.getTeamFromCreator(player.getUUID());
        if (team == null) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.notCreator").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        try {
            ServerPlayerEntity requestPlayer = EntityArgument.getPlayer(context, "player");
            boolean success = data.acceptRequest(requestPlayer.getUUID(), team);
            data.setDirty();
            if (success) {
                player.sendMessage(new TranslationTextComponent("info.skyfarm.accepted", ((IFormattableTextComponent) requestPlayer.getDisplayName()).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
                requestPlayer.sendMessage(new TranslationTextComponent("info.skyfarm.teamJoined", new StringTextComponent(team).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
                return 6;
            } else {
                player.sendMessage(new TranslationTextComponent("warning.skyfarm.cannotAccept", ((IFormattableTextComponent) requestPlayer.getDisplayName()).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
                return 4;
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.cannotGetPlayer").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 5;
        }
    }

    private static int denyPlayer(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        String team = data.getTeamFromCreator(player.getUUID());
        if (team == null) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.notCreator").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        try {
            ServerPlayerEntity requestPlayer = EntityArgument.getPlayer(context, "player");
            boolean success = data.denyRequest(requestPlayer.getUUID(), team);
            data.setDirty();
            if (success) {
                player.sendMessage(new TranslationTextComponent("info.skyfarm.denied", ((IFormattableTextComponent) requestPlayer.getDisplayName()).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
                requestPlayer.sendMessage(new TranslationTextComponent("info.skyfarm.teamDenied", new StringTextComponent(team).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
                return 6;
            } else {
                player.sendMessage(new TranslationTextComponent("warning.skyfarm.cannotDeny", ((IFormattableTextComponent) requestPlayer.getDisplayName()).withStyle(TextFormatting.GOLD)).withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
                return 4;
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.cannotGetPlayer").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 5;
        }
    }
}
