package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityMillChest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockMillChest extends BlockChest
{
	BlockMillChest()
	{
		super(2);
		
		this.setBlockUnbreakable();
	}

	@Override
	public int quantityDropped(final Random random) { return 0; }
	
	@Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (hasTileEntity(state) && !(this instanceof BlockContainer))
        {
            worldIn.removeTileEntity(pos);
        }
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

            if (ilockablecontainer != null)
            {
                playerIn.openGui(Millenaire.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
	
	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) { return new TileEntityMillChest(); }
	
	@Override
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityMillChest))
        {
            return null;
        }
        else
        {
            ILockableContainer ilockablecontainer = (TileEntityMillChest)tileentity;
            
            //String doubleName = ((TileEntityMillChest)ilockablecontainer).getLargeDisplayName();

            if (this.isBlocked(worldIn, pos))
            {
                return null;
            }
            else
            {
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    BlockPos blockpos = pos.offset(enumfacing);
                    Block block = worldIn.getBlockState(blockpos).getBlock();

                    if (block == this)
                    {
                        if (this.isBlocked(worldIn, blockpos))
                        {
                            return null;
                        }

                        TileEntity tileentity1 = worldIn.getTileEntity(blockpos);

                        if (tileentity1 instanceof TileEntityChest)
                        {
                            if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH)
                            {
                                ilockablecontainer = new InventoryLargeChest("test", ilockablecontainer, (TileEntityChest)tileentity1);
                            }
                            else
                            {
                                ilockablecontainer = new InventoryLargeChest("test2", (TileEntityChest)tileentity1, ilockablecontainer);
                            }
                        }
                    }
                }

                return ilockablecontainer;
            }
        }
    }
	
    private boolean isBlocked(World worldIn, BlockPos pos)
    {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isBelowSolidBlock(World worldIn, BlockPos pos)
    {
        return worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false);
    }

    private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
    {
        for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))))
        {
            EntityOcelot entityocelot = (EntityOcelot)entity;

            if (entityocelot.isSitting())
            {
                return true;
            }
        }

        return false;
    }
}
