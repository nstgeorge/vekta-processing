package vekta.object;

import processing.core.*;
import vekta.Vekta;

import java.util.List;

public abstract class SpaceObject {
	private static final float MAX_INFLUENCE = 2F;

	private int id;

	protected final PVector position;
	protected final PVector velocity;
	private final int color;

	private final PVector[] trail = new PVector[Vekta.TRAIL_LENGTH];

	public SpaceObject(int color) {
		this(new PVector(), new PVector(), color);
	}

	public SpaceObject(PVector position, PVector velocity, int color) {
		this.position = position;
		this.velocity = velocity;
		this.color = color;
	}

	/**
	 * Gets the unique ID of an object
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Sets the unique ID of an object
	 */
	public final void setID(int id) {
		this.id = id;
	}

	/**
	 * Gets the name of the object
	 */
	public abstract String getName();

	/**
	 * Gets the mass of the object
	 */
	public abstract float getMass();

	/**
	 * Gets the position of the object
	 */
	public final PVector getPosition() {
		return position.copy(); // TODO: copy externally for performance
	}

	/**
	 * Gets the velocity of the object
	 */
	public final PVector getVelocity() {
		return velocity.copy(); // TODO: copy externally for performance
	}

	/**
	 * Sets the velocity of the object
	 */
	public final PVector setVelocity(PVector velocity) {
		return this.velocity.set(velocity);
	}

	/**
	 * Adds velocity to the object
	 */
	public final PVector addVelocity(PVector delta) {
		return this.velocity.add(delta);
	}

	/**
	 * Gets the color of the object
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Gets the radius of the object (for collision purposes, not all objects are circular)
	 */
	public abstract float getRadius();

	/**
	 * Returns and applies the influence vector of another object on this
	 */
	public PVector applyInfluenceVector(List<SpaceObject> objects) {
		float mass = getMass();
		PVector influence = new PVector();
		for(SpaceObject s : objects) {
			float distSq = Vekta.getDistSq(position, s.getPosition());
			if(distSq == 0)
				continue; // If the planet being checked is itself (or directly on top), don't move
			float force = Vekta.G * mass * s.getMass() / (distSq * Vekta.SCALE * Vekta.SCALE); // G defined in orbit
			influence.add(new PVector(s.getPosition().x - position.x, s.getPosition().y - position.y).setMag(force / mass));
		}
		// Prevent insane acceleration
		influence.limit(MAX_INFLUENCE);
		if(!Float.isFinite(influence.x) || !Float.isFinite(influence.y)) {
			// This helps prevent the random blank screen of doom (NaN propagation)
			return new PVector();
		}
		addVelocity(influence);
		return influence;
	}

	/**
	 * Does this collide with that?
	 */
	public boolean collidesWith(SpaceObject s) {
		double r = PVector.dist(getPosition(), s.getPosition());
		return r < (getRadius() + s.getRadius());
	}

	/**
	 * When colliding, does this object destroy the other?
	 * By default, the other object will be destroyed if this mass is at least half of their mass.
	 */
	public boolean shouldDestroy(SpaceObject other) {
		return getMass() * 2 >= other.getMass();
	}

	/**
	 * Invoked when colliding with SpaceObject `s`
	 */
	public void onCollide(SpaceObject s) {
		if(shouldDestroy(s)) {
			s.onDestroy(this);
			Vekta.removeObject(s);
		}
	}

	/**
	 * Invoked when destroyed by SpaceObject `s`
	 */
	public void onDestroy(SpaceObject s) {
	}

	public abstract void draw();

	public void drawTrail() {
		Vekta v = Vekta.getInstance();
		// Update trail vectors
		for(int i = trail.length - 1; i > 0; i--) {
			trail[i] = trail[i - 1];
		}
		trail[0] = position.copy();

		for(int i = 1; i < trail.length; i++) {
			PVector oldPos = trail[i - 1];
			PVector newPos = trail[i];
			if(newPos == null) {
				break;
			}
			// Set the color and draw the line segment
			v.stroke(v.lerpColor(getColor(), v.color(0), (float)i / trail.length));
			v.line(oldPos.x, oldPos.y, newPos.x, newPos.y);
		}
	}

	/**
	 * Update the position of this SpaceObject.
	 */
	public final void update() {
		onUpdate();
		position.add(velocity);
	}

	public void onUpdate() {
	}
}  