package enhancedportals.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.network.ByteBufUtils;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import enhancedportals.network.GuiHandler;
import enhancedportals.portal.GlyphElement;
import enhancedportals.portal.GlyphIdentifier;
import enhancedportals.portal.PortalTextureManager;
import enhancedportals.utility.ComputerUtils;
import enhancedportals.utility.Localization;

@InterfaceList(value = {
        @Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft|API|Peripheral"),
        @Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputersAPI|Network") })
public class TileDialingDevice extends TileFrame implements IPeripheral, SimpleComponent {
    public ArrayList<GlyphElement> glyphList = new ArrayList<GlyphElement>();

    @Override
    public boolean activate(EntityPlayer player, ItemStack stack) {
        TileController controller = getPortalController();

        if (worldObj.isRemote)
            return controller != null;

        if (controller != null && controller.isFinalized()) {
            if (controller.getIdentifierUnique() == null)
                player.addChatComponentMessage(new ChatComponentText(Localization.getChatError("noUidSet")));
            else if (!player.isSneaking())
                GuiHandler.openGui(player, this, GuiHandler.DIALING_DEVICE_A);
            else if (controller.isPortalActive())
                controller.connectionTerminate();
            else
                GuiHandler.openGui(player, this, GuiHandler.DIALING_DEVICE_B);

            return true;
        }

        return false;
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void attach(IComputerAccess computer) {

    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        try {
            if (method == 0)
                return comp_Dial(arguments);
            if (method == 2)
                return comp_DialStored(arguments);
            if (method == 3)
                return comp_GetStoredName(arguments);
            if (method == 4)
                return comp_GetStoredGlyph(arguments);
        } catch (Exception e) {
            throw new LuaException(e.getMessage());
        }
        if (method == 5)
            return new Object[] { glyphList.size() };
        if (method == 1)
            getPortalController().connectionTerminate();
        return null;
    }

    Object[] comp_Dial(Object[] arguments) throws Exception {
        if (arguments.length == 1) {
            String s = arguments[0].toString();
            s = s.replace(" ", GlyphIdentifier.GLYPH_SEPERATOR);

            String error = ComputerUtils.verifyGlyphArguments(s);
            if (error != null)
                throw new Exception(error);

            getPortalController().connectionDial(new GlyphIdentifier(s), null, null);
        } else
            throw new Exception("Invalid arguments");

        return new Object[] { true };
    }

    Object[] comp_DialStored(Object[] arguments) throws Exception {
        int num = getSelectedEntry(arguments);

        if (num >= 0 && num < glyphList.size())
            getPortalController().connectionDial(glyphList.get(num).identifier, null, null);

        return new Object[] { true };
    }

    Object[] comp_GetStoredGlyph(Object[] arguments) throws Exception {
        int num = getSelectedEntry(arguments);
        GlyphElement entry = glyphList.get(num);

        if (entry != null)
            return new Object[] { entry.identifier.getGlyphString() };
        else
            throw new Exception("Entry not found");
    }

    Object[] comp_GetStoredName(Object[] arguments) throws Exception {
        int num = getSelectedEntry(arguments);
        GlyphElement entry = glyphList.get(num);

        if (entry != null)
            return new Object[] { entry.name };
        else
            throw new Exception("Entry not found");
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public void detach(IComputerAccess computer) {

    }

    @Callback(doc = "function(uid:string):boolean -- Attempts to create a connection to the specified portal. UID must be given as a single string in the format of numbers separated by spaces.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] dial(Context context, Arguments args) throws Exception {
        if (args.count() < 1)
            return null;

        return comp_Dial(ComputerUtils.argsToArray(args));
    }

    @Callback(doc = "function(entry:number):boolean -- Dials the specified entry in the Dialing Device's list.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] dialStored(Context context, Arguments args) throws Exception {
        return comp_DialStored(ComputerUtils.argsToArray(args));
    }

    @Override
    @Method(modid = "ComputerCraft|API|Peripheral")
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    @Override
    public String getComponentName() {
        return "ep_dialling_device";
    }

    @Override
    public String[] getMethodNames() {
        return new String[] { "dial", "terminate", "dialStored", "getStoredName", "getStoredGlyph", "getStoredCount" };
    }

    int getSelectedEntry(Object[] arguments) throws Exception {
        try {
            if (arguments.length == 1) {
                if (arguments[0].toString().contains("."))
                    arguments[0] = arguments[0].toString().substring(0, arguments[0].toString().indexOf("."));

                int i = Integer.parseInt(arguments[0].toString());

                if (i < 0 || i >= glyphList.size())
                    throw new Exception("There is no entry in location " + i);

                return i;
            }
        } catch (NumberFormatException e) {
            throw new Exception(arguments[0].toString() + " is not an integer.");
        }

        throw new Exception("Invalid number of arguments.");
    }

    @Callback(direct = true, doc = "function():number -- Returns the amount of entries in the Dialing Device's list.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getStoredCount(Context context, Arguments args) {
        return new Object[] { glyphList.size() };
    }

    @Callback(direct = true, doc = "function(entry:number):string -- Returns the UID as a string of the specified entry in the Dialing Device's list.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getStoredGlyph(Context context, Arguments args) throws Exception {
        return comp_GetStoredGlyph(ComputerUtils.argsToArray(args));
    }

    @Callback(direct = true, doc = "function(entry:number):string -- Returns the name of the specified entry in the Dialing Device's list.")
    @Method(modid = "OpenComputersAPI|Machine")
    public Object[] getStoredName(Context context, Arguments args) throws Exception {
        return comp_GetStoredName(ComputerUtils.argsToArray(args));
    }

    @Override
    public String getType() {
        return "ep_dialling_device";
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }

    @Override
    public void packetGuiFill(ByteBuf buffer) {
        buffer.writeInt(glyphList.size());

        for (int i = 0; i < glyphList.size(); i++) {
            ByteBufUtils.writeUTF8String(buffer, glyphList.get(i).name);
            ByteBufUtils.writeUTF8String(buffer, glyphList.get(i).identifier.getGlyphString());
        }
    }

    @Override
    public void packetGuiUse(ByteBuf buffer) {
        int max = buffer.readInt();
        glyphList.clear();

        for (int i = 0; i < max; i++)
            glyphList.add(new GlyphElement(ByteBufUtils.readUTF8String(buffer), new GlyphIdentifier(ByteBufUtils.readUTF8String(buffer))));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList list = tag.getTagList("glyphList", 10);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound t = list.getCompoundTagAt(i);
            String name = t.getString("Name"), glyph = t.getString("Identifier");

            if (t.hasKey("texture")) {
                PortalTextureManager tex = new PortalTextureManager();
                tex.readFromNBT(t, "texture");

                glyphList.add(new GlyphElement(name, new GlyphIdentifier(glyph), tex));
            } else
                glyphList.add(new GlyphElement(name, new GlyphIdentifier(glyph)));
        }
    }

    @Callback(doc = "function():boolean -- Terminates any active connection.")
    @Method(modid = "OpenComputersAPI|Network")
    public Object[] terminate(Context context, Arguments args) {
        getPortalController().connectionTerminate();
        return new Object[] { true };
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < glyphList.size(); i++) {
            NBTTagCompound t = new NBTTagCompound();
            GlyphElement e = glyphList.get(i);
            t.setString("Name", e.name);
            t.setString("Identifier", e.identifier.getGlyphString());

            if (e.hasSpecificTexture())
                e.texture.writeToNBT(t, "texture");

            list.appendTag(t);
        }

        tag.setTag("glyphList", list);
    }
}
