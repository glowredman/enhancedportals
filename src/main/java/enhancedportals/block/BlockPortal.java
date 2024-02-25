package enhancedportals.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enhancedportals.client.PortalParticleFX;
import enhancedportals.client.PortalRenderer;
import enhancedportals.item.ItemPortalModule;
import enhancedportals.network.ClientProxy;
import enhancedportals.network.CommonProxy;
import enhancedportals.portal.EntityManager;
import enhancedportals.tile.TileController;
import enhancedportals.tile.TilePortal;
import enhancedportals.tile.TilePortalManipulator;

public class BlockPortal extends BlockContainer {
    public static BlockPortal instance;
    IIcon texture;

    public BlockPortal(String n) {
        super(Material.portal);
        instance = this;
        setBlockUnbreakable();
        setResistance(2000);
        setBlockName(n);
        setLightOpacity(0);
        setStepSound(soundTypeGlass);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int newID) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            ((TilePortal) tile).breakBlock(b, newID);

        super.breakBlock(world, x, y, z, b, newID);
    }

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).getColour();

        return 0xFFFFFF;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TilePortal();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).getBlockTexture(side);

        return null;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return texture;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 14;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int getRenderType() {
        return PortalRenderer.ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TilePortal)
            return ((TilePortal) tile).activate(player, player.inventory.getCurrentItem());

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            if (EntityManager.isEntityFitForTravel(entity)) {
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).closeScreen();

                TileEntity t = world.getTileEntity(x, y, z);

                if (t instanceof TilePortal)
                    ((TilePortal) t).onEntityCollidedWithBlock(entity);
            }

            EntityManager.setEntityPortalCooldown(entity);
        }
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (CommonProxy.CONFIG_DISABLE_SOUNDS && CommonProxy.CONFIG_DISABLE_PARTICLES)
            return;

        TileEntity tile = world.getTileEntity(x, y, z);

        if (!(tile instanceof TilePortal))
            return;

        int metadata = world.getBlockMetadata(x, y, z);
        TileController controller = ((TilePortal) tile).getPortalController();
        TilePortalManipulator module = controller == null ? null : controller.getModuleManipulator();
        boolean doSounds = !CommonProxy.CONFIG_DISABLE_SOUNDS && random.nextInt(100) == 0, doParticles = !CommonProxy.CONFIG_DISABLE_PARTICLES;

        if (module != null) {
            if (doSounds)
                doSounds = !module.hasModule(ItemPortalModule.PortalModules.REMOVE_SOUNDS.getUniqueID());

            doParticles = !module.hasModule(ItemPortalModule.PortalModules.REMOVE_PARTICLES.getUniqueID());
        }

        if (doSounds)
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 0.5F, random.nextFloat() * 0.4F + 0.8F, false);

        if (doParticles)
            for (int l = 0; l < 4; ++l) {
                double d0 = x + random.nextFloat();
                double d1 = y + random.nextFloat();
                double d2 = z + random.nextFloat();
                double d3 = 0.0D;
                double d4 = 0.0D;
                double d5 = 0.0D;
                int i1 = random.nextInt(2) * 2 - 1;
                d3 = (random.nextFloat() - 0.5D) * 0.5D;
                d4 = (random.nextFloat() - 0.5D) * 0.5D;
                d5 = (random.nextFloat() - 0.5D) * 0.5D;

                if (metadata == 1) {
                    d2 = z + 0.5D + 0.25D * i1;
                    d5 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 2) {
                    d0 = x + 0.5D + 0.25D * i1;
                    d3 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 3) {
                    d1 = y + 0.5D + 0.25D * i1;
                    d4 = random.nextFloat() * 2.0F * i1;
                } else if (metadata == 4)
                    d3 = d5 = random.nextFloat() * 2F * i1;
                else if (metadata == 5) {
                    d3 = d5 = random.nextFloat() * 2F * i1;
                    d3 = -d3;
                }

                PortalParticleFX fx = new PortalParticleFX(world, controller, d0, d1, d2, d3, d4, d5);

                if (module != null)
                    module.particleCreated(fx);

                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        texture = iconRegister.registerIcon("enhancedportals:portal");
        int counter = 0;
        ClientProxy.customPortalTextures.clear();

        while (ClientProxy.resourceExists("textures/blocks/customPortal/" + String.format("%02d", counter) + ".png")) {
            CommonProxy.logger.debug("Registered custom portal Icon: " + String.format("%02d", counter) + ".png");
            ClientProxy.customPortalTextures.add(iconRegister.registerIcon("enhancedportals:customPortal/" + String.format("%02d", counter)));
            counter++;
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);

        if (tile instanceof TilePortal) {
            TilePortal portal = (TilePortal) tile;
            TileController controller = portal.getPortalController();
            TilePortalManipulator manip = controller == null ? null : controller.getModuleManipulator();

            if (controller != null && manip != null && manip.isPortalInvisible()) {
                setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
                return;
            }

            int meta = blockAccess.getBlockMetadata(x, y, z);

            if (meta == 1)
                setBlockBounds(0f, 0f, 0.375f, 1f, 1f, 0.625f);
            else if (meta == 2)
                setBlockBounds(0.375f, 0f, 0f, 0.625f, 1f, 1f);
            else if (meta == 3)
                setBlockBounds(0, 0.375f, 0f, 1f, 0.625f, 1f);
            else
                setBlockBounds(0f, 0f, 0f, 1f, 1, 1f);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (blockAccess.getBlock(x, y, z) == this || blockAccess.getBlock(x, y, z) == BlockFrame.instance)
            return false;

        return super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }
}
