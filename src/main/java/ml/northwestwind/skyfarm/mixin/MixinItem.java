package ml.northwestwind.skyfarm.mixin;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ml.northwestwind.skyfarm.itemstages.ItemStages.getStage;
import static ml.northwestwind.skyfarm.itemstages.ItemStages.sendDropMessage;

@Mixin(Item.class)
public class MixinItem {
    @Inject(at = @At("HEAD"), method = "inventoryTick", cancellable = true)
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.isCreative()) return;
        final String stage = getStage(stack);
        if ((stage != null && !GameStageHelper.hasStage(player, stage))) {
            player.inventory.setItem(slot, ItemStack.EMPTY);
            player.drop(stack, false);
            sendDropMessage(player);
        }
    }
}
