package org.millenaire.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDecorativeSodPlank extends Block
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockDecorativeSodPlank.EnumType.class);

    BlockDecorativeSodPlank()
	{
		super(Material.wood);
		this.setHardness(2F);
		this.setResistance(15F);
		this.setStepSound(Block.soundTypeWood);
	}
	
	@Override
    public int damageDropped(IBlockState state)
    {
        return ((BlockDecorativeSodPlank.EnumType)state.getValue(VARIANT)).getMetadata();
    }

	public IProperty getVariantProperty() { return VARIANT; }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        if (Block.getBlockFromItem(itemIn) == this)
        {
            BlockDecorativeSodPlank.EnumType[] aenumtype = BlockDecorativeSodPlank.EnumType.values();
            int i = aenumtype.length;

            for (int j = 0; j < i; j++)
            {
            	BlockDecorativeSodPlank.EnumType enumtype = aenumtype[j];
                list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
            }
        }
    }

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + BlockDecorativeSodPlank.EnumType.byMetadata(meta).getUnlocalizedName();
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockDecorativeSodPlank.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockDecorativeSodPlank.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState() { return new BlockState(this, new IProperty[] {VARIANT}); }

    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static enum EnumType implements IStringSerializable
    {
    	OAK(0, "oak"),
    	PINE(1, "pine"),
    	BIRCH(2, "birch"),
    	JUNGLE(3, "jungle"),
    	ACACIA(4, "acacia"),
    	DARKOAK(5, "darkoak");
        
        private static final BlockDecorativeSodPlank.EnumType[] META_LOOKUP = new BlockDecorativeSodPlank.EnumType[values().length];
        private final int meta;
        private final String name;

        private EnumType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() { return this.meta; }

        public String toString() { return this.name; }

        public static BlockDecorativeSodPlank.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() { return this.name; }

        public String getUnlocalizedName() { return this.name; }

        static
        {
        	BlockDecorativeSodPlank.EnumType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
            	BlockDecorativeSodPlank.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
}
