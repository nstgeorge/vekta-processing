package vekta.terrain.settlement;

import vekta.economy.Economy;
import vekta.economy.NoiseModifier;
import vekta.faction.Faction;
import vekta.object.planet.TerrestrialPlanet;
import vekta.spawner.PersonGenerator;
import vekta.spawner.SettlementGenerator;
import vekta.terrain.settlement.building.*;

import java.util.Set;

import static vekta.Vekta.v;

public class CitySettlement extends Settlement {

	public CitySettlement(TerrestrialPlanet planet, Faction faction) {
		super(planet, faction, "city");

		add(new District(this, "Trade District", BuildingType.MARKET));
		add(new District(this, "Industrial District", BuildingType.INDUSTRIAL));
		add(new District(this, "Housing District", BuildingType.RESIDENTIAL));
		add(new District(this, "Financial District", BuildingType.ECONOMY));
		//		add(new District(this, "Government Offices", BuildingType.GOVERNMENT));

		add(new CapitalBuilding("Governor", this));
		add(new AcademyBuilding(this));

		for(MarketBuilding building : SettlementGenerator.randomMarkets(3, .4F)) {
			add(building);
		}

		if(v.chance(.75F)) {
			if(v.chance(.75F)) {
				add(new JunkyardBuilding());
			}
			add(new RefineryBuilding());
			add(new WorkshopBuilding());
		}

		add(new ForumBuilding(this, (int)(v.random(10, 20))));
		add(new ExchangeBuilding());

		// Add extra people
		int personCt = (int)v.random(4) + 1;
		for(int i = 0; i < personCt; i++) {
			PersonGenerator.createPerson(this);
		}
	}

	@Override
	public String getGenericName() {
		return "City";
	}

	@Override
	public void onSettlementSurveyTags(Set<String> tags) {
		tags.add("Urban");
	}

	@Override
	public void initEconomy(Economy economy) {
		economy.setValue(v.random(5, 10));
		economy.addModifier(new NoiseModifier(2));
	}
}
