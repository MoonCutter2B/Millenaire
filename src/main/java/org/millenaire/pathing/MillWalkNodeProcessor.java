package org.millenaire.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class MillWalkNodeProcessor extends WalkNodeProcessor
{
	@Override
	public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance)
    {
        int i = 0;
        int j = 0;

        if (this.getMillVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1)
        {
            j = 1;
        }

        PathPoint pathpoint = this.getMillSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, j);
        PathPoint pathpoint1 = this.getMillSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, j);
        PathPoint pathpoint2 = this.getMillSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, j);
        PathPoint pathpoint3 = this.getMillSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, j);

        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[i++] = pathpoint;
        }

        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[i++] = pathpoint3;
        }

        return i;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getMillSafePoint(Entity entityIn, int x, int y, int z, int p_176171_5_)
    {
        PathPoint pathpoint = null;
        int i = this.getMillVerticalOffset(entityIn, x, y, z);

        if (i == 2)
        {
            return this.openPoint(x, y, z);
        }
        else
        {
            if (i == 1)
            {
                pathpoint = this.openPoint(x, y, z);
            }

            if (pathpoint == null && p_176171_5_ > 0 && i != -3 && i != -4 && this.getMillVerticalOffset(entityIn, x, y + p_176171_5_, z) == 1)
            {
                pathpoint = this.openPoint(x, y + p_176171_5_, z);
                y += p_176171_5_;
            }

            if (pathpoint != null)
            {
                int j = 0;
                int k;

                for (k = 0; y > 0; pathpoint = this.openPoint(x, y, z))
                {
                    k = this.getMillVerticalOffset(entityIn, x, y - 1, z);

                    if (this.getAvoidsWater() && k == -1)
                    {
                        return null;
                    }

                    if (k != 1)
                    {
                        break;
                    }

                    if (j++ >= entityIn.getMaxFallHeight())
                    {
                        return null;
                    }

                    --y;

                    if (y <= 0)
                    {
                        return null;
                    }
                }

                if (k == -2)
                {
                    return null;
                }
            }

            return pathpoint;
        }
    }

    /**
     * Checks if an entity collides with blocks at a position.
     * Returns 1 if clear, 0 for colliding with any solid block, -1 for water(if avoids water),
     * -2 for lava, -3 for fence and wall, -4 for closed trapdoor, 2 if otherwise clear except for open trapdoor or
     * water(if not avoiding)
     */
    private int getMillVerticalOffset(Entity entityIn, int x, int y, int z)
    {
    	BlockPos pos = new BlockPos(x, y, z);
    	Block block = this.blockaccess.getBlockState(pos).getBlock();
    	
    	if(block instanceof BlockFenceGate && this.blockaccess.isAirBlock(pos.up()))
    		return 2;
    	else
    		return func_176170_a(this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getAvoidsWater(), true, this.getEnterDoors());
    }
}
