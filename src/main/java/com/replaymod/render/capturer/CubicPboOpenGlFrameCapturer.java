package com.replaymod.render.capturer;

import com.replaymod.render.frame.CubicOpenGlFrame;
import com.replaymod.render.frame.OpenGlFrame;

public class CubicPboOpenGlFrameCapturer extends
        PboOpenGlFrameCapturer<CubicOpenGlFrame, CubicOpenGlFrameCapturer.Data> {

    private final int frameSize;

    public CubicPboOpenGlFrameCapturer(LevelRenderer levelRenderer, RenderInfo renderInfo, int frameSize) {
        super(levelRenderer, renderInfo, CubicOpenGlFrameCapturer.Data.class, frameSize * frameSize);
        this.frameSize = frameSize;
        levelRenderer.setOmnidirectional(true);
    }

    @Override
    protected int getFrameWidth() {
        return frameSize;
    }

    @Override
    protected int getFrameHeight() {
        return frameSize;
    }

    @Override
    protected CubicOpenGlFrame create(OpenGlFrame[] from) {
        return new CubicOpenGlFrame(from[0], from[1], from[2], from[3], from[4], from[5]);
    }
}
