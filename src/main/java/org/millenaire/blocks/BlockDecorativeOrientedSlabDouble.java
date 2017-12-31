package org.millenaire.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockDecorativeOrientedSlabDouble extends BlockOrientedSlab
{
	BlockDecorativeOrientedSlabDouble(Material materialIn, Block blockIn) { super(materialIn, blockIn); }

	@Override
	public boolean isDouble() { return true; }
}
