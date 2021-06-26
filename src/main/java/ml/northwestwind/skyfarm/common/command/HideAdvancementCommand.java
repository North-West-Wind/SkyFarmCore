package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.common.world.generators.SkyblockChunkGenerator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Set;
import java.util.stream.Collectors;

public class HideAdvancementCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher, boolean isIntegrated) {
        dispatcher.register(Commands.literal("hideadv").requires(src -> src.hasPermission(2) || isIntegrated)
                .then(Commands.argument("value", BoolArgumentType.bool()).executes(HideAdvancementCommand::hideAdvancement)));
    }

    private static int hideAdvancement(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        boolean value = BoolArgumentType.getBool(context, "value");
        SkyFarmConfig.setHideAdvancement(value);
        ServerPlayerEntity p = (ServerPlayerEntity) context.getSource().getEntity();
        ServerWorld world = p.getLevel();
        if (!SkyblockChunkGenerator.isWorldSkyblock(world)) return 0;
        Set<Advancement> advancements = world.getServer().getAdvancements().getAllAdvancements().stream().filter(adv ->
                !adv.getId().getNamespace().equals("skyfarm") && (adv.getDisplay() == null || !adv.getDisplay().isHidden()) && adv.getParent() == null
        ).collect(Collectors.toSet());
        PlayerAdvancements pAdv = p.getAdvancements();
        if (value) pAdv.visible.removeAll(advancements);
        else {
            advancements = world.getServer().getAdvancements().getAllAdvancements().stream().filter(adv -> {
                AdvancementProgress progress = pAdv.advancements.get(adv);
                boolean obtained = progress != null && progress.getPercent() == 1f;
                if (!obtained) {
                    Advancement parent = adv.getParent();
                    if (parent == null) return false;
                    AdvancementProgress progress1 = pAdv.advancements.get(parent);
                    return progress1 != null && progress1.getPercent() == 1f;
                }
                return true;
            }).collect(Collectors.toSet());
            pAdv.visible.addAll(advancements);
        }
        pAdv.visibilityChanged.addAll(advancements);
        p.sendMessage(
                new StringTextComponent("")
                        .append(new TranslationTextComponent("config.skyfarm.hide_advancement." + value).setStyle(Style.EMPTY.applyFormat(value ? TextFormatting.GREEN : TextFormatting.RED)))
                        .append(new TranslationTextComponent("config.skyfarm.hide_advancement")),
                Util.NIL_UUID);
        return 2;
    }
}
