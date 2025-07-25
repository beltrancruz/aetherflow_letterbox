package com.kiwisaki.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class LetterboxClientMixin {

    private static final int FADE_DURATION_TICKS = 60;
    private int fadeProgressTicks = 0;
    private boolean isFadingIn = false;

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At("HEAD"))
    private void kiwisaki_renderLetterboxWithFade(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        boolean shouldShowLetterbox = client.player.isSneaking();

        if (shouldShowLetterbox) {
            if (!isFadingIn) {
                isFadingIn = true;
                fadeProgressTicks = 0;
            }
        } else {
            if (isFadingIn) {
                isFadingIn = false;
                fadeProgressTicks = FADE_DURATION_TICKS;
            }
        }

        if (isFadingIn) {
            if (fadeProgressTicks < FADE_DURATION_TICKS) {
                fadeProgressTicks++;
            }
        } else {
            if (fadeProgressTicks > 0) {
                fadeProgressTicks--;
            }
        }

        float linearProgress = (float) fadeProgressTicks + tickCounter.getTickDelta(true);
        float normalizedProgress = MathHelper.clamp(linearProgress / FADE_DURATION_TICKS, 0.0F, 1.0F);

        float easedProgress;
        if (isFadingIn) {
            float x = normalizedProgress;
            easedProgress = 1.0F - (float)Math.pow(1.0F - x, 3.0);
        } else {
            float x = normalizedProgress;
            easedProgress = (float)Math.pow(x, 3.0);
        }

        float alpha = easedProgress;

        if (!shouldShowLetterbox && alpha == 0.0F && fadeProgressTicks == 0) {
            return;
        }

        if (alpha > 0.0F) {
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();

            float proportionalBarHeight = height / 10.0F;
            int currentBarHeight = (int) (proportionalBarHeight * alpha);

            int color = ((int)(alpha * 255.0F) << 24) | 0x000000;

            context.fill(0, 0, width, currentBarHeight, color);
            context.fill(0, height - currentBarHeight, width, height, color);
        }
    }
}