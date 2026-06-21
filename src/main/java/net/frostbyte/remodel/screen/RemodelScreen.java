package net.frostbyte.remodel.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RemodelScreen extends Screen {
    int x, y;
    final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/container/model.png");
    final int BACKGROUND_WIDTH = 176;
    final int BACKGROUND_HEIGHT = 194;
    final int TEXTURE_WIDTH = 256;
    final int TEXTURE_HEIGHT = 256;
    int titleX, titleY;
    final Identifier MODEL = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/model.png");
    final Identifier MODEL_HIGHLIGHTED = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/model_highlighted.png");
    final Identifier MODEL_SELECTED = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/model_selected.png");
    int modelListX, modelListY;
    List<Identifier> itemModels;
    int selectedIdx;
    Identifier selectedIdentifier;
    final Identifier SCROLLER = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/scroller.png");
    final Identifier SCROLLER_DISABLED = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/scroller_disabled.png");
    int scrollerX, scrollerY;
    final int SCROLLBAR_WIDTH = 12;
    final int SCROLLBAR_HEIGHT = 15;
    final int LIST_COLUMNS = 8;
    final int LIST_ROWS = 4;
    final int ENTRY_WIDTH = 16;
    final int ENTRY_HEIGHT = 18;
    final int SCROLLBAR_AREA_HEIGHT = 72;
    float scrollAmount = 0;
    int scrollOffset = 0;
    boolean canScroll;
    Minecraft client;
    Player player;
    ItemStack original, modified;
    int originalX, originalY, modifiedX, modifiedY;
    final Identifier SLOT_HIGHLIGHT_BACK = Identifier.withDefaultNamespace("textures/gui/sprites/container/slot_highlight_back.png");
    final Identifier SLOT_HIGHLIGHT_FRONT = Identifier.withDefaultNamespace("textures/gui/sprites/container/slot_highlight_front.png");
    final Identifier BUTTON = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/button.png");
    final Identifier BUTTON_DISABLED = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/button_disabled.png");
    final Identifier BUTTON_HIGHLIGHTED = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/button_highlighted.png");
    final Identifier RESET = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/reset.png");
    final Identifier CONFIRM = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/confirm.png");
    final Identifier CANCEL = Identifier.fromNamespaceAndPath(ItemModelModifier.MOD_ID, "textures/gui/sprites/container/cancel.png");
    int resetButtonX, resetButtonY;
    int setButtonX, setButtonY;
    int closeButtonX, closeButtonY;
    EditBox searchBar;
    String search = "";
    int searchX, searchY;
    final int SEARCH_WIDTH = 142;
    final int SEARCH_HEIGHT = 10;

    public RemodelScreen() {
        super(Component.translatable("model.screen.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.client = Minecraft.getInstance();
        this.player = this.client.player;
        if (this.player == null) {
            this.onClose();
        }
        this.original = this.player.getInventory().getSelectedItem();
        this.modified = this.original.copy();

        this.x = (this.width - this.BACKGROUND_WIDTH) / 2;
        this.y = (this.height - this.BACKGROUND_HEIGHT) / 2;

        this.titleX = this.x + (this.BACKGROUND_WIDTH - this.client.font.width(this.title)) / 2;
        this.titleY = this.y + 8;

        this.itemModels = this.client.getModelManager().bakedItemStackModels.keySet().stream().sorted(Comparator.comparing(Identifier::toString)).toList();
        this.selectedIdentifier = this.original.get(DataComponents.ITEM_MODEL);
        this.selectedIdx = this.getItemModels().indexOf(this.selectedIdentifier);
        this.modelListX = this.x + 17;
        this.modelListY = this.y + 70;

        this.scrollerX = this.x + 148;
        this.scrollerY = this.y + 70;

        this.originalX = this.x + 44;
        this.originalY = this.y + 26;
        this.modifiedX = this.x + 120;
        this.modifiedY = this.y + 26;

        this.resetButtonX = this.x + 16;
        this.resetButtonY = this.y + 156;
        this.setButtonX = this.x + 77;
        this.setButtonY = this.y + 156;
        this.closeButtonX = this.x + 139;
        this.closeButtonY = this.y + 156;

        this.searchX = this.x + 18;
        this.searchY = this.y + 58;
        this.searchBar = new EditBox(
            this.font,
            this.searchX,
            this.searchY,
            this.SEARCH_WIDTH,
            this.SEARCH_HEIGHT,
            Component.empty()
        );
        this.searchBar.setBordered(false);
        this.searchBar.setMaxLength(Integer.MAX_VALUE);
        this.searchBar.setHint(Component.translatable("model.screen.search"));
        this.searchBar.setResponder((text) -> {
            if (!this.search.equals(text)) {
                this.search = text;
                this.scrollAmount = 0;
                this.scrollOffset = 0;
            }
        });
        this.addRenderableWidget(this.searchBar);
    }

    List<Identifier> getItemModels() {
        if (this.search != null && !this.search.isBlank()) {
            return this.itemModels.stream().filter((id) -> id.toString().contains(this.search.replace(" ", "_").toLowerCase())).toList();
        }
        return this.itemModels;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Draw the screen background
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.x, this.y, 0.0F, 0.0F, this.BACKGROUND_WIDTH, this.BACKGROUND_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // Draw the screen title
        graphics.text(this.font, this.title, this.titleX, this.titleY, -12566464, false);

        // Draw the list entry backgrounds
        for(int i = this.scrollOffset; i < scrollOffset + (this.LIST_COLUMNS * this.LIST_ROWS) && i < this.getItemModels().size(); ++i) {
            int j = i - this.scrollOffset;
            int k = this.modelListX + j % this.LIST_COLUMNS * this.ENTRY_WIDTH;
            int l = j / this.LIST_COLUMNS;
            int m = this.modelListY + l * this.ENTRY_HEIGHT;

            Identifier identifier;
            if (i == this.selectedIdx && this.getItemModels().get(i).equals(this.selectedIdentifier)) {
                identifier = MODEL_SELECTED;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + this.ENTRY_WIDTH && mouseY < m + this.ENTRY_HEIGHT) {
                identifier = MODEL_HIGHLIGHTED;
            } else {
                identifier = MODEL;
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, identifier, k, m, 0, 0, this.ENTRY_WIDTH, this.ENTRY_HEIGHT, this.ENTRY_WIDTH, this.ENTRY_HEIGHT);
        }

        // Draw the list entries
        for (int i = this.scrollOffset; i < scrollOffset + (this.LIST_COLUMNS * this.LIST_ROWS) && i < this.getItemModels().size(); ++i) {
            int j = i - this.scrollOffset;
            int k = this.modelListX + j % this.LIST_COLUMNS * this.ENTRY_WIDTH;
            int l = j / this.LIST_COLUMNS;
            int m = this.modelListY + l * this.ENTRY_HEIGHT + 1;

            Identifier id = this.getItemModels().get(i);
            ItemStack entry = this.original.copy();
            entry.set(DataComponents.ITEM_MODEL, id);
            graphics.item(entry, k, m);
            if (mouseX >= k && mouseY >= m && mouseX < k + this.ENTRY_WIDTH && mouseY < m + this.ENTRY_HEIGHT) {
                graphics.setTooltipForNextFrame(Component.literal(id.toString()), mouseX, mouseY);
            }
        }

        // Draw the scrollbar
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.shouldScroll() ? SCROLLER : SCROLLER_DISABLED, this.scrollerX, this.scrollerY + (int)((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount), 0, 0, this.SCROLLBAR_WIDTH, this.SCROLLBAR_HEIGHT, this.SCROLLBAR_WIDTH, this.SCROLLBAR_HEIGHT);

        // Draw the original itemStack
        if (mouseX >= this.originalX && mouseX <= this.originalX + 16 && mouseY >= this.originalY && mouseY <= this.originalY + 16) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK, this.originalX - 4, this.originalY - 4, 0, 0, 24, 24, 24, 24);
        }
        graphics.item(this.original, this.originalX, this.originalY);
        if (mouseX >= this.originalX && mouseX <= this.originalX + 16 && mouseY >= this.originalY && mouseY <= this.originalY + 16) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT, this.originalX - 4, this.originalY - 4, 0, 0, 24, 24, 24, 24);
            graphics.itemDecorations(this.client.font, this.original, mouseX, mouseY);
        }

        // Draw the modified itemStack
        if (mouseX >= this.modifiedX && mouseX <= this.modifiedX + 16 && mouseY >= this.modifiedY && mouseY <= this.modifiedY + 16) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK, this.modifiedX - 4, this.modifiedY - 4, 0, 0, 24, 24, 24, 24);
        }
        graphics.item(this.modified, this.modifiedX, this.modifiedY);
        if (mouseX >= this.modifiedX && mouseX <= this.modifiedX + 16 && mouseY >= this.modifiedY && mouseY <= this.modifiedY + 16) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT, this.modifiedX - 4, this.modifiedY - 4, 0, 0, 24, 24, 24, 24);
            graphics.itemDecorations(this.client.font, this.modified, mouseX, mouseY);
        }

        // Draw the reset button
        Identifier resetButton;
        if (Objects.equals(this.original.get(DataComponents.ITEM_MODEL), this.original.getItem().getDefaultInstance().get(DataComponents.ITEM_MODEL)) && Objects.equals(this.original.get(DataComponents.EQUIPPABLE), this.original.getItem().getDefaultInstance().get(DataComponents.EQUIPPABLE))) {
            resetButton = this.BUTTON_DISABLED;
        } else if (mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            resetButton = this.BUTTON_HIGHLIGHTED;
        } else {
            resetButton = this.BUTTON;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, resetButton, this.resetButtonX, this.resetButtonY, 0, 0, 22, 22, 22, 22);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.RESET, this.resetButtonX + 2, this.resetButtonY + 2, 0, 0, 18, 18, 18, 18);
        if (mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            graphics.setTooltipForNextFrame(Component.translatable("model.screen.reset_button"), mouseX, mouseY);
        }

        // Draw the set button
        Identifier setButton;
        if (ItemStack.isSameItemSameComponents(this.original, this.modified)) {
            setButton = this.BUTTON_DISABLED;
        } else if (mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            setButton = this.BUTTON_HIGHLIGHTED;
        } else {
            setButton = this.BUTTON;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, setButton, this.setButtonX, this.setButtonY, 0, 0, 22, 22, 22, 22);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.CONFIRM, this.setButtonX + 2, this.setButtonY + 2, 0, 0, 18, 18, 18, 18);
        if (mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            graphics.setTooltipForNextFrame(Component.translatable("model.screen.set_button"), mouseX, mouseY);
        }

        // Draw the close button
        Identifier closeButton;
        if (mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            closeButton = this.BUTTON_HIGHLIGHTED;
        } else {
            closeButton = this.BUTTON;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, closeButton, this.closeButtonX, this.closeButtonY, 0, 0, 22, 22, 22, 22);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.CANCEL, this.closeButtonX + 2, this.closeButtonY + 2, 0, 0, 18, 18, 18, 18);
        if (mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            graphics.setTooltipForNextFrame(Component.translatable("model.screen.close_button"), mouseX, mouseY);
        }

        // Draw the search bar
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    boolean shouldScroll() {
        return this.getItemModels().size() > this.LIST_COLUMNS * this.LIST_ROWS;
    }

    protected int getMaxScroll() {
        return (this.getItemModels().size() + this.LIST_COLUMNS - 1) / this.LIST_COLUMNS - this.LIST_ROWS;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.canScroll = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        // Get mouse button info
        int button = event.button();
        double mouseX = event.x();
        double mouseY = event.y();

        // Grab the scroll bar
        this.canScroll = button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.scrollerX && mouseX <= this.scrollerX + this.SCROLLBAR_WIDTH && mouseY >= this.scrollerY + (int) ((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount) && mouseY <= this.scrollerY + (int) ((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount) + this.SCROLLBAR_HEIGHT;

        // Select a new model from the list
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.modelListX && mouseX <= this.modelListX + this.ENTRY_WIDTH * this.LIST_COLUMNS && mouseY >= this.modelListY && mouseY <= this.modelListY + this.ENTRY_HEIGHT * this.LIST_ROWS) {
            int col = Math.floorDiv((int) (mouseX - this.modelListX), this.ENTRY_WIDTH);
            int row = Math.floorDiv((int) (mouseY - this.modelListY), this.ENTRY_HEIGHT);
            int entry = this.scrollOffset + col + row * this.LIST_COLUMNS;
            if (entry >= this.scrollOffset && entry < this.getItemModels().size()) {
                this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
                this.selectedIdx = entry;
                this.selectedIdentifier = this.getItemModels().get(this.selectedIdx);
                this.modified.set(DataComponents.ITEM_MODEL, this.selectedIdentifier);
            }
        }

        // Get the itemstack
        ItemStack stack = this.player.getInventory().getSelectedItem();

        // Reset the model
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
            stack.set(DataComponents.ITEM_MODEL, stack.getItem().getDefaultInstance().getComponents().get(DataComponents.ITEM_MODEL));
            stack.set(DataComponents.EQUIPPABLE, stack.getItem().getDefaultInstance().get(DataComponents.EQUIPPABLE));
            this.player.playSound(SoundEvents.ANVIL_USE);
            this.onClose();
        }

        // Set the model
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
            if (!ItemStack.isSameItemSameComponents(this.original, this.modified)) {
                stack.set(DataComponents.ITEM_MODEL, this.selectedIdentifier);
                Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                if (equippable == null) {
                    stack.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build());
                } else if (equippable.slot() == EquipmentSlot.HEAD) {
                    stack.set(DataComponents.EQUIPPABLE, new Equippable(
                        equippable.slot(),
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
                this.player.playSound(SoundEvents.ANVIL_USE);
                this.onClose();
            }
        }

        // Close the screen
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
            this.onClose();
        }

        // Clear the search bar
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && this.searchBar.isHovered()) {
            this.searchBar.setValue("");
        }

        return super.mouseClicked(event, doubled);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
        if (this.shouldScroll() && this.canScroll) {
            this.scrollAmount = ((float) event.y() - (float) this.scrollerY - (float) this.SCROLLBAR_HEIGHT / 2) / ((float) (this.scrollerY + this.SCROLLBAR_AREA_HEIGHT - this.scrollerY) - this.SCROLLBAR_HEIGHT);
            this.scrollAmount = Math.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + 0.5) * this.LIST_COLUMNS;
        }

        return super.mouseDragged(event, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            if (this.shouldScroll()) {
                this.scrollAmount = Math.clamp(this.scrollAmount - (float) verticalAmount / (float) this.getMaxScroll(), 0.0F, 1.0F);
                this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + 0.5) * this.LIST_COLUMNS;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
