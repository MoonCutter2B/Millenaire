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

		public ItemDecorativeSlab(Block b) {
			super(b,Mill.pathSlab,Mill.path,b.isOpaqueCube());	  
			block=(BlockDecorativeSlab) b;

			setMaxDamage(0);
			setHasSubtypes(true);
			this.theHalfSlabDec = Mill.pathSlab;
			this.doubleSlabDec = Mill.path;
			this.isFullBlockDec = b.isOpaqueCube(); 	
		}

		public ItemDecorativeSlab(BlockDecorativeSlab b,BlockDecorativeSlab halfSlab,BlockDecorativeSlab fullBlock, boolean full)
		{
			super(b,halfSlab,fullBlock,full);

			block=b;

			setMaxDamage(0);
			setHasSubtypes(true);
			this.theHalfSlabDec = halfSlab;
			this.doubleSlabDec = fullBlock;
			this.isFullBlockDec = full;
		}

		@Override
		public IIcon getIconFromDamage(int i)
		{
			return block.getIcon(2, i);
		}

		@Override
		public String getUnlocalizedName(ItemStack itemstack)
		{
			return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
		}

		@Override
		public int getMetadata(int i)
		{
			return i;
		}



		@Override
		public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
		{
			if (this.isFullBlockDec)
			{
				return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
			}
			else if (par1ItemStack.stackSize == 0)
			{
				return false;
			}
			else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
			{
				return false;
			}
			else
			{
				Block block = par3World.getBlock(par4, par5, par6);
				int j1 = par3World.getBlockMetadata(par4, par5, par6);

				if ((par7 == 1) && block == this.theHalfSlabDec && j1 == par1ItemStack.getItemDamage())
				{
					if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlabDec, j1, 3))
					{
						par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.doubleSlabDec.stepSound.soundName, (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlabDec.stepSound.getPitch() * 0.8F);
						--par1ItemStack.stackSize;
					}

					return true;
				}
				else
				{
					return this.func_77888_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
				}
			}
		}

		private boolean func_77888_a(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
		{
			if (par7 == 0)
			{
				--par5;
			}

			if (par7 == 1)
			{
				++par5;
			}

			if (par7 == 2)
			{
				--par6;
			}

			if (par7 == 3)
			{
				++par6;
			}

			if (par7 == 4)
			{
				--par4;
			}

			if (par7 == 5)
			{
				++par4;
			}

			Block block = par3World.getBlock(par4, par5, par6);
			int j1 = par3World.getBlockMetadata(par4, par5, par6);
			int k1 = j1 & 7;

			if (block == this.theHalfSlabDec && k1 == par1ItemStack.getItemDamage())
			{
				if (par3World.checkBlockCollision(this.doubleSlabDec.getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.doubleSlabDec, k1, 3))
				{
					par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.doubleSlabDec.stepSound.soundName, (this.doubleSlabDec.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlabDec.stepSound.getPitch() * 0.8F);
					--par1ItemStack.stackSize;
				}

				return true;
			}
			else
			{
				return false;
			}
		}
	}

	public static int getBlockFromDye(int i)
	{
		return i;
	}
	public static int getDyeFromBlock(int i)
	{
		return i;
	}

	HashMap<Integer,String> textureSideNames=new HashMap<Integer,String>();
	HashMap<Integer,String> textureTopNames=new HashMap<Integer,String>();
	HashMap<Integer,String> textureBottomNames=new HashMap<Integer,String>();
	HashMap<Integer,IIcon> texturesSide=new HashMap<Integer,IIcon>();
	HashMap<Integer,IIcon> texturesTop=new HashMap<Integer,IIcon>();
	HashMap<Integer,IIcon> texturesBottom=new HashMap<Integer,IIcon>();

	HashMap<Integer,String> names=new HashMap<Integer,String>();

	public BlockDecorativeSlab(Material material, boolean full) {
		super(full, material);
		setTickRandomly(true);
		setLightOpacity(0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public int damageDropped(int par1)
	{
		return (par1 & 7) | 8;
	}

	@Override
	public IIcon getIcon(int side, int meta)  {

		if (side==1) {
			if (texturesTop.containsKey(meta))
				return texturesTop.get(meta);
			else
				return texturesTop.get(0);
		} else if (side==0) {
			if (texturesBottom.containsKey(meta))
				return texturesBottom.get(meta);
			else
				return texturesBottom.get(0);
		} else {
			if (texturesSide.containsKey(meta))
				return texturesSide.get(meta);
			else
				return texturesTop.get(0);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (final int meta : texturesSide.keySet()) {
			if (meta>=8)
				par3List.add(new ItemStack(item, 1, meta));
		}
	}

	public void registerTexture(int meta, String name) {
		textureTopNames.put(meta, name);
		textureBottomNames.put(meta, name);
		textureSideNames.put(meta, name);

		textureTopNames.put(meta | 8, name);
		textureBottomNames.put(meta | 8, name);
		textureSideNames.put(meta | 8, name);
	}

	public void registerTexture(int meta, String top,String bottom,String side) {
		textureTopNames.put(meta, top);
		textureBottomNames.put(meta, bottom);
		textureSideNames.put(meta, side);

		textureTopNames.put(meta | 8, top);
		textureBottomNames.put(meta | 8, bottom);
		textureSideNames.put(meta | 8, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for (int meta : textureTopNames.keySet()) {
			texturesTop.put(meta, MillCommonUtilities.getIcon(iconRegister, textureTopNames.get(meta)));
		}
		for (int meta : textureBottomNames.keySet()) {
			texturesBottom.put(meta, MillCommonUtilities.getIcon(iconRegister, textureBottomNames.get(meta)));
		}
		for (int meta : textureSideNames.keySet()) {
			texturesSide.put(meta, MillCommonUtilities.getIcon(iconRegister, textureSideNames.get(meta)));
		}
	}

	@Override
	public String func_150002_b(int meta) {
		return names.get(meta);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		if (this.opaque)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	@Override
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int meta)
	{
		return meta;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if (this.opaque)
		{
			return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
		}
		else if (par5 != 1 && par5 != 0 && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 1;
	}
}
