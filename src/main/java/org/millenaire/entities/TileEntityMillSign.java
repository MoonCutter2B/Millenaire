package org.millenaire.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ITickable;

public class TileEntityMillSign extends TileEntitySign implements ITickable
{
	public static final int etatCivil = 1;
	public static final int constructions = 2;
	public static final int projects = 3;
	public static final int house = 4;
	public static final int resources = 5;
	public static final int archives = 6;
	public static final int villageMap = 7;
	public static final int military = 8;
	public static final int tradeGoods = 9;
	public static final int innVisitors = 10;
	public static final int marketMerchants = 11;
	public static final int controlledProjects = 12;
	public static final int controlledMilitary = 13;
	
	public int thisSignType = 0;
	
	@Override
	public boolean executeCommand(final EntityPlayer playerIn)
    {
		//Display GuiPanel with appropriate info based on SignType
		return false;
    }
	
	public void setSignType(int typeIn)
	{
		thisSignType = typeIn;
	}
	
	@Override
	public void update() 
	{
		signText[0] = new ChatComponentText("The End is Nigh");
		// update signText[0-3] with information from VillageStone	
	}
}
