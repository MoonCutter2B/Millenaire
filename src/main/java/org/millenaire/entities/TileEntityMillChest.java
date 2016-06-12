package org.millenaire.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class TileEntityMillChest extends TileEntityChest
{
	public boolean isLocked = true;
	
	public TileEntityMillChest()
	{
		super(2);
	}
	
	public boolean setLock()
	{
		if(isLocked)
			isLocked = false;
		else
			isLocked = true;
		
		checkForAdjacentChests();
		if(adjacentChestZNeg != null)
			((TileEntityMillChest)adjacentChestZNeg).isLocked = this.isLocked;
		if(adjacentChestZPos != null)
			((TileEntityMillChest)adjacentChestZPos).isLocked = this.isLocked;
		if(adjacentChestXNeg != null)
			((TileEntityMillChest)adjacentChestXNeg).isLocked = this.isLocked;
		if(adjacentChestXPos != null)
			((TileEntityMillChest)adjacentChestXPos).isLocked = this.isLocked;
		
		return isLocked;
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
		
		return isLocked;
	}
	
	@Override
    public IChatComponent getDisplayName()
    {
		boolean bool;
		EntityPlayer currentPlayer = Minecraft.getMinecraft().thePlayer;
		
		if(currentPlayer != null)
			bool = this.isLockedFor(currentPlayer);
		else
			bool = isLocked;
		
        return (IChatComponent)(bool ? new ChatComponentTranslation("container.millChestLocked", new Object[0]) : new ChatComponentTranslation("container.millChestUnlocked", new Object[0]));
    }
	
	public String getLargeDisplayName()
	{
		boolean bool;
		EntityPlayer currentPlayer = Minecraft.getMinecraft().thePlayer;
		
		if(currentPlayer != null)
			bool = this.isLockedFor(currentPlayer);
		else
			bool = isLocked;
		
		return bool ? "container.millChestDoubleLocked" : "container.millChestDoubleUnlocked";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        
        if(compound.hasKey("millChestLocked"))
        	isLocked = compound.getBoolean("millChestLocked");
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
