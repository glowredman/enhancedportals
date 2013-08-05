package uk.co.shadeddimensions.enhancedportals.util;

import java.util.LinkedList;
import java.util.Queue;

import org.bouncycastle.util.Arrays;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import uk.co.shadeddimensions.enhancedportals.lib.Coordinate;
import uk.co.shadeddimensions.enhancedportals.lib.Identifiers;

public class PortalUtils
{
    /***
     * Creates a portal using the existing portal data.
     */
    public static boolean createExistingPortal(WorldServer world, int x, int y, int z)
    {
        createNewPortal(world, x, y, z);
        
        // TODO add all data from main frame blocks

        return true;
    }

    /***
     * Creates a new portal disregarding any existing portal data included in
     * the blocks.
     */
    public static boolean createNewPortal(WorldServer world, int x, int y, int z)
    {
        createPreliminaryPortalBlocks(world, x, y, z);
        createSecondaryPortalBlocks(world, x, y, z);
        //preliminaryValidation(world, x, y, z);

        //if (!thoroughValidation(world, x, y, z))
        //{
        //    removePortal(world, x, y, z);
        //    return false;
        //}
        
        processMetadata(world, x, y, z);

        return true;
    }

    private static void processMetadata(WorldServer world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == Identifiers.Block.PORTAL_FRAME || world.getBlockId(x, y, z) == 0)
        {
            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);

