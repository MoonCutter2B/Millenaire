package org.millenaire.common.forge;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import org.millenaire.client.network.ClientReceiver;
import org.millenaire.common.Culture;
import org.millenaire.common.EntityMillDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Quest;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.WorldGenVillage;
import org.millenaire.common.block.BlockDecorative;
import org.millenaire.common.block.BlockDecorative.ItemDecorative;
import org.millenaire.common.block.BlockDecorativeSlab;
import org.millenaire.common.block.BlockDecorativeSlab.ItemDecorativeSlab;
import org.millenaire.common.block.BlockMLNPane;
import org.millenaire.common.block.BlockMillChest;
import org.millenaire.common.block.BlockMillCrops;
import org.millenaire.common.block.BlockOrientedBrick;
import org.millenaire.common.block.BlockOrientedSlab;
import org.millenaire.common.block.BlockPanel;
import org.millenaire.common.building.BuildingPlan;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.forge.BuildingChunkLoader.ChunkLoaderCallback;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.Goods;
import org.millenaire.common.item.Goods.ItemAmuletAlchemist;
import org.millenaire.common.item.Goods.ItemAmuletSkollHati;
import org.millenaire.common.item.Goods.ItemAmuletVishnu;
import org.millenaire.common.item.Goods.ItemAmuletYddrasil;
import org.millenaire.common.item.Goods.ItemBrickMould;
import org.millenaire.common.item.Goods.ItemClothes;
import org.millenaire.common.item.Goods.ItemMayanQuestCrown;
import org.millenaire.common.item.Goods.ItemMillenaireArmour;
import org.millenaire.common.item.Goods.ItemMillenaireAxe;
import org.millenaire.common.item.Goods.ItemMillenaireBow;
import org.millenaire.common.item.Goods.ItemMillenaireHoe;
import org.millenaire.common.item.Goods.ItemMillenairePickaxe;
import org.millenaire.common.item.Goods.ItemMillenaireShovel;
import org.millenaire.common.item.Goods.ItemMillenaireSword;
import org.millenaire.common.item.Goods.ItemNegationWand;
import org.millenaire.common.item.Goods.ItemSummoningWand;
import org.millenaire.common.item.Goods.ItemTapestry;
import org.millenaire.common.item.Goods.ItemText;
import org.millenaire.common.item.ItemFoodMultiple;
import org.millenaire.common.item.ItemMillSeeds;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.item.ItemPurse;
import org.millenaire.common.network.ServerReceiver;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Mill.modId, name = Mill.name, version = Mill.versionNumber)
public class Mill {
	static class CreativeTabMill extends CreativeTabs {

		public CreativeTabMill(final String par2Str) {
			super(par2Str);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public Item getTabIconItem() {
			return Mill.denier_or;
		}
	}

	public static final String versionNumber = "5.2.0";

	public static final String versionBound = "[5.2.0,6.0)";
	public static final String modId = "millenaire";
	public static final String name = "Mill\u00e9naire";

	public static final String version = name + " " + versionNumber;

	public static final CreativeTabs tabMillenaire = new CreativeTabMill(
			"Millenaire");

	@SidedProxy(clientSide = "org.millenaire.client.forge.ClientProxy", serverSide = "org.millenaire.common.forge.CommonProxy")
	public static CommonProxy proxy;

	@Instance
	public static Mill instance;

	public static final int VILLAGER_ENT_ID = 1;

	public static List<MillWorld> serverWorlds = new ArrayList<MillWorld>();
	public static MillWorld clientWorld = null;

	public static List<File> loadingDirs = new ArrayList<File>();

	static ToolMaterial TOOLS_norman = EnumHelper.addToolMaterial(
			"normanTools", 2, 1561, 10.0F, 4.0F, 10);
	static ToolMaterial TOOLS_obsidian = EnumHelper.addToolMaterial(
			"obsidianTools", 3, 1561, 6.0F, 2.0F, 25);

	static ArmorMaterial ARMOUR_norman = EnumHelper.addArmorMaterial(
			"normanArmour", 66, new int[] { 3, 8, 6, 3 }, 10);
	static ArmorMaterial ARMOUR_japaneseWarrior = EnumHelper.addArmorMaterial(
			"japaneseWarrior", 33, new int[] { 2, 6, 5, 2 }, 25);
	static ArmorMaterial ARMOUR_japaneseGuard = EnumHelper.addArmorMaterial(
			"japaneseGuard", 25, new int[] { 2, 5, 4, 1 }, 25);
	static ArmorMaterial ARMOUR_byzantine = EnumHelper.addArmorMaterial(
			"byzantineArmour", 33, new int[] { 3, 8, 6, 3 }, 20);

	public static FMLEventChannel millChannel;

	public static Block lockedChest;
	public static Block panel;
	public static BlockDecorative wood_decoration;
	public static BlockDecorative earth_decoration;
	public static BlockDecorative stone_decoration;
	public static BlockDecorativeSlab path;
	public static BlockDecorativeSlab pathSlab;

	public static BlockOrientedBrick byzantine_tiles;
	public static BlockOrientedSlab byzantine_tile_slab;
	public static BlockOrientedBrick byzantine_stone_tiles;

	public static BlockMillCrops cropRice;
	public static BlockMillCrops cropTurmeric;
	public static BlockMillCrops cropMaize;
	public static BlockMillCrops cropVine;

	public static Block paperWall;
	public static int normanArmourId = 0;
	public static int japaneseWarriorBlueArmourId = 0;
	public static int japaneseWarriorRedArmourId = 0;
	public static int japaneseGuardArmourId = 0;
	public static int byzantineArmourId = 0;
	public static int mayanQuestArmourId = 0;

