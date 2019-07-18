package vekta.terrain;

import vekta.menu.Menu;
import vekta.menu.option.SettlementButton;
import vekta.terrain.settlement.Settlement;

import java.util.Collections;
import java.util.List;

public class HabitableTerrain extends Terrain {
	private Settlement settlement;
	
	public HabitableTerrain(Settlement settlement) {
		this.settlement = settlement;

		addFeature("Atmosphere");
		addFeature("Habitable");
	}

	public Settlement getSettlement() {
		return settlement;
	}

	@Override
	public List<Settlement> getSettlements() {
		return Collections.singletonList(getSettlement());
	}

	public void changeSettlement(LandingSite site, Settlement settlement) {
		if(settlement == null) {
			throw new RuntimeException("Settlement cannot be null");
		}
		this.settlement = settlement;
		settlement.setup(site);
	}

	@Override
	public String getOverview() {
		return getSettlement().getOverview();
	}

	@Override
	public boolean isInhabited() {
		return getSettlement().isInhabited();
	}

	@Override
	public void setup(LandingSite site) {
		getSettlement().setup(site);
	}

	@Override
	public void setupLandingMenu(Menu menu) {
		menu.add(new SettlementButton(getSettlement()));
	}
}
