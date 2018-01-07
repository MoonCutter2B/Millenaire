package org.millenaire.gui;

import org.millenaire.blocks.BlockMillChest;
import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MillGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == 1)
		{
			TileEntityMillChest teMillChest = null;
			if(world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityMillChest)
				teMillChest = (TileEntityMillChest)world.getTileEntity(new BlockPos(x, y, z));
			else
				System.err.println("TileEntityMillChest Missing");
			
			BlockMillChest millChest = (BlockMillChest)teMillChest.getBlockType();
			ILockableContainer lockableContainer = millChest.getLockableContainer(world, new BlockPos(x, y, z));
			
			return new ContainerChest(player.inventory, lockableContainer, player);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == 0)
		{
			return new GuiParchment(player.getHeldItem());
		}
		if(ID == 1)
		{
			TileEntityMillChest teMillChest = null;
			if(world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityMillChest)
				teMillChest = (TileEntityMillChest)world.getTileEntity(new BlockPos(x, y, z));
			else
				System.err.println("TileEntityMillChest Missing");

			BlockMillChest millChest = (BlockMillChest)teMillChest.getBlockType();
			ILockableContainer lockableContainer = millChest.getLockableContainer(world, new BlockPos(x, y, z));
			
			return new GuiMillChest(player.inventory, lockableContainer, player, teMillChest);
		}
		if(ID == 2)
		{
			return new GuiOptions(ID, "option.negateVillage.text");
		}
		if(ID == 3)
		{
			return new GuiOptions(ID, "option.negateVillager.text");
		}
		if(ID == 4)
		{
			return new GuiOptions(ID, "option.summonRandom.text");
		}
		if(ID == 5)
		{
			return new GuiChief();
		}
		
		return null;
	}

}
