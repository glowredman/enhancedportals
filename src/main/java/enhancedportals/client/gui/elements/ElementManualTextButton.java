package enhancedportals.client.gui.elements;

import java.util.List;

import enhancedportals.client.gui.GuiManual;
import enhancedportals.network.ClientProxy;
import enhancedportals.utility.Localization;

public class ElementManualTextButton extends BaseElement {
    String entry;
    String displayStr;
    boolean tooLong = false;
    int length = 21;

    public ElementManualTextButton(GuiManual gui, int x, int y, String mEntry) {
        super(gui, x, y, 115, 8);
        entry = mEntry;

        if (entry != null) {
            displayStr = Localization.get("manual." + entry + ".title");

            if (displayStr.length() > length) {
                displayStr = displayStr.substring(0, length);

                if (displayStr.endsWith(" "))
                    displayStr = displayStr.substring(0, displayStr.length() - 1);

                displayStr += "...";
                tooLong = true;
            }
        }
    }

    public void updateEntry(String s) {
        entry = s;
        tooLong = false;

        if (entry != null) {
            displayStr = Localization.get("manual." + entry + ".title");

            if (displayStr.length() > length) {
                displayStr = displayStr.substring(0, length);

                if (displayStr.endsWith(" "))
                    displayStr = displayStr.substring(0, displayStr.length() - 1);

                displayStr += "...";
                tooLong = true;
            }
        }
    }

    @Override
    public void addTooltip(List<String> list) {
        if (tooLong)
            list.add(Localization.get("manual." + entry + ".title"));
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    public boolean handleMouseClicked(int x, int y, int mouseButton) {
        if (entry == null)
            return false;

        ClientProxy.manualChangeEntry(entry);
        ((GuiManual) parent).pageChanged();
        return true;
    }

    @Override
    protected void drawContent() {
        if (entry != null) {
            boolean isHovering = intersectsWith(parent.getMouseX(), parent.getMouseY());
            parent.getFontRenderer().drawString(displayStr, posX, posY, (isHovering ? 0xFF0000 : 0x991100));
        }
    }

    @Override
    public void update() {

    }
}
