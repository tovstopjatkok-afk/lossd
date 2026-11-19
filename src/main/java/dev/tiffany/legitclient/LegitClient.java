package dev.tiffany.legitclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public final class LegitClient implements ClientModInitializer {
    public static final List<ClientModule> MODULES = new ArrayList<>();
    private static KeyBinding clickGuiKey;

    @Override
    public void onInitializeClient() {
        MODULES.add(new ClientModule("Aim Assist (UI)", "Combat"));
        MODULES.add(new ClientModule("Smart Trigger (UI)", "Combat"));
        MODULES.add(new ClientModule("NameTags", "Visual"));
        MODULES.add(new ClientModule("Player ESP (UI)", "Visual"));
        MODULES.add(new ClientModule("Target ESP (UI)", "Visual"));
        MODULES.add(new ClientModule("HUD", "Client"));

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.legitclient.clickgui", InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT, "category.legitclient"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clickGuiKey.wasPressed()) client.setScreen(new ClickGuiScreen());
        });
    }
}
