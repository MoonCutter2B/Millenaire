package org.millenaire.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillPathSlab extends BlockSlab
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockMillPath.EnumType.class);
	private static final PropertyBool SEAMLESS = PropertyBool.create("seamless");

	BlockMillPathSlab()
	{
		super(Material.ground);

		if(this.isDouble())
        	this.setDefaultState(this.blockState.getBaseState().withProperty(SEAMLESS, true));
        else
        	this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM));
		
		this.useNeighborBrightness = true;
	}
	
	@Override
	public boolean isDouble() { return false; }

	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(MillBlocks.blockMillPathSlab);
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos) { return Item.getItemFromBlock(MillBlocks.blockMillPathSlab); }
    
	@Override
    public boolean isFullCube() { return false; }
	
	@Override
    public boolean isOpaqueCube() { return false; }
	
    @Override
    public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
        if (this.isDouble())
        {
            return false;
        }
        
        // face is on the block being rendered, not this block.
        EnumBlockHalf side = world.getBlockState(pos).getValue(HALF);
        return (side == EnumBlockHalf.TOP && face == EnumFacing.DOWN) || (side == EnumBlockHalf.BOTTOM && face == EnumFacing.UP);
    }

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        if (this.isDouble())
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == this)
            {
                if (iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
                {
                    this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 0.9375F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.4375F, 1.0F);
                }
            }
        }
    }

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + BlockMillPath.EnumType.byMetadata(meta).getUnlocalizedName();
    }
    
	@Override
    public IProperty getVariantProperty() { return VARIANT; }

    @Override
    public Object getVariant(ItemStack stack)
    {
        return BlockMillPath.EnumType.byMetadata(stack.getMetadata() & 7);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        if (itemIn != Item.getItemFromBlock(MillBlocks.blockMillPathSlabDouble))
        {
            BlockMillPath.EnumType[] aenumtype = BlockMillPath.EnumType.values();
            int i = aenumtype.length;

            for (int j = 0; j < i; ++j)
            {
                BlockMillPath.EnumType enumtype = aenumtype[j];
                list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
            }
        }
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockMillPath.EnumType.byMetadata(meta & 7));

        if (this.isDouble())
        {
            iblockstate = iblockstate.withProperty(SEAMLESS, (meta & 8) != 0);
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
        int i = b0 | ((BlockMillPath.EnumType)state.getValue(VARIANT)).getMetadata();

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
        return this.isDouble() ? new BlockState(this, new IProperty[] {SEAMLESS, VARIANT}): new BlockState(this, new IProperty[] {HALF, VARIANT});
    }

	@Override
    public int damageDropped(IBlockState state)
    {
        return ((BlockMillPath.EnumType)state.getValue(VARIANT)).getMetadata();
    }
}
