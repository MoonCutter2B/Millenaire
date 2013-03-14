package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
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
		public int getIconFromDamage(int i)
		{
			return block.getBlockTextureFromSideAndMetadata(2, i);
		}

		@Override
		public String getItemNameIS(ItemStack itemstack)
		{
			return (new StringBuilder()).append(super.getItemName()).append(".").append(itemstack.getItemDamage()).toString();
		}

		@Override
		public int getMetadata(int i)
		{
			return i;
		}

		@Override
		public String getTextureFile() {
			return MLN.getSpritesPath();
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

	HashMap<Integer,Integer> textures=new HashMap<Integer,Integer>();

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

		for (final int meta: textures.keySet()) {
			list.add(new ItemStack(blockID,1,meta));
		}

	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}


	@Override
	public void dropBlockAsItemWithChance(World world, int i, int j, int k,
			int l, float f, int m) {

		super.dropBlockAsItemWithChance(world, i, j, k, l, f, m);



		if ((blockID==Mill.stone_decoration.blockID) && (l==3)) {//ALchimist's explosive

			//Horrible hack to make sure the method was called from the explosion class
			final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			if (stackTraceElements[2].getClassName().equals(Explosion.class.getName())) {


				world.setBlockWithNotify(i, j, k, 0);


				world.newExplosion(null, i, j, k, 30, false, true);

				for (int y=EXPLOSION_RADIUS;y>=-EXPLOSION_RADIUS;y--) {
					if (((y+j)>=0) && ((y+j)<128)) {
						for (int x=-EXPLOSION_RADIUS;x<=EXPLOSION_RADIUS;x++) {
							for (int z=-EXPLOSION_RADIUS;z<=EXPLOSION_RADIUS;z++) {
								if (((x*x)+(y*y)+(z*z))<=(EXPLOSION_RADIUS*EXPLOSION_RADIUS)) {
									final int bid=world.getBlockId(i+x, j+y, k+z);
									if (bid>0) {
										//if (bid!=Block.bedrock.blockID)
										//	Block.blocksList[bid].dropBlockAsItemWithChance(world,  i,j,k, world.getBlockMetadata(i,j,k), 0.1F);
										world.setBlockWithNotify(i+x, j+y, k+z, 0);
										Block.blocksList[bid].onBlockDestroyedByExplosion(world, i,j,k);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int meta)  {
		if (textures.containsKey(meta))
			return textures.get(meta);
		else
			return 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{

		for (final int meta : textures.keySet()) {
			par3List.add(new ItemStack(par1, 1, meta));
		}
	}

	@Override
	public String getTextureFile() {
		return MLN.getSpritesPath();
	}

	public void registerName(int meta, String name) {
		names.put(meta, name);
	}

	public void registerTexture(int meta, int tid) {
		textures.put(meta, tid);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random)
	{

		final int meta=world.getBlockMetadata(i, j, k);

		if ((blockMaterial==Material.ground) && (meta==0)) {
			if(world.getBlockLightValue(i, j + 1, k) >= 15)
			{
				if(MillCommonUtilities.chanceOn(5))
				{
					world.setBlockAndMetadataWithNotify(i, j, k,Mill.stone_decoration.blockID, 1);
				}
			}
		} else if ((blockMaterial==Material.wood) && (meta==3)) {
			if(world.getBlockLightValue(i, j + 1, k) <7)
			{
				if(MillCommonUtilities.chanceOn(5))
				{
					world.setBlockAndMetadataWithNotify(i, j, k,Mill.wood_decoration.blockID, 4);
				}
			}
		}

		return;
	}
}
