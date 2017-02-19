package org.millenaire.entities.ai;

import org.millenaire.entities.EntityMillVillager;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBuild extends EntityAIBase
{
	protected EntityLiving theEntity;
	
	public EntityAIBuild(EntityLiving entityIn)
	{
		this.theEntity = entityIn;

        if (!(entityIn instanceof EntityMillVillager))
        {
            throw new IllegalArgumentException("Unsupported mob type for BuildGoal");
        }
        else if (!((EntityMillVillager)entityIn).getVillagerType().canBuild)
        {
        	throw new IllegalArgumentException("Villager does support BuildGoal");
        }
	}

	/**
     * Returns whether the EntityAIBase should begin execution.
     */
	@Override
	public boolean shouldExecute() 
	{
		
		return false;
	}

	/**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
	@Override
    public boolean continueExecuting()
    {
		
		return false;
    }
	
	/**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	
    }
    
    /**
     * Updates the task
     */
    public void updateTask()
    {
    	
    }
    
    /**
     * Resets the task
     */
    @Override
    public void resetTask()
    {
    	
    }
}
