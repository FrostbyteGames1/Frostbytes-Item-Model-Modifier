package net.frostbyte.remodel.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frostbyte.remodel.command.argument.ItemModelArgumentType;
import net.frostbyte.remodel.networking.ModNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModelCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            CommandManager.literal("model")
                .then(CommandManager.literal("set")
                    .then(((((CommandManager.argument("model", ItemModelArgumentType.itemModel())
                        .executes((context) -> executeModelSet(context, context.getArgument("model", Identifier.class)))))))
                    )
                )
                .then(CommandManager.literal("reset").executes(ModelCommand::executeModelReset))
                .then(CommandManager.literal("get").executes(ModelCommand::executeModelGet))
                .then(CommandManager.literal("gui").executes(ModelCommand::executeModelGui))
            )
        );
    }

    static int executeModelSet(CommandContext<ServerCommandSource> context, Identifier model) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
             ItemStack stack = player.getInventory().getSelectedStack();
             if (!stack.isEmpty()) {
                 stack.set(DataComponentTypes.ITEM_MODEL, model);

                 EquippableComponent original = stack.get(DataComponentTypes.EQUIPPABLE);
                 if (original != null) {
                     stack.set(DataComponentTypes.EQUIPPABLE, new EquippableComponent(
                         original.slot(),
                         original.equipSound(),
                         Optional.empty(),
                         original.cameraOverlay(),
                         original.allowedEntities(),
                         original.dispensable(),
                         original.swappable(),
                         original.damageOnHurt(),
                         original.equipOnInteract(),
                         original.canBeSheared(),
                         original.shearingSound()
                     ));
                 }

                 player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1, 1);

                 return 0;
             } else {
                 context.getSource().sendFeedback(() -> Text.translatable("command.no_target"), false);
             }
        }

        return 1;
    }

    static int executeModelReset(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedStack();
            if (!stack.isEmpty()) {
                stack.set(DataComponentTypes.ITEM_MODEL, stack.getItem().getDefaultStack().getComponents().get(DataComponentTypes.ITEM_MODEL));

                stack.set(DataComponentTypes.EQUIPPABLE, stack.getItem().getDefaultStack().get(DataComponentTypes.EQUIPPABLE));

                player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 1, 1);

                return 0;
            } else {
                context.getSource().sendFeedback(() -> Text.translatable("command.no_target"), false);
            }
        }

        return 1;
    }

    @SuppressWarnings("DataFlowIssue")
    static int executeModelGet(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedStack();
            if (!stack.isEmpty()) {
                context.getSource().sendFeedback(() -> Text.of(player.getInventory().getSelectedStack().get(DataComponentTypes.ITEM_MODEL)), false);

                return 0;
            } else {
                context.getSource().sendFeedback(() -> Text.translatable("command.no_target"), false);
            }
        }

        return 1;
    }

    static int executeModelGui(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedStack();
            if (!stack.isEmpty()) {
                ServerPlayNetworking.send(player, new ModNetworking.OpenModelGuiS2CPayload());

                return 0;
            } else {
                context.getSource().sendFeedback(() -> Text.translatable("command.no_target"), false);
            }
        }

        return 1;
    }

}
