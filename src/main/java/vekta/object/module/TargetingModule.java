package vekta.object.module;

import vekta.object.Planet;
import vekta.object.Ship;
import vekta.object.SpaceObject;
import vekta.object.Targeter;

import static vekta.Vekta.*;

public class TargetingModule implements Module, Targeter {
	private static boolean selecting;

	/**
	 * Static access to target selection for UI
	 */
	public static boolean isUsingTargeter() {
		return selecting;
	}

	private Ship ship; // TODO: add a parent class for stateful modules
	private TargetingMode mode;
	private SpaceObject target;

	public TargetingModule() {

	}

	public TargetingMode getMode() {
		return mode;
	}

	public void setMode(TargetingMode mode) {
		this.mode = mode;
		selecting = false;
		getWorld().updateTargeter(ship, this);
	}

	@Override
	public SpaceObject getTarget() {
		return target;
	}

	@Override
	public void setTarget(SpaceObject target) {
		this.target = target;
	}

	@Override
	public boolean isValidTarget(SpaceObject obj) {
		if(ship.isLanding()) {
			return obj instanceof Planet || obj instanceof Ship;
		}
		if(mode == null) {
			return false;
		}

		switch(mode) {
		case NEAREST_PLANET:
			return obj instanceof Planet;
		case NEAREST_SHIP:
			return obj instanceof Ship;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUpdateTarget() {
		return ship.isLanding() || getTarget() == null;
	}

	@Override
	public String getName() {
		return "Targeting Computer";
	}

	@Override
	public ModuleType getType() {
		return ModuleType.TARGETING_COMPUTER;
	}

	@Override
	public boolean isBetter(Module other) {
		return false;
	}

	@Override
	public void onInstall(Ship ship) {
		this.ship = ship;
		setMode(null);
	}

	@Override
	public void onUninstall(Ship ship) {
		onInstall(null);
	}

	@Override
	public void onKeyPress(Ship ship, char key) {
		switch(key) {
		case 't':
			selecting = true;
			break;
		case '1':
			setMode(TargetingModule.TargetingMode.NEAREST_PLANET);
			break;
		case '2':
			setMode(TargetingModule.TargetingMode.NEAREST_SHIP);
			break;
		}
	}

	public enum TargetingMode {
		NEAREST_PLANET,
		NEAREST_SHIP,
	}
}