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
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
 
import org.millenaire.client.network.ClientReceiver;
import org.millenaire.common.Culture;
import org.millenaire.common.EntityWallDecoration;
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
import org.millenaire.common.forge.BuildingChunkLoader.ChunkLoaderCallback;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.Goods;
import org.millenaire.common.item.Goods.ItemAmuletAlchemist;
import org.millenaire.common.item.Goods.ItemAmuletSkollHati;
import org.millenaire.common.item.Goods.ItemAmuletVishnu;
import org.millenaire.common.item.Goods.ItemAmuletYddrasil;
import org.millenaire.common.item.Goods.ItemBrickMould;
import org.millenaire.common.item.Goods.ItemClothes;
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
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
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

	public static final String versionNumber = "4.7.0";
	public static final String versionBound = "[4.7.0,5.0)";
	public static final String modId="Millenaire";
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

	public static boolean loadingComplete=false;

	public static boolean startMessageDisplayed=false;

	@SuppressWarnings("rawtypes")
	public static HashMap<Class,String> entityNames;

	public static final String ENTITY_PIG="Pig",ENTITY_COW="Cow",ENTITY_CHICKEN="Chicken",ENTITY_SHEEP="Sheep";

	public static final String ENTITY_SKELETON="Skeleton",ENTITY_CREEPER="Creeper",ENTITY_SPIDER="Spider",
			ENTITY_CAVESPIDER="CaveSpider",ENTITY_ZOMBIE="Zombie";

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

		proxy.setTextureIds();

		denier = (new ItemText(nextItemId(),"denier")).setUnlocalizedName("ml_denier");
		denier_or = (new ItemText(nextItemId(),"denier_or")).setUnlocalizedName("ml_denier_or");
		denier_argent = (new ItemText(nextItemId(),"denier_argent")).setUnlocalizedName("ml_denier_argent");
		ciderapple = (new ItemFoodMultiple(nextItemId(),"ciderapple",0,1,0.05f,0,false)).setUnlocalizedName("ml_ciderapple").setMaxStackSize(64);;
		cider = (new ItemFoodMultiple(nextItemId(),"cider",4,0,0,2,true)).setUnlocalizedName("ml_cider");
		calva = (new ItemFoodMultiple(nextItemId(),"calva",4,0,0,9,true)).setUnlocalizedName("ml_calva");
		tripes = (new ItemFoodMultiple(nextItemId(),"tripes",5,8,0.8f,2,false)).setUnlocalizedName("ml_tripes");

		normanPickaxe =  new ItemMillenairePickaxe(nextItemId(),"normanpickaxe",EnumToolMaterial.IRON,12).setUnlocalizedName("ml_normanPickaxe");
		normanAxe =  new ItemMillenaireAxe(nextItemId(),"normanaxe",EnumToolMaterial.IRON,12).setUnlocalizedName("ml_normanAxe");
		normanShovel =  new ItemMillenaireShovel(nextItemId(),"normanshovel",EnumToolMaterial.IRON,12).setUnlocalizedName("ml_normanShovel");
		normanHoe =  new ItemMillenaireHoe(nextItemId(),"normanhoe",1500).setUnlocalizedName("ml_normanHoe");

		summoningWand = new ItemSummoningWand(nextItemId(),"summoningwand").setFull3D().setUnlocalizedName("ml_villageWand");

		normanBroadsword = new ItemMillenaireSword(nextItemId(),"normansword",1500,15,EnumToolMaterial.EMERALD.getEnchantability(),0,0,false).setUnlocalizedName("ml_normanBroadsword");
		normanHelmet = new ItemMillenaireArmour(nextItemId(),"normanhelmet",EnumArmorMaterial.DIAMOND,normanArmourId,2,EnumArmorMaterial.DIAMOND.getEnchantability(),0).setUnlocalizedName("ml_normanHelmet");
		normanPlate = new ItemMillenaireArmour(nextItemId(),"normanplate",EnumArmorMaterial.DIAMOND,normanArmourId,2,EnumArmorMaterial.DIAMOND.getEnchantability(),1).setUnlocalizedName("ml_normanPlate");
		normanLegs = new ItemMillenaireArmour(nextItemId(),"normanlegs",EnumArmorMaterial.DIAMOND,normanArmourId,2,EnumArmorMaterial.DIAMOND.getEnchantability(),2).setUnlocalizedName("ml_normanLegs");
		normanBoots = new ItemMillenaireArmour(nextItemId(),"normanboots",EnumArmorMaterial.DIAMOND,normanArmourId,2,EnumArmorMaterial.DIAMOND.getEnchantability(),3).setUnlocalizedName("ml_normanBoots");

		parchmentVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.villagers).setUnlocalizedName("ml_parchmentVillagers");

		parchmentBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.buildings).setUnlocalizedName("ml_parchmentBuildings");

		parchmentItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.items).setUnlocalizedName("ml_parchmentItems");


		parchmentComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.villagers,ItemParchment.buildings,ItemParchment.items}).setUnlocalizedName("ml_marchmentComplete");

		boudin = (new ItemFoodMultiple(nextItemId(),"boudin",3,6,0.6f,2,false)).setUnlocalizedName("ml_boudin");

		tapestry = (new ItemTapestry(nextItemId(),"normantapestry",EntityWallDecoration.NORMAN_TAPESTRY)).setUnlocalizedName("ml_tapestry");



		vishnu_amulet = new ItemAmuletVishnu(nextItemId(),"amulet_vishnu").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_raven_amulet").setMaxStackSize(1);
		alchemist_amulet = new ItemAmuletAlchemist(nextItemId(),"amulet_alchemist").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_dwarves_amulet").setMaxStackSize(1);
		yddrasil_amulet = new ItemAmuletYddrasil(nextItemId(),"amulet_yggdrasil").setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_yddrasil_amulet").setMaxStackSize(1);

		skoll_hati_amulet = (new ItemAmuletSkollHati(nextItemId(),"amulet_skollhati")).setCreativeTab(Mill.tabMillenaire).setUnlocalizedName("ml_skoll_hati_amulet").setMaxStackSize(1).setMaxDamage(10);
		parchmentVillageScroll = new ItemParchment(nextItemId(),"parchmentvillage",new int[]{ItemParchment.villageBook}).setUnlocalizedName("ml_parchmentVillageScroll");

		rice = (new ItemMillSeeds(nextItemId(),"rice", crops.blockID,0,Mill.CROP_RICE)).setUnlocalizedName("ml_rice");
		turmeric = (new ItemMillSeeds(nextItemId(),"turmeric", crops.blockID,2,Mill.CROP_TURMERIC)).setUnlocalizedName("ml_turmeric");
		vegcurry = (new ItemFoodMultiple(nextItemId(),"curry",2,2,0.3f,0,false)).setUnlocalizedName("ml_vegcurry");
		chickencurry = (new ItemFoodMultiple(nextItemId(),"currychicken",4,6,0.6f,0,false)).setUnlocalizedName("ml_chickencurry");
		brickmould = (new ItemBrickMould(nextItemId(),"brickmould")).setUnlocalizedName("ml_brickmould").setMaxStackSize(1).setMaxDamage(128);
		rasgulla = (new ItemFoodMultiple(nextItemId(),"rasgulla",2,0,0,0,false)).setUnlocalizedName("ml_rasgullaId").setMaxStackSize(8);
		indianstatue = (new ItemTapestry(nextItemId(),"indianstatue",EntityWallDecoration.INDIAN_STATUE)).setUnlocalizedName("ml_indianstatue");

		parchmentIndianVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.indianVillagers).setUnlocalizedName("ml_parchmentIndianVillagers");
		parchmentIndianBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.indianBuildings).setUnlocalizedName("ml_parchmentIndianBuildings");
		parchmentIndianItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.indianItems).setUnlocalizedName("ml_parchmentIndianItems");
		parchmentIndianComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.indianVillagers,ItemParchment.indianBuildings,ItemParchment.indianItems}).setUnlocalizedName("ml_marchmentIndianComplete");


		mayanstatue = (new ItemTapestry(nextItemId(),"mayanstatue",EntityWallDecoration.MAYAN_STATUE)).setUnlocalizedName("ml_mayanstatue");
		maize = (new ItemMillSeeds(nextItemId(),"maize", crops.blockID,4,Mill.CROP_MAIZE)).setUnlocalizedName("ml_maize");
		wah = (new ItemFoodMultiple(nextItemId(),"wah",2,4,0.4f,0,false)).setUnlocalizedName("ml_wah");
		masa = (new ItemFoodMultiple(nextItemId(),"masa",4,6,0.6f,0,false)).setUnlocalizedName("ml_masa");

		parchmentMayanVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.mayanVillagers).setUnlocalizedName("ml_parchmentMayanVillagers");
		parchmentMayanBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.mayanBuildings).setUnlocalizedName("ml_parchmentMayanBuildings");
		parchmentMayanItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.mayanItems).setUnlocalizedName("ml_parchmentMayanItems");
		parchmentMayanComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.mayanVillagers,ItemParchment.mayanBuildings,ItemParchment.mayanItems}).setUnlocalizedName("ml_parchmentMayanComplete");

		parchmentSadhu = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.sadhu}).setUnlocalizedName("ml_parchmentSadhu");

		unknownPowder =  new ItemText(nextItemId(),"unknownpowder").setUnlocalizedName("ml_unknownPowder").setCreativeTab(Mill.tabMillenaire);

		udon = (new ItemFoodMultiple(nextItemId(),"udon",4,6,0.6f,0,false)).setUnlocalizedName("ml_udon");

		tachiSword = new ItemMillenaireSword(nextItemId(),"tachisword",250,6,EnumToolMaterial.IRON.getEnchantability(),(float) 0.2,3,false).setUnlocalizedName("ml_taichiSword");

		negationWand = new ItemNegationWand(nextItemId(),"negationwand").setFull3D().setUnlocalizedName("ml_negationWand");

		obsidianFlake = new ItemText(nextItemId(),"obsidianflake").setUnlocalizedName("ml_obsidianFlake");
		mayanMace =  new ItemMillenaireSword(nextItemId(),"mayanmace",1500,6,25,0,0,false).setUnlocalizedName("ml_mayanMace");
		mayanPickaxe =  new ItemMillenairePickaxe(nextItemId(),"mayanpickaxe",EnumToolMaterial.EMERALD,6,1500,25).setUnlocalizedName("ml_mayanPickaxe");
		mayanAxe =  new ItemMillenaireAxe(nextItemId(),"mayanaxe",EnumToolMaterial.EMERALD,6,1500,25).setUnlocalizedName("ml_mayanAxe");
		mayanShovel =  new ItemMillenaireShovel(nextItemId(),"mayanshovel",EnumToolMaterial.EMERALD,6,1500,25).setUnlocalizedName("ml_mayanShovel");
		mayanHoe =  new ItemMillenaireHoe(nextItemId(),"mayanhoe",1500).setUnlocalizedName("ml_mayanHoe");

		yumiBow =  new ItemMillenaireBow(nextItemId(),2,(float) 0.5,"yumibow0","yumibow1","yumibow2","yumibow3").setUnlocalizedName("ml_yumiBow").setFull3D();


		japaneseWarriorBlueLegs = new ItemMillenaireArmour(nextItemId(),"japanesebluelegs",EnumArmorMaterial.IRON,japaneseWarriorBlueArmourId,1,25,2).setUnlocalizedName("ml_japaneseWarriorBlueLegs");
		japaneseWarriorBlueHelmet = new ItemMillenaireArmour(nextItemId(),"japanesebluehelmet",EnumArmorMaterial.IRON,japaneseWarriorBlueArmourId,1,25,0).setUnlocalizedName("ml_japaneseWarriorBlueHelmet");
		japaneseWarriorBluePlate = new ItemMillenaireArmour(nextItemId(),"japaneseblueplate",EnumArmorMaterial.IRON,japaneseWarriorBlueArmourId,1,25,1).setUnlocalizedName("ml_japaneseWarriorBluePlate");
		japaneseWarriorBlueBoots = new ItemMillenaireArmour(nextItemId(),"japaneseblueboots",EnumArmorMaterial.IRON,japaneseWarriorBlueArmourId,1,25,3).setUnlocalizedName("ml_japaneseWarriorBlueBoots");

		japaneseWarriorRedLegs = new ItemMillenaireArmour(nextItemId(),"japaneseredlegs",EnumArmorMaterial.IRON,japaneseWarriorRedArmourId,1,25,2).setUnlocalizedName("ml_japaneseWarriorRedLegs");
		japaneseWarriorRedHelmet = new ItemMillenaireArmour(nextItemId(),"japaneseredhelmet",EnumArmorMaterial.IRON,japaneseWarriorRedArmourId,1,25,0).setUnlocalizedName("ml_japaneseWarriorRedHelmet");
		japaneseWarriorRedPlate = new ItemMillenaireArmour(nextItemId(),"japaneseredplate",EnumArmorMaterial.IRON,japaneseWarriorRedArmourId,1,25,1).setUnlocalizedName("ml_japaneseWarriorRedPlate");
		japaneseWarriorRedBoots = new ItemMillenaireArmour(nextItemId(),"japaneseredboots",EnumArmorMaterial.IRON,japaneseWarriorRedArmourId,1,25,3).setUnlocalizedName("ml_japaneseWarriorRedBoots");

		japaneseGuardLegs = new ItemMillenaireArmour(nextItemId(),"japaneseguardlegs",EnumArmorMaterial.CHAIN,japaneseGuardArmourId,1,25,2).setUnlocalizedName("ml_japaneseGuardLegs");
		japaneseGuardHelmet = new ItemMillenaireArmour(nextItemId(),"japaneseguardhelmet",EnumArmorMaterial.CHAIN,japaneseGuardArmourId,1,25,0).setUnlocalizedName("ml_japaneseGuardHelmet");
		japaneseGuardPlate = new ItemMillenaireArmour(nextItemId(),"japaneseguardplate",EnumArmorMaterial.CHAIN,japaneseGuardArmourId,1,25,1).setUnlocalizedName("ml_japaneseGuardPlate");
		japaneseGuardBoots = new ItemMillenaireArmour(nextItemId(),"japaneseguardboots",EnumArmorMaterial.CHAIN,japaneseGuardArmourId,1,25,3).setUnlocalizedName("ml_japaneseGuardBoots");

		parchmentJapaneseVillagers = new ItemParchment(nextItemId(),"parchmentvillagers",ItemParchment.japaneseVillagers).setUnlocalizedName("ml_parchmentJapaneseVillagers");
		parchmentJapaneseBuildings = new ItemParchment(nextItemId(),"parchmentbuildings",ItemParchment.japaneseBuildings).setUnlocalizedName("ml_parchmentJapaneseBuildings");
		parchmentJapaneseItems = new ItemParchment(nextItemId(),"parchmentitems",ItemParchment.japaneseItems).setUnlocalizedName("ml_parchmentJapaneseItems");
		parchmentJapaneseComplete = new ItemParchment(nextItemId(),"parchmentall",new int[]{ItemParchment.japaneseVillagers,ItemParchment.japaneseBuildings,ItemParchment.japaneseItems}).setUnlocalizedName("ml_parchmentJapaneseComplete");


		grapes=(new ItemMillSeeds(nextItemId(),"grapes", crops.blockID,6,Mill.CROP_VINE)).setUnlocalizedName("ml_vine");
		wineFancy = (new ItemFoodMultiple(nextItemId(),"winefancy",4,0,0,4,true)).setUnlocalizedName("ml_wine");
		silk=new ItemText(nextItemId(),"silk").setUnlocalizedName("ml_silk");
		byzantineiconsmall = (new ItemTapestry(nextItemId(),"byzantineicon",EntityWallDecoration.BYZANTINE_ICON_SMALL)).setUnlocalizedName("ml_byzantineicon");
		byzantineiconmedium = (new ItemTapestry(nextItemId(),"byzantineicon",EntityWallDecoration.BYZANTINE_ICON_MEDIUM)).setUnlocalizedName("ml_byzantineiconmedium");
		byzantineiconlarge = (new ItemTapestry(nextItemId(),"byzantineicon",EntityWallDecoration.BYZANTINE_ICON_LARGE)).setUnlocalizedName("ml_byzantineiconlarge");


		byzantineLegs = new ItemMillenaireArmour(nextItemId(),"byzantinelegs",EnumArmorMaterial.DIAMOND,byzantineArmourId,1,20,2).setUnlocalizedName("ml_byzantineLegs");
		byzantineHelmet = new ItemMillenaireArmour(nextItemId(),"byzantinehelmet",EnumArmorMaterial.DIAMOND,byzantineArmourId,1,20,0).setUnlocalizedName("ml_byzantineHelmet");
		byzantinePlate = new ItemMillenaireArmour(nextItemId(),"byzantineplate",EnumArmorMaterial.DIAMOND,byzantineArmourId,1,20,1).setUnlocalizedName("ml_byzantinePlate");
		byzantineBoots = new ItemMillenaireArmour(nextItemId(),"byzantineboots",EnumArmorMaterial.DIAMOND,byzantineArmourId,1,20,3).setUnlocalizedName("ml_byzantineBoots");

		byzantineMace =  new ItemMillenaireSword(nextItemId(),"byzantinemace",120,25,10,0,0,true).setUnlocalizedName("ml_byzantineMace");

		clothes = (ItemClothes) new ItemClothes(nextItemId(),"byzantineclothwool","byzantineclothsilk").setUnlocalizedName("ml_clothes");
		wineBasic = (new ItemFoodMultiple(nextItemId(),"winebasic",3,0,0,3,true)).setUnlocalizedName("ml_wine_basic");
		lambRaw = (new ItemFoodMultiple(nextItemId(),"lambraw",0,2,0.2f,0,false)).setMaxStackSize(64).setUnlocalizedName("ml_lamb_raw");
		lambCooked = (new ItemFoodMultiple(nextItemId(),"lambcooked",0,6,0.6f,0,false)).setMaxStackSize(64).setUnlocalizedName("ml_lamb_cooked");
		feta = (new ItemFoodMultiple(nextItemId(),"feta",2,0,0,0,false)).setMaxStackSize(8).setUnlocalizedName("ml_feta");
		souvlaki = (new ItemFoodMultiple(nextItemId(),"souvlaki",5,8,0.8f,2,false)).setUnlocalizedName("ml_souvlaki");

		purse = (ItemPurse) new ItemPurse(nextItemId(),"purse").setMaxStackSize(1).setUnlocalizedName("ml_purse");
		
		sake = (new ItemFoodMultiple(nextItemId(),"sake",3,0,0,6,true)).setUnlocalizedName("ml_sake");
		
		cacauhaa = (new ItemFoodMultiple(nextItemId(),"cacauhaa",6,0,0,3,true)).setUnlocalizedName("ml_cacauhaa");

		byzantine_tiles = (BlockOrientedBrick) new BlockOrientedBrick(MLN.blockByzantineBrickId,
				"tilestopvert","tilestophor","tilestopvert","tilestophor","tilesfront","tilestophor").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_brick");
		byzantine_tile_slab = (BlockOrientedSlab) new BlockOrientedSlab(MLN.blockByzantineSlabId,"tilestophor","tilestopvert","tilesfront").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_brick_slab");
		byzantine_stone_tiles = (BlockOrientedBrick) new BlockOrientedBrick(MLN.blockByzantineMixedId,
				"tilestopvert","tilestophor","stone","stone","tileshalffront","tileshalfside").setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("byzantine_mixedbrick");

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

	@Init
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



			EntityRegistry.registerGlobalEntityID(EntityWallDecoration.class, "ml_Tapestry", ModLoader.getUniqueEntityId());
			EntityRegistry.registerModEntity(EntityWallDecoration.class, "ml_Tapestry",VILLAGER_ENT_ID+7,instance, 80, 3, true);

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

			ModLoader.addSmelting(stone_decoration.blockID, new ItemStack(stone_decoration.blockID,1,0));
			ModLoader.addSmelting(lambRaw.itemID, new ItemStack(lambCooked,1));

			ModLoader.addShapelessRecipe(new ItemStack(vegcurry,1),new Object[]{rice,turmeric});
			ModLoader.addShapelessRecipe(new ItemStack(chickencurry,1),new Object[]{rice,turmeric,Item.chickenRaw});

			ModLoader.addShapelessRecipe(new ItemStack(wineBasic,1),new Object[]{grapes,grapes,grapes,grapes,grapes,grapes});

			ModLoader.addRecipe(new ItemStack(masa, 1), new Object []{"###", Character.valueOf('#'), maize});
			ModLoader.addRecipe(new ItemStack(wah, 1), new Object []{"#X#", Character.valueOf('#'), maize,Character.valueOf('X'), Item.chickenRaw});


			ModLoader.addRecipe(new ItemStack(byzantine_tile_slab, 6), new Object []{"###", Character.valueOf('#'), byzantine_tiles});
			ModLoader.addRecipe(new ItemStack(byzantine_stone_tiles, 6), new Object []{"###","SSS", Character.valueOf('#'), byzantine_tiles,
				Character.valueOf('S'), Block.stone});


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

	@PostInit
	public void modsLoaded(FMLPostInitializationEvent evt)
	{
		/* and this replaces modsLoaded(), all mods are loaded at this point, do inter-mod stuff here */
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		MLN.loadConfig();

		initBlockItems();

		AchievementPage.registerAchievementPage(MillAchievements.millAchievements);
	}

	@ServerStarted
	public void serverStarted(FMLServerStartedEvent event)
	{
		/*
		 * Should you need something particular done on a server, this is the place
		 * fires on both the Integrated Server ("SSP") and real servers
		 */
	}
}

