package vekta;

import processing.core.PVector;
import vekta.item.Inventory;
import vekta.item.Item;
import vekta.item.ItemType;
import vekta.object.*;

import static vekta.Vekta.*;

public class UniverseGen {
	private static final int MIN_POPULATE_DISTANCE = 1000; // Min initial object spawn distance
	private static final float STAR_LIKELIHOOD = .7F;

	private final float radius; // Max persistent object distance
	private final int density;

	public UniverseGen(int size, int density) {
		this.radius = size;
		this.density = density;
	}

	public float getRadius() {
		return radius;
	}

	public void populate() {
		for(int i = 0; i < density; i++) {
			createSystem(randomPos(MIN_POPULATE_DISTANCE, getRadius()));
		}
	}

	public void spawnOccasional(PVector around) {
		Vekta v = getInstance();
		PVector pos = randomPos(getRadius() / 2, getRadius()).add(around);
		float r = v.random(1);
		if(r < .4F) {
			Ship s = new PirateShip("YARRYACHT", PVector.random2D(), pos, new PVector(), v.color(220, 100, 0));
			setupShip(s, 1);
			addObject(s);
		}
		else if(r < .7F) {
			Ship s = new CargoShip("TRAWLX", PVector.random2D(), pos, new PVector(), v.color(0, 100, 255));
			setupShip(s, 3);
			addObject(s);
		}
		else {
			createSystem(pos);
		}
	}

	private void createSystem(PVector pos) {
		Vekta v = Vekta.getInstance();
		float order = v.random(29, 32);

		// Create the center body
		float centerPower = (float)Math.pow(10, order);
		float centerMass = v.random(0.8F, 4) * centerPower;
		float centerDensity = v.random(1, 2);
		if(v.random(0, 1) < STAR_LIKELIHOOD) {
			addObject(new Star(
					centerMass, // Mass
					centerDensity, // Radius
					pos,
					new PVector(),
					v.color(v.random(100, 255), v.random(150, 255), v.random(100, 255))
			));
		}
		else {
			addObject(new GasGiant(
					centerMass, // Mass
					centerDensity,   // Radius
					pos,  // Position
					new PVector(),  // Velocity
					v.color(v.random(100, 255), v.random(100, 255), v.random(100, 255))
			));
		}

		// Generate planets around body
		int planets = (int)v.random(1, 8);
		for(int i = 0; i <= planets; i++) {
			float power = (float)Math.pow(10, order - 1);
			float radiusLoc = v.random(100, 2000);
			float speed = sqrt(G * centerMass / radiusLoc) / SCALE;
			float mass = v.random(0.05F, 0.5F) * power;
			float density = v.random(4, 8);
			float angle = v.random(360);
			TerrestrialPlanet planet = new TerrestrialPlanet(
					mass, // Mass
					density,   // Density
					true, // Habitable
					new PVector(radiusLoc, 0).rotate(angle).add(pos),  // Coords
					new PVector(0, speed).rotate(angle),  // Velocity
					v.color(v.random(100, 255), v.random(100, 255), v.random(100, 255))
			);
			setupPlanet(planet);
			addObject(planet);
		}
	}

	private void setupPlanet(TerrestrialPlanet planet) {
		Vekta v = getInstance();
		LandingSite site = planet.getLandingSite();
		Inventory inv = site.getInventory();
		inv.add((int)v.random(10, 500));
		addItems(inv, 1, 4);
	}

	private void setupShip(Ship ship, int lootTier) {
		addItems(ship.getInventory(), lootTier - 1, lootTier * 2);
	}

	private void addItems(Inventory inv, int min, int max) {
		int itemCt = round(getInstance().random(min, max));
		for(int i = 0; i < itemCt; i++) {
			ItemType type = randomItemType();
			Item item = new Item(generateItemName(type), type);
			inv.add(item);
		}
	}

	private ItemType randomItemType() {
		float r = getInstance().random(1);
		if(r < .7) {
			return ItemType.COMMON;
		}
		else if(r < .95) {
			return ItemType.RARE;
		}
		else {
			return ItemType.LEGENDARY;
		}
	}

	private PVector randomPos(float min, float max) {
		return PVector.random2D().mult(getInstance().random(min, max));
	}
}  
