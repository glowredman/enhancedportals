package enhancedportals.client.gui.tabs;

import java.util.List;

import enhancedportals.client.gui.BaseGui;
import enhancedportals.utility.Localization;

public class TabTip extends BaseTab {
    String tip;

    public TabTip(BaseGui gui, String n) {
        super(gui, 0);
        name = "tab." + n;
        tip = Localization.get("tab." + n + ".info").replace("<NL>", "\n").replaceAll("<([^<]*)>", "\u00A7$1");
        List<String> l = gui.getFontRenderer().listFormattedStringToWidth(tip, maxWidth - 14);
        maxHeight = l.size() * gui.getFontRenderer().FONT_HEIGHT + 25;
        backgroundColor = 0x33AA00;
    }

    @Override
    public void drawFullyOpened() {
        parent.getFontRenderer().drawSplitString(tip, posX - maxWidth + 7, posY + 20, maxWidth - 14, 0x000000);
    }

    @Override
    public void drawFullyClosed() {

    }
}
