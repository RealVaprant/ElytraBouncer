package dev.elytrabouncer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dev.elytrabouncer.mixin.KeyMappingAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

public final class ElytraBouncerClient implements ClientModInitializer {
    private boolean hasGlidedSinceLastBounce;
    private boolean shouldReleaseJumpAfterBounce;

    @Override
    public void onInitializeClient() {
        ElytraBouncerConfig.load();
        ClientTickEvents.START_CLIENT_TICK.register(this::handleJumpInput);
    }

    private void handleJumpInput(Minecraft minecraft) {
        KeyMapping jumpKey = minecraft.options.keyJump;

        if (minecraft.screen != null) {
            resetBounceState();
            jumpKey.setDown(false);
            return;
        }

        if (!ElytraBouncerConfig.isEnabled()) {
            resetBounceState();
            return;
        }

        if (!(jumpKey instanceof KeyMappingAccessor jumpKeyAccessor)) {
            resetBounceState();
            return;
        }

        InputConstants.Key boundJumpKey = jumpKeyAccessor.elytraBouncer$getBoundKey();
        InputConstants.Type boundJumpKeyType = boundJumpKey.getType();
        int boundJumpKeyCode = boundJumpKey.getValue();

        if (!isSupportedJumpKey(boundJumpKeyType)) {
            resetBounceState();
            return;
        }

        boolean isJumpKeyPhysicallyHeld =
                isJumpKeyPhysicallyHeld(minecraft, boundJumpKeyType, boundJumpKeyCode);
        LocalPlayer player = minecraft.player;
        boolean canAssistElytraBounce = isJumpKeyPhysicallyHeld && player != null;

        if (!canAssistElytraBounce) {
            resetBounceState();
            jumpKey.setDown(isJumpKeyPhysicallyHeld);
            return;
        }

        boolean isPlayerOnGround = player.onGround();

        if (shouldReleaseJumpAfterBounce && isPlayerOnGround) {
            jumpKey.setDown(true);
            return;
        }

        if (shouldReleaseJumpAfterBounce) {
            resetBounceState();
            jumpKey.setDown(false);
            return;
        }

        if (player.isFallFlying()) {
            hasGlidedSinceLastBounce = true;
        }

        if (isPlayerOnGround && hasGlidedSinceLastBounce) {
            shouldReleaseJumpAfterBounce = true;
        }

        jumpKey.setDown(true);
    }

    private void resetBounceState() {
        hasGlidedSinceLastBounce = false;
        shouldReleaseJumpAfterBounce = false;
    }

    private static boolean isSupportedJumpKey(InputConstants.Type jumpKeyType) {
        return jumpKeyType == InputConstants.Type.KEYSYM || jumpKeyType == InputConstants.Type.MOUSE;
    }

    private static boolean isJumpKeyPhysicallyHeld(
            Minecraft minecraft,
            InputConstants.Type jumpKeyType,
            int jumpKeyCode
    ) {
        Window minecraftWindow = minecraft.getWindow();

        if (jumpKeyType == InputConstants.Type.KEYSYM) {
            return InputConstants.isKeyDown(minecraftWindow, jumpKeyCode);
        }

        long minecraftWindowHandle = minecraftWindow.handle();
        return GLFW.glfwGetMouseButton(minecraftWindowHandle, jumpKeyCode) == GLFW.GLFW_PRESS;
    }
}
