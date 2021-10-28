package ml.northwestwind.skyfarm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.events.RegistryEvents;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow private ClientWorld level;

    @Inject(at = @At("HEAD"), method = "renderClouds", cancellable = true)
    public void renderClouds(MatrixStack matrix, float partialTicks, double p_228425_3_, double p_228425_5_, double p_228425_7_, CallbackInfo ci) {
        if (this.level.dimension().equals(RegistryEvents.Dimensions.ASTEROIDS)) ci.cancel();
    }
}
