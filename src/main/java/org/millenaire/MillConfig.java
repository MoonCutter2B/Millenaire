package org.millenaire;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.millenaire.events.MillenaireEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MillConfig 
{	
	public static boolean learnLanguages;
	public static boolean villageAnnouncement;
	public static boolean displayNames;
	public static int nameDistance;
	public static int dialogueDistance;
	
	public static boolean generateVillages;
	public static boolean generateLoneBuildings;
	public static int minVillageDistance;
	public static int minLoneDistance;
	public static int minVillageLoneDistance;
	public static int spawnDistance;
	
	public static int loadedRadius;
	public static int minBuildingDistance;
	public static int maxChildren;
	public static boolean buildPaths;
	public static int villageRelationDistance;
	public static int banditRaidDistance;
	public static int raidPercentChance;
	public static String forbiddenBlocks;
	
	public static final String CATEGORYUIOPTIONS = "ctgy_uioptions";
	public static final String CATEGORYWORLDGEN = "ctgy_worldgen";
	public static final String CATEGORYVILLAGEBEV = "ctgy_villagebehavior";

	public static void preinitialize()
	{
		File configFile = new File(Loader.instance().getConfigDir(), "Millenaire.cfg");
		config = new Configuration(configFile);
		
		syncFromFile();
	}
	
	@SideOnly(Side.CLIENT)
	public static void eventRegister()
	{
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
		MinecraftForge.EVENT_BUS.register(new MillenaireEventHandler());
	}
	
	public static Configuration getConfig() { return config; }
	
	public static void syncFromFile() { syncConfig(true, true); }
	
	public static void syncFromGui() { syncConfig(false, true); }
	
	public static void syncFromFields() { syncConfig(false, false); }
	
	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{
		//Load
		if(loadConfigFromFile) { config.load(); }
		
		//Define
		Property learnLanguagesProp = config.get(CATEGORYUIOPTIONS, "learnLanguages", true);
		learnLanguagesProp.setLanguageKey("gui.millConfig.learnLanguages").setRequiresWorldRestart(true);
		Property villageAnnouncementProp = config.get(CATEGORYUIOPTIONS, "villageAnnouncementRecipe", false);
		villageAnnouncementProp.setLanguageKey("gui.millConfig.villageAnnouncement").setRequiresWorldRestart(true);
		Property displayNamesProp = config.get(CATEGORYUIOPTIONS, "displayNames", true);
		displayNamesProp.setLanguageKey("gui.millConfig.displayNames");
		Property nameDistanceProp = config.get(CATEGORYUIOPTIONS, "nameDistance", 20);
		nameDistanceProp.setLanguageKey("gui.millConfig.nameDistance");
		Property dialogueDistanceProp = config.get(CATEGORYUIOPTIONS, "dialogueDistance", 5);
		dialogueDistanceProp.setLanguageKey("gui.millConfig.dialogueDistance");
		
		Property generateVillagesProp = config.get(CATEGORYWORLDGEN, "generateVillages", true);
		generateVillagesProp.setLanguageKey("gui.millConfig.generateVillages").setRequiresWorldRestart(true);
		Property generateLoneBuildingsProp = config.get(CATEGORYWORLDGEN, "generateLoneBuildings", true);
		generateLoneBuildingsProp.setLanguageKey("gui.millConfig.generateLoneBuildings").setRequiresWorldRestart(true);
		Property minVillageDistanceProp = config.get(CATEGORYWORLDGEN, "minVillageDistance", 600);
		minVillageDistanceProp.setLanguageKey("gui.millConfig.minVillageDistance").setRequiresWorldRestart(true);
		Property minLoneDistanceProp = config.get(CATEGORYWORLDGEN, "minLoneDistance", 600);
		minLoneDistanceProp.setLanguageKey("gui.millConfig.minLoneDistance").setRequiresWorldRestart(true);
		Property minVillageLoneDistanceProp = config.get(CATEGORYWORLDGEN, "minVillageLoneDistance", 300);
		minVillageLoneDistanceProp.setLanguageKey("gui.millConfig.minVillageLoneDistance").setRequiresWorldRestart(true);
		Property spawnDistanceProp = config.get(CATEGORYWORLDGEN, "spawnDistance", 200);
		spawnDistanceProp.setLanguageKey("gui.millConfig.spawnDistance").setRequiresWorldRestart(true);
		
		Property loadedRadiusProp = config.get(CATEGORYVILLAGEBEV, "loadedRadius", 200);
		loadedRadiusProp.setLanguageKey("gui.millConfig.loadedRadius");
		Property minBuildingDistanceProp = config.get(CATEGORYVILLAGEBEV, "minBuildingDistance", 2);
		minBuildingDistanceProp.setLanguageKey("gui.millConfig.minBuildingDistance");
		Property maxChildrenProp = config.get(CATEGORYVILLAGEBEV, "maxChildren", 10);
		maxChildrenProp.setLanguageKey("gui.millConfig.maxChildren");
		Property buildPathsProp = config.get(CATEGORYVILLAGEBEV, "buildPaths", true);
		buildPathsProp.setLanguageKey("gui.millConfig.buildPaths");
		Property villageRelationDistanceProp = config.get(CATEGORYVILLAGEBEV, "villageRelationDistance", 2000);
		villageRelationDistanceProp.setLanguageKey("gui.millConfig.villageRelationDistance").setRequiresWorldRestart(true);
		Property banditRaidDistanceProp = config.get(CATEGORYVILLAGEBEV, "banditRaidDistance", 1500);
		banditRaidDistanceProp.setLanguageKey("gui.millConfig.banditRaidDistance").setRequiresWorldRestart(true);
		Property raidPercentChanceProp = config.get(CATEGORYVILLAGEBEV, "raidPercentChance", 20);
		raidPercentChanceProp.setLanguageKey("gui.millConfig.raidPercentChance");
		Property forbiddenBlockProp = config.get(CATEGORYVILLAGEBEV, "forbiddenBlocks", "forbidden: ");
		forbiddenBlockProp.setLanguageKey("gui.millconfig.forbiddenBlocks").setRequiresMcRestart(true);
		
		//Ordering Config
		List<String> propOrderUIOptions = new ArrayList<String>() {{
			add(learnLanguagesProp.getName());
			add(villageAnnouncementProp.getName());
			add(displayNamesProp.getName());
			add(nameDistanceProp.getName());
			add(dialogueDistanceProp.getName());
		}};
		config.setCategoryPropertyOrder(CATEGORYUIOPTIONS, propOrderUIOptions);

		List<String> propOrderWorldGen = new ArrayList<String>() {{
            add(generateVillagesProp.getName());
            add(generateLoneBuildingsProp.getName());
            add(minVillageDistanceProp.getName());
            add(minLoneDistanceProp.getName());
            add(minVillageLoneDistanceProp.getName());
            add(spawnDistanceProp.getName());
        }};
		config.setCategoryPropertyOrder(CATEGORYWORLDGEN, propOrderWorldGen);

		List<String> propOrderVillageBev = new ArrayList<String>() {{
            add(loadedRadiusProp.getName());
            add(minBuildingDistanceProp.getName());
            add(maxChildrenProp.getName());
            add(buildPathsProp.getName());
            add(villageRelationDistanceProp.getName());
            add(banditRaidDistanceProp.getName());
            add(raidPercentChanceProp.getName());
            add(forbiddenBlockProp.getName());
        }};
		config.setCategoryPropertyOrder(CATEGORYVILLAGEBEV, propOrderVillageBev);
		
		//Read
		if(readFieldsFromConfig)
		{
			learnLanguages = learnLanguagesProp.getBoolean(true);
			villageAnnouncement = villageAnnouncementProp.getBoolean(false);
			displayNames = displayNamesProp.getBoolean(true);
			nameDistance = nameDistanceProp.getInt();
			dialogueDistance = dialogueDistanceProp.getInt();
			
			generateVillages = generateVillagesProp.getBoolean(true);
			generateLoneBuildings = generateLoneBuildingsProp.getBoolean(true);
			minVillageDistance = minVillageDistanceProp.getInt();
			minLoneDistance = minLoneDistanceProp.getInt();
			minVillageLoneDistance = minVillageLoneDistanceProp.getInt();
			spawnDistance = spawnDistanceProp.getInt();
			
			loadedRadius = loadedRadiusProp.getInt();
			minBuildingDistance = minBuildingDistanceProp.getInt();
			maxChildren = maxChildrenProp.getInt();
			buildPaths = buildPathsProp.getBoolean(true);
			villageRelationDistance = villageRelationDistanceProp.getInt();
			banditRaidDistance = banditRaidDistanceProp.getInt();
			raidPercentChance = raidPercentChanceProp.getInt();
			forbiddenBlocks = forbiddenBlockProp.getString();
		}
		
		//Save
		learnLanguagesProp.set(learnLanguages);
		villageAnnouncementProp.set(villageAnnouncement);
		displayNamesProp.set(displayNames);
		nameDistanceProp.set(nameDistance);
		dialogueDistanceProp.set(dialogueDistance);
		
		generateVillagesProp.set(generateVillages);
		generateLoneBuildingsProp.set(generateLoneBuildings);
		minVillageDistanceProp.set(minVillageDistance);
		minLoneDistanceProp.set(minLoneDistance);
		minVillageLoneDistanceProp.set(minVillageLoneDistance);
		spawnDistanceProp.set(spawnDistance);
		
		loadedRadiusProp.set(loadedRadius);
		minBuildingDistanceProp.set(minBuildingDistance);
		maxChildrenProp.set(maxChildren);
		buildPathsProp.set(buildPaths);
		villageRelationDistanceProp.set(villageRelationDistance);
		banditRaidDistanceProp.set(banditRaidDistance);
		raidPercentChanceProp.set(raidPercentChance);
		forbiddenBlockProp.set(forbiddenBlocks);
		
		if(config.hasChanged())
		{
			config.save();
		}
	}
	
	private static Configuration config = null;

    public static class ConfigEventHandler
    {
        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if(event.modID.equals(Millenaire.MODID) && !event.isWorldRunning)
            {
                syncFromGui();
                System.out.println("Reloaded Config");
            }
        }
    }
}
