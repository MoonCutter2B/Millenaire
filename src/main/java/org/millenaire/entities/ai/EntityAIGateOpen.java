package org.millenaire.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIGateOpen extends EntityAIBase
{
    private EntityLiving theEntity;
    private BlockPos gatePosition = BlockPos.ORIGIN;
    /** The gate block */
    private BlockFenceGate gateBlock;
    /** If is true then the Entity has stopped Gate Interaction and completed the task. */
    private boolean hasStoppedDoorInteraction;
    private float entityPositionX;
    private float entityPositionZ;
    
    /** If the entity close the gate */
    private boolean closeGate;
    
    /** The temporisation before the entity close the door (in ticks, always 20 = 1 second) */
    private int closeGateTemporisation;

    public EntityAIGateOpen(EntityLiving entityIn, boolean shouldClose)
    {
        this.theEntity = entityIn;

        if (!(entityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for GateOpenGoal");
        }
        
        this.theEntity = entityIn;
        this.closeGate = shouldClose;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
    	//System.out.println("AI called - should execute");
        if (!this.theEntity.isCollidedHorizontally)
        {
            return false;
        }
        else
        {
            PathNavigateGround pathnavigateground = (PathNavigateGround)this.theEntity.getNavigator();
            PathEntity pathentity = pathnavigateground.getPath();

            if (pathentity != null && !pathentity.isFinished() && pathnavigateground.getEnterDoors())
            {
            	System.out.println("pathfinding check");
                for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i)
                {
                    PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
                    this.gatePosition = new BlockPos(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord);

                    if (this.theEntity.getDistanceSq((double)this.gatePosition.getX(), this.theEntity.posY, (double)this.gatePosition.getZ()) <= 2.25D)
                    {
                    	System.out.println("gate check");
                        this.gateBlock = this.getBlockGate(this.gatePosition);

                        if (this.gateBlock != null)
                        {
                            return true;
                        }
                    }
                }

                this.gatePosition = (new BlockPos(this.theEntity)).up();
                this.gateBlock = this.getBlockGate(this.gatePosition);
                return this.gateBlock != null;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        return this.closeGate && this.closeGateTemporisation > 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        this.closeGateTemporisation = 20;
        this.toggleGate(gateBlock, this.theEntity.worldObj, this.gatePosition, true);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask()
    {
    	--this.closeGateTemporisation;
    	
        float f = (float)((double)((float)this.gatePosition.getX() + 0.5F) - this.theEntity.posX);
        float f1 = (float)((double)((float)this.gatePosition.getZ() + 0.5F) - this.theEntity.posZ);
        float f2 = this.entityPositionX * f + this.entityPositionZ * f1;

        if (f2 < 0.0F)
        {
            this.hasStoppedDoorInteraction = true;
        }
    }
    
    @Override
    public void resetTask()
    {
        if (this.closeGate)
        {
            this.toggleGate(gateBlock, this.theEntity.worldObj, this.gatePosition, false);
        }
    }

    private BlockFenceGate getBlockGate(BlockPos pos)
    {
        Block block = this.theEntity.worldObj.getBlockState(pos).getBlock();
        return block instanceof BlockFenceGate && block.getMaterial() == Material.wood ? (BlockFenceGate)block : null;
    }
    
    private void toggleGate(BlockFenceGate gateIn, World worldIn, BlockPos pos, boolean open)
    {
    	if(gateIn == null)
    	{
    		return;
    	}
    	
    	IBlockState state = worldIn.getBlockState(pos);
    	if(state.getBlock() != gateIn)
    	{
    		return;
    	}
    	
    	if(open)
    	{
            worldIn.setBlockState(pos, state.withProperty(BlockFenceGate.OPEN, true));
        }
    	
    	if(!open)
    	{
            worldIn.setBlockState(pos, state.withProperty(BlockFenceGate.OPEN, false));
        }
    }
}
