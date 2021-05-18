package ml.northwestwind.skyfarm.world.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class SkyblockData extends WorldSavedData {
    public static VotingStatus votingStatus = VotingStatus.NONE;
    public static List<UUID> voted = Lists.newArrayList();
    public static boolean isVoting, forced, shouldRestore;
    public static Thread thread;
    private boolean worldGenerated, isInLoop, usingParabox, noStage;
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
        SkyblockData data = world.getServer().overworld().getDataStorage().computeIfAbsent(SkyblockData::new, NAME);
        data.noStage = SkyblockChunkGenerator.hasNoStage(world);
        data.setDirty();
        return data;
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

    public static void startVoting(MinecraftServer server, VotingStatus votingStatus) {
        SkyblockData.votingStatus = votingStatus;
        isVoting = true;
        startTimeout(server);
    }

    public static void startTimeout(MinecraftServer server) {
        thread = new Thread(() -> {
            try {
                Thread.sleep(60000);
                if (isVoting) {
                    isVoting = false;
                    server.getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.vote.timeout").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, Util.NIL_UUID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void cancelVote(MinecraftServer server, String reason) {
        endVoting();
        server.getPlayerList().broadcastMessage(new TranslationTextComponent("parabox.vote."+reason).setStyle(Style.EMPTY.applyFormat(TextFormatting.RED)), ChatType.SYSTEM, Util.NIL_UUID);
    }

    public static void endVoting() {
        if (thread != null) thread.stop();
        voted.clear();
        votingStatus = VotingStatus.NONE;
        isVoting = false;
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
        noStage = nbt.getBoolean("noStage");
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
        nbt.putBoolean("noStage", noStage);
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

    public Iterable<String> getStages() {
        return noStage ? GameStageHelper.getKnownStages() : ImmutableList.copyOf(stages);
    }

    public enum VotingStatus {
        ACTIVATE(1),
        DEACTIVATE(2),
        NONE(0);

        final int id;
        VotingStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static VotingStatus getFromID(int id) {
            for (VotingStatus status : VotingStatus.values()) if (status.id == id) return status;
            return null;
        }
    }
}
