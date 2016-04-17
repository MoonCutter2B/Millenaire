package org.millenaire.blocks;

import org.millenaire.entities.TileEntityVillageStone;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockVillageStone extends BlockContainer
{

	protected BlockVillageStone() 
	{
		super(Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityVillageStone();
	}

}
