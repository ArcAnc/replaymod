package com.replaymod.mixin;

import com.replaymod.replay.camera.CameraEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.replaymod.core.versions.MCVer.getMinecraft;

@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders", remap = false)
public abstract class Mixin_ShowSpectatedHand_OF {
    // OptiFine redirects renderItemInHand to a new method with the same name but with more arguments
    // since that method is only added by Optifabric, we can't inject our redirect in there (Mixin won't see it).
    // So instead we fake the getCurrentGameMode for an entire section of the new renderItemInHand method by injecting
    // into `Shaders.setRenderingFirstPersonHand` which OF calls before and after our original target.
    // We could also just inject into HEAD and RETURN of the original renderItemInHand method but then we'd miss any calls
    // which OF manually does to its method (and given it has extra parameters, I'd assume that it'll do some).

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "setRenderingFirstPersonHand", at = @At("HEAD"), remap = false)
    private static void fakePlayerGameMode(boolean renderingHand, CallbackInfo ci) {
        LocalPlayer camera = getMinecraft().player;
        if (camera instanceof CameraEntity) {
            MultiPlayerGameMode interactionManager = getMinecraft().gameMode;
            assert interactionManager != null;
            if (renderingHand) {
                // alternative doesn't really matter, the caller only checks for equality to SPECTATOR
                interactionManager.setLocalMode(camera.isSpectator() ? GameType.SPECTATOR : GameType.SURVIVAL);
            } else {
                // reset back to spectator (we're always in spectator during a replay)
                interactionManager.setLocalMode(GameType.SPECTATOR);
            }
        }
    }
}
