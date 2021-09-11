package ml.northwestwind.skyfarm.common.world.data;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SkyblockNetherData extends WorldSavedData {
    private static final TextFormatting[] formattings = { TextFormatting.RED, TextFormatting.GOLD, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.DARK_GREEN };
    private static final String NAME = "skyfarm_nether";
    private final Map<UUID, Integer> shielded = Maps.newHashMap();

    public SkyblockNetherData() {
        super(NAME);
    }

    public static SkyblockNetherData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(SkyblockNetherData::new, NAME);
    }

    public void playerEntered(UUID uuid) {
        shielded.put(uuid, 300);
        this.setDirty();
    }

    public void minusTick(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (isPlayerShielded(uuid)) {
            int ticks = shielded.get(uuid) - 1;
            if (ticks <= 0) {
                player.sendMessage(new TranslationTextComponent("warning.shield.disabled").withStyle(TextFormatting.RED), ChatType.SYSTEM, Util.NIL_UUID);
                shielded.remove(uuid);
            } else {
                if (ticks <= 100 && ticks % 20 == 0) player.sendMessage(new TranslationTextComponent("warning.shield.timeout", new StringTextComponent(Integer.toString(ticks / 20)).withStyle(formattings[(ticks / 20) - 1])), ChatType.SYSTEM, Util.NIL_UUID);
                shielded.put(uuid, ticks);
            }
            this.setDirty();
        }
    }

    public boolean isPlayerShielded(UUID uuid) {
        return shielded.containsKey(uuid);
    }

    @Override
    public void load(CompoundNBT nbt) {
        ListNBT listNBT = (ListNBT) nbt.get("shielded");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                int ticks = compound.getInt("ticks");
                if (ticks > 0) shielded.put(compound.getUUID("uuid"), ticks);
                i++;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT listNBT = new ListNBT();
        Iterator<Map.Entry<UUID, Integer>> it = shielded.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            if (entry.getValue() <= 0) continue;
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", entry.getKey());
            compound.putInt("ticks", entry.getValue());
            listNBT.add(i, compound);
            i++;
        }
        nbt.put("shielded", listNBT);
        return nbt;
    }
}
