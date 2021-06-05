package ml.northwestwind.skyfarm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ml.northwestwind.skyfarm.config.SkyFarmConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.ModList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Random;

// From Botania
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    @Final
    private VertexFormat skyFormat;

    @Shadow
    @Nullable
    private VertexBuffer starBuffer;

    /**
     * Render planets and other extras, after the first invoke to ms.mulPose(Y) after getRainStrength is called
     */
    @Inject(
            method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
            slice = @Slice(
                    from = @At(
                            ordinal = 0, value = "INVOKE",
                            target = "Lnet/minecraft/client/world/ClientWorld;getRainLevel(F)F"
                    )
            ),
            at = @At(
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/matrix/MatrixStack;mulPose(Lnet/minecraft/util/math/vector/Quaternion;)V"
            ),
            require = 0
    )
    private void renderExtras(MatrixStack ms, float partialTicks, CallbackInfo ci) {
        if (ModList.get().isLoaded("botania") && SkyFarmConfig.GOG_SKYBOX.get()) renderExtra(ms, Minecraft.getInstance().level, partialTicks, 0);
    }

    /**
     * Make the sun bigger, replace any 30.0F seen before first call to bind
     */
    @ModifyConstant(
            method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
            slice = @Slice(to = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/util/ResourceLocation;)V")),
            constant = @Constant(floatValue = 30.0F),
            require = 0
    )
    private float makeSunBigger(float oldValue) {
        return ModList.get().isLoaded("botania") && SkyFarmConfig.GOG_SKYBOX.get() ? 60f : oldValue;
    }

    /**
     * Make the moon bigger, replace any 20.0F seen between first and second call to bind
     */
    @ModifyConstant(
            method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
            slice = @Slice(
                    from = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/util/ResourceLocation;)V"),
                    to = @At(ordinal = 1, value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/util/ResourceLocation;)V")
            ),
            constant = @Constant(floatValue = 20.0F),
            require = 0
    )
    private float makeMoonBigger(float oldValue) {
        return ModList.get().isLoaded("botania") && SkyFarmConfig.GOG_SKYBOX.get() ? 60f : oldValue;
    }

    /**
     * Render lots of extra stars
     */
    @Inject(
            method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"),
            require = 0
    )
    private void renderExtraStars(MatrixStack ms, float partialTicks, CallbackInfo ci) {
        if (ModList.get().isLoaded("botania") && SkyFarmConfig.GOG_SKYBOX.get()) renderStars(skyFormat, starBuffer, ms, partialTicks);
    }

    private static final String PREFIX_MOD = "botania:";
    private static final String PREFIX_MISC = PREFIX_MOD + "textures/misc/";
    private static final String MISC_SKYBOX = PREFIX_MISC + "skybox.png";
    private static final String MISC_RAINBOW = PREFIX_MISC + "rainbow.png";
    private static final String MISC_PLANET = PREFIX_MISC + "planet";

    private static final ResourceLocation textureSkybox = new ResourceLocation(MISC_SKYBOX);
    private static final ResourceLocation textureRainbow = new ResourceLocation(MISC_RAINBOW);
    private static final ResourceLocation[] planetTextures = new ResourceLocation[] {
            new ResourceLocation(MISC_PLANET + "0.png"),
            new ResourceLocation(MISC_PLANET + "1.png"),
            new ResourceLocation(MISC_PLANET + "2.png"),
            new ResourceLocation(MISC_PLANET + "3.png"),
            new ResourceLocation(MISC_PLANET + "4.png"),
            new ResourceLocation(MISC_PLANET + "5.png")
    };

    private static void renderExtra(MatrixStack ms, ClientWorld world, float partialTicks, float insideVoid) {
        // Botania - Begin extra stuff
        Tessellator tessellator = Tessellator.getInstance();
        float rain = 1.0F - world.getRainLevel(partialTicks);
        float celAng = world.getTimeOfDay(partialTicks);
        float effCelAng = celAng;
        if (celAng > 0.5) {
            effCelAng = 0.5F - (celAng - 0.5F);
        }

        // === Planets
        float scale = 20F;
        float lowA = Math.max(0F, effCelAng - 0.3F) * rain;
        float a = Math.max(0.1F, lowA);

        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        ms.pushPose();
        RenderSystem.color4f(1F, 1F, 1F, a * 4 * (1F - insideVoid));
        ms.mulPose(new Vector3f(0.5F, 0.5F, 0F).rotationDegrees(90));
        for (int p = 0; p < planetTextures.length; p++) {
            Minecraft.getInstance().textureManager.bind(planetTextures[p]);
            Matrix4f mat = ms.last().pose();
            tessellator.getBuilder().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            tessellator.getBuilder().vertex(mat, -scale, 100, -scale).uv(0.0F, 0.0F).endVertex();
            tessellator.getBuilder().vertex(mat, scale, 100, -scale).uv(1.0F, 0.0F).endVertex();
            tessellator.getBuilder().vertex(mat, scale, 100, scale).uv(1.0F, 1.0F).endVertex();
            tessellator.getBuilder().vertex(mat, -scale, 100, scale).uv(0.0F, 1.0F).endVertex();
            tessellator.end();

            switch (p) {
                case 0:
                    ms.mulPose(Vector3f.XP.rotationDegrees(70));
                    scale = 12F;
                    break;
                case 1:
                    ms.mulPose(Vector3f.ZP.rotationDegrees(120));
                    scale = 15F;
                    break;
                case 2:
                    ms.mulPose(new Vector3f(1, 0, 1).rotationDegrees(80));
                    scale = 25F;
                    break;
                case 3:
                    ms.mulPose(Vector3f.ZP.rotationDegrees(100));
                    scale = 10F;
                    break;
                case 4:
                    ms.mulPose(new Vector3f(1, 0, 0.5F).rotationDegrees(-60));
                    scale = 40F;
            }
        }
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        ms.popPose();

        // === Rays
        Minecraft.getInstance().textureManager.bind(textureSkybox);

        scale = 20F;
        a = lowA;
        ms.pushPose();
        RenderSystem.blendFuncSeparate(770, 1, 1, 0);
        ms.translate(0, -1, 0);
        ms.mulPose(Vector3f.XP.rotationDegrees(220));
        RenderSystem.color4f(1F, 1F, 1F, a);
        int angles = 90;
        float y = 2F;
        float y0 = 0F;
        float uPer = 1F / 360F;
        float anglePer = 360F / angles;
        double fuzzPer = Math.PI * 10 / angles;
        float rotSpeed = 1F;
        float rotSpeedMod = 0.4F;

        for (int p = 0; p < 3; p++) {
            float baseAngle = rotSpeed * rotSpeedMod * (Minecraft.getInstance().getDeltaFrameTime() + partialTicks);
            ms.mulPose(Vector3f.YP.rotationDegrees((Minecraft.getInstance().getDeltaFrameTime() + partialTicks) * 0.25F * rotSpeed * rotSpeedMod));

            Matrix4f mat = ms.last().pose();
            tessellator.getBuilder().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            for (int i = 0; i < angles; i++) {
                int j = i;
                if (i % 2 == 0) {
                    j--;
                }

                float ang = j * anglePer + baseAngle;
                float xp = (float) Math.cos(ang * Math.PI / 180F) * scale;
                float zp = (float) Math.sin(ang * Math.PI / 180F) * scale;
                float yo = (float) Math.sin(fuzzPer * j) * 1;

                float ut = ang * uPer;
                if (i % 2 == 0) {
                    tessellator.getBuilder().vertex(mat, xp, yo + y0 + y, zp).uv(ut, 1F).endVertex();
                    tessellator.getBuilder().vertex(mat, xp, yo + y0, zp).uv(ut, 0).endVertex();
                } else {
                    tessellator.getBuilder().vertex(mat, xp, yo + y0, zp).uv(ut, 0).endVertex();
                    tessellator.getBuilder().vertex(mat, xp, yo + y0 + y, zp).uv(ut, 1F).endVertex();
                }

            }
            tessellator.end();

            switch (p) {
                case 0:
                    ms.mulPose(Vector3f.XP.rotationDegrees(20));
                    RenderSystem.color4f(1F, 0.4F, 0.4F, a);
                    fuzzPer = Math.PI * 14 / angles;
                    rotSpeed = 0.2F;
                    break;
                case 1:
                    ms.mulPose(Vector3f.XP.rotationDegrees(50));
                    RenderSystem.color4f(0.4F, 1F, 0.7F, a);
                    fuzzPer = Math.PI * 6 / angles;
                    rotSpeed = 2F;
                    break;
            }
        }
        ms.popPose();

        // === Rainbow
        ms.pushPose();
        GlStateManager._blendFuncSeparate(770, 771, 1, 0);
        Minecraft.getInstance().textureManager.bind(textureRainbow);
        scale = 10F;
        float effCelAng1 = celAng;
        if (effCelAng1 > 0.25F) {
            effCelAng1 = 1F - effCelAng1;
        }
        effCelAng1 = 0.25F - Math.min(0.25F, effCelAng1);

        long time = world.getDayTime() + 1000;
        int day = (int) (time / 24000L);
        Random rand = new Random(day * 0xFF);
        float angle1 = rand.nextFloat() * 360F;
        float angle2 = rand.nextFloat() * 360F;
        RenderSystem.color4f(1F, 1F, 1F, effCelAng1 * (1F - insideVoid));
        ms.mulPose(Vector3f.YP.rotationDegrees(angle1));
        ms.mulPose(Vector3f.ZP.rotationDegrees(angle2));

        Matrix4f mat = ms.last().pose();
        tessellator.getBuilder().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        for (int i = 0; i < angles; i++) {
            int j = i;
            if (i % 2 == 0) {
                j--;
            }

            float ang = j * anglePer;
            float xp = (float) Math.cos(ang * Math.PI / 180F) * scale;
            float zp = (float) Math.sin(ang * Math.PI / 180F) * scale;
            float yo = 0;

            float ut = ang * uPer;
            if (i % 2 == 0) {
                tessellator.getBuilder().vertex(mat, xp, yo + y0 + y, zp).uv(ut, 1F).endVertex();
                tessellator.getBuilder().vertex(mat, xp, yo + y0, zp).uv(ut, 0).endVertex();
            } else {
                tessellator.getBuilder().vertex(mat, xp, yo + y0, zp).uv(ut, 0).endVertex();
                tessellator.getBuilder().vertex(mat, xp, yo + y0 + y, zp).uv(ut, 1F).endVertex();
            }

        }
        tessellator.end();
        ms.popPose();
        RenderSystem.color4f(1F, 1F, 1F, 1F - insideVoid);
        GlStateManager._blendFuncSeparate(770, 1, 1, 0);
    }

    private static void renderStars(VertexFormat format, VertexBuffer starVBO, MatrixStack ms, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        float rain = 1.0F - mc.level.getRainLevel(partialTicks);
        float celAng = mc.level.getTimeOfDay(partialTicks);
        float effCelAng = celAng;
        if (celAng > 0.5) {
            effCelAng = 0.5F - (celAng - 0.5F);
        }
        float alpha = rain * Math.max(0.1F, effCelAng * 2);

        float t = (mc.getDeltaFrameTime() + partialTicks + 2000) * 0.005F;
        ms.pushPose();

        starVBO.bind();
        format.setupBufferState(0);

        ms.pushPose();
        ms.mulPose(Vector3f.YP.rotationDegrees(t * 3));
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.pushPose();
        ms.mulPose(Vector3f.YP.rotationDegrees(t));
        RenderSystem.color4f(0.5F, 1F, 1F, alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.pushPose();
        ms.mulPose(Vector3f.YP.rotationDegrees(t * 2));
        RenderSystem.color4f(1F, 0.75F, 0.75F, alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.pushPose();
        ms.mulPose(Vector3f.ZP.rotationDegrees(t * 3));
        RenderSystem.color4f(1F, 1F, 1F, 0.25F * alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.pushPose();
        ms.mulPose(Vector3f.ZP.rotationDegrees(t));
        RenderSystem.color4f(0.5F, 1F, 1F, 0.25F * alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.pushPose();
        ms.mulPose(Vector3f.ZP.rotationDegrees(t * 2));
        RenderSystem.color4f(1F, 0.75F, 0.75F, 0.25F * alpha);
        starVBO.draw(ms.last().pose(), GL11.GL_QUADS);
        ms.popPose();

        ms.popPose();

        VertexBuffer.unbind();
        format.clearBufferState();
    }
}
