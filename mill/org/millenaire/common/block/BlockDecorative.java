package org.millenaire.common.block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockDecorative extends Block {

	public static class ItemDecorative extends ItemBlock {

		BlockDecorative block;

		public ItemDecorative(final Block b) {
			super(b);
			setMaxDamage(0);
			setHasSubtypes(true);
			this.block = (BlockDecorative) b;
		}

		@Override
		public IIcon getIconFromDamage(final int i) {
			return block.getIcon(2, i);
		}

		@Override
		public int getMetadata(final int i) {
			return i;
		}

		@Override
		public String getUnlocalizedName(final ItemStack itemstack) {
			return new StringBuilder().append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
		}
	}

	private static final int EXPLOSION_RADIUS = 32;

	public static int getBlockFromDye(final int i) {
		return i;
	}

	public static int getDyeFromBlock(final int i) {
		return i;
	}

	HashMap<Integer, String> textureSideNames = new HashMap<Integer, String>();
	HashMap<Integer, String> textureTopNames = new HashMap<Integer, String>();
	HashMap<Integer, String> textureBottomNames = new HashMap<Integer, String>();
	HashMap<Integer, IIcon> texturesSide = new HashMap<Integer, IIcon>();
	HashMap<Integer, IIcon> texturesTop = new HashMap<Integer, IIcon>();
	HashMap<Integer, IIcon> texturesBottom = new HashMap<Integer, IIcon>();

	HashMap<Integer, String> names = new HashMap<Integer, String>();

	public BlockDecorative(final Material material) {
		super(material);
		setTickRandomly(true);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public void addCreativeItems(@SuppressWarnings("rawtypes") ArrayList
	// itemList) {
	// final ArrayList<ItemStack> list=itemList;
	//
	// for (final int meta: textureSideNames.keySet()) {
	// list.add(new ItemStack(this,1,meta));
	// }
	//
	// }

	public void alchemistExplosion(final World world, final int i, final int j, final int k) {

		MillCommonUtilities.setBlockAndMetadata(world, i, j, k, Blocks.air, 0, true, false);

		for (int y = EXPLOSION_RADIUS; y >= -EXPLOSION_RADIUS; y--) {
			if (y + j >= 0 && y + j < 128) {
				for (int x = -EXPLOSION_RADIUS; x <= EXPLOSION_RADIUS; x++) {
					for (int z = -EXPLOSION_RADIUS; z <= EXPLOSION_RADIUS; z++) {
						if (x * x + y * y + z * z <= EXPLOSION_RADIUS * EXPLOSION_RADIUS) {
							final Block block = world.getBlock(i + x, j + y, k + z);
							if (block != Blocks.air) {
								MillCommonUtilities.setBlockAndMetadata(world, i + x, j + y, k + z, Blocks.air, 0, true, false);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int damageDropped(final int i) {
		return i;
	}

	@Override
	public void dropBlockAsItemWithChance(final World par1World, final int i, final int j, final int k, final int meta, final float par6, final int par7) {

		if (this == Mill.stone_decoration && meta == 3) {
			boolean isExplosion = false;

			// Hack to check whether the drop is due to an explosion
			final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

			for (int id = 0; id < trace.length; id++) {
				final String className = trace[id].getClassName();
				if (className.equals(Explosion.class.getName())) {
					isExplosion = true;
				}
			}

			if (isExplosion) {
				alchemistExplosion(par1World, i, j, k);
				return;
			}

		}

		super.dropBlockAsItemWithChance(par1World, i, j, k, meta, par6, par7);
	}

	@Override
	public int getFireSpreadSpeed(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection face) {
		if (getMaterial() == Material.wood) {
			return 5;
		}

		return 0;
	}

	@Override
	public int getFlammability(final IBlockAccess world, final int x, final int y, final int z, final ForgeDirection face) {
		if (getMaterial() == Material.wood) {
			return 150;
		}

		return 0;
	}

	@Override
	public IIcon getIcon(final int side, final int meta) {

		if (side == 1) {
			if (texturesTop.containsKey(meta)) {
				return texturesTop.get(meta);
			} else {
				return texturesTop.get(0);
			}
		} else if (side == 0) {
			if (texturesBottom.containsKey(meta)) {
				return texturesBottom.get(meta);
			} else {
				return texturesBottom.get(0);
			}
		} else {
			if (texturesSide.containsKey(meta)) {
				return texturesSide.get(meta);
			} else {
				return texturesSide.get(0);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs par2CreativeTabs, final List par3List) {

		for (final int meta : texturesSide.keySet()) {
			par3List.add(new ItemStack(item, 1, meta));
		}
	}

	@Override
	public void registerBlockIcons(final IIconRegister iconRegister) {
		for (final int meta : textureTopNames.keySet()) {
			texturesTop.put(meta, MillCommonUtilities.getIcon(iconRegister, textureTopNames.get(meta)));
		}
		for (final int meta : textureBottomNames.keySet()) {
			texturesBottom.put(meta, MillCommonUtilities.getIcon(iconRegister, textureBottomNames.get(meta)));
		}
		for (final int meta : textureSideNames.keySet()) {
			texturesSide.put(meta, MillCommonUtilities.getIcon(iconRegister, textureSideNames.get(meta)));
		}
	}

	public void registerName(final int meta, final String name) {
		names.put(meta, name);
	}

	public void registerTexture(final int meta, final String name) {
		textureTopNames.put(meta, name);
		textureBottomNames.put(meta, name);
		textureSideNames.put(meta, name);
	}

	public void registerTexture(final int meta, final String top, final String bottom, final String side) {
		textureTopNames.put(meta, top);
		textureBottomNames.put(meta, bottom);
		textureSideNames.put(meta, side);
	}

	@Override
	public void updateTick(final World world, final int i, final int j, final int k, final Random random) {

		final int meta = world.getBlockMetadata(i, j, k);

		if (this == Mill.earth_decoration && meta == 0) {
			if (world.getBlockLightValue(i, j + 1, k) >= 15) {
				if (MillCommonUtilities.chanceOn(5)) {
					MillCommonUtilities.setBlockAndMetadata(world, i, j, k, Mill.stone_decoration, 1, true, false);
				}
			}
		} else if (this == Mill.wood_decoration && meta == 3) {
			if (world.getBlockLightValue(i, j + 1, k) < 7) {
				if (MillCommonUtilities.chanceOn(5)) {
					MillCommonUtilities.setBlockAndMetadata(world, i, j, k, Mill.wood_decoration, 4, true, false);
				}
			}
		}

		return;
	}
}
