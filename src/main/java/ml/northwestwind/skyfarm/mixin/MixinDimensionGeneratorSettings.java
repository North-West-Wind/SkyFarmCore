package ml.northwestwind.skyfarm.mixin;

import ml.northwestwind.skyfarm.common.world.SeedHolder;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

// Thank you Undergarden
@Mixin(DimensionGeneratorSettings.class)
public class MixinDimensionGeneratorSettings {
    @Inject(at = @At("RETURN"), method = "<init>(JZZLnet/minecraft/util/registry/SimpleRegistry;Ljava/util/Optional;)V")
    private void storeSeed(long seed, boolean generateFeatures, boolean bonusChest, SimpleRegistry<Dimension> options, Optional<String> legacyOptions, CallbackInfo ci) {
        SeedHolder.setSeed(seed);
    }
}
