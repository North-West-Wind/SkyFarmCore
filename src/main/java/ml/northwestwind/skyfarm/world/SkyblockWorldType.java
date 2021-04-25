package ml.northwestwind.skyfarm.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import ml.northwestwind.skyfarm.SkyFarm;
import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import ml.northwestwind.skyfarm.world.generators.SkyblockNetherChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.*;
import net.minecraftforge.common.world.ForgeWorldType;

import java.util.Optional;
import java.util.OptionalLong;

public class SkyblockWorldType extends ForgeWorldType {
    public static final SkyblockWorldType INSTANCE = new SkyblockWorldType();
    public static final RegistryKey<DimensionSettings> NETHER_SETTINGS = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(SkyFarm.MOD_ID, "nether"));
    public static final DimensionType SKYFARM_NETHER = new DimensionType(OptionalLong.of(18000L), false, true, false, false, 8.0D, false, true, false, true, false, 128, FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_NETHER.getName(), DimensionType.NETHER_EFFECTS, 0.1F);

    private SkyblockWorldType() {
        super(new SkyblockChunkGeneratorFactory());
    }

    static {
        register(NETHER_SETTINGS, new DimensionSettings(
                new DimensionStructuresSettings(Optional.empty(), ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .put(Structure.NETHER_BRIDGE, new StructureSeparationSettings(15, 4, 30084232))
                        .put(Structure.BASTION_REMNANT, new StructureSeparationSettings(25, 4, 30084232))
                        .build()),
                new NoiseSettings(128,
                        new ScalingSettings(3, 2, 80, 10),
                        new SlideSettings(-3000, 64, -46),
                        new SlideSettings(-30, 7, 1),
                        1, 1, 0, 0, true, false, false, false),
                Blocks.NETHERRACK.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                -10, -10, 10, false
        ));
    }

    private static void register(RegistryKey<DimensionSettings> registry, DimensionSettings settings) {
        WorldGenRegistries.register(WorldGenRegistries.NOISE_GENERATOR_SETTINGS, registry.location(), settings);
    }

    @Override
    public String getTranslationKey() {
        return "generator.skyfarm";
    }

    public static class SkyblockChunkGeneratorFactory implements ForgeWorldType.IChunkGeneratorFactory {

        @Override
        public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> settings, long seed, String generatorSettings) {
            return new SkyblockChunkGenerator(new OverworldBiomeProvider(seed, false, false, biomeRegistry), seed,
                    () -> settings.getOrThrow(DimensionSettings.OVERWORLD));
        }

        @Override
        public DimensionGeneratorSettings createSettings(DynamicRegistries dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, String generatorSettings) {
            Registry<Biome> biomeRegistry = dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY);
            Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            Registry<DimensionSettings> dimensionSettingsRegistry = dynamicRegistries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
            return new DimensionGeneratorSettings(seed, generateStructures, bonusChest,
                    DimensionGeneratorSettings.withOverworld(dimensionTypeRegistry,
                            skyblockDimensions(dimensionTypeRegistry, biomeRegistry, dimensionSettingsRegistry, seed),
                            createChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed, generatorSettings)));
        }
    }

    public static SimpleRegistry<Dimension> skyblockDimensions(Registry<DimensionType> dimensionTypes, Registry<Biome> biomeRegistry, Registry<DimensionSettings> settings, long seed) {
        SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        simpleregistry.register(Dimension.NETHER, new Dimension(
                () -> SKYFARM_NETHER,
                new SkyblockNetherChunkGenerator(
                        NetherBiomeProvider.Preset.NETHER.biomeSource(biomeRegistry, seed),
                        seed,
                        () -> settings.getOrThrow(NETHER_SETTINGS))),
                Lifecycle.stable());
        simpleregistry.register(Dimension.END, new Dimension(
                () -> dimensionTypes.getOrThrow(DimensionType.END_LOCATION),
                DimensionType.defaultEndGenerator(biomeRegistry, settings, seed)),
                Lifecycle.stable());
        return simpleregistry;
    }
}