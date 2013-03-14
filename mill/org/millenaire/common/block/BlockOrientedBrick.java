package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.MLN;

public class BlockOrientedBrick extends Block {

	/**
	 * returns a number between 0 and 3
	 */
	public static int limitToValidMetadata(int par0)
	{
		return par0 & 1;
	}


	int topTextureId,topTextureRotateId,bottomTextureId,bottomTextureRotateId,frontbackTextureId,sideTextureId;

	public BlockOrientedBrick(int blockId,
			int topTextureId,int topTextureRotateId,int bottomTextureId,int bottomTextureRotateId,
			int frontbackTextureId,int sideTextureId)
	{
		super(blockId, Material.rock);
		this.blockIndexInTexture = 9;
		this.setCreativeTab(CreativeTabs.tabBlock);

		this.topTextureId=topTextureId;
		this.topTextureRotateId=topTextureRotateId;
		this.bottomTextureId=bottomTextureId;
		this.bottomTextureRotateId=bottomTextureRotateId;
		this.frontbackTextureId=frontbackTextureId;
		this.sideTextureId=sideTextureId;
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
	 * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int par1)
	{
		return new ItemStack(this.blockID, 1, 0);
	}


	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int par1)
	{
		return 0;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta)
	{

		if (side == 0) {
			if (meta==0)
				return bottomTextureId;
			else
				return bottomTextureRotateId;
		}

		if (side == 1) {
			if (meta==0)
				return topTextureId;
			else
				return topTextureRotateId;
		}

		int turn=0;

		if ((side==4) || (side==5)) {
			turn=1;
		}

		if (meta==1) {
			turn=1-turn;
		}

		if ((turn == 0))
			return frontbackTextureId;
		if ((turn == 1))
			return sideTextureId;
		return 50;
	}

	@Override
	public String getTextureFile() {
		return MLN.getSpritesPath();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving)
	{

		final int var6 = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;

		if (var6 == 0)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0);
		}

		if (var6 == 1)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1);
		}

		if (var6 == 2)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 0);
		}

		if (var6 == 3)
		{
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 1);
		}
	}
}
