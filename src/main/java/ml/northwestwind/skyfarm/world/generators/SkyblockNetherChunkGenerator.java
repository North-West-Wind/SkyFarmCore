package ml.northwestwind.skyfarm.world.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcp.MethodsReturnNonnullByDefault;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkyblockNetherChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<SkyblockNetherChunkGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(
                        BiomeProvider.CODEC.fieldOf("biome_source").forGetter((gen) -> {
                            return gen.biomeSource;
                        }),
                        Codec.LONG.fieldOf("seed").stable().forGetter((gen) -> {
                            return gen.seed;
                        }),
                        DimensionSettings.CODEC.fieldOf("settings").forGetter((gen) -> {
                            return gen.settings;
                        })
                ).apply(instance, instance.stable((provider, seed, settings) -> {
                    return new SkyblockNetherChunkGenerator(provider, seed, settings);
                }));
            });

    public static void init() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(SkyFarm.MOD_ID, "skyfarm_nether"), CODEC);
    }

    public SkyblockNetherChunkGenerator(BiomeProvider provider, long seed, Supplier<DimensionSettings> settingsSupplier) {
        super(provider, seed, settingsSupplier);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new SkyblockNetherChunkGenerator(biomeSource.withSeed(seed), seed, settings);
    }
}
