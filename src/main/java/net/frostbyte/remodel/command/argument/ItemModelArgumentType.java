package net.frostbyte.remodel.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemModelArgumentType implements ArgumentType<Identifier> {
    @Override
    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.read(stringReader);
    }

    public static ItemModelArgumentType itemModel() {
        return new ItemModelArgumentType();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        Identifier[] ids = new Identifier[Minecraft.getInstance().getModelManager().bakedItemStackModels.size()];
        Minecraft.getInstance().getModelManager().bakedItemStackModels.keySet().toArray(ids);

        String[] strings = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            strings[i] = ids[i].toString();
        }

        return SharedSuggestionProvider.suggest(strings, builder);
    }
}
