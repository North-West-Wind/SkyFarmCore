package ml.northwestwind.skyfarm.tile.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// Thank you TurtyWurty
@OnlyIn(Dist.CLIENT)
public class ParaboxRenderer extends TileEntityRenderer<ParaboxTileEntity> {
    private float degrees = 0f;

    public ParaboxRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ParaboxTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        if (!tile.isWorldInLoop()) return;
        ItemStack stack = new ItemStack(tile.getWantingItem());
        if (!stack.isEmpty()) {
            matrix.pushPose();
            matrix.translate(0.5D, 1.6D, 0.5D);
            matrix.scale(0.9f, 0.9f, 0.9f);
            matrix.mulPose(Vector3f.YP.rotationDegrees(degrees++ / 2));
            renderItem(stack, matrix, buffer, light);
            matrix.popPose();
        }
    }

    private void renderItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrix, bufferIn);
    }
}
