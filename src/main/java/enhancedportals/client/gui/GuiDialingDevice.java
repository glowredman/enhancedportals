package enhancedportals.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import enhancedportals.EnhancedPortals;
import enhancedportals.client.gui.elements.ElementScrollDiallingDevice;
import enhancedportals.client.gui.tabs.TabTip;
import enhancedportals.inventory.ContainerDialingDevice;
import enhancedportals.network.ClientProxy;
import enhancedportals.network.GuiHandler;
import enhancedportals.network.packet.PacketGuiData;
import enhancedportals.network.packet.PacketRequestGui;
import enhancedportals.tile.TileController;
import enhancedportals.tile.TileDialingDevice;
import enhancedportals.utility.Localization;

public class GuiDialingDevice extends BaseGui {
    public static final int CONTAINER_SIZE = 175, CONTAINER_WIDTH = 256;
    TileDialingDevice dial;
    TileController controller;
    GuiButton buttonDial;

    public GuiDialingDevice(TileDialingDevice d, EntityPlayer p) {
        super(new ContainerDialingDevice(d, p.inventory), CONTAINER_SIZE);
        texture = new ResourceLocation("enhancedportals", "textures/gui/dialling_device.png");
        xSize = CONTAINER_WIDTH;
        dial = d;
        controller = dial.getPortalController();
        name = "gui.dialDevice";
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonDial = new GuiButton(1, guiLeft + xSize - 147, guiTop + ySize - 27, 140, 20, Localization.get("gui.terminate"));
        buttonDial.enabled = controller.isPortalActive();
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + ySize - 27, 100, 20, Localization.get("gui.manualEntry")));
        buttonList.add(buttonDial);

        addElement(new ElementScrollDiallingDevice(this, dial, 7, 28));
        addTab(new TabTip(this, "dialling"));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        buttonDial.enabled = controller.isPortalActive();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        getFontRenderer().drawString(Localization.get("gui.storedIdentifiers"), 7, 18, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0)
            EnhancedPortals.packetPipeline.sendToServer(new PacketRequestGui(dial, GuiHandler.DIALING_DEVICE_B));
        else if (button.id == 1)
            if (controller.isPortalActive()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("terminate", true);
                EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
            }
    }

    public void onEntrySelected(int entry) {
        if (!controller.isPortalActive()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dial", entry);
            EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    public void onEntryEdited(int entry) {
        ClientProxy.editingID = entry;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("edit", entry);
        EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    public void onEntryDeleted(int entry) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("delete", entry);
        EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
    }
}
