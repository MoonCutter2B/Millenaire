package org.millenaire.building;

import java.util.List;

import org.millenaire.MillConfig;
import org.millenaire.entities.EntityMillVillager;
import org.millenaire.util.ResourceLocationUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

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
	
	List<EntityMillVillager>residents;
	public List<String> subBuildings;
	
	BuildingLocation(BuildingPlan plan, BlockPos pos, EnumFacing orientIn)
	{
		orientation = orientIn;
		position = pos;
		//this.computeMargins();
	}
	
	BuildingLocation(ResourceLocation rl, BlockPos pos, EnumFacing orientIn) {
		this(BuildingTypes.getTypeByID(rl).loadBuilding(), pos, orientIn);
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
	/*
	public static BuildingLocation fromNBT(NBTTagCompound nbt) {
		ResourceLocation rl = ResourceLocationUtil.getRL(nbt.getString("planID"));
		BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
		EnumFacing fac = EnumFacing.getHorizontal(nbt.getInteger("facing"));
		return new BuildingLocation(rl, pos, fac);
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setString("planID", ResourceLocationUtil.getString(planid));
		nbt.setInteger("facing", orientation.getHorizontalIndex());
		
		
		return nbt;
	}
	*/
}
