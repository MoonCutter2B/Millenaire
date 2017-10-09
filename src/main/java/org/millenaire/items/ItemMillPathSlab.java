package org.millenaire.items;

import org.millenaire.blocks.BlockMillPathSlabDouble;
import org.millenaire.blocks.BlockMillPathSlabHalf;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;

public class ItemMillPathSlab extends ItemSlab
{

	public ItemMillPathSlab(Block block, BlockMillPathSlabHalf singleSlab, BlockMillPathSlabDouble doubleSlab) 
	{
		super(block, (BlockSlab)singleSlab, (BlockSlab)doubleSlab);
	}
}
