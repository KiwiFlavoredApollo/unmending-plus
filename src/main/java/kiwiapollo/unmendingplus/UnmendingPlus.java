package kiwiapollo.unmendingplus;

import net.fabricmc.api.ModInitializer;

public class UnmendingPlus implements ModInitializer {
	@Override
	public void onInitialize() {
		AnvilUpdateEvent.EVENT.register(new AnvilUpdateDisabler());
	}
}