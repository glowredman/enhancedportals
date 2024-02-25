package enhancedportals.tile;

import io.netty.buffer.ByteBuf;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.network.ByteBufUtils;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhancedportals.network.GuiHandler;
import enhancedportals.utility.GeneralUtils;

@InterfaceList(value = {
        @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft|API|Peripheral"),
        @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputersAPI|Network") })
public class TileTransferItem extends TileFrameTransfer implements IInventory, IPeripheral, SimpleComponent {
    ItemStack stack;

    int tickTimer = 20, time = 0;

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        TileController controller = getPortalController();

        if (GeneralUtils.isWrench(stack) && controller != null && controller.isFinalized()) {
            GuiHandler.openGui(player, this, GuiHandler.TRANSFER_ITEM);
            return true;
        }

        return false;
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void attach(IComputerAccess computer) {

    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
    @Method(modid = "ComputerCraft|API|Peripheral")
        if (method == 0)
            return new Object[] { stack != null ? Item.getIdFromItem(stack.getItem()) : 0 };
        else if (method == 1)
            return new Object[] { stack != null ? stack.stackSize : 0 };
        else if (method == 2)
            return new Object[] { stack != null };
        else if (method == 3)
            return new Object[] { isSending };

        return null;
    }

    @Override
    public void closeInventory() {

    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack stack = getStackInSlot(i);

        if (stack != null)
            if (stack.stackSize <= j)
                setInventorySlotContents(i, null);
            else {
                stack = stack.splitStack(j);

                if (stack.stackSize == 0)
                    setInventorySlotContents(i, null);
            }

        return stack;
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void detach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    public String getComponentName() {
        return "ep_transfer_item";
    }

    @Override
    public String getInventoryName() {
        return "tile.frame.item.name";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "getItemStored", "getAmountStored", "hasStack", "isSending" };
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Callback(direct = true, doc = "function():table -- Returns a description of the item stored in this module.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getStack(Context context, Arguments args) {
        return new Object[] { stack.copy() };
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return stack;
    }

    @Override
    public String getType() {
        return "ep_transfer_item";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Callback(direct = true, doc = "function():boolean -- Return whether there is an item stored in this module.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] hasStack(Context context, Arguments args) {
        return new Object[] { stack != null };
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true if the module is set to send items.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] isSending(Context context, Arguments args) {
        return new Object[] { isSending };
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void packetGuiFill(ByteBuf buffer) {
        if (stack != null) {
            buffer.writeBoolean(true);
            ByteBufUtils.writeItemStack(buffer, stack);
        } else
            buffer.writeBoolean(false);
    }

    @Override
    public void packetGuiUse(ByteBuf buffer) {
        if (buffer.readBoolean())
            stack = ByteBufUtils.readItemStack(buffer);
        else
            stack = null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        stack = itemstack;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote)
            if (isSending) {
                if (time >= tickTimer) {
                    time = 0;

                    TileController controller = getPortalController();

                    if (controller != null && controller.isPortalActive() && stack != null) {
                        TileController exitController = (TileController) controller.getDestinationLocation().getTileEntity();

                        if (exitController != null)
                            for (ChunkCoordinates c : exitController.getTransferItems()) {
                                TileEntity tile = exitController.getWorldObj().getTileEntity(c.posX, c.posY, c.posZ);

                                if (tile != null && tile instanceof TileTransferItem) {
                                    TileTransferItem item = (TileTransferItem) tile;

                                    if (!item.isSending)
                                        if (item.getStackInSlot(0) == null) {
                                            item.setInventorySlotContents(0, stack);
                                            item.markDirty();
                                            stack = null;
                                            markDirty();
                                        } else if (item.getStackInSlot(0).getItem() == stack.getItem()) {
                                            int amount = 0;

                                            if (item.getStackInSlot(0).stackSize + stack.stackSize <= stack.getMaxStackSize())
                                                amount = stack.stackSize;
                                            else
                                                amount = stack.stackSize - (item.getStackInSlot(0).stackSize + stack.stackSize - 64);

                                            if (amount <= 0)
                                                continue;

                                            item.getStackInSlot(0).stackSize += amount;
                                            item.markDirty();

                                            if (amount == stack.stackSize)
                                                stack = null;
                                            else
                                                stack.stackSize -= amount;

                                            markDirty();
                                        }
                                }

                                if (stack == null)
                                    break;
                            }
                    }
                }

                time++;
            }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound st = new NBTTagCompound();

        if (stack != null)
            stack.writeToNBT(st);

        tag.setTag("stack", st);
    }
}
