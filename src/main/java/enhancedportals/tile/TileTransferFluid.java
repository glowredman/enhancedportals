package enhancedportals.tile;

import io.netty.buffer.ByteBuf;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
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
        @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputersAPI|Network") })
public class TileTransferFluid extends TileFrameTransfer implements IFluidHandler, IPeripheral, SimpleComponent {
    public FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);

    int tickTimer = 20, time = 0;

    IFluidHandler[] handlers = new IFluidHandler[6];

    boolean cached = false;

    byte outputTracker = 0;

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;

        TileController controller = getPortalController();

        if (stack != null && controller != null && controller.isFinalized())
            if (GeneralUtils.isWrench(stack)) {
                GuiHandler.openGui(player, this, GuiHandler.TRANSFER_FLUID);
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
            return new Object[] { tank.getFluid() != null ? tank.getFluid().getFluid().getName() : "" };
        else if (method == 1)
            return new Object[] { tank.getFluidAmount() };
        else if (method == 2)
            return new Object[] { tank.getFluidAmount() == tank.getCapacity() };
        else if (method == 3)
            return new Object[] { tank.getFluidAmount() == 0 };
        else if (method == 4)
            return new Object[] { isSending };

        return null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void detach(IComputerAccess computer) {

    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid()))
            return null;

        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public String getComponentName() {
        return "ep_transfer_fluid";
    }

    @Callback(direct = true, limit = 1, doc = "function():table -- Get a description of the fluid stored inside the module.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getFluid(Context context, Arguments args) {
        return new Object[] { tank.getInfo() };
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "getFluidStored", "getAmountStored", "isFull", "isEmpty", "isSending" };
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    public String getType() {
        return "ep_transfer_fluid";
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true if the module is set to send fluids.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] isSending(Context context, Arguments args) {
        return new Object[] { isSending };
    }

    @Override
    public void onNeighborChanged() {
        updateFluidHandlers();
    }

    @Override
    public void packetGuiFill(ByteBuf buffer) {
        if (tank.getFluid() != null) {
            buffer.writeBoolean(false);
            buffer.writeInt(tank.getFluid().getFluidID());
            buffer.writeInt(tank.getFluidAmount());
        } else
            buffer.writeBoolean(false);
    }

    @Override
    public void packetGuiUse(ByteBuf buffer) {
        if (buffer.readBoolean())
            tank.setFluid(new FluidStack(FluidRegistry.getFluid(buffer.readInt()), buffer.readInt()));
        else
            tank.setFluid(null);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.writeToNBT(tag);
    }

    void transferFluid(int side) {
        if (handlers[side] == null)
            return;

        tank.drain(handlers[side].fill(ForgeDirection.getOrientation(side).getOpposite(), tank.getFluid(), true), true);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote)
            if (isSending) {
                if (time >= tickTimer) {
                    time = 0;

                    TileController controller = getPortalController();

                    if (controller != null && controller.isPortalActive() && tank.getFluidAmount() > 0) {
                        TileController exitController = (TileController) controller.getDestinationLocation().getTileEntity();

                        if (exitController != null)
                            for (ChunkCoordinates c : exitController.getTransferFluids()) {
                                TileEntity tile = exitController.getWorldObj().getTileEntity(c.posX, c.posY, c.posZ);

                                if (tile != null && tile instanceof TileTransferFluid) {
                                    TileTransferFluid fluid = (TileTransferFluid) tile;

                                    if (!fluid.isSending)
                                        if (fluid.fill(null, tank.getFluid(), false) > 0)
                                            tank.drain(fluid.fill(null, tank.getFluid(), true), true);
                                }

                                if (tank.getFluidAmount() == 0)
                                    break;
                            }
                    }
                }

                time++;
            } else {
                if (!cached)
                    updateFluidHandlers();

                for (int i = outputTracker; i < 6 && tank.getFluidAmount() > 0; i++)
                    transferFluid(i);

                outputTracker++;
                outputTracker = (byte) (outputTracker % 6);
            }
    }

    void updateFluidHandlers() {
        for (int i = 0; i < 6; i++) {
            ChunkCoordinates c = GeneralUtils.offset(getChunkCoordinates(), ForgeDirection.getOrientation(i));
            TileEntity tile = worldObj.getTileEntity(c.posX, c.posY, c.posZ);

            if (tile != null && tile instanceof IFluidHandler) {
                IFluidHandler fluid = (IFluidHandler) tile;

                if (fluid.getTankInfo(ForgeDirection.getOrientation(i).getOpposite()) != null)
                    handlers[i] = fluid;
                else
                    handlers[i] = null;
            } else
                handlers[i] = null;
        }

        cached = true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.readFromNBT(tag);
    }
}
