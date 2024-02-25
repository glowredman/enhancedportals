package enhancedportals.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class GuiBetterSlider extends GuiButton {
    public float sliderValue = 1.0F;
    public boolean dragging;

    public GuiBetterSlider(int id, int x, int y, String displayText, float initialValue) {
        super(id, x, y, 150, 20, displayText);
        sliderValue = initialValue;
    }

    public GuiBetterSlider(int id, int x, int y, String displayText, float initialValue, int w) {
        super(id, x, y, w, 20, displayText);
        sliderValue = initialValue;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            if (dragging) {
                sliderValue = (float) (mouseX - (xPosition + 4)) / (float) (width - 8);

                if (sliderValue < 0.0F)
                    sliderValue = 0.0F;

                if (sliderValue > 1.0F)
                    sliderValue = 1.0F;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(xPosition + (int) (sliderValue * (width - 8)), yPosition, 0, 66, 4, height);
            drawTexturedModalRect(xPosition + (int) (sliderValue * (width - 8)) + 4, yPosition, 196, 66, 4, height);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent e).
     */
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            sliderValue = (float) (mouseX - (xPosition + 4)) / (float) (width - 8);

            if (sliderValue < 0.0F)
                sliderValue = 0.0F;

            if (sliderValue > 1.0F)
                sliderValue = 1.0F;

            dragging = true;
            return true;
        } else
            return false;
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            drawTexturedModalRect(xPosition, yPosition, 0, 46, width / 2, height);
            drawTexturedModalRect(xPosition + width / 2, yPosition, 199 - width / 2, 46, 1 + width / 2, height);
            mouseDragged(mc, mouseX, mouseY);
            int l = 0xE0E0E0;

            if (!enabled)
                l = 0xFFA0A0A0;
            else if (field_146123_n)
                l = 0xFFFFA0;

            drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, l);
        }
    }
}
