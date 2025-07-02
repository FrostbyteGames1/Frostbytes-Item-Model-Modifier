package net.frostbyte.remodel.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frostbyte.remodel.ItemModelModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class RemodelScreen extends Screen {
    int x, y;
    final Identifier BACKGROUND = Identifier.of(ItemModelModifier.MOD_ID, "textures/gui/container/model.png");
    final int BACKGROUND_WIDTH = 176;
    final int BACKGROUND_HEIGHT = 194;
    final int TEXTURE_WIDTH = 256;
    final int TEXTURE_HEIGHT = 256;
    int titleX, titleY;
    final Identifier MODEL = Identifier.of(ItemModelModifier.MOD_ID, "container/model");
    final Identifier MODEL_HIGHLIGHTED = Identifier.of(ItemModelModifier.MOD_ID, "container/model_highlighted");
    final Identifier MODEL_SELECTED = Identifier.of(ItemModelModifier.MOD_ID, "container/model_selected");
    int modelListX, modelListY;
    List<Identifier> itemModels;
    int selectedIdx;
    Identifier selectedIdentifier;
    final Identifier SCROLLER = Identifier.of(ItemModelModifier.MOD_ID, "container/scroller");
    final Identifier SCROLLER_DISABLED = Identifier.of(ItemModelModifier.MOD_ID, "container/scroller_disabled");
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
    MinecraftClient client;
    ClientPlayerEntity player;
    ItemStack original, modified;
    int originalX, originalY, modifiedX, modifiedY;
    final Identifier SLOT_HIGHLIGHT_BACK = Identifier.ofVanilla("container/slot_highlight_back");
    final Identifier SLOT_HIGHLIGHT_FRONT = Identifier.ofVanilla("container/slot_highlight_front");
    final Identifier BUTTON = Identifier.of(ItemModelModifier.MOD_ID, "container/button");
    final Identifier BUTTON_DISABLED = Identifier.of(ItemModelModifier.MOD_ID, "container/button_disabled");
    final Identifier BUTTON_HIGHLIGHTED = Identifier.of(ItemModelModifier.MOD_ID, "container/button_highlighted");
    final Identifier RESET = Identifier.of(ItemModelModifier.MOD_ID, "container/reset");
    final Identifier CONFIRM = Identifier.of(ItemModelModifier.MOD_ID, "container/confirm");
    final Identifier CANCEL = Identifier.of(ItemModelModifier.MOD_ID, "container/cancel");
    int resetButtonX, resetButtonY;
    int setButtonX, setButtonY;
    int closeButtonX, closeButtonY;
    TextFieldWidget searchBar;
    String search = "";
    int searchX, searchY;
    final int SEARCH_WIDTH = 142;
    final int SEARCH_HEIGHT = 10;

    public RemodelScreen() {
        super(Text.translatable("model.screen.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void applyBlur(DrawContext context) {
    }

    @Override
    protected void init() {
        this.client = MinecraftClient.getInstance();
        this.player = this.client.player;
        if (this.player == null) {
            this.close();
        }
        this.original = this.player.getInventory().getSelectedStack();
        this.modified = this.original.copy();

        this.x = (this.width - this.BACKGROUND_WIDTH) / 2;
        this.y = (this.height - this.BACKGROUND_HEIGHT) / 2;

        this.titleX = this.x + (this.BACKGROUND_WIDTH - this.client.textRenderer.getWidth(this.title)) / 2;
        this.titleY = this.y + 8;

        this.itemModels = this.client.getBakedModelManager().bakedItemModels.keySet().stream().sorted(Comparator.comparing(Identifier::toString)).toList();
        this.selectedIdentifier = this.original.get(DataComponentTypes.ITEM_MODEL);
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
        this.searchBar = new TextFieldWidget(
            this.textRenderer,
            this.searchX,
            this.searchY,
            this.SEARCH_WIDTH,
            this.SEARCH_HEIGHT,
            Text.empty()
        );
        this.searchBar.setDrawsBackground(false);
        this.searchBar.setMaxLength(Integer.MAX_VALUE);
        this.searchBar.setPlaceholder(Text.translatable("model.screen.search"));
        this.searchBar.setChangedListener((text) -> {
            if (!this.search.equals(text)) {
                this.search = text;
                this.scrollAmount = 0;
                this.scrollOffset = 0;
            }
        });
        this.addDrawableChild(this.searchBar);
    }

    List<Identifier> getItemModels() {
        if (this.search != null && !this.search.isBlank()) {
            return this.itemModels.stream().filter((id) -> id.toString().contains(this.search.replaceAll(" ", "_").toLowerCase())).toList();
        }
        return this.itemModels;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Darken the background
        this.renderInGameBackground(context);

        // Draw the screen background
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.x, this.y, 0.0F, 0.0F, this.BACKGROUND_WIDTH, this.BACKGROUND_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // Draw the screen title
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, -12566464, false);

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
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, k, m, this.ENTRY_WIDTH, this.ENTRY_HEIGHT);
        }

        // Draw the list entries
        for(int i = this.scrollOffset; i < scrollOffset + (this.LIST_COLUMNS * this.LIST_ROWS) && i < this.getItemModels().size(); ++i) {
            int j = i - this.scrollOffset;
            int k = this.modelListX + j % this.LIST_COLUMNS * this.ENTRY_WIDTH;
            int l = j / this.LIST_COLUMNS;
            int m = this.modelListY + l * this.ENTRY_HEIGHT + 1;

            Identifier id = this.getItemModels().get(i);
            ItemStack entry = this.original.copy();
            entry.set(DataComponentTypes.ITEM_MODEL, id);
            context.drawItem(entry, k, m);
            if (mouseX >= k && mouseY >= m && mouseX < k + this.ENTRY_WIDTH && mouseY < m + this.ENTRY_HEIGHT) {
                context.drawTooltip(Text.of(id.toString()), mouseX, mouseY);
            }
        }

        // Draw the scrollbar
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.shouldScroll() ? SCROLLER : SCROLLER_DISABLED, this.scrollerX, this.scrollerY + (int)((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount), this.SCROLLBAR_WIDTH, this.SCROLLBAR_HEIGHT);

        // Draw the original itemStack
        if (mouseX >= this.originalX && mouseX <= this.originalX + 16 && mouseY >= this.originalY && mouseY <= this.originalY + 16) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK, this.originalX - 4, this.originalY - 4, 24, 24);
        }
        context.drawItem(this.original, this.originalX, this.originalY);
        if (mouseX >= this.originalX && mouseX <= this.originalX + 16 && mouseY >= this.originalY && mouseY <= this.originalY + 16) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT, this.originalX - 4, this.originalY - 4, 24, 24);
            context.drawItemTooltip(this.client.textRenderer, this.original, mouseX, mouseY);
        }

        // Draw the modified itemStack
        if (mouseX >= this.modifiedX && mouseX <= this.modifiedX + 16 && mouseY >= this.modifiedY && mouseY <= this.modifiedY + 16) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK, this.modifiedX - 4, this.modifiedY - 4, 24, 24);
        }
        context.drawItem(this.modified, this.modifiedX, this.modifiedY);
        if (mouseX >= this.modifiedX && mouseX <= this.modifiedX + 16 && mouseY >= this.modifiedY && mouseY <= this.modifiedY + 16) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT, this.modifiedX - 4, this.modifiedY - 4, 24, 24);
            context.drawItemTooltip(this.client.textRenderer, this.modified, mouseX, mouseY);
        }

        // Draw the reset button
        Identifier resetButton;
        if (Objects.equals(this.original.get(DataComponentTypes.ITEM_MODEL), this.original.getItem().getDefaultStack().get(DataComponentTypes.ITEM_MODEL)) && Objects.equals(this.original.get(DataComponentTypes.EQUIPPABLE), this.original.getItem().getDefaultStack().get(DataComponentTypes.EQUIPPABLE))) {
            resetButton = this.BUTTON_DISABLED;
        } else if (mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            resetButton = this.BUTTON_HIGHLIGHTED;
        } else {
            resetButton = this.BUTTON;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, resetButton, this.resetButtonX, this.resetButtonY, 22, 22);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.RESET, this.resetButtonX + 2, this.resetButtonY + 2, 18, 18);
        if (mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            context.drawTooltip(Text.translatable("model.screen.reset_button"), mouseX, mouseY);
        }

        // Draw the set button
        Identifier setButton;
        if (ItemStack.areItemsAndComponentsEqual(this.original, this.modified)) {
            setButton = this.BUTTON_DISABLED;
        } else if (mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            setButton = this.BUTTON_HIGHLIGHTED;
        } else {
            setButton = this.BUTTON;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, setButton, this.setButtonX, this.setButtonY, 22, 22);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.CONFIRM, this.setButtonX + 2, this.setButtonY + 2, 18, 18);
        if (mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            context.drawTooltip(Text.translatable("model.screen.set_button"), mouseX, mouseY);
        }

        // Draw the close button
        Identifier closeButton;
        if (mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            closeButton = this.BUTTON_HIGHLIGHTED;
        } else {
            closeButton = this.BUTTON;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, closeButton, this.closeButtonX, this.closeButtonY, 22, 22);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.CANCEL, this.closeButtonX + 2, this.closeButtonY + 2, 18, 18);
        if (mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            context.drawTooltip(Text.translatable("model.screen.close_button"), mouseX, mouseY);
        }

        // Draw the search bar
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    boolean shouldScroll() {
        return this.getItemModels().size() > this.LIST_COLUMNS * this.LIST_ROWS;
    }

    protected int getMaxScroll() {
        return (this.getItemModels().size() + this.LIST_COLUMNS - 1) / this.LIST_COLUMNS - this.LIST_ROWS;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.canScroll = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Grab the scroll bar
        this.canScroll = button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.scrollerX && mouseX <= this.scrollerX + this.SCROLLBAR_WIDTH && mouseY >= this.scrollerY + (int) ((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount) && mouseY <= this.scrollerY + (int) ((this.SCROLLBAR_AREA_HEIGHT - this.SCROLLBAR_HEIGHT) * this.scrollAmount) + this.SCROLLBAR_HEIGHT;

        // Select a new model from the list
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.modelListX && mouseX <= this.modelListX + this.ENTRY_WIDTH * this.LIST_COLUMNS && mouseY >= this.modelListY && mouseY <= this.modelListY + this.ENTRY_HEIGHT * this.LIST_ROWS) {
            int col = Math.floorDiv((int) (mouseX - this.modelListX), this.ENTRY_WIDTH);
            int row = Math.floorDiv((int) (mouseY - this.modelListY), this.ENTRY_HEIGHT);
            int entry = this.scrollOffset + col + row * this.LIST_COLUMNS;
            if (entry >= this.scrollOffset && entry < this.getItemModels().size()) {
                this.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.UI, 1, 1);
                this.selectedIdx = entry;
                this.selectedIdentifier = this.getItemModels().get(this.selectedIdx);
                this.modified.set(DataComponentTypes.ITEM_MODEL, this.selectedIdentifier);
            }
        }

        // Reset the model
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.resetButtonX && mouseX <= this.resetButtonX + 22 && mouseY >= this.resetButtonY && mouseY <= this.resetButtonY + 22) {
            this.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.UI, 1, 1);
            if (!Objects.equals(this.original.get(DataComponentTypes.ITEM_MODEL), this.original.getItem().getDefaultStack().get(DataComponentTypes.ITEM_MODEL)) || !Objects.equals(this.original.get(DataComponentTypes.EQUIPPABLE), this.original.getItem().getDefaultStack().get(DataComponentTypes.EQUIPPABLE))) {
                this.player.networkHandler.sendChatCommand("model reset");
                this.close();
            }
        }

        // Set the model
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.setButtonX && mouseX <= this.setButtonX + 22 && mouseY >= this.setButtonY && mouseY <= this.setButtonY + 22) {
            this.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.UI, 1, 1);
            if (!ItemStack.areItemsAndComponentsEqual(this.original, this.modified)) {
                this.player.networkHandler.sendChatCommand("model set " + this.selectedIdentifier);
                this.close();
            }
        }

        // Close the screen
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= this.closeButtonX && mouseX <= this.closeButtonX + 22 && mouseY >= this.closeButtonY && mouseY <= this.closeButtonY + 22) {
            this.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.UI, 1, 1);
            this.close();
        }

        // Clear the search bar
        if (button == GLFW.GLFW_MOUSE_BUTTON_2 && this.searchBar.isHovered()) {
            this.searchBar.setText("");
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.shouldScroll() && this.canScroll) {
            this.scrollAmount = ((float) mouseY - (float) this.scrollerY - (float) this.SCROLLBAR_HEIGHT / 2) / ((float) (this.scrollerY + this.SCROLLBAR_AREA_HEIGHT - this.scrollerY) - this.SCROLLBAR_HEIGHT);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + 0.5) * this.LIST_COLUMNS;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            if (this.shouldScroll()) {
                this.scrollAmount = MathHelper.clamp(this.scrollAmount - (float) verticalAmount / (float) this.getMaxScroll(), 0.0F, 1.0F);
                this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + 0.5) * this.LIST_COLUMNS;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
