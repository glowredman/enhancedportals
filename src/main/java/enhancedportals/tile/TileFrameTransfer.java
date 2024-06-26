package enhancedportals.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileFrameTransfer extends TileFrame {
    public boolean isSending = true;

    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }

    public void onNeighborChanged() {

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        isSending = tag.getBoolean("Sending");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Sending", isSending);
    }
}
