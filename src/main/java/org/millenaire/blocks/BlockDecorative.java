package org.millenaire.blocks;

import org.millenaire.Millenaire;
import org.millenaire.items.ItemBlockDecorativeEarth;
import org.millenaire.items.ItemBlockDecorativeStone;
import org.millenaire.items.ItemBlockDecorativeWood;
import org.millenaire.items.ItemOrientedSlab;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unchecked")
public class BlockDecorative 
{
	//To become the customizable block, also to hold declarations
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
	//Declarations
		public static Block blockDecorativeStone;
		public static Block blockDecorativeWood;
		public static Block blockDecorativeEarth;
		
		public static Block emptySericulture;
		public static Block mudBrick;
		
		public static Block byzantineTile;
		public static Block byzantineStoneTile;
		public static Block byzantineTileSlab;
		public static Block byzantineTileSlabDouble;
		
		public static Block paperWall;

	public static void preinitialize()
    {
    	blockDecorativeStone = new BlockDecorativeStone().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockDecorativeStone");
		GameRegistry.registerBlock(blockDecorativeStone, ItemBlockDecorativeStone.class, "blockDecorativeStone");
		blockDecorativeWood = new BlockDecorativeWood().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockDecorativeWood");
		GameRegistry.registerBlock(blockDecorativeWood, ItemBlockDecorativeWood.class, "blockDecorativeWood");
		blockDecorativeEarth = new BlockDecorativeEarth().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockDecorativeEarth");
		GameRegistry.registerBlock(blockDecorativeEarth, ItemBlockDecorativeEarth.class, "blockDecorativeEarth");
		
		emptySericulture = new BlockDecorativeUpdate(Material.wood, blockDecorativeWood.getDefaultState().withProperty(BlockDecorativeWood.VARIANT, BlockDecorativeWood.EnumType.SERICULTURE)).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("emptySericulture");
		GameRegistry.registerBlock(emptySericulture, "emptySericulture");
		mudBrick = new BlockDecorativeUpdate(Material.ground, blockDecorativeEarth.getDefaultState().withProperty(BlockDecorativeEarth.VARIANT, BlockDecorativeEarth.EnumType.DRIEDBRICK)).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mudBrick");
		GameRegistry.registerBlock(mudBrick, "mudBrick");
		
		byzantineTile = new BlockDecorativeOriented(Material.rock).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineTile");
		GameRegistry.registerBlock(byzantineTile, "byzantineTile");
		byzantineStoneTile = new BlockDecorativeOriented(Material.rock).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineStoneTile");
		GameRegistry.registerBlock(byzantineStoneTile, "byzantineStoneTile");
		byzantineTileSlab = new BlockDecorativeOrientedSlabHalf(Material.rock).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineTileSlab");
		byzantineTileSlabDouble = new BlockDecorativeOrientedSlabDouble(Material.rock, byzantineTileSlab).setUnlocalizedName("byzantineTileSlabDouble");
		GameRegistry.registerBlock(byzantineTileSlab, ItemOrientedSlab.class, "byzantineTileSlab", byzantineTileSlab, byzantineTileSlabDouble);
		GameRegistry.registerBlock(byzantineTileSlabDouble, ItemOrientedSlab.class, "byzantineTileSlabDouble", byzantineTileSlab, byzantineTileSlabDouble);
		
		paperWall = new BlockDecorativePane(Material.cloth).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("paperWall");
		GameRegistry.registerBlock(paperWall, "paperWall");
		
		GameRegistry.addSmelting(mudBrick, new ItemStack(blockDecorativeStone, 1, 1), 0.3f);
		GameRegistry.addRecipe(new ItemStack(byzantineStoneTile),
				"AAA",
				"BBB",
				'A', new ItemStack(byzantineTile), 'B', new ItemStack(Blocks.stone));
    }
    
    @SideOnly(Side.CLIENT)
	public static void prerender()
	{
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeStone), 0, new ModelResourceLocation(Millenaire.MODID + ":goldOrnament", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeStone), 1, new ModelResourceLocation(Millenaire.MODID + ":cookedBrick", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeWood), 0, new ModelResourceLocation(Millenaire.MODID + ":plainTimberFrame", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeWood), 1, new ModelResourceLocation(Millenaire.MODID + ":crossTimberFrame", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeWood), 2, new ModelResourceLocation(Millenaire.MODID + ":thatch", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeWood), 3, new ModelResourceLocation(Millenaire.MODID + ":sericulture", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeEarth), 0, new ModelResourceLocation(Millenaire.MODID + ":dirtWall", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDecorativeEarth), 1, new ModelResourceLocation(Millenaire.MODID + ":driedBrick", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(byzantineTileSlabDouble), 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineTile", "inventory"));
	}
    
	@SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(Item.getItemFromBlock(emptySericulture), 0, new ModelResourceLocation(Millenaire.MODID + ":emptySericulture", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(mudBrick), 0, new ModelResourceLocation(Millenaire.MODID + ":mudBrick", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(byzantineTile), 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineTile", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(byzantineStoneTile), 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineStoneTile", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(byzantineTileSlab), 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineTileSlab", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(paperWall), 0, new ModelResourceLocation(Millenaire.MODID + ":paperWall", "inventory"));
	}
}