	public static Item denier;
	public static Item denier_or;
	public static Item denier_argent;
	public static Item ciderapple;
	public static Item cider;
	public static Item calva;
	public static Item tripes;

	public static Item normanPickaxe;
	public static Item normanAxe;
	public static Item normanShovel;
	public static Item normanHoe;

	public static Item summoningWand;

	public static Item normanBroadsword;
	public static Item normanHelmet;
	public static Item normanPlate;
	public static Item normanLegs;
	public static Item normanBoots;
	public static Item parchmentVillagers;

	public static Item parchmentBuildings;

	public static Item parchmentItems;

	public static Item parchmentComplete;
	public static Item boudin;
	public static Item tapestry;
	public static Item vishnu_amulet;

	public static Item alchemist_amulet;

	public static Item yddrasil_amulet;

	public static Item skoll_hati_amulet;
	public static Item parchmentVillageScroll;

	public static Item rice;
	public static Item turmeric;
	public static Item vegcurry;
	public static Item chickencurry;
	public static Item brickmould;
	public static Item rasgulla;
	public static Item indianstatue;

	public static Item parchmentIndianVillagers;
	public static Item parchmentIndianBuildings;
	public static Item parchmentIndianItems;
	public static Item parchmentIndianComplete;

	public static Item mayanstatue;
	public static Item maize;
	public static Item wah;
	public static Item masa;

	public static Item parchmentMayanVillagers;
	public static Item parchmentMayanBuildings;
	public static Item parchmentMayanItems;
	public static Item parchmentMayanComplete;

	public static Item parchmentSadhu;

	public static Item unknownPowder;
	public static Item udon;
	public static Item tachiSword;
	public static Item negationWand;
	public static Item obsidianFlake;
	public static Item mayanMace;
	public static Item mayanPickaxe;
	public static Item mayanAxe;
	public static Item mayanShovel;
	public static Item mayanHoe;

	public static Item yumiBow;

	public static Item japaneseWarriorBlueLegs;
	public static Item japaneseWarriorBlueHelmet;
	public static Item japaneseWarriorBluePlate;
	public static Item japaneseWarriorBlueBoots;
	public static Item japaneseWarriorRedLegs;
	public static Item japaneseWarriorRedHelmet;
	public static Item japaneseWarriorRedPlate;
	public static Item japaneseWarriorRedBoots;

	public static Item japaneseGuardLegs;
	public static Item japaneseGuardHelmet;
	public static Item japaneseGuardPlate;
	public static Item japaneseGuardBoots;

	public static Item parchmentJapaneseVillagers;
	public static Item parchmentJapaneseBuildings;
	public static Item parchmentJapaneseItems;
	public static Item parchmentJapaneseComplete;

	public static Item grapes;
	public static Item wineFancy;
	public static Item silk;
	public static Item byzantineiconsmall;
	public static Item byzantineiconmedium;
	public static Item byzantineiconlarge;

	public static Item byzantineMace;

	public static Item byzantineLegs;
	public static Item byzantineHelmet;
	public static Item byzantinePlate;
	public static Item byzantineBoots;

	public static ItemClothes clothes;

	public static Item wineBasic, lambRaw, lambCooked, feta, souvlaki;

	public static ItemPurse purse;

	public static Item sake, cacauhaa;

	public static Item mayanQuestCrown;

	public static Item ikayaki;

	public static boolean loadingComplete = false;

	public static boolean startMessageDisplayed = false;

	@SuppressWarnings("rawtypes")
	public static HashMap<Class, String> entityNames;

	public static final String ENTITY_PIG = "Pig", ENTITY_COW = "Cow",
			ENTITY_CHICKEN = "Chicken", ENTITY_SHEEP = "Sheep",
			ENTITY_SQUID = "Squid", ENTITY_WOLF = "Wolf";

	public static final String ENTITY_SKELETON = "Skeleton",
			ENTITY_CREEPER = "Creeper", ENTITY_SPIDER = "Spider",
			ENTITY_CAVESPIDER = "CaveSpider", ENTITY_ZOMBIE = "Zombie",
			ENTITY_TARGETED_GHAST = "MillGhast",
			ENTITY_TARGETED_BLAZE = "MillBlaze",
			ENTITY_TARGETED_WITHERSKELETON = "MillWitherSkeleton";

	public static final String CROP_WHEAT = "wheat", CROP_CARROT = "carrot",
			CROP_POTATO = "potato", CROP_RICE = "rice",
			CROP_TURMERIC = "turmeric", CROP_MAIZE = "maize",
			CROP_VINE = "vine", CROP_CACAO = "cacao";

	public static boolean startupError = false;

	public static boolean checkedMillenaireDir = false;

	public static boolean displayMillenaireLocationError = false;

	public static MillWorld getMillWorld(final World world) {

		if (clientWorld != null && clientWorld.world == world) {
			return clientWorld;
		}

		for (final MillWorld mw : serverWorlds) {
			if (mw.world == world) {
				return mw;
			}
		}

		if (serverWorlds != null && serverWorlds.size() > 0) {
			return serverWorlds.get(0);
		}

		return null;
	}

