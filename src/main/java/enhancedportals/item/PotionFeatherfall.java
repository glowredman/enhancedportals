package enhancedportals.item;

import net.minecraft.potion.Potion;

public class PotionFeatherfall extends Potion {
    public PotionFeatherfall(int id, boolean isBadEffect, int liquidColor) {
        super(id, isBadEffect, liquidColor);
        setIconIndex(0, 0);
        setPotionName("potion.featherfall");
    }
}
