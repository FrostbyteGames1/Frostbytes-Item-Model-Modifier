package net.frostbyte.remodel.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ModNetworking {
    public record OpenModelGuiS2CPayload() implements CustomPacketPayload {
        public static final Identifier OPEN_MODEL_GUI_PAYLOAD_ID = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "open_gui");

        public static final CustomPacketPayload.Type<OpenModelGuiS2CPayload> TYPE = new CustomPacketPayload.Type<>(OPEN_MODEL_GUI_PAYLOAD_ID);

        @SuppressWarnings({"LambdaParameterTypeCanBeSpecified", "unused"})
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenModelGuiS2CPayload> CODEC = StreamCodec.of((buf, payload) -> {}, buf -> new OpenModelGuiS2CPayload());

        @Override
        public @NonNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void registerS2C() {
        PayloadTypeRegistry.clientboundPlay().register(OpenModelGuiS2CPayload.TYPE, OpenModelGuiS2CPayload.CODEC);
    }
}
