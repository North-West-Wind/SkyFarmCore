package ml.northwestwind.skyfarm.world.data;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.UUID;

public class SkyblockNetherData extends WorldSavedData {
    private static final String NAME = "skyfarm_nether";
    private final List<UUID> landed = Lists.newArrayList();

    public SkyblockNetherData() {
        super(NAME);
    }

    public static SkyblockNetherData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(SkyblockNetherData::new, NAME);
    }

    public boolean hasPlayerLanded(UUID uuid) {
        return landed.contains(uuid);
    }

    public void playerLanded(UUID uuid) {
        landed.add(uuid);
    }

    public void playerLeft(UUID uuid) {
        landed.remove(uuid);
    }

    @Override
    public void load(CompoundNBT nbt) {
        ListNBT listNBT = (ListNBT) nbt.get("landed");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                landed.add(compound.getUUID("uuid_"+i));
                i++;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT listNBT = new ListNBT();
        for (int i = 0; i < landed.size(); i++) {
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid_"+i, landed.get(i));
            listNBT.add(i, compound);
        }
        nbt.put("landed", listNBT);
        return nbt;
    }
}
