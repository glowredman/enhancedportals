package enhancedportals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhancedportals.client.gui.BaseGui;
import enhancedportals.client.gui.GuiTextureParticle;
import enhancedportals.tile.TileController;

public class ContainerTextureParticle extends BaseContainer {
    protected TileController controller;

    public ContainerTextureParticle(TileController c, InventoryPlayer p) {
        super(null, p, GuiTextureParticle.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize, 7);
        controller = c;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("colour"))
            controller.setParticleColour(tag.getInteger("colour"));
        else if (tag.hasKey("type"))
            controller.setParticleType(tag.getInteger("type"));
    }
}
