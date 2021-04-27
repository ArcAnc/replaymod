package com.replaymod.mixin;

import com.replaymod.core.MinecraftMethodAccessor;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=11400
import com.replaymod.core.events.PostRenderCallback;
import com.replaymod.core.events.PreRenderCallback;
//#else
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import com.replaymod.replay.InputReplayTimer;
//$$ import org.lwjgl.input.Mouse;
//#endif

//#if MC>=11400
import net.minecraft.util.thread.ReentrantThreadExecutor;
//#endif

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft
        //#if MC>=11400
        extends ReentrantThreadExecutor<Runnable>
        //#endif
        implements MinecraftMethodAccessor
        {

    //#if MC>=11400
    public MixinMinecraft(String string_1) { super(string_1); }
    //#endif

    //#if FABRIC>=1
    @Shadow protected abstract void handleInputEvents();
    //#elseif MC>=11400
    //$$ @Shadow protected abstract void processKeyBinds();
    //#endif

    public void replayModProcessKeyBinds() {
        handleInputEvents();
    }

    //#if MC>=11400
    public void replayModExecuteTaskQueue() {
        runTasks();
    }
    //#endif

    //#if MC>=11400
    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private void preRender(boolean unused, CallbackInfo ci) {
        PreRenderCallback.EVENT.invoker().preRender();
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V",
                    shift = At.Shift.AFTER))
    private void postRender(boolean unused, CallbackInfo ci) {
        PostRenderCallback.EVENT.invoker().postRender();
    }
    //#else
    //#if MC>=10904
    //$$ @Shadow protected abstract void runTickKeyboard() throws IOException;
    //$$ @Shadow protected abstract void runTickMouse() throws IOException;
    //$$
    //$$ @Override
    //$$ public void replayModRunTickKeyboard() {
    //$$     try {
    //$$         runTickKeyboard();
    //$$     } catch (IOException e) {
    //$$         e.printStackTrace();
    //$$     }
    //$$ }
    //$$
    //$$ @Override
    //$$ public void replayModRunTickMouse() {
    //$$     try {
    //$$         runTickMouse();
    //$$     } catch (IOException e) {
    //$$         e.printStackTrace();
    //$$     }
    //$$ }
    //#else
    //$$ private boolean earlyReturn;
    //$$
    //$$ @Override
    //$$ public void replayModSetEarlyReturnFromRunTick(boolean earlyReturn) {
    //$$     this.earlyReturn = earlyReturn;
    //$$ }
    //$$
    //$$ @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;sendClickBlockToController(Z)V"), cancellable = true)
    //$$ private void doEarlyReturnFromRunTick(CallbackInfo ci) {
    //$$     if (earlyReturn) ci.cancel();
    //$$ }
    //#endif
    //$$ @Redirect(
            //#if MC>=10904
            //$$ method = "runTickMouse",
            //#else
            //$$ method = "runTick",
            //#endif
    //$$         at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false)
    //$$ )
    //$$ private int scroll() {
    //$$     int wheel = Mouse.getEventDWheel();
    //$$     InputReplayTimer.handleScroll(wheel);
    //$$     return wheel;
    //$$ }
    //#endif
}
