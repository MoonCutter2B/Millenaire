package org.millenaire.items;

import org.millenaire.Millenaire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
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
	}
}
