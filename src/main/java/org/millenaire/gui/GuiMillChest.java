package org.millenaire.gui;

import java.io.IOException;

import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class GuiMillChest extends GuiChest
{
	boolean isLocked;
	
	public GuiMillChest(IInventory playerInv, IInventory chestInv, EntityPlayer playerIn, TileEntityMillChest entityIn) 
	{
		super(playerInv, chestInv);
		System.out.println("GuiCreated");
		isLocked = entityIn.isLockedFor(playerIn);
	}

	@Override
	protected void keyTyped(final char par1, final int par2) throws IOException 
	{
		if (!isLocked) 
		{
				super.keyTyped(par1, par2);
		}
		else 
		{
			if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) 
			{
				this.mc.thePlayer.closeScreen();
			}
		}
	}

	@Override
	protected void mouseClicked(final int i, final int j, final int k) throws IOException 
	{
		if (!isLocked) 
		{
			super.mouseClicked(i, j, k);
		}
	}
}
