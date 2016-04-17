package org.millenaire.common.block;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecorativeSlab extends BlockSlab {

	public static class ItemDecorativeSlab extends ItemSlab {

		BlockDecorativeSlab block;

		private final boolean isFullBlockDec;

		/** Instance of BlockSlab. */
		private final BlockSlab theHalfSlabDec;

		/** The double-slab block corresponding to this item. */
		private final BlockSlab doubleSlabDec;

		public ItemDecorativeSlab(final Block b) {
			super(b, Mill.pathSlab, Mill.path, b.isOpaqueCube());
			block = (BlockDecorativeSlab) b;

			setMaxDamage(0);
			setHasSubtypes(true);
			this.theHalfSlabDec = Mill.pathSlab;
			this.doubleSlabDec = Mill.path;
			this.isFullBlockDec = b.isOpaqueCube();
		}

		public ItemDecorativeSlab(final BlockDecorativeSlab b, final BlockDecorativeSlab halfSlab, final BlockDecorativeSlab fullBlock, final boolean full) {
			super(b, halfSlab, fullBlock, full);

			block = b;

			setMaxDamage(0);
			setHasSubtypes(true);
			this.theHalfSlabDec = halfSlab;
			this.doubleSlabDec = fullBlock;
			this.isFullBlockDec = full;
		}

		private boolean func_77888_a(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, int par5, int par6, final int par7) {
			if (par7 == 0) {
				--par5;
			}

			if (par7 == 1) {
				++par5;
			}

			if (par7 == 2) {
				--par6;
			}

			if (par7 == 3) {
				++par6;
			}

			if (par7 == 4) {
				--par4;
			}

			if (par7 == 5) {
				++par4;
			}

			final Block block = par3World.getBlock(par4, par5, par6);
			final int j1 = par3World.getBlockMetadata(par4, par5, par6);
			final int k1 = j1 & 7;

			if (block == this.theHalfSlabDec && k1 == par1ItemStack.getItemDamage()) {
				if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlabDec, k1, 3)) {
					par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.doubleSlabDec.stepSound.soundName, (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F,
							this.doubleSlabDec.stepSound.getPitch() * 0.8F);
					--par1ItemStack.stackSize;
				}

				return true;
			} else {
				return false;
			}
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

		@Override
		public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7,
				final float par8, final float par9, final float par10) {
			if (this.isFullBlockDec) {
				return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
			} else if (par1ItemStack.stackSize == 0) {
				return false;
			} else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
				return false;
			} else {
				final Block block = par3World.getBlock(par4, par5, par6);
				final int j1 = par3World.getBlockMetadata(par4, par5, par6);

				if (par7 == 1 && block == this.theHalfSlabDec && j1 == par1ItemStack.getItemDamage()) {
					if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6))
							&& par3World.setBlock(par4, par5, par6, this.doubleSlabDec, j1, 3)) {
						par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.doubleSlabDec.stepSound.soundName, (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F,
								this.doubleSlabDec.stepSound.getPitch() * 0.8F);
						--par1ItemStack.stackSize;
					}

					return true;
				} else {
					return this.func_77888_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6,
							par7, par8, par9, par10);
				}
			}
		}
	}

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

	public BlockDecorativeSlab(final Material material, final boolean full) {
		super(full, material);
		setTickRandomly(true);
		setLightOpacity(0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public int damageDropped(final int par1) {
		return par1 & 7 | 8;
	}

	@Override
	public String func_150002_b(final int meta) {
		return names.get(meta);
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
				return texturesTop.get(0);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs par2CreativeTabs, final List par3List) {
		for (final int meta : texturesSide.keySet()) {
			if (meta >= 8) {
				par3List.add(new ItemStack(item, 1, meta));
			}
		}
	}

	@Override
	public int onBlockPlaced(final World par1World, final int par2, final int par3, final int par4, final int par5, final float par6, final float par7, final float par8, final int meta) {
		return meta;
	}

	@Override
	public int quantityDropped(final Random par1Random) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
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

	public void registerTexture(final int meta, final String name) {
		textureTopNames.put(meta, name);
		textureBottomNames.put(meta, name);
		textureSideNames.put(meta, name);

		textureTopNames.put(meta | 8, name);
		textureBottomNames.put(meta | 8, name);
		textureSideNames.put(meta | 8, name);
	}

	public void registerTexture(final int meta, final String top, final String bottom, final String side) {
		textureTopNames.put(meta, top);
		textureBottomNames.put(meta, bottom);
		textureSideNames.put(meta, side);

		textureTopNames.put(meta | 8, top);
		textureBottomNames.put(meta | 8, bottom);
		textureSideNames.put(meta | 8, side);
	}

	@Override
	public void setBlockBoundsBasedOnState(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4) {
		if (this.opaque) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	@Override
	public boolean shouldSideBeRendered(final IBlockAccess par1IBlockAccess, final int par2, final int par3, final int par4, final int par5) {
		if (this.opaque) {
			return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
		} else if (par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5)) {
			return false;
		} else {
			return true;
		}
	}
}
