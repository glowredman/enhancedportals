package enhancedportals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import enhancedportals.client.gui.BaseGui;
import enhancedportals.client.gui.GuiTexturePortal;
import enhancedportals.tile.TileController;

public class ContainerTexturePortal extends BaseContainer {
    protected TileController controller;

    public ContainerTexturePortal(TileController c, InventoryPlayer p) {
        super(null, p, GuiTexturePortal.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize, 7);
        controller = c;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("colour"))
            controller.setPortalColour(tag.getInteger("colour"));
        else if (tag.hasKey("custom"))
            controller.setCustomPortalTexture(tag.getInteger("custom"));
        else if (tag.hasKey("removeItem"))
            controller.setPortalItem(null);
        else if (tag.hasKey("id") && tag.hasKey("Damage"))
            controller.setPortalItem(ItemStack.loadItemStackFromNBT(tag));
    }
}
