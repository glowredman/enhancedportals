package enhancedportals.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import enhancedportals.inventory.ContainerModuleManipulator;
import enhancedportals.tile.TilePortalManipulator;
import enhancedportals.utility.Localization;

public class GuiModuleManipulator extends BaseGui {
    public static final int CONTAINER_SIZE = 53;
    TilePortalManipulator module;

    public GuiModuleManipulator(TilePortalManipulator m, EntityPlayer p) {
        super(new ContainerModuleManipulator(m, p.inventory), CONTAINER_SIZE);
        module = m;
        name = "gui.moduleManipulator";
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        getFontRenderer().drawString(Localization.get("gui.modules"), 8, containerSize - 35, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        mc.renderEngine.bindTexture(playerInventoryTexture);
        drawTexturedModalRect(guiLeft + 7, guiTop + containerSize - 25, 7, 7, 162, 18);
    }
}
