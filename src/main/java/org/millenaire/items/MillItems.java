package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.blocks.MillBlocks;
import org.millenaire.items.ItemMillArmor.mayanQuestCrown;
import org.millenaire.items.ItemMillTool.ItemMillAxe;
import org.millenaire.items.ItemMillTool.ItemMillHoe;
import org.millenaire.items.ItemMillTool.ItemMillMace;
import org.millenaire.items.ItemMillTool.ItemMillPickaxe;
import org.millenaire.items.ItemMillTool.ItemMillShovel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.model.ModelLoader;
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
	
	public static Item ciderApple;
	public static Item cider;
	public static Item calva;
	public static Item tripes;
	public static Item boudinNoir;
	
	public static Item vegCurry;
	public static Item murghCurry;
	public static Item rasgulla;
	
	public static Item cacauhaa;
	public static Item masa;
	public static Item wah;
	
	public static Item wine;
	public static Item malvasiaWine;
	public static Item feta;
	public static Item souvlaki;
	
	public static Item sake;
	public static Item udon;
	public static Item ikayaki;
	
	//Armour
	public static Item normanHelmet;
	public static Item normanChestplate;
	public static Item normanLeggings;
	public static Item normanBoots;
	
	public static Item byzantineHelmet;
	public static Item byzantineChestplate;
	public static Item byzantineLeggings;
	public static Item byzantineBoots;
	
	public static Item japaneseGuardHelmet;
	public static Item japaneseGuardChestplate;
	public static Item japaneseGuardLeggings;
	public static Item japaneseGuardBoots;
	
	public static Item japaneseBlueHelmet;
	public static Item japaneseBlueChestplate;
	public static Item japaneseBlueLeggings;
	public static Item japaneseBlueBoots;
	
	public static Item japaneseRedHelmet;
	public static Item japaneseRedChestplate;
	public static Item japaneseRedLeggings;
	public static Item japaneseRedBoots;
	
	public static Item mayanQuestCrown;
	
	//Wands
	public static Item wandSummoning;
	public static Item wandNegation;
	public static Item wandCreative;
	public static Item tuningFork;
	
	//Tools
	public static Item normanAxe;
	public static Item normanShovel;
	public static Item normanPickaxe;
	public static Item normanHoe;
	public static Item normanSword;
	
	public static Item mayanAxe;
	public static Item mayanShovel;
	public static Item mayanPickaxe;
	public static Item mayanHoe;
	public static Item mayanMace;
	
	public static Item byzantineMace;
	
	public static Item japaneseSword;
	public static Item japaneseBow;
	
	//Amulets
	public static Item amuletSkollHati;
	public static Item amuletYggdrasil;
	public static Item amuletAlchemist;
	public static Item amuletVishnu;
	
	//Wallet
	public static Item itemMillPurse;
	
	//Sign
	public static Item itemMillSign;
	
	//Parchments
	public static Item normanVillagerParchment;
	public static Item normanBuildingParchment;
	public static Item normanItemParchment;
	public static Item normanAllParchment;
	
	public static Item byzantineVillagerParchment;
	public static Item byzantineBuildingParchment;
	public static Item byzantineItemParchment;
	public static Item byzantineAllParchment;
	
	public static Item hindiVillagerParchment;
	public static Item hindiBuildingParchment;
	public static Item hindiItemParchment;
	public static Item hindiAllParchment;
	
	public static Item mayanVillagerParchment;
	public static Item mayanBuildingParchment;
	public static Item mayanItemParchment;
	public static Item mayanAllParchment;
	
	public static Item japaneseVillagerParchment;
	public static Item japaneseBuildingParchment;
	public static Item japaneseItemParchment;
	public static Item japaneseAllParchment;
	
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
    	ciderApple = new ItemMillFood(0, 0, 0, 1, 0.05F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("ciderApple");
    	GameRegistry.registerItem(ciderApple, "ciderApple");
    	cider = new ItemMillFood(4, 15, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("cider");
    	GameRegistry.registerItem(cider, "cider");
    	calva = ((ItemMillFood)new ItemMillFood(8, 30, 10, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.damageBoost.id, 180, 0, 1f).setUnlocalizedName("calva");
    	GameRegistry.registerItem(calva, "calva");
    	tripes = new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("tripes");
    	GameRegistry.registerItem(tripes, "tripes");
    	boudinNoir = new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("boudinNoir");
    	GameRegistry.registerItem(boudinNoir, "boudinNoir");
    	vegCurry = new ItemMillFood(0, 0, 0, 6, 0.6F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("vegCurry");
    	GameRegistry.registerItem(vegCurry, "vegCurry");
    	murghCurry = ((ItemMillFood)new ItemMillFood(0, 0, 0, 8, 0.8F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.fireResistance.id, 8 * 60, 0, 1f).setUnlocalizedName("murghCurry");
    	GameRegistry.registerItem(murghCurry, "murghCurry");
    	rasgulla = ((ItemMillFood)new ItemMillFood(2, 30, 0, 0, 0.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.moveSpeed.id, 8 * 60, 1, 1f).setAlwaysEdible().setUnlocalizedName("rasgulla");
    	GameRegistry.registerItem(rasgulla, "rasgulla");
    	cacauhaa = ((ItemMillFood)new ItemMillFood(6, 30, 3, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.nightVision.id, 8 * 60, 0, 1f).setUnlocalizedName("cacauhaa");
    	GameRegistry.registerItem(cacauhaa, "cacauhaa");
    	masa = new ItemMillFood(0, 0, 0, 6, 0.6F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("masa");
    	GameRegistry.registerItem(masa, "masa");
    	wah = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.digSpeed.id, 8 * 60, 0, 1f).setUnlocalizedName("wah");
    	GameRegistry.registerItem(wah, "wah");
    	wine = new ItemMillFood(3, 15, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wine");
    	GameRegistry.registerItem(wine, "wine");
    	malvasiaWine = ((ItemMillFood)new ItemMillFood(8, 30, 5, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.resistance.id, 8 * 60, 0, 1f).setUnlocalizedName("malvasiaWine");
    	GameRegistry.registerItem(malvasiaWine, "malvasiaWine");
    	feta = new ItemMillFood(3, 10, 0, 0, 0.0F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("feta");
    	GameRegistry.registerItem(feta, "feta");
    	souvlaki = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.heal.id, 1, 0, 1f).setUnlocalizedName("souvlaki");
    	GameRegistry.registerItem(souvlaki, "souvlaki");
    	sake = ((ItemMillFood)new ItemMillFood(8, 30, 10, 0, 0.0F, true).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.jump.id, 8 * 60, 1, 1f).setUnlocalizedName("sake");
    	GameRegistry.registerItem(sake, "sake");
    	udon = new ItemMillFood(0, 0, 0, 8, 0.8F, false).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("udon");
    	GameRegistry.registerItem(udon, "udon");
    	ikayaki = ((ItemMillFood)new ItemMillFood(0, 0, 0, 10, 1.0F, false).setCreativeTab(Millenaire.tabMillenaire)).setPotionEffect(Potion.waterBreathing.id, 8 * 60, 2, 1f).setUnlocalizedName("ikayaki");
    	GameRegistry.registerItem(ikayaki, "ikayaki");
    	
    	//Armour
    	normanHelmet = new ItemArmor(ItemMillArmor.ARMOR_norman, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanHelmet");
		GameRegistry.registerItem(normanHelmet, "normanHelmet");
		normanChestplate = new ItemArmor(ItemMillArmor.ARMOR_norman, 2, 1).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanChestplate");
		GameRegistry.registerItem(normanChestplate, "normanChestplate");
		normanLeggings = new ItemArmor(ItemMillArmor.ARMOR_norman, 2, 2).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanLeggings");
		GameRegistry.registerItem(normanLeggings, "normanLeggings");
		normanBoots = new ItemArmor(ItemMillArmor.ARMOR_norman, 2, 3).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanBoots");
		GameRegistry.registerItem(normanBoots, "normanBoots");
		
		byzantineHelmet = new ItemArmor(ItemMillArmor.ARMOR_byzantine, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineHelmet");
		GameRegistry.registerItem(byzantineHelmet, "byzantineHelmet");
		byzantineChestplate = new ItemArmor(ItemMillArmor.ARMOR_byzantine, 2, 1).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineChestplate");
		GameRegistry.registerItem(byzantineChestplate, "byzantineChestplate");
		byzantineLeggings = new ItemArmor(ItemMillArmor.ARMOR_byzantine, 2, 2).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineLeggings");
		GameRegistry.registerItem(byzantineLeggings, "byzantineLeggings");
		byzantineBoots = new ItemArmor(ItemMillArmor.ARMOR_byzantine, 2, 3).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineBoots");
		GameRegistry.registerItem(byzantineBoots, "byzantineBoots");
		
		japaneseGuardHelmet = new ItemArmor(ItemMillArmor.ARMOR_japaneseGuard, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseGuardHelmet");
		GameRegistry.registerItem(japaneseGuardHelmet, "japaneseGuardHelmet");
		japaneseGuardChestplate = new ItemArmor(ItemMillArmor.ARMOR_japaneseGuard, 2, 1).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseGuardChestplate");
		GameRegistry.registerItem(japaneseGuardChestplate, "japaneseGuardChestplate");
		japaneseGuardLeggings = new ItemArmor(ItemMillArmor.ARMOR_japaneseGuard, 2, 2).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseGuardLeggings");
		GameRegistry.registerItem(japaneseGuardLeggings, "japaneseGuardLeggings");
		japaneseGuardBoots = new ItemArmor(ItemMillArmor.ARMOR_japaneseGuard, 2, 3).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseGuardBoots");
		GameRegistry.registerItem(japaneseGuardBoots, "japaneseGuardBoots");
		
		japaneseBlueHelmet = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorBlue, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBlueHelmet");
		GameRegistry.registerItem(japaneseBlueHelmet, "japaneseBlueHelmet");
		japaneseBlueChestplate = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorBlue, 2, 1).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBlueChestplate");
		GameRegistry.registerItem(japaneseBlueChestplate, "japaneseBlueChestplate");
		japaneseBlueLeggings = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorBlue, 2, 2).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBlueLeggings");
		GameRegistry.registerItem(japaneseBlueLeggings, "japaneseBlueLeggings");
		japaneseBlueBoots = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorBlue, 2, 3).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBlueBoots");
		GameRegistry.registerItem(japaneseBlueBoots, "japaneseBlueBoots");
		
		japaneseRedHelmet = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorRed, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseRedHelmet");
		GameRegistry.registerItem(japaneseRedHelmet, "japaneseRedHelmet");
		japaneseRedChestplate = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorRed, 2, 1).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseRedChestplate");
		GameRegistry.registerItem(japaneseRedChestplate, "japaneseRedChestplate");
		japaneseRedLeggings = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorRed, 2, 2).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseRedLeggings");
		GameRegistry.registerItem(japaneseRedLeggings, "japaneseRedLeggings");
		japaneseRedBoots = new ItemArmor(ItemMillArmor.ARMOR_japaneseWarriorRed, 2, 3).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseRedBoots");
		GameRegistry.registerItem(japaneseRedBoots, "japaneseRedBoots");
		
		mayanQuestCrown = new mayanQuestCrown(ItemMillArmor.ARMOR_mayanQuest, 2, 0).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanQuestCrown");
		GameRegistry.registerItem(mayanQuestCrown, "mayanQuestCrown");
		
		//Wands
		wandSummoning = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandSummoning");
		GameRegistry.registerItem(wandSummoning, "wandSummoning");
		wandNegation = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandNegation");
		GameRegistry.registerItem(wandNegation, "wandNegation");
		wandCreative = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("wandCreative");
		GameRegistry.registerItem(wandCreative, "wandCreative");
		
		tuningFork = new ItemMillWand().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("tuningFork");
		GameRegistry.registerItem(tuningFork, "tuningFork");
		
		//Tools
		normanAxe = new ItemMillAxe(ItemMillTool.TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanAxe");
		GameRegistry.registerItem(normanAxe, "normanAxe");
		normanShovel = new ItemMillShovel(ItemMillTool.TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanShovel");
		GameRegistry.registerItem(normanShovel, "normanShovel");
		normanPickaxe = new ItemMillPickaxe(ItemMillTool.TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanPickaxe");
		GameRegistry.registerItem(normanPickaxe, "normanPickaxe");
		normanHoe = new ItemMillHoe(ItemMillTool.TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanHoe");
		GameRegistry.registerItem(normanHoe, "normanHoe");
		normanSword = new ItemSword(ItemMillTool.TOOLS_norman).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanSword");
		GameRegistry.registerItem(normanSword, "normanSword");
		
		mayanAxe = new ItemMillAxe(ItemMillTool.TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanAxe");
		GameRegistry.registerItem(mayanAxe, "mayanAxe");
		mayanShovel = new ItemMillShovel(ItemMillTool.TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanShovel");
		GameRegistry.registerItem(mayanShovel, "mayanShovel");
		mayanPickaxe = new ItemMillPickaxe(ItemMillTool.TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanPickaxe");
		GameRegistry.registerItem(mayanPickaxe, "mayanPickaxe");
		mayanHoe = new ItemMillHoe(ItemMillTool.TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanHoe");
		GameRegistry.registerItem(mayanHoe, "mayanHoe");
		mayanMace = new ItemSword(ItemMillTool.TOOLS_obsidian).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanMace");
		GameRegistry.registerItem(mayanMace, "mayanMace");
		
		byzantineMace = new ItemMillMace(Item.ToolMaterial.IRON).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineMace");
		GameRegistry.registerItem(byzantineMace, "byzantineMace");
		
		japaneseSword = new ItemSword(Item.ToolMaterial.IRON).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseSword");
		GameRegistry.registerItem(japaneseSword, "japaneseSword");
		japaneseBow = new ItemMillBow(2, 0.5F, "japaneseBow").setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBow");
		GameRegistry.registerItem(japaneseBow, "japaneseBow");
		
		//Amulets
		amuletSkollHati = new ItemMillAmulet().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("amuletSkollHati");
		GameRegistry.registerItem(amuletSkollHati, "amuletSkollHati");
		amuletAlchemist = new ItemMillAmulet().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("amuletAlchemist");
		GameRegistry.registerItem(amuletAlchemist, "amuletAlchemist");
		amuletVishnu = new ItemMillAmulet().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("amuletVishnu");
		GameRegistry.registerItem(amuletVishnu, "amuletVishnu");
		amuletYggdrasil = new ItemMillAmulet().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("amuletYggdrasil");
		GameRegistry.registerItem(amuletYggdrasil, "amuletYggdrasil");
		
		//Wallet
		itemMillPurse = new ItemMillWallet().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("itemMillPurse");
    	GameRegistry.registerItem(itemMillPurse, "itemMillPurse");
    	
    	//Sign
    	itemMillSign = new ItemMillSign().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("itemMillSign");
		GameRegistry.registerItem(itemMillSign, "itemMillSign");
		
		//Parchments
		normanVillagerParchment = new ItemMillParchment("scroll.normanVillager.title", new String[]{"scroll.normanVillager.leaders", "scroll.normanVillager.men", "scroll.normanVillager.women", "scroll.normanVillager.children"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanVillagerParchment");
		GameRegistry.registerItem(normanVillagerParchment, "normanVillagerParchment");
		normanBuildingParchment= new ItemMillParchment("scroll.normanBuilding.title", new String[]{"scroll.normanBuilding.centers", "scroll.normanBuilding.houses", "scroll.normanBuilding.uninhabited", "scroll.normanBuilding.player"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanBuildingParchment");
		GameRegistry.registerItem(normanBuildingParchment, "normanBuildingParchment");
		normanItemParchment = new ItemMillParchment("scroll.normanItem.title", new String[]{"scroll.normanItem.food", "scroll.normanItem.tools", "scroll.normanItem.weapons", "scroll.normanItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanItemParchment");
		GameRegistry.registerItem(normanItemParchment, "normanItemParchment");
		normanAllParchment = new ItemMillParchment("scroll.normanVillager.title", new String[]{"scroll.normanVillager.leaders", "scroll.normanVillager.men", "scroll.normanVillager.women", "scroll.normanVillager.children",
				"scroll.normanBuilding.centers", "scroll.normanBuilding.houses", "scroll.normanBuilding.uninhabited", "scroll.normanBuilding.player", "scroll.normanItem.food", "scroll.normanItem.tools", "scroll.normanItem.weapons", "scroll.normanItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("normanAllParchment");
		GameRegistry.registerItem(normanAllParchment, "normanAllParchment");
		
		byzantineVillagerParchment = new ItemMillParchment("scroll.byzantineVillager.title", new String[]{"scroll.byzantineVillager.leaders", "scroll.byzantineVillager.men", "scroll.byzantineVillager.women", "scroll.byzantineVillager.children"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineVillagerParchment");
		GameRegistry.registerItem(byzantineVillagerParchment, "byzantineVillagerParchment");
		byzantineBuildingParchment= new ItemMillParchment("scroll.byzantineBuilding.title", new String[]{"scroll.byzantineBuilding.centers", "scroll.byzantineBuilding.houses", "scroll.byzantineBuilding.uninhabited", "scroll.byzantineBuilding.player"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineBuildingParchment");
		GameRegistry.registerItem(byzantineBuildingParchment, "byzantineBuildingParchment");
		byzantineItemParchment = new ItemMillParchment("scroll.byzantineItem.title", new String[]{"scroll.byzantineItem.food", "scroll.byzantineItem.tools", "scroll.byzantineItem.weapons", "scroll.byzantineItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineItemParchment");
		GameRegistry.registerItem(byzantineItemParchment, "byzantineItemParchment");
		byzantineAllParchment = new ItemMillParchment("scroll.byzantineVillager.title", new String[]{"scroll.byzantineVillager.leaders", "scroll.byzantineVillager.men", "scroll.byzantineVillager.women", "scroll.byzantineVillager.children",
				"scroll.byzantineBuilding.centers", "scroll.byzantineBuilding.houses", "scroll.byzantineBuilding.uninhabited", "scroll.byzantineBuilding.player", "scroll.byzantineItem.food", "scroll.byzantineItem.tools", "scroll.byzantineItem.weapons", "scroll.byzantineItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("byzantineAllParchment");
		GameRegistry.registerItem(byzantineAllParchment, "byzantineAllParchment");
		
		hindiVillagerParchment = new ItemMillParchment("scroll.hindiVillager.title", new String[]{"scroll.hindiVillager.leaders", "scroll.hindiVillager.men", "scroll.hindiVillager.women", "scroll.hindiVillager.children"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiVillagerParchment");
		GameRegistry.registerItem(hindiVillagerParchment, "hindiVillagerParchment");
		hindiBuildingParchment= new ItemMillParchment("scroll.hindiBuilding.title", new String[]{"scroll.hindiBuilding.centers", "scroll.hindiBuilding.houses", "scroll.hindiBuilding.uninhabited", "scroll.hindiBuilding.player"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiBuildingParchment");
		GameRegistry.registerItem(hindiBuildingParchment, "hindiBuildingParchment");
		hindiItemParchment = new ItemMillParchment("scroll.hindiItem.title", new String[]{"scroll.hindiItem.food", "scroll.hindiItem.tools", "scroll.hindiItem.weapons", "scroll.hindiItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiItemParchment");
		GameRegistry.registerItem(hindiItemParchment, "hindiItemParchment");
		hindiAllParchment = new ItemMillParchment("scroll.hindiVillager.title", new String[]{"scroll.hindiVillager.leaders", "scroll.hindiVillager.men", "scroll.hindiVillager.women", "scroll.hindiVillager.children",
				"scroll.hindiBuilding.centers", "scroll.hindiBuilding.houses", "scroll.hindiBuilding.uninhabited", "scroll.hindiBuilding.player", "scroll.hindiItem.food", "scroll.hindiItem.tools", "scroll.hindiItem.weapons", "scroll.hindiItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("hindiAllParchment");
		GameRegistry.registerItem(hindiAllParchment, "hindiAllParchment");
		
		mayanVillagerParchment = new ItemMillParchment("scroll.mayanVillager.title", new String[]{"scroll.mayanVillager.leaders", "scroll.mayanVillager.men", "scroll.mayanVillager.women", "scroll.mayanVillager.children"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanVillagerParchment");
		GameRegistry.registerItem(mayanVillagerParchment, "mayanVillagerParchment");
		mayanBuildingParchment= new ItemMillParchment("scroll.mayanBuilding.title", new String[]{"scroll.mayanBuilding.centers", "scroll.mayanBuilding.houses", "scroll.mayanBuilding.uninhabited", "scroll.mayanBuilding.player"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanBuildingParchment");
		GameRegistry.registerItem(mayanBuildingParchment, "mayanBuildingParchment");
		mayanItemParchment = new ItemMillParchment("scroll.mayanItem.title", new String[]{"scroll.mayanItem.food", "scroll.mayanItem.tools", "scroll.mayanItem.weapons", "scroll.mayanItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanItemParchment");
		GameRegistry.registerItem(mayanItemParchment, "mayanItemParchment");
		mayanAllParchment = new ItemMillParchment("scroll.mayanVillager.title", new String[]{"scroll.mayanVillager.leaders", "scroll.mayanVillager.men", "scroll.mayanVillager.women", "scroll.mayanVillager.children",
				"scroll.mayanBuilding.centers", "scroll.mayanBuilding.houses", "scroll.mayanBuilding.uninhabited", "scroll.mayanBuilding.player", "scroll.mayanItem.food", "scroll.mayanItem.tools", "scroll.mayanItem.weapons", "scroll.mayanItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("mayanAllParchment");
		GameRegistry.registerItem(mayanAllParchment, "mayanAllParchment");
		
		japaneseVillagerParchment = new ItemMillParchment("scroll.japaneseVillager.title", new String[]{"scroll.japaneseVillager.leaders", "scroll.japaneseVillager.men", "scroll.japaneseVillager.women", "scroll.japaneseVillager.children"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseVillagerParchment");
		GameRegistry.registerItem(japaneseVillagerParchment, "japaneseVillagerParchment");
		japaneseBuildingParchment= new ItemMillParchment("scroll.japaneseBuilding.title", new String[]{"scroll.japaneseBuilding.centers", "scroll.japaneseBuilding.houses", "scroll.japaneseBuilding.uninhabited", "scroll.japaneseBuilding.player"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseBuildingParchment");
		GameRegistry.registerItem(japaneseBuildingParchment, "japaneseBuildingParchment");
		japaneseItemParchment = new ItemMillParchment("scroll.japaneseItem.title", new String[]{"scroll.japaneseItem.food", "scroll.japaneseItem.tools", "scroll.japaneseItem.weapons", "scroll.japaneseItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseItemParchment");
		GameRegistry.registerItem(japaneseItemParchment, "japaneseItemParchment");
		japaneseAllParchment = new ItemMillParchment("scroll.japaneseVillager.title", new String[]{"scroll.japaneseVillager.leaders", "scroll.japaneseVillager.men", "scroll.japaneseVillager.women", "scroll.japaneseVillager.children",
				"scroll.japaneseBuilding.centers", "scroll.japaneseBuilding.houses", "scroll.japaneseBuilding.uninhabited", "scroll.japaneseBuilding.player", "scroll.japaneseItem.food", "scroll.japaneseItem.tools", "scroll.japaneseItem.weapons", "scroll.japaneseItem.construction"}).setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("japaneseAllParchment");
		GameRegistry.registerItem(japaneseAllParchment, "japaneseAllParchment");
	}
	
	public static void recipies() {
		GameRegistry.addShapelessRecipe(new ItemStack(vegCurry, 1), new ItemStack(MillItems.rice), new ItemStack(MillItems.turmeric));
    	GameRegistry.addShapelessRecipe(new ItemStack(murghCurry, 1), new ItemStack(MillItems.rice), new ItemStack(MillItems.turmeric), new ItemStack(Items.chicken));
    	GameRegistry.addRecipe(new ItemStack(masa, 1), 
    			"AAA",
    			'A', new ItemStack(MillItems.maize));
    	GameRegistry.addRecipe(new ItemStack(wah, 1), 
    			"ABA",
    			'A', new ItemStack(MillItems.maize), 'B', new ItemStack(Items.chicken));
    	GameRegistry.addShapelessRecipe(new ItemStack(wine, 1), new ItemStack(MillItems.grapes), new ItemStack(MillItems.grapes), new ItemStack(MillItems.grapes), new ItemStack(MillItems.grapes), 
    			new ItemStack(MillItems.grapes), new ItemStack(MillItems.grapes));
	}
	
	@SideOnly(Side.CLIENT)
	public static void prerender() {
		//Tools
		//ModelBakery.addVariantName(japaneseBow_pulling_1, Millenaire.MODID + ":japaneseBow", Millenaire.MODID + ":japaneseBow_pulling_1", Millenaire.MODID + ":japaneseBow_pulling_2", Millenaire.MODID + ":japaneseBow_pulling_3");
		ModelBakery.registerItemVariants(japaneseBow, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow", "inventory"), new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_1", "inventory"),
				new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_2", "inventory"), new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_3", "inventory"));
		ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow", "inventory"));
		/*ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_1", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_2", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(japaneseBow, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_3", "inventory"));*/
		
		//Amulets
		ModelLoader.setCustomModelResourceLocation(amuletSkollHati,0, new ModelResourceLocation(Millenaire.MODID + ":amuletSkollHati"));
		ModelLoader.setCustomModelResourceLocation(amuletAlchemist, 0, new ModelResourceLocation(Millenaire.MODID + ":amuletAlchemist"));
		ModelLoader.setCustomModelResourceLocation(amuletVishnu, 0, new ModelResourceLocation(Millenaire.MODID + ":amuletVishnu"));
		ModelLoader.setCustomModelResourceLocation(amuletYggdrasil, 0, new ModelResourceLocation(Millenaire.MODID + ":amuletYggdrasil"));
		
		//Parchments
		ModelLoader.setCustomModelResourceLocation(normanVillagerParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentVillager"));
    	ModelLoader.setCustomModelResourceLocation(normanBuildingParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentBuilding"));
    	ModelLoader.setCustomModelResourceLocation(normanItemParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentItem"));
    	ModelLoader.setCustomModelResourceLocation(normanAllParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentAll"));
    	
    	ModelLoader.setCustomModelResourceLocation(byzantineVillagerParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentVillager"));
    	ModelLoader.setCustomModelResourceLocation(byzantineBuildingParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentBuilding"));
    	ModelLoader.setCustomModelResourceLocation(byzantineItemParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentItem"));
    	ModelLoader.setCustomModelResourceLocation(byzantineAllParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentAll"));
    	
    	ModelLoader.setCustomModelResourceLocation(hindiVillagerParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentVillager"));
    	ModelLoader.setCustomModelResourceLocation(hindiBuildingParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentBuilding"));
    	ModelLoader.setCustomModelResourceLocation(hindiItemParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentItem"));
    	ModelLoader.setCustomModelResourceLocation(hindiAllParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentAll"));
    	
    	ModelLoader.setCustomModelResourceLocation(mayanVillagerParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentVillager"));
    	ModelLoader.setCustomModelResourceLocation(mayanBuildingParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentBuilding"));
    	ModelLoader.setCustomModelResourceLocation(mayanItemParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentItem"));
    	ModelLoader.setCustomModelResourceLocation(mayanAllParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentAll"));
    	
    	ModelLoader.setCustomModelResourceLocation(japaneseVillagerParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentVillager"));
    	ModelLoader.setCustomModelResourceLocation(japaneseBuildingParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentBuilding"));
    	ModelLoader.setCustomModelResourceLocation(japaneseItemParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentItem"));
    	ModelLoader.setCustomModelResourceLocation(japaneseAllParchment, 0, new ModelResourceLocation(Millenaire.MODID + ":parchmentAll"));
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

		renderItem.getItemModelMesher().register(ciderApple, 0, new ModelResourceLocation(Millenaire.MODID + ":ciderApple", "inventory"));
		renderItem.getItemModelMesher().register(cider, 0, new ModelResourceLocation(Millenaire.MODID + ":cider", "inventory"));
		renderItem.getItemModelMesher().register(calva, 0, new ModelResourceLocation(Millenaire.MODID + ":calva", "inventory"));
		renderItem.getItemModelMesher().register(tripes, 0, new ModelResourceLocation(Millenaire.MODID + ":tripes", "inventory"));
		renderItem.getItemModelMesher().register(boudinNoir, 0, new ModelResourceLocation(Millenaire.MODID + ":boudinNoir", "inventory"));
		
		renderItem.getItemModelMesher().register(vegCurry, 0, new ModelResourceLocation(Millenaire.MODID + ":vegCurry", "inventory"));
		renderItem.getItemModelMesher().register(murghCurry, 0, new ModelResourceLocation(Millenaire.MODID + ":murghCurry", "inventory"));
		renderItem.getItemModelMesher().register(rasgulla, 0, new ModelResourceLocation(Millenaire.MODID + ":rasgulla", "inventory"));
		
		renderItem.getItemModelMesher().register(cacauhaa, 0, new ModelResourceLocation(Millenaire.MODID + ":cacauhaa", "inventory"));
		renderItem.getItemModelMesher().register(masa, 0, new ModelResourceLocation(Millenaire.MODID + ":masa", "inventory"));
		renderItem.getItemModelMesher().register(wah, 0, new ModelResourceLocation(Millenaire.MODID + ":wah", "inventory"));
		
		renderItem.getItemModelMesher().register(wine, 0, new ModelResourceLocation(Millenaire.MODID + ":wine", "inventory"));
		renderItem.getItemModelMesher().register(malvasiaWine, 0, new ModelResourceLocation(Millenaire.MODID + ":malvasiaWine", "inventory"));
		renderItem.getItemModelMesher().register(feta, 0, new ModelResourceLocation(Millenaire.MODID + ":feta", "inventory"));
		renderItem.getItemModelMesher().register(souvlaki, 0, new ModelResourceLocation(Millenaire.MODID + ":souvlaki", "inventory"));
		
		renderItem.getItemModelMesher().register(sake, 0, new ModelResourceLocation(Millenaire.MODID + ":sake", "inventory"));
		renderItem.getItemModelMesher().register(udon, 0, new ModelResourceLocation(Millenaire.MODID + ":udon", "inventory"));
		renderItem.getItemModelMesher().register(ikayaki, 0, new ModelResourceLocation(Millenaire.MODID + ":ikayaki", "inventory"));
		
		//Armour
		renderItem.getItemModelMesher().register(normanHelmet, 0, new ModelResourceLocation(Millenaire.MODID + ":normanHelmet", "inventory"));
		renderItem.getItemModelMesher().register(normanChestplate, 0, new ModelResourceLocation(Millenaire.MODID + ":normanChestplate", "inventory"));
		renderItem.getItemModelMesher().register(normanLeggings, 0, new ModelResourceLocation(Millenaire.MODID + ":normanLeggings", "inventory"));
		renderItem.getItemModelMesher().register(normanBoots, 0, new ModelResourceLocation(Millenaire.MODID + ":normanBoots", "inventory"));
		
		renderItem.getItemModelMesher().register(byzantineHelmet, 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineHelmet", "inventory"));
		renderItem.getItemModelMesher().register(byzantineChestplate, 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineChestplate", "inventory"));
		renderItem.getItemModelMesher().register(byzantineLeggings, 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineLeggings", "inventory"));
		renderItem.getItemModelMesher().register(byzantineBoots, 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineBoots", "inventory"));
		
		renderItem.getItemModelMesher().register(japaneseGuardHelmet, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseGuardHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardChestplate, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseGuardChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardLeggings, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseGuardLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseGuardBoots, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseGuardBoots", "inventory"));
		
		renderItem.getItemModelMesher().register(japaneseBlueHelmet, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBlueHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueChestplate, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBlueChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueLeggings, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBlueLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseBlueBoots, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBlueBoots", "inventory"));
		
		renderItem.getItemModelMesher().register(japaneseRedHelmet, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseRedHelmet", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedChestplate, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseRedChestplate", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedLeggings, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseRedLeggings", "inventory"));
		renderItem.getItemModelMesher().register(japaneseRedBoots, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseRedBoots", "inventory"));
		
		renderItem.getItemModelMesher().register(mayanQuestCrown, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanQuestCrown", "inventory"));

		//Wands
		renderItem.getItemModelMesher().register(wandSummoning, 0, new ModelResourceLocation(Millenaire.MODID + ":wandSummoning", "inventory"));
		renderItem.getItemModelMesher().register(wandNegation, 0, new ModelResourceLocation(Millenaire.MODID + ":wandNegation", "inventory"));
		renderItem.getItemModelMesher().register(wandCreative, 0, new ModelResourceLocation(Millenaire.MODID + ":wandCreative", "inventory"));
		renderItem.getItemModelMesher().register(tuningFork, 0, new ModelResourceLocation(Millenaire.MODID + ":tuningFork", "inventory"));

		//Tools
		renderItem.getItemModelMesher().register(normanAxe, 0, new ModelResourceLocation(Millenaire.MODID + ":normanAxe", "inventory"));
		renderItem.getItemModelMesher().register(normanShovel, 0, new ModelResourceLocation(Millenaire.MODID + ":normanShovel", "inventory"));
		renderItem.getItemModelMesher().register(normanPickaxe, 0, new ModelResourceLocation(Millenaire.MODID + ":normanPickaxe", "inventory"));
		renderItem.getItemModelMesher().register(normanHoe, 0, new ModelResourceLocation(Millenaire.MODID + ":normanHoe", "inventory"));
		renderItem.getItemModelMesher().register(normanSword, 0, new ModelResourceLocation(Millenaire.MODID + ":normanSword", "inventory"));
		
		renderItem.getItemModelMesher().register(mayanAxe, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanAxe", "inventory"));
		renderItem.getItemModelMesher().register(mayanShovel, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanShovel", "inventory"));
		renderItem.getItemModelMesher().register(mayanPickaxe, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanPickaxe", "inventory"));
		renderItem.getItemModelMesher().register(mayanHoe, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanHoe", "inventory"));
		renderItem.getItemModelMesher().register(mayanMace, 0, new ModelResourceLocation(Millenaire.MODID + ":mayanMace", "inventory"));
		
		renderItem.getItemModelMesher().register(byzantineMace, 0, new ModelResourceLocation(Millenaire.MODID + ":byzantineMace", "inventory"));
		
		renderItem.getItemModelMesher().register(japaneseSword, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseSword", "inventory"));
		//renderItem.getItemModelMesher().register(japaneseBow_pulling_1, 0, new ModelResourceLocation(Millenaire.MODID + ":japaneseBow_pulling_1", "inventory"));
		
		//Wallet
		renderItem.getItemModelMesher().register(itemMillPurse, 0, new ModelResourceLocation(Millenaire.MODID + ":itemMillPurse", "inventory"));
		
		//Sign
		renderItem.getItemModelMesher().register(itemMillSign, 0, new ModelResourceLocation(Millenaire.MODID + ":blockMillSign", "inventory"));
	}
}
