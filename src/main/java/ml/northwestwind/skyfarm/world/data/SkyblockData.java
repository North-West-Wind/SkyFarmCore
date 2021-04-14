package ml.northwestwind.skyfarm.world.data;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.UUID;

public class SkyblockData extends WorldSavedData {
    private boolean worldGenerated, isInLoop;
    private final List<UUID> joined = Lists.newArrayList();
    private long points;
    private int paraboxLevel;
    private static final String NAME = "skyfarm";

    public SkyblockData() {
        super(NAME);
    }

    public static SkyblockData get(ServerWorld world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(SkyblockData::new, NAME);
    }

    @Override
    public void load(CompoundNBT nbt) {
        worldGenerated = nbt.getBoolean("generated");
        isInLoop = nbt.getBoolean("looping");
        points = nbt.getLong("points");
        paraboxLevel = nbt.getInt("parabox");
        ListNBT listNBT = (ListNBT) nbt.get("joined");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                joined.add(compound.getUUID("uuid"));
                i++;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putBoolean("generated", worldGenerated);
        nbt.putBoolean("looping", isInLoop);
        nbt.putInt("parabox", paraboxLevel);
        ListNBT listNBT = new ListNBT();
        for (int i = 0; i < joined.size(); i++) {
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", joined.get(i));
            listNBT.add(i, compound);
        }
        nbt.put("joined", listNBT);
        nbt.putLong("points", points);
        return nbt;
    }

    public boolean isWorldGenerated() {
        return worldGenerated;
    }

    public boolean isInLoop() {
        return isInLoop;
    }

    public boolean isFirstSpawn(UUID uuid) {
        return !joined.contains(uuid);
    }

    public void setWorldGenerated(boolean worldGenerated) {
        this.worldGenerated = worldGenerated;
    }

    public void setInLoop(boolean inLoop) {
        isInLoop = inLoop;
    }

    public void playerJoin(PlayerEntity player) {
        joined.add(player.getUUID());
    }

    public void addPoint(long point) {
        points += point;
    }

    public void setPoint(long point) {
        points = point;
    }

    public long getPoint() {
        return points;
    }

    public int getParaboxLevel() {
        return paraboxLevel;
    }

    public void setParaboxLevel(int paraboxLevel) {
        this.paraboxLevel = paraboxLevel;
    }
}
