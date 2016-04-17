package org.millenaire.gui;

import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

public class ContainerMillChest extends ContainerChest
{
	EntityPlayer player;
	public ContainerMillChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer playerIn) 
	{
		super(playerInventory, chestInventory, playerIn);
	}

	@Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
		if(this.getLowerChestInventory() instanceof TileEntityMillChest)
		{
			if(!((TileEntityMillChest)this.getLowerChestInventory()).isLockedFor(playerIn))
				return true;
		}
        return super.canInteractWith(playerIn);
    }
}
