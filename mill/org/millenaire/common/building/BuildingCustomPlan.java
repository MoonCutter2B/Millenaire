package org.millenaire.common.building;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BuildingCustomPlan {

	public static enum TypeRes {

		CHEST("chest"), CRAFT("craft"), SIGN("sign"), FIELD("field");

		public final String key;

		TypeRes(final String key) {
			this.key = key;
		}
	}

	public Map<TypeRes, List<Point>> findResources(final World world,
			final Point pos) {

		final Map<TypeRes, List<Point>> resources = new HashMap<TypeRes, List<Point>>();

		int currentRadius = 0;

		while (currentRadius < radius) {

			int y = pos.getiY() - heightRadius + 1;

			while (y < pos.getiY() + heightRadius + 1) {
				int x, z;

				// First side of square: -x to +x with -z
				z = pos.getiZ() - currentRadius;
				for (x = pos.getiX() - currentRadius; x <= pos.getiX()
						+ currentRadius; x++) {
					handlePoint(x, y, z,  world, resources);
				}

				// Second side of square: -z to +z with -x
				x = pos.getiX() - currentRadius;
				for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ()
						+ currentRadius - 1; z++) {
					handlePoint(x, y, z,  world, resources);
				}

				// Third side of square: -x to +x with +z
				z = pos.getiZ() + currentRadius;
				for (x = pos.getiX() - currentRadius; x <= pos.getiX()
						+ currentRadius; x++) {
					handlePoint(x, y, z,  world, resources);
				}

				// Fourth side of square: -z to +z with +x
				x = pos.getiX() + currentRadius;
				for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ()
						+ currentRadius - 1; z++) {
					handlePoint(x, y, z, world, resources);
				}

				y++;
			}

			currentRadius++;
		}
		return resources;
	}

	private void handlePoint(final int x, final int y, final int z,
			final World world,
			final Map<TypeRes, List<Point>> resources) {

		final Point p = new Point(x, y, z);

		final TypeRes res = identifyRes(world, p);

		/*
		 * If the resource is needed for this type of custom building we add it
		 * within limit of max number
		 */
		if (res != null && maxResources.containsKey(res)) {
			if (resources.containsKey(res)
					&& resources.get(res).size() < maxResources
							.get(res)) {
				resources.get(res).add(p);
			} else if (!resources.containsKey(res)) {
				final List<Point> points = new ArrayList<Point>();
				points.add(p);
				resources.put(res, points);
			}
		}
	}

	private static TypeRes identifyRes(final World world, final Point p) {

		final Block b = p.getBlock(world);

		if (b.equals(Blocks.chest)) {
			return TypeRes.CHEST;
		}

		if (b.equals(Blocks.crafting_table)) {
			return TypeRes.CRAFT;
		}

		if (b.equals(Blocks.wall_sign)) {
			return TypeRes.SIGN;
		}
		if (b.equals(Blocks.farmland)) {
			return TypeRes.FIELD;
		}

		return null;
	}

	/**
	 * Loads all custom buildings for a culture
	 * 
	 * @param culturesDirs
	 * @param culture
	 * @return
	 */
	public static Map<String, BuildingCustomPlan> loadCustomBuildings(
			final List<File> culturesDirs, final Culture culture) {

		final Map<String, BuildingCustomPlan> buildingCustoms = new HashMap<String, BuildingCustomPlan>();

		final List<File> dirs = new ArrayList<File>();

		for (final File cultureDir : culturesDirs) {

			final File customDir = new File(cultureDir, "custombuildings");

			if (customDir.exists()) {
				dirs.add(customDir);
			}
		}

		final BuildingFileFiler textFiles = new BuildingFileFiler(".txt");

		for (int i = 0; i < dirs.size(); i++) {

			for (final File file : dirs.get(i).listFiles(textFiles)) {

				try {
					if (MLN.LogBuildingPlan >= MLN.MAJOR) {
						MLN.major(file, "Loaded custom building");
					}

					final BuildingCustomPlan buildingCustom = new BuildingCustomPlan(
							file, culture);

					buildingCustoms.put(buildingCustom.buildingKey,
							buildingCustom);

				} catch (final Exception e) {
					MLN.printException(
							"Error when loading " + file.getAbsolutePath(), e);
				}
			}

		}

		return buildingCustoms;

	}

	public final Culture culture;

	public String nativeName, shop = null, buildingKey, gameNameKey = null;
	public final Map<String, String> names = new HashMap<String, String>();
	public List<String> maleResident = new ArrayList<String>();
	public List<String> femaleResident = new ArrayList<String>();
	public int priorityMoveIn = 1;
	public int radius = 5, heightRadius = 4;
	
	public List<String> tags = new ArrayList<String>();
	
	public String cropType = null;

	public Map<TypeRes, Integer> minResources = new HashMap<TypeRes, Integer>();
	public Map<TypeRes, Integer> maxResources = new HashMap<TypeRes, Integer>();

	/**
	 * Light constructor used to create mock custom buildings from server data
	 * (for use for custom buildings not present on client)
	 * 
	 * @param culture
	 * @param key
	 */
	public BuildingCustomPlan(final Culture culture, final String key) {
		this.culture = culture;
		buildingKey = key;
	}

	/**
	 * Regular constructor, used to load the building from a text file
	 * 
	 * @param file
	 * @param culture
	 * @throws IOException
	 */
	public BuildingCustomPlan(final File file, final Culture culture)
			throws IOException {
		this.culture = culture;
		this.buildingKey = file.getName().split("\\.")[0];

		final BufferedReader reader = MillCommonUtilities.getReader(file);

		final String line = reader.readLine();

		readConfigLine(line);

		if (MLN.LogBuildingPlan >= MLN.MAJOR) {
			MLN.major(this, "Loaded custom building " + buildingKey
					+ nativeName + " pop: " + maleResident + "/"
					+ femaleResident);
		}
	}

	/**
	 * Name in native and game language (if readable by player) Ex: Puit (Well)
	 * 
	 * @return
	 */
	public String getFullDisplayName() {
		String name = nativeName;
		if (getGameName() != null && getGameName().length() > 0) {
			name += " (" + getGameName() + ")";
		}
		return name;
	}

	/**
	 * Name in player's language, if readable by player
	 * 
	 * @return
	 */
	public String getGameName() {
		if (culture.canReadBuildingNames()) {
			return culture.getCustomBuildingGameName(this);
		}
		return "";
	}

	/**
	 * Read the parameters from the text file (one line only for custom
	 * buildings)
	 * 
	 * @param line
	 */
	private void readConfigLine(final String line) {

		final String[] configs = line.split(";", -1);

		for (final String config : configs) {
			if (config.split(":").length == 2) {
				final String key = config.split(":")[0].toLowerCase();
				final String value = config.split(":")[1];

				if (key.equalsIgnoreCase("moveinpriority")) {
					priorityMoveIn = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("radius")) {
					radius = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("heightradius")) {
					heightRadius = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("native")) {
					nativeName = value;
				} else if (key.equalsIgnoreCase("gameNameKey")) {
					gameNameKey = value;
				} else if (key.equalsIgnoreCase("cropType")) {
					cropType = value;
				} else if (key.startsWith("name_")) {
					names.put(key, value);
				} else if (key.equalsIgnoreCase("male")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())
							|| MillVillager.oldVillagers.containsKey(value
									.toLowerCase())) {
						maleResident.add(value.toLowerCase());
					} else {
						MLN.error(this,
								"Attempted to load unknown male villager: "
										+ value);
					}
				} else if (key.equalsIgnoreCase("female")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())
							|| MillVillager.oldVillagers.containsKey(value
									.toLowerCase())) {
						femaleResident.add(value.toLowerCase());
					} else {
						MLN.error(this,
								"Attempted to load unknown female villager: "
										+ value);
					}
				} else if (key.equalsIgnoreCase("shop")) {
					if (culture.shopBuys.containsKey(value)
							|| culture.shopSells.containsKey(value)
							|| culture.shopBuysOptional.containsKey(value)) {
						shop = value;
					} else {
						MLN.error(this, "Undefined shop type: " + value);
					}
				} else if (key.equalsIgnoreCase("tag")) {
					tags.add(value.toLowerCase());
				} else {

					boolean found = false;

					for (final TypeRes typeRes : TypeRes.values()) {
						if (typeRes.key.equals(key)) {

							try {
								found = true;

								if (value.contains("-")) {// Range: min-max
									minResources.put(typeRes, Integer
											.parseInt(value.split("-")[0]));
									maxResources.put(typeRes, Integer
											.parseInt(value.split("-")[1]));
								} else {// Single value
									minResources.put(typeRes,
											Integer.parseInt(value));
									maxResources.put(typeRes,
											Integer.parseInt(value));
								}
							} catch (final Exception e) {
								MLN.printException(
										"Exception while parsing res "
												+ typeRes.key
												+ " in custom file "
												+ buildingKey + " of culture "
												+ culture.key + ":", e);
							}
						}
					}

					if (!found) {
						MLN.error(this, "Could not recognise key on line: "
								+ config);
					}
				}
			}
		}

	}

	/**
	 * Look for the resource blocks in the location and register them with the
	 * building
	 * 
	 * @param building
	 * @param location
	 */
	public void registerResources(final Building building, final BuildingLocation location) {
		final Map<TypeRes, List<Point>> resources = findResources(building.worldObj, location.pos);

		building.getResManager().setSleepingPos(location.pos.getAbove());

		if (resources.containsKey(TypeRes.CHEST)) {
			for (final Point chestP : resources.get(TypeRes.CHEST)) {
				final int meta = chestP.getMeta(building.worldObj);

				//No notifications as will cause issues with large chests
				chestP.setBlock(building.worldObj, Mill.lockedChest, meta,
						false, false);

				building.getResManager().chests.add(chestP);
			}
		}

		if (resources.containsKey(TypeRes.CRAFT)
				&& resources.get(TypeRes.CRAFT).size() > 0) {
			location.craftingPos = resources.get(TypeRes.CRAFT).get(0);
		}

		if (resources.containsKey(TypeRes.SIGN)) {
			for (final Point signP : resources.get(TypeRes.SIGN)) {
				final int meta = signP.getMeta(building.worldObj);

				signP.setBlock(building.worldObj, Mill.panel, meta, true, false);

				building.getResManager().signs.add(signP);
			}
		}
		
		if (cropType != null  && resources.containsKey(TypeRes.FIELD)) {
			for (final Point p : resources.get(TypeRes.FIELD)) {
				building.getResManager().addSoilPoint(cropType, p);
			}
		}
	}
}
