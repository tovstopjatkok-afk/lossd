package dev.tiffany.legitclient;

import org.lwjgl.glfw.GLFW;

public final class ClientModule {
    private final String name;
    private final String category;
    private boolean enabled;
    private int keyCode = GLFW.GLFW_KEY_UNKNOWN;
    private boolean wasDown;
    private double range = 4.0;

    public ClientModule(String name, String category) {
        this.name = name;
        this.category = category;
    }
    public String name() { return name; }
    public String category() { return category; }
    public boolean enabled() { return enabled; }
    public void toggle() { enabled = !enabled; }
    public int keyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; this.wasDown = false; }
    public double range() { return range; }
    public void setRange(double range) { this.range = Math.max(1.0, Math.min(10.0, range)); }

    public void tick(long windowHandle) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return;
        boolean down = GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
        if (down && !wasDown) toggle();
        wasDown = down;
    }
}
