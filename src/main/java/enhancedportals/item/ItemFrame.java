package enhancedportals.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import enhancedportals.block.BlockFrame;
import enhancedportals.utility.Localization;

public class ItemFrame extends ItemBlockWithMetadata {
    public static String[] unlocalizedName = new String[] { "frame", "controller", "redstone", "network_interface", "dial_device", "program_interface", "upgrade", "fluid", "item", "energy" };

    public ItemFrame(Block b) {
        super(b, BlockFrame.instance);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips) {
        int damage = stack.getItemDamage();

        if (damage > 0)
            list.add(Localization.get("block.portalFramePart"));
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < BlockFrame.FRAME_TYPES; i++)
            list.add(new ItemStack(item, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + unlocalizedName[stack.getItemDamage()];
    }
}
