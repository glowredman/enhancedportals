package enhancedportals.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import enhancedportals.block.BlockDecorBorderedQuartz;
import enhancedportals.block.BlockDecorEnderInfusedMetal;
import enhancedportals.block.BlockFrame;
import enhancedportals.block.BlockStabilizer;
import enhancedportals.item.ItemBlankPortalModule;
import enhancedportals.item.ItemBlankUpgrade;
import enhancedportals.item.ItemGlasses;
import enhancedportals.item.ItemLocationCard;
import enhancedportals.item.ItemManual;
import enhancedportals.item.ItemNanobrush;
import enhancedportals.item.ItemPortalModule;
import enhancedportals.item.ItemUpgrade;
import enhancedportals.item.ItemWrench;

public class Vanilla {
    public static void registerRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 4, 0), "SIS", "IQI", "SIS", 'S', Blocks.stone, 'Q', Blocks.quartz_block, 'I', Items.iron_ingot));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.REDSTONE_INTERFACE), " R ", "RFR", " R ", 'F', new ItemStack(BlockFrame.instance, 1, 0), 'R', Items.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 0), " R ", "RFR", " R ", 'F', new ItemStack(ItemBlankUpgrade.instance), 'R', Items.redstone));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 1), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockStabilizer.instance, 6), "QPQ", "PDP", "QPQ", 'D', Items.diamond, 'Q', Blocks.iron_block, 'P', Items.ender_pearl));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.PORTAL_CONTROLLER), new ItemStack(BlockFrame.instance, 1, 0), Items.diamond));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.DIALLING_DEVICE), new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), Items.diamond));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.MODULE_MANIPULATOR), new ItemStack(BlockFrame.instance, 1, 0), Items.diamond, Items.emerald, new ItemStack(ItemBlankPortalModule.instance)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ENERGY), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_FLUID), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Items.bucket));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ITEM), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Blocks.chest));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 2), new ItemStack(ItemUpgrade.instance, 1, 1), Items.diamond));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 4), new ItemStack(ItemBlankUpgrade.instance), Items.diamond, Items.emerald, new ItemStack(ItemBlankPortalModule.instance)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 7), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 5), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Items.bucket));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 6), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Blocks.chest));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockDecorBorderedQuartz.instance, 9), "SQS", "QQQ", "SQS", 'S', Blocks.stone, 'Q', Blocks.quartz_block));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockDecorEnderInfusedMetal.instance, 9), Blocks.iron_block, Items.iron_ingot, Items.iron_ingot, Items.ender_pearl, Items.ender_pearl));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemBlankPortalModule.instance), true, "NNN", "NIN", "NNN", 'I', Items.iron_ingot, 'N', Items.gold_nugget));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemBlankUpgrade.instance, 8, 0), "D", "P", "R", 'P', Items.paper, 'D', Items.diamond, 'R', "dyeRed"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 0), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.redstone), new ItemStack(Items.gunpowder)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 1), new ItemStack(ItemBlankPortalModule.instance), "dyeRed", "dyeBlue", "dyeGreen"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 2), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.redstone), new ItemStack(Blocks.noteblock)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 3), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Blocks.anvil), new ItemStack(Items.feather)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 5), new ItemStack(ItemBlankPortalModule.instance), "dyeWhite", "dyeBlack"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 6), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.compass)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 7), "FFF", "FXF", "FFF", 'X', new ItemStack(ItemBlankPortalModule.instance), 'F', new ItemStack(Items.feather)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemManual.instance), new ItemStack(Items.book), new ItemStack(ItemLocationCard.instance)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemNanobrush.instance), "WT ", "TS ", "  S", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemNanobrush.instance), " TW", " ST", "S  ", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemLocationCard.instance, 16), "IPI", "PPP", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', "dyeBlue"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemWrench.instance), "I I", " Q ", " I ", 'I', Items.iron_ingot, 'Q', Items.quartz));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemGlasses.instance), true, "R B", "GLG", "L L", 'R', "dyeRed", 'B', "dyeCyan", 'G', Blocks.glass_pane, 'L', Items.leather));
    }
}
