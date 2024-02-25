package enhancedportals;

import java.io.File;
import java.lang.reflect.Method;

import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.LoggerConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import enhancedportals.block.BlockFrame;
import enhancedportals.network.CommonProxy;
import enhancedportals.network.GuiHandler;
import enhancedportals.network.PacketPipeline;
import enhancedportals.portal.NetworkManager;
import enhancedportals.utility.GeneralUtils;

@Mod(name = EnhancedPortals.MOD_NAME, modid = EnhancedPortals.MOD_ID, version = EnhancedPortals.MOD_VERSION, dependencies = EnhancedPortals.MOD_DEPENDENCIES)
public class EnhancedPortals {
    public static final String MOD_NAME = "EnhancedPortals",
                               MOD_ID = "enhancedportals",
                               MOD_VERSION = "3.0.12",
                               MOD_DEPENDENCIES = "after:ThermalExpansion",
                               UPDATE_URL = "https://raw.githubusercontent.com/enhancedportals/enhancedportals/master/docs/VERSION";
    public static final String MODID_THERMALEXPANSION = "ThermalExpansion";
    public static final PacketPipeline packetPipeline = new PacketPipeline();

    @Instance(MOD_ID)
    public static EnhancedPortals instance;

    @SidedProxy(clientSide = "enhancedportals.network.ClientProxy", serverSide = "enhancedportals.network.CommonProxy")
    public static CommonProxy proxy;

    public EnhancedPortals() {
        LoggerConfig fml = new LoggerConfig(FMLCommonHandler.instance().getFMLLogger().getName(), Level.ALL, true);
        LoggerConfig modConf = new LoggerConfig(CommonProxy.logger.getName(), Level.ALL, true);
        modConf.setParent(fml);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.miscSetup();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    /** Taken from the CC-API, allowing for use it if it's available, instead of shipping it/requiring it **/
    void initializeComputerCraft() {
        if (!Loader.isModLoaded("ComputerCraft"))
            return;

        try {
            Class<?> computerCraft = Class.forName("dan200.computercraft.ComputerCraft");
            Method computerCraft_registerPeripheralProvider = computerCraft.getMethod("registerPeripheralProvider", new Class[] { Class.forName("dan200.computercraft.api.peripheral.IPeripheralProvider") });
            computerCraft_registerPeripheralProvider.invoke(null, BlockFrame.instance);
        } catch (Exception e) {
            CommonProxy.logger.error("Could not load the CC-API");
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInitialise();
        initializeComputerCraft();
        proxy.setupCrafting();
        
        if (event.getSide() == Side.CLIENT)
            FMLCommonHandler.instance().bus().register(new enhancedportals.network.LogOnHandler());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GeneralUtils.checkAPIs();
        proxy.setupConfiguration(new File(event.getSuggestedConfigurationFile().getParentFile(), MOD_NAME + File.separator + "config.cfg"));
        packetPipeline.initalise();
        proxy.registerBlocks();
        proxy.registerTileEntities();
        proxy.registerItems();
        proxy.registerPackets();
        proxy.registerPotions();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.networkManager = new NetworkManager(event);
    }
    
    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event) {
        PotionEffect effect = event.entityLiving.getActivePotionEffect(CommonProxy.featherfallPotion);

        if (effect != null) {
            event.entityLiving.fallDistance = 0f;

            if (event.entityLiving.getActivePotionEffect(CommonProxy.featherfallPotion).getDuration() <= 0)
                event.entityLiving.removePotionEffect(CommonProxy.featherfallPotion.id);
        }
    }

    @SubscribeEvent
    public void worldSave(WorldEvent.Save event) {
        if (!event.world.isRemote)
            proxy.networkManager.saveAllData();
    }
}
