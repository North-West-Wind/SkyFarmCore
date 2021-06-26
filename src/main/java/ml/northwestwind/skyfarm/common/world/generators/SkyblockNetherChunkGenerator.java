package ml.northwestwind.skyfarm.common.world.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcp.MethodsReturnNonnullByDefault;
import ml.northwestwind.skyfarm.SkyFarm;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.settings.NoiseSettings;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkyblockNetherChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<SkyblockNetherChunkGenerator> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter((gen) -> gen.biomeSource),
                    Codec.LONG.fieldOf("seed").stable().forGetter((gen) -> gen.seed),
                    DimensionSettings.CODEC.fieldOf("settings").forGetter((gen) -> gen.settings)
            ).apply(instance, instance.stable(SkyblockNetherChunkGenerator::new)));

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

    @Override
    protected void fillNoiseColumn(double[] p_222548_1_, int x, int z) {
        NoiseSettings noisesettings = this.settings.get().noiseSettings();
        double d0;
        double d1;
        if (islandNoise == null) {
            super.fillNoiseColumn(p_222548_1_, x, z);
            return;
        }
        d0 = getHeightValue(this.islandNoise, x, z) - 8.0F;
        if (d0 > 0.0D) {
            d1 = 0.25D;
        } else {
            d1 = 1.0D;
        }

        double d12 = 684.412D * noisesettings.noiseSamplingSettings().xzScale();
        double d13 = 684.412D * noisesettings.noiseSamplingSettings().yScale();
        double d14 = d12 / noisesettings.noiseSamplingSettings().xzFactor();
        double d15 = d13 / noisesettings.noiseSamplingSettings().yFactor();
        double d17 = noisesettings.topSlideSettings().target();
        double d19 = noisesettings.topSlideSettings().size();
        double d20 = noisesettings.topSlideSettings().offset();
        double d21 = noisesettings.bottomSlideSettings().target();
        double d2 = noisesettings.bottomSlideSettings().size();
        double d3 = noisesettings.bottomSlideSettings().offset();
        double d4 = noisesettings.randomDensityOffset() ? this.getRandomDensity(x, z) : 0.0D;
        double d5 = noisesettings.densityFactor();
        double d6 = noisesettings.densityOffset();

        for (int i1 = 0; i1 <= this.chunkCountY; ++i1) {
            double d7 = this.sampleAndClampNoise(x, i1, z, d12, d13, d14, d15);
            double d8 = 1.0D - (double) i1 * 2.0D / (double) this.chunkCountY + d4;
            double d9 = d8 * d5 + d6;
            double d10 = (d9 + d0) * d1;
            if (d10 > 0.0D) {
                d7 = d7 + d10 * 4.0D;
            } else {
                d7 = d7 + d10;
            }

            if (d19 > 0.0D) {
                double d11 = ((double) (this.chunkCountY - i1) - d20) / d19;
                d7 = MathHelper.clampedLerp(d17, d7, d11);
            }

            if (d2 > 0.0D) {
                double d22 = ((double) i1 - d3) / d2;
                d7 = MathHelper.clampedLerp(d21, d7, d22);
            }

            p_222548_1_[i1] = d7;
        }
    }

    public static float getHeightValue(SimplexNoiseGenerator p_235317_0_, int x, int z) {
        int i = x / 2;
        int j = z / 2;
        int k = x % 2;
        int l = z % 2;
        float f = 100.0F - MathHelper.sqrt((float)(x * x + z * z)) * 4.0F;
        f = MathHelper.clamp(f, -100.0F, 80.0F);

        for(int i1 = -12; i1 <= 12; ++i1) {
            for(int j1 = -12; j1 <= 12; ++j1) {
                long k1 = i + i1;
                long l1 = j + j1;
                if (k1 * k1 + l1 * l1 > 4096L && p_235317_0_.getValue((double)k1, (double)l1) < (double)-0.9F) {
                    float f1 = (MathHelper.abs((float)k1) * 3439.0F + MathHelper.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
                    float f2 = (float)(k - i1 * 2);
                    float f3 = (float)(l - j1 * 2);
                    float f4 = 100.0F - MathHelper.sqrt(f2 * f2 + f3 * f3) * f1;
                    f4 = MathHelper.clamp(f4, -100.0F, 80.0F);
                    f = Math.max(f, f4);
                }
            }
        }
        return f;
    }

    @Override
    public void applyCarvers(long p_230350_1_, BiomeManager p_230350_3_, IChunk p_230350_4_, GenerationStage.Carving p_230350_5_) { }
}
