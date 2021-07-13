package ml.northwestwind.skyfarm.misc;

import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import ml.northwestwind.skyfarm.events.SkyblockEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosStuff {
    public static void sendIMC() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("shifter")
                        .icon(new ResourceLocation(SkyFarm.MOD_ID, "gui/shifters"))
                        .size(1)
                        .build());
    }

    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        LazyOptional<IItemHandlerModifiable> curios = CuriosApi.getCuriosHelper().getEquippedCurios(event.player);
        int slot = CuriosApi.getSlotHelper().getSlotsForType(event.player, SkyFarm.MOD_ID + ":shifter");
        PlayerEntity player = event.player;
        curios.ifPresent(handler -> {
            ItemStack boots = handler.getStackInSlot(slot);
            if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_VOID_SHIFTER_NETHER))
                SkyblockEvents.handleWorldWarp(World.OVERWORLD, World.NETHER, 8, player);
            else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_SKY_SHIFTER_END))
                SkyblockEvents.handleWorldWarp(World.END, World.OVERWORLD, 1, player);
            else if (player.getY() <= -64) player.teleportTo(player.getX(), 316, player.getZ());
            else if (player.getY() >= 320) player.teleportTo(player.getX(), -60, player.getZ());

            if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_UG) && ModList.get().isLoaded("undergarden"))
                SkyblockEvents.speedyWorldWarp(World.OVERWORLD, SkyblockEvents.UNDERGARDEN, player);
            else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_TF) && ModList.get().isLoaded("twilightforest"))
                SkyblockEvents.speedyWorldWarp(SkyblockEvents.TWILIGHT_FOREST, World.OVERWORLD, player);
            else if (boots.getItem().equals(RegistryEvents.Items.OVERWORLD_AXIS_SHIFTER_LC) && ModList.get().isLoaded("lostcities"))
                SkyblockEvents.speedyWorldWarp(SkyblockEvents.LOST_CITIES, World.OVERWORLD, player);
        });
    }
}
