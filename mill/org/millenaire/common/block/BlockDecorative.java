package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BlockDecorative extends Block {

	public static class ItemDecorative extends ItemBlock {

		BlockDecorative block;

		public ItemDecorative(int i)
		{
			super(i);
			setMaxDamage(0);
			setHasSubtypes(true);
			this.block=(BlockDecorative)Block.blocksList[i+256];
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

	private static final int EXPLOSION_RADIUS = 20;

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

	public BlockDecorative(int i, Material material) {
		super(i, material);
		setTickRandomly(true);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addCreativeItems(@SuppressWarnings("rawtypes") ArrayList itemList) {
		final ArrayList<ItemStack> list=itemList;

		for (final int meta: textureSideNames.keySet()) {
			list.add(new ItemStack(blockID,1,meta));
		}

	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}


	public void alchemistExplosion(World world, int i,
			int j, int k) {

		MillCommonUtilities.setBlockAndMetadata(world, i, j, k, 0, 0, true, false);

		for (int y=EXPLOSION_RADIUS;y>=-EXPLOSION_RADIUS;y--) {
			if (((y+j)>=0) && ((y+j)<128)) {
				for (int x=-EXPLOSION_RADIUS;x<=EXPLOSION_RADIUS;x++) {
					for (int z=-EXPLOSION_RADIUS;z<=EXPLOSION_RADIUS;z++) {
						if (((x*x)+(y*y)+(z*z))<=(EXPLOSION_RADIUS*EXPLOSION_RADIUS)) {
							int bid=world.getBlockId(i+x, j+y, k+z);
							if (bid>0) {								
								MillCommonUtilities.setBlockAndMetadata(world,i+x, j+y, k+z, 0, 0, true, false);
							}
						}
					}
				}
			}
		}
	}



	@Override
	public void dropBlockAsItemWithChance(World par1World, int i, int j,
			int k, int meta, float par6, int par7) {

		if (blockID==Mill.stone_decoration.blockID && meta==3) {
			boolean isExplosion=false;

			//Hack to check whether the drop is due to an explosion
			final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

			for (int id=0;id<trace.length;id++) {
				String className=trace[id].getClassName();
				if (className.equals(Explosion.class.getName()))
					isExplosion=true;
			}
			
			if (isExplosion) {
				alchemistExplosion(par1World,i,j,k);
				return;
			}

		}


		super.dropBlockAsItemWithChance(par1World, i, j, k, meta, par6, par7);
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
			par3List.add(new ItemStack(par1, 1, meta));
		}
	}

	public void registerName(int meta, String name) {
		names.put(meta, name);
	}

	public void registerTexture(int meta, String name) {
		textureTopNames.put(meta, name);
		textureBottomNames.put(meta, name);
		textureSideNames.put(meta, name);
	}

	public void registerTexture(int meta, String top,String bottom,String side) {
		textureTopNames.put(meta, top);
		textureBottomNames.put(meta, bottom);
		textureSideNames.put(meta, side);
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
	public void updateTick(World world, int i, int j, int k, Random random)
	{

		final int meta=world.getBlockMetadata(i, j, k);

		if ((blockID==Mill.earth_decoration.blockID) && (meta==0)) {
			if(world.getBlockLightValue(i, j + 1, k) >= 15)
			{
				if(MillCommonUtilities.chanceOn(5))
				{
					MillCommonUtilities.setBlockAndMetadata(world,i, j, k,Mill.stone_decoration.blockID, 1, true, false);
				}
			}
		} else if ((blockID==Mill.wood_decoration.blockID) && (meta==3)) {
			if(world.getBlockLightValue(i, j + 1, k) <7)
			{
				if(MillCommonUtilities.chanceOn(5))
				{
					MillCommonUtilities.setBlockAndMetadata(world,i, j, k,Mill.wood_decoration.blockID, 4, true, false);
				}
			}
		}

		return;
	}
}
