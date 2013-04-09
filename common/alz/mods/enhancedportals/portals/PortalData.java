package alz.mods.enhancedportals.portals;

import alz.mods.enhancedportals.teleportation.TeleportData;

public class PortalData
{
    public String DisplayName;
    public PortalTexture Texture;
    public TeleportData TeleportData;
    public int Frequency;

    public PortalData()
    {
        DisplayName = "";
        TeleportData = null;
        Frequency = 0;
        Texture = PortalTexture.PURPLE;
    }

    public PortalData(String displayName, int texture, int frequency)
    {
        DisplayName = displayName;
        Texture = PortalTexture.getPortalTexture(texture);
        Frequency = frequency;
    }

    public PortalData(String displayName, int texture, TeleportData data)
    {
        DisplayName = displayName;
        Texture = PortalTexture.getPortalTexture(texture);
        TeleportData = data;
    }

    public PortalData(String displayName, PortalTexture texture, int frequency)
    {
        DisplayName = displayName;
        Texture = texture;
        Frequency = frequency;
    }

    public PortalData(String displayName, PortalTexture texture, TeleportData data)
    {
        DisplayName = displayName;
        Texture = texture;
        TeleportData = data;
    }

    public boolean equals(PortalData data)
    {
        if (data.DisplayName.equalsIgnoreCase(DisplayName) && data.Frequency == Frequency && data.Texture == Texture && data.TeleportData.equals(TeleportData))
        {
            return true;
        }

        return false;
    }

    public String GetTextureAsString()
    {
        return PortalTexture.getLocalizedName(Texture);
    }

    public boolean UseTeleportData()
    {
        return TeleportData != null;
    }
}