package net.frostbyte.remodel.command.argument;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;

public class ModCommandArguments {

    public static void registerModCommandArguments() {
        ArgumentTypeRegistry.registerArgumentType(
            Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "item_model"),
            ItemModelArgumentType.class,
            SingletonArgumentInfo.contextFree(ItemModelArgumentType::itemModel)
        );

        ItemModelModifier.LOGGER.info("Registered command arguments for Frostbyte's Item Model Modifier");
    }

}
