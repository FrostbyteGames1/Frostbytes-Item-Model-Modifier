package net.frostbyte.remodel;

import net.fabricmc.api.ModInitializer;
import net.frostbyte.remodel.command.ModCommands;
import net.frostbyte.remodel.networking.ModNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemModelModifier implements ModInitializer {
	public static final String MOD_ID = "remodel";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Frostbyte's Item Model Modifier");

		ModNetworking.registerS2C();
		ModCommands.registerModCommands();
	}
}