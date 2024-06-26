package enhancedportals.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import enhancedportals.EnhancedPortals;
import enhancedportals.block.BlockPortal;
import enhancedportals.item.ItemNanobrush;
import enhancedportals.network.ClientProxy;
import enhancedportals.network.GuiHandler;
import enhancedportals.utility.GeneralUtils;

public class TilePortal extends TilePortalPart {
    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        TileController controller = getPortalController();

        if (stack != null && controller != null && controller.isFinalized())
            if (GeneralUtils.isWrench(stack)) {
                GuiHandler.openGui(player, controller, GuiHandler.PORTAL_CONTROLLER_A);
                return true;
            } else if (stack.getItem() == ItemNanobrush.instance) {
                GuiHandler.openGui(player, controller, player.isSneaking() ? GuiHandler.TEXTURE_C : GuiHandler.TEXTURE_B);
                return true;
            }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    public IIcon getBlockTexture(int side) {
        TileController controller = getPortalController();

        if (controller != null) {
            if (controller.activeTextureData.hasCustomPortalTexture() && ClientProxy.customPortalTextures.size() > controller.activeTextureData.getCustomPortalTexture() && ClientProxy.customPortalTextures.get(controller.activeTextureData.getCustomPortalTexture()) != null)
                return ClientProxy.customPortalTextures.get(controller.activeTextureData.getCustomPortalTexture());
            else if (controller.activeTextureData.getPortalItem() != null && controller.activeTextureData.getPortalItem().getItem() instanceof ItemBlock)
                return Block.getBlockFromItem(controller.activeTextureData.getPortalItem().getItem()).getIcon(side, controller.activeTextureData.getPortalItem().getItemDamage());
        } else if (portalController != null)
            EnhancedPortals.proxy.waitForController(new ChunkCoordinates(portalController.posX, portalController.posY, portalController.posZ), getChunkCoordinates());

        return BlockPortal.instance.getIcon(side, 0);
    }

    public int getColour() {
        TileController controller = getPortalController();

        if (controller != null)
            return controller.activeTextureData.getPortalColour();
        else if (portalController != null)
            EnhancedPortals.proxy.waitForController(new ChunkCoordinates(portalController.posX, portalController.posY, portalController.posZ), getChunkCoordinates());

        return 0xFFFFFF;
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }

    public void onEntityCollidedWithBlock(Entity entity) {
        TileController controller = getPortalController();

        if (controller != null)
            controller.onEntityEnterPortal(entity, this);
    }
}
