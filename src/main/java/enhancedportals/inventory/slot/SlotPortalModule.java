package enhancedportals.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import enhancedportals.utility.IPortalModule;

public class SlotPortalModule extends Slot {
    public SlotPortalModule(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }

    @Override
    public boolean isItemValid(ItemStack s) {
        return s == null || s.getItem() instanceof IPortalModule;
    }
}
