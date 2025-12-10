package net.frostbyte.remodel.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public record OpenModelGuiS2CPayload() implements CustomPayload {
        public static final Identifier OPEN_MODEL_GUI_PAYLOAD_ID = Identifier.of(ItemModelModifier.MOD_ID, "open_gui");
        public static final CustomPayload.Id<OpenModelGuiS2CPayload> ID = new CustomPayload.Id<>(OPEN_MODEL_GUI_PAYLOAD_ID);
        public static final PacketCodec<PacketByteBuf, OpenModelGuiS2CPayload> CODEC = PacketCodec.of((buf, payload) -> {}, buf -> new OpenModelGuiS2CPayload());
        @Override
        public Id<? extends CustomPayload> getId() {
            return OpenModelGuiS2CPayload.ID;
        }
    }

    public static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(OpenModelGuiS2CPayload.ID, OpenModelGuiS2CPayload.CODEC);
    }
}
