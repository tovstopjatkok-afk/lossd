package dev.tiffany.legitclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final ClientModule module;
    private boolean waitingForKey = false;

    public ModuleSettingsScreen(Screen parent, ClientModule module) {
        super(Text.literal(module.name() + " settings"));
        this.parent = parent;
        this.module = module;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int x = width / 2 - 150, y = height / 2 - 95;
        ctx.fill(x, y, x + 300, y + 190, 0xF0121218);
        ctx.fill(x, y, x + 300, y + 3, 0xFFB52B38);
        ctx.drawText(textRenderer, module.name(), x + 16, y + 16, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "Настройки модуля", x + 16, y + 39, 0xFFAAAAAA, false);

        ctx.fill(x + 16, y + 66, x + 284, y + 96, 0xFF292933);
        String bind = waitingForKey ? "Нажми клавишу..." :
            (module.keyCode() < 0 ? "Не назначен" :
             InputUtil.fromKeyCode(module.keyCode(), 0).getLocalizedText().getString());
        ctx.drawText(textRenderer, "Бинд: " + bind, x + 27, y + 77, 0xFFFFFFFF, false);

        ctx.fill(x + 16, y + 106, x + 284, y + 136, 0xFF292933);
        ctx.drawText(textRenderer, "Дистанция (настройка-заготовка): " +
            String.format(java.util.Locale.ROOT, "%.1f", module.range()), x + 27, y + 117, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "ЛКМ по строке дистанции: +0.5", x + 16, y + 147, 0xFF999999, false);
        ctx.drawText(textRenderer, "ESC — назад | DEL — убрать бинд", x + 16, y + 169, 0xFF999999, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = width / 2 - 150, y = height / 2 - 95;
        if (button == 0 && mouseX >= x + 16 && mouseX <= x + 284) {
            if (mouseY >= y + 66 && mouseY <= y + 96) {
                waitingForKey = true;
                return true;
            }
            if (mouseY >= y + 106 && mouseY <= y + 136) {
                module.setRange(module.range() >= 10.0 ? 1.0 : module.range() + 0.5);
                LegitClient.saveConfig();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                waitingForKey = false;
            } else if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                module.setKeyCode(GLFW.GLFW_KEY_UNKNOWN);
                waitingForKey = false;
                LegitClient.saveConfig();
            } else {
                module.setKeyCode(keyCode);
                waitingForKey = false;
                LegitClient.saveConfig();
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}
