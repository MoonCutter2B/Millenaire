package org.millenaire.entities;

import java.util.ArrayList;
import java.util.List;

import org.millenaire.CommonUtilities;
import org.millenaire.MillCulture;
import org.millenaire.VillagerType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class TileEntityVillageStone extends TileEntity
{
	List<EntityMillVillager> currentVillagers = new ArrayList<EntityMillVillager>();
	
	public int testVar = 0;
	
	@Override
	public void onLoad()
    {
        World world = this.getWorld();
        BlockPos pos = this.getPos();
        
        if (world.getBiomeGenForCoords(pos) != null)
        {
        	
        }
    }
	
	//@SideOnly(Side.SERVER)
	public EntityMillVillager createVillager(World worldIn, MillCulture cultureIn, int villagerID)
	{
		VillagerType currentVillagerType;
		int currentGender;
		
		if(villagerID == 0)
		{
			int balance = 0;
			villagerID = CommonUtilities.getRandomNonzero();
			boolean checkAgain = false;

			for(int i = 0; i < currentVillagers.size(); i++)
			{
				if(currentVillagers.get(i).getGender() == 0)
					balance++;
				else
					balance--;
				
				if(villagerID == currentVillagers.get(i).villagerID)
				{
					villagerID = CommonUtilities.getRandomNonzero();
					checkAgain = true;
				}
			}
			while(checkAgain)
			{
				checkAgain = false;
				for(int i = 0; i < currentVillagers.size(); i++)
				{
					if(villagerID == currentVillagers.get(i).villagerID)
					{
						villagerID = CommonUtilities.getRandomNonzero();
						checkAgain = true;
					}
				}
			}
			
			balance += CommonUtilities.randomizeGender();
			
			if(balance < 0)
			{
				currentGender = 0;
				currentVillagerType = cultureIn.getChildType(0);
			}
			else
			{
				currentGender = 1;
				currentVillagerType = cultureIn.getChildType(1);
			}
			
			EntityMillVillager newVillager = new EntityMillVillager(worldIn, villagerID, cultureIn);
			newVillager.setTypeAndGender(currentVillagerType, currentGender);
			
			return newVillager;
		}
		else
		{
			for(int i = 0; i < currentVillagers.size(); i++)
			{
				if(villagerID == currentVillagers.get(i).villagerID)
					return currentVillagers.get(i);
			}
			
			System.err.println("Attempted to create nonspecific Villager.");
		}

		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
		
    }
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
    {
		
    }
}
