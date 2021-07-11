package ml.northwestwind.skyfarm.common.packet.message;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ml.northwestwind.skyfarm.common.packet.IPacket;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import ml.northwestwind.skyfarm.misc.Utils;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

public class CAddStagePacket implements IPacket {
    public static final Map<String, Triple<Item, Integer, List<String>>> STAGES = Maps.newHashMap();
    public static final List<String> EMPTY_STRING_LIST = Lists.newArrayList();
    private final String stage;
    public CAddStagePacket(String stage) {
        this.stage = stage;
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player == null || player.getServer() == null) return;
        if (!GameStageHelper.isStageKnown(stage)) return;
        for (ServerPlayerEntity p : player.getServer().getPlayerList().getPlayers()) GameStageHelper.addStage(p, stage);
        SkyblockData data = SkyblockData.get(player.getLevel());
        data.addStage(stage);
        data.setPoint(data.getPoint() - STAGES.getOrDefault(stage, new ImmutableTriple<>(null, 1, null)).getMiddle());
        data.setDirty();
        SSyncPointsPacket.serverSyncAll(player.getServer());
    }

    static {
        STAGES.put("mob_grinding_utils", new ImmutableTriple<>(Utils.getItemByModAndName("mob_grinding_utils", "fan"), 1, EMPTY_STRING_LIST));
        STAGES.put("botanypots", new ImmutableTriple<>(Utils.getItemByModAndName("botanypots", "botany_pot"), 1, EMPTY_STRING_LIST));
        STAGES.put("darkutils", new ImmutableTriple<>(Utils.getItemByModAndName("darkutils", "vector_plate"), 1, EMPTY_STRING_LIST));
        STAGES.put("prudentium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "prudentium_essence"), 1, EMPTY_STRING_LIST));
        STAGES.put("tertium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "tertium_essence"), 1, Lists.newArrayList("prudentium")));
        STAGES.put("imperium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "imperium_essence"), 1, Lists.newArrayList("tertium")));
        STAGES.put("supremium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagriculture", "supremium_essence"), 1, Lists.newArrayList("imperium")));
        STAGES.put("insanium", new ImmutableTriple<>(Utils.getItemByModAndName("mysticalagradditions", "insanium_essence"), 1, Lists.newArrayList("supremium")));
        STAGES.put("ironjetpacks", new ImmutableTriple<>(Utils.getItemByModAndName("ironjetpacks", "emerald_jetpack"), 2, EMPTY_STRING_LIST));
        STAGES.put("cgm", new ImmutableTriple<>(Utils.getItemByModAndName("cgm", "mini_gun"), 1, EMPTY_STRING_LIST));
        STAGES.put("vehicle", new ImmutableTriple<>(Utils.getItemByModAndName("vehicle", "standard_wheel"), 1, EMPTY_STRING_LIST));
        STAGES.put("sky_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "sky_slime_grass_seeds"), 1, EMPTY_STRING_LIST));
        STAGES.put("ender_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "ender_slime_grass_seeds"), 2, Lists.newArrayList("sky_slime_grass_seeds")));
        STAGES.put("blood_slime_grass_seeds", new ImmutableTriple<>(Utils.getItemByModAndName("tconstruct", "blood_slime_grass_seeds"), 3, Lists.newArrayList("ender_slime_grass_seeds")));
        STAGES.put("projecte", new ImmutableTriple<>(Utils.getItemByModAndName("projecte", "philosophers_stone"), 100, EMPTY_STRING_LIST));
        STAGES.put("simpleplanes", new ImmutableTriple<>(Utils.getItemByModAndName("simpleplanes", "plane"), 3, Lists.newArrayList("vehicle")));
        STAGES.put("mekasuit", new ImmutableTriple<>(Utils.getItemByModAndName("mekanism", "mekasuit_bodyarmor"), 5, EMPTY_STRING_LIST));
        STAGES.put("mekatool", new ImmutableTriple<>(Utils.getItemByModAndName("mekanism", "meka_tool"), 5, EMPTY_STRING_LIST));
        STAGES.put("illuminati_pet", new ImmutableTriple<>(Utils.getItemByModAndName("inventorypets", "pet_illuminati"), 4, EMPTY_STRING_LIST));
        STAGES.put("void_miner", new ImmutableTriple<>(Utils.getItemByModAndName("envirotech", "xerothium_void_miner_ccu"), 10, EMPTY_STRING_LIST));
    }
}
