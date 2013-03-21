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

	HashMap<Integer,String> textureNames=new HashMap<Integer,String>();
	HashMap<Integer,Icon> textures=new HashMap<Integer,Icon>();

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

		for (final int meta: textureNames.keySet()) {
			list.add(new ItemStack(blockID,1,meta));
		}

	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}
	
	


	@Override
	public void onBlockDestroyedByExplosion(World world, int i,
			int j, int k, Explosion par5Explosion) {
		
		MillCommonUtilities.setBlockAndMetadata(world, i, j, k, 0, 0, true, false);
		
		for (int y=EXPLOSION_RADIUS;y>=-EXPLOSION_RADIUS;y--) {
			if (((y+j)>=0) && ((y+j)<128)) {
				for (int x=-EXPLOSION_RADIUS;x<=EXPLOSION_RADIUS;x++) {
					for (int z=-EXPLOSION_RADIUS;z<=EXPLOSION_RADIUS;z++) {
						if (((x*x)+(y*y)+(z*z))<=(EXPLOSION_RADIUS*EXPLOSION_RADIUS)) {
							final int bid=world.getBlockId(i+x, j+y, k+z);
							if (bid>0) {
								//if (bid!=Block.bedrock.blockID)
								//	Block.blocksList[bid].dropBlockAsItemWithChance(world,  i,j,k, world.getBlockMetadata(i,j,k), 0.1F);
								
								MillCommonUtilities.setBlockAndMetadata(world,i+x, j+y, k+z, 0, 0, true, false);
								Block.blocksList[bid].onBlockDestroyedByExplosion(world, i,j,k, par5Explosion);
							}
						}
					}
				}
			}
		}
		
		super.onBlockDestroyedByExplosion(world, i, j, k, par5Explosion);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int i, int meta)  {
		if (textures.containsKey(meta))
			return textures.get(meta);
		else
			return textures.get(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{

		for (final int meta : textures.keySet()) {
			par3List.add(new ItemStack(par1, 1, meta));
		}
	}

	public void registerName(int meta, String name) {
		names.put(meta, name);
	}

	public void registerTexture(int meta, String name) {
		textureNames.put(meta, name);
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		for (int meta : textureNames.keySet()) {
			textures.put(meta, MillCommonUtilities.getIcon(iconRegister, textureNames.get(meta)));
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
