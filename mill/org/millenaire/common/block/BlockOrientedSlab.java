package org.millenaire.common.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockOrientedSlab extends BlockSlab {

	public final String textureTopVertName, textureTopHorName, textureSideName;
	public IIcon textureTopVert, textureTopHor, textureSide;

	public BlockOrientedSlab(final String textureTopVertName, final String textureTopHorName, final String textureSideName) {
		super(false, Material.rock);
		this.setCreativeTab(Mill.tabMillenaire);
		setLightOpacity(0);
		this.textureTopVertName = textureTopVertName;
		this.textureTopHorName = textureTopHorName;
		this.textureSideName = textureSideName;
	}

	/**
	 * Returns an item stack containing a single instance of the current block
	 * type. 'i' is the block's subtype/damage and is ignored for blocks which
	 * do not support subtypes. Blocks which cannot be harvested should return
	 * null.
	 */
	@Override
	protected ItemStack createStackedBlock(final int par1) {
		return new ItemStack(this, 2, 0);
	}

	@Override
	public String func_150002_b(final int var1) {
		return "byzantinebrick";
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	@Override
	public IIcon getIcon(final int side, int meta) {
		meta = meta & 1;

		if (side == 1 || side == 0) {// top & bottom
			if (meta == 0) {
				return textureSide;
			} else {
				return textureTopVert;
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
			return textureTopHor;
		}
		if (turn == 1) {
			return textureTopVert;
		}
		return textureTopVert;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase par5EntityLiving, final ItemStack par6ItemStack) {

		if (world.getBlock(x, y - 1, z) == this) {

			final int meta = world.getBlockMetadata(x, y - 1, z);

			MillCommonUtilities.setBlockAndMetadata(world, x, y - 1, z, Mill.byzantine_tiles, meta & 1, true, false);
			MillCommonUtilities.setBlockAndMetadata(world, x, y, z, Blocks.air, 0, true, false);

		} else {

			final int var6 = MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			final int meta = world.getBlockMetadata(x, y, z);

			if (var6 == 0) {
				MillCommonUtilities.setBlockMetadata(world, x, y, z, 0 | meta, true);
			}

			if (var6 == 1) {
				MillCommonUtilities.setBlockMetadata(world, x, y, z, 1 | meta, true);
			}

			if (var6 == 2) {
				MillCommonUtilities.setBlockMetadata(world, x, y, z, 0 | meta, true);
			}

			if (var6 == 3) {
				MillCommonUtilities.setBlockMetadata(world, x, y, z, 1 | meta, true);
			}
		}
	}

	@Override
	public void registerBlockIcons(final IIconRegister iconRegister) {
		textureTopVert = MillCommonUtilities.getIcon(iconRegister, textureTopVertName);
		textureTopHor = MillCommonUtilities.getIcon(iconRegister, textureTopHorName);
		textureSide = MillCommonUtilities.getIcon(iconRegister, textureSideName);
	}
}
