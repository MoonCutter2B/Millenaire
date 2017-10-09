package org.millenaire.blocks;

import net.minecraft.block.material.Material;

public class BlockDecorativeOrientedSlabHalf extends BlockOrientedSlab {
	public BlockDecorativeOrientedSlabHalf(Material materialIn) {
		super(materialIn, null);
	}

	@Override
	public boolean isDouble() {
		return false;
	}
}
