package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.blocks.MillBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MillItems 
{
	//Class to hold basic items
	
	public static Item denier;
	public static Item denierOr;
	public static Item denierArgent;
	
	public static Item silk;
	public static Item obsidianFlake;
	public static Item unknownPowder;
	
	public static Item woolClothes;
	public static Item silkClothes;
	
	public static Item galianiteDust;
	
	//Crops
	public static Item turmeric;
	public static Item rice;
	public static Item maize;
	public static Item grapes;
	
	public static void preinitialize()
	{
		denier = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("denier");
		GameRegistry.registerItem(denier, "denier");
		denierOr = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("denierOr");
		GameRegistry.registerItem(denierOr, "denierOr");
		denierArgent = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("denierArgent");
		GameRegistry.registerItem(denierArgent, "denierArgent");
		
		silk = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("silk");
		GameRegistry.registerItem(silk, "silk");
		obsidianFlake = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("obsidianFlake");
		GameRegistry.registerItem(obsidianFlake, "obsidianFlake");
		unknownPowder = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("unknownPowder");
		GameRegistry.registerItem(unknownPowder, "unknownPowder");
		
		woolClothes = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("woolClothes");
		GameRegistry.registerItem(woolClothes, "woolClothes");
		silkClothes = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("silkClothes");
		GameRegistry.registerItem(silkClothes, "silkClothes");
		
		galianiteDust = new Item().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("galianiteDust");
		GameRegistry.registerItem(galianiteDust, "galianiteDust");
		
		//Crops
		turmeric = new ItemMillSeeds(MillBlocks.cropTurmeric).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("turmeric");
    	((BlockMillCrops) MillBlocks.cropTurmeric).setSeed((IPlantable) turmeric);
    	GameRegistry.registerItem(turmeric, "turmeric");
    	
    	rice = new ItemMillSeeds(MillBlocks.cropRice).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("rice");
    	((BlockMillCrops) MillBlocks.cropRice).setSeed((IPlantable) rice);
    	GameRegistry.registerItem(rice, "rice");
    	
    	maize = new ItemMillSeeds(MillBlocks.cropMaize).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("maize");
    	((BlockMillCrops) MillBlocks.cropMaize).setSeed((IPlantable) maize);
    	GameRegistry.registerItem(maize, "maize");
    	
    	grapes = new ItemMillSeeds(MillBlocks.cropGrapeVine).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("grapes");
    	((BlockMillCrops) MillBlocks.cropGrapeVine).setSeed((IPlantable) grapes);
    	GameRegistry.registerItem(grapes, "grapes");
	}
	
	@SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(denier, 0, new ModelResourceLocation(Millenaire.MODID + ":denier", "inventory"));
		renderItem.getItemModelMesher().register(denierOr, 0, new ModelResourceLocation(Millenaire.MODID + ":denierOr", "inventory"));
		renderItem.getItemModelMesher().register(denierArgent, 0, new ModelResourceLocation(Millenaire.MODID + ":denierArgent", "inventory"));
		
		renderItem.getItemModelMesher().register(silk, 0, new ModelResourceLocation(Millenaire.MODID + ":silk", "inventory"));
		renderItem.getItemModelMesher().register(obsidianFlake, 0, new ModelResourceLocation(Millenaire.MODID + ":obsidianFlake", "inventory"));
		renderItem.getItemModelMesher().register(unknownPowder, 0, new ModelResourceLocation(Millenaire.MODID + ":unknownPowder", "inventory"));
		
		renderItem.getItemModelMesher().register(woolClothes, 0, new ModelResourceLocation(Millenaire.MODID + ":woolClothes", "inventory"));
		renderItem.getItemModelMesher().register(silkClothes, 0, new ModelResourceLocation(Millenaire.MODID + ":silkClothes", "inventory"));
		renderItem.getItemModelMesher().register(galianiteDust, 0, new ModelResourceLocation(Millenaire.MODID + ":galianiteDust", "inventory"));
		
		//Crops
		renderItem.getItemModelMesher().register(turmeric, 0, new ModelResourceLocation(Millenaire.MODID + ":turmeric", "inventory"));
		renderItem.getItemModelMesher().register(rice, 0, new ModelResourceLocation(Millenaire.MODID + ":rice", "inventory"));
		renderItem.getItemModelMesher().register(maize, 0, new ModelResourceLocation(Millenaire.MODID + ":maize", "inventory"));
		renderItem.getItemModelMesher().register(grapes, 0, new ModelResourceLocation(Millenaire.MODID + ":grapes", "inventory"));

	}
}
