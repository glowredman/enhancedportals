package enhancedportals.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.Optional.Interface;
import enhancedportals.network.CommonProxy;
import enhancedportals.utility.IDismantleable;

@Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraftAPI|tools")
public class ItemWrench extends Item implements IToolWrench {
    public static ItemWrench instance;

    IIcon texture;

    public ItemWrench(String n) {
        super();
        instance = this;
        setCreativeTab(CommonProxy.creativeTab);
        setUnlocalizedName(n);
        setMaxStackSize(1);
    }

    @Override
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return texture;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && player.isSneaking()) {
            Block block = world.getBlock(x, y, z);

            if (block instanceof IDismantleable) {
                ((IDismantleable) block).dismantleBlock(player, world, x, y, z);
                return true;
            }
        }

        return false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        texture = register.registerIcon("enhancedportals:wrench");
    }

    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {}
}
