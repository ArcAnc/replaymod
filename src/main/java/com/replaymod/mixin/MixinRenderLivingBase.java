//#if MC>=10800
package com.replaymod.mixin;

import com.replaymod.render.blend.BlendState;
import com.replaymod.render.blend.exporters.EntityExporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=10904
import net.minecraft.client.renderer.entity.LivingRenderer;
//#else
//$$ import net.minecraft.client.renderer.entity.RendererLivingEntity;
//#endif

//#if MC>=10904
@Mixin(LivingRenderer.class)
//#else
//$$ @Mixin(RendererLivingEntity.class)
//#endif
public abstract class MixinRenderLivingBase {
    //#if MC>=11500
    @Inject(method = "render", at = @At(
    //#else
    //#if FABRIC>=1
    //$$ @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;DDDFF)V", at = @At(
    //#else
    //$$ @Inject(method = "doRender(Lnet/minecraft/entity/LivingEntity;DDDFF)V", at = @At(
    //#endif
    //#endif
            value = "INVOKE",
            //#if MC>=11500
            target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;preRenderCallback(Lnet/minecraft/entity/LivingEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
            //#else
            //#if MC>=10904
            //$$ target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;scaleAndTranslate(Lnet/minecraft/entity/LivingEntity;F)F",
            //#else
            //$$ target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;preRenderCallback(Lnet/minecraft/entity/EntityLivingBase;F)V",
            //#endif
            //#endif
            shift = At.Shift.AFTER
    ))
    private void recordModelMatrix(CallbackInfo ci) {
        BlendState blendState = BlendState.getState();
        if (blendState != null) {
            blendState.get(EntityExporter.class).postEntityLivingSetup();
        }
    }
}
//#endif
