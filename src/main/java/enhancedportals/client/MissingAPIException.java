package enhancedportals.client;

import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;

public class MissingAPIException extends CustomModLoadingErrorDisplayException {

    private static final long serialVersionUID = 8897558375796606138L;

    private final String[] msg;

    public MissingAPIException(String... msg) {
        super();
        this.msg = msg;
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {}

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        int x = errorScreen.width / 2;
        int y = errorScreen.height / 2 - msg.length * 5;
        for (String s : msg) {
            errorScreen.drawCenteredString(fontRenderer, s, x, y, 0xFFFFFF);
            y += 10;
        }
    }
}
