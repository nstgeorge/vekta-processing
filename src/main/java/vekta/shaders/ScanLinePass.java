package vekta.shaders;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;
import processing.core.PGraphics;
import processing.opengl.PShader;
import vekta.Settings;

import static vekta.Vekta.v;

public class ScanLinePass implements Pass {
    PShader shader;

    public ScanLinePass() {
        shader = v.loadShader("src/main/java/vekta/shaders/ScanLineFrag.glsl");
    }

    @Override
    public void prepare(Supervisor supervisor) {
        shader.set("count", 50.0f);
        shader.set("resolution", (float)v.width, (float)v.height);
        shader.set("brightnessBoost", 0.001f * Settings.getFloat("scanLineIntensity"));
        shader.set("time", (float)v.millis() / 1000.0f);
    }

    @Override
    public void apply(Supervisor supervisor) {
        PGraphics pass = supervisor.getNextPass();
        supervisor.clearPass(pass);
        shader.set("time", (float)v.millis() / 1000.0f);
        pass.beginDraw();
        pass.shader(shader);
        pass.image(supervisor.getCurrentPass(), 0, 0);
        pass.endDraw();
    }
}