	public static boolean isDistantClient() {
		if (clientWorld != null && serverWorlds.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isRunningDeobf() {
		return EntityCreature.class.getSimpleName().equals("EntityCreature");
	}

	@SuppressWarnings("rawtypes")
	protected void initBlockItems() {

		lockedChest = new BlockMillChest().setBlockName("ml_building")
				.setHardness(10F).setResistance(2000F)
				.setStepSound(Block.soundTypeWood);

		panel = new BlockPanel(TileEntityPanel.class, false)
				.setBlockName("ml_panel").setHardness(10F).setResistance(2000F)
				.setStepSound(Block.soundTypeWood);
		wood_decoration = new BlockDecorative(Material.wood);
		earth_decoration = new BlockDecorative(Material.ground);
		stone_decoration = new BlockDecorative(Material.rock);
		path = new BlockDecorativeSlab(Material.ground, true);
		pathSlab = new BlockDecorativeSlab(Material.ground, false);

		cropRice = new BlockMillCrops(new String[] { "rice0", "rice0", "rice0",
				"rice0", "rice0", "rice0", "rice0", "rice1" }, true, false);
		cropTurmeric = new BlockMillCrops(new String[] { "turmeric0",
				"turmeric0", "turmeric0", "turmeric0", "turmeric0",
				"turmeric0", "turmeric0", "turmeric1" }, false, false);
		cropMaize = new BlockMillCrops(new String[] { "maize0", "maize0",
				"maize0", "maize0", "maize0", "maize0", "maize0", "maize1" },
				false, true);
		cropVine = new BlockMillCrops(new String[] { "vine0", "vine0", "vine0",
				"vine0", "vine0", "vine0", "vine0", "vine1" }, false, false);
		rice = new ItemMillSeeds("rice", cropRice, "rice")
				.setUnlocalizedName("ml_rice");
		turmeric = new ItemMillSeeds("turmeric", cropTurmeric, "turmeric")
				.setUnlocalizedName("ml_turmeric");
		maize = new ItemMillSeeds("maize", cropMaize, "maize")
				.setUnlocalizedName("ml_maize");
		grapes = new ItemMillSeeds("grapes", cropVine, "vine")
				.setUnlocalizedName("ml_vine");
		cropRice.setSeed((IPlantable) rice);
		cropTurmeric.setSeed((IPlantable) turmeric);
		cropMaize.setSeed((IPlantable) maize);
		cropVine.setSeed((IPlantable) grapes);
		cropRice.setBlockName("ml_cropRice");
		cropTurmeric.setBlockName("ml_cropTurmeric");
		cropMaize.setBlockName("ml_cropMaize");
		cropVine.setBlockName("ml_cropVine");

		paperWall = new BlockMLNPane("paperwall", "paperwall", Material.cloth,
				true).setHardness(0.3F).setStepSound(Block.soundTypeCloth)
				.setBlockName("ml_panes");

		byzantine_tiles = (BlockOrientedBrick) new BlockOrientedBrick(
				"tilestopvert", "tilestophor", "tilestopvert", "tilestophor",
				"tilesfront", "tilestophor").setHardness(2.0F)
				.setResistance(10.0F).setStepSound(Block.soundTypeStone)
				.setBlockName("byzantine_brick");
		byzantine_tile_slab = (BlockOrientedSlab) new BlockOrientedSlab(
				"tilestophor", "tilestopvert", "tilesfront").setHardness(2.0F)
				.setResistance(10.0F).setStepSound(Block.soundTypeStone)
				.setBlockName("byzantine_brick_slab");
		byzantine_stone_tiles = (BlockOrientedBrick) new BlockOrientedBrick(
				"tilestopvert", "tilestophor", "stone", "stone",
				"tileshalffront", "tileshalfside").setHardness(2.0F)
				.setResistance(10.0F).setStepSound(Block.soundTypeStone)
				.setBlockName("byzantine_mixedbrick");

		lockedChest.setHarvestLevel("axe", 0);
		wood_decoration.setHarvestLevel("axe", 0);
		paperWall.setHarvestLevel("axe", 0);
		panel.setHarvestLevel("axe", 0);
		stone_decoration.setHarvestLevel("pickaxe", 0);
		byzantine_tiles.setHarvestLevel("pickaxe", 0);
		byzantine_tile_slab.setHarvestLevel("pickaxe", 0);
		byzantine_stone_tiles.setHarvestLevel("pickaxe", 0);
		earth_decoration.setHarvestLevel("shovel", 0);
		path.setHarvestLevel("shovel", 0);
		pathSlab.setHarvestLevel("shovel", 0);

		proxy.setTextureIds();

		denier = new ItemText("denier").setUnlocalizedName("ml_denier");
		denier_or = new ItemText("denier_or")
				.setUnlocalizedName("ml_denier_or");
		denier_argent = new ItemText("denier_argent")
				.setUnlocalizedName("ml_denier_argent");
		ciderapple = new ItemFoodMultiple("ciderapple", 0, 0, 1, 0.05f, false,
				0).setUnlocalizedName("ml_ciderapple").setMaxStackSize(64);
		;
		cider = new ItemFoodMultiple("cider", 4, 15, 0, 0, true, 5)
				.setAlwaysEdible().setUnlocalizedName("ml_cider");
		calva = new ItemFoodMultiple("calva", 8, 30, 0, 0, true, 10)
				.setPotionEffect(Potion.damageBoost.id, 180, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_calva");
		tripes = new ItemFoodMultiple("tripes", 0, 0, 10, 1f, false, 0)
				.setPotionEffect(Potion.regeneration.id, 90, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_tripes");

		normanPickaxe = new ItemMillenairePickaxe("normanpickaxe", TOOLS_norman)
				.setUnlocalizedName("ml_normanPickaxe");
		normanAxe = new ItemMillenaireAxe("normanaxe", TOOLS_norman)
				.setUnlocalizedName("ml_normanAxe");
		normanShovel = new ItemMillenaireShovel("normanshovel", TOOLS_norman)
				.setUnlocalizedName("ml_normanShovel");
		normanHoe = new ItemMillenaireHoe("normanhoe", TOOLS_norman)
				.setUnlocalizedName("ml_normanHoe");

		summoningWand = new ItemSummoningWand("summoningwand").setFull3D()
				.setUnlocalizedName("ml_villageWand");

		normanBroadsword = new ItemMillenaireSword("normansword", TOOLS_norman,
				0, 0, false).setUnlocalizedName("ml_normanBroadsword");
		normanHelmet = new ItemMillenaireArmour("normanhelmet", ARMOUR_norman,
				normanArmourId, 0).setUnlocalizedName("ml_normanHelmet");
		normanPlate = new ItemMillenaireArmour("normanplate", ARMOUR_norman,
				normanArmourId, 1).setUnlocalizedName("ml_normanPlate");
		normanLegs = new ItemMillenaireArmour("normanlegs", ARMOUR_norman,
				normanArmourId, 2).setUnlocalizedName("ml_normanLegs");
		normanBoots = new ItemMillenaireArmour("normanboots", ARMOUR_norman,
				normanArmourId, 3).setUnlocalizedName("ml_normanBoots");

		parchmentVillagers = new ItemParchment("parchmentvillagers",
				ItemParchment.villagers)
				.setUnlocalizedName("ml_parchmentVillagers");

		parchmentBuildings = new ItemParchment("parchmentbuildings",
				ItemParchment.buildings)
				.setUnlocalizedName("ml_parchmentBuildings");

		parchmentItems = new ItemParchment("parchmentitems",
				ItemParchment.items).setUnlocalizedName("ml_parchmentItems");

		parchmentComplete = new ItemParchment("parchmentall", new int[] {
				ItemParchment.villagers, ItemParchment.buildings,
				ItemParchment.items })
				.setUnlocalizedName("ml_marchmentComplete");

		boudin = new ItemFoodMultiple("boudin", 0, 0, 10, 1f, false, 0)
				.setUnlocalizedName("ml_boudin");

		tapestry = new ItemTapestry("normantapestry",
				EntityMillDecoration.NORMAN_TAPESTRY)
				.setUnlocalizedName("ml_tapestry");

		vishnu_amulet = new ItemAmuletVishnu("amulet_vishnu")
				.setCreativeTab(Mill.tabMillenaire)
				.setUnlocalizedName("ml_raven_amulet").setMaxStackSize(1);
		alchemist_amulet = new ItemAmuletAlchemist("amulet_alchemist")
				.setCreativeTab(Mill.tabMillenaire)
				.setUnlocalizedName("ml_dwarves_amulet").setMaxStackSize(1);
		yddrasil_amulet = new ItemAmuletYddrasil("amulet_yggdrasil")
				.setCreativeTab(Mill.tabMillenaire)
				.setUnlocalizedName("ml_yddrasil_amulet").setMaxStackSize(1);

		skoll_hati_amulet = new ItemAmuletSkollHati("amulet_skollhati")
				.setCreativeTab(Mill.tabMillenaire)
				.setUnlocalizedName("ml_skoll_hati_amulet").setMaxStackSize(1)
				.setMaxDamage(10);
		parchmentVillageScroll = new ItemParchment("parchmentvillage",
				new int[] { ItemParchment.villageBook })
				.setUnlocalizedName("ml_parchmentVillageScroll");

		vegcurry = new ItemFoodMultiple("curry", 0, 0, 6, 0.6f, false, 0)
				.setUnlocalizedName("ml_vegcurry");
		chickencurry = new ItemFoodMultiple("currychicken", 0, 0, 8, 0.8f,
				false, 0)
				.setPotionEffect(Potion.fireResistance.id, 8 * 60, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_chickencurry");
		brickmould = new ItemBrickMould("brickmould")
				.setUnlocalizedName("ml_brickmould").setMaxStackSize(1)
				.setMaxDamage(128);
		rasgulla = new ItemFoodMultiple("rasgulla", 2, 30, 0, 0, false, 0)
				.setPotionEffect(Potion.moveSpeed.id, 8 * 60, 1, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_rasgullaId");
		indianstatue = new ItemTapestry("indianstatue",
				EntityMillDecoration.INDIAN_STATUE)
				.setUnlocalizedName("ml_indianstatue");

		parchmentIndianVillagers = new ItemParchment("parchmentvillagers",
				ItemParchment.indianVillagers)
				.setUnlocalizedName("ml_parchmentIndianVillagers");
		parchmentIndianBuildings = new ItemParchment("parchmentbuildings",
				ItemParchment.indianBuildings)
				.setUnlocalizedName("ml_parchmentIndianBuildings");
		parchmentIndianItems = new ItemParchment("parchmentitems",
				ItemParchment.indianItems)
				.setUnlocalizedName("ml_parchmentIndianItems");
		parchmentIndianComplete = new ItemParchment("parchmentall", new int[] {
				ItemParchment.indianVillagers, ItemParchment.indianBuildings,
				ItemParchment.indianItems })
				.setUnlocalizedName("ml_marchmentIndianComplete");

		mayanstatue = new ItemTapestry("mayanstatue",
				EntityMillDecoration.MAYAN_STATUE)
				.setUnlocalizedName("ml_mayanstatue");
		masa = new ItemFoodMultiple("masa", 0, 0, 6, 0.6f, false, 0)
				.setUnlocalizedName("ml_masa");
		wah = new ItemFoodMultiple("wah", 0, 0, 10, 1f, false, 0)
				.setPotionEffect(Potion.digSpeed.id, 8 * 60, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_wah");

		parchmentMayanVillagers = new ItemParchment("parchmentvillagers",
				ItemParchment.mayanVillagers)
				.setUnlocalizedName("ml_parchmentMayanVillagers");
		parchmentMayanBuildings = new ItemParchment("parchmentbuildings",
				ItemParchment.mayanBuildings)
				.setUnlocalizedName("ml_parchmentMayanBuildings");
		parchmentMayanItems = new ItemParchment("parchmentitems",
				ItemParchment.mayanItems)
				.setUnlocalizedName("ml_parchmentMayanItems");
		parchmentMayanComplete = new ItemParchment("parchmentall", new int[] {
				ItemParchment.mayanVillagers, ItemParchment.mayanBuildings,
				ItemParchment.mayanItems })
				.setUnlocalizedName("ml_parchmentMayanComplete");

		parchmentSadhu = new ItemParchment("parchmentall",
				new int[] { ItemParchment.sadhu })
				.setUnlocalizedName("ml_parchmentSadhu");

		unknownPowder = new ItemText("unknownpowder").setUnlocalizedName(
				"ml_unknownPowder").setCreativeTab(Mill.tabMillenaire);

		udon = new ItemFoodMultiple("udon", 0, 0, 8, 0.8f, false, 0)
				.setAlwaysEdible().setUnlocalizedName("ml_udon");

		tachiSword = new ItemMillenaireSword("tachisword", ToolMaterial.IRON,
				(float) 0.2, 5, false).setUnlocalizedName("ml_taichiSword");

		negationWand = new ItemNegationWand("negationwand").setFull3D()
				.setUnlocalizedName("ml_negationWand");

		obsidianFlake = new ItemText("obsidianflake")
				.setUnlocalizedName("ml_obsidianFlake");
		mayanMace = new ItemMillenaireSword("mayanmace", TOOLS_obsidian, 0, 0,
				false).setUnlocalizedName("ml_mayanMace");
		mayanPickaxe = new ItemMillenairePickaxe("mayanpickaxe", TOOLS_obsidian)
				.setUnlocalizedName("ml_mayanPickaxe");
		mayanAxe = new ItemMillenaireAxe("mayanaxe", TOOLS_obsidian)
				.setUnlocalizedName("ml_mayanAxe");
		mayanShovel = new ItemMillenaireShovel("mayanshovel", TOOLS_obsidian)
				.setUnlocalizedName("ml_mayanShovel");
		mayanHoe = new ItemMillenaireHoe("mayanhoe", TOOLS_obsidian)
				.setUnlocalizedName("ml_mayanHoe");

		yumiBow = new ItemMillenaireBow(2, (float) 0.5, "yumibow0", "yumibow1",
				"yumibow2", "yumibow3").setUnlocalizedName("ml_yumiBow")
				.setFull3D();

		japaneseWarriorBlueLegs = new ItemMillenaireArmour("japanesebluelegs",
				ARMOUR_japaneseWarrior, japaneseWarriorBlueArmourId, 2)
				.setUnlocalizedName("ml_japaneseWarriorBlueLegs");
		japaneseWarriorBlueHelmet = new ItemMillenaireArmour(
				"japanesebluehelmet", ARMOUR_japaneseWarrior,
				japaneseWarriorBlueArmourId, 0)
				.setUnlocalizedName("ml_japaneseWarriorBlueHelmet");
		japaneseWarriorBluePlate = new ItemMillenaireArmour(
				"japaneseblueplate", ARMOUR_japaneseWarrior,
				japaneseWarriorBlueArmourId, 1)
				.setUnlocalizedName("ml_japaneseWarriorBluePlate");
		japaneseWarriorBlueBoots = new ItemMillenaireArmour(
				"japaneseblueboots", ARMOUR_japaneseWarrior,
				japaneseWarriorBlueArmourId, 3)
				.setUnlocalizedName("ml_japaneseWarriorBlueBoots");

		japaneseWarriorRedLegs = new ItemMillenaireArmour("japaneseredlegs",
				ARMOUR_japaneseWarrior, japaneseWarriorRedArmourId, 2)
				.setUnlocalizedName("ml_japaneseWarriorRedLegs");
		japaneseWarriorRedHelmet = new ItemMillenaireArmour(
				"japaneseredhelmet", ARMOUR_japaneseWarrior,
				japaneseWarriorRedArmourId, 0)
				.setUnlocalizedName("ml_japaneseWarriorRedHelmet");
		japaneseWarriorRedPlate = new ItemMillenaireArmour("japaneseredplate",
				ARMOUR_japaneseWarrior, japaneseWarriorRedArmourId, 1)
				.setUnlocalizedName("ml_japaneseWarriorRedPlate");
		japaneseWarriorRedBoots = new ItemMillenaireArmour("japaneseredboots",
				ARMOUR_japaneseWarrior, japaneseWarriorRedArmourId, 3)
				.setUnlocalizedName("ml_japaneseWarriorRedBoots");

		japaneseGuardLegs = new ItemMillenaireArmour("japaneseguardlegs",
				ARMOUR_japaneseGuard, japaneseGuardArmourId, 2)
				.setUnlocalizedName("ml_japaneseGuardLegs");
		japaneseGuardHelmet = new ItemMillenaireArmour("japaneseguardhelmet",
				ARMOUR_japaneseGuard, japaneseGuardArmourId, 0)
				.setUnlocalizedName("ml_japaneseGuardHelmet");
		japaneseGuardPlate = new ItemMillenaireArmour("japaneseguardplate",
				ARMOUR_japaneseGuard, japaneseGuardArmourId, 1)
				.setUnlocalizedName("ml_japaneseGuardPlate");
		japaneseGuardBoots = new ItemMillenaireArmour("japaneseguardboots",
				ARMOUR_japaneseGuard, japaneseGuardArmourId, 3)
				.setUnlocalizedName("ml_japaneseGuardBoots");

		parchmentJapaneseVillagers = new ItemParchment("parchmentvillagers",
				ItemParchment.japaneseVillagers)
				.setUnlocalizedName("ml_parchmentJapaneseVillagers");
		parchmentJapaneseBuildings = new ItemParchment("parchmentbuildings",
				ItemParchment.japaneseBuildings)
				.setUnlocalizedName("ml_parchmentJapaneseBuildings");
		parchmentJapaneseItems = new ItemParchment("parchmentitems",
				ItemParchment.japaneseItems)
				.setUnlocalizedName("ml_parchmentJapaneseItems");
		parchmentJapaneseComplete = new ItemParchment("parchmentall",
				new int[] { ItemParchment.japaneseVillagers,
						ItemParchment.japaneseBuildings,
						ItemParchment.japaneseItems })
				.setUnlocalizedName("ml_parchmentJapaneseComplete");

		wineFancy = new ItemFoodMultiple("winefancy", 8, 30, 0, 0, true, 5)
				.setPotionEffect(Potion.resistance.id, 8 * 60, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_wine");
		silk = new ItemText("silk").setUnlocalizedName("ml_silk");
		byzantineiconsmall = new ItemTapestry("byzantineicon",
				EntityMillDecoration.BYZANTINE_ICON_SMALL)
				.setUnlocalizedName("ml_byzantineicon");
		byzantineiconmedium = new ItemTapestry("byzantineicon",
				EntityMillDecoration.BYZANTINE_ICON_MEDIUM)
				.setUnlocalizedName("ml_byzantineiconmedium");
		byzantineiconlarge = new ItemTapestry("byzantineicon",
				EntityMillDecoration.BYZANTINE_ICON_LARGE)
				.setUnlocalizedName("ml_byzantineiconlarge");

		byzantineLegs = new ItemMillenaireArmour("byzantinelegs",
				ARMOUR_byzantine, byzantineArmourId, 2)
				.setUnlocalizedName("ml_byzantineLegs");
		byzantineHelmet = new ItemMillenaireArmour("byzantinehelmet",
				ARMOUR_byzantine, byzantineArmourId, 0)
				.setUnlocalizedName("ml_byzantineHelmet");
		byzantinePlate = new ItemMillenaireArmour("byzantineplate",
				ARMOUR_byzantine, byzantineArmourId, 1)
				.setUnlocalizedName("ml_byzantinePlate");
		byzantineBoots = new ItemMillenaireArmour("byzantineboots",
				ARMOUR_byzantine, byzantineArmourId, 3)
				.setUnlocalizedName("ml_byzantineBoots");

		byzantineMace = new ItemMillenaireSword("byzantinemace",
				ToolMaterial.IRON, 0, 0, true)
				.setUnlocalizedName("ml_byzantineMace");

		clothes = (ItemClothes) new ItemClothes("byzantineclothwool",
				"byzantineclothsilk").setUnlocalizedName("ml_clothes");
		wineBasic = new ItemFoodMultiple("winebasic", 3, 15, 0, 0, true, 5)
				.setAlwaysEdible().setUnlocalizedName("ml_wine_basic");
		lambRaw = new ItemFoodMultiple("lambraw", 0, 0, 2, 0.2f, false, 0)
				.setUnlocalizedName("ml_lamb_raw");
		lambCooked = new ItemFoodMultiple("lambcooked", 0, 0, 6, 0.6f, false, 0)
				.setUnlocalizedName("ml_lamb_cooked");
		feta = new ItemFoodMultiple("feta", 2, 15, 0, 0, false, 0)
				.setUnlocalizedName("ml_feta");
		souvlaki = new ItemFoodMultiple("souvlaki", 0, 0, 10, 1f, false, 0)
				.setPotionEffect(Potion.heal.id, 1, 0, 1f).setAlwaysEdible()
				.setUnlocalizedName("ml_souvlaki");

		purse = (ItemPurse) new ItemPurse("purse").setMaxStackSize(1)
				.setUnlocalizedName("ml_purse");

		sake = new ItemFoodMultiple("sake", 8, 30, 0, 0, true, 10)
				.setPotionEffect(Potion.jump.id, 8 * 60, 1, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_sake");

		cacauhaa = new ItemFoodMultiple("cacauhaa", 6, 30, 0, 0, true, 0)
				.setPotionEffect(Potion.nightVision.id, 8 * 60, 0, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_cacauhaa");

		mayanQuestCrown = new ItemMayanQuestCrown("mayanquestcrown",
				mayanQuestArmourId, 0).setUnlocalizedName("ml_mayanQuestCrown");

		ikayaki = new ItemFoodMultiple("ikayaki", 0, 0, 10, 1f, false, 0)
				.setPotionEffect(Potion.waterBreathing.id, 8 * 60, 2, 1f)
				.setAlwaysEdible().setUnlocalizedName("ml_ikayaki");

		wood_decoration.setBlockName("ml_wood_deco").setHardness(2.0F)
				.setResistance(5F).setStepSound(Block.soundTypeWood);
		wood_decoration.registerTexture(0, "timberframeplain");
		wood_decoration.registerTexture(1, "timberframecross");
		wood_decoration.registerTexture(2, "thatch");
		wood_decoration.registerTexture(3, "silkwormempty");
		wood_decoration.registerTexture(4, "silkwormfull");

		earth_decoration.setBlockName("ml_earth_deco").setHardness(1.0F)
				.setResistance(2F).setStepSound(Block.soundTypeGravel);
		earth_decoration.registerTexture(0, "mudbrick");
		earth_decoration.registerTexture(1, "dirtwall");

		stone_decoration.setBlockName("ml_stone_deco").setHardness(1.0F)
				.setResistance(8F).setStepSound(Block.soundTypeStone);
		stone_decoration.registerTexture(0, "cookedbrick");
		stone_decoration.registerTexture(1, "mudbrickdried");
		stone_decoration.registerTexture(2, "mayangoldblock");
		stone_decoration.registerTexture(3, "alchemistexplosive");

		path.setBlockName("ml_path").setHardness(1.0F).setResistance(2F)
				.setStepSound(Block.soundTypeGravel);
		path.registerTexture(0, "pathdirt", "pathbottom", "pathdirt_side");
		path.registerTexture(1, "pathgravel", "pathbottom", "pathgravel_side");
		path.registerTexture(2, "pathslabs", "pathbottom", "pathslabs_side");
		path.registerTexture(3, "pathsandstone", "pathbottom",
				"pathsandstone_side");
		path.registerTexture(4, "pathochretiles", "pathbottom",
				"pathochretiles_side");
		path.registerTexture(5, "pathgravelslabs", "pathbottom",
				"pathgravel_side");

		pathSlab.setBlockName("ml_path_slab").setHardness(1.0F)
				.setResistance(2F).setStepSound(Block.soundTypeGravel);
		pathSlab.registerTexture(0, "pathdirt", "pathbottom",
				"pathdirt_halfside");
		pathSlab.registerTexture(1, "pathgravel", "pathbottom",
				"pathgravel_halfside");
		pathSlab.registerTexture(2, "pathslabs", "pathbottom",
				"pathslabs_halfside");
		pathSlab.registerTexture(3, "pathsandstone", "pathbottom",
				"pathsandstone_halfside");
		pathSlab.registerTexture(4, "pathochretiles", "pathbottom",
				"pathochretiles_halfside");
		pathSlab.registerTexture(5, "pathgravelslabs", "pathbottom",
				"pathgravel_halfside");

		entityNames = new HashMap<Class, String>();

		entityNames.put(MillVillager.MLEntityGenericMale.class,
				MillVillager.GENERIC_VILLAGER);
		entityNames.put(MillVillager.MLEntityGenericAsymmFemale.class,
				MillVillager.GENERIC_ASYMM_FEMALE);
		entityNames.put(MillVillager.MLEntityGenericSymmFemale.class,
				MillVillager.GENERIC_SYMM_FEMALE);
		entityNames.put(MillVillager.MLEntityGenericZombie.class,
				MillVillager.GENERIC_ZOMBIE);

		entityNames.put(EntityTargetedGhast.class, ENTITY_TARGETED_GHAST);
		entityNames.put(EntityTargetedBlaze.class, ENTITY_TARGETED_BLAZE);
		entityNames.put(EntityTargetedWitherSkeleton.class,
				ENTITY_TARGETED_WITHERSKELETON);

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void load(final FMLInitializationEvent evt) {

		if (startupError) {
			return;
		}

		LanguageRegistry.instance().addStringLocalization(
				"itemGroup.Millenaire", "en_US", name);

		if (MLN.stopDefaultVillages) {
			MapGenVillage.villageSpawnBiomes = Arrays
					.asList(new BiomeGenBase[] {});
		}

		boolean error = false;

		if (!error) {
			error = BuildingPlan.loadBuildingPoints();
		}

		if (!error) {
			Goods.loadGoods();
		}

		if (!error) {
			Goal.initGoals();
		}

		if (!error) {
			error = Culture.loadCultures();
		}

		if (!error) {
			Quest.loadQuests();
		}
		if (MLN.generateGoodsList) {
			Goods.generateGoodsList();
		}

		startupError = error;

		loadingComplete = true;

		if (MLN.LogOther >= MLN.MAJOR) {
			if (startupError) {
				MLN.major(this, "Mill\u00e9naire " + versionNumber
						+ " loaded unsuccessfully.");
			} else {
				MLN.major(this, "Mill\u00e9naire " + versionNumber
						+ " loaded successfully.");
			}
		}

		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
		FMLCommonHandler.instance().bus().register(this);

		millChannel = NetworkRegistry.INSTANCE
				.newEventDrivenChannel(ServerReceiver.PACKET_CHANNEL);
		millChannel.register(new ServerReceiver());
		millChannel.register(new ClientReceiver());

		proxy.registerForgeClientClasses();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance,
				proxy.createGuiHandler());
		GameRegistry.registerWorldGenerator(new WorldGenVillage(), 1000);

		MinecraftForge.EVENT_BUS.register(new MillEventController());

		ForgeChunkManager.setForcedChunkLoadingCallback(this,
				new ChunkLoaderCallback());

		MLN.checkBonusCode(false);

		proxy.loadLanguages();

		for (final Object o : Block.blockRegistry) {
			final Block b = (Block) o;

			if (Item.getItemFromBlock(b) == null) {
				MLN.major(null, "Block without item: " + b);
			}

		}

	}

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		MLN.loadConfig();

		initBlockItems();

		AchievementPage
				.registerAchievementPage(MillAchievements.millAchievements);

		registerBlocksItemsEntities();

		Mill.proxy.refreshClientResources();
	}

	private void registerBlocksItemsEntities() {
		try {
			EntityRegistry.registerGlobalEntityID(
					MillVillager.MLEntityGenericAsymmFemale.class,
					MillVillager.GENERIC_ASYMM_FEMALE,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(
					MillVillager.MLEntityGenericAsymmFemale.class,
					MillVillager.GENERIC_ASYMM_FEMALE, VILLAGER_ENT_ID + 1,
					instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(
					MillVillager.MLEntityGenericSymmFemale.class,
					MillVillager.GENERIC_SYMM_FEMALE,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(
					MillVillager.MLEntityGenericSymmFemale.class,
					MillVillager.GENERIC_SYMM_FEMALE, VILLAGER_ENT_ID + 2,
					instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(
					MillVillager.MLEntityGenericMale.class,
					MillVillager.GENERIC_VILLAGER,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(
					MillVillager.MLEntityGenericMale.class,
					MillVillager.GENERIC_VILLAGER, VILLAGER_ENT_ID, instance,
					64, 3, true);

			EntityRegistry.registerGlobalEntityID(EntityMillDecoration.class,
					"ml_Tapestry", EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityMillDecoration.class,
					"ml_Tapestry", VILLAGER_ENT_ID + 7, instance, 80, 100000,
					false);

			EntityRegistry.registerGlobalEntityID(EntityTargetedGhast.class,
					ENTITY_TARGETED_GHAST,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityTargetedGhast.class,
					ENTITY_TARGETED_GHAST, VILLAGER_ENT_ID + 8, instance, 64,
					3, true);

			EntityRegistry.registerGlobalEntityID(EntityTargetedBlaze.class,
					ENTITY_TARGETED_BLAZE,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityTargetedBlaze.class,
					ENTITY_TARGETED_BLAZE, VILLAGER_ENT_ID + 9, instance, 64,
					3, true);

			EntityRegistry.registerGlobalEntityID(
					EntityTargetedWitherSkeleton.class,
					ENTITY_TARGETED_WITHERSKELETON,
					EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(
					EntityTargetedWitherSkeleton.class,
					ENTITY_TARGETED_WITHERSKELETON, VILLAGER_ENT_ID + 10,
					instance, 64, 3, true);

			GameRegistry.registerBlock(lockedChest,
					lockedChest.getUnlocalizedName());
			GameRegistry.registerBlock(panel, panel.getUnlocalizedName());
			GameRegistry.registerBlock(wood_decoration, ItemDecorative.class,
					wood_decoration.getUnlocalizedName());
			GameRegistry.registerBlock(earth_decoration, ItemDecorative.class,
					earth_decoration.getUnlocalizedName());
			GameRegistry.registerBlock(stone_decoration, ItemDecorative.class,
					stone_decoration.getUnlocalizedName());
			GameRegistry.registerBlock(path, ItemDecorativeSlab.class,
					path.getUnlocalizedName());
			GameRegistry.registerBlock(pathSlab, ItemDecorativeSlab.class,
					pathSlab.getUnlocalizedName());

			GameRegistry.registerBlock(cropRice, cropRice.getUnlocalizedName());
			GameRegistry.registerBlock(cropTurmeric,
					cropTurmeric.getUnlocalizedName());
			GameRegistry.registerBlock(cropMaize,
					cropMaize.getUnlocalizedName());
			GameRegistry.registerBlock(cropVine, cropVine.getUnlocalizedName());

			// GameRegistry.registerItem(new ItemDecorativeSlab(path, pathSlab,
			// path, true), "ml_path");
			// GameRegistry.registerItem(new ItemDecorativeSlab(pathSlab,
			// pathSlab, path, false), "ml_path_slab");
			// Item.itemRegistry.addObject(Block.getIdFromBlock(path),
			// "ml_path", new ItemDecorativeSlab(path, pathSlab, path, true));
			// Item.itemRegistry.addObject(Block.getIdFromBlock(pathSlab),
			// "ml_path_slab", new ItemDecorativeSlab(pathSlab, pathSlab, path,
			// false));

			GameRegistry.registerBlock(byzantine_tiles,
					byzantine_tiles.getUnlocalizedName());
			GameRegistry.registerBlock(byzantine_tile_slab,
					byzantine_tile_slab.getUnlocalizedName());
			GameRegistry.registerBlock(byzantine_stone_tiles,
					byzantine_stone_tiles.getUnlocalizedName());

			GameRegistry.addSmelting(lambRaw, new ItemStack(lambCooked, 1),
					0.3f);

			FurnaceRecipes.smelting().func_151394_a(
					new ItemStack(stone_decoration, 1, 1),
					new ItemStack(stone_decoration, 1, 0), 0.3f);

			GameRegistry.addShapelessRecipe(new ItemStack(vegcurry, 1),
					new Object[] { rice, turmeric });
			GameRegistry.addShapelessRecipe(new ItemStack(chickencurry, 1),
					new Object[] { rice, turmeric, Items.chicken });

			GameRegistry.addShapelessRecipe(new ItemStack(wineBasic, 1),
					new Object[] { grapes, grapes, grapes, grapes, grapes,
							grapes });

			GameRegistry.addRecipe(new ItemStack(masa, 1), new Object[] {
					"###", Character.valueOf('#'), maize });
			GameRegistry.addRecipe(new ItemStack(wah, 1), new Object[] { "#X#",
					Character.valueOf('#'), maize, Character.valueOf('X'),
					Items.chicken });

			GameRegistry.addRecipe(new ItemStack(byzantine_tile_slab, 6),
					new Object[] { "###", Character.valueOf('#'),
							byzantine_tiles });
			GameRegistry.addRecipe(new ItemStack(byzantine_stone_tiles, 6),
					new Object[] { "###", "SSS", Character.valueOf('#'),
							byzantine_tiles, Character.valueOf('S'),
							Blocks.stone });

			for (int meta = 0; meta < 16; meta++) {
				GameRegistry.addRecipe(new ItemStack(Mill.pathSlab, 3, meta),
						"xxx", 'x', new ItemStack(Mill.path, 1, meta));
			}

			GameRegistry.registerBlock(paperWall,
					paperWall.getUnlocalizedName());

			final Field[] fields = Mill.class.getFields();

			for (final Field f : fields) {
				if (f.getType().isAssignableFrom(Item.class)) {
					final Item item = (Item) f.get(this);
					GameRegistry.registerItem(item, item.getUnlocalizedName());
				}
			}
			GameRegistry.registerItem(clothes, clothes.getUnlocalizedName());
			GameRegistry.registerItem(purse, purse.getUnlocalizedName());

			proxy.registerTileEntities();
			proxy.registerGraphics();
			proxy.preloadTextures();

		} catch (final Exception e) {
			MLN.error(this, "Exception in mod initialisation: ");
			MLN.printException(e);
		}
	}

	@EventHandler
	public void serverLoad(final FMLServerStartingEvent event) {

	}
}
