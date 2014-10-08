package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import org.millenaire.common.core.MillCommonUtilities;

public class BlockMillCrops extends BlockCrops implements IGrowable {

	private final String textureNames[];

	private final IIcon textures[];

	private final boolean requireIrrigation;

	private final boolean slowGrowth;

	private IPlantable seed;

	public BlockMillCrops(final String textureNames[], final boolean requireIrrigation, final boolean slowGrowth) {
		textures = new IIcon[8];
		this.textureNames = textureNames;
		this.requireIrrigation = requireIrrigation;
		this.slowGrowth = slowGrowth;
		setTickRandomly(true);
		final float f = 0.5F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
	}

	@Override
	protected Item func_149865_P() {
		return (Item) seed;
	}

	@Override
	protected Item func_149866_i() {
		return (Item) seed;
	}

	@Override
	public ArrayList getDrops(final World world, final int x, final int y, final int z, final int metadata, final int fortune) {
		final ArrayList ret = new ArrayList();
		ret.add(new ItemStack(func_149865_P(), 1, 0));
		if (metadata == 7) {
			for (int n = 0; n < 3 + fortune; n++) {
				if (world.rand.nextInt(15) <= metadata) {
					ret.add(new ItemStack(func_149865_P(), 1, 0));
				}
			}

		}
		return ret;
	}

	@Override
	public int getFireSpreadSpeed(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection face) {
		return 60;
	}

	@Override
	public int getFlammability(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection face) {
		return 150;
	}

	protected float getGrowthRate(final World world, final int i, final int j, final int k) {
		final int irrigation = world.getBlockMetadata(i, j - 1, k);
		if (requireIrrigation && irrigation == 0) {
			return 0.0F;
		}
		return !slowGrowth ? 8.0F : 4.0F;
	}

	@Override
	public IIcon getIcon(final int p_149691_1_, int meta) {
		if (meta < 0 || meta > 7) {
			meta = 7;
		}
		return textures[meta];
	}

	@Override
	public Item getItemDropped(final int par1, final Random par2Random, final int par3) {
		return (Item) seed;
	}

	@Override
	public int getRenderType() {
		return 6;
	}

	public IPlantable getSeed() {
		return seed;
	}

	@Override
	public int quantityDropped(final Random par1Random) {
		return 1;
	}

	@Override
	public void registerBlockIcons(final IIconRegister iconRegister) {
		for (int i = 0; i < 8; i++) {
			textures[i] = MillCommonUtilities.getIcon(iconRegister, textureNames[i]);
		}

	}

	public void setSeed(final IPlantable seed) {
		this.seed = seed;
	}

	@Override
	public void updateTick(final World world, final int i, final int j, final int k, final Random random) {
		checkAndDropBlock(world, i, j, k);
		if (world.getBlockLightValue(i, j + 1, k) >= 9) {
			final int l = world.getBlockMetadata(i, j, k);
			if (l < 7) {
				final float f = getGrowthRate(world, i, j, k);
				if (f > 0.0F && random.nextInt((int) (100F / f)) == 0) {
					MillCommonUtilities.setBlockMetadata(world, i, j, k, l + 1, true);
				}
			}
		}
	}
}
