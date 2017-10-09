package org.millenaire.items;

import org.millenaire.blocks.BlockDecorativeOrientedSlabDouble;
import org.millenaire.blocks.BlockDecorativeOrientedSlabHalf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;

public class ItemOrientedSlab extends ItemSlab {

	public ItemOrientedSlab(Block block, BlockDecorativeOrientedSlabHalf singleSlab,
			BlockDecorativeOrientedSlabDouble doubleSlab) {
		super(block, (BlockSlab) singleSlab, (BlockSlab) doubleSlab);

		this.setHasSubtypes(false);
	}

	@Override
	public int getMetadata(int damage) {
		return 0;
	}
}
