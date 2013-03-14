package org.millenaire.common.pathing.atomicstryker;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Vec3;

/**
 * Extension of the Minecraft PathEntity to try and fix some of its horrible shortcomings
 * 
 * 
 * @author AtomicStryker
 */

public class AS_PathEntity extends PathEntity
{
	private long timeLastPathIncrement = 0L;
	public final PathPoint[] pointsCopy;
	private int pathIndexCopy;

	public AS_PathEntity(PathPoint[] points)
	{
		super(points);
		timeLastPathIncrement = System.currentTimeMillis();
		this.pointsCopy = points;
		this.pathIndexCopy = 0;
	}

	public void advancePathIndex()
	{
		timeLastPathIncrement = System.currentTimeMillis();
		pathIndexCopy++;
		setCurrentPathIndex(pathIndexCopy);
	}

	public PathPoint getCurrentTargetPathPoint()
	{
		if (this.isFinished()) return null;
		return this.pointsCopy[getCurrentPathIndex()];
	}

	public PathPoint getFuturePathPoint(int jump)
	{
		if (getCurrentPathIndex()>=(pointsCopy.length-jump)) return null;
		return this.pointsCopy[getCurrentPathIndex()+jump];
	}

	public PathPoint getNextTargetPathPoint()
	{
		if (getCurrentPathIndex()>=(pointsCopy.length-1)) return null;
		return this.pointsCopy[getCurrentPathIndex()+1];
	}

	public PathPoint getPastTargetPathPoint(int jump)
	{
		if ((getCurrentPathIndex()<jump) || (pointsCopy.length==0)) return null;
		return this.pointsCopy[getCurrentPathIndex()-jump];
	}

	@Override
	public Vec3 getPosition(Entity var1)
	{
		if (super.isFinished()) return null;
		return super.getPosition(var1);
	}

	public PathPoint getPreviousTargetPathPoint()
	{
		if ((getCurrentPathIndex()<1) || (pointsCopy.length==0)) return null;
		return this.pointsCopy[getCurrentPathIndex()-1];
	}

	public long getTimeSinceLastPathIncrement()
	{
		return (System.currentTimeMillis() - timeLastPathIncrement);
	}

	@Override
	public void setCurrentPathIndex(int par1)
	{
		timeLastPathIncrement = System.currentTimeMillis();
		pathIndexCopy = par1;
		super.setCurrentPathIndex(par1);
	}
}
