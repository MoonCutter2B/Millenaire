package org.millenaire.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Quest.QuestStep;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingPlanSet;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.PrefixExtFileFilter;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class MLN {
	public static class FileFiler implements FilenameFilter {

		String end;

		public FileFiler(String ending) {
			end=ending;
		}

		@Override
		public boolean accept(File file, String name) {

			if (!name.endsWith(end))
				return false;

			if (name.startsWith("."))
				return false;

			return true;
		}
	}
	public static class Language {

		private static class ParchmentFileFilter implements FilenameFilter {

			private final String filePrefix;

			public ParchmentFileFilter(String filePrefix) {
				this.filePrefix=filePrefix;
			}

			@Override
			public boolean accept(File file, String name) {

				if (!name.startsWith(filePrefix))
					return false;

				if (!name.endsWith(".txt"))
					return false;

				final String id=name.substring(filePrefix.length()+1, name.length()-4);

				if ((id.length()==0) || (Integer.parseInt(id)<1))
					return false;

				return true;
			}
		}

		private static final int PARCHMENT = 0;
		private static final int HELP = 1;

		public String language,topLevelLanguage=null;
		public boolean serverContent;

		public HashMap<String,String> strings=new HashMap<String,String>();
		public HashMap<String,String> questStrings=new HashMap<String,String>();


		public HashMap<Integer,Vector<Vector<String>>> texts=new HashMap<Integer,Vector<Vector<String>>>();
		public HashMap<Integer,String> textsVersion=new HashMap<Integer,String>();

		public HashMap<Integer,Vector<Vector<String>>> help=new HashMap<Integer,Vector<Vector<String>>>();
		public HashMap<Integer,String> helpVersion=new HashMap<Integer,String>();

		public Language(String key,boolean serverContent) {
			language=key;

			if (language.split("_").length>1) {
				topLevelLanguage=language.split("_")[0];
			}

			this.serverContent=serverContent;

		}

		public void compareWithLanguage(HashMap<String,Integer> percentages, Language ref) {
			MLN.major(null, "Generating translation gap file between "+language+" and "+ref.language);

			final File translationGapDir=new File(Mill.proxy.getBaseDir(),"Translation gaps");

			if (!translationGapDir.exists()) {
				translationGapDir.mkdirs();
			}

			final File file = new File(translationGapDir,language+"-"+ref.language+".txt");

			if (file.exists()) {
				file.delete();
			}

			BufferedWriter writer;
			try {

				int translationsMissing=0,translationsDone=0;

				writer = MillCommonUtilities.getWriter(file);

				writer.write("Translation comparison between "+language+" and "+ref.language+MLN.EOL+MLN.EOL);

				Vector<String> errors=new Vector<String>();

				Vector<String> keys=new Vector<String>(ref.strings.keySet());
				Collections.sort(keys);

				for (final String key : keys) {
					if (!strings.containsKey(key)) {
						errors.add("Key missing in the strings.txt file: "+key);
						translationsMissing++;
					} else {
						final int nbValues=ref.strings.get(key).split("<").length-1;
						final int nbValues2=strings.get(key).split("<").length-1;
						if (nbValues!=nbValues2) {
							errors.add("Mismatched number of parameters for "+key+": "+nbValues+" in "+ref.language+" and "+nbValues2+" in "+language);
							translationsMissing++;
						} else {
							translationsDone++;
						}
					}
				}

				if (errors.size()>0) {
					writer.write("List of gaps found in strings.txt: "+MLN.EOL+MLN.EOL);

					for (final String s : errors) {
						writer.write(s+MLN.EOL);
					}
					writer.write(MLN.EOL);
				}

				errors=new Vector<String>();
				keys=new Vector<String>(ref.questStrings.keySet());
				Collections.sort(keys);


				for (final String key : keys) {
					if (!questStrings.containsKey(key)) {
						errors.add("Key missing in the quests.txt file: "+key);
						translationsMissing++;
					} else {
						translationsDone++;
					}
				}

				if (errors.size()>0) {
					writer.write("List of gaps found in quest files: "+MLN.EOL+MLN.EOL);

					for (final String s : errors) {
						writer.write(s+MLN.EOL);
					}
					writer.write(MLN.EOL);
				}

				errors=new Vector<String>();

				for (final Goal goal : Goal.goals.values()) {
					if (!strings.containsKey("goal."+goal.labelKey()) && !ref.strings.containsKey("goal."+goal.labelKey())) {
						errors.add("Could not find label for goal."+goal.labelKey()+" (class: "+(goal.getClass().getSimpleName())+") in either language.");
					}
				}

				if (errors.size()>0) {
					writer.write("List of goals without labels: "+MLN.EOL+MLN.EOL);

					for (final String s : errors) {
						writer.write(s+MLN.EOL);
					}
					writer.write(MLN.EOL);
				}

				errors=new Vector<String>();

				for (final int id : ref.texts.keySet()) {
					if (!texts.containsKey(id)) {
						errors.add("Parchment "+id+" is missing.");
						translationsMissing+=10;
					} else {
						if (!textsVersion.get(id).equals(ref.textsVersion.get(id))) {
							errors.add("Parchment "+id+" has a different version: it is at version "+textsVersion.get(id)+" while "+ref.language+" parchment is at "+ref.textsVersion.get(id));
							translationsMissing+=5;
						} else {
							translationsDone+=10;
						}
					}
				}

				for (final int id : ref.help.keySet()) {
					if (!help.containsKey(id)) {
						errors.add("Help "+id+" is missing.");
						translationsMissing+=10;
					} else {
						if (!helpVersion.get(id).equals(ref.helpVersion.get(id))) {
							errors.add("Help "+id+" has a different version: it is at version "+helpVersion.get(id)+" while "+ref.language+" parchment is at "+ref.helpVersion.get(id));
							translationsMissing+=5;
						} else {
							translationsDone+=10;
						}
					}
				}

				if (errors.size()>0) {
					writer.write("List of gaps found between parchments: "+MLN.EOL+MLN.EOL);

					for (final String s : errors) {
						writer.write(s+MLN.EOL);
					}
					writer.write(MLN.EOL);
				}

				for (final Culture c : Culture.vectorCultures) {
					final int[] res=c.compareCultureLanguages(language, ref.language, writer);
					translationsDone+=res[0];
					translationsMissing+=res[1];
				}

				int percentDone;

				if ((translationsDone+translationsMissing)>0) {
					percentDone=(translationsDone*100)/(translationsDone+translationsMissing);
				} else {
					percentDone=0;
				}

				percentages.put(language, percentDone);

				writer.write("Traduction completness: "+percentDone+"%"+MLN.EOL);

				writer.flush();
				writer.close();
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}

		public void loadFromDisk(Vector<File> languageDirs) {
			for (final File languageDir : languageDirs) {

				File effectiveLanguageDir = new File(languageDir,language);

				if (!effectiveLanguageDir.exists()) {
					effectiveLanguageDir = new File(languageDir,language.split("_")[0]);
				}

				final File stringFile = new File(effectiveLanguageDir,"strings.txt");
				if (stringFile.exists()) {
					loadStrings(strings, stringFile);
				}

				if (effectiveLanguageDir.exists()) {
					for (final File file : effectiveLanguageDir.listFiles(new PrefixExtFileFilter("quests","txt"))) {
						loadStrings(questStrings,file);
					}
				}
			}

			for (final Quest q : Quest.quests.values()) {
				for (final QuestStep step : q.steps) {
					if (step.labels.containsKey(language)) {
						questStrings.put(step.getStringKey()+"label", step.labels.get(language));
					} else if ((topLevelLanguage!=null) && step.labels.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"label", step.labels.get(topLevelLanguage));
					}

					if (step.descriptions.containsKey(language)) {
						questStrings.put(step.getStringKey()+"description", step.descriptions.get(language));
					} else if ((topLevelLanguage!=null) && step.descriptions.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"description", step.descriptions.get(topLevelLanguage));
					}

					if (step.descriptionsSuccess.containsKey(language)) {
						questStrings.put(step.getStringKey()+"description_success", step.descriptionsSuccess.get(language));
					} else if ((topLevelLanguage!=null) && step.descriptionsSuccess.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"description_success", step.descriptionsSuccess.get(topLevelLanguage));
					}

					if (step.descriptionsRefuse.containsKey(language)) {
						questStrings.put(step.getStringKey()+"description_refuse", step.descriptionsRefuse.get(language));
					} else if ((topLevelLanguage!=null) && step.descriptionsRefuse.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"description_refuse", step.descriptionsRefuse.get(topLevelLanguage));
					}

					if (step.descriptionsTimeUp.containsKey(language)) {
						questStrings.put(step.getStringKey()+"description_timeup", step.descriptionsTimeUp.get(language));
					} else if ((topLevelLanguage!=null) && step.descriptionsTimeUp.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"description_timeup", step.descriptionsTimeUp.get(topLevelLanguage));
					}

					if (step.listings.containsKey(language)) {
						questStrings.put(step.getStringKey()+"listing", step.listings.get(language));
					} else if ((topLevelLanguage!=null) && step.listings.containsKey(topLevelLanguage)) {
						questStrings.put(step.getStringKey()+"listing", step.listings.get(topLevelLanguage));
					}
				}
			}

			loadTextFiles(languageDirs,PARCHMENT);
			loadTextFiles(languageDirs,HELP);

			if (!MLN.loadedLanguages.containsKey(language)) {
				MLN.loadedLanguages.put(language,this);
			}

		}

		private void loadStrings(HashMap<String,String> strings, File file) {

			try {

				final BufferedReader reader = MillCommonUtilities.getReader(file);

				String line;

				while ((line=reader.readLine()) != null) {
					line=line.trim();
					if ((line.length() > 0) && !line.startsWith("//")) {
						final String[] temp=line.split("=");
						if (temp.length==2) {

							final String key=temp[0].trim().toLowerCase();
							final String value=temp[1].trim();

							if (strings.containsKey(key)) {
								MLN.error(null, "Key "+key+" is present more than once in "+file.getAbsolutePath());
							} else {
								strings.put(key, value);
							}
						} else if (line.endsWith("=")) {
							final String key=temp[0].toLowerCase();

							if (strings.containsKey(key)) {
								MLN.error(null, "Key "+key+" is present more than once in "+file.getAbsolutePath());
							} else {
								strings.put(key, "");
							}
						}
					}
				}
				reader.close();
			} catch (final Exception e) {
				MLN.printException(e);
				return;
			}

			return;
		}



		public void loadTextFiles(Vector<File> languageDirs,int type) {

			String dirName,filePrefix;

			if (type==PARCHMENT) {
				dirName="parchments";
			} else {
				dirName="help";
			}

			if (type==PARCHMENT) {
				filePrefix="parchment";
			} else {
				filePrefix="help";
			}

			for (final File languageDir : languageDirs) {

				File parchmentsDir = new File(new File(languageDir,language),dirName);

				if (!parchmentsDir.exists()) {
					parchmentsDir = new File(new File(languageDir,language.split("_")[0]),dirName);
				}


				if (!parchmentsDir.exists())
					return;

				final ParchmentFileFilter filter=new ParchmentFileFilter(filePrefix);

				for (final File file : parchmentsDir.listFiles(filter)) {

					final String sId=file.getName().substring(filePrefix.length()+1, file.getName().length()-4);

					int id=0;

					if (sId.length()>0) {
						try {
							id=Integer.parseInt(sId);
						} catch (final Exception e) {
							MLN.printException("Error when trying to read pachment id: ", e);
						}
					} else {
						MLN.error(null, "Couldn't read the ID of "+file.getAbsolutePath()+". sId: "+sId);
					}

					if (MLN.LogBuildingPlan>=MLN.MAJOR) {
						MLN.minor(file, "Loading "+dirName+": "+file.getAbsolutePath());
					}

					final Vector<Vector<String>> text=new Vector<Vector<String>>();

					String version="unknown";

					try {
						final BufferedReader reader = MillCommonUtilities.getReader(file);

						String line;

						Vector<String> page=new Vector<String>();

						while ((line=reader.readLine()) != null) {

							if (line.equals("NEW_PAGE")) {
								text.add(page);
								page=new Vector<String>();
							} else if (line.startsWith("version:")) {
								version=line.split(":")[1];
							} else {
								page.add(line);
							}
						}
						text.add(page);

						if (type==PARCHMENT) {
							texts.put(id,text);
							textsVersion.put(id, version);
						} else {
							help.put(id,text);
							helpVersion.put(id, version);
						}

					} catch (final Exception e) {
						MLN.printException(e);
					}
				}
			}
		}

	}
	public static class MillenaireException extends Exception {
		private static final long serialVersionUID = 1L;

		public MillenaireException(String string) {
			super(string);
		}
	}

	public static boolean logPerformed=false;

	public static final char BLACK = '0';
	public static final char DARKBLUE = '1';
	public static final char DARKGREEN = '2';
	public static final char LIGHTBLUE = '3';
	public static final char DARKRED = '4';
	public static final char PURPLE = '5';
	public static final char ORANGE = '6';
	public static final char LIGHTGREY = '7';
	public static final char DARKGREY = '8';
	public static final char BLUE = '9';
	public static final char LIGHTGREEN = 'a';
	public static final char CYAN = 'b';
	public static final char LIGHTRED = 'c';

	public static final char PINK = 'd';
	public static final char YELLOW = 'e';

	public static final char WHITE = 'f';

	public static int KeepActiveRadius=200;
	public static int BackgroundRadius=2000;
	public static int BanditRaidRadius=1500;
	public static int blockBuildingId = 1515;
	public static int blockPanelId = 1516;
	public static int blockWoodId = 1517;
	public static int blockEarthId = 1518;
	public static int blockStoneId = 1519;
	public static int blockCropsId = 1520;
	public static int blockPanesId = 1521;
	public static int blockByzantineBrickId = 1522;
	public static int blockByzantineSlabId = 1523;
	public static int blockByzantineMixedId = 1524;
	public static int blockPathId = 1525;
	public static int blockPathSlabId = 1526;
	public static int itemRangeStart = 25744;


	public static int LogBuildingPlan = 0;
	public static int CattleFarmer=0;
	public static int Children = 0;
	public static int Translation = 0;
	public static int Connections = 0;
	public static int LogCulture=0;
	public static int Diplomacy=0;
	public static int FarmerAI = 0;
	public static int GeneralAI = 0;
	public static int getPath = 0;
	public static int Hybernation = 0;
	public static int Lumberman = 0;
	public static int Merchant=0;
	public static int Miner = 0;
	public static int Other = 0;
	public static int Pathing = 0;
	public static int Performance = 0;
	public static int Selling = 0;
	public static int TileEntityBuilding = 0;
	public static int LogVillage=0;
	public static int LogVillager = 0;
	public static int LogQuest = 0;
	public static int WifeAI = 0;
	public static int WorldGeneration = 0;
	public static int WorldInfo=0;
	public static int Pujas=0;
	public static int VillagerSpawn=0;
	public static int VillagePaths=0;

	public static String questBiomeForest = "forest";
	public static String questBiomeDesert = "desert";
	public static String questBiomeMountain = "mountain";


	public static int Network=0;
	public static final int MAJOR=1;
	public static final int MINOR=2;

	public static final int DEBUG=3;
	private static boolean console = true;

	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss";
	public static boolean DEV = false;
	public static boolean displayNames = true;
	public static boolean displayStart = true;
	public static final String EOL = System.getProperty("line.separator");
	public static Vector<Integer> forbiddenBlocks = new Vector<Integer>();
	public static boolean generateBuildingRes = false;
	public static boolean generateColourSheet = false;
	public static boolean generateVillages = true;
	public static boolean generateVillagesDefault = true;
	public static boolean generateLoneBuildings = true;
	public static boolean generateTranslationGap = false;
	public static boolean generateGoodsList = false;
	public static boolean infiniteAmulet = false;
	public static boolean languageLearning = true;
	public static boolean stopDefaultVillages = false;
	public static boolean seIndicators = false;
	public static boolean loadAllLanguages = true;

	public static boolean jpsPathing = true;

	public static String main_language="";
	public static String effective_language="";
	public static String fallback_language="en";

	private static boolean logfile = true;
	public static int maxChildrenNumber = 10;
	public static int minDistanceBetweenBuildings = 5;
	public static int minDistanceBetweenVillages = 500;
	public static int minDistanceBetweenVillagesAndLoneBuildings = 250;
	public static int minDistanceBetweenLoneBuildings = 500;
	public static int forcePreload = 0;
	public static int spawnProtectionRadius = 250;
	public static int VillageRadius=60;
	public static int VillagersNamesDistance=6;
	public static boolean BuildVillagePaths=true;

	public static int RaidingRate=20;
	public static int keyVillageList;
	public static int keyInfoPanelList;


	public static int keyAggressiveEscorts;

	private static FileWriter writer;

	private static String loadedLanguage=null;
	public static int textureSize=-1;
	public static boolean dynamictextures = true;

	public static String customTexture=null;

	public static Language mainLanguage=null;
	public static Language fallbackLanguage=null;
	public static Language serverMainLanguage=null;
	public static Language serverFallbackLanguage=null;
	public static HashMap<String,Language> loadedLanguages=new HashMap<String,Language>();


	private static void applyLanguage() {
		nameItems();

		ModLoader.addLocalization("entity.ml_GenericVillager.name", MLN.string("other.malevillager"));
		ModLoader.addLocalization("entity.ml_GenericAsimmFemale.name", MLN.string("other.femalevillager"));
		ModLoader.addLocalization("entity.ml_GenericSimmFemale.name", MLN.string("other.femalevillager"));


		if (!Mill.proxy.isTrueServer()) {

			LanguageRegistry.reloadLanguageTable();

			final InvItem iv=new InvItem(Mill.summoningWand.itemID,1);

			MLN.major(null, "Language loaded: "+effective_language+". Wand name: "+MLN.string("item.villagewand")
					+" Wand invitem name: "+iv.getName());

			if (MLN.generateBuildingRes) {//Doing it now because it requires item names
				MLN.major(null, "Generating building res file.");
				BuildingPlan.generateBuildingRes();
				MLN.major(null, "Generated building res file.");
			}
		}
	}

	public static void debug(Object obj, String s) {
		writeText("DEBUG: "+obj+": "+s);
	}

	public static void error(Object obj,String s) {
		if (DEV) {
			writeText("    !====================================!");
		}
		writeText("ERROR: "+obj+": "+s);
		if (DEV) {
			writeText("     ==================================== ");
		}
	}

	private static String fillInName(String s) {

		if (s==null)
			return "";

		final EntityPlayer player=Mill.proxy.getTheSinglePlayer();

		if (player!=null)
			return s.replaceAll("\\$name", player.username);
		else
			return s;
	}

	public static Vector<Vector<String>> getHelp(int id) {
		if (mainLanguage.help.containsKey(id))
			return mainLanguage.help.get(id);
		if (fallbackLanguage.help.containsKey(id))
			return fallbackLanguage.help.get(id);
		return null;
	}

	public static Vector<String> getHoFData() {

		final Vector<String> hofData=new Vector<String>();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(new File(Mill.proxy.getBaseDir(),"hof.txt"));

			String line;

			while ((line=reader.readLine()) != null) {
				line=line.trim();
				if ((line.length() > 0) && !line.startsWith("//")) {
					hofData.add(line);
				}
			}

		} catch (final Exception e) {
			MLN.printException("Error when loading HoF: ", e);
		}

		return hofData;
	}

	public static String getLargeLockedChestTexture() {

		if (MLN.dynamictextures && (textureSize>=64))
			return "/graphics/item/ML_lockedlargechest_64.png";

		return "/graphics/item/ML_lockedlargechest.png";
	}

	public static String getLockedChestTexture() {

		if (MLN.dynamictextures && (textureSize>=64))
			return "/graphics/item/ML_lockedchest_64.png";

		return "/graphics/item/ML_lockedchest.png";
	}

	public static Vector<Vector<String>> getParchment(int id) {
		if (mainLanguage.texts.containsKey(id))
			return mainLanguage.texts.get(id);
		if (fallbackLanguage.texts.containsKey(id))
			return fallbackLanguage.texts.get(id);
		return null;
	}

	public static String getRawString(String key,boolean mustFind) {
		return getRawString(key,mustFind,true,true);
	}


	public static String getRawString(String key,boolean mustFind, boolean main, boolean fallback) {

		if (main && (mainLanguage!=null) && mainLanguage.strings.containsKey(key))
			return mainLanguage.strings.get(key);

		if (main && (serverMainLanguage!=null) && serverMainLanguage.strings.containsKey(key))
			return serverMainLanguage.strings.get(key);

		if (fallback && (fallbackLanguage!=null) && fallbackLanguage.strings.containsKey(key))
			return fallbackLanguage.strings.get(key);

		if (fallback && (serverFallbackLanguage!=null) && serverFallbackLanguage.strings.containsKey(key))
			return serverFallbackLanguage.strings.get(key);

		if (mustFind && (MLN.Translation>=MLN.MAJOR)) {
			MLN.error(null, "String not found: "+key);
		}

		if (mustFind)
			return key;
		else
			return null;
	}

	public static String getRawStringFallbackOnly(String key,boolean mustFind) {
		return getRawString(key,mustFind,false,true);
	}

	public static String getRawStringMainOnly(String key,boolean mustFind) {
		return getRawString(key,mustFind,true,false);
	}

	public static String getTextSuffix() {

		if (textureSize==-1) {
			Mill.proxy.testTextureSize();
		}

		if (customTexture!=null)
			return "";

		if (MLN.dynamictextures && (textureSize>=64))
			return "_64";

		return "";
	}

	public static boolean isTranslationLoaded() {
		return (mainLanguage!=null);
	}

	public static void loadConfig() {

		Mill.proxy.loadKeyDefaultSettings();

		final boolean mainConfig=readConfigFile(Mill.proxy.getConfigFile());

		if (mainConfig==false) {
			System.err.println("ERREUR: Impossible de trouver le fichier de configuration "+Mill.proxy.getConfigFile().getAbsolutePath()+". V\u00e9rifiez que le dossier millenaire est bien dans minecraft/mods/");
			System.err.println("ERROR: Could not find the config file at "+Mill.proxy.getConfigFile().getAbsolutePath()+". Check that the millenaire directory is in minecraft/mods/");

			if (!Mill.proxy.isTrueServer()) {
				Mill.displayMillenaireLocationError=true;
			}

			Mill.startupError=true;
			return;
		}

		readConfigFile(Mill.proxy.getCustomConfigFile());

		if (logfile) {
			try {
				writer=new FileWriter(Mill.proxy.getLogFile(),true);
			} catch (final IOException e) {
				writer=null;
			}
		} else {
			writer=null;
		}

		Mill.loadingDirs.add(Mill.proxy.getBaseDir());

		final File modDirs=new File(Mill.proxy.getCustomDir(),"mods");

		modDirs.mkdirs();

		String mods="";

		for (final File mod : modDirs.listFiles()) {

			if (mod.isDirectory() && !mod.isHidden()) {
				Mill.loadingDirs.add(mod);
				mods+=mod.getName()+" ";
			}

		}

		if (mods.length()==0) {
			writeText("Starting new session.");
		} else {
			writeText("Starting new session. Mods: "+mods);
		}
	}

	public static void loadLanguages(String minecraftLanguage) {

		if (!MLN.main_language.equals("")) {
			effective_language=MLN.main_language;//if the main language is set, override the game language
		} else if (minecraftLanguage!=null) {
			effective_language=minecraftLanguage;
		} else {
			effective_language="fr";
		}

		if ((loadedLanguage!=null) && loadedLanguage.equals(effective_language))//language already loaded
			return;

		MLN.major(null, "Loading language: "+effective_language);

		loadedLanguage=effective_language;

		final Vector<File> languageDirs=new Vector<File>();

		for (final File dir : Mill.loadingDirs) {
			final File languageDir=new File(dir,"languages");

			if (languageDir.exists()) {
				languageDirs.add(languageDir);
			}
		}

		mainLanguage=new Language(MLN.effective_language,false);
		mainLanguage.loadFromDisk(languageDirs);

		if (MLN.main_language.equals(MLN.fallback_language)) {
			fallbackLanguage=mainLanguage;
		} else {
			fallbackLanguage=new Language(MLN.fallback_language,false);
			fallbackLanguage.loadFromDisk(languageDirs);
		}

		if (MLN.loadAllLanguages) {
			final File mainDir=languageDirs.firstElement();

			for (final File lang : mainDir.listFiles()) {
				if (lang.isDirectory() && !lang.isHidden()) {
					final String key=lang.getName().toLowerCase();
					if (!loadedLanguages.containsKey(key)) {
						final Language l=new Language(key,false);
						l.loadFromDisk(languageDirs);
					}
				}
			}
		}

		for (final Culture c : Culture.vectorCultures) {
			c.loadLanguages(languageDirs, effective_language, fallback_language);
		}

		VillageType.loadLevelNames();

		applyLanguage();

		if (MLN.generateTranslationGap) {

			final HashMap<String,Integer> percentageComplete=new HashMap<String,Integer>();

			final ArrayList<Language> list=new ArrayList<Language>(loadedLanguages.values());

			for (final Language l : list) {

				String refLanguage;

				if (l.language.startsWith("fr")) {
					refLanguage="en";
				} else {
					refLanguage="fr";
				}

				Language ref=null;

				if (loadedLanguages.containsKey(refLanguage)) {
					ref=loadedLanguages.get(refLanguage);
				} else {
					ref=new Language(refLanguage,false);
					ref.loadFromDisk(languageDirs);
				}

				l.compareWithLanguage(percentageComplete,ref);
			}

			final File translationGapDir=new File(Mill.proxy.getBaseDir(),"Translation gaps");

			if (!translationGapDir.exists()) {
				translationGapDir.mkdirs();
			}

			final File file = new File(translationGapDir,"Results.txt");

			if (file.exists()) {
				file.delete();
			}

			BufferedWriter writer;
			try {
				writer = MillCommonUtilities.getWriter(file);

				for (final String key : percentageComplete.keySet()) {
					writer.write(key+": "+percentageComplete.get(key)+"%"+EOL);
				}
				writer.close();
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	public static void major(Object obj, String s) {
		writeText("MAJOR: "+obj+": "+s);
	}

	public static void minor(Object obj, String s) {
		writeText("MINOR: "+obj+": "+s);
	}

	private static void nameItems() {
		ModLoader.addName(Mill.lockedChest, MLN.string("item.building"));
		ModLoader.addName(Mill.denier, MLN.string("item.denier"));
		ModLoader.addName(Mill.denier_or, MLN.string("item.denieror"));
		ModLoader.addName(Mill.denier_argent, MLN.string("item.denierargent"));

		ModLoader.addName(Mill.calva, MLN.string("item.calva"));
		ModLoader.addName(Mill.tripes, MLN.string("item.tripes"));
		ModLoader.addName(Mill.boudin, MLN.string("item.boudin"));

		ModLoader.addName(Mill.ciderapple, MLN.string("item.ciderapple"));
		ModLoader.addName(Mill.cider, MLN.string("item.cider"));
		ModLoader.addName(Mill.summoningWand, MLN.string("item.villagewand"));
		ModLoader.addName(Mill.negationWand, MLN.string("item.negationwand"));
		ModLoader.addName(Mill.normanPickaxe, MLN.string("item.normanPickaxe"));
		ModLoader.addName(Mill.normanAxe, MLN.string("item.normanAxe"));
		ModLoader.addName(Mill.normanShovel, MLN.string("item.normanShovel"));
		ModLoader.addName(Mill.normanHoe, MLN.string("item.normanHoe"));
		ModLoader.addName(Mill.normanBroadsword, MLN.string("item.normanBroadsword"));
		ModLoader.addName(Mill.normanHelmet, MLN.string("item.normanHelmet"));
		ModLoader.addName(Mill.normanPlate, MLN.string("item.normanPlate"));
		ModLoader.addName(Mill.normanLegs, MLN.string("item.normanLegs"));
		ModLoader.addName(Mill.normanBoots, MLN.string("item.normanBoots"));
		ModLoader.addName(Mill.parchmentVillagers, MLN.string("item.normanvillagers"));
		ModLoader.addName(Mill.parchmentBuildings, MLN.string("item.normanbuildings"));
		ModLoader.addName(Mill.parchmentItems, MLN.string("item.normanitems"));
		ModLoader.addName(Mill.parchmentComplete, MLN.string("item.normanfull"));
		ModLoader.addName(Mill.tapestry, MLN.string("item.tapestry"));
		ModLoader.addName(Mill.vishnu_amulet, MLN.string("item.vishnu_amulet"));
		ModLoader.addName(Mill.alchemist_amulet, MLN.string("item.alchemist_amulet"));
		ModLoader.addName(Mill.yddrasil_amulet, MLN.string("item.yddrasil_amulet"));
		ModLoader.addName(Mill.skoll_hati_amulet, MLN.string("item.skoll_hati_amulet"));
		ModLoader.addName(Mill.parchmentVillageScroll, MLN.string("item.villagescroll"));
		ModLoader.addName(Mill.rice, MLN.string("item.rice"));
		ModLoader.addName(Mill.turmeric, MLN.string("item.turmeric"));
		ModLoader.addName(Mill.vegcurry,  MLN.string("item.vegcurry"));
		ModLoader.addName(Mill.chickencurry,  MLN.string("item.chickencurry"));
		ModLoader.addName(Mill.brickmould,  MLN.string("item.brickmould"));
		ModLoader.addName(Mill.rasgulla,  MLN.string("item.rasgulla"));
		ModLoader.addName(Mill.indianstatue, MLN.string("item.indianstatue"));

		ModLoader.addName(Mill.parchmentIndianVillagers, MLN.string("item.indianvillagers"));
		ModLoader.addName(Mill.parchmentIndianBuildings, MLN.string("item.indianbuildings"));
		ModLoader.addName(Mill.parchmentIndianItems, MLN.string("item.indianitems"));
		ModLoader.addName(Mill.parchmentIndianComplete, MLN.string("item.indianfull"));

		ModLoader.addName(new ItemStack(Mill.wood_decoration, 1, 0), MLN.string("item.plaintimber"));
		ModLoader.addName(new ItemStack(Mill.wood_decoration, 1, 1), MLN.string("item.crosstimber"));
		ModLoader.addName(new ItemStack(Mill.wood_decoration, 1, 2), MLN.string("item.thatched"));
		ModLoader.addName(new ItemStack(Mill.wood_decoration, 1, 3), MLN.string("item.emptysilkwormblock"));
		ModLoader.addName(new ItemStack(Mill.wood_decoration, 1, 4), MLN.string("item.fullsilkwormblock"));
		ModLoader.addName(new ItemStack(Mill.earth_decoration, 1, 0),MLN.string("item.wetbrick"));
		ModLoader.addName(new ItemStack(Mill.earth_decoration, 1, 1),MLN.string("item.dirtwall"));
		ModLoader.addName(new ItemStack(Mill.stone_decoration, 1, 0), MLN.string("item.cookedbrick"));
		ModLoader.addName(new ItemStack(Mill.stone_decoration, 1, 1), MLN.string("item.mudbrick"));
		ModLoader.addName(new ItemStack(Mill.stone_decoration, 1, 2), MLN.string("item.mayangold"));
		ModLoader.addName(new ItemStack(Mill.stone_decoration, 1, 3), MLN.string("item.alchimistexplosive"));

		ModLoader.addName(new ItemStack(Mill.path, 1, 0), MLN.string("item.pathdirt"));
		ModLoader.addName(new ItemStack(Mill.path, 1, 1), MLN.string("item.pathgravel"));
		ModLoader.addName(new ItemStack(Mill.path, 1, 2), MLN.string("item.pathslabs"));
		ModLoader.addName(new ItemStack(Mill.path, 1, 3), MLN.string("item.pathsandstone"));
		ModLoader.addName(new ItemStack(Mill.path, 1, 4), MLN.string("item.pathochretiles"));
		ModLoader.addName(new ItemStack(Mill.path, 1, 5), MLN.string("item.pathgravelslabs"));
		
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 0), MLN.string("item.pathdirt"));
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 1), MLN.string("item.pathgravel"));
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 2), MLN.string("item.pathslabs"));
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 3), MLN.string("item.pathsandstone"));
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 4), MLN.string("item.pathochretiles"));
		ModLoader.addName(new ItemStack(Mill.pathSlab, 1, 5), MLN.string("item.pathgravelslabs"));

		ModLoader.addName(Mill.mayanstatue, MLN.string("item.mayanstatue"));
		ModLoader.addName(Mill.maize, MLN.string("item.maize"));
		ModLoader.addName(Mill.wah, MLN.string("item.wah"));
		ModLoader.addName(Mill.masa, MLN.string("item.masa"));
		ModLoader.addName(Mill.unknownPowder, MLN.string("item.unknownpowder"));

		ModLoader.addName(Mill.parchmentMayanVillagers, MLN.string("item.mayanvillagers"));
		ModLoader.addName(Mill.parchmentMayanBuildings, MLN.string("item.mayanbuildings"));
		ModLoader.addName(Mill.parchmentMayanItems, MLN.string("item.mayanitems"));
		ModLoader.addName(Mill.parchmentMayanComplete, MLN.string("item.mayanfull"));
		ModLoader.addName(Mill.parchmentSadhu, MLN.string("item.parchmentsadhu"));

		ModLoader.addName(new ItemStack(Mill.paperWall, 1, 0), MLN.string("item.paperwall"));
		ModLoader.addName(Mill.udon, MLN.string("item.udon"));

		ModLoader.addName(Mill.tachiSword, MLN.string("item.tachisword"));

		ModLoader.addName(Mill.obsidianFlake, MLN.string("item.obsidianFlake"));
		ModLoader.addName(Mill.mayanPickaxe, MLN.string("item.mayanPickaxe"));
		ModLoader.addName(Mill.mayanAxe, MLN.string("item.mayanAxe"));
		ModLoader.addName(Mill.mayanShovel, MLN.string("item.mayanShovel"));
		ModLoader.addName(Mill.mayanHoe, MLN.string("item.mayanHoe"));
		ModLoader.addName(Mill.mayanMace, MLN.string("item.mayanMace"));

		ModLoader.addName(Mill.yumiBow, MLN.string("item.yumibow"));

		ModLoader.addName(Mill.japaneseWarriorBlueLegs, MLN.string("item.japaneseWarriorBlueLegs"));
		ModLoader.addName(Mill.japaneseWarriorBlueHelmet, MLN.string("item.japaneseWarriorBlueHelmet"));
		ModLoader.addName(Mill.japaneseWarriorBluePlate, MLN.string("item.japaneseWarriorBluePlate"));
		ModLoader.addName(Mill.japaneseWarriorBlueBoots, MLN.string("item.japaneseWarriorBlueBoots"));

		ModLoader.addName(Mill.japaneseWarriorRedLegs, MLN.string("item.japaneseWarriorRedLegs"));
		ModLoader.addName(Mill.japaneseWarriorRedHelmet, MLN.string("item.japaneseWarriorRedHelmet"));
		ModLoader.addName(Mill.japaneseWarriorRedPlate, MLN.string("item.japaneseWarriorRedPlate"));
		ModLoader.addName(Mill.japaneseWarriorRedBoots, MLN.string("item.japaneseWarriorRedBoots"));

		ModLoader.addName(Mill.japaneseGuardLegs, MLN.string("item.japaneseGuardLegs"));
		ModLoader.addName(Mill.japaneseGuardHelmet, MLN.string("item.japaneseGuardHelmet"));
		ModLoader.addName(Mill.japaneseGuardPlate, MLN.string("item.japaneseGuardPlate"));
		ModLoader.addName(Mill.japaneseGuardBoots, MLN.string("item.japaneseGuardBoots"));

		ModLoader.addName(Mill.parchmentJapaneseVillagers, MLN.string("item.japanesevillagers"));
		ModLoader.addName(Mill.parchmentJapaneseBuildings, MLN.string("item.japanesebuildings"));
		ModLoader.addName(Mill.parchmentJapaneseItems, MLN.string("item.japaneseitems"));
		ModLoader.addName(Mill.parchmentJapaneseComplete, MLN.string("item.japanesefull"));


		ModLoader.addName(Mill.grapes, MLN.string("item.grapes"));
		ModLoader.addName(Mill.wineFancy, MLN.string("item.wine"));
		ModLoader.addName(Mill.silk, MLN.string("item.silk"));
		ModLoader.addName(Mill.byzantineiconsmall, MLN.string("item.byzantineiconsmall"));
		ModLoader.addName(Mill.byzantineiconmedium, MLN.string("item.byzantineiconmedium"));
		ModLoader.addName(Mill.byzantineiconlarge, MLN.string("item.byzantineiconlarge"));
		ModLoader.addName(Mill.byzantine_tiles, MLN.string("item.byzantinebrick"));
		ModLoader.addName(Mill.byzantine_tile_slab, MLN.string("item.byzantineslab"));
		ModLoader.addName(Mill.byzantine_stone_tiles, MLN.string("item.byzantinemixedbrick"));

		ModLoader.addName(Mill.byzantineLegs, MLN.string("item.byzantineLegs"));
		ModLoader.addName(Mill.byzantineHelmet, MLN.string("item.byzantineHelmet"));
		ModLoader.addName(Mill.byzantinePlate, MLN.string("item.byzantinePlate"));
		ModLoader.addName(Mill.byzantineBoots, MLN.string("item.byzantineBoots"));


		ModLoader.addName(Mill.byzantineMace, MLN.string("item.byzantineMace"));


		ModLoader.addName(new ItemStack(Mill.clothes, 1, 0), MLN.string("item.clothes_byz_wool"));
		ModLoader.addName(new ItemStack(Mill.clothes, 1, 1), MLN.string("item.clothes_byz_silk"));
		ModLoader.addName(Mill.wineBasic, MLN.string("item.wineBasic"));
		ModLoader.addName(Mill.lambRaw, MLN.string("item.lambRaw"));
		ModLoader.addName(Mill.lambCooked, MLN.string("item.lambCooked"));
		ModLoader.addName(Mill.feta, MLN.string("item.feta"));
		ModLoader.addName(Mill.souvlaki, MLN.string("item.souvlaki"));
		ModLoader.addName(Mill.purse, MLN.string("item.purse"));

	}

	private static String now() {
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public static void printException(Exception e) {
		printException(null,e);
	}

	public static void printException(String s,Exception e) {
		if (DEV) {
			writeText("    !====================================!");
		}

		if (s==null) {
			writeText("Exception, printing stack:");
		} else {
			writeText(s);
		}

		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();

		writeText(sw.toString());
		if (DEV) {
			writeText("     ==================================== ");
		}
	}

	public static String questString(String key,boolean required) {
		return questString(key,true,true,required);
	}

	public static String questString(String key,boolean main,boolean fallback,boolean required) {
		key=key.toLowerCase();
		if (main && (mainLanguage!=null) && mainLanguage.questStrings.containsKey(key))
			return mainLanguage.questStrings.get(key);

		if (main && (serverMainLanguage!=null) && serverMainLanguage.questStrings.containsKey(key))
			return serverMainLanguage.questStrings.get(key);

		if (fallback && (fallbackLanguage!=null) && fallbackLanguage.questStrings.containsKey(key))
			return fallbackLanguage.questStrings.get(key);

		if (fallback && (serverFallbackLanguage!=null) && serverFallbackLanguage.questStrings.containsKey(key))
			return serverFallbackLanguage.questStrings.get(key);

		if (required)
			return key;

		return null;
	}

	public static String questStringFallbackOnly(String key,boolean required) {
		return questString(key,false,true,required);
	}

	public static String questStringMainOnly(String key,boolean required) {
		return questString(key,true,false,required);
	}

	private static boolean readConfigFile(File file) {

		if (!file.exists())
			return false;

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final String[] temp=line.split("=");
					if (temp.length==2) {

						final String key=temp[0];
						final String value=temp[1];
						if (key.equalsIgnoreCase("devmode")) {
							DEV=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("console")) {
							console=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("logfile")) {
							logfile=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("village_list_key")) {
							final int keyCode=Mill.proxy.loadKeySetting(value.toUpperCase());
							if (keyCode>0) {
								MLN.keyVillageList=keyCode;
							} else {
								MLN.error(null, "Invalid key setting on line: "+line);
							}
						} else if (key.equalsIgnoreCase("quest_list_key")) {
							final int keyCode=Mill.proxy.loadKeySetting(value.toUpperCase());
							if (keyCode>0) {
								MLN.keyInfoPanelList=keyCode;
							} else {
								MLN.error(null, "Invalid key setting on line: "+line);
							}
						} else if (key.equalsIgnoreCase("logfile")) {
							logfile=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("infinite_amulet")) {
							infiniteAmulet=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("language_learning")) {
							languageLearning=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("stop_default_villages")) {
							stopDefaultVillages=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("load_all_languages")) {
							loadAllLanguages=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("se_indicators")) {
							seIndicators=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("generate_colour_chart")) {
							generateColourSheet=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("generate_building_res")) {
							generateBuildingRes=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("generate_translation_gap")) {
							generateTranslationGap=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("generate_goods_list")) {
							generateGoodsList=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("generate_villages")) {
							generateVillagesDefault=Boolean.parseBoolean(value);
							generateVillages=generateVillagesDefault;
						} else if (key.equalsIgnoreCase("generate_lone_buildings")) {
							generateLoneBuildings=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("display_start")) {
							displayStart=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("display_names")) {
							displayNames=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("language")) {
							main_language=value.toLowerCase();
						} else if (key.equalsIgnoreCase("fallback_language")) {
							fallback_language=value.toLowerCase();
						} else if (key.equalsIgnoreCase("forbidden_blocks")) {
							for (final String id : value.split(",")) {
								if (Integer.parseInt(id)>0) {
									forbiddenBlocks.add(Integer.parseInt(id));
								} else {
									System.out.println("Could not read forbidden ID: "+id);
								}
							}
						} else if (key.equalsIgnoreCase("log.TileEntityBuilding")) {
							TileEntityBuilding=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.WorldGeneration")) {
							WorldGeneration=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.FarmerAI")) {
							FarmerAI=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Diplomacy")) {
							Diplomacy=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.WifeAI")) {
							WifeAI=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Villager")) {
							LogVillager=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Quest")) {
							LogQuest=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Pathing")) {
							Pathing=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Connections")) {
							Connections=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.getPath")) {
							getPath=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Lumberman")) {
							Lumberman=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.BuildingPlan")) {
							LogBuildingPlan=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.GeneralAI")) {
							GeneralAI=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Selling")) {
							Selling=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Hybernation")) {
							Hybernation=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Other")) {
							Other=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Children")) {
							Children=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Performance")) {
							Performance=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.CattleFarmer")) {
							CattleFarmer=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Miner")) {
							Miner=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Village")) {
							LogVillage=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.WorldInfo")) {
							WorldInfo=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Pujas")) {
							Pujas=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.villagerspawn")) {
							VillagerSpawn=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.villagepaths")) {
							VillagePaths=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Network")) {
							Network=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Merchant")) {
							Merchant=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Culture")) {
							LogCulture=readLogLevel(value);
						} else if (key.equalsIgnoreCase("log.Translation")) {
							Translation=readLogLevel(value);
						} else if (key.equalsIgnoreCase("min_village_distance")) {
							minDistanceBetweenVillages=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("min_village_lonebuilding_distance")) {
							minDistanceBetweenVillagesAndLoneBuildings=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("min_lonebuilding_distance")) {
							minDistanceBetweenLoneBuildings=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("force_preload_radius")) {
							forcePreload=Integer.parseInt(value)/16;//set in blocks but converted to chunks
						} else if (key.equalsIgnoreCase("village_radius")) {
							VillageRadius=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("spawn_protection_radius")) {
							spawnProtectionRadius=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("min_distance_between_buildings")) {
							minDistanceBetweenBuildings=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_building_id")) {
							blockBuildingId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_panel_id")) {
							blockPanelId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_wood_id")) {
							blockWoodId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_earth_id")) {
							blockEarthId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_stone_id")) {
							blockStoneId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_crops_id")) {
							blockCropsId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_panes_id")) {
							blockPanesId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_byzantine_brick_id")) {
							blockByzantineBrickId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_byzantine_slab_id")) {
							blockByzantineSlabId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_byzantine_mixedbrick_id")) {
							blockByzantineMixedId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_path_id")) {
							blockPathId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("block_path_slab_id")) {
							blockPathSlabId=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("item_range_start")) {
							itemRangeStart=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("keep_active_radius")) {
							KeepActiveRadius=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("background_radius")) {
							BackgroundRadius=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("max_children_number")) {
							maxChildrenNumber=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("villagers_names_distance")) {
							VillagersNamesDistance=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("village_paths")) {
							BuildVillagePaths=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("raiding_rate")) {
							RaidingRate=Integer.parseInt(value);
						} else if (key.equalsIgnoreCase("sprites_path")) {
							customTexture=value.trim();
						} else if (key.equalsIgnoreCase("dynamic_textures")) {
							dynamictextures=Boolean.parseBoolean(value);
						} else if (key.equalsIgnoreCase("quest_biome_forest")) {
							questBiomeForest=value.trim().toLowerCase();
						} else if (key.equalsIgnoreCase("quest_biome_desert")) {
							questBiomeDesert=value.trim().toLowerCase();
						} else if (key.equalsIgnoreCase("quest_biome_mountain")) {
							questBiomeMountain=value.trim().toLowerCase();
						}
					}
				}
			}
			reader.close();

			System.out.println("Read config in "+file.getName()+". Logging: "+console+"/"+logfile);

			return true;

		} catch (final Exception e) {
			MLN.printException(e);
			return false;
		}
	}

	protected static int readLogLevel(String s) {
		if (s.equalsIgnoreCase("major"))
			return 1;
		if (s.equalsIgnoreCase("minor"))
			return 2;
		if (s.equalsIgnoreCase("debug"))
			return 3;
		return 0;
	}

	public static String removeAccent(String source) {
		return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
	}

	public static String string(String key) {

		if (!isTranslationLoaded())//can happen if called before languages are loaded
			return "";

		key=key.toLowerCase();

		return fillInName(getRawString(key,true));
	}

	public static String string(String key,String... values) {

		String s=string(key);

		int pos=0;
		for (final String value : values) {
			if (value!=null) {
				s=s.replaceAll("<"+pos+">", value);
			} else {
				s=s.replaceAll("<"+pos+">", "");
			}
			pos++;
		}

		return s;
	}

	//first val is the key
	public static String string(String[] values) {

		if (values.length==0)
			return "";

		String s=unknownString(values[0]);

		int pos=-1;
		for (final String value : values) {
			if (pos>-1) {
				if (value!=null) {
					s=s.replaceAll("<"+pos+">", unknownString(value));
				} else {
					s=s.replaceAll("<"+pos+">", "");
				}
			}
			pos++;
		}

		return fillInName(s);
	}

	public static void temp(Object obj, String s) {
		if (MLN.DEV) {
			writeText("TEMP: "+obj+": "+s);
		}
	}

	public static String unknownString(String key) {

		if (key==null)
			return "";

		if (!isTranslationLoaded())//can happen if called before languages are loaded
			return key;

		if (key.startsWith("_item:")) {
			final int id=Integer.parseInt(key.split(":")[1]);
			final int meta=Integer.parseInt(key.split(":")[2]);
			final InvItem item=new InvItem(id,meta);
			return item.getName();
		}

		if (key.startsWith("_buildingGame:")) {
			final String cultureKey=key.split(":")[1];
			final Culture culture=Culture.getCultureByName(cultureKey);
			if (culture!=null) {
				final String buildingKey=key.split(":")[2];
				final BuildingPlanSet set=culture.getBuildingPlanSet(buildingKey);
				if (set!=null) {
					final int variation=Integer.parseInt(key.split(":")[3]);
					if (variation<set.plans.size()) {
						final int level=Integer.parseInt(key.split(":")[4]);
						if (level<set.plans.get(variation).length) {
							final BuildingPlan plan=set.plans.get(variation)[level];
							return plan.getGameName();
						}
					}
				}
			}
		}

		final String rawKey=getRawString(key,false);

		if  (rawKey!=null)
			return fillInName(rawKey);

		return key;
	}

	public static void warning(Object obj,String s) {
		if (DEV) {
			writeText("    !=============!");
		}
		writeText("WARNING: "+obj+": "+s);
		if (DEV) {
			writeText("     =============");
		}
	}

	private static void writeText(String s) {
		if (console) {
			FMLLog.info(Mill.proxy.logPrefix()+removeAccent(s));
		}

		if (writer != null) {
			try {
				writer.write(Mill.versionNumber+" "+now()+" "+s+EOL);
				writer.flush();
			} catch (final IOException e) {
				System.out.println("Failed to write line to log file.");
			}
		}

	}

}
