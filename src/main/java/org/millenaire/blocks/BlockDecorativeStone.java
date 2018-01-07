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

public class BlockDecorativeStone extends Block
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockDecorativeStone.EnumType.class);
	
	BlockDecorativeStone() { super(Material.rock); }
	
	@Override
    public int damageDropped(IBlockState state)
    {
        return ((BlockDecorativeStone.EnumType)state.getValue(VARIANT)).getMetadata();
    }

	public IProperty getVariantProperty() { return VARIANT; }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        if (Block.getBlockFromItem(itemIn) == this)
        {
            BlockDecorativeStone.EnumType[] aenumtype = BlockDecorativeStone.EnumType.values();

            for (EnumType enumtype : aenumtype) {
                list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
            }
        }
    }

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + BlockDecorativeStone.EnumType.byMetadata(meta).getUnlocalizedName();
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockDecorativeStone.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockDecorativeStone.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState() { return new BlockState(this, VARIANT); }

    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static enum EnumType implements IStringSerializable
    {
        GOLDORNAMENT(0, "goldOrnament"),
    	COOKEDBRICK(1, "cookedBrick"),
    	GALIANITEBLOCK(2, "galianiteBlock");
        
        private static final BlockDecorativeStone.EnumType[] META_LOOKUP = new BlockDecorativeStone.EnumType[values().length];
        private final int meta;
        private final String name;

        EnumType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() { return this.meta; }

        public String toString() { return this.name; }

        public static BlockDecorativeStone.EnumType byMetadata(int meta)
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
        	BlockDecorativeStone.EnumType[] var0 = values();

            for (EnumType var3 : var0) {
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
}
