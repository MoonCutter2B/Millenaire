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
	
	//Saved Data
	private List<PlayerCrop> playerCropKnowledge = new ArrayList<PlayerCrop>();
	
	public VillageTracker()
	{
		super(IDENTITY);
	}
	
	public VillageTracker(String id)
	{
		super(id);
	}
	
	//Sets Player to be able to plant this crop
	public void setPlayerUseCrop(EntityPlayer playerIn, Item cropIn)
	{
		String playerName = playerIn.getName();
		ItemStack crop = new ItemStack(cropIn);
		PlayerCrop pc = new PlayerCrop(playerName, crop);
		playerCropKnowledge.add(pc);

		this.markDirty();
	}
	
	//Removes all entries for This Player/Crop.  For debugging.
	public boolean removePlayerUseCrop(EntityPlayer playerIn, Item cropIn)
	{
		String playerName = playerIn.getName();
		boolean result = false;
		
		if(cropIn == null)
			System.out.println("what the hell????");
		for(int i = 0; i < playerCropKnowledge.size(); i++)
		{
			if(playerCropKnowledge.get(i).getCrop(playerName) == null)
				break;

			if(playerCropKnowledge.get(i).getCrop(playerName).getItem().equals(cropIn))
			{
				playerCropKnowledge.remove(i);
				result = true;
			}
		}
		
		this.markDirty();
		return result;
	}
	
	//Returns whether Player can plant specified crop
	public boolean canPlayerUseCrop(EntityPlayer playerIn, Item cropIn)
	{
		String playerName = playerIn.getName();
		
		boolean result = false;
		
		for(int i = 0; i < playerCropKnowledge.size(); i++)
		{
			if(playerCropKnowledge.get(i).getCrop(playerName) == null)
				break;

			if(playerCropKnowledge.get(i).getCrop(playerName).getItem().equals(cropIn))
				result = true;
		}
		System.out.println(result);
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		System.out.println("Village Tracker reading from NBT");
		
		//Restore playerCropKnowledge List
		NBTTagList pckList = nbt.getTagList("playerCropKnowledge", 10);
	    for (int i = 0; i < pckList.tagCount(); ++i) 
	    {
	        NBTTagCompound stackTag = pckList.getCompoundTagAt(i);
	        PlayerCrop pc = new PlayerCrop();
	        pc.deserializeNBT(stackTag);
	        playerCropKnowledge.add(pc);
	    }
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		System.out.println("Village Tracker Writing to NBT");
		
		//Save PlayerCropKnowlege List
		NBTTagList pckList = new NBTTagList();
		for(int i = 0; i < playerCropKnowledge.size(); i++)
		{
			pckList.appendTag(playerCropKnowledge.get(i).serializeNBT());
		}
		nbt.setTag("playerCropKnowledge", pckList);
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
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	private static class PlayerCrop implements INBTSerializable<NBTTagCompound>
	{
		private String playerName;
		private ItemStack crop;
		
		public PlayerCrop(){}		
		
		public PlayerCrop(String nameIn, ItemStack cropIn)
		{
			playerName = nameIn;
			crop = cropIn;
		}
		
		@Override
		public NBTTagCompound serializeNBT() 
		{
			System.out.println("Player:Crop being serialized: " + playerName + ", " + crop.toString());
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("playerName", playerName);
			crop.writeToNBT(nbt);
			return nbt;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt) 
		{
			playerName = nbt.getString("playerName");
			crop = ItemStack.loadItemStackFromNBT(nbt);
			System.out.println("Player:Crop being deserialized: " + playerName + ", " + crop.toString());
		}
		
		public ItemStack getCrop(String nameIn)
		{
			if(nameIn.equals(playerName))
				return crop;
			else
				return null;
		}
		
		//public String getName() {return playerName;}
	}
}
