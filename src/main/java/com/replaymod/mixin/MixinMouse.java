package com.replaymod.mixin;

import com.replaymod.core.events.KeyMappingEventCallback;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouse {
    @Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", shift = At.Shift.AFTER))
    private void afterKeyMappingTick(CallbackInfo ci) {
        KeyMappingEventCallback.EVENT.invoker().onKeybindingEvent();
    }
}
