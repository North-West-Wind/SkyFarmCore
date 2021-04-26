package ml.northwestwind.skyfarm.world;

import ml.northwestwind.skyfarm.world.generators.SkyblockChunkGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

public class NoStageSkyblockWorldType extends SkyblockWorldType {
    public static final NoStageSkyblockWorldType INSTANCE = new NoStageSkyblockWorldType();

    public NoStageSkyblockWorldType() {
        super(new NoStageSkyblockChunkGeneratorFactory());
    }

    @Override
    public String getTranslationKey() {
        return "generator.skyfarm_nostage";
    }

    public static class NoStageSkyblockChunkGeneratorFactory extends SkyblockChunkGeneratorFactory {
        @Override
        public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> settings, long seed, String generatorSettings) {
            return new SkyblockChunkGenerator(new OverworldBiomeProvider(seed, false, false, biomeRegistry), seed,
                    () -> settings.getOrThrow(DimensionSettings.OVERWORLD), true);
        }
    }
}
