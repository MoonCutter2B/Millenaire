package org.millenaire.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

public class TileEntityMillChest extends TileEntityChest
{
	private boolean isLocked = true;
	
	public TileEntityMillChest() { super(); }
	
	public boolean setLock()
	{
		isLocked = !isLocked;

		
		checkForAdjacentChests();
		if(adjacentChestZNeg != null || adjacentChestZPos != null ||
                adjacentChestXNeg != null || adjacentChestXPos != null) {
            assert adjacentChestZNeg != null;
            ((TileEntityMillChest)adjacentChestZNeg).isLocked = this.isLocked;
        }
		
		return isLocked;
	}
	
	public boolean isLockedFor(EntityPlayer playerIn)
	{
		if(playerIn == null)
		{
            return false;
        }
		
		//final Building building = mw.getBuilding(buildingPos);

		//if (building == null)
		//	return true;

		//if (building.lockedForPlayer(playerIn.getDisplayName()))
		//	return true;
		
		return isLocked;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        
        if(compound.hasKey("millChestLocked"))
        {
            isLocked = compound.getBoolean("millChestLocked");
        }
    }
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        
        compound.setBoolean("millChestLocked", isLocked);
    }
	
	@Override
    public String getGuiID()
    {
        return "millenaire:chest";
    }
    
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerChest(playerInventory, this, playerIn);
    }
}
