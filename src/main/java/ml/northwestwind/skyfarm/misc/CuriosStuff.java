package ml.northwestwind.skyfarm.misc;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.common.registries.item.ShifterItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CuriosStuff {
    public static void sendIMC() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("shifter")
                        .icon(new ResourceLocation(SkyFarm.MOD_ID, "gui/shifters"))
                        .size(1)
                        .build());
    }

    public static ItemStack playerTick(final TickEvent.PlayerTickEvent event, ItemStack original) {
        AtomicReference<ItemStack> shifter = new AtomicReference<>(original);
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosHelper().getCuriosHandler(event.player);
        optional.ifPresent(itemHandler -> {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler("shifter");
            stacksOptional.ifPresent(stacksHandler -> {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(0);
                if (stack.getItem() instanceof ShifterItem) shifter.set(stack);
            });
        });
        return shifter.get();
    }
}
