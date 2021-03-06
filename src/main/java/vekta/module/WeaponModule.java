package vekta.module;

import vekta.KeyBinding;
import vekta.util.InfoGroup;

public abstract class WeaponModule extends ShipModule {

	@Override
	public ModuleType getType() {
		return ModuleType.WEAPON;
	}

	@Override
	public void onKeyPress(KeyBinding key) {
		if(key == getFireKey()) {
			fireWeapon();
		}
	}

	@Override
	public void onInfo(InfoGroup info) {
		info.addKey(getFireKey(), "attack");
	}

	public KeyBinding getFireKey() {
		return KeyBinding.SHIP_ATTACK;
	}

	public abstract void fireWeapon();
}
