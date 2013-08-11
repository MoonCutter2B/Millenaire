package org.millenaire.common.forge;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

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
import org.millenaire.common.construction.BuildingPlan;
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
import org.millenaire.common.network.ConnectionHandler;
import org.millenaire.common.network.ServerReceiver;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = Mill.modId, name = Mill.name, version = Mill.versionNumber)
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = {ServerReceiver.PACKET_CHANNEL}, packetHandler = ClientReceiver.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = {ServerReceiver.PACKET_CHANNEL}, packetHandler = ServerReceiver.class),
connectionHandler = ConnectionHandler.class,
versionBounds = Mill.versionBound)
public class Mill
{
	static class CreativeTabMill extends CreativeTabs {

		public CreativeTabMill(String par2Str) {
			super(par2Str);
		}
		@Override
		@SideOnly(Side.CLIENT)

		/**
		 * the itemID for the item to be displayed on the tab
		 */
		public int getTabIconItemIndex()
		{
			return Mill.denier_or.itemID;
		} 
	} 


	public static final String versionNumber = "5.1.8";

	public static final String versionBound = "[5.0.0,6.0)";
	public static final String modId="millenaire";
	public static final String name = "Mill\u00e9naire"; 
  
	public static final String version = name+" "+versionNumber;

	public static final CreativeTabs tabMillenaire = new CreativeTabMill("Millenaire");

	@SidedProxy(clientSide = "org.millenaire.client.forge.ClientProxy", serverSide = "org.millenaire.common.forge.CommonProxy")
	public static CommonProxy proxy;

	@Instance
	public static Mill instance;

	public static final int VILLAGER_ENT_ID = 1;

	public static Vector<MillWorld> serverWorlds=new Vector<MillWorld>();
	public static MillWorld clientWorld=null;

	public static Vector<File> loadingDirs=new Vector<File>();
	private static int nextItemId=MLN.itemRangeStart-1;
	
	static EnumToolMaterial TOOLS_norman = EnumHelper.addToolMaterial("normanTools", 2, 1561, 10.0F, 4.0F, 10);
	static EnumToolMaterial TOOLS_obsidian = EnumHelper.addToolMaterial("obsidianTools", 3, 1561, 6.0F, 2.0F, 25);
	
	static EnumArmorMaterial ARMOUR_norman= EnumHelper.addArmorMaterial("normanArmour", 66, new int[]{3, 8, 6, 3}, 10);
	static EnumArmorMaterial ARMOUR_japaneseWarrior= EnumHelper.addArmorMaterial("japaneseWarrior", 33, new int[]{2, 6, 5, 2}, 25);
	static EnumArmorMaterial ARMOUR_japaneseGuard= EnumHelper.addArmorMaterial("japaneseGuard", 25, new int[]{2, 5, 4, 1}, 25);
	static EnumArmorMaterial ARMOUR_byzantine= EnumHelper.addArmorMaterial("byzantineArmour", 33, new int[]{3, 8, 6, 3}, 20);
	

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

	public static BlockMillCrops crops;

	public static Block paperWall;
	public static int normanArmourId=0;
	public static int japaneseWarriorBlueArmourId=0;
	public static int japaneseWarriorRedArmourId=0;
	public static int japaneseGuardArmourId=0;
	public static int byzantineArmourId=0;
	public static int mayanQuestArmourId=0;
	

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

	public static Item wineBasic,lambRaw,lambCooked,feta,souvlaki;

	public static ItemPurse purse;
	
	public static Item sake,cacauhaa;
	
	public static Item mayanQuestCrown;

	public static Item ikayaki;
	
	public static boolean loadingComplete=false;

	public static boolean startMessageDisplayed=false;

	@SuppressWarnings("rawtypes")
	public static HashMap<Class,String> entityNames;

	public static final String ENTITY_PIG="Pig",ENTITY_COW="Cow",ENTITY_CHICKEN="Chicken",ENTITY_SHEEP="Sheep",ENTITY_SQUID="Squid";

	public static final String ENTITY_SKELETON="Skeleton",ENTITY_CREEPER="Creeper",ENTITY_SPIDER="Spider",
			ENTITY_CAVESPIDER="CaveSpider",ENTITY_ZOMBIE="Zombie",ENTITY_TARGETED_GHAST="MillGhast",ENTITY_TARGETED_BLAZE="MillBlaze",ENTITY_TARGETED_WITHERSKELETON="MillWitherSkeleton";

	public static final String CROP_WHEAT="wheat",CROP_CARROT="carrot",CROP_POTATO="potato",
			CROP_RICE="rice",CROP_TURMERIC="turmeric",CROP_MAIZE="maize",CROP_VINE="vine",CROP_CACAO="cacao";

	public static boolean startupError=false;

