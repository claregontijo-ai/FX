package dev.aurorafx.module.impl;

import dev.aurorafx.module.Module;
import dev.aurorafx.module.Setting;
import dev.aurorafx.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** HUD compacto no topo da tela, estilo "ilha", com os módulos que você escolher. */
public class WatermarkHud extends Module {
    private final Setting showFps = add(new Setting("FPS", true));
    private final Setting showPing = add(new Setting("Ping", true));
    private final Setting showXyz = add(new Setting("Coordenadas", false));
    private final Setting showTime = add(new Setting("Hora", true));
    private final Setting offsetY = add(new Setting("Margem topo", 6f, 0f, 40f));

    private final SimpleDateFormat clock = new SimpleDateFormat("HH:mm");

    public WatermarkHud() {
        super("Island HUD", true);
    }

    @Override
    public void onRenderHud(ScaledResolution sr) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;

        List<String> parts = new ArrayList<String>();
        parts.add(mc.thePlayer.getName());
        if (showFps.bool()) parts.add(Minecraft.getDebugFPS() + " FPS");
        if (showPing.bool()) {
            NetworkPlayerInfo info = mc.getNetHandler() == null
                ? null : mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            parts.add((info == null ? 0 : info.getResponseTime()) + "ms");
        }
        if (showXyz.bool()) {
            parts.add((int) mc.thePlayer.posX + " " + (int) mc.thePlayer.posY + " " + (int) mc.thePlayer.posZ);
        }
        if (showTime.bool()) parts.add(clock.format(new Date()));

        String sep = "  §7|  §f";
        StringBuilder sb = new StringBuilder("§f");
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(parts.get(i));
        }
        String text = sb.toString();

        int padX = 10;
        int w = fr.getStringWidth(text) + padX * 2;
        int h = 16;
        int x = (sr.getScaledWidth() - w) / 2;
        int y = (int) offsetY.value;

        RenderUtil.pill(x, y, w, h, 0xCC0A0A0B);
        // linha de destaque com cor animada
        int accent = Color.HSBtoRGB((System.currentTimeMillis() % 6000L) / 6000f, 0.55f, 1f) | 0xFF000000;
        net.minecraft.client.gui.Gui.drawRect(x + 3, y + h - 1, x + w - 3, y + h, accent);
        fr.drawStringWithShadow(text, x + padX, y + 4, 0xFFFFFF);
    }
}
