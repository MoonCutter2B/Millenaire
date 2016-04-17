package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockOrientedBrick extends Block {

	/**
	 * returns a number between 0 and 3
	 */
	public static int limitToValidMetadata(final int par0) {
		return par0 & 1;
	}

	String topTextureId, topTextureRotateId, bottomTextureId, bottomTextureRotateId, frontbackTextureId, sideTextureId;
	IIcon topTexture, topTextureRotate, bottomTexture, bottomTextureRotate, frontbackTexture, sideTexture;

	public BlockOrientedBrick(final String topTextureId, final String topTextureRotateId, final String bottomTextureId, final String bottomTextureRotateId, final String frontbackTextureId,
			final String sideTextureId) {
		super(Material.rock);
		this.setCreativeTab(Mill.tabMillenaire);

		this.topTextureId = topTextureId;
		this.topTextureRotateId = topTextureRotateId;
		this.bottomTextureId = bottomTextureId;
		this.bottomTextureRotateId = bottomTextureRotateId;
		this.frontbackTextureId = frontbackTextureId;
		this.sideTextureId = sideTextureId;
	}

	/**
	 * Returns an item stack containing a single instance of the current block
	 * type. 'i' is the block's subtype/damage and is ignored for blocks which
	 * do not support subtypes. Blocks which cannot be harvested should return
	 * null.
	 */
	@Override
	protected ItemStack createStackedBlock(final int par1) {
		return new ItemStack(this, 1, 0);
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and
	 * wood.
	 */
	@Override
	public int damageDropped(final int par1) {
		return 0;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	@Override
	public IIcon getIcon(final int side, final int meta) {

		if (side == 0) {
			if (meta == 0) {
				return bottomTexture;
			} else {
				return bottomTextureRotate;
			}
		}

		if (side == 1) {
			if (meta == 0) {
				return topTexture;
			} else {
				return topTextureRotate;
			}
		}

		int turn = 0;

		if (side == 4 || side == 5) {
			turn = 1;
		}

		if (meta == 1) {
			turn = 1 - turn;
		}

		if (turn == 0) {
			return frontbackTexture;
		}
		if (turn == 1) {
			return sideTexture;
		}
		return sideTexture;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(final World world, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLiving, final ItemStack par6ItemStack) {

		final int var6 = MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (var6 == 0) {
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 0, true);
		}

		if (var6 == 1) {
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 1, true);
		}

		if (var6 == 2) {
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 0, true);
		}

		if (var6 == 3) {
			MillCommonUtilities.setBlockMetadata(world, par2, par3, par4, 1, true);
		}
	}

	@Override
	public void registerBlockIcons(final IIconRegister iconRegister) {
		topTexture = MillCommonUtilities.getIcon(iconRegister, topTextureId);
		topTextureRotate = MillCommonUtilities.getIcon(iconRegister, topTextureRotateId);
		bottomTexture = MillCommonUtilities.getIcon(iconRegister, bottomTextureId);
		bottomTextureRotate = MillCommonUtilities.getIcon(iconRegister, bottomTextureRotateId);
		frontbackTexture = MillCommonUtilities.getIcon(iconRegister, frontbackTextureId);
		sideTexture = MillCommonUtilities.getIcon(iconRegister, sideTextureId);
	}
}
