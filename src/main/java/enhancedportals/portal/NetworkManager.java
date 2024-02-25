package enhancedportals.portal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import enhancedportals.EnhancedPortals;
import enhancedportals.network.CommonProxy;
import enhancedportals.tile.TileController;
import enhancedportals.utility.DimensionCoordinates;

public class NetworkManager {
    /*** Stores locations of all portals ***/
    HashMap<String, DimensionCoordinates> portalCoordinates;

    /*** Reverse lookup for {@link portalCoordinates} ***/
    HashMap<DimensionCoordinates, String> portalCoordinatesReverse;

    /***
     * Portal Identifier, Network Identifier. Used for looking up which portal is in which network, quickly.
     ***/
    HashMap<String, String> portalNetworks;

    /***
     * Network Identifier, Portal Identifier List. Used for looking up all portals in a network, without searching every entry in {@link portalNetworks}
     ***/
    HashMap<String, ArrayList<String>> networkedPortals;

    File portalFile, networkFile;
    MinecraftServer server;

    public NetworkManager(FMLServerStartingEvent event) {
        portalCoordinates = new HashMap<String, DimensionCoordinates>();
        portalCoordinatesReverse = new HashMap<DimensionCoordinates, String>();
        portalNetworks = new HashMap<String, String>();
        networkedPortals = new HashMap<String, ArrayList<String>>();
        server = event.getServer();
        portalFile = new File(EnhancedPortals.proxy.getWorldDir(), "EP3_PortalLocations.json");
        networkFile = new File(EnhancedPortals.proxy.getWorldDir(), "EP3_PortalNetworks.json");

        try {
            loadAllData();
        } catch (Exception e) {
            CommonProxy.logger.catching(e);
            e.printStackTrace();
        }
    }

    /***
     * Creates a new network if one does not already exist
     */
    private void addNetwork(GlyphIdentifier network) {
        if (networkedPortals.get(network.getGlyphString()) == null)
            networkedPortals.put(network.getGlyphString(), new ArrayList<String>());
    }

    /***
     * Adds a new portal to the system
     */
    public void addPortal(GlyphIdentifier g, DimensionCoordinates w) {
        if (getPortalIdentifier(w) != null || getPortalLocation(g) != null)
            return;

        portalCoordinates.put(g.getGlyphString(), w);
        portalCoordinatesReverse.put(w, g.getGlyphString());
    }

    /***
     * Adds a portal to a network
     */
    public void addPortalToNetwork(GlyphIdentifier portal, GlyphIdentifier network) {
        if (portal == null || network == null || getPortalNetwork(portal) != null)
            return;

        getNetwork(network).add(portal.getGlyphString());
        portalNetworks.put(portal.getGlyphString(), network.getGlyphString());
    }

    public GlyphIdentifier getDestination(GlyphIdentifier identifier, GlyphIdentifier portalNetwork) {
        ArrayList<String> network = getNetwork(portalNetwork);
        int index = network.indexOf(identifier.getGlyphString());

        if (index == network.size() - 1)
            return new GlyphIdentifier(network.get(0));
        else
            return new GlyphIdentifier(network.get(index + 1));
    }

    /***
     * Retrieves all the portals for the specified network. Will create a network if one does not already exist
     */
    private ArrayList<String> getNetwork(GlyphIdentifier network) {
        addNetwork(network);

        return networkedPortals.get(network.getGlyphString());
    }

    public int getNetworkSize(GlyphIdentifier nID) {
        ArrayList<String> list = getNetwork(nID);
        return list.isEmpty() ? -1 : list.size();
    }

    /***
     * Gets the portal controller for the specified portal identifier
     */
    public TileController getPortalController(GlyphIdentifier portal) {
        DimensionCoordinates w = getPortalLocation(portal);

        if (w == null)
            return null;

        TileEntity tile = w.getTileEntity();

        if (tile == null || !(tile instanceof TileController))
            return null;

        return (TileController) tile;
    }

    /***
     * Gets the unique identifier of the specified controller
     *
     * @return Null if one is not set
     */
    public GlyphIdentifier getPortalIdentifier(DimensionCoordinates w) {
        if (w == null)
            return null;

        String ID = portalCoordinatesReverse.get(w);

        return ID == null ? null : new GlyphIdentifier(portalCoordinatesReverse.get(w));
    }