                if (world.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == Identifiers.Block.PORTAL_BLOCK)
                {
                    x += dir.offsetX;
                    y += dir.offsetY;
                    z += dir.offsetZ;
                    break;
                }
            }
        }

        if (world.getBlockId(x, y, z) != Identifiers.Block.PORTAL_BLOCK)
        {
            return;
        }
        
        Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
        Queue<Coordinate> processed = new LinkedList<Coordinate>();
        toProcess.add(new Coordinate(x, y, z));

        while (!toProcess.isEmpty())
        {
            Coordinate self = toProcess.remove();
            setupMetadata(world, self);

            if (!processed.contains(self))
            {
                toProcess = findConnectedPortalBlocks(toProcess, world, self);
                processed.add(self);
            }
        }
    }
    
    private static void setupMetadata(WorldServer world, Coordinate coord)
    {
        int[] blockIds = new int[6];
        
        for (int i = 0; i < 6; i++)
        {
            Coordinate c = coord.offset(ForgeDirection.getOrientation(i));            
            blockIds[i] = world.getBlockId(c.x, c.y, c.z);
        }
        
        if (Arrays.areEqual(blockIds, new int[] { Identifiers.Block.PORTAL_BLOCK, Identifiers.Block.PORTAL_BLOCK, Identifiers.Block.PORTAL_BLOCK, Identifiers.Block.PORTAL_BLOCK, Identifiers.Block.PORTAL_BLOCK, Identifiers.Block.PORTAL_BLOCK })) // XYZ
        {
            world.setBlockMetadataWithNotify(coord.x, coord.y, coord.z, 4, 2);
        }
        else if (isPortalPart(blockIds[0]) && isPortalPart(blockIds[1]) && isPortalPart(blockIds[4]) && isPortalPart(blockIds[5])) // X
        {
            world.setBlockMetadataWithNotify(coord.x, coord.y, coord.z, 1, 2);
        }
        else if (isPortalPart(blockIds[0]) && isPortalPart(blockIds[1]) && isPortalPart(blockIds[2]) && isPortalPart(blockIds[3])) // Z
        {
            world.setBlockMetadataWithNotify(coord.x, coord.y, coord.z, 2, 2);
        }
        else if (isPortalPart(blockIds[2]) && isPortalPart(blockIds[3]) && isPortalPart(blockIds[4]) && isPortalPart(blockIds[5])) // XZ
        {
            world.setBlockMetadataWithNotify(coord.x, coord.y, coord.z, 3, 2);
        }
    }
    
    private static boolean isPortalPart(int id)
    {
        return id == Identifiers.Block.PORTAL_BLOCK || id == Identifiers.Block.PORTAL_FRAME;
    }
    
    /***
     * Creates a new portal. First looks to see if one existed there previously,
     * then calls the correct method.
     */
    public static boolean createPortal(WorldServer world, int x, int y, int z)
    {
        boolean isNew = true;

        if (isNew)
        {
            return createNewPortal(world, x, y, z);
        }
        else
        {
            return createExistingPortal(world, x, y, z);
        }
    }

    private static boolean createPortalBlocksOnFreeSides(WorldServer world, Coordinate coord)
    {
        boolean created = false;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);

            if (world.isAirBlock(coord.x + dir.offsetX, coord.y + dir.offsetY, coord.z + dir.offsetZ) && isConnectedByTwoSides(world, coord.offset(dir)))
            {
                world.setBlock(coord.x + dir.offsetX, coord.y + dir.offsetY, coord.z + dir.offsetZ, Identifiers.Block.PORTAL_BLOCK);
                created = true;
            }
        }

        return created;
    }

    private static void createPreliminaryPortalBlocks(WorldServer world, int x, int y, int z)
    {
        Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
        Queue<Coordinate> processed = new LinkedList<Coordinate>();
        toProcess.add(new Coordinate(x, y, z));
        boolean retriedFirst = false;

        while (!toProcess.isEmpty())
        {
            Coordinate self = toProcess.remove();
            createPortalBlocksOnFreeSides(world, self);

            if (!processed.contains(self))
            {
                toProcess = findConnectedBorderBlocks(toProcess, world, self);
                processed.add(self);
            }

            if (toProcess.isEmpty() && !retriedFirst)
            {
                retriedFirst = true;
                createPortalBlocksOnFreeSides(world, new Coordinate(x, y, z));
            }
        }
    }

    private static void createSecondaryPortalBlocks(WorldServer world, int x, int y, int z)
    {
        Coordinate coord = null;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);

            if (world.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == Identifiers.Block.PORTAL_BLOCK)
            {
                coord = new Coordinate(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                break;
            }
        }

        if (coord != null)
        {
            Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
            Queue<Coordinate> processed = new LinkedList<Coordinate>();
            toProcess.add(coord);

            while (!toProcess.isEmpty())
            {
                Coordinate self = toProcess.remove();
                createPortalBlocksOnFreeSides(world, self);

                if (!processed.contains(self))
                {
                    toProcess = findConnectedPortalBlocks(toProcess, world, self);
                    processed.add(self);
                }
            }
        }
    }

    private static Queue<Coordinate> findConnectedBorderBlocks(Queue<Coordinate> queue, WorldServer world, Coordinate coord)
    {
        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    if (i == 0 && j == 0 && k == 0)
                    {
                        continue; // Skip self...
                    }

                    if (world.getBlockId(coord.x + i, coord.y + k, coord.z + j) == Identifiers.Block.PORTAL_FRAME)
                    {
                        queue.add(new Coordinate(coord.x + i, coord.y + k, coord.z + j));
                    }
                }
            }
        }

        return queue;
    }

    private static Queue<Coordinate> findConnectedPortalBlocks(Queue<Coordinate> queue, WorldServer world, Coordinate coord)
    {
        for (int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                for (int k = -1; k < 2; k++)
                {
                    if (i == 0 && j == 0 && k == 0)
                    {
                        continue; // Skip self...
                    }

                    if (world.getBlockId(coord.x + i, coord.y + k, coord.z + j) == Identifiers.Block.PORTAL_BLOCK)
                    {
                        queue.add(new Coordinate(coord.x + i, coord.y + k, coord.z + j));
                    }
                }
            }
        }

        return queue;
    }

    private static boolean isConnectedByTwoSides(WorldServer world, Coordinate coord)
    {
        int sides = 0;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            int blockID = world.getBlockId(coord.x + dir.offsetX, coord.y + dir.offsetY, coord.z + dir.offsetZ);

            if (blockID == Identifiers.Block.PORTAL_BLOCK || blockID == Identifiers.Block.PORTAL_FRAME)
            {
                sides++;
            }
        }

        return sides >= 2;
    }

    private static boolean isInValidPosition(WorldServer world, Coordinate self)
    {
        int counter = 0;

        if (world.getBlockId(self.x, self.y, self.z) != Identifiers.Block.PORTAL_BLOCK)
        {
            return false;
        }

        for (int i = 0; i < 3; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i * 2), oppositeDir = dir.getOpposite();
            int ID = world.getBlockId(self.x + dir.offsetX, self.y + dir.offsetY, self.z + dir.offsetZ), ID2 = world.getBlockId(self.x + oppositeDir.offsetX, self.y + oppositeDir.offsetY, self.z + oppositeDir.offsetZ);

            if ((ID == Identifiers.Block.PORTAL_BLOCK || ID == Identifiers.Block.PORTAL_FRAME) && (ID2 == Identifiers.Block.PORTAL_BLOCK || ID2 == Identifiers.Block.PORTAL_FRAME))
            {
                counter++;
            }
            else if (ID != Identifiers.Block.PORTAL_BLOCK && ID != Identifiers.Block.PORTAL_FRAME && (ID2 == Identifiers.Block.PORTAL_BLOCK || ID2 == Identifiers.Block.PORTAL_FRAME) || ID2 != Identifiers.Block.PORTAL_BLOCK && ID2 != Identifiers.Block.PORTAL_FRAME && (ID == Identifiers.Block.PORTAL_BLOCK || ID == Identifiers.Block.PORTAL_FRAME))
            {
                return false;
            }
        }

        return counter >= 2;
    }

    private static void preliminaryValidation(WorldServer world, int x, int y, int z)
    {
        Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
        Queue<Coordinate> processed = new LinkedList<Coordinate>();
        toProcess.add(new Coordinate(x, y, z));

        while (!toProcess.isEmpty())
        {
            Coordinate self = toProcess.remove();

            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);

                if (world.getBlockId(self.x + dir.offsetX, self.y + dir.offsetY, self.z + dir.offsetZ) == Identifiers.Block.PORTAL_BLOCK && !isInValidPosition(world, self.offset(dir)))
                {
                    removePortal(world, self.x + dir.offsetX, self.y + dir.offsetY, self.z + dir.offsetZ);
                }
            }

            if (!processed.contains(self))
            {
                toProcess = findConnectedBorderBlocks(toProcess, world, self);
                processed.add(self);
            }
        }
    }

    /***
     * Removes a portal at the specified location.
     */
    public static boolean removePortal(WorldServer world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == Identifiers.Block.PORTAL_FRAME || world.getBlockId(x, y, z) == 0)
        {
            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);

                if (world.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == Identifiers.Block.PORTAL_BLOCK)
                {
                    x += dir.offsetX;
                    y += dir.offsetY;
                    z += dir.offsetZ;
                    break;
                }
            }
        }

        if (world.getBlockId(x, y, z) != Identifiers.Block.PORTAL_BLOCK)
        {
            return false;
        }

        Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
        toProcess.add(new Coordinate(x, y, z));

        while (!toProcess.isEmpty())
        {
            Coordinate self = toProcess.remove();

            if (world.getBlockId(self.x, self.y, self.z) == Identifiers.Block.PORTAL_BLOCK)
            {
                world.setBlockToAir(self.x, self.y, self.z);
                toProcess = findConnectedPortalBlocks(toProcess, world, self);
            }
        }

        return false;
    }

    private static boolean thoroughValidation(WorldServer world, int x, int y, int z)
    {
        if (world.getBlockId(x, y, z) == Identifiers.Block.PORTAL_FRAME)
        {
            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);

                if (world.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == Identifiers.Block.PORTAL_BLOCK)
                {
                    x += dir.offsetX;
                    y += dir.offsetY;
                    z += dir.offsetZ;
                    break;
                }
            }
        }

        if (world.getBlockId(x, y, z) != Identifiers.Block.PORTAL_BLOCK)
        {
            return false;
        }

        Queue<Coordinate> toProcess = new LinkedList<Coordinate>();
        Queue<Coordinate> processed = new LinkedList<Coordinate>();
        toProcess.add(new Coordinate(x, y, z));

        while (!toProcess.isEmpty())
        {
            Coordinate self = toProcess.remove();

            if (!isInValidPosition(world, self))
            {
                return false;
            }

            if (!processed.contains(self))
            {
                toProcess = findConnectedPortalBlocks(toProcess, world, self);
                processed.add(self);
            }
        }

        return true;
    }
}