package org.millenaire.entities;

import org.millenaire.gui.ContainerMillChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

public class TileEntityMillChest extends TileEntityChest
{
	public boolean unlocked = false;
	
	public TileEntityMillChest()
	{
		super(2);
	}
	
	public boolean isLockedFor(EntityPlayer playerIn)
	{
		if(playerIn == null)
			return false;
		
		//final Building building = mw.getBuilding(buildingPos);

		//if (building == null)
		//	return true;

		//if (building.lockedForPlayer(playerIn.getDisplayName()))
		//	return true;
		
		return unlocked;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        
        if(compound.hasKey("millChestUnlocked"))
        	unlocked = compound.getBoolean("millChestUnlocked");
    }
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        
        compound.setBoolean("millChestUnlocked", unlocked);
    }
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerMillChest(playerInventory, this, playerIn);
    }
}
