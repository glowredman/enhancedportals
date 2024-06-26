package enhancedportals.client.gui.tabs;

import enhancedportals.client.gui.BaseGui;
import enhancedportals.network.CommonProxy;
import enhancedportals.tile.TileStabilizerMain;
import enhancedportals.utility.GeneralUtils;
import enhancedportals.utility.Localization;

public class TabRedstoneFlux extends BaseTab {
    TileStabilizerMain stabilizer;

    public TabRedstoneFlux(BaseGui gui, TileStabilizerMain s) {
        super(gui);
        name = "tab.redstoneFlux";
        stabilizer = s;
        maxHeight = 110;
        titleColour = 0xDDDD00;
        backgroundColor = 0xDD6600;
    }

    @Override
    public void drawFullyOpened() {
        int instability = stabilizer.powerState == 0 ? stabilizer.instability : stabilizer.powerState == 1 ? 20 : stabilizer.powerState == 2 ? 50 : 70;
        int powerCost = (int) (stabilizer.intActiveConnections * CommonProxy.CONFIG_REDSTONE_FLUX_COST * GeneralUtils.getPowerMultiplier());
        powerCost -= (int) (powerCost * (instability / 100f));

        parent.getFontRenderer().drawStringWithShadow(Localization.get("tab.redstoneFlux.maxPower"), posX + 10, posY + 20, 0xAAAAAA);
        parent.getFontRenderer().drawString(stabilizer.getEnergyStorage().getMaxEnergyStored() + " RF", posX + 17, posY + 32, 0x000000);

        parent.getFontRenderer().drawStringWithShadow(Localization.get("tab.redstoneFlux.storedPower"), posX + 10, posY + 45, 0xAAAAAA);
        parent.getFontRenderer().drawString(stabilizer.getEnergyStorage().getEnergyStored() + " RF", posX + 17, posY + 57, 0x000000);

        parent.getFontRenderer().drawStringWithShadow(Localization.get("tab.redstoneFlux.powerUsage"), posX + 10, posY + 70, 0xAAAAAA);
        parent.getFontRenderer().drawString(powerCost + " RF/s", posX + 17, posY + 83, 0x000000);
        parent.getFontRenderer().drawString(powerCost / 20 + " RF/t", posX + 17, posY + 94, 0x000000);
    }

    @Override
    public void drawFullyClosed() {

    }
}
