package ml.northwestwind.skyfarm.common.world.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Supplier;

// Thank you Botania
public class SkyblockChunkGenerator extends ChunkGenerator {
    public static final Codec<SkyblockChunkGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter((gen) -> gen.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter((gen) -> gen.seed),
                    DimensionSettings.CODEC.fieldOf("settings").forGetter((gen) -> gen.settings)
            ).apply(instance, instance.stable(SkyblockChunkGenerator::new)));

    public static void init() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(SkyFarm.MOD_ID, "skyfarm"), CODEC);
    }

    private final long seed;
    private final Supplier<DimensionSettings> settings;
    private final boolean noStage;

    public SkyblockChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settings, boolean noStage) {
        super(provider, provider, settings.get().structureSettings(), seed);
        this.seed = seed;
        this.settings = settings;
        this.noStage = noStage;
    }

    public SkyblockChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settings) {
        this(provider, seed, settings, false);
    }

    public static boolean isWorldSkyblock(ServerWorld world) {
        return world.getServer().overworld().getChunkSource().getGenerator() instanceof SkyblockChunkGenerator;
    }

    public static boolean hasNoStage(ServerWorld world) {
        return isWorldSkyblock(world) && ((SkyblockChunkGenerator) world.getServer().overworld().getChunkSource().getGenerator()).noStage;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long newSeed) {
        return new SkyblockChunkGenerator(this.biomeSource.withSeed(newSeed), newSeed, settings, noStage);
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {}

    @Override
    public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {}

    @Override
    public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_) {}

    @Override
    public void applyBiomeDecoration(WorldGenRegion p_230351_1_, StructureManager p_230351_2_) {}

    @Override
    public int getBaseHeight(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
        return 0;
    }

    @Override
    public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
        return new Blockreader(new BlockState[0]);
    }
}