package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockDecorativeSlab extends BlockHalfSlab {

	public static class ItemDecorativeSlab extends ItemSlab {

		BlockDecorativeSlab block;

		public ItemDecorativeSlab(int i,BlockDecorativeSlab halfSlab,BlockDecorativeSlab fullBlock, boolean full)
		{
			super(i,halfSlab,fullBlock,full);
			setMaxDamage(0);
			setHasSubtypes(true);
			this.block=(BlockDecorativeSlab)Block.blocksList[i+256];
		}

		@Override
		public Icon getIconFromDamage(int i)
		{
			return block.getBlockTextureFromSideAndMetadata(2, i);
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
	HashMap<Integer,Icon> texturesSide=new HashMap<Integer,Icon>();
	HashMap<Integer,Icon> texturesTop=new HashMap<Integer,Icon>();
	HashMap<Integer,Icon> texturesBottom=new HashMap<Integer,Icon>();

	HashMap<Integer,String> names=new HashMap<Integer,String>();

	public BlockDecorativeSlab(int i, Material material, boolean full) {
		super(i, full, material);
		setTickRandomly(true);
		setLightOpacity(0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addCreativeItems(@SuppressWarnings("rawtypes") ArrayList itemList) {
		final ArrayList<ItemStack> list=itemList;

		for (final int meta: textureSideNames.keySet()) {
			if (meta<8)
				list.add(new ItemStack(blockID,1,meta));
		}

	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int meta)  {

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
				return texturesSide.get(0);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (final int meta : texturesSide.keySet()) {
			if (meta<8)
				par3List.add(new ItemStack(par1, 1, meta));
		}
	}

	public void registerName(int meta, String name) {
		names.put(meta, name);
		names.put(meta | 8, name);
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
	public void registerIcons(IconRegister iconRegister)
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
	public String getFullSlabName(int meta) {
		return names.get(meta);
	}
}
