package dev.tiffany.legitclient;

public final class ClientModule {
    private final String name;
    private final String category;
    private boolean enabled;

    public ClientModule(String name, String category) {
        this.name = name;
        this.category = category;
    }
    public String name() { return name; }
    public String category() { return category; }
    public boolean enabled() { return enabled; }
    public void toggle() { enabled = !enabled; }
}
