package com.replaymod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.render.hooks.EntityRendererHandler;
import de.johni0702.minecraft.gui.utils.lwjgl.ReadableColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Instead of rendering the normal sky, clears the screen with a uniform color for use with chroma keying.
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
@Mixin(LevelRenderer.class)
public abstract class Mixin_ChromaKeyColorSky {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void chromaKeyingSky(CallbackInfo ci) {
        EntityRendererHandler handler = ((EntityRendererHandler.IEntityRenderer) this.minecraft.gameRenderer).replayModRender_getHandler();
        if (handler != null) {
            ReadableColor color = handler.getSettings().getChromaKeyingColor();
            if (color != null) {
                GlStateManager._clearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
                GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT
                        , false
                );
                ci.cancel();
            }
        }
    }
}