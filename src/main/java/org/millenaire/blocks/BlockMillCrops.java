package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.items.ItemMillSeeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillCrops extends BlockCrops
{
	private boolean requiresIrrigation;
	private boolean slowGrowth;
	
	private IPlantable seed;
	
	BlockMillCrops(boolean irrigationIn, boolean growthIn)
	{
		super();
		
		requiresIrrigation = irrigationIn;
		slowGrowth = growthIn;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.checkAndDropBlock(worldIn, pos, state);

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
            int i = state.getValue(AGE);

            if (i < 7)
            {
                float f = getLocalGrowthChance(this, worldIn, pos);

                if(f != 0)
                {
                	if (rand.nextInt((int)(25.0F / f) + 1) == 0)
                	{
                		worldIn.setBlockState(pos, state.withProperty(AGE, i + 1), 2);
                	}
                }
            }
        }
    }
	
	private float getLocalGrowthChance(Block blockIn, World worldIn, BlockPos pos)
	{
		IBlockState groundIn = worldIn.getBlockState(pos.down());
		if(groundIn.getBlock() != Blocks.farmland)
		{
			System.err.println("BlockMillCrop growth logic not applied, unrecognized farmland");
			return getGrowthChance(blockIn, worldIn, pos);
		}
		if(requiresIrrigation && groundIn.getValue(BlockFarmland.MOISTURE) < 1) { return 0.0F; }
		else
		{
			if(slowGrowth)
			{
				return getGrowthChance(blockIn, worldIn, pos) / 2;
			}
			else
			{
				return getGrowthChance(blockIn, worldIn, pos);
			}
		}
	}
	
	@Override
    public Item getSeed() { return (Item)seed; }

	@Override
    protected Item getCrop() { return (Item)seed; }
	
	public Block setSeed(final IPlantable seedIn) 
	{
		this.seed = seedIn;
		return this;
	}
}
