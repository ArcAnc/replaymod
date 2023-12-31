package com.replaymod.mixin;

import com.replaymod.replay.camera.CameraEntity;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.replaymod.core.versions.MCVer.getMinecraft;

@Mixin(SpectatorGui.class)
public abstract class MixinGuiSpectator {
    @Inject(method = "onMouseScrolled", at = @At("HEAD"), cancellable = true)
    public void isInReplay(
            int pAmount, CallbackInfo ci
    ) {
        // Prevent spectator gui from opening while in a replay
        if (getMinecraft().player instanceof CameraEntity) {
            ci.cancel();
        }
    }
}
