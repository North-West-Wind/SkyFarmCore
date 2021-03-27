package ml.northwestwind.skyfarm.world;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.UUID;

public class SkyblockData extends WorldSavedData {
    private boolean worldGenerated;
    private final List<UUID> joined = Lists.newArrayList();
    private static final String NAME = "skyfarm";

    public SkyblockData() {
        super(NAME);
    }

    public static SkyblockData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(SkyblockData::new, NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        worldGenerated = nbt.getBoolean("generated");
        ListNBT listNBT = (ListNBT) nbt.get("joined");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                joined.add(compound.getUUID("uuid_"+i));
                i++;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putBoolean("generated", worldGenerated);
        ListNBT listNBT = new ListNBT();
        for (int i = 0; i < joined.size(); i++) {
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid_"+i, joined.get(i));
            listNBT.add(i, compound);
        }
        nbt.put("joined", listNBT);
        return nbt;
    }

    public boolean isWorldGenerated() {
        return worldGenerated;
    }

    public boolean isFirstSpawn(UUID uuid) {
        return !joined.contains(uuid);
    }

    public void setWorldGenerated(boolean worldGenerated) {
        this.worldGenerated = worldGenerated;
    }

    public void playerJoin(PlayerEntity player) {
        joined.add(player.getUUID());
    }
}
