package dev.aurorafx.module.impl;

import dev.aurorafx.module.Module;
import dev.aurorafx.module.Setting;
import dev.aurorafx.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/** Faixa luminosa que acompanha o seu movimento. */
public class Trails extends Module {
    private final Setting length = add(new Setting("Comprimento", 40f, 10f, 120f));
    private final Setting ribbon = add(new Setting("Altura da faixa", 0.5f, 0.1f, 1.8f));
    private final Setting opacity = add(new Setting("Opacidade", 0.7f, 0.1f, 1f));
    private final Setting rainbow = add(new Setting("Arco-íris", true));

    private final List<double[]> points = new ArrayList<double[]>();

    public Trails() {
        super("Trails", true);
    }

    @Override
    public void onTick() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if (p == null) return;
        double[] last = points.isEmpty() ? null : points.get(points.size() - 1);
        double moved = last == null ? 1 : Math.abs(last[0] - p.posX) + Math.abs(last[1] - p.posY) + Math.abs(last[2] - p.posZ);
        if (moved > 0.01) {
            points.add(new double[] {p.posX, p.posY + 0.1, p.posZ});
        } else if (!points.isEmpty()) {
            points.remove(0); // some aos poucos quando parado
        }
        int max = (int) length.value;
        while (points.size() > max) points.remove(0);
    }

    @Override
    public void onRender3D(float pt) {
        if (points.size() < 2) return;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        double[] cam = RenderUtil.camera(pt);
        double[] now = RenderUtil.entityPos(p, pt);
        float h = ribbon.value;
        long time = System.currentTimeMillis();

        int n = points.size();
        RenderUtil.begin3D(1f);
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= n; i++) {
            double[] pos = i == n ? new double[] {now[0], now[1] + 0.1, now[2]} : points.get(i);
            float f = (float) i / n;
            float[] c = rainbow.bool()
                ? RenderUtil.hue(f * 0.6f + (time % 4000L) / 4000f)
                : new float[] {0.45f, 0.65f, 1f};
            float a = f * opacity.value;
            double x = pos[0] - cam[0];
            double y = pos[1] - cam[1];
            double z = pos[2] - cam[2];
            wr.pos(x, y, z).color(c[0], c[1], c[2], a).endVertex();
            wr.pos(x, y + h, z).color(c[0], c[1], c[2], 0f).endVertex();
        }
        tess.draw();
        RenderUtil.end3D();
    }
}
