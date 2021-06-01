package ml.northwestwind.skyfarm.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/util/math/vector/Vector3d;"), method = "setupColor", ordinal = 2, require = 1, allow = 1)
    private static Vector3d onSampleColor(Vector3d val) {
        final Minecraft mc = Minecraft.getInstance();

        final ClientWorld world = mc.level;

        if (world.dimensionType().hasSkyLight()) {
            return world.getSkyColor(mc.gameRenderer.getMainCamera().getBlockPosition(), mc.getDeltaFrameTime());
        } else {
            return val;
        }
    }

    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/vector/Vector3f;dot(Lnet/minecraft/util/math/vector/Vector3f;)F"), method = "setupColor", ordinal = 7, require = 1, allow = 1)
    private static float afterPlaneDot(float dotPrduct) {
        return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainLevel(F)F"), method = "setupColor", require = 1, allow = 1)
    private static float onGetRainGradient(ClientWorld world, float tickDelta) {
        return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderLevel(F)F"), method = "setupColor", require = 1, allow = 1)
    private static float onGetThunderGradient(ClientWorld world, float tickDelta) {
        return 0;
    }
}
