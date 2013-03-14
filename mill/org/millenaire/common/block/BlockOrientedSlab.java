package org.millenaire.common.block;

import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.forge.Mill;

public class BlockOrientedSlab extends BlockHalfSlab {

	public BlockOrientedSlab(int par1)
	{
		super(par1, false, Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		setLightOpacity(0);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int par1)
	{
		return new ItemStack(blockID, 2, 0);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta)
	{
		meta = meta & 1;

		if ((side == 1) || (side == 0)) {//top & bottom
			if (meta==0)
				return 52;
			else
				return 50;
		}

		int turn=0;

		if ((side==4) || (side==5)) {
			turn=1;
		}

		if (meta==1) {
			turn=1-turn;
		}

		if ((turn == 0))
			return 51;
		if ((turn == 1))
			return 50;
		return 50;
	}

	@Override
	public String getFullSlabName(int var1) {
		return "byzantinebrick";
	}

	@Override
	public String getTextureFile() {
		return MLN.getSpritesPath();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
	{

		if(par1World.getBlockId(x, y - 1, z) == blockID){

			final int meta = par1World.getBlockMetadata(x, y-1, z);
			par1World.setBlockAndMetadata(x, y-1, z, Mill.byzantine_tiles.blockID,meta & 1);
			par1World.setBlockWithNotify(x, y, z, 0);

		} else {

			final int var6 = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;

			final int meta = par1World.getBlockMetadata(x, y, z);

			if (var6 == 0)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 0 | meta);
			}

			if (var6 == 1)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 1 | meta);
			}

			if (var6 == 2)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 0 | meta);
			}

			if (var6 == 3)
			{
				par1World.setBlockMetadataWithNotify(x, y, z, 1 | meta);
			}
		}
	}
}