    /***
     * Gets the world coordinates of the specified controller
     *
     * @return Null if one is not found
     */
    public DimensionCoordinates getPortalLocation(GlyphIdentifier g) {
        return g == null ? null : portalCoordinates.get(g.getGlyphString());
    }

    /***
     * Gets the network identifier of the specified controller
     *
     * @return Null if one is not set
     */
    public GlyphIdentifier getPortalNetwork(GlyphIdentifier g) {
        if (g == null)
            return null;

        String ID = portalNetworks.get(g.getGlyphString());

        return ID == null ? null : new GlyphIdentifier(portalNetworks.get(g.getGlyphString()));
    }

    public boolean hasIdentifier(DimensionCoordinates w) {
        return w == null ? null : portalCoordinates.containsValue(w);
    }

    public boolean hasNetwork(GlyphIdentifier g) {
        return g == null ? null : portalNetworks.containsKey(g.getGlyphString());
    }

    public boolean hasNetwork(DimensionCoordinates w) {
        return w == null ? null : hasNetwork(getPortalIdentifier(w));
    }

    public void loadAllData() throws Exception {
        if (!makeFiles())
            return;

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String portalData = FileUtils.readFileToString(portalFile), networkData = FileUtils.readFileToString(networkFile);

        portalCoordinates = gson.fromJson(portalData, new TypeToken<HashMap<String, DimensionCoordinates>>() {}.getType());
        portalNetworks = gson.fromJson(networkData, new TypeToken<HashMap<String, String>>() {}.getType());

        if (portalCoordinates == null)
            portalCoordinates = new HashMap<String, DimensionCoordinates>();

        if (portalNetworks == null)
            portalNetworks = new HashMap<String, String>();

        if (!portalCoordinates.isEmpty())
            for (Entry<String, DimensionCoordinates> entry : portalCoordinates.entrySet())
                portalCoordinatesReverse.put(entry.getValue(), entry.getKey());

        if (!portalNetworks.isEmpty())
            for (Entry<String, String> entry : portalNetworks.entrySet())
                if (networkedPortals.containsKey(entry.getValue()))
                    networkedPortals.get(entry.getValue()).add(entry.getKey());
                else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(entry.getKey());
                    networkedPortals.put(entry.getValue(), list);
                }
    }

    private boolean makeFiles() {
        try {
            if (!portalFile.exists())
                portalFile.createNewFile();

            if (!networkFile.exists())
                networkFile.createNewFile();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean portalIdentifierExists(GlyphIdentifier id) {
        return portalCoordinates.containsKey(id.getGlyphString());
    }

    /***
     * Removes a portal
     */
    public void removePortal(GlyphIdentifier g) {
        removePortal(g, getPortalLocation(g));
    }

    /***
     * Removes a portal
     */
    public void removePortal(GlyphIdentifier g, DimensionCoordinates w) {
        if (g == null || w == null)
            return;

        GlyphIdentifier n = getPortalNetwork(g);

        if (n != null)
            removePortalFromNetwork(g, n);

        portalCoordinates.remove(g.getGlyphString());
        portalCoordinatesReverse.remove(w);
    }

    /***
     * Removes a portal
     */
    public void removePortal(DimensionCoordinates w) {
        removePortal(getPortalIdentifier(w), w);
    }

    /***
     * Removes a portal from a network
     */
    public void removePortalFromNetwork(GlyphIdentifier portal, GlyphIdentifier network) {
        if (portal == null || network == null)
            return;

        getNetwork(network).remove(portal.getGlyphString());
        portalNetworks.remove(portal.getGlyphString());
    }

    public void saveAllData() {
        makeFiles();

        try {
            Gson gson = new GsonBuilder().create();
            FileWriter portalWriter = new FileWriter(portalFile), networkWriter = new FileWriter(networkFile);

            gson.toJson(portalCoordinates, portalWriter);
            gson.toJson(portalNetworks, networkWriter);

            portalWriter.close();
            networkWriter.close();
        } catch (Exception e) {
            CommonProxy.logger.catching(e);
        }
    }
}
