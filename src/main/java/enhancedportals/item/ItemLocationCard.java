package enhancedportals.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import enhancedportals.network.CommonProxy;
import enhancedportals.utility.DimensionCoordinates;

public class ItemLocationCard extends Item {
    public static ItemLocationCard instance;

    public static void clearDBSLocation(ItemStack s) {
        s.setTagCompound(null);
    }

    public static DimensionCoordinates getDBSLocation(ItemStack s) {
        if (hasDBSLocation(s)) {
            NBTTagCompound t = s.getTagCompound();
            return new DimensionCoordinates(t.getInteger("X"), t.getInteger("Y"), t.getInteger("Z"), t.getInteger("D"));
        }

        return null;
    }

    public static boolean hasDBSLocation(ItemStack s) {
        return s.hasTagCompound();
    }

    public static void setDBSLocation(ItemStack s, DimensionCoordinates w) {
        NBTTagCompound t = new NBTTagCompound();
        t.setInteger("X", w.posX);
        t.setInteger("Y", w.posY);
        t.setInteger("Z", w.posZ);
        t.setInteger("D", w.dimension);

        s.setTagCompound(t);
    }

    IIcon texture;

    public ItemLocationCard(String n) {
        super();
        instance = this;
        setCreativeTab(CommonProxy.creativeTab);
        setUnlocalizedName(n);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips) {
        DimensionCoordinates w = getDBSLocation(stack);

        if (w != null)
            list.add("Location set");
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return texture;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking() && hasDBSLocation(stack)) {
            clearDBSLocation(stack);
            return stack;
        }

        return stack;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        texture = register.registerIcon("enhancedportals:location_card");
    }
}
