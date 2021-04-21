package ml.northwestwind.skyfarm.world.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SkyblockData extends WorldSavedData {
    public static boolean isVoting, forced, shouldRestore;
    public static int votedFor;
    private boolean worldGenerated, isInLoop, usingParabox;
    private BlockPos paraboxPos = BlockPos.ZERO;
    private final List<UUID> joined = Lists.newArrayList();
    private List<String> stages = Lists.newArrayList();
    private static final Random rng = new Random();
    private long points;
    private int paraboxLevel;
    private static Item wantingItem = Items.AIR;
    private static final String NAME = "skyfarm";

    public SkyblockData() {
        super(NAME);
    }

    public static SkyblockData get(ServerWorld world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(SkyblockData::new, NAME);
    }

    public static Item generateItem(ServerWorld world) {
        ImmutableList<Item> items = ImmutableList.copyOf(ForgeRegistries.ITEMS.getValues());
        wantingItem = items.get(rng.nextInt(items.size()));
        RecipeManager manager = world.getRecipeManager();
        Collection<IRecipe<?>> recipes = manager.getRecipes();
        boolean craftable = false;
        for (IRecipe<?> recipe : recipes) if (recipe.getResultItem().getItem().equals(wantingItem)) {
            craftable = true;
            break;
        }
        SkyblockData.get(world).setDirty();
        return craftable ? wantingItem : generateItem(world);
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
        String id = nbt.getString("wantingItem");
        if (!id.equals("")) wantingItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
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

    public void addStage(String stage) {
        if (GameStageHelper.isStageKnown(stage)) stages.add(stage);
    }

    public ImmutableList<String> getStages() {
        return ImmutableList.copyOf(stages);
    }
}
