package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;

public class BlockOrientedBrick extends Block {

	/**
	 * returns a number between 0 and 3
	 */
	public static int limitToValidMetadata(int par0)
	{
		return par0 & 1;
	}


	String topTextureId,topTextureRotateId,bottomTextureId,bottomTextureRotateId,frontbackTextureId,sideTextureId;
	Icon topTexture,topTextureRotate,bottomTexture,bottomTextureRotate,frontbackTexture,sideTexture;

	public BlockOrientedBrick(int blockId,
			String topTextureId,String topTextureRotateId,String bottomTextureId,String bottomTextureRotateId,
			String frontbackTextureId,String sideTextureId)
	{
		super(blockId, Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);

		this.topTextureId=topTextureId;
		this.topTextureRotateId=topTextureRotateId;
		this.bottomTextureId=bottomTextureId;
		this.bottomTextureRotateId=bottomTextureRotateId;
		this.frontbackTextureId=frontbackTextureId;
		this.sideTextureId=sideTextureId;
	}
	
	@Override
	public void func_94332_a(IconRegister iconRegister)
	{
		topTexture=MillCommonUtilities.getIcon(iconRegister, topTextureId);
		topTextureRotate=MillCommonUtilities.getIcon(iconRegister, topTextureRotateId);
		bottomTexture=MillCommonUtilities.getIcon(iconRegister, bottomTextureId);
		bottomTextureRotate=MillCommonUtilities.getIcon(iconRegister, bottomTextureRotateId);
		frontbackTexture=MillCommonUtilities.getIcon(iconRegister, frontbackTextureId);
		sideTexture=MillCommonUtilities.getIcon(iconRegister, sideTextureId);
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
	public Icon getBlockTextureFromSideAndMetadata(int side, int meta)
	{

		if (side == 0) {
			if (meta==0)
				return bottomTexture;
			else
				return bottomTextureRotate;
		}

		if (side == 1) {
			if (meta==0)
				return topTexture;
			else
				return topTextureRotate;
		}

		int turn=0;

		if ((side==4) || (side==5)) {
			turn=1;
		}

		if (meta==1) {
			turn=1-turn;
		}

		if ((turn == 0))
			return frontbackTexture;
		if ((turn == 1))
			return sideTexture;
		return sideTexture;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
	{

		final int var6 = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;

		if (var6 == 0)
		{
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 0,true);
		}

		if (var6 == 1)
		{
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 1,true);
		}

		if (var6 == 2)
		{
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 0,true);
		}

		if (var6 == 3)
		{
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 1,true);
		}
	}
}
