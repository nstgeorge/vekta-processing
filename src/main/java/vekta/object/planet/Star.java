package vekta.object.planet;

import processing.core.PVector;
import vekta.RenderDistance;

import static vekta.Vekta.v;

public class Star extends Planet {
	public Star(String name, float mass, float density, PVector position, PVector velocity, int color) {
		super(name, mass, density, position, velocity, color);
	}

	@Override
	public boolean isHabitable() {
		return false;
	}

	@Override
	public void draw(RenderDistance dist) {
		drawRadialGradient(getColor(), v.color(0), getRadius(), getRadius() * 1.3F);
		v.fill(0);
		super.draw(dist);
	}
	
	// Draws radial gradient. This abstraction isn't necessary, but it helps readability
	private void drawRadialGradient(int colorFrom, int colorTo, float innerRadius, float outerRadius) {
		for(float i = outerRadius; i >= innerRadius; i -= 20) {
			int color = v.lerpColor(colorFrom, colorTo, (i - innerRadius) / (outerRadius - innerRadius));
			v.stroke(color);
			v.fill(color);
			v.ellipse(0, 0, i, i);
		}
	}
}
