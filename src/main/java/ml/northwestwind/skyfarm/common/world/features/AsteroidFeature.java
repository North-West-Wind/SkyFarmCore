package ml.northwestwind.skyfarm.common.world.features;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.config.SkyFarmAsteroidsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AsteroidFeature extends Feature<NoFeatureConfig> {
    private static final Map<Block, Integer> WEIGHTS = Maps.newHashMap();

    public AsteroidFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
        setRegistryName(SkyFarm.MOD_ID, "asteroid");
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        int radius = rand.nextInt(5) + 3;
        List<Map.Entry<Block, Integer>> entries = new ArrayList<>(WEIGHTS.entrySet());
        double totalWeight = WEIGHTS.values().stream().mapToDouble(v -> v).sum();
        for (BlockPos targetPos : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
            if (targetPos.distSqr(pos) > radius) continue;
            Block block = Blocks.STONE;
            if (rand.nextInt(4) == 0) {
                int idx = 0;
                for (double r = Math.random() * totalWeight; idx < entries.size() - 1; ++idx) {
                    r -= entries.get(idx).getValue();
                    if (r <= 0.0) break;
                }
                block = entries.get(idx).getKey();
            }
            world.setBlock(targetPos, block.defaultBlockState(), 3);
        }
        return true;
    }

    public static void initWeights() {
        Map<ResourceLocation, Integer> tags = Maps.newHashMap();
        Map<ResourceLocation, Integer> blocks = Maps.newHashMap();
        int defaultWeight = SkyFarmAsteroidsConfig.getDefaultWeight();

        SkyFarmAsteroidsConfig.getWeights().forEach((rl, in) ->{
            if (BlockTags.getAllTags().getTag(rl) == null) blocks.put(rl, in);
            else tags.put(rl, in);
        });
        BlockTags.getAllTags().getTagOrEmpty(new ResourceLocation("forge", "block/ores")).getValues()
            .forEach(block -> {
                ResourceLocation rl = block.getRegistryName();
                WEIGHTS.put(block, blocks.getOrDefault(rl, tags.getOrDefault(rl, defaultWeight)));
            });

    }
}
