package ml.northwestwind.skyfarm.world.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SkyblockData extends WorldSavedData {
    public static boolean isVoting, forced, shouldRestore;
    public static int votedFor;
    private boolean worldGenerated, isInLoop, usingParabox;
    private BlockPos paraboxPos = BlockPos.ZERO;
    private final List<Triple<UUID, List<NonNullList<ItemStack>>, Pair<Vector3d, Pair<Float, Float>>>> playerData = Lists.newArrayList();
    private final List<UUID> joined = Lists.newArrayList();
    private List<String> stages = Lists.newArrayList();
    private static final Random rng = new Random();
    private long points;
    private int paraboxLevel, originalParaboxLevel;
    private static Item wantingItem;
    private static final String NAME = "skyfarm";

    public SkyblockData() {
        super(NAME);
    }

    public static SkyblockData get(ServerWorld world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(SkyblockData::new, NAME);
    }

    public static void generateItem() {
        ImmutableList<Item> items = ImmutableList.copyOf(ForgeRegistries.ITEMS.getValues());
        wantingItem = items.get(rng.nextInt(items.size()));
    }

    public static Item getWantingItem() {
        return wantingItem;
    }

    @Override
    public void load(CompoundNBT nbt) {
        worldGenerated = nbt.getBoolean("generated");
        isInLoop = nbt.getBoolean("looping");
        points = nbt.getLong("points");
        paraboxLevel = nbt.getInt("parabox");
        originalParaboxLevel = paraboxLevel;
        String id = nbt.getString("wantingItem");
        if (!id.equals("")) wantingItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        else generateItem();
        CompoundNBT pos = nbt.getCompound("paraboxPos");
        paraboxPos = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
        ListNBT listNBT = (ListNBT) nbt.get("joined");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                joined.add(compound.getUUID("uuid"));
                i++;
            }
        }
        listNBT = (ListNBT) nbt.get("stages");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                stages.add(compound.getString("name"));
                i++;
            }
        }
        stages = stages.stream().filter(GameStageHelper::isStageKnown).collect(Collectors.toList());
        listNBT = (ListNBT) nbt.get("playerdata");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                CompoundNBT p = compound.getCompound("pos");
                Vector3d position = new Vector3d(p.getDouble("x"), p.getDouble("y"), p.getDouble("z"));
                NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
                NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);
                NonNullList<ItemStack> offhand = NonNullList.withSize(1, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(compound.getCompound("items"), items);
                ItemStackHelper.loadAllItems(compound.getCompound("armor"), armor);
                ItemStackHelper.loadAllItems(compound.getCompound("offhand"), offhand);
                CompoundNBT r = compound.getCompound("rot");
                playerData.add(new MutableTriple<>(compound.getUUID("uuid"), Lists.newArrayList(items, armor, offhand), Pair.of(position, Pair.of(r.getFloat("x"), r.getFloat("y")))));
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
        if (wantingItem != null) nbt.putString("wantingItem", wantingItem.getRegistryName().toString());
        CompoundNBT pos = new CompoundNBT();
        pos.putInt("x", paraboxPos.getX());
        pos.putInt("y", paraboxPos.getY());
        pos.putInt("z", paraboxPos.getZ());
        nbt.put("paraboxPos", pos);
        ListNBT listNBT = new ListNBT();
        for (int i = 0; i < joined.size(); i++) {
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", joined.get(i));
            listNBT.add(i, compound);
        }
        nbt.put("joined", listNBT);
        nbt.putLong("points", points);
        ListNBT listNBT1 = new ListNBT();
        for (int i = 0; i < playerData.size(); i++) {
            Triple<UUID, List<NonNullList<ItemStack>>, Pair<Vector3d, Pair<Float, Float>>> data = playerData.get(i);
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", data.getLeft());
            CompoundNBT items = new CompoundNBT();
            CompoundNBT armor = new CompoundNBT();
            CompoundNBT offhand = new CompoundNBT();
            ItemStackHelper.saveAllItems(items, data.getMiddle().get(0));
            ItemStackHelper.saveAllItems(armor, data.getMiddle().get(1));
            ItemStackHelper.saveAllItems(offhand, data.getMiddle().get(2));
            compound.put("items", items);
            compound.put("armor", armor);
            compound.put("offhand", offhand);
            CompoundNBT p = new CompoundNBT();
            p.putDouble("x", data.getRight().getLeft().x());
            p.putDouble("y", data.getRight().getLeft().y());
            p.putDouble("z", data.getRight().getLeft().z());
            compound.put("pos", p);
            CompoundNBT r = new CompoundNBT();
            r.putFloat("x", data.getRight().getRight().getLeft());
            r.putFloat("y", data.getRight().getRight().getRight());
            compound.put("rot", r);
            listNBT1.add(i, compound);
        }
        nbt.put("playerdata", listNBT1);
        ListNBT listNBT2 = new ListNBT();
        for (int i = 0; i < stages.size(); i++) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString("name", stages.get(i));
            listNBT2.add(i, compound);
        }
        nbt.put("stages", listNBT2);
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

    public boolean isUsingParabox() {
        return usingParabox;
    }

    public void setUsingParabox(boolean usingParabox) {
        this.usingParabox = usingParabox;
    }

    public boolean isUsing(BlockPos pos) {
        return pos.equals(paraboxPos);
    }

    public void setParaboxPos(BlockPos paraboxPos) {
        this.paraboxPos = paraboxPos;
    }

    public BlockPos getParaboxPos() {
        return paraboxPos;
    }

    public boolean hasPlayerData(UUID uuid) {
        return playerData.stream().anyMatch(triple -> triple.getLeft().equals(uuid));
    }

    public void setupPlayerData(PlayerEntity player) {
        Optional<Triple<UUID, List<NonNullList<ItemStack>>, Pair<Vector3d, Pair<Float, Float>>>> opTriple = playerData.stream().filter(tri -> tri.getLeft().equals(player.getUUID())).findAny();
        if (!opTriple.isPresent()) return;
        player.inventory.items = opTriple.get().getMiddle().get(0);
        player.inventory.armor = opTriple.get().getMiddle().get(1);
        player.inventory.offhand = opTriple.get().getMiddle().get(2);
        player.inventory.compartments = opTriple.get().getMiddle();
        Vector3d pos = opTriple.get().getRight().getLeft();
        player.teleportTo(pos.x, pos.y, pos.z);
        player.xRot = opTriple.get().getRight().getRight().getLeft();
        player.yRot = opTriple.get().getRight().getRight().getRight();
        playerData.remove(opTriple.get());
        setDirty();
    }

    public void addPlayerData(PlayerEntity player) {
        Triple<UUID, List<NonNullList<ItemStack>>, Pair<Vector3d, Pair<Float, Float>>> triple = new MutableTriple<>(player.getUUID(), player.inventory.compartments, Pair.of(player.position(), Pair.of(player.xRot, player.yRot)));
        playerData.add(triple);
    }

    public void addStage(String stage) {
        if (GameStageHelper.isStageKnown(stage)) stages.add(stage);
    }

    public ImmutableList<String> getStages() {
        return ImmutableList.copyOf(stages);
    }

    public int getOriginalParaboxLevel() {
        return originalParaboxLevel;
    }
}
