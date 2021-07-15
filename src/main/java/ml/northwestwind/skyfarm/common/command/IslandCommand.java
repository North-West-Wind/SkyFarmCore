package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.events.SkyblockEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class IslandCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("seekIsland").executes(IslandCommand::seekIsland));
        dispatcher.register(Commands.literal("createIsland").executes(IslandCommand::createIsland));
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
}
