package org.millenaire.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.Millenaire;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockDecorativeCarving extends BlockDecorativeOriented {

	BlockDecorativeCarving(Material materialIn) {
		super(materialIn);
		this.setCreativeTab(Millenaire.tabMillenaire);
	}

	@Override
	public boolean isOpaqueCube() { return false; }

	@Override
	public boolean isFullBlock() { return false; }

	@Override
    public boolean isFullCube() { return false; }

    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue() { return 0.85F; }

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (iblockstate.getBlock() == this)
		{
			if (iblockstate.getValue(FACING) == EnumFacing.NORTH || iblockstate.getValue(FACING) == EnumFacing.SOUTH)
			{
				this.setBlockBounds(0.25F, 0.0F, 0.0F, 0.75F, 0.5F, 1.0F);
			}
			else
			{
				this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 0.5F, 0.75F);
			}
		}

	}
}