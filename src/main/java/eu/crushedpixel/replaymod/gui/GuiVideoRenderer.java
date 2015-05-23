package eu.crushedpixel.replaymod.gui;

import eu.crushedpixel.replaymod.video.VideoRenderer;
import eu.crushedpixel.replaymod.video.frame.FrameRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiVideoRenderer extends GuiScreen {
    private final VideoRenderer renderer;

    private final String PAUSE_RENDERING = "Pause Rendering";
    private final String RESUME_RENDERING = "Resume Rendering";
    private final String CANCEL = "Cancel Rendering";
    private final String CANCEL_CONFIRM = "Sure?";

    private GuiButton pauseButton;
    private GuiButton cancelButton;
    private GuiCheckBox previewCheckBox;

    public GuiVideoRenderer(VideoRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    @SuppressWarnings("unchecked") // I blame forge for not re-adding generics to that list
    public void initGui() {
        FrameRenderer frameRenderer = renderer.getFrameRenderer();

        String text = "Show Preview (Performance might suffer)";
        buttonList.add(previewCheckBox = new GuiCheckBox(0, (width - fontRendererObj.getStringWidth(text)) / 2 - 8,
                frameRenderer.getPreviewHeight(this), text, false));

        text = PAUSE_RENDERING;
        buttonList.add(pauseButton = new GuiButton(1, width / 2 - 202, height / 2 - 23, text));

        text = CANCEL;
        buttonList.add(cancelButton = new GuiButton(1, width / 2 + 2, height / 2 - 23, text));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        String cancelBefore = cancelButton.displayString;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        String cancelAfter = cancelButton.displayString;
        if (cancelBefore.equals(cancelAfter)) {
            cancelButton.displayString = CANCEL;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == pauseButton) {
            if (PAUSE_RENDERING.equals(pauseButton.displayString)) {
                renderer.setPaused(true);
                pauseButton.displayString = RESUME_RENDERING;
            } else if (RESUME_RENDERING.equals(pauseButton.displayString)) {
                renderer.setPaused(false);
                pauseButton.displayString = PAUSE_RENDERING;
            } else {
                throw new IllegalStateException(pauseButton.displayString);
            }
        } else if (button == cancelButton) {
            if (CANCEL.equals(cancelButton.displayString)) {
                cancelButton.displayString = CANCEL_CONFIRM;
            } else if (CANCEL_CONFIRM.equals(cancelButton.displayString)) {
                renderer.cancel();
            }
        } else if (button == previewCheckBox) {
            renderer.getFrameRenderer().setPreviewActive(previewCheckBox.isChecked());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FrameRenderer frameRenderer = renderer.getFrameRenderer();
        int centerX = width / 2;
        int centerY = height / 2;

        drawBackground(0);

        String framesProgress = String.format("Frames rendered: %d / %d", renderer.getFramesDone(), renderer.getTotalFrames());
        drawCenteredString(fontRendererObj, framesProgress, centerX, centerY - 45, 0xffffffff);

        if (previewCheckBox.isChecked()) {
            frameRenderer.renderPreview(this);
        } else {
            int previewHeight = frameRenderer.getPreviewHeight(this) - height / 2 - 10;
            FrameRenderer.renderNoPreview(width, height, previewHeight);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
