package ml.northwestwind.skyfarm.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Optional;

public class DebugCommand {
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher, boolean isIntegrated) {
        dispatcher.register(Commands.literal("logRecipe").requires(src -> src.hasPermission(2) || isIntegrated)
                .then(Commands.argument("id", StringArgumentType.string()).executes(DebugCommand::logRecipe)));
    }

    private static int logRecipe(CommandContext<CommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) return 1;
        ServerPlayerEntity p = (ServerPlayerEntity) context.getSource().getEntity();
        Optional<? extends IRecipe<?>> opt = context.getSource().getServer().getRecipeManager().byKey(new ResourceLocation(StringArgumentType.getString(context, "id")));
        if (!opt.isPresent()) p.sendMessage(new StringTextComponent("No recipe found"), ChatType.SYSTEM, Util.NIL_UUID);
        else {
            p.sendMessage(new StringTextComponent("Debugging Infusion Recipe"), ChatType.SYSTEM, Util.NIL_UUID);
            List<Ingredient> ingredients = opt.get().getIngredients();
            for (int ii = 0; ii < ingredients.size(); ii++) {
                Ingredient ingredient = ingredients.get(ii);
                p.sendMessage(new StringTextComponent(String.format("ItemStack in %d: %s", ii, ingredient.getItems()[0].toString())), ChatType.SYSTEM, Util.NIL_UUID);
            }
        }
        return 2;
    }
}
