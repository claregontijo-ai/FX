package dev.aurorafx.module.impl;

import dev.aurorafx.module.Module;
import dev.aurorafx.module.Setting;
import dev.aurorafx.util.RenderUtil;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

/** Anéis coloridos que orbitam o último alvo atingido. */
public class TargetAura extends Module {
    private final Setting duration = add(new Setting("Duração (s)", 3f, 1f, 10f));
    private final Setting speed = add(new Setting("Velocidade", 1.5f, 0.5f, 4f));
    private final Setting width = add(new Setting("Espessura", 2.5f, 1f, 6f));
    private final Setting rainbow = add(new Setting("Arco-íris", true));

    private EntityLivingBase target;
    private long hitTime;

    public TargetAura() {
        super("Target Aura", true);
    }

    @Override
    public void onAttack(Entity e) {
        if (e instanceof EntityLivingBase) {
            target = (EntityLivingBase) e;
            hitTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onRender3D(float pt) {
        if (target == null) return;
        if (target.isDead || target.getHealth() <= 0f) {
            target = null;
            return;
        }
        float elapsed = (System.currentTimeMillis() - hitTime) / 1000f;
        float total = duration.value;
        if (elapsed > total) {
            target = null;
            return;
        }
        float fade = Math.min(1f, (total - elapsed) / 0.6f) * Math.min(1f, elapsed / 0.15f);

        double[] cam = RenderUtil.camera(pt);
        double[] p = RenderUtil.entityPos(target, pt);
        double x = p[0] - cam[0];
        double y = p[1] - cam[1];
        double z = p[2] - cam[2];

        float t = (System.currentTimeMillis() % 100000L) / 1000f * speed.value;
        float h = target.height;
        float radius = target.width * 0.75f + 0.15f;

        RenderUtil.begin3D(width.value);
        // dois anéis em fases opostas subindo e descendo
        for (int ring = 0; ring < 2; ring++) {
            float phase = t * 1.6f + ring * (float) Math.PI;
            float yy = h * (0.5f + 0.5f * (float) Math.sin(phase));
            drawRing(x, y + yy, z, radius, t * 2f + ring, fade);
        }
        // anel fixo na base
        drawRing(x, y + 0.02, z, radius * 1.1f, -t, fade * 0.6f);
        RenderUtil.end3D();
    }

    private void drawRing(double cx, double cy, double cz, float r, float spin, float alpha) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int seg = 64;
        for (int i = 0; i <= seg; i++) {
            float a = (float) (i * Math.PI * 2 / seg);
            float[] c = rainbow.bool()
                ? RenderUtil.hue(a / (float) (Math.PI * 2) + spin * 0.15f)
                : new float[] {0.45f, 0.65f, 1f};
            wr.pos(cx + Math.cos(a + spin) * r, cy, cz + Math.sin(a + spin) * r)
              .color(c[0], c[1], c[2], alpha).endVertex();
        }
        tess.draw();
    }
}
