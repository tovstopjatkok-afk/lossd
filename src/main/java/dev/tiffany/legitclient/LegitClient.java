package dev.tiffany.legitclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
        loadConfig();

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.legitclient.clickgui", InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT, "category.legitclient"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clickGuiKey.wasPressed()) client.setScreen(new ClickGuiScreen());
            if (client.getWindow() != null) {
                long handle = client.getWindow().getHandle();
                for (ClientModule module : MODULES) module.tick(handle);
            }
        });
    }

    public static void saveConfig() {
        Properties properties = new Properties();
        for (int i = 0; i < MODULES.size(); i++) {
            ClientModule m = MODULES.get(i);
            properties.setProperty("module." + i + ".key", Integer.toString(m.keyCode()));
            properties.setProperty("module." + i + ".range", Double.toString(m.range()));
            properties.setProperty("module." + i + ".enabled", Boolean.toString(m.enabled()));
        }
        Path file = FabricLoader.getInstance().getConfigDir().resolve("legitclient.properties");
        try (OutputStream out = new FileOutputStream(file.toFile())) {
            properties.store(out, "LegitClient settings");
        } catch (IOException e) {
            System.err.println("LegitClient: could not save config: " + e.getMessage());
        }
    }

    private static void loadConfig() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("legitclient.properties");
        if (!file.toFile().exists()) return;
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(file.toFile())) {
            properties.load(in);
            for (int i = 0; i < MODULES.size(); i++) {
                ClientModule m = MODULES.get(i);
                m.setKeyCode(Integer.parseInt(properties.getProperty("module." + i + ".key", "-1")));
                m.setRange(Double.parseDouble(properties.getProperty("module." + i + ".range", "4.0")));
                if (Boolean.parseBoolean(properties.getProperty("module." + i + ".enabled", "false")) != m.enabled()) m.toggle();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("LegitClient: could not load config: " + e.getMessage());
        }
    }
}
