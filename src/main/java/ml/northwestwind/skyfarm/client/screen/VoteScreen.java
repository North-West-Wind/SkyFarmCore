package ml.northwestwind.skyfarm.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import ml.northwestwind.skyfarm.common.packet.SkyFarmPacketHandler;
import ml.northwestwind.skyfarm.common.packet.message.CVoteActivateParaboxPacket;
import ml.northwestwind.skyfarm.common.packet.message.CVoteDeactivateParaboxPacket;
import ml.northwestwind.skyfarm.common.packet.message.DSyncVotePacket;
import ml.northwestwind.skyfarm.common.world.data.SkyblockData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoteScreen extends Screen {
    private Button yes, no;
    private boolean synced = false, voted;
    private SkyblockData.VotingStatus status = SkyblockData.VotingStatus.NONE;
    private static final ITextComponent syncing = new TranslationTextComponent("screen.vote.syncing");
    private static final ITextComponent alrVoted = new TranslationTextComponent("screen.vote.voted");

    public VoteScreen() {
        super(new TranslationTextComponent("screen.vote"));
    }

    @Override
    protected void init() {
        this.yes = new Button(this.width / 2 - 110, this.height / 2 - 10, 100, 20, new TranslationTextComponent("parabox.yes"), button -> {
            if (status.equals(SkyblockData.VotingStatus.ACTIVATE)) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(true));
            else if (status.equals(SkyblockData.VotingStatus.DEACTIVATE)) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(true));
            yes.active = false;
            no.active = false;
            onClose();
        });
        this.no = new Button(this.width / 2 + 10, this.height / 2 - 10, 100, 20, new TranslationTextComponent("parabox.no"), button -> {
            if (status.equals(SkyblockData.VotingStatus.ACTIVATE)) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteActivateParaboxPacket(false));
            else if (status.equals(SkyblockData.VotingStatus.DEACTIVATE)) SkyFarmPacketHandler.INSTANCE.sendToServer(new CVoteDeactivateParaboxPacket(false));
            yes.active = false;
            no.active = false;
            onClose();
        });
        yes.active = false;
        no.active = false;

        addButton(yes);
        addButton(no);
        SkyFarmPacketHandler.INSTANCE.sendToServer(new DSyncVotePacket(SkyblockData.VotingStatus.NONE, false));
    }

    @Override
    public void tick() {
        yes.active = synced && !voted && !status.equals(SkyblockData.VotingStatus.NONE);
        no.active = synced && !voted && !status.equals(SkyblockData.VotingStatus.NONE);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, (height / 9) / 2 - font.lineHeight / 2, 0xFFFFFF);
        if (!synced) drawCenteredString(matrixStack, font, syncing, width / 2, (height / 9) - font.lineHeight / 2, 0xFFFFFF);
        else if (voted) drawCenteredString(matrixStack, font, alrVoted, width / 2, (height / 9) - font.lineHeight / 2, 0xFFFFFF);
        else drawCenteredString(matrixStack, font,
                    new TranslationTextComponent("screen.vote.voting", strFromStatus()),
                    width / 2, (height / 9) - font.lineHeight / 2, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void syncFromPacket(SkyblockData.VotingStatus status, boolean voted) {
        this.status = status;
        this.voted = voted;
        this.synced = true;
    }

    private String strFromStatus() {
        switch (status) {
            case ACTIVATE:
                return new TranslationTextComponent("screen.vote.activate").getString();
            case DEACTIVATE:
                return new TranslationTextComponent("screen.vote.deactivate").getString();
            default:
                return new TranslationTextComponent("screen.vote.none").getString();
        }
    }
}
