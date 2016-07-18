package org.millenaire.building;

import java.util.List;

import org.millenaire.MillConfig;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BuildingLocation 
{
	public int minx, maxx, minz, maxz, miny, maxy;
	public int minxMargin, maxxMargin, minyMargin, maxyMargin, minzMargin, maxzMargin;
	public int length, width;
	
	public EnumFacing orientation;
	public BlockPos position;
	
	public List<BlockPos>chestPos;
	public BlockPos tradePos;
	public List<BlockPos>sourcePos;
	public List<BlockPos>craftPos;
	public List<BlockPos>sleepPos;
	public List<BlockPos>hidePos;
	public List<BlockPos>defendPos;
	
	BuildingLocation(BuildingPlan planIn, BlockPos pos, EnumFacing orientIn)
	{
		orientation = orientIn;
		position = pos;
	}
	
	public void computeMargins() 
	{
		minxMargin = minx - MillConfig.minBuildingDistance + 1;
		minzMargin = minz - MillConfig.minBuildingDistance + 1;
		minyMargin = miny - 3;
		maxyMargin = maxy + 1;
		maxxMargin = maxx + MillConfig.minBuildingDistance + 1;
		maxzMargin = maxz + MillConfig.minBuildingDistance + 1;
	}
}
