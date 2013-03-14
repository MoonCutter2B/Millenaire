package org.millenaire.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.MLN;
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
		blockIndexInTexture = 14;
		this.setCreativeTab(CreativeTabs.tabDecorations);
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
	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
		if(l == 1)
			return blockIndexInTexture - 1;
		if(l == 0)
			return blockIndexInTexture - 1;
		final int i1 = iblockaccess.getBlockId(i, j, k - 1);
		final int j1 = iblockaccess.getBlockId(i, j, k + 1);
		final int k1 = iblockaccess.getBlockId(i - 1, j, k);
		final int l1 = iblockaccess.getBlockId(i + 1, j, k);
		if((i1 == blockID) || (j1 == blockID))
		{
			if((l == 2) || (l == 3))
				return blockIndexInTexture;
			int i2 = 0;
			if(i1 == blockID)
			{
				i2 = -1;
			}
			final int k2 = iblockaccess.getBlockId(i - 1, j, i1 != blockID ? k + 1 : k - 1);
			final int i3 = iblockaccess.getBlockId(i + 1, j, i1 != blockID ? k + 1 : k - 1);
			if(l == 4)
			{
				i2 = -1 - i2;
			}
			byte byte1 = 5;
			if((Block.opaqueCubeLookup[k1] || Block.opaqueCubeLookup[k2]) && !Block.opaqueCubeLookup[l1] && !Block.opaqueCubeLookup[i3])
			{
				byte1 = 5;
			}
			if((Block.opaqueCubeLookup[l1] || Block.opaqueCubeLookup[i3]) && !Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[k2])
			{
				byte1 = 4;
			}
			return (l != byte1 ? blockIndexInTexture + 32 : blockIndexInTexture + 16) + i2;
		}
		if((k1 == blockID) || (l1 == blockID))
		{
			if((l == 4) || (l == 5))
				return blockIndexInTexture;
			int j2 = 0;
			if(k1 == blockID)
			{
				j2 = -1;
			}
			final int l2 = iblockaccess.getBlockId(k1 != blockID ? i + 1 : i - 1, j, k - 1);
			final int j3 = iblockaccess.getBlockId(k1 != blockID ? i + 1 : i - 1, j, k + 1);
			if(l == 3)
			{
				j2 = -1 - j2;
			}
			byte byte2 = 3;
			if((Block.opaqueCubeLookup[i1] || Block.opaqueCubeLookup[l2]) && !Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[j3])
			{
				byte2 = 3;
			}
			if((Block.opaqueCubeLookup[j1] || Block.opaqueCubeLookup[j3]) && !Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l2])
			{
				byte2 = 2;
			}
			return (l != byte2 ? blockIndexInTexture + 32 : blockIndexInTexture + 16) + j2;
		}
		byte byte0 = 3;
		if(Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[j1])
		{
			byte0 = 3;
		}
		if(Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[i1])
		{
			byte0 = 2;
		}
		if(Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[l1])
		{
			byte0 = 5;
		}
		if(Block.opaqueCubeLookup[l1] && !Block.opaqueCubeLookup[k1])
		{
			byte0 = 4;
		}
		return l != byte0 ? blockIndexInTexture : blockIndexInTexture + 1;
	}

	@Override
	public String getTextureFile() {
		return MLN.getSpritesPath();
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
