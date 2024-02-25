package enhancedportals.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import enhancedportals.item.ItemLocationCard;
import enhancedportals.utility.GeneralUtils;

public class SlotDBS extends Slot {
    public SlotDBS(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }

    @Override
    public boolean isItemValid(ItemStack s) {
        return s == null || GeneralUtils.isEnergyContainerItem(s) || s.getItem() == ItemLocationCard.instance;
    }
}
