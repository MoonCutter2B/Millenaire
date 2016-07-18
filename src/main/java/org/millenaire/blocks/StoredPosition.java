package org.millenaire.blocks;

import java.util.List;
import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityVillageStone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StoredPosition extends BlockAir
{
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", StoredPosition.EnumType.class);
	private boolean showParticles = false;
	
	private final int sourceColor = 44820;
	private final int tradeColor = 9983;
	private final int pathColor = 16766976;
	private final int sleepColor = 57538;
	private final int defendColor = 16711680;
	private final int hideColor = 8323127;
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(showParticles)
		{
			int color = 16777215;
			
			if(state.getValue(VARIANT) == EnumType.TRADEPOS)
				color = tradeColor;
			else if(state.getValue(VARIANT) == EnumType.SOURCEPOS)
				color = sourceColor;
			else if(state.getValue(VARIANT) == EnumType.PATHPOS)
				color = pathColor;
			else if(state.getValue(VARIANT) == EnumType.SLEEPPOS)
				color = sleepColor;
			else if(state.getValue(VARIANT) == EnumType.DEFENDPOS)
				color = defendColor;
			else if(state.getValue(VARIANT) == EnumType.HIDEPOS)
				color = hideColor;
			
			double d0 = (double)(color >> 16 & 255) / 255.0D;
            double d1 = (double)(color >> 8 & 255) / 255.0D;
            double d2 = (double)(color >> 0 & 255) / 255.0D;
			worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + 0.5D, pos.getY()+ 0.5D, pos.getZ()+ 0.5D, d0, d1, d2, new int[0]);
		}
	}
	
	public void setShowParticles(boolean bool)
	{
		showParticles = bool;
	}
	
	public boolean getShowParticles()
	{
		return showParticles;
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
        	StoredPosition.EnumType[] aenumtype = StoredPosition.EnumType.values();
            int i = aenumtype.length;

            for (int j = 0; j < i; ++j)
            {
            	StoredPosition.EnumType enumtype = aenumtype[j];
                list.add(new ItemStack(itemIn, 1, enumtype.getMetadata()));
            }
        }
    }

    public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + StoredPosition.EnumType.byMetadata(meta).getUnlocalizedName();
    }

	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, StoredPosition.EnumType.byMetadata(meta));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        return ((StoredPosition.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }
    
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
	//Declarations
		public static Block storedPosition;

    public static void preinitialize()
    {
    	storedPosition = new BlockVillageStone().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("storedPosition");
		GameRegistry.registerBlock(storedPosition, "storedPosition");
    }
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static enum EnumType implements IStringSerializable
    {
    	TRADEPOS(0, "tradePos"),
    	SOURCEPOS(1, "sourcePos"),
    	PATHPOS(2, "pathPos"),
    	HIDEPOS(3, "hidePos"),
    	DEFENDPOS(4, "defendPos"),
    	SLEEPPOS(5, "sleepPos");
        
        private static final StoredPosition.EnumType[] META_LOOKUP = new StoredPosition.EnumType[values().length];
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

        public static StoredPosition.EnumType byMetadata(int meta)
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
        	StoredPosition.EnumType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
            	StoredPosition.EnumType var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
}
