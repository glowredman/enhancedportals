package enhancedportals.client.gui.elements;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import enhancedportals.client.gui.GuiDialingDevice;
import enhancedportals.portal.GlyphElement;
import enhancedportals.tile.TileDialingDevice;
import enhancedportals.utility.Localization;

public class ElementScrollDiallingDevice extends BaseElement {
    TileDialingDevice dial;
    float currentScroll = 0f;
    boolean isScrolling = false, wasClicking = false;
    int scrollAmount = 0;
    int offsetX = 5, offsetY = 5, sizeMButton = 196, sizeSButton = 20, buttonSpacing = 2, entryHeight = 22;

    public ElementScrollDiallingDevice(GuiDialingDevice gui, TileDialingDevice d, int x, int y) {
        super(gui, x, y, gui.getSizeX() - 14, gui.getSizeY() - 57);
        dial = d;
        texture = gui.getTexture();
    }

    @Override
    public void addTooltip(List<String> list) {
        int x = parent.getMouseX() + parent.getGuiLeft(), y = parent.getMouseY() + parent.getGuiTop();

        for (int i = 0; i < 5; i++) {
            if (scrollAmount + i >= dial.glyphList.size())
                break;

            int entryOffset = i * entryHeight;
            boolean mouseOverEntry = y >= posY + offsetY + entryOffset && y <= posY + offsetY + entryOffset + 20, mouseOverSmall = mouseOverEntry && x >= posX + offsetX + sizeMButton + buttonSpacing && x < posX + offsetX + sizeMButton + buttonSpacing + sizeSButton;

            if (mouseOverSmall)
                if (GuiScreen.isShiftKeyDown()) {
                    list.add(Localization.get("gui.delete"));
                    break;
                } else {
                    list.add(Localization.get("gui.edit"));
                    break;
                }
        }
    }

    @Override
    public boolean handleMouseClicked(int x, int y, int mouseButton) {
        x += parent.getGuiLeft();
        y += parent.getGuiTop();

        for (int i = 0; i < 5; i++) {
            if (scrollAmount + i >= dial.glyphList.size())
                break;

            int entryOffset = i * entryHeight;
            boolean mouseOverEntry = y >= posY + offsetY + entryOffset && y <= posY + offsetY + entryOffset + 20, mouseOverMain = mouseOverEntry && x >= posX + offsetX && x < posX + offsetX + sizeMButton, mouseOverSmall = mouseOverEntry && x >= posX + offsetX + sizeMButton + buttonSpacing && x < posX + offsetX + sizeMButton + buttonSpacing + sizeSButton;

            if (mouseOverMain) {
                ((GuiDialingDevice) parent).onEntrySelected(scrollAmount + i);
                break;
            } else if (mouseOverSmall)
                if (GuiScreen.isShiftKeyDown()) {
                    ((GuiDialingDevice) parent).onEntryDeleted(scrollAmount + i);
                    break;
                } else {
                    ((GuiDialingDevice) parent).onEntryEdited(scrollAmount + i);
                    break;
                }
        }

        return true;
    }

    protected void handleMouse() {
        boolean isMouseDown = Mouse.isButtonDown(0), ignoreScroll = false;
        int mouseX = parent.getMouseX() + parent.getGuiLeft(), mouseY = parent.getMouseY() + parent.getGuiTop();
        int scrollbarX = posX + sizeX - 13, scrollbarY = posY + 1, scrollbarX2 = scrollbarX + 11, scrollbarY2 = scrollbarY + sizeY - 3;

        if (!wasClicking && isMouseDown && mouseX >= scrollbarX && mouseX <= scrollbarX2 && mouseY >= scrollbarY && mouseY <= scrollbarY2)
            isScrolling = true;

        if (!isMouseDown)
            isScrolling = false;

        wasClicking = isMouseDown;

        if (!isScrolling && !isMouseDown && intersectsWith(mouseX - parent.getGuiLeft(), mouseY - parent.getGuiTop())) {
            int wheel = Mouse.getDWheel();

            if (wheel < 0) {
                currentScroll += 0.1;
                isScrolling = ignoreScroll = true;
            } else if (wheel > 0) {
                currentScroll -= 0.1;
                isScrolling = ignoreScroll = true;
            }
        }

        if (isScrolling) {
            if (!ignoreScroll)
                currentScroll = ((mouseY - scrollbarY) - 7.5F) / ((scrollbarY2 - scrollbarY) - 15f);

            if (currentScroll < 0f)
                currentScroll = 0f;
            else if (currentScroll > 1f)
                currentScroll = 1f;

            int items = dial.glyphList.size() - 5 + 1;
            scrollAmount = (int) ((currentScroll * items) + 0.5D);

            if (scrollAmount < 0)
                scrollAmount = 0;

            int max = dial.glyphList.size() - 5;

            if (scrollAmount > max)
                scrollAmount = max;
        }
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawContent() {
        boolean canScroll = false;

        if (dial.glyphList.size() >= 6) {
            canScroll = true;
            handleMouse();
        }

        int l = posY + 1, k = l + sizeY - 1;
        GL11.glColor3f(1f, 1f, 1f);
        parent.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(posX + sizeX - 13, posY + 1 + (int) ((k - l - 16) * currentScroll), 244, 226 + (canScroll ? 0 : 15), 12, 15);

        for (int i = 0; i < 5; i++) {
            if (scrollAmount + i >= dial.glyphList.size())
                break;

            GL11.glColor3f(1f, 1f, 1f);
            GlyphElement e = dial.glyphList.get(scrollAmount + i);
            int state = 1, entryOffset = i * entryHeight, mouseX = parent.getMouseX() + parent.getGuiLeft(), mouseY = parent.getMouseY() + parent.getGuiTop(), fontColour = 0xFFFFFF;
            boolean mouseOverEntry = mouseY >= posY + offsetY + entryOffset && mouseY <= posY + offsetY + entryOffset + 20, mouseOverMain = mouseOverEntry && mouseX >= posX + offsetX && mouseX < posX + offsetX + sizeMButton, mouseOverSmall = mouseOverEntry && mouseX >= posX + offsetX + sizeMButton + buttonSpacing && mouseX < posX + offsetX + sizeMButton + buttonSpacing + sizeSButton, delete = GuiScreen.isShiftKeyDown();

            if (dial.getPortalController().isPortalActive())
                state = 0;

            if (state == 1 && mouseOverMain)
                state = 2;

            parent.getTextureManager().bindTexture(texture);
            parent.drawTexturedModalRect(posX + offsetX, posY + entryOffset + offsetY, 0, 196 + (state * 20), sizeMButton, 20);
            parent.drawTexturedModalRect(posX + offsetX + sizeMButton + buttonSpacing, posY + entryOffset + offsetY, 196 + (delete ? 20 : 0), 216 + (mouseOverSmall ? 20 : 0), sizeSButton, 20);

            parent.getFontRenderer().drawStringWithShadow(e.name, posX + offsetX + 5, posY + offsetY + 6 + entryOffset, fontColour);
        }
    }

    @Override
    public void update() {

    }
}
