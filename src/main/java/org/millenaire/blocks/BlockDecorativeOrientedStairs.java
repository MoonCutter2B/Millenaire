package org.millenaire.blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

class BlockDecorativeOrientedStairs extends BlockStairs
{
	BlockDecorativeOrientedStairs(IBlockState modelState)
	{
		super(modelState);
		
		this.useNeighborBrightness = true;
	}

}
