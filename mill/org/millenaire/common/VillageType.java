package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.construction.BuildingPlanSet;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.WeightedChoice;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;
import org.millenaire.common.network.StreamReadWrite;

public class VillageType implements WeightedChoice {

	private static class VillageFileFiler implements FilenameFilter {

		String end;

		public VillageFileFiler(final String ending) {
			end = ending;
		}

		@Override
		public boolean accept(final File file, final String name) {

			if (!name.endsWith(end)) {
				return false;
			}

			if (name.startsWith(".")) {
				return false;
			}

			return true;
		}
	}

	public static String[] levelNames;

	public static final String HAMEAU = "hameau";

	public static void loadLevelNames() {
		levelNames = new String[] { MLN.string("ui.buildingscentre"),
				MLN.string("ui.buildingsstarting"),
				MLN.string("ui.buildingsplayer"),
				MLN.string("ui.buildingskey"),
				MLN.string("ui.buildingssecondary"),
				MLN.string("ui.buildingsextra") };
	}

	public static List<VillageType> loadLoneBuildings(
			final List<File> culturesDirs, final Culture culture) {

		final List<File> dirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "lonebuildings");

			if (dir.exists()) {
				dirs.add(dir);
			}
		}

		final File dircusto = new File(new File(new File(
				Mill.proxy.getCustomDir(), "cultures"), culture.key),
				"custom lonebuildings");

		if (dircusto.exists()) {
			dirs.add(dircusto);
		}

		final List<VillageType> v = new ArrayList<VillageType>();

		final VillageFileFiler filer = new VillageFileFiler(".txt");

		for (final File villageDir : dirs) {

			villageDir.mkdirs();
			for (final File file : villageDir.listFiles(filer)) {

				try {

					if (MLN.LogVillage >= MLN.MAJOR) {
						MLN.major(
								file,
								"Loading lone building: "
										+ file.getAbsolutePath());
					}

					final VillageType village = new VillageType(culture, file,
							true);
					village.lonebuilding = true;

					v.remove(village);
					v.add(village);

				} catch (final MillenaireException e) {
					MLN.error(null, e.getMessage());
				} catch (final Exception e) {
					MLN.printException(e);
				}

			}

		}

		return v;
	}

	public static List<VillageType> loadVillages(final List<File> culturesDirs,
			final Culture culture) {

		final List<File> dirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "villages");

			if (dir.exists()) {
				dirs.add(dir);
			}
		}

		final File dircusto = new File(new File(new File(
				Mill.proxy.getCustomDir(), "cultures"), culture.key),
				"custom villages");

		if (dircusto.exists()) {
			dirs.add(dircusto);
		}

		final List<VillageType> v = new ArrayList<VillageType>();

		final VillageFileFiler filer = new VillageFileFiler(".txt");

		for (final File villageDir : dirs) {

			villageDir.mkdirs();
			for (final File file : villageDir.listFiles(filer)) {

				try {

					if (MLN.LogVillage >= MLN.MAJOR) {
						MLN.major(file,
								"Loading village: " + file.getAbsolutePath());
					}

					final VillageType village = new VillageType(culture, file,
							false);

					v.remove(village);
					v.add(village);

				} catch (final MillenaireException e) {
					MLN.error(null, e.getMessage());
				} catch (final Exception e) {
					MLN.printException(e);
				}

			}

		}

		return v;
	}

	public static List<VillageType> spawnableVillages(final EntityPlayer player) {

		final List<VillageType> villages = new ArrayList<VillageType>();

		final UserProfile profile = Mill.getMillWorld(player.worldObj)
				.getProfile(player.getDisplayName());

		for (final Culture culture : Culture.ListCultures) {
			for (final VillageType village : culture.listVillageTypes) {
				if (village.spawnable
						&& village.playerControlled
						&& (MLN.DEV || profile
								.isTagSet(MillWorld.CULTURE_CONTROL
										+ village.culture.key))) {
					villages.add(village);
				}
			}
			for (final VillageType village : culture.listVillageTypes) {
				if (village.spawnable && !village.playerControlled) {
					villages.add(village);
				}
			}
			for (final VillageType village : culture.listLoneBuildingTypes) {
				if (village.spawnable
						&& !(!MLN.DEV && village.playerControlled && !profile
								.isTagSet(MillWorld.CULTURE_CONTROL
										+ village.culture.key))) {
					villages.add(village);
				}
			}
		}

		return villages;
	}

	public String name = null, key = null, type = null;
	public int weight, radius = MLN.VillageRadius, max = 0;
	public BuildingPlanSet centreBuilding = null;
	public List<BuildingPlanSet> startBuildings = new ArrayList<BuildingPlanSet>();
	public List<BuildingPlanSet> playerBuildings = new ArrayList<BuildingPlanSet>();
	public List<BuildingPlanSet> coreBuildings = new ArrayList<BuildingPlanSet>();
	public List<BuildingPlanSet> secondaryBuildings = new ArrayList<BuildingPlanSet>();
	public List<BuildingPlanSet> extraBuildings = new ArrayList<BuildingPlanSet>();
	public List<BuildingPlanSet> excludedBuildings = new ArrayList<BuildingPlanSet>();
	public List<String> hamlets = new ArrayList<String>();
	public List<String> qualifiers = new ArrayList<String>();
	public HashMap<InvItem, Integer> sellingPrices = new HashMap<InvItem, Integer>();

	public HashMap<InvItem, Integer> buyingPrices = new HashMap<InvItem, Integer>();
	public List<InvItem> pathMaterial = new ArrayList<InvItem>();
	public Culture culture;

	public List<String> biomes = new ArrayList<String>();
	List<String> requiredTags = new ArrayList<String>();
	List<String> forbiddenTags = new ArrayList<String>();
	public String nameList = null;
	public String hillQualifier = null;
	public String mountainQualifier = null;
	public String desertQualifier = null;
	public String forestQualifier = null;

	public String lavaQualifier = null;
	public String lakeQualifier = null;
	public String oceanQualifier = null;
	public boolean lonebuilding = false;

	public boolean keyLonebuilding = false;

	public String keyLoneBuildingGenerateTag = null;
	public boolean spawnable = true;

	public boolean carriesRaid = false;
	public boolean playerControlled = false;

	public boolean generateOnServer = true;

	public int minDistanceFromSpawn = 0;

	public boolean generatedForPlayer = false;

	public VillageType(final Culture c, final File file, final boolean lone)
			throws Exception {
		lonebuilding = lone;
		spawnable = !lonebuilding;
		culture = c;

		key = file.getName().split("\\.")[0];

		if (lonebuilding) {
			nameList = null;
		} else {
			nameList = "villages";
		}

		final BufferedReader reader = MillCommonUtilities.getReader(file);
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.trim().length() > 0 && !line.trim().startsWith("//")) {
				final String[] temp = line.trim().split("\\:");
				if (temp.length == 2) {

					final String paramkey = temp[0].trim();
					final String value = temp[1].trim();
					if (paramkey.equalsIgnoreCase("name")) {
						name = value;
					} else if (paramkey.equalsIgnoreCase("spawnable")) {
						spawnable = Boolean.parseBoolean(value);
					} else if (paramkey.equalsIgnoreCase("generateonserver")) {
						generateOnServer = Boolean.parseBoolean(value);
					} else if (paramkey.equalsIgnoreCase("generateforplayer")) {
						generatedForPlayer = Boolean.parseBoolean(value);
					} else if (paramkey.equalsIgnoreCase("carriesraid")) {
						carriesRaid = Boolean.parseBoolean(value);
					} else if (paramkey.equalsIgnoreCase("keyLoneBuilding")) {
						keyLonebuilding = Boolean.parseBoolean(value);
					} else if (paramkey
							.equalsIgnoreCase("keyLoneBuildingGenerateTag")) {
						keyLoneBuildingGenerateTag = value;
					} else if (paramkey.equalsIgnoreCase("playerControlled")) {
						playerControlled = Boolean.parseBoolean(value);
					} else if (paramkey.equalsIgnoreCase("weight")) {
						weight = Integer.parseInt(value);
					} else if (paramkey.equalsIgnoreCase("max")) {
						max = Integer.parseInt(value);
					} else if (paramkey.equalsIgnoreCase("radius")) {
						radius = Integer.parseInt(value);
					} else if (paramkey
							.equalsIgnoreCase("minDistanceFromSpawn")) {
						minDistanceFromSpawn = Integer.parseInt(value);
					} else if (paramkey.equalsIgnoreCase("qualifier")) {
						qualifiers.add(value);
					} else if (paramkey.equalsIgnoreCase("hameau")) {
						hamlets.add(value);
					} else if (paramkey.equalsIgnoreCase("type")) {
						type = value;
					} else if (paramkey.equalsIgnoreCase("nameList")) {
						nameList = value;
					} else if (paramkey.equalsIgnoreCase("biome")) {
						biomes.add(value.toLowerCase());
					} else if (paramkey.equalsIgnoreCase("requiredtag")) {
						requiredTags.add(value);
					} else if (paramkey.equalsIgnoreCase("forbiddentag")) {
						forbiddenTags.add(value);
					} else if (paramkey.equalsIgnoreCase("hillQualifier")) {
						hillQualifier = value;
					} else if (paramkey.equalsIgnoreCase("mountainQualifier")) {
						mountainQualifier = value;
					} else if (paramkey.equalsIgnoreCase("desertQualifier")) {
						desertQualifier = value;
					} else if (paramkey.equalsIgnoreCase("forestQualifier")) {
						forestQualifier = value;
					} else if (paramkey.equalsIgnoreCase("lavaQualifier")) {
						lavaQualifier = value;
					} else if (paramkey.equalsIgnoreCase("lakeQualifier")) {
						lakeQualifier = value;
					} else if (paramkey.equalsIgnoreCase("oceanQualifier")) {
						oceanQualifier = value;
					} else if (paramkey.equalsIgnoreCase("pathMaterial")) {
						if (Goods.goodsName.containsKey(value.toLowerCase())) {
							pathMaterial.add(Goods.goodsName.get(value
									.toLowerCase()));
						} else {
							MLN.error(this, "When loading village type " + key
									+ " could not recognise path material: "
									+ value);
						}
					} else if (paramkey.equalsIgnoreCase("centre")) {
						if (culture.getBuildingPlanSet(value) != null) {
							centreBuilding = culture.getBuildingPlanSet(value);
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading centre building: "
										+ value);
							}
						} else {
							throw new MillenaireException(
									"When loading village type "
											+ key
											+ " could not find centre building type "
											+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("start")) {
						if (culture.getBuildingPlanSet(value) != null) {
							startBuildings.add(culture
									.getBuildingPlanSet(value));
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading start building: "
										+ value);
							}
						} else {
							MLN.error(this, "When loading village type " + key
									+ " could not find start building type "
									+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("player")) {
						if (culture.getBuildingPlanSet(value) != null) {
							playerBuildings.add(culture
									.getBuildingPlanSet(value));
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading player building: "
										+ value);
							}
						} else {
							MLN.error(this, "When loading village type " + key
									+ " could not find player building type "
									+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("core")) {
						if (culture.getBuildingPlanSet(value) != null) {
							coreBuildings
									.add(culture.getBuildingPlanSet(value));
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading core building: "
										+ value);
							}
						} else {
							MLN.error(this, "When loading village type " + key
									+ " could not find core building type "
									+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("secondary")) {
						if (culture.getBuildingPlanSet(value) != null) {
							secondaryBuildings.add(culture
									.getBuildingPlanSet(value));
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading secondary building: "
										+ value);
							}
						} else {
							MLN.error(
									this,
									"When loading village type "
											+ key
											+ " could not find secondary building type "
											+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("never")) {
						if (culture.getBuildingPlanSet(value) != null) {
							excludedBuildings.add(culture
									.getBuildingPlanSet(value));
							if (MLN.LogVillage >= MLN.MINOR) {
								MLN.minor(this, "Loading excluded building: "
										+ value);
							}
						} else {
							MLN.error(this, "When loading village type " + key
									+ " could not find excluded building type "
									+ value + ".");
						}
					} else if (paramkey.equalsIgnoreCase("sellingPrice")) {

						final String goodstr = value.split(",")[0]
								.toLowerCase();

						if (Goods.goodsName.containsKey(goodstr)) {

							final InvItem item = Goods.goodsName.get(goodstr);

							int price = 0;
							try {
								final String[] pricestr = value.split(",")[1]
										.split("/");
								if (pricestr.length == 1) {
									price = Integer.parseInt(pricestr[0]);
								} else if (pricestr.length == 2) {
									price = Integer.parseInt(pricestr[0]) * 64
											+ Integer.parseInt(pricestr[1]);
								} else if (pricestr.length == 3) {
									price = Integer.parseInt(pricestr[0]) * 64
											* 64
											+ Integer.parseInt(pricestr[1])
											* 64
											+ Integer.parseInt(pricestr[2]);
								} else {
									MLN.error(this,
											"Could not parse the price in line: "
													+ line);
								}
							} catch (final Exception e) {
								MLN.error(this,
										"Exception when parsing the price in line: "
												+ line);
							}
							if (price > 0) {
								sellingPrices.put(item, price);
							}
						} else {
							MLN.error(this, "Could not find the good in line: "
									+ line);
						}
					} else if (paramkey.equalsIgnoreCase("buyingPrice")) {

						final String goodstr = value.split(",")[0]
								.toLowerCase();

						if (Goods.goodsName.containsKey(goodstr)) {

							final InvItem item = Goods.goodsName.get(goodstr);
							try {
								int price = 0;

								final String[] pricestr = value.split(",")[1]
										.split("/");
								if (pricestr.length == 1) {
									price = Integer.parseInt(pricestr[0]);
								} else if (pricestr.length == 2) {
									price = Integer.parseInt(pricestr[0]) * 64
											+ Integer.parseInt(pricestr[1]);
								} else if (pricestr.length == 3) {
									price = Integer.parseInt(pricestr[0]) * 64
											* 64
											+ Integer.parseInt(pricestr[1])
											* 64
											+ Integer.parseInt(pricestr[2]);
								} else {
									MLN.error(this,
											"Could not parse the price in line: "
													+ line);
								}

								if (price > 0) {
									buyingPrices.put(item, price);
								}
							} catch (final Exception e) {
								MLN.error(this,
										"Exception when parsing the price in line: "
												+ line);
							}
						} else {
							MLN.error(this, "Could not find the good in line: "
									+ line);
						}

					} else {
						MLN.error(this, "Could not recognise parameter "
								+ paramkey + ": " + line);
					}

				} else {
					MLN.error(this, "Could not understand line: " + line);
				}
			}
		}

		if (name == null) {
			throw new MillenaireException("No name found for village: " + key);
		}
		if (centreBuilding == null) {
			throw new MillenaireException(
					"No central building found for village: " + key);
		}

		for (final BuildingPlanSet set : culture.ListPlanSets) {

			if (!excludedBuildings.contains(set)) {

				int nb = 0;

				for (final BuildingPlanSet aset : startBuildings) {
					if (aset == set) {
						nb++;
					}
				}

				for (final BuildingPlanSet aset : coreBuildings) {
					if (aset == set) {
						nb++;
					}
				}

				for (final BuildingPlanSet aset : secondaryBuildings) {
					if (aset == set) {
						nb++;
					}
				}

				for (int i = nb; i < set.max; i++) {
					extraBuildings.add(set);
				}
			}
		}

		if (pathMaterial.size() == 0) {
			pathMaterial.add(Goods.goodsName.get("pathgravel"));
		}

		if (MLN.LogVillage >= MLN.MAJOR) {
			MLN.major(this, "Loaded village type " + name + ". NameList: "
					+ nameList);
		}

	}

	public VillageType(final Culture c, final String key, final boolean lone) {
		this.key = key;
		this.culture = c;
		lonebuilding = lone;
	}

	@Override
	public boolean equals(final Object obj) {

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof VillageType)) {
			return false;
		}

		final VillageType v = (VillageType) obj;

		return v.culture == culture && v.key.equals(key);

	}

	public List<List<BuildingProject>> getBuildingProjects() {

		final List<BuildingProject> centre = new ArrayList<BuildingProject>();
		centre.add(centreBuilding.getBuildingProject());

		final List<BuildingProject> start = new ArrayList<BuildingProject>();
		for (final BuildingPlanSet set : startBuildings) {
			start.add(set.getBuildingProject());
		}

		final List<BuildingProject> players = new ArrayList<BuildingProject>();
		if (!playerControlled) {
			for (final BuildingPlanSet set : playerBuildings) {
				players.add(set.getBuildingProject());
			}
		}

		final List<BuildingProject> core = new ArrayList<BuildingProject>();
		if (!playerControlled) {
			for (final BuildingPlanSet set : coreBuildings) {
				core.add(set.getBuildingProject());
			}
		}

		final List<BuildingProject> secondary = new ArrayList<BuildingProject>();
		if (!playerControlled) {
			for (final BuildingPlanSet set : secondaryBuildings) {
				secondary.add(set.getBuildingProject());
			}
		}

		final List<BuildingProject> extra = new ArrayList<BuildingProject>();
		if (!playerControlled
				&& (type == null || !type.equalsIgnoreCase(HAMEAU))
				&& !lonebuilding) {
			for (final BuildingPlanSet set : extraBuildings) {
				extra.add(set.getBuildingProject());
			}
		}

		final List<List<BuildingProject>> v = new ArrayList<List<BuildingProject>>();
		v.add(centre);
		v.add(start);
		v.add(players);
		v.add(core);
		v.add(secondary);
		v.add(extra);

		return v;
	}

	@Override
	public int getChoiceWeight(final EntityPlayer player) {
		if (isKeyLoneBuildingForGeneration(player)) {
			return 10000;
		}

		return weight;
	}

	@Override
	public int hashCode() {
		return culture.hashCode() + key.hashCode();
	}

	public boolean isKeyLoneBuildingForGeneration(final EntityPlayer player) {
		if (keyLonebuilding) {
			return true;
		}

		if (player != null) {
			final UserProfile profile = Mill.getMillWorld(player.worldObj)
					.getProfile(player.getDisplayName());

			if (keyLoneBuildingGenerateTag != null
					&& profile.isTagSet(keyLoneBuildingGenerateTag)) {
				return true;
			}
		}

		return false;
	}

	public boolean isValidForGeneration(final MillWorld mw,
			final EntityPlayer player,
			final HashMap<String, Integer> nbVillages, final Point pos,
			final String biome, final boolean keyLoneBuildingsOnly) {

		if (!generateOnServer && Mill.proxy.isTrueServer()) {
			return false;
		}

		if (minDistanceFromSpawn > 0
				&& pos.horizontalDistanceTo(mw.world.getSpawnPoint()) <= minDistanceFromSpawn) {
			return false;
		}

		for (final String tag : requiredTags) {
			if (!mw.isGlobalTagSet(tag)) {
				return false;
			}
		}

		for (final String tag : forbiddenTags) {
			if (mw.isGlobalTagSet(tag)) {
				return false;
			}
		}

		if (keyLoneBuildingsOnly && !isKeyLoneBuildingForGeneration(player)) {
			return false;
		}

		if (!biomes.contains(biome)) {
			return false;
		}

		if (!isKeyLoneBuildingForGeneration(player)) {
			if (max != 0 && nbVillages.containsKey(key)
					&& nbVillages.get(key) >= max) {
				return false;
			}
		} else {
			boolean existingOneInRange = false;

			for (int i = 0; i < mw.loneBuildingsList.pos.size(); i++) {
				if (mw.loneBuildingsList.types.get(i).equals(key)) {
					if (pos.horizontalDistanceTo(mw.loneBuildingsList.pos
							.get(i)) < 2000) {
						existingOneInRange = true;
					}
				}
			}

			if (existingOneInRange) {
				return false;
			}
		}

		return true;
	}

	public void readVillageTypeInfoPacket(final ByteBufInputStream ds)
			throws IOException {
		playerControlled = ds.readBoolean();
		spawnable = ds.readBoolean();
		name = StreamReadWrite.readNullableString(ds);
		type = StreamReadWrite.readNullableString(ds);
		radius = ds.read();
	}

	public void sendVillageTypePacket(final EntityPlayer player) {

	}

	@Override
	public String toString() {
		return key;
	}

	public void writeVillageTypeInfo(final DataOutput data) throws IOException {
		data.writeUTF(key);
		data.writeBoolean(playerControlled);
		data.writeBoolean(spawnable);
		StreamReadWrite.writeNullableString(name, data);
		StreamReadWrite.writeNullableString(type, data);
		data.write(radius);
	}

}
