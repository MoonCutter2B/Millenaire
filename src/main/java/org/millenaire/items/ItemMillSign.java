package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.blocks.BlockMillSign;
import org.millenaire.blocks.MillBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMillSign extends Item
{
	ItemMillSign() { this.setCreativeTab(Millenaire.tabMillenaire); }

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (side == EnumFacing.DOWN)
		{
			return false;
		}
		else if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
		{
			return false;
		}
		else
		{
			pos = pos.offset(side);

			if (!playerIn.canPlayerEdit(pos, side, stack))
			{
				return false;
			}
			else if (worldIn.isRemote)
			{
				return true;
			}
			else
			{
				worldIn.setBlockState(pos, MillBlocks.blockMillSign.getDefaultState().withProperty(BlockMillSign.FACING, side)/*Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, side)*/, 3);


				--stack.stackSize;
				TileEntity tileentity = worldIn.getTileEntity(pos);

				return true;
			}
		}
	}
}