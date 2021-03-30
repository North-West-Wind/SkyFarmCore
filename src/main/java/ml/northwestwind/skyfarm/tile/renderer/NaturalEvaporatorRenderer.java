package ml.northwestwind.skyfarm.tile.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.tile.NaturalEvaporatorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NaturalEvaporatorRenderer extends TileEntityRenderer<NaturalEvaporatorTileEntity> {

    public NaturalEvaporatorRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(NaturalEvaporatorTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        NonNullList<ItemStack> items = tile.getItems();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                matrix.pushPose();
                if (stack.getItem() instanceof BlockItem) matrix.translate(0.5D, 1.055D, 0.5D);
                else matrix.translate(0.5D, 0.95D, 0.5D);
                matrix.scale(0.75f, 0.75f, 0.75f);
                matrix.mulPose(Vector3f.XP.rotationDegrees(90));
                renderItem(stack, matrix, buffer, light);
                matrix.popPose();
            }
        }
    }

    private void renderItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn);
    }
}
