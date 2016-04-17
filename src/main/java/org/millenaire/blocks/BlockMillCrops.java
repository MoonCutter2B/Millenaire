package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.items.ItemMillSeeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillCrops extends BlockCrops
{
	private boolean requiresIrrigation;
	private boolean slowGrowth;
	
	private IPlantable seed;
	
	public BlockMillCrops(boolean irrigationIn, boolean growthIn)
	{
		super();
		
		requiresIrrigation = irrigationIn;
		slowGrowth = growthIn;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.checkAndDropBlock(worldIn, pos, state);

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
            int i = ((Integer)state.getValue(AGE)).intValue();

            if (i < 7)
            {
                float f = getLocalGrowthChance(this, worldIn, pos);

                if(f != 0)
                {
                	if (rand.nextInt((int)(25.0F / f) + 1) == 0)
                	{
                		worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
                	}
                }
            }
        }
    }
	
	protected float getLocalGrowthChance(Block blockIn, World worldIn, BlockPos pos)
	{
		IBlockState groundIn = worldIn.getBlockState(pos.down());
		if(groundIn.getBlock() != Blocks.farmland)
		{
			System.err.println("BlockMillCrop growth logic not applied, unrecognized farmland");
			return getGrowthChance(blockIn, worldIn, pos);
		}
		if(requiresIrrigation && groundIn.getValue(BlockFarmland.MOISTURE) < 1)
			return 0.0F;
		else
		{
			if(slowGrowth)
				return getGrowthChance(blockIn, worldIn, pos) / 2;
			else
				return getGrowthChance(blockIn, worldIn, pos);
		}
	}
	
	@Override
    protected Item getSeed()
    {
        return (Item)seed;
    }

	@Override
    protected Item getCrop()
    {
        return (Item)seed;
    }
	
	public Block setSeed(final IPlantable seed) 
	{
		this.seed = seed;
		return this;
	}
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
	//Declarations
		public static Block cropTurmeric;
		public static Block cropRice;
		public static Block cropMaize;
		public static Block cropGrapeVine;
		
		public static Item turmeric;
		public static Item rice;
		public static Item maize;
		public static Item grapes;

    public static void preinitialize()
    {
    	cropTurmeric = new BlockMillCrops(false, false).setSeed((ItemMillSeeds)turmeric).setCreativeTab(null).setUnlocalizedName("cropTurmeric");
    	turmeric = new ItemMillSeeds(cropTurmeric).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("turmeric");
    	GameRegistry.registerItem(turmeric, "turmeric");
    	GameRegistry.registerBlock(cropTurmeric, "cropTurmeric");
    	
    	cropRice = new BlockMillCrops(true, false).setSeed((ItemMillSeeds)rice).setCreativeTab(null).setUnlocalizedName("cropRice");
    	rice = new ItemMillSeeds(cropRice).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("rice");
    	GameRegistry.registerItem(rice, "rice");
    	GameRegistry.registerBlock(cropRice, "cropRice");

    	cropMaize = new BlockMillCrops(false, true).setSeed((ItemMillSeeds)maize).setCreativeTab(null).setUnlocalizedName("cropMaize");
    	maize = new ItemMillSeeds(cropMaize).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("maize");
    	GameRegistry.registerItem(maize, "maize");
    	GameRegistry.registerBlock(cropMaize, "cropMaize");

    	cropGrapeVine = new BlockMillCrops(false, false).setSeed((ItemMillSeeds)grapes).setCreativeTab(null).setUnlocalizedName("cropGrapeVine");
    	grapes = new ItemMillSeeds(cropGrapeVine).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("grapes");
    	GameRegistry.registerItem(grapes, "grapes");
    	GameRegistry.registerBlock(cropGrapeVine, "cropGrapeVine");
    }
    
    @SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(turmeric, 0, new ModelResourceLocation(Millenaire.MODID + ":turmeric", "inventory"));
		renderItem.getItemModelMesher().register(rice, 0, new ModelResourceLocation(Millenaire.MODID + ":rice", "inventory"));
		renderItem.getItemModelMesher().register(maize, 0, new ModelResourceLocation(Millenaire.MODID + ":maize", "inventory"));
		renderItem.getItemModelMesher().register(grapes, 0, new ModelResourceLocation(Millenaire.MODID + ":grapes", "inventory"));
	}
}
