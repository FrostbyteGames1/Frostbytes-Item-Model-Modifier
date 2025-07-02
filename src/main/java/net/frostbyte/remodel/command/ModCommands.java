package net.frostbyte.remodel.command;

import net.frostbyte.remodel.ItemModelModifier;
import net.frostbyte.remodel.command.argument.ModCommandArguments;

public class ModCommands {

    public static void registerModCommands() {
        ModCommandArguments.registerModCommandArguments();

        ModelCommand.register();

        ItemModelModifier.LOGGER.info("Registered commands for Frostbyte's Item Model Modifier");
    }

}
