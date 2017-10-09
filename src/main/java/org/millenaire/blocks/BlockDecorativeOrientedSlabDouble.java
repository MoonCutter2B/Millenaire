package org.millenaire.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDecorativeOrientedSlabDouble extends BlockOrientedSlab
{
	public BlockDecorativeOrientedSlabDouble(Material materialIn, Block blockIn) 
	{
		super(materialIn, blockIn);
	}

	@Override
	public boolean isDouble() 
	{
		return true;
	}
}
