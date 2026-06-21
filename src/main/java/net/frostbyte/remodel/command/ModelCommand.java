package net.frostbyte.remodel.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frostbyte.remodel.command.argument.ItemModelArgumentType;
import net.frostbyte.remodel.networking.ModNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Optional;

public class ModelCommand {

    @SuppressWarnings("unused")
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            Commands.literal("model")
            .then(Commands.literal("set")
            .then(((((Commands.argument("model", ItemModelArgumentType.itemModel())
                .executes((context) -> executeModelSet(context, context.getArgument("model", Identifier.class)))))))
            ))
            .then(Commands.literal("reset").executes(ModelCommand::executeModelReset))
            .then(Commands.literal("get").executes(ModelCommand::executeModelGet))
            .then(Commands.literal("gui").executes(ModelCommand::executeModelGui))
        ));
    }

    static int executeModelSet(CommandContext<CommandSourceStack> context, Identifier model) {
        Player player = context.getSource().getPlayer();
        if (player != null) {
             ItemStack stack = player.getInventory().getSelectedItem();
             if (!stack.isEmpty()) {
                 stack.set(DataComponents.ITEM_MODEL, model);

                 Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                 if (equippable == null) {
                     stack.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build());
                 } else if (equippable.slot() == EquipmentSlot.HEAD) {
                     stack.set(DataComponents.EQUIPPABLE, new Equippable(
                         EquipmentSlot.HEAD,
                         equippable.equipSound(),
                         Optional.empty(),
                         equippable.cameraOverlay(),
                         equippable.allowedEntities(),
                         equippable.dispensable(),
                         equippable.swappable(),
                         equippable.damageOnHurt(),
                         equippable.equipOnInteract(),
                         equippable.canBeSheared(),
                         equippable.shearingSound()
                     ));
                 }

                 player.playSound(SoundEvents.ANVIL_USE);

                 return 0;
             } else {
                 context.getSource().sendFailure(Component.translatable("command.no_target"));
             }
        }

        return 1;
    }

    static int executeModelReset(CommandContext<CommandSourceStack> context) {
        Player player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedItem();
            if (!stack.isEmpty()) {
                stack.set(DataComponents.ITEM_MODEL, stack.getItem().getDefaultInstance().getComponents().get(DataComponents.ITEM_MODEL));

                stack.set(DataComponents.EQUIPPABLE, stack.getItem().getDefaultInstance().get(DataComponents.EQUIPPABLE));

                player.playSound(SoundEvents.ANVIL_USE);

                return 0;
            } else {
                context.getSource().sendFailure(Component.translatable("command.no_target"));
            }
        }

        return 1;
    }

    @SuppressWarnings("DataFlowIssue")
    static int executeModelGet(CommandContext<CommandSourceStack> context) {
        Player player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedItem();
            if (!stack.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal(player.getInventory().getSelectedItem().get(DataComponents.ITEM_MODEL).toString()));

                return 0;
            } else {
                context.getSource().sendSystemMessage(Component.translatable("command.no_target"));
            }
        }

        return 1;
    }

    static int executeModelGui(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            ItemStack stack = player.getInventory().getSelectedItem();
            if (!stack.isEmpty()) {
                ServerPlayNetworking.send(player, new ModNetworking.OpenModelGuiS2CPayload());

                return 0;
            } else {
                context.getSource().sendFailure(Component.translatable("command.no_target"));
            }
        }

        return 1;
    }

}
