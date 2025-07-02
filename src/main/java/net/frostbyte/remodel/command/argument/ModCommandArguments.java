package net.frostbyte.remodel.command.argument;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

public class ModCommandArguments {

    public static void registerModCommandArguments() {
        ArgumentTypeRegistry.registerArgumentType(
            Identifier.of(ItemModelModifier.MOD_ID, "item_model"),
            ItemModelArgumentType.class,
            ConstantArgumentSerializer.of(ItemModelArgumentType::itemModel)
        );

        ItemModelModifier.LOGGER.info("Registered command arguments for Frostbyte's Item Model Modifier");
    }

}
