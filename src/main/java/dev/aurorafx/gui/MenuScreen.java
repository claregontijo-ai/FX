package dev.aurorafx.gui;

import dev.aurorafx.module.Module;
import dev.aurorafx.module.ModuleManager;
import dev.aurorafx.module.Setting;
import dev.aurorafx.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/** Menu simples (RShift): lista de módulos à esquerda, configurações à direita. */
public class MenuScreen extends GuiScreen {
    private static final int PW = 340;
    private static final int PH = 210;
    private static final int LIST_W = 104;
    private static final int SLIDER_W = 170;

    private final ModuleManager manager;
    private Module selected;
    private Setting dragging;
    private int px;
    private int py;

    public MenuScreen(ModuleManager manager) {
        this.manager = manager;
        this.selected = manager.modules.isEmpty() ? null : manager.modules.get(0);
    }

    @Override
    public void initGui() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawRect(0, 0, width, height, 0x66000000);
        RenderUtil.pill(px, py, PW, PH, 0xEE0A0A0B);
        fontRendererObj.drawStringWithShadow("AuroraFX", px + 10, py + 9, 0x7AA5FF);

        // lista de módulos
        for (int i = 0; i < manager.modules.size(); i++) {
            Module m = manager.modules.get(i);
            int y = py + 28 + i * 22;
            boolean sel = m == selected;
            RenderUtil.pill(px + 8, y, LIST_W, 18, sel ? 0xFF22232A : 0xFF141418);
            fontRendererObj.drawString(m.name, px + 16, y + 5, 0xFFFFFF);
            int dot = m.enabled ? 0xFF4ADE80 : 0xFF55555C;
            drawRect(px + 8 + LIST_W - 14, y + 6, px + 8 + LIST_W - 6, y + 12, dot);
        }

        // configurações do módulo selecionado
        if (selected != null) {
            int sx = px + LIST_W + 24;
            fontRendererObj.drawStringWithShadow(selected.name, sx, py + 9, 0xFFFFFF);
            for (int j = 0; j < selected.settings.size(); j++) {
                Setting s = selected.settings.get(j);
                int y = py + 30 + j * 26;
                fontRendererObj.drawString(s.name, sx, y, 0xB0B0B8);
                if (s.isBool) {
                    int bx = sx + SLIDER_W - 22;
                    drawRect(bx, y, bx + 22, y + 9, s.bool() ? 0xFF4ADE80 : 0xFF3A3A42);
                    int kx = s.bool() ? bx + 13 : bx + 1;
                    drawRect(kx, y + 1, kx + 8, y + 8, 0xFFFFFFFF);
                } else {
                    int by = y + 11;
                    drawRect(sx, by, sx + SLIDER_W, by + 4, 0xFF2A2A31);
                    float f = (s.value - s.min) / (s.max - s.min);
                    drawRect(sx, by, sx + (int) (SLIDER_W * f), by + 4, 0xFF7AA5FF);
                    drawRect(sx + (int) (SLIDER_W * f) - 2, by - 2, sx + (int) (SLIDER_W * f) + 2, by + 6, 0xFFFFFFFF);
                    String v = String.format("%.2f", s.value);
                    fontRendererObj.drawString(v, sx + SLIDER_W - fontRendererObj.getStringWidth(v), y, 0x7AA5FF);
                }
            }
        }
        fontRendererObj.drawString("Clique no ponto verde para ligar/desligar • ESC fecha",
            px + 10, py + PH - 14, 0x70707A);
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        super.mouseClicked(mx, my, button);
        if (button != 0) return;

        for (int i = 0; i < manager.modules.size(); i++) {
            Module m = manager.modules.get(i);
            int y = py + 28 + i * 22;
            if (mx >= px + 8 && mx <= px + 8 + LIST_W && my >= y && my <= y + 18) {
                if (mx >= px + 8 + LIST_W - 18) m.enabled = !m.enabled;
                else selected = m;
                return;
            }
        }

        if (selected == null) return;
        int sx = px + LIST_W + 24;
        for (int j = 0; j < selected.settings.size(); j++) {
            Setting s = selected.settings.get(j);
            int y = py + 30 + j * 26;
            if (mx < sx || mx > sx + SLIDER_W || my < y - 2 || my > y + 18) continue;
            if (s.isBool) {
                s.toggle();
            } else {
                dragging = s;
                updateSlider(mx, sx);
            }
            return;
        }
    }

    @Override
    protected void mouseClickMove(int mx, int my, int button, long time) {
        if (dragging != null) updateSlider(mx, px + LIST_W + 24);
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        dragging = null;
    }

    private void updateSlider(int mx, int sx) {
        float f = Math.max(0f, Math.min(1f, (mx - sx) / (float) SLIDER_W));
        dragging.set(dragging.min + f * (dragging.max - dragging.min));
    }
}
