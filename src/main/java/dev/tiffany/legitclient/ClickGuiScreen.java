package dev.tiffany.legitclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final String[] CATEGORIES = {"Combat", "Movement", "Player", "Visual", "Client"};
    private static final int W = 440, H = 270;
    private String selected = "Combat";

    public ClickGuiScreen() { super(Text.literal("LegitClient")); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int x = (width - W) / 2, y = (height - H) / 2;
        ctx.fill(x, y, x + W, y + H, 0xF0121218);
        ctx.fill(x, y, x + W, y + 3, 0xFFB52B38);
        ctx.fill(x + 9, y + 9, x + 116, y + H - 9, 0xFF1A1A22);
        ctx.drawText(textRenderer, "LEGIT CLIENT", x + 17, y + 19, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "RIGHT SHIFT", x + 17, y + H - 23, 0xFF888888, false);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int ry = y + 49 + i * 31;
            boolean active = selected.equals(CATEGORIES[i]);
            if (active) ctx.fill(x + 13, ry - 5, x + 110, ry + 18, 0xFF71303A);
            ctx.drawText(textRenderer, CATEGORIES[i], x + 21, ry,
                active ? 0xFFFFFFFF : 0xFFAAAAAA, false);
        }
        ctx.drawText(textRenderer, selected, x + 132, y + 20, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "ЛКМ — включить | ПКМ — настройки", x + 132, y + 36, 0xFF999999, false);
        List<ClientModule> visible = LegitClient.MODULES.stream()
            .filter(m -> m.category().equals(selected)).toList();
        for (int i = 0; i < visible.size(); i++) {
            ClientModule m = visible.get(i);
            int ry = y + 58 + i * 35;
            ctx.fill(x + 126, ry, x + W - 12, ry + 28, 0xFF22222B);
            ctx.drawText(textRenderer, m.name(), x + 138, ry + 10, 0xFFEAEAEA, false);
            String bind = m.keyCode() < 0 ? "NONE" : net.minecraft.client.util.InputUtil.fromKeyCode(m.keyCode(), 0).getLocalizedText().getString();
            ctx.drawText(textRenderer, bind, x + W - 105, ry + 10, 0xFFAAAAAA, false);
            ctx.fill(x + W - 57, ry + 7, x + W - 25, ry + 21,
                m.enabled() ? 0xFF438F69 : 0xFF55555F);
            ctx.drawText(textRenderer, m.enabled() ? "ON" : "OFF",
                x + W - 53, ry + 10, 0xFFFFFFFF, false);
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = (width - W) / 2, y = (height - H) / 2;
        if (button == 0) {
            for (int i = 0; i < CATEGORIES.length; i++) {
                int ry = y + 49 + i * 31;
                if (mx >= x + 13 && mx <= x + 113 && my >= ry - 5 && my <= ry + 20) {
                    selected = CATEGORIES[i];
                    return true;
                }
            }
        }
        List<ClientModule> visible = LegitClient.MODULES.stream()
            .filter(m -> m.category().equals(selected)).toList();
        for (int i = 0; i < visible.size(); i++) {
            int ry = y + 58 + i * 35;
            if (mx >= x + 126 && mx <= x + W - 12 && my >= ry && my <= ry + 28) {
                ClientModule module = visible.get(i);
                if (button == 0) {
                    module.toggle();
                    LegitClient.saveConfig();
                    return true;
                }
                if (button == 1) {
                    client.setScreen(new ModuleSettingsScreen(this, module));
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
