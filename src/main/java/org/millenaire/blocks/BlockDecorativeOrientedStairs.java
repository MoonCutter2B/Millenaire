package org.millenaire.blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class BlockDecorativeOrientedStairs extends BlockStairs {
	public BlockDecorativeOrientedStairs(IBlockState modelState) {
		super(modelState);

		this.useNeighborBrightness = true;
	}

}
