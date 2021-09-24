package ml.northwestwind.skyfarm.common.world.data;

import com.google.common.collect.*;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import ml.northwestwind.skyfarm.itemstages.ItemStages;
import ml.northwestwind.skyfarm.misc.Utils;
import ml.northwestwind.skyfarm.misc.teleporter.SimpleTeleporter;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.stream.Collectors;

public class SkyblockData extends WorldSavedData {
    public static VotingStatus votingStatus = VotingStatus.NONE;
    public static Set<UUID> voted = Sets.newHashSet();
    public static boolean isVoting, forced, shouldRestore;
    public static Thread thread;
    private boolean worldGenerated, isInLoop, usingParabox, noStage;
    private BlockPos paraboxPos = BlockPos.ZERO;
    private final Set<UUID> joined = Sets.newHashSet();
    private final Map<UUID, BlockPos> islands = Maps.newHashMap();
    private final Map<UUID, Pair<Collection<UUID>, Integer>> invites = Maps.newHashMap();
    private Set<String> stages = Sets.newHashSet();
    private final Map<UUID, Triple<Vector3d, RegistryKey<World>, GameType>> spectators = Maps.newHashMap();
    private final Map<String, MutableTriple<UUID, Set<String>, Long>> teams = Maps.newHashMap();
    private final Map<UUID, String> playerTeamMap = Maps.newHashMap();
    private final Map<UUID, String> teamRequests = Maps.newHashMap();
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
        data.setDirty();
        return data;
    }

    public static Item generateItem(ServerWorld world) {
        ImmutableList<Item> items = ImmutableList.copyOf(ForgeRegistries.ITEMS.getValues());
        wantingItem = items.get(rng.nextInt(items.size()));
        RecipeManager manager = world.getRecipeManager();
        Collection<IRecipe<?>> recipes = manager.getRecipes();
        SkyblockData data = SkyblockData.get(world);
        boolean craftable = false;
        for (IRecipe<?> recipe : recipes) if (recipe.getResultItem().getItem().equals(wantingItem)) {
            final String stage = ItemStages.getStage(recipe.getResultItem());
            if (craftable = stage == null || (SkyFarmConfig.GLOBAL_STAGE.get() && data.hasStage(stage))) break;
        }
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
        noStage = nbt.getBoolean("noStage");
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
        stages = stages.stream().filter(GameStageHelper::isStageKnown).collect(Collectors.toSet());
        listNBT = (ListNBT) nbt.get("hasIsland");
        if (listNBT != null) {
            ListNBT islandList = (ListNBT) nbt.get("hasIsland");
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                CompoundNBT islandNBT = islandList.getCompound(i);
                islandNBT.putUUID("uuid", compound.getUUID("uuid"));
                islandList.set(i, islandNBT);
                i++;
            }
        }
        listNBT = (ListNBT) nbt.get("spectators");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                CompoundNBT posNBT = compound.getCompound("pos");
                Vector3d vec = new Vector3d(posNBT.getDouble("x"), posNBT.getDouble("y"), posNBT.getDouble("z"));
                spectators.put(compound.getUUID("uuid"), new ImmutableTriple<>(vec, RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("dimension"))), GameType.byId(compound.getInt("gamemode"))));
                i++;
            }
        }
        listNBT = (ListNBT) nbt.get("islands");
        if (listNBT != null) {
            int i = 0;
            while (!listNBT.getCompound(i).isEmpty()) {
                CompoundNBT compound = listNBT.getCompound(i);
                islands.put(compound.getUUID("uuid"), new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z")));
                i++;
            }
        }
        CompoundNBT teamsNbt = nbt.getCompound("teams");
        for (String key : teamsNbt.getAllKeys()) {
            CompoundNBT teamNbt = teamsNbt.getCompound(key);
            ListNBT stageList = (ListNBT) nbt.get("stages");
            Set<String> stageSet = Sets.newHashSet();
            if (stageList != null) {
                int i = 0;
                while (!stageList.getCompound(i).isEmpty()) {
                    CompoundNBT compound = stageList.getCompound(i);
                    stageSet.add(compound.getString("name"));
                    i++;
                }
            }
            MutableTriple<UUID, Set<String>, Long> triple = new MutableTriple<>(teamNbt.getUUID("creator"), stageSet, teamNbt.getLong("points"));
            teams.put(key, triple);
            ListNBT playerList = (ListNBT) nbt.get("players");
            if (playerList != null) {
                int i = 0;
                while (!playerList.getCompound(i).isEmpty()) {
                    CompoundNBT compound = playerList.getCompound(i);
                    playerTeamMap.put(compound.getUUID("uuid"), key);
                    i++;
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putBoolean("generated", worldGenerated);
        nbt.putBoolean("looping", isInLoop);
        nbt.putBoolean("noStage", noStage);
        nbt.putInt("parabox", paraboxLevel);
        if (wantingItem != null) nbt.putString("wantingItem", wantingItem.getRegistryName().toString());
        CompoundNBT pos = new CompoundNBT();
        pos.putInt("x", paraboxPos.getX());
        pos.putInt("y", paraboxPos.getY());
        pos.putInt("z", paraboxPos.getZ());
        nbt.put("paraboxPos", pos);
        ListNBT listNBT = new ListNBT();
        Iterator<UUID> it = joined.iterator();
        int i = 0;
        while (it.hasNext()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", it.next());
            listNBT.add(i++, compound);
        }
        nbt.put("joined", listNBT);
        nbt.putLong("points", points);
        ListNBT listNBT2 = new ListNBT();
        Iterator<String> it2 = stages.iterator();
        i = 0;
        while (it2.hasNext()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString("name", it2.next());
            listNBT2.add(i++, compound);
        }
        nbt.put("stages", listNBT2);
        ListNBT listNBT4 = new ListNBT();
        Iterator<Map.Entry<UUID, Triple<Vector3d, RegistryKey<World>, GameType>>> it4 = spectators.entrySet().iterator();
        i = 0;
        while (it4.hasNext()) {
            Map.Entry<UUID, Triple<Vector3d, RegistryKey<World>, GameType>> entry = it4.next();
            CompoundNBT compound = new CompoundNBT();
            compound.putUUID("uuid", entry.getKey());
            CompoundNBT posNBT = new CompoundNBT();
            posNBT.putDouble("x", entry.getValue().getLeft().x);
            posNBT.putDouble("y", entry.getValue().getLeft().y);
            posNBT.putDouble("z", entry.getValue().getLeft().z);
            compound.put("pos", posNBT);
            compound.putString("dimension", entry.getValue().getMiddle().location().toString());
            compound.putInt("gamemode", entry.getValue().getRight().getId());
            listNBT4.add(i++, compound);
        }
        nbt.put("spectators", listNBT4);
        ListNBT listNBT5 = new ListNBT();
        Iterator<Map.Entry<UUID, BlockPos>> it5 = islands.entrySet().iterator();
        i = 0;
        while (it5.hasNext()) {
            Map.Entry<UUID, BlockPos> entry = it5.next();
            BlockPos blockPos = entry.getValue();
            CompoundNBT compound = new CompoundNBT();
            compound.putInt("x", blockPos.getX());
            compound.putInt("y", blockPos.getY());
            compound.putInt("z", blockPos.getZ());
            compound.putUUID("uuid", entry.getKey());
            listNBT5.add(i++, compound);
        }
        nbt.put("islands", listNBT5);
        CompoundNBT teamsNbt = new CompoundNBT();
        teams.forEach((key, value) -> {
            CompoundNBT compoundNBT = new CompoundNBT();
            Iterator<String> stages = value.getMiddle().iterator();
            ListNBT stageList = new ListNBT();
            ListNBT playerList = new ListNBT();
            int ii = 0;
            while (stages.hasNext()) {
                CompoundNBT nbt1 = new CompoundNBT();
                nbt1.putString("name", stages.next());
                stageList.add(ii++, nbt1);
            }
            Iterator<UUID> players = playerTeamMap.entrySet().stream().filter(en -> en.getValue().equals(key)).map(Map.Entry::getKey).collect(Collectors.toSet()).iterator();
            ii = 0;
            while (players.hasNext()) {
                CompoundNBT nbt1 = new CompoundNBT();
                nbt1.putUUID("uuid", players.next());
                playerList.add(ii++, nbt1);
            }
            compoundNBT.putUUID("creator", value.getLeft());
            compoundNBT.putLong("points", value.getRight());
            compoundNBT.put("stages", stageList);
            compoundNBT.put("players", playerList);
            teamsNbt.put(key, compoundNBT);
        });
        nbt.put("teams", teamsNbt);
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

    public void addGlobalPoint(long point) {
        points += point;
        teams.keySet().forEach(key -> addTeamPoint(key, point));
    }

    public void setGlobalPoint(long point) {
        points = point;
        teams.keySet().forEach(key -> setTeamPoint(key, point));
    }

    public long getGlobalPoint() {
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

    public void addStages(String... stages) {
        this.stages.addAll(Arrays.asList(stages));
    }

    public void removeStages(String... stages) {
        Arrays.asList(stages).forEach(this.stages::remove);
    }

    public Iterable<String> getGlobalStages() {
        return noStage ? GameStageHelper.getKnownStages() : ImmutableSet.copyOf(stages);
    }

    public Iterable<String> getStages(String name) {
        return noStage ? GameStageHelper.getKnownStages() : ImmutableSet.copyOf(teams.getOrDefault(name, new MutableTriple<>(null, Sets.newHashSet(), null)).getMiddle());
    }

    public boolean hasStage(String stage) {
        return noStage || (GameStageHelper.isStageKnown(stage) && stages.contains(stage));
    }

    public void noStage() {
        noStage = true;
    }

    public boolean hasIsland(UUID uuid) {
        return islands.containsKey(uuid);
    }

    public void addIsland(UUID uuid, BlockPos pos) {
        islands.put(uuid, pos);
    }

    public BlockPos getIsland(UUID uuid) {
        return islands.getOrDefault(uuid, BlockPos.ZERO);
    }

    public BlockPos getRandomIsland() {
        if (islands.isEmpty()) return new BlockPos(0, 64, 0);
        return Utils.getRandomValueFromMap(islands);
    }

    public boolean isPosIsland(BlockPos pos) {
        return islands.containsValue(pos);
    }

    public BlockPos findPosForNewIsland(int offset) {
        BlockPos pos = getRandomIsland();
        BlockPos pos1 = pos.offset(offset, 0, 0);
        if (!isPosIsland(pos1)) return pos1;
        pos1 = pos.offset(0, 0, offset);
        if (!isPosIsland(pos1)) return pos1;
        pos1 = pos.offset(-offset, 0, 0);
        if (!isPosIsland(pos1)) return pos1;
        pos1 = pos.offset(0, 0, -offset);
        if (!isPosIsland(pos1)) return pos1;
        return findPosForNewIsland(offset);
    }

    public boolean isSpectator(UUID uuid) {
        return spectators.containsKey(uuid);
    }

    public void switchGameMode(ServerPlayerEntity player) {
        if (isSpectator(player.getUUID())) {
            Triple<Vector3d, RegistryKey<World>, GameType> triple = spectators.get(player.getUUID());
            player.setGameMode(triple.getRight());
            if (!player.level.dimension().equals(triple.getMiddle())) {
                ServerWorld dimension = player.getServer().getLevel(triple.getMiddle());
                if (dimension != null) player.changeDimension(dimension, new SimpleTeleporter());
            }
            Vector3d pos = triple.getLeft();
            player.teleportTo(pos.x, pos.y, pos.z);
            spectators.remove(player.getUUID());
        } else {
            spectators.put(player.getUUID(), new ImmutableTriple<>(player.position(), player.level.dimension(), player.gameMode.getGameModeForPlayer()));
            player.setGameMode(GameType.SPECTATOR);
        }
    }

    public void removeSpectator(ServerPlayerEntity player) {
        if (!isSpectator(player.getUUID())) return;
        switchGameMode(player);
    }
    
    public void addInvites(UUID inviter, Collection<UUID> invited) {
        invites.put(inviter, Pair.of(invited, 600));
    }

    public UUID findInvite(UUID uuid) {
        Optional<Map.Entry<UUID, Pair<Collection<UUID>, Integer>>> e = invites.entrySet().stream().filter(entry -> entry.getValue().getLeft().contains(uuid)).findFirst();
        return e.map(Map.Entry::getKey).orElse(null);
    }

    public void tick() {
        if (!invites.isEmpty()) {
            for (Map.Entry<UUID, Pair<Collection<UUID>, Integer>> entry : invites.entrySet()) {
                int ticks = entry.getValue().getRight() - 1;
                if (ticks <= 0) invites.remove(entry.getKey());
            }
        }
    }

    public boolean hasTeam(String name) {
        return teams.containsKey(name);
    }

    public boolean isInTeam(UUID uuid) {
        return playerTeamMap.containsKey(uuid);
    }

    public String getTeam(UUID uuid) {
        return playerTeamMap.get(uuid);
    }

    public void createTeam(UUID uuid, String name, Set<String> stages) {
        teams.put(name, new MutableTriple<>(uuid, stages, 0L));
        playerTeamMap.put(uuid, name);
    }

    public Collection<UUID> removeTeam(String name) {
        teams.remove(name);
        Collection<UUID> collection = playerTeamMap.entrySet().stream().filter(entry -> entry.getValue().equals(name)).map(Map.Entry::getKey).collect(Collectors.toSet());
        collection.forEach(playerTeamMap::remove);
        return collection;
    }

    public boolean hasRequest(UUID uuid) {
        return teamRequests.containsKey(uuid);
    }

    public void requestTeam(UUID uuid, String name) {
        teamRequests.put(uuid, name);
    }

    public String cancelRequest(UUID uuid) {
        String name = teamRequests.get(uuid);
        teamRequests.remove(uuid);
        return name;
    }

    public String leaveTeam(UUID uuid) {
        String name = playerTeamMap.get(uuid);
        playerTeamMap.remove(uuid);
        return name;
    }

    public String getTeamFromCreator(UUID uuid) {
        return teams.entrySet().stream().filter(entry -> entry.getValue().getLeft().equals(uuid)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public boolean acceptRequest(UUID uuid, String name) {
        if (!teamRequests.containsKey(uuid) || !name.equals(teamRequests.get(uuid))) return false;
        teamRequests.remove(uuid);
        playerTeamMap.put(uuid, name);
        return true;
    }

    public boolean denyRequest(UUID uuid, String name) {
        if (!teamRequests.containsKey(uuid) || !name.equals(teamRequests.get(uuid))) return false;
        teamRequests.remove(uuid);
        return true;
    }

    public void addTeamStages(String name, String... stages) {
        if (!teams.containsKey(name)) return;
        MutableTriple<UUID, Set<String>, Long> triple = teams.get(name);
        Set<String> known = triple.getMiddle();
        known.addAll(Arrays.asList(stages));
        triple.setMiddle(known);
        teams.put(name, triple);
    }

    public void removeTeamStages(String name, String... stages) {
        if (!teams.containsKey(name)) return;
        MutableTriple<UUID, Set<String>, Long> triple = teams.get(name);
        Set<String> known = triple.getMiddle();
        Arrays.asList(stages).forEach(known::remove);
        triple.setMiddle(known);
        teams.put(name, triple);
    }

    public void addTeamPoint(String name, long point) {
        if (!teams.containsKey(name)) return;
        MutableTriple<UUID, Set<String>, Long> triple = teams.get(name);
        triple.setRight(triple.getRight() + point);
        teams.put(name, triple);
    }

    public void setTeamPoint(String name, long point) {
        if (!teams.containsKey(name)) return;
        MutableTriple<UUID, Set<String>, Long> triple = teams.get(name);
        triple.setRight(point);
        teams.put(name, triple);
    }

    public long getTeamPoint(String name) {
        if (!teams.containsKey(name)) return 0;
        return teams.get(name).getRight();
    }

    public Collection<UUID> getTeamPlayers(String name) {
        if (!teams.containsKey(name)) return null;
        return playerTeamMap.entrySet().stream().filter(entry -> name.equals(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
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
