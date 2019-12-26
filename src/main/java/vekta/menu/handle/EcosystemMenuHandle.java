package vekta.menu.handle;

import vekta.ecosystem.Ecosystem;
import vekta.menu.Menu;
import vekta.menu.option.MenuOption;
import vekta.menu.option.SpeciesButton;

import static vekta.Vekta.quantityString;
import static vekta.Vekta.v;

public class EcosystemMenuHandle extends SideLayoutMenuHandle {
	private final Ecosystem ecosystem;

	public EcosystemMenuHandle(Ecosystem ecosystem) {
		super(true);

		this.ecosystem = ecosystem;
	}

	public Ecosystem getEcosystem() {
		return ecosystem;
	}

	//	@Override
	//	public void render(Menu menu) {
	//		super.render(menu);
	//
	//		v.textSize(32);
	//		v.fill(container.getColor());
	//		v.text(container.getName());
	//	}

	@Override
	protected void drawButton(Menu menu, MenuOption opt, int index) {
		super.drawButton(menu, opt, index);

		if(opt instanceof SpeciesButton) {
			
		}
	}
}