	public static boolean checkedMillenaireDir=false;

	public static boolean displayMillenaireLocationError=false;


	public static MillWorld getMillWorld(World world) {

		if ((clientWorld != null) && (clientWorld.world==world))
			return clientWorld;

		for (final MillWorld mw : serverWorlds) {
			if (mw.world==world)
				return mw;
		}

		if ((serverWorlds!=null) && (serverWorlds.size()>0))

			return serverWorlds.firstElement();

		return null;
	}

	public static boolean isDistantClient() {
		if ((clientWorld!=null) && serverWorlds.isEmpty())
			return true;
		return false;
	}


	public static boolean isRunningDeobf() {
		return (EntityCreature.class.getSimpleName().equals("EntityCreature"));
	}
	private static int nextItemId() {
		nextItemId++;
		return nextItemId;
	}


	@SuppressWarnings("rawtypes")
	protected void initBlockItems() {

		nextItemId=MLN.itemRangeStart-1;

		lockedChest = (new BlockMillChest(MLN.blockBuildingId).setUnlocalizedName("ml_building").setHardness(10F).setResistance(2000F)).setStepSound(Block.soundWoodFootstep);

		panel = (new BlockPanel(MLN.blockPanelId,TileEntityPanel.class,false).setUnlocalizedName("ml_panel").setHardness(10F).setResistance(2000F)).setStepSound(Block.soundWoodFootstep);;
		wood_decoration = new BlockDecorative(MLN.blockWoodId,Material.wood);
		earth_decoration = new BlockDecorative(MLN.blockEarthId,Material.ground);
		stone_decoration = new BlockDecorative(MLN.blockStoneId,Material.rock);
		path = new BlockDecorativeSlab(MLN.blockPathId,Material.ground,true);
		pathSlab = new BlockDecorativeSlab(MLN.blockPathSlabId,Material.ground,false);

		crops = new BlockMillCrops(MLN.blockCropsId);

		paperWall = new BlockMLNPane(MLN.blockPanesId, "paperwall", "paperwall", Material.cloth, true).setHardness(0.3F).setStepSound(Block.soundClothFootstep).setUnlocalizedName("ml_panes");

		byzantine_tiles = (BlockOrientedBrick) new BlockOrientedBrick(MLN.blockByzantineBrickId,
				"tilestopvert","tilestophor","tilestopvert","tilestophor","tilesfront","tilestophor").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_brick");
		byzantine_tile_slab = (BlockOrientedSlab) new BlockOrientedSlab(MLN.blockByzantineSlabId,"tilestophor","tilestopvert","tilesfront").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_brick_slab");
		byzantine_stone_tiles = (BlockOrientedBrick) new BlockOrientedBrick(MLN.blockByzantineMixedId,
				"tilestopvert","tilestophor","stone","stone","tileshalffront","tileshalfside").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_mixedbrick");

		
		MinecraftForge.setBlockHarvestLevel(lockedChest, "axe", 0);
		MinecraftForge.setBlockHarvestLevel(wood_decoration, "axe", 0);
		MinecraftForge.setBlockHarvestLevel(paperWall, "axe", 0);
		MinecraftForge.setBlockHarvestLevel(panel, "axe", 0);
		MinecraftForge.setBlockHarvestLevel(stone_decoration, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(byzantine_tiles, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(byzantine_tile_slab, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(byzantine_stone_tiles, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(earth_decoration, "shovel", 0);
		MinecraftForge.setBlockHarvestLevel(path, "shovel", 0);
		MinecraftForge.setBlockHarvestLevel(pathSlab, "shovel", 0);
		
		
		proxy.setTextureIds();

		denier = (new ItemText(nextItemId(),"denier")).setUnlocalizedName("ml_denier");
		denier_or = (new ItemText(nextItemId(),"denier_or")).setUnlocalizedName("ml_denier_or");
		denier_argent = (new ItemText(nextItemId(),"denier_argent")).setUnlocalizedName("ml_denier_argent");
		ciderapple = (new ItemFoodMultiple(nextItemId(),"ciderapple",0,0,1,0.05f,false,0)).setUnlocalizedName("ml_ciderapple").setMaxStackSize(64);;
		cider = (new ItemFoodMultiple(nextItemId(),"cider",4,15,0,0,true,5)).setAlwaysEdible().setUnlocalizedName("ml_cider");
		calva = (new ItemFoodMultiple(nextItemId(),"calva",8,30,0,0,true,10)).setPotionEffect(Potion.damageBoost.id, 180, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_calva");
		tripes = (new ItemFoodMultiple(nextItemId(),"tripes",0,0,10,1f,false,0)).setPotionEffect(Potion.regeneration.id, 90, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_tripes");

		normanPickaxe =  new ItemMillenairePickaxe(nextItemId(),"normanpickaxe",TOOLS_norman).setUnlocalizedName("ml_normanPickaxe");
		MinecraftForge.setToolClass(normanPickaxe, "pickaxe", 2);
		normanAxe =  new ItemMillenaireAxe(nextItemId(),"normanaxe",TOOLS_norman).setUnlocalizedName("ml_normanAxe");
		MinecraftForge.setToolClass(normanAxe, "axe", 2);
		normanShovel =  new ItemMillenaireShovel(nextItemId(),"normanshovel",TOOLS_norman).setUnlocalizedName("ml_normanShovel");
		MinecraftForge.setToolClass(normanShovel, "shovel", 2);
		normanHoe =  new ItemMillenaireHoe(nextItemId(),"normanhoe",TOOLS_norman).setUnlocalizedName("ml_normanHoe");

		summoningWand = new ItemSummoningWand(nextItemId(),"summoningwand").setFull3D().setUnlocalizedName("ml_villageWand");

		normanBroadsword = new ItemMillenaireSword(nextItemId(),"normansword",TOOLS_norman,0,0,false).setUnlocalizedName("ml_normanBroadsword");
		normanHelmet = new ItemMillenaireArmour(nextItemId(),"normanhelmet",ARMOUR_norman,normanArmourId,0).setUnlocalizedName("ml_normanHelmet");
		normanPlate = new ItemMillenaireArmour(nextItemId(),"normanplate",ARMOUR_norman,normanArmourId,1).setUnlocalizedName("ml_normanPlate");
		normanLegs = new ItemMillenaireArmour(nextItemId(),"normanlegs",ARMOUR_norman,normanArmourId,2).setUnlocalizedName("ml_normanLegs");
		normanBoots = new ItemMillenaireArmour(nextItemId(),"normanboots",ARMOUR_norman,normanArmourId,3).setUnlocalizedName("ml_normanBoots");

		parchmentVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.villagers).setUnlocalizedName("ml_parchmentVillagers");

		parchmentBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.buildings).setUnlocalizedName("ml_parchmentBuildings");

		parchmentItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.items).setUnlocalizedName("ml_parchmentItems");


		parchmentComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.villagers,ItemParchment.buildings,ItemParchment.items}).setUnlocalizedName("ml_marchmentComplete");

		boudin = (new ItemFoodMultiple(nextItemId(),"boudin",0,0,10,1f,false,0)).setUnlocalizedName("ml_boudin");

		tapestry = (new ItemTapestry(nextItemId(),"normantapestry",EntityMillDecoration.NORMAN_TAPESTRY)).setUnlocalizedName("ml_tapestry");



		vishnu_amulet = new ItemAmuletVishnu(nextItemId(),"amulet_vishnu").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_raven_amulet").setMaxStackSize(1);
		alchemist_amulet = new ItemAmuletAlchemist(nextItemId(),"amulet_alchemist").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_dwarves_amulet").setMaxStackSize(1);
		yddrasil_amulet = new ItemAmuletYddrasil(nextItemId(),"amulet_yggdrasil").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_yddrasil_amulet").setMaxStackSize(1);

		skoll_hati_amulet = (new ItemAmuletSkollHati(nextItemId(),"amulet_skollhati")).setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_skoll_hati_amulet").setMaxStackSize(1).setMaxDamage(10);
		parchmentVillageScroll = new ItemParchment(nextItemId(),"parchmentvillage",new int[]{ItemParchment.villageBook}).setUnlocalizedName("ml_parchmentVillageScroll");

		rice = (new ItemMillSeeds(nextItemId(),"rice", crops.blockID,0,Mill.CROP_RICE)).setUnlocalizedName("ml_rice");
		turmeric = (new ItemMillSeeds(nextItemId(),"turmeric", crops.blockID,2,Mill.CROP_TURMERIC)).setUnlocalizedName("ml_turmeric");
		vegcurry = (new ItemFoodMultiple(nextItemId(),"curry",0,0,6,0.6f,false,0)).setUnlocalizedName("ml_vegcurry");
		chickencurry = (new ItemFoodMultiple(nextItemId(),"currychicken",0,0,8,0.8f,false,0)).setPotionEffect(Potion.fireResistance.id, 8*60, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_chickencurry");
		brickmould = (new ItemBrickMould(nextItemId(),"brickmould")).setUnlocalizedName("ml_brickmould").setMaxStackSize(1).setMaxDamage(128);
		rasgulla = (new ItemFoodMultiple(nextItemId(),"rasgulla",2,30,0,0,false,0)).setPotionEffect(Potion.moveSpeed.id, 8*60, 1, 1f).setAlwaysEdible().setUnlocalizedName("ml_rasgullaId");
		indianstatue = (new ItemTapestry(nextItemId(),"indianstatue",EntityMillDecoration.INDIAN_STATUE)).setUnlocalizedName("ml_indianstatue");

		parchmentIndianVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.indianVillagers).setUnlocalizedName("ml_parchmentIndianVillagers");
		parchmentIndianBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.indianBuildings).setUnlocalizedName("ml_parchmentIndianBuildings");
		parchmentIndianItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.indianItems).setUnlocalizedName("ml_parchmentIndianItems");
		parchmentIndianComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.indianVillagers,ItemParchment.indianBuildings,ItemParchment.indianItems}).setUnlocalizedName("ml_marchmentIndianComplete");


		mayanstatue = (new ItemTapestry(nextItemId(),"mayanstatue",EntityMillDecoration.MAYAN_STATUE)).setUnlocalizedName("ml_mayanstatue");
		maize = (new ItemMillSeeds(nextItemId(),"maize", crops.blockID,4,Mill.CROP_MAIZE)).setUnlocalizedName("ml_maize");
		masa = (new ItemFoodMultiple(nextItemId(),"masa",0,0,6,0.6f,false,0)).setUnlocalizedName("ml_masa");
		wah = (new ItemFoodMultiple(nextItemId(),"wah",0,0,10,1f,false,0)).setPotionEffect(Potion.digSpeed.id, 8*60, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_wah");

		parchmentMayanVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.mayanVillagers).setUnlocalizedName("ml_parchmentMayanVillagers");
		parchmentMayanBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.mayanBuildings).setUnlocalizedName("ml_parchmentMayanBuildings");
		parchmentMayanItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.mayanItems).setUnlocalizedName("ml_parchmentMayanItems");
		parchmentMayanComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.mayanVillagers,ItemParchment.mayanBuildings,ItemParchment.mayanItems}).setUnlocalizedName("ml_parchmentMayanComplete");

		parchmentSadhu = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.sadhu}).setUnlocalizedName("ml_parchmentSadhu");

		unknownPowder =  new ItemText(nextItemId(),"unknownpowder").setUnlocalizedName("ml_unknownPowder").setCreativeTab(Mill.tabMillenaire);

		udon = (new ItemFoodMultiple(nextItemId(),"udon",0,0,8,0.8f,false,0)).setAlwaysEdible().setUnlocalizedName("ml_udon");

		tachiSword = new ItemMillenaireSword(nextItemId(),"tachisword",EnumToolMaterial.IRON,(float) 0.2,5,false).setUnlocalizedName("ml_taichiSword");

		negationWand = new ItemNegationWand(nextItemId(),"negationwand").setFull3D().setUnlocalizedName("ml_negationWand");

		obsidianFlake = new ItemText(nextItemId(),"obsidianflake").setUnlocalizedName("ml_obsidianFlake");
		mayanMace =  new ItemMillenaireSword(nextItemId(),"mayanmace",TOOLS_obsidian,0,0,false).setUnlocalizedName("ml_mayanMace");
		mayanPickaxe =  new ItemMillenairePickaxe(nextItemId(),"mayanpickaxe",TOOLS_obsidian).setUnlocalizedName("ml_mayanPickaxe");
		MinecraftForge.setToolClass(mayanAxe, "pickaxe", 2);
		mayanAxe =  new ItemMillenaireAxe(nextItemId(),"mayanaxe",TOOLS_obsidian).setUnlocalizedName("ml_mayanAxe");
		MinecraftForge.setToolClass(mayanAxe, "axe", 2);
		mayanShovel =  new ItemMillenaireShovel(nextItemId(),"mayanshovel",TOOLS_obsidian).setUnlocalizedName("ml_mayanShovel");
		MinecraftForge.setToolClass(mayanShovel, "shovel", 2);
		mayanHoe =  new ItemMillenaireHoe(nextItemId(),"mayanhoe",TOOLS_obsidian).setUnlocalizedName("ml_mayanHoe");

		yumiBow =  new ItemMillenaireBow(nextItemId(),2,(float) 0.5,"yumibow0","yumibow1","yumibow2","yumibow3").setUnlocalizedName("ml_yumiBow").setFull3D();


		japaneseWarriorBlueLegs = new ItemMillenaireArmour(nextItemId(),"japanesebluelegs",ARMOUR_japaneseWarrior,japaneseWarriorBlueArmourId,2).setUnlocalizedName("ml_japaneseWarriorBlueLegs");
		japaneseWarriorBlueHelmet = new ItemMillenaireArmour(nextItemId(),"japanesebluehelmet",ARMOUR_japaneseWarrior,japaneseWarriorBlueArmourId,0).setUnlocalizedName("ml_japaneseWarriorBlueHelmet");
		japaneseWarriorBluePlate = new ItemMillenaireArmour(nextItemId(),"japaneseblueplate",ARMOUR_japaneseWarrior,japaneseWarriorBlueArmourId,1).setUnlocalizedName("ml_japaneseWarriorBluePlate");
		japaneseWarriorBlueBoots = new ItemMillenaireArmour(nextItemId(),"japaneseblueboots",ARMOUR_japaneseWarrior,japaneseWarriorBlueArmourId,3).setUnlocalizedName("ml_japaneseWarriorBlueBoots");

		japaneseWarriorRedLegs = new ItemMillenaireArmour(nextItemId(),"japaneseredlegs",ARMOUR_japaneseWarrior,japaneseWarriorRedArmourId,2).setUnlocalizedName("ml_japaneseWarriorRedLegs");
		japaneseWarriorRedHelmet = new ItemMillenaireArmour(nextItemId(),"japaneseredhelmet",ARMOUR_japaneseWarrior,japaneseWarriorRedArmourId,0).setUnlocalizedName("ml_japaneseWarriorRedHelmet");
		japaneseWarriorRedPlate = new ItemMillenaireArmour(nextItemId(),"japaneseredplate",ARMOUR_japaneseWarrior,japaneseWarriorRedArmourId,1).setUnlocalizedName("ml_japaneseWarriorRedPlate");
		japaneseWarriorRedBoots = new ItemMillenaireArmour(nextItemId(),"japaneseredboots",ARMOUR_japaneseWarrior,japaneseWarriorRedArmourId,3).setUnlocalizedName("ml_japaneseWarriorRedBoots");

		japaneseGuardLegs = new ItemMillenaireArmour(nextItemId(),"japaneseguardlegs",ARMOUR_japaneseGuard,japaneseGuardArmourId,2).setUnlocalizedName("ml_japaneseGuardLegs");
		japaneseGuardHelmet = new ItemMillenaireArmour(nextItemId(),"japaneseguardhelmet",ARMOUR_japaneseGuard,japaneseGuardArmourId,0).setUnlocalizedName("ml_japaneseGuardHelmet");
		japaneseGuardPlate = new ItemMillenaireArmour(nextItemId(),"japaneseguardplate",ARMOUR_japaneseGuard,japaneseGuardArmourId,1).setUnlocalizedName("ml_japaneseGuardPlate");
		japaneseGuardBoots = new ItemMillenaireArmour(nextItemId(),"japaneseguardboots",ARMOUR_japaneseGuard,japaneseGuardArmourId,3).setUnlocalizedName("ml_japaneseGuardBoots");

		parchmentJapaneseVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.japaneseVillagers).setUnlocalizedName("ml_parchmentJapaneseVillagers");
		parchmentJapaneseBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.japaneseBuildings).setUnlocalizedName("ml_parchmentJapaneseBuildings");
		parchmentJapaneseItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.japaneseItems).setUnlocalizedName("ml_parchmentJapaneseItems");
		parchmentJapaneseComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.japaneseVillagers,ItemParchment.japaneseBuildings,ItemParchment.japaneseItems}).setUnlocalizedName("ml_parchmentJapaneseComplete");


		grapes=(new ItemMillSeeds(nextItemId(),"grapes", crops.blockID,6,Mill.CROP_VINE)).setUnlocalizedName("ml_vine");
		wineFancy = (new ItemFoodMultiple(nextItemId(),"winefancy",8,30,0,0,true,5)).setPotionEffect(Potion.resistance.id, 8*60, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_wine");
		silk=new ItemText(nextItemId(),"silk").setUnlocalizedName("ml_silk");
		byzantineiconsmall = (new ItemTapestry(nextItemId(),"byzantineicon",EntityMillDecoration.BYZANTINE_ICON_SMALL)).setUnlocalizedName("ml_byzantineicon");
		byzantineiconmedium = (new ItemTapestry(nextItemId(),"byzantineicon",EntityMillDecoration.BYZANTINE_ICON_MEDIUM)).setUnlocalizedName("ml_byzantineiconmedium");
		byzantineiconlarge = (new ItemTapestry(nextItemId(),"byzantineicon",EntityMillDecoration.BYZANTINE_ICON_LARGE)).setUnlocalizedName("ml_byzantineiconlarge");


		byzantineLegs = new ItemMillenaireArmour(nextItemId(),"byzantinelegs",ARMOUR_byzantine,byzantineArmourId,2).setUnlocalizedName("ml_byzantineLegs");
		byzantineHelmet = new ItemMillenaireArmour(nextItemId(),"byzantinehelmet",ARMOUR_byzantine,byzantineArmourId,0).setUnlocalizedName("ml_byzantineHelmet");
		byzantinePlate = new ItemMillenaireArmour(nextItemId(),"byzantineplate",ARMOUR_byzantine,byzantineArmourId,1).setUnlocalizedName("ml_byzantinePlate");
		byzantineBoots = new ItemMillenaireArmour(nextItemId(),"byzantineboots",ARMOUR_byzantine,byzantineArmourId,3).setUnlocalizedName("ml_byzantineBoots");

		byzantineMace =  new ItemMillenaireSword(nextItemId(),"byzantinemace",EnumToolMaterial.IRON,0,0,true).setUnlocalizedName("ml_byzantineMace");

		clothes = (ItemClothes) new ItemClothes(nextItemId(),"byzantineclothwool","byzantineclothsilk").setUnlocalizedName("ml_clothes");
		wineBasic = (new ItemFoodMultiple(nextItemId(),"winebasic",3,15,0,0,true,5)).setAlwaysEdible().setUnlocalizedName("ml_wine_basic");
		lambRaw = (new ItemFoodMultiple(nextItemId(),"lambraw",0,0,2,0.2f,false,0)).setUnlocalizedName("ml_lamb_raw");
		lambCooked = (new ItemFoodMultiple(nextItemId(),"lambcooked",0,0,6,0.6f,false,0)).setUnlocalizedName("ml_lamb_cooked");
		feta = (new ItemFoodMultiple(nextItemId(),"feta",2,15,0,0,false,0)).setUnlocalizedName("ml_feta");
		souvlaki = (new ItemFoodMultiple(nextItemId(),"souvlaki",0,0,10,1f,false,0)).setPotionEffect(Potion.heal.id, 1, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_souvlaki");

		purse = (ItemPurse) new ItemPurse(nextItemId(),"purse").setMaxStackSize(1).setUnlocalizedName("ml_purse");
		
		sake = (new ItemFoodMultiple(nextItemId(),"sake",8,30,0,0,true,10)).setPotionEffect(Potion.jump.id, 8*60, 1, 1f).setAlwaysEdible().setUnlocalizedName("ml_sake");
		
		cacauhaa = (new ItemFoodMultiple(nextItemId(),"cacauhaa",6,30,0,0,true,0)).setPotionEffect(Potion.nightVision.id, 8*60, 0, 1f).setAlwaysEdible().setUnlocalizedName("ml_cacauhaa");

		mayanQuestCrown = new ItemMayanQuestCrown(nextItemId(),"mayanquestcrown",mayanQuestArmourId,0).setUnlocalizedName("ml_mayanQuestCrown");
		
		ikayaki = (new ItemFoodMultiple(nextItemId(),"ikayaki",0,0,10,1f,false,0)).setPotionEffect(Potion.waterBreathing.id, 8*60, 2, 1f).setAlwaysEdible().setUnlocalizedName("ml_ikayaki");

		
		wood_decoration.setUnlocalizedName("ml_wood_deco").setHardness(2.0F).setResistance(5F).setStepSound(Block.soundWoodFootstep);
		wood_decoration.registerTexture(0, "timberframeplain");
		wood_decoration.registerTexture(1, "timberframecross");
		wood_decoration.registerTexture(2, "thatch");
		wood_decoration.registerTexture(3, "silkwormempty");
		wood_decoration.registerTexture(4, "silkwormfull");

		earth_decoration.setUnlocalizedName("ml_earth_deco").setHardness(1.0F).setResistance(2F).setStepSound(Block.soundGravelFootstep);
		earth_decoration.registerTexture(0, "mudbrick");
		earth_decoration.registerTexture(1, "dirtwall");

		stone_decoration.setUnlocalizedName("ml_stone_deco").setHardness(1.0F).setResistance(8F).setStepSound(Block.soundStoneFootstep);
		stone_decoration.registerTexture(0, "cookedbrick");
		stone_decoration.registerTexture(1, "mudbrickdried");
		stone_decoration.registerTexture(2, "mayangoldblock");
		stone_decoration.registerTexture(3, "alchemistexplosive");

		path.setUnlocalizedName("ml_path").setHardness(1.0F).setResistance(2F).setStepSound(Block.soundGravelFootstep);
		path.registerTexture(0, "pathdirt","pathbottom","pathdirt_side");
		path.registerTexture(1, "pathgravel","pathbottom","pathgravel_side");
		path.registerTexture(2, "pathslabs","pathbottom","pathslabs_side");
		path.registerTexture(3, "pathsandstone","pathbottom","pathsandstone_side");
		path.registerTexture(4, "pathochretiles","pathbottom","pathochretiles_side");
		path.registerTexture(5, "pathgravelslabs","pathbottom","pathgravel_side");
		
		pathSlab.setUnlocalizedName("ml_path_slab").setHardness(1.0F).setResistance(2F).setStepSound(Block.soundGravelFootstep);
		pathSlab.registerTexture(0, "pathdirt","pathbottom","pathdirt_halfside");
		pathSlab.registerTexture(1, "pathgravel","pathbottom","pathgravel_halfside");
		pathSlab.registerTexture(2, "pathslabs","pathbottom","pathslabs_halfside");
		pathSlab.registerTexture(3, "pathsandstone","pathbottom","pathsandstone_halfside");
		pathSlab.registerTexture(4, "pathochretiles","pathbottom","pathochretiles_halfside");
		pathSlab.registerTexture(5, "pathgravelslabs","pathbottom","pathgravel_halfside");
		

		crops.setUnlocalizedName("ml_crops").setHardness(0.0F).setStepSound(Block.soundGrassFootstep);

		entityNames=new HashMap<Class,String>();

		entityNames.put(MillVillager.MLEntityGenericMale.class, MillVillager.GENERIC_VILLAGER);
		entityNames.put(MillVillager.MLEntityGenericAsymmFemale.class, MillVillager.GENERIC_ASYMM_FEMALE);
		entityNames.put(MillVillager.MLEntityGenericSymmFemale.class, MillVillager.GENERIC_SYMM_FEMALE);
		entityNames.put(MillVillager.MLEntityGenericZombie.class, MillVillager.GENERIC_ZOMBIE);
		
		entityNames.put(EntityTargetedGhast.class, ENTITY_TARGETED_GHAST);
		entityNames.put(EntityTargetedBlaze.class, ENTITY_TARGETED_BLAZE);
		entityNames.put(EntityTargetedWitherSkeleton.class, ENTITY_TARGETED_WITHERSKELETON);

		try {
			final int chanceToEncourageFire[]=(int[]) ModLoader.getPrivateValue(BlockFire.class, Block.fire, 0);
			final int abilityToCatchFire[]=(int[]) ModLoader.getPrivateValue(BlockFire.class, Block.fire, 1);

			chanceToEncourageFire[wood_decoration.blockID]=3;
			abilityToCatchFire[wood_decoration.blockID]=10;

			chanceToEncourageFire[crops.blockID]=60;
			abilityToCatchFire[crops.blockID]=100;

			chanceToEncourageFire[paperWall.blockID]=30;
			abilityToCatchFire[paperWall.blockID]=60;


		} catch (final Exception e) {
			MLN.printException("Could not access BlockFire's fields: ",e);
		}
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{		
		MLN.loadConfig();

		initBlockItems();

		AchievementPage.registerAchievementPage(MillAchievements.millAchievements);
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		
		if (startupError)
			return;



		LanguageRegistry.instance().addStringLocalization("itemGroup.Millenaire", "en_US", name);

		if (MLN.stopDefaultVillages) {
			//MapGenVillage.villageSpawnBiomes=Arrays.asList(new BiomeGenBase[] { });
		}

		try {
			EntityRegistry.registerGlobalEntityID(MillVillager.MLEntityGenericAsymmFemale.class, MillVillager.GENERIC_ASYMM_FEMALE, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(MillVillager.MLEntityGenericAsymmFemale.class,MillVillager.GENERIC_ASYMM_FEMALE, VILLAGER_ENT_ID+1,instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(MillVillager.MLEntityGenericSymmFemale.class, MillVillager.GENERIC_SYMM_FEMALE, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(MillVillager.MLEntityGenericSymmFemale.class,MillVillager.GENERIC_SYMM_FEMALE, VILLAGER_ENT_ID+2,instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(MillVillager.MLEntityGenericMale.class, MillVillager.GENERIC_VILLAGER, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(MillVillager.MLEntityGenericMale.class,MillVillager.GENERIC_VILLAGER, VILLAGER_ENT_ID,instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(EntityMillDecoration.class, "ml_Tapestry", ModLoader.getUniqueEntityId());
			EntityRegistry.registerModEntity(EntityMillDecoration.class, "ml_Tapestry",VILLAGER_ENT_ID+7,instance, 80, 100000, false);
			
			EntityRegistry.registerGlobalEntityID(EntityTargetedGhast.class, ENTITY_TARGETED_GHAST, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityTargetedGhast.class,ENTITY_TARGETED_GHAST, VILLAGER_ENT_ID+8,instance, 64, 3, true);
			
			EntityRegistry.registerGlobalEntityID(EntityTargetedBlaze.class, ENTITY_TARGETED_BLAZE, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityTargetedBlaze.class,ENTITY_TARGETED_BLAZE, VILLAGER_ENT_ID+9,instance, 64, 3, true);

			EntityRegistry.registerGlobalEntityID(EntityTargetedWitherSkeleton.class, ENTITY_TARGETED_WITHERSKELETON, EntityRegistry.findGlobalUniqueEntityId());
			EntityRegistry.registerModEntity(EntityTargetedWitherSkeleton.class,ENTITY_TARGETED_WITHERSKELETON, VILLAGER_ENT_ID+10,instance, 64, 3, true);


			ModLoader.registerBlock(lockedChest);
			ModLoader.registerBlock(panel);
			ModLoader.registerBlock(wood_decoration,ItemDecorative.class);
			ModLoader.registerBlock(earth_decoration,ItemDecorative.class);
			ModLoader.registerBlock(stone_decoration,ItemDecorative.class);
			ModLoader.registerBlock(path);
			ModLoader.registerBlock(pathSlab);
			
	        Item.itemsList[path.blockID] = (new ItemDecorativeSlab(path.blockID - 256, pathSlab, path, true)).setUnlocalizedName("ml_path");
	        Item.itemsList[pathSlab.blockID] = (new ItemDecorativeSlab(pathSlab.blockID - 256, pathSlab, path, false)).setUnlocalizedName("ml_path_slab");

			ModLoader.registerBlock(byzantine_tiles);
			ModLoader.registerBlock(byzantine_tile_slab);
			ModLoader.registerBlock(byzantine_stone_tiles);

			ModLoader.addSmelting(lambRaw.itemID, new ItemStack(lambCooked,1));
			
			FurnaceRecipes.smelting().addSmelting(stone_decoration.blockID, 1, new ItemStack(stone_decoration.blockID,1,0), 0.3f);

			ModLoader.addShapelessRecipe(new ItemStack(vegcurry,1),new Object[]{rice,turmeric});
			ModLoader.addShapelessRecipe(new ItemStack(chickencurry,1),new Object[]{rice,turmeric,Item.chickenRaw});

			ModLoader.addShapelessRecipe(new ItemStack(wineBasic,1),new Object[]{grapes,grapes,grapes,grapes,grapes,grapes});

			ModLoader.addRecipe(new ItemStack(masa, 1), new Object []{"###", Character.valueOf('#'), maize});
			ModLoader.addRecipe(new ItemStack(wah, 1), new Object []{"#X#", Character.valueOf('#'), maize,Character.valueOf('X'), Item.chickenRaw});


			ModLoader.addRecipe(new ItemStack(byzantine_tile_slab, 6), new Object []{"###", Character.valueOf('#'), byzantine_tiles});
			ModLoader.addRecipe(new ItemStack(byzantine_stone_tiles, 6), new Object []{"###","SSS", Character.valueOf('#'), byzantine_tiles,
				Character.valueOf('S'), Block.stone});

			for (int meta=0;meta<16;meta++) {
				GameRegistry.addRecipe(new ItemStack(Mill.pathSlab,3,meta), "xxx",
				        'x', new ItemStack(Mill.path,1,meta));
			}
		

			ModLoader.registerBlock(paperWall);


			proxy.registerTileEntities();
			proxy.registerGraphics();
			proxy.preloadTextures();

		} catch (final Exception e) {
			MLN.error(this, "Exception in mod initialisation: ");
			MLN.printException(e);
		}

		boolean error=false;

		final Block block = Block.blocksList[Mill.lockedChest.blockID];
		if (block.getClass() != BlockMillChest.class) {
			MLN.error(this, "Block "+MLN.blockBuildingId+" is not Millenaire's BlockBuilding. Maybe you have a clash with an other mod?");
			error=true;
		}

		if (!error) {
			error=BuildingPlan.loadBuildingPoints();
		}

		if (!error) {
			Goods.loadGoods();
		}

		if (!error) {
			Goal.initGoals();
		}

		if (!error) {
			error=Culture.loadCultures();
		}

		if (!error) {
			Quest.loadQuests();
		}
		if (MLN.generateGoodsList) {
			Goods.generateGoodsList();
		}

		startupError=error;

		loadingComplete=true;

		if (MLN.LogOther>=MLN.MAJOR) {
			if (startupError) {
				MLN.major(this, "Mill\u00e9naire "+versionNumber+" loaded unsuccessfully.");
			} else {
				MLN.major(this, "Mill\u00e9naire "+versionNumber+" loaded successfully.");
			}
		}

		TickRegistry.registerTickHandler(new ServerTickHandler(EnumSet.of(TickType.SERVER)), Side.SERVER);

		proxy.registerForgeClientClasses();

		NetworkRegistry.instance().registerGuiHandler(instance,proxy.createGuiHandler());
		GameRegistry.registerWorldGenerator(new WorldGenVillage());

		MinecraftForge.EVENT_BUS.register(new MillEventController());

		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoaderCallback());



		proxy.loadLanguages();
	}





}

