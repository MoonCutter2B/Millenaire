package org.millenaire.pathing;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class MillPathNavigate extends PathNavigateGround
{
	public MillPathNavigate(EntityLiving entitylivingIn, World worldIn) 
	{
		super(entitylivingIn, worldIn);
		this.setBreakDoors(true);
		this.setAvoidsWater(true);
		this.setAvoidSun(false);
	}
	
	@Override
	protected PathFinder getPathFinder()
    {
        this.nodeProcessor = new MillWalkNodeProcessor();
        this.nodeProcessor.setEnterDoors(true);
        this.nodeProcessor.setBreakDoors(true);
        this.nodeProcessor.setAvoidsWater(true);
        return new PathFinder(this.nodeProcessor);
    }
}
