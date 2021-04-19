package ml.northwestwind.skyfarm.tile.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.tile.ParaboxTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

// Thank you TurtyWurty
public class ParaboxRenderer extends TileEntityRenderer<ParaboxTileEntity> {
    private float degrees = 0f;

    public ParaboxRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ParaboxTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int light, int overlay) {
        if (!tile.isWorldInLoop()) return;
        ItemStack stack = new ItemStack(tile.getWantingItem());
        if (!stack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 1.5D, 0.5D);
            float currentTime = tile.getLevel().getGameTime() + partialTicks;
            matrixStackIn.translate(0D, (Math.sin(Math.PI * currentTime / 16) / 4) + 0.1D, 0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(degrees++ / 2));
            renderItem(stack, matrixStackIn, buffer, light);
            matrixStackIn.popPose();
        }
    }

    private void renderItem(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
    }
}
