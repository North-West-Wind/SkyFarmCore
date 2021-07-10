package ml.northwestwind.skyfarm.common.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class StageCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher, boolean integrated) {
        dispatcher.register(Commands.literal("disableStages").requires(source -> integrated || source.hasPermission(2))
                .executes(StageCommand::warnDisableStages).then(
                        Commands.literal("true").executes(StageCommand::disableStages)
        ));
    }

    private static int warnDisableStages(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        player.sendMessage(new TranslationTextComponent("disable_stages.warning")
                .append(new TranslationTextComponent("disable_stages.warning.l2", new StringTextComponent("/disableStages true").withStyle(TextFormatting.RED))),
                ChatType.SYSTEM, Util.NIL_UUID);
        return 2;
    }

    private static int disableStages(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.noStage();
        Iterable<String> stages = GameStageHelper.getKnownStages();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            stages.forEach(stage -> GameStageHelper.addStage(p, stage));
            p.sendMessage(new TranslationTextComponent("disable_stages.done").withStyle(TextFormatting.GREEN), ChatType.SYSTEM, Util.NIL_UUID);
        });
        data.addStage(ImmutableList.copyOf(stages).toArray(new String[0]));
        data.setDirty();
        return 2;
    }
}
