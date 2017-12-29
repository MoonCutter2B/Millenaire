package org.millenaire;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class RaidTracker extends WorldSavedData
{
	private final static String IDENTITY = "Millenaire.RaidInfo";
	
	private RaidTracker() { super(IDENTITY); }

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		
	}

	public static RaidTracker get(World world)
	{
		RaidTracker data = (RaidTracker)world.loadItemData(RaidTracker.class, IDENTITY);
		if(data == null)
		{
			data = new RaidTracker();
			world.setItemData(IDENTITY, data);
		}
		
		return data;
	}
}
