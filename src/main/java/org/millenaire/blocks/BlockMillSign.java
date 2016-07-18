package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.entities.TileEntityMillSign;

import net.minecraft.block.BlockSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMillSign extends BlockSign
{
	public BlockMillSign()
	{
		super();
		
		this.setBlockUnbreakable();
	}
	
	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }
	
	@Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityMillSign();
    }
}
