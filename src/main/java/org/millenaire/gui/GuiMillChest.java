package org.millenaire.gui;

import java.io.IOException;

import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class GuiMillChest extends GuiChest
{
	private boolean isLocked;

	private IInventory lowerChestInventory;
	private TileEntityMillChest chest;
	
	GuiMillChest(IInventory playerInv, IInventory chestInv, EntityPlayer playerIn, TileEntityMillChest entityIn)
	{
		super(playerInv, chestInv);
		System.out.println("GuiCreated");
		lowerChestInventory = playerInv;
		chest = entityIn;
		isLocked = entityIn.isLockedFor(playerIn);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		chest.checkForAdjacentChests();
		IChatComponent string;
		if(chest.adjacentChestXNeg == null && chest.adjacentChestXPos == null && chest.adjacentChestZNeg == null && chest.adjacentChestZPos == null) {
			string = (isLocked ? new ChatComponentTranslation("container.millChestLocked") : new ChatComponentTranslation("container.millChestUnlocked"));
		}
		else {
			string = (isLocked ? new ChatComponentTranslation("container.millChestDoubleLocked") : new ChatComponentTranslation("container.millChestDoubleUnlocked"));
		}
		
        this.fontRendererObj.drawString(string.getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
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
