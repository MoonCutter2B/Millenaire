package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.entities.TileEntityVillageStone;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockVillageStone extends BlockContainer
{

	BlockVillageStone()
	{
		super(Material.rock);
		
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
	}
	
	@Override
    public int getRenderType() { return 3; }
	
	@Override
    public int quantityDropped(Random random) { return 0; }
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if(worldIn.isRemote)
		{
			playerIn.addChatMessage(new ChatComponentText("The Village name almost seems to shimmer in the twilight"));
		}
		
		TileEntityVillageStone te = (TileEntityVillageStone) worldIn.getTileEntity(pos);
		if(te.testVar < 16)
		{
			te.testVar++;
		}
		else
		{
			te.testVar = 0;
		}

        return false;
    }
	
	public void negate(World worldIn, BlockPos pos, EntityPlayer playerIn)
	{
		TileEntityVillageStone te;
		
		if(worldIn.getTileEntity(pos) instanceof TileEntityVillageStone)
			te = (TileEntityVillageStone) worldIn.getTileEntity(pos);
		else
		{
			System.err.println("Negation failed.  TileEntity not loaded correctly.");
			return;
		}
		
		te.willExplode = true;
		worldIn.scheduleUpdate(pos, this, 60);
		worldIn.playSoundEffect(pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, "portal.portal", 1.0F, 0.01F);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		TileEntityVillageStone te;
		
		if(worldIn.getTileEntity(pos) instanceof TileEntityVillageStone)
		{
			te = (TileEntityVillageStone) worldIn.getTileEntity(pos);
			
			if(te.willExplode)
			{
				//Do Some Stuff
				worldIn.setBlockToAir(pos);
				worldIn.createExplosion(new EntityTNTPrimed(worldIn, pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, null), pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, 2.0F, true);
			}
		}
		else
		{
			System.err.println("Negation failed.  TileEntity not loaded correctly.");
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileEntityVillageStone(); }
}
