package net.frostbyte.remodel.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemModelArgumentType implements ArgumentType<Identifier> {
    @Override
    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(stringReader);
        return MinecraftClient.getInstance().getBakedModelManager().bakedItemModels.containsKey(id) ? id : ItemStack.EMPTY.getComponents().get(DataComponentTypes.ITEM_MODEL);
    }

    public static ItemModelArgumentType itemModel() {
        return new ItemModelArgumentType();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Identifier[] ids = new Identifier[MinecraftClient.getInstance().getBakedModelManager().bakedItemModels.size()];
        MinecraftClient.getInstance().getBakedModelManager().bakedItemModels.keySet().toArray(ids);

        String[] strings = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            strings[i] = ids[i].toString();
        }

        return CommandSource.suggestMatching(strings, builder);
    }
}
