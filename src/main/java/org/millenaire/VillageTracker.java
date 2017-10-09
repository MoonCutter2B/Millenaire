package org.millenaire;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class VillageTracker extends WorldSavedData
{
	private final static String IDENTITY = "Millenaire.VillageInfo";
	
	
	public VillageTracker()
	{
		super(IDENTITY);
	}
	
	public VillageTracker(String id)
	{
		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		System.out.println("Village Tracker reading from NBT");
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		System.out.println("Village Tracker Writing to NBT");
		
	}
	
	public static VillageTracker get(World world)
	{
		MapStorage storage = world.getPerWorldStorage();
		VillageTracker data = (VillageTracker)storage.loadData(VillageTracker.class, IDENTITY);
		if(data == null)
		{
			data = new VillageTracker(IDENTITY);
			storage.setData(IDENTITY, data);
		}
		
		return data;
	}
}
