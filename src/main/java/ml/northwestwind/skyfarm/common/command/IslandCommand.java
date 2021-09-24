package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.events.SkyblockEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class IslandCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("seekIsland").executes(IslandCommand::seekIsland));
        dispatcher.register(Commands.literal("createIsland").executes(IslandCommand::createIsland));
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("invite")
                        .then(Commands.argument("players", EntityArgument.players()).executes(IslandCommand::inviteToIsland))
                        .then(Commands.literal("accept").then(Commands.argument("accepted", BoolArgumentType.bool()).executes(IslandCommand::acceptInvite)))
                )
        );
    }

    private static int seekIsland(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        if (!SkyFarmConfig.ALLOW_SEEK_ISLAND.get()) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.disabledIsland").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (data.hasIsland(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.hasIsland").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        data.switchGameMode(player);
        data.setDirty();
        return 3;
    }

    private static int createIsland(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        if (!SkyFarmConfig.ALLOW_SEEK_ISLAND.get()) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.disabledIsland").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        SkyblockData data = SkyblockData.get(player.getLevel());
        if (data.hasIsland(player.getUUID())) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.hasIsland").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        BlockPos pos = player.blockPosition();
        if (pos.getY() > 255 || pos.getY() < 2) {
            player.sendMessage(new TranslationTextComponent("warning.skyfarm.invalidIsland").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        SkyblockEvents.generateIsland(player.getLevel(), pos);
        data.addIsland(player.getUUID(), pos);
        data.removeSpectator(player);
        data.setDirty();
        return 3;
    }

    private static int inviteToIsland(CommandContext<CommandSource> context) throws CommandSyntaxException {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        if (player.level.dimension() != World.OVERWORLD) {
            player.sendMessage(new TranslationTextComponent("invite.island.error.dimension").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(context, "players").stream().filter(p -> p.level.dimension() == World.OVERWORLD).collect(Collectors.toList());
        for (ServerPlayerEntity p : players)
            p.sendMessage(
                    new TranslationTextComponent("invite.island", player.getDisplayName())
                            .append(new TranslationTextComponent("invite.island.accept").setStyle(Style.EMPTY.applyFormat(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island invite accept true"))))
                            .append(new TranslationTextComponent("invite.island.deny").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island invite accept false"))))
                    , ChatType.SYSTEM, Util.NIL_UUID
            );
        player.sendMessage(new TranslationTextComponent("invite.island.send").withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.addInvites(player.getUUID(), players.stream().map(Entity::getUUID).collect(Collectors.toList()));
        return 3;
    }

    private static int acceptInvite(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        UUID uuid = data.findInvite(player.getUUID());
        if (uuid == null) {
            player.sendMessage(new TranslationTextComponent("invite.island.notFound").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        ServerPlayerEntity inviter = ((ServerPlayerEntity) player.getServer().overworld().getEntity(uuid));
        if (inviter == null) {
            player.sendMessage(new TranslationTextComponent("invite.island.notOverworld").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 2;
        }
        boolean accepted = BoolArgumentType.getBool(context, "accepted");
        if (!accepted) {
            player.sendMessage(new TranslationTextComponent("invite.island.denied.sender").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            inviter.sendMessage(new TranslationTextComponent("invite.island.denied").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
            return 3;
        }
        BlockPos pos = data.getIsland(uuid);
        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
        return 0;
    }
}
