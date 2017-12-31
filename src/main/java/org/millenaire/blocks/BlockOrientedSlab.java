package org.millenaire.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOrientedSlab extends BlockSlab
{
    private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
	
	private Block singleSlab;

    BlockOrientedSlab(Material materialIn, Block singleSlabIn)
	{
		super(materialIn);
		singleSlab = singleSlabIn;
		
		this.useNeighborBrightness = true;
	}

	@Override
	public boolean isDouble() 
	{
		return false;
	}
	
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
    	if(singleSlab != null)
    		return Item.getItemFromBlock(singleSlab);
    	else
    		return super.getItemDropped(state, rand, fortune);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos)
    {
    	if(singleSlab != null)
    		return Item.getItemFromBlock(singleSlab);
    	else
    		return super.getItem(worldIn, pos);
    }

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName();
    }
    
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        return this.isDouble() ? iblockstate : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? iblockstate : iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
    }
    
	@Override
    public IProperty getVariantProperty()
    {
        return FACING;
    }

    @Override
    public Object getVariant(ItemStack stack)
    {
        return EnumFacing.getHorizontal(3);//Boolean.valueOf((stack.getMetadata() & 8) != 0);
    }
    
    @SideOnly(Side.CLIENT)
    private static boolean isSlabX(Block blockIn)
    {
        return blockIn instanceof BlockSlab;
    }
    
    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        if (this.isDouble())
        {
            return super.shouldSideBeRendered(worldIn, pos, side);
        }
    	else if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(worldIn, pos, side))
        {
            return false;
        }
        else
        {
            BlockPos blockpos1 = pos.offset(side.getOpposite());
            IBlockState iblockstate = worldIn.getBlockState(pos);
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
            boolean flag = isSlabX(iblockstate.getBlock()) && iblockstate.getValue(HALF) == BlockOrientedSlab.EnumBlockHalf.TOP;
            boolean flag1 = isSlabX(iblockstate1.getBlock()) && iblockstate1.getValue(HALF) == BlockOrientedSlab.EnumBlockHalf.TOP;
            return flag1 ? (side == EnumFacing.DOWN ? true : (side == EnumFacing.UP && super.shouldSideBeRendered(worldIn, pos, side) ? true : !isSlabX(iblockstate.getBlock()) || !flag)) : (side == EnumFacing.UP ? true : (side == EnumFacing.DOWN && super.shouldSideBeRendered(worldIn, pos, side) ? true : !isSlabX(iblockstate.getBlock()) || flag));
        }
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(3));

        if (this.isDouble())
        {
            iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf((meta & 8) != 0));
        }
        else
        {
            iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        byte b0 = 0;
        int i = b0;

        if (this.isDouble())
        {
            if (state.getValue(SEAMLESS))
            {
                i |= 8;
            }
        }
        else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }
	
	@Override
    protected BlockState createBlockState()
    {
        return this.isDouble() ? new BlockState(this, new IProperty[] {SEAMLESS, FACING}): new BlockState(this, new IProperty[] {HALF, FACING});
    }
}