package enhancedportals.tile;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhancedportals.item.ItemNanobrush;
import enhancedportals.network.GuiHandler;
import enhancedportals.utility.GeneralUtils;

@InterfaceList(value = {
        @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft|API|Peripheral"),
        @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputersAPI|Network")})
public class TileTransferEnergy extends TileFrameTransfer implements IEnergyHandler, IPeripheral, SimpleComponent {
    public final EnergyStorage storage = new EnergyStorage(16000);
    // public final PowerHandler mjHandler;

    int tickTimer = 20, time = 0;

    IEnergyHandler[] handlers = new IEnergyHandler[6];

    boolean cached = false;

    byte outputTracker = 0;

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        TileController controller = getPortalController();

        if (stack != null && controller != null && controller.isFinalized())
            if (GeneralUtils.isWrench(stack)) {
                GuiHandler.openGui(player, this, GuiHandler.TRANSFER_ENERGY);
                return true;
            } else if (stack.getItem() == ItemNanobrush.instance) {
                GuiHandler.openGui(player, controller, GuiHandler.TEXTURE_A);
                return true;
            }

        return false;
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        if (method == 0)
            return new Object[] { storage.getEnergyStored() };
        else if (method == 1)
            return new Object[] { storage.getEnergyStored() == storage.getMaxEnergyStored() };
        else if (method == 2)
            return new Object[] { storage.getEnergyStored() == 0 };
        else if (method == 3)
            return new Object[] { isSending };

        return null;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
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
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public String getComponentName() {
        return "ep_transfer_energy";
    }

    @Callback(direct = true, doc = "function():number -- Returns the amount of energy stored inside the module.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getEnergy(Context context, Arguments args) {
        return new Object[] { storage.getEnergyStored() };
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Callback(direct = true, doc = "function():number -- Get the maximum amount of energy stored inside the module.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getMaxEnergy(Context context, Arguments args) {
        return new Object[] { storage.getMaxEnergyStored() };
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "getEnergyStored", "isFull", "isEmpty", "isSending" };
    }

    @Override
    public String getType() {
        return "ep_transfer_energy";
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true if the module is set to send energy.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] isSending(Context context, Arguments args) {
        return new Object[] { isSending };
    }

    @Override
    public void onNeighborChanged() {
        updateEnergyHandlers();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storage.readFromNBT(nbt);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    void transferEnergy(int side) {
        if (handlers[side] == null)
            return;

        storage.extractEnergy(handlers[side].receiveEnergy(ForgeDirection.getOrientation(side).getOpposite(), storage.getEnergyStored(), false), false);
    }

    void updateEnergyHandlers() {
        for (int i = 0; i < 6; i++) {
            ChunkCoordinates c = GeneralUtils.offset(getChunkCoordinates(), ForgeDirection.getOrientation(i));
            TileEntity tile = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (tile != null && tile instanceof IEnergyHandler) {
                IEnergyHandler energy = (IEnergyHandler) tile;

                if (energy.canConnectEnergy(ForgeDirection.getOrientation(i).getOpposite()))
                    handlers[i] = energy;
                else
                    handlers[i] = null;
            } else
                handlers[i] = null;
        }

        cached = true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote)
            if (isSending) {
                if (time >= tickTimer) {
                    time = 0;

                    TileController controller = getPortalController();

                    if (controller != null && controller.isPortalActive() && storage.getEnergyStored() > 0) {
                        TileController exitController = (TileController) controller.getDestinationLocation().getTileEntity();

                        if (exitController != null)
                            for (ChunkCoordinates c : exitController.getTransferEnergy()) {
                                TileEntity tile = exitController.getWorldObj().getTileEntity(c.posX, c.posY, c.posZ);

                                if (tile != null && tile instanceof TileTransferEnergy) {
                                    TileTransferEnergy energy = (TileTransferEnergy) tile;

                                    if (!energy.isSending)
                                        if (energy.receiveEnergy(null, storage.getEnergyStored(), true) > 0)
                                            storage.extractEnergy(energy.receiveEnergy(null, storage.getEnergyStored(), false), false);
                                }

                                if (storage.getEnergyStored() == 0)
                                    break;
                            }
                    }
                }

                time++;
            } else {
                if (!cached)
                    updateEnergyHandlers();

                for (int i = outputTracker; i < 6 && storage.getEnergyStored() > 0; i++)
                    transferEnergy(i);

                outputTracker++;
                outputTracker = (byte) (outputTracker % 6);
            }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);
    }
}
