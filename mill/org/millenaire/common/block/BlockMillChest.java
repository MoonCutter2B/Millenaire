package org.millenaire.common.block;

import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.forge.Mill;


public class BlockMillChest extends BlockChest {

	public static ContainerChest createContainer(World world, int i, int j, int k, EntityPlayer entityplayer) {
		final TileEntityMillChest lockedchest = (TileEntityMillChest) world.getBlockTileEntity(i, j, k);

		final IInventory chest=getChestInventory(lockedchest,world,i,j,k);

		return new ContainerChest(entityplayer.inventory,chest);
	}

	public static IInventory getChestInventory(TileEntityMillChest lockedchest,World world, int i, int j, int k) {

		final String largename=lockedchest.getInvLargeName();

		IInventory chest=lockedchest;

		final int blockID=world.getBlockId(i, j, k);

		if(world.getBlockId(i - 1, j, k) == blockID)
		{
			chest = new InventoryLargeChest(largename, (TileEntityChest)world.getBlockTileEntity(i - 1, j, k), (chest));
		}
		if(world.getBlockId(i + 1, j, k) == blockID)
		{
			chest = new InventoryLargeChest(largename, (chest), (TileEntityChest)world.getBlockTileEntity(i + 1, j, k));
		}
		if(world.getBlockId(i, j, k - 1) == blockID)
		{
			chest = new InventoryLargeChest(largename, (TileEntityChest)world.getBlockTileEntity(i, j, k - 1), (chest));
		}
		if(world.getBlockId(i, j, k + 1) == blockID)
		{
			chest = new InventoryLargeChest(largename, (chest), (TileEntityChest)world.getBlockTileEntity(i, j, k + 1));
		}

		return chest;
	}

	public BlockMillChest(int blockID) {
		super(blockID,0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6)
	{

	}



	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityMillChest();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityMillChest();
	}
	


	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	{
		if (world.isRemote) {
			ClientSender.activateMillChest(entityplayer, new Point(i,j,k));
		}
		return true;
	}

	@Override
	public void onBlockClicked(World world, int i, int j, int k,
			EntityPlayer entityplayer) {

		if ((entityplayer.inventory.getCurrentItem() != null) && (entityplayer.inventory.getCurrentItem().itemID != Mill.summoningWand.itemID)) {
			super.onBlockClicked(world, i, j, k, entityplayer);
		}


	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

}
