package vekta.terrain;

import vekta.Resources;
import vekta.item.Inventory;
import vekta.item.Item;
import vekta.menu.Menu;
import vekta.menu.option.TradeMenuOption;
import vekta.object.PlayerShip;

import java.util.HashMap;
import java.util.Map;

import static vekta.Vekta.v;

public class InhabitedTerrain extends Terrain {
	private static final float ITEM_MARKUP = 1.5F; // Price modifier after buying/selling to a landing terrain

	private final Inventory inventory = new Inventory();
	private final Map<Item, Integer> offers = new HashMap<>();
	private final Map<Item, Integer> shipOffers = new HashMap<>();

	private final String overview;

	public InhabitedTerrain() {
		addFeature("Atmosphere");
		addFeature("Habitable");

		String key;
		if(chance(.4F)) {
			addFeature("Urban");
			addFeature("Defended");
			key = "overview_urban";
		}
		else {
			addFeature("Rural");
			key = "overview_rural";
		}

		overview = v.random(Resources.getStrings(key));
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public String getOverview() {
		return overview;
	}

	@Override
	public void setupLandingMenu(PlayerShip ship, Menu menu) {
		computeOffers(ship.getInventory(), shipOffers, offers, 1 / ITEM_MARKUP);
		computeOffers(getInventory(), offers, shipOffers, ITEM_MARKUP);

		menu.add(new TradeMenuOption(true, ship.getInventory(), getInventory(), offers));
		menu.add(new TradeMenuOption(false, ship.getInventory(), getInventory(), shipOffers));
	}

	private void computeOffers(Inventory inv, Map<Item, Integer> thisSide, Map<Item, Integer> otherSide, float markup) {
		// TODO: adjust based on economic system
		for(Item item : inv) {
			if(otherSide.containsKey(item)) {
				int price = (int)(otherSide.get(item) * markup);
				thisSide.put(item, price);
			}
			else if(!thisSide.containsKey(item)) {
				int price = item.getType().randomPrice();
				thisSide.put(item, price);
			}
		}
	}
}
