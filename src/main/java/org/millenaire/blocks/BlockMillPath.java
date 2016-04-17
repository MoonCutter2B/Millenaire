package org.millenaire.blocks;

import java.util.List;

import org.millenaire.Millenaire;
import org.millenaire.items.ItemMillPath;
import org.millenaire.items.ItemMillPathSlab;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings( {"rawtypes", "unchecked"} )
public class BlockMillPath extends Block
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockMillPath.EnumType.class);

	public BlockMillPath() 
	{
		super(Material.ground);
		
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
	}
	
	@Override
    public boolean isFullCube()
    {
        return false;
    }
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
    public int damageDropped(IBlockState state)
    {
        return ((BlockMillPath.EnumType)state.getValue(VARIANT)).getMetadata();
    }

	public IProperty getVariantProperty()
    {
        return VARIANT;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        if (Block.getBlockFromItem(itemIn) == this)
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

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + BlockMillPath.EnumType.byMetadata(meta).getUnlocalizedName();
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockMillPath.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockMillPath.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }
    
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    //Declarations
  		public static Block blockMillPath;
  		public static Block blockMillPathSlab;
  		public static Block blockMillPathSlabDouble;

    public static void preinitialize()
    {
    	blockMillPath = new BlockMillPath().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockMillPath");
    	GameRegistry.registerBlock(blockMillPath, ItemMillPath.class, "blockMillPath");
    	
    	blockMillPathSlab = new BlockMillPathSlabHalf().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockMillPathSlab");
    	blockMillPathSlabDouble = new BlockMillPathSlabDouble().setUnlocalizedName("blockMillPathSlabDouble");
    	GameRegistry.registerBlock(blockMillPathSlab, ItemMillPathSlab.class, "blockMillPathSlab", blockMillPathSlab, blockMillPathSlabDouble);
    	GameRegistry.registerBlock(blockMillPathSlabDouble, ItemMillPathSlab.class, "blockMillPathSlabDouble", blockMillPathSlab, blockMillPathSlabDouble);
    }
      
      @SideOnly(Side.CLIENT)
  	public static void prerender()
  	{
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 0, new ModelResourceLocation(Millenaire.MODID + ":pathDirt", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 1, new ModelResourceLocation(Millenaire.MODID + ":pathGravel", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 2, new ModelResourceLocation(Millenaire.MODID + ":pathSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 3, new ModelResourceLocation(Millenaire.MODID + ":pathSandstoneSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 4, new ModelResourceLocation(Millenaire.MODID + ":pathOchreSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPath), 5, new ModelResourceLocation(Millenaire.MODID + ":pathSlabAndGravel", "inventory"));
    	  
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 0, new ModelResourceLocation(Millenaire.MODID + ":pathDirtHalf", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 1, new ModelResourceLocation(Millenaire.MODID + ":pathGravelHalf", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 2, new ModelResourceLocation(Millenaire.MODID + ":pathSlabHalf", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 3, new ModelResourceLocation(Millenaire.MODID + ":pathSandstoneSlabHalf", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 4, new ModelResourceLocation(Millenaire.MODID + ":pathOchreSlabHalf", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlab), 5, new ModelResourceLocation(Millenaire.MODID + ":pathSlabAndGravelHalf", "inventory"));
    	  
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 0, new ModelResourceLocation(Millenaire.MODID + ":pathDirt", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 1, new ModelResourceLocation(Millenaire.MODID + ":pathGravel", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 2, new ModelResourceLocation(Millenaire.MODID + ":pathSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 3, new ModelResourceLocation(Millenaire.MODID + ":pathSandstoneSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 4, new ModelResourceLocation(Millenaire.MODID + ":pathOchreSlab", "inventory"));
    	  ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMillPathSlabDouble), 5, new ModelResourceLocation(Millenaire.MODID + ":pathSlabAndGravel", "inventory"));
  	}
    
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static enum EnumType implements IStringSerializable
    {
    	DIRT(0, "dirt"),
    	GRAVEL(1, "gravel"),
    	SLAB(2, "slab"),
    	SANDSTONESLAB(3, "sandstoneSlab"),
    	OCHRESLAB(4, "ochreSlab"),
    	SLABANDGRAVEL(5, "slabAndGravel");
        
        private static final BlockMillPath.EnumType[] META_LOOKUP = new BlockMillPath.EnumType[values().length];
        private final int meta;
        private final String name;

        private EnumType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockMillPath.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.name;
        }

        static
        {
        	BlockMillPath.EnumType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
            	BlockMillPath.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
}
