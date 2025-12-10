package net.frostbyte.remodel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frostbyte.remodel.networking.ModNetworking;
import net.frostbyte.remodel.screen.RemodelScreen;

public class ItemModelModifierClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OpenModelGuiS2CPayload.ID, (payload, context) -> context.client().execute(() -> context.client().setScreen(new RemodelScreen())));
    }
}
