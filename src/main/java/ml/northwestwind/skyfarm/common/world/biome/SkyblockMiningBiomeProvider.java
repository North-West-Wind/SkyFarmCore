package ml.northwestwind.skyfarm.common.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ml.northwestwind.skyfarm.common.world.SeedHolder;
import ml.northwestwind.skyfarm.misc.Utils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

import java.util.List;
import java.util.stream.Collectors;

public class SkyblockMiningBiomeProvider extends BiomeProvider {
    public static final Codec<SkyblockMiningBiomeProvider> CODEC = RecordCodecBuilder.create((p_235302_0_) -> {
        return p_235302_0_.group(Codec.LONG.fieldOf("seed").orElseGet(SeedHolder::getSeed).forGetter((p_235304_0_) -> {
            return p_235304_0_.seed;
        }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", Boolean.FALSE, Lifecycle.stable()).forGetter((p_235303_0_) -> {
            return p_235303_0_.legacyBiomeInitLayer;
        }), Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((p_235301_0_) -> {
            return p_235301_0_.largeBiomes;
        }), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((p_242637_0_) -> {
            return p_242637_0_.biomes;
        })).apply(p_235302_0_, p_235302_0_.stable(SkyblockMiningBiomeProvider::new));
    });
    private static final List<RegistryKey<Biome>> POSSIBLE_BIOMES = ImmutableList.of(RegistryKey.create(Registry.BIOME_REGISTRY, Utils.prefix("asteroids")));
    private final long seed;
    private final boolean legacyBiomeInitLayer;
    private final boolean largeBiomes;
    private final Registry<Biome> biomes;

    protected SkyblockMiningBiomeProvider(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes, Registry<Biome> biomes) {
        super(POSSIBLE_BIOMES.stream().map(biomes::getOrThrow).collect(Collectors.toList()));
        this.seed = seed;
        this.legacyBiomeInitLayer = legacyBiomeInitLayer;
        this.largeBiomes = largeBiomes;
        this.biomes = biomes;
    }

    public static void init() {
        Registry.register(Registry.BIOME_SOURCE, Utils.prefix("mining"), CODEC);
    }

    @Override
    protected Codec<? extends BiomeProvider> codec() {
        return CODEC;
    }

    @Override
    public BiomeProvider withSeed(long seed) {
        return new SkyblockMiningBiomeProvider(seed, legacyBiomeInitLayer, largeBiomes, biomes);
    }

    @Override
    public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
        return this.biomes.getOrThrow(POSSIBLE_BIOMES.get(0));
    }
}
