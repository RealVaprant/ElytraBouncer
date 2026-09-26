package dev.elytrabouncer;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ElytraBouncerConfigScreen extends Screen {
    private static final int CONFIG_BUTTON_WIDTH = 200;
    private static final int CONFIG_BUTTON_HEIGHT = 20;
    private final Screen parentScreen;
    private Button enabledButton;

    public ElytraBouncerConfigScreen(Screen parentScreen) {
        super(Component.translatable("screen.elytra_bouncer.config.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int buttonLeft = (width - CONFIG_BUTTON_WIDTH) / 2;
        int enabledButtonTop = height / 2 - CONFIG_BUTTON_HEIGHT - 4;
        int doneButtonTop = height / 2 + 4;

        Component enabledButtonLabel = createEnabledButtonLabel();
        Button.Builder enabledButtonBuilder = Button.builder(enabledButtonLabel, clickedButton -> toggleEnabled());
        Button.Builder enabledButtonBuilderWithBounds =
                enabledButtonBuilder.bounds(buttonLeft, enabledButtonTop, CONFIG_BUTTON_WIDTH, CONFIG_BUTTON_HEIGHT);
        Button enabledButtonWidget = enabledButtonBuilderWithBounds.build();
        enabledButton = addRenderableWidget(enabledButtonWidget);

        Component doneButtonLabel = Component.translatable("gui.done");
        Button.Builder doneButtonBuilder = Button.builder(doneButtonLabel, clickedButton -> onClose());
        Button.Builder doneButtonBuilderWithBounds =
                doneButtonBuilder.bounds(buttonLeft, doneButtonTop, CONFIG_BUTTON_WIDTH, CONFIG_BUTTON_HEIGHT);
        Button doneButtonWidget = doneButtonBuilderWithBounds.build();
        addRenderableWidget(doneButtonWidget);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    private Component createEnabledButtonLabel() {
        boolean isEnabled = ElytraBouncerConfig.isEnabled();
        String enabledStateTranslationKey = isEnabled ? "options.on" : "options.off";
        Component enabledStateLabel = Component.translatable(enabledStateTranslationKey);
        return Component.translatable("config.elytra_bouncer.enabled", enabledStateLabel);
    }

    private void toggleEnabled() {
        boolean isEnabled = ElytraBouncerConfig.isEnabled();
        boolean shouldEnable = !isEnabled;
        ElytraBouncerConfig.setEnabled(shouldEnable);
        ElytraBouncerConfig.saveConfig();

        Component updatedEnabledButtonLabel = createEnabledButtonLabel();
        enabledButton.setMessage(updatedEnabledButtonLabel);
    }
}
