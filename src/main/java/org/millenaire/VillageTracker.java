package org.millenaire;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.INBTSerializable;

public class VillageTracker extends WorldSavedData
{
	private final static String IDENTITY = "Millenaire.VillageInfo";
	
	//Saved Data
	private List<PlayerCrop> playerCropKnowledge;
	
	public VillageTracker()
	{
		super(IDENTITY);
	}
	
	//Sets Player to be able to plant this crop
	public void setPlayerUseCrop(EntityPlayer playerIn, Item cropIn)
	{
		String playerName = playerIn.getName();
		ItemStack crop = new ItemStack(cropIn);
		
		playerCropKnowledge.add(new PlayerCrop(playerName, crop));
		markDirty();
	}
	
	//Returns whether Player can plant specified crop
	public boolean canPlayerUseCrop(EntityPlayer playerIn, Item cropIn)
	{
		String playerName = playerIn.getName();
		ItemStack crop = new ItemStack(cropIn);
		
		boolean result = false;
		
		for(int i = 0; i > playerCropKnowledge.size(); i++)
		{
			if(playerCropKnowledge.get(i).getCrop(playerName).equals(crop))
				result = true;
		}
		
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
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
		//Save PlayerCropKnowlege List
		NBTTagList pckList = new NBTTagList();
		for(int i = 0; i > playerCropKnowledge.size(); i++)
		{
			pckList.appendTag(playerCropKnowledge.get(i).serializeNBT());
		}
		nbt.setTag("playerCropKnowledge", pckList);
	}
	
	public static VillageTracker get(World world)
	{
		VillageTracker data = (VillageTracker)world.loadItemData(VillageTracker.class, IDENTITY);
		if(data == null)
		{
			data = new VillageTracker();
			world.setItemData(IDENTITY, data);
		}
		
		return data;
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	private static class PlayerCrop implements INBTSerializable<NBTTagCompound>
	{
		private String playerName;
		private ItemStack crop;
		
		private PlayerCrop(){}		
		
		public PlayerCrop(String nameIn, ItemStack cropIn)
		{
			playerName = nameIn;
			crop = cropIn;
		}
		
		@Override
		public NBTTagCompound serializeNBT() 
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("playerName", playerName);
			crop.writeToNBT(nbt);
			return nbt;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt) 
		{
			if(nbt.hasKey("playerName"))
			{
				playerName = nbt.getString("playerName");
				crop = ItemStack.loadItemStackFromNBT(nbt);
			}
		}
		
		public ItemStack getCrop(String nameIn)
		{
			if(nameIn == playerName)
				return crop;
			else
				return null;
		}
	}
}
