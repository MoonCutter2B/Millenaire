package org.millenaire.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockDecorativeUpdate extends Block
{
	private IBlockState updateState;

	BlockDecorativeUpdate(Material materialIn, IBlockState updateIn)
	{
		super(materialIn);
		updateState = updateIn;
		this.setTickRandomly(true);
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
		int i = rand.nextInt(3);

        if (i > 1)
        {
        	worldIn.setBlockState(pos, updateState);
        }
    }
}
