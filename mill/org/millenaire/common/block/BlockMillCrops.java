// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

// Referenced classes of package net.minecraft.src:
//            BlockFlower, Block, World, EntityItem,
//            ItemStack, Item

public class BlockMillCrops extends BlockCrops
{

	String riceTexture0name,riceTexture1name,turmericTexture0name,
	turmericTexture1name,maizeTexture0name,maizeTexture1name,vineTexture0name,vineTexture1name;
	
	Icon riceTexture0,riceTexture1,turmericTexture0,
	turmericTexture1,maizeTexture0,maizeTexture1,vineTexture0,vineTexture1;

	public BlockMillCrops(int i)
	{
		super(i);

		riceTexture0name="rice0";
		riceTexture1name="rice1";
		turmericTexture0name="turmeric0";
		turmericTexture1name="turmeric1";
		maizeTexture0name="maize0";
		maizeTexture1name="maize1";
		vineTexture0name="vine0";
		vineTexture1name="vine1";

		setTickRandomly(true);
		final float f = 0.5F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
	}

	@Override
	public void func_94332_a(IconRegister iconRegister)
	{
		riceTexture0=MillCommonUtilities.getIcon(iconRegister, riceTexture0name);
		riceTexture1=MillCommonUtilities.getIcon(iconRegister, riceTexture1name);
		turmericTexture0=MillCommonUtilities.getIcon(iconRegister, turmericTexture0name);
		turmericTexture1=MillCommonUtilities.getIcon(iconRegister, turmericTexture1name);
		maizeTexture0=MillCommonUtilities.getIcon(iconRegister, maizeTexture0name);
		maizeTexture1=MillCommonUtilities.getIcon(iconRegister, maizeTexture1name);
		vineTexture0=MillCommonUtilities.getIcon(iconRegister, vineTexture0name);
		vineTexture1=MillCommonUtilities.getIcon(iconRegister, vineTexture1name);
	}
	
	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	{
		final int i = par1World.getBlockId(par2, par3, par4);

		return (i == Block.tilledField.blockID);
	}

	@Override
	public boolean canThisPlantGrowOnThisBlockID(int i)
	{
		return ((i == Block.tilledField.blockID) ||
				(i == Block.dirt.blockID));
	}

	public void fertilize(World world, int i, int j, int k)
	{
		final int crop=world.getBlockMetadata(i, j, k);
		
		int newCrop=-1;
		
		if (crop==0)
			newCrop=1;
		else if (crop==2)
			newCrop=3;
		else if (crop==4)
			newCrop=5;
		else if (crop==6)
			newCrop=7;
		
		if (newCrop>-1) {
			MillCommonUtilities.setBlockMetadata(world, i, j, k, newCrop, true);
		}
		
		
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int i, int meta)
	{
		if (meta==0)
			return riceTexture0;
		else if (meta==1)
			return riceTexture1;
		else if (meta==2)
			return turmericTexture0;
		else if (meta==3)
			return turmericTexture1;
		else if (meta==4)
			return maizeTexture0;
		else if (meta==5)
			return maizeTexture1;
		else if (meta==6)
			return vineTexture0;
		else if (meta==7)
			return vineTexture1;
		else
			return riceTexture0;
	}

	private float getGrowthRate(World world, int i, int j, int k)
	{

		final int irrigation = world.getBlockMetadata(i, j-1, k);

		final int crop=world.getBlockMetadata(i, j, k);
		if (crop==0) {
			if (irrigation==0)
				return 0;

			return 2;
		} else if (crop==6)
			return 1;
		else
			return 2;
	}

	@Override
	public int getRenderType()
	{
		return 6;
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
    {
		return idDropped(par1);
    }


	public int idDropped(int meta)
	{
		if ((meta == 0) || (meta == 1))
			return Mill.rice.itemID;
		else if ((meta == 2) || (meta == 3))
			return Mill.turmeric.itemID;
		else if ((meta == 4) || (meta == 5))
			return Mill.maize.itemID;
		else if ((meta == 6) || (meta == 7))
			return Mill.grapes.itemID;
		else
			return -1;
	}
	
	/**
     * Returns the quantity of items to drop on block destruction.
     */
	@Override
    public int quantityDropped(Random par1Random)
    {		
        return 1;
    }
	
    @Override 
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret =  new ArrayList<ItemStack>();

        ret.add(new ItemStack(idDropped(metadata), 1, 0));
        
        if (metadata %2 == 1)
        {
            for (int n = 0; n < 3 + fortune; n++)
            {
                if (world.rand.nextInt(15) <= metadata)
                {
                    ret.add(new ItemStack(idDropped(metadata), 1, 0));
                }
            }
        }

        return ret;
    }

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, int par5)
	{
		final int soilId = world.getBlockId(i, j-1, k);

		//reverting field decay
		if (soilId==Block.dirt.blockID) {
			MillCommonUtilities.setBlockAndMetadata(world, i, j-1, k, Block.tilledField.blockID, 0, true, false);
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random)
	{
		
		checkFlowerChange(world, i, j, k);

		if(world.getBlockLightValue(i, j + 1, k) >= 9)
		{
			final int l = world.getBlockMetadata(i, j, k);
			if(l == 0)//rice
			{
				final float f = getGrowthRate(world, i, j, k);
				if (f>0) {
					if(random.nextInt((int)(100F / f)) == 0)
					{
						MillCommonUtilities.setBlockMetadata(world, i,j,k, 1,true);
					}
				}
			} else if(l == 2)//turmeric
			{
				final float f = getGrowthRate(world, i, j, k);
				if (f>0) {
					if(random.nextInt((int)(100F / f)) == 0)
					{
						MillCommonUtilities.setBlockMetadata(world, i,j,k, 3,true);
					}
				}
			} else if(l == 4)//maize
			{
				final float f = getGrowthRate(world, i, j, k);
				if (f>0) {
					if(random.nextInt((int)(100F / f)) == 0)
					{
						MillCommonUtilities.setBlockMetadata(world, i,j,k, 5,true);
					}
				}
			} else if(l == 6)//vine
			{
				final float f = getGrowthRate(world, i, j, k);
				if (f>0) {
					if(random.nextInt((int)(100F / f)) == 0)
					{
						MillCommonUtilities.setBlockMetadata(world, i,j,k, 7,true);
					}
				}
			}
		}
	}
}
