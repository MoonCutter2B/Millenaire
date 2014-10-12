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
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BuildingCustomPlan {

	public static enum TypeRes {

		CHEST("chest"), CRAFT("craft"), SIGN("sign"), FIELD("field"), SPAWN("spawn"), SAPLING("sapling"), STALL("stall"), MINING("mining"), FURNACE("furnace"), MUDBRICK("mudbrick"), SUGAR("sugar"), FISHING(
				"fishing"), SILK("silk"), SQUID("squid"), CACAO("cacao");

		public final String key;

		TypeRes(final String key) {
			this.key = key;
		}
	}

	/**
	 * Loads all custom buildings for a culture
	 * 
	 * @param culturesDirs
	 * @param culture
	 * @return
	 */
	public static Map<String, BuildingCustomPlan> loadCustomBuildings(final List<File> culturesDirs, final Culture culture) {

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

					final BuildingCustomPlan buildingCustom = new BuildingCustomPlan(file, culture);

					buildingCustoms.put(buildingCustom.buildingKey, buildingCustom);

				} catch (final Exception e) {
					MLN.printException("Error when loading " + file.getAbsolutePath(), e);
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
	public String spawnType = null;

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
	public BuildingCustomPlan(final File file, final Culture culture) throws IOException {
		this.culture = culture;
		this.buildingKey = file.getName().split("\\.")[0];

		final BufferedReader reader = MillCommonUtilities.getReader(file);

		final String line = reader.readLine();

		readConfigLine(line);

		if (MLN.LogBuildingPlan >= MLN.MAJOR) {
			MLN.major(this, "Loaded custom building " + buildingKey + nativeName + " pop: " + maleResident + "/" + femaleResident);
		}
	}

	/**
	 * Calculates borders of locations based on resources found
	 * 
	 * @param location
	 * @param resources
	 */
	private void adjustLocationSize(final BuildingLocation location, final Map<TypeRes, List<Point>> resources) {
		int startX = location.pos.getiX();
		int startZ = location.pos.getiZ();
		int endX = location.pos.getiX();
		int endZ = location.pos.getiZ();

		for (final TypeRes type : resources.keySet()) {
			for (final Point p : resources.get(type)) {
				if (startX >= p.getiX()) {
					startX = p.getiX();
				}
				if (startZ >= p.getiZ()) {
					startZ = p.getiZ();
				}
				if (endX <= p.getiX()) {
					endX = p.getiX();
				}
				if (endZ <= p.getiZ()) {
					endZ = p.getiZ();
				}
			}
		}

		location.minx = startX - 1;
		location.maxx = endX + 1;
		location.minz = startZ - 1;
		location.maxz = endZ + 1;

		location.length = location.maxx - location.minx + 1;
		location.width = location.maxz - location.minz + 1;

		location.computeMargins();
	}

	public Map<TypeRes, List<Point>> findResources(final World world, final Point pos) {

		final Map<TypeRes, List<Point>> resources = new HashMap<TypeRes, List<Point>>();

		int currentRadius = 0;

		MLN.temp(null, "Looking for resources around point: " + pos);

		while (currentRadius < radius) {

			int y = pos.getiY() - heightRadius + 1;

			while (y < pos.getiY() + heightRadius + 1) {
				int x, z;

				// First side of square: -x to +x with -z
				z = pos.getiZ() - currentRadius;
				for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; x++) {
					handlePoint(x, y, z, world, resources);
				}

				// Second side of square: -z to +z with -x
				x = pos.getiX() - currentRadius;
				for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; z++) {
					handlePoint(x, y, z, world, resources);
				}

				// Third side of square: -x to +x with +z
				z = pos.getiZ() + currentRadius;
				for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; x++) {
					handlePoint(x, y, z, world, resources);
				}

				// Fourth side of square: -z to +z with +x
				x = pos.getiX() + currentRadius;
				for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; z++) {
					handlePoint(x, y, z, world, resources);
				}

				y++;
			}

			currentRadius++;
		}
		return resources;
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

	private void handlePoint(final int x, final int y, final int z, final World world, final Map<TypeRes, List<Point>> resources) {

		final Point p = new Point(x, y, z);

		final TypeRes res = identifyRes(world, p);

		/*
		 * If the resource is needed for this type of custom building we add it
		 * within limit of max number
		 */
		if (res != null && maxResources.containsKey(res)) {
			if (resources.containsKey(res) && resources.get(res).size() < maxResources.get(res)) {
				resources.get(res).add(p);
			} else if (!resources.containsKey(res)) {
				final List<Point> points = new ArrayList<Point>();
				points.add(p);
				resources.put(res, points);
			}
		}
	}

	private TypeRes identifyRes(final World world, final Point p) {

		final Block b = p.getBlock(world);
		final int meta = p.getMeta(world);

		if (b.equals(Blocks.chest) || b.equals(Mill.lockedChest)) {
			return TypeRes.CHEST;
		}

		if (b.equals(Blocks.crafting_table)) {
			return TypeRes.CRAFT;
		}

		if (b.equals(Blocks.wall_sign) || b.equals(Mill.panel)) {
			return TypeRes.SIGN;
		}
		if (b.equals(Blocks.farmland)) {
			return TypeRes.FIELD;
		}
		if (b.equals(Blocks.hay_block)) {
			return TypeRes.SPAWN;
		}
		// Sapling is either a sapling or wood over dirt
		if (b.equals(Blocks.sapling) || (b.equals(Blocks.log) || b.equals(Blocks.log2)) && p.getBelow().getBlock(world).equals(Blocks.dirt)) {
			return TypeRes.SAPLING;
		}
		if (b.equals(Blocks.wool) && p.getMeta(world) == 4) {// yellow
			return TypeRes.STALL;
		}
		// Mining blocks must have at least one side open
		if (b.equals(Blocks.stone) || b.equals(Blocks.sandstone) || b.equals(Blocks.sand) || b.equals(Blocks.gravel) || b.equals(Blocks.clay)) {

			if (p.getAbove().getBlock(world).equals(Blocks.air) || p.getRelative(1, 0, 0).getBlock(world).equals(Blocks.air) || p.getRelative(-1, 0, 0).getBlock(world).equals(Blocks.air)
					|| p.getRelative(0, 0, 1).getBlock(world).equals(Blocks.air) || p.getRelative(0, 0, -1).getBlock(world).equals(Blocks.air)) {
				return TypeRes.MINING;
			}
		}
		if (b.equals(Blocks.furnace)) {
			return TypeRes.FURNACE;
		}

		if (b.equals(Mill.earth_decoration) && meta == 0) {
			return TypeRes.MUDBRICK;
		}

		if (b.equals(Blocks.reeds) && !p.getBelow().getBlock(world).equals(Blocks.reeds)) {
			return TypeRes.SUGAR;
		}
		if (b.equals(Blocks.wool) && p.getMeta(world) == 3) {// light blue
			return TypeRes.FISHING;
		}
		if (b.equals(Blocks.wool) && p.getMeta(world) == 0) {// white
			return TypeRes.SILK;
		}
		if (b.equals(Blocks.wool) && p.getMeta(world) == 11) {// blue
			final Point[] neighbours = new Point[] { p.getRelative(1, 0, 0), p.getRelative(-1, 0, 0), p.getRelative(0, 0, 1), p.getRelative(0, 0, -1) };
			boolean waterAround = true;

			for (final Point p2 : neighbours) {
				if (!p2.getBlock(world).equals(Blocks.water)) {
					waterAround = false;
				}
			}

			if (waterAround) {
				return TypeRes.SQUID;
			}
		}
		if (b.equals(Blocks.cocoa)) {
			return TypeRes.CACAO;
		}
		return null;
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
				} else if (key.equalsIgnoreCase("spawnType")) {
					spawnType = value;
				} else if (key.startsWith("name_")) {
					names.put(key, value);
				} else if (key.equalsIgnoreCase("male")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())) {
						maleResident.add(value.toLowerCase());
					} else {
						MLN.error(this, "Attempted to load unknown male villager: " + value);
					}
				} else if (key.equalsIgnoreCase("female")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())) {
						femaleResident.add(value.toLowerCase());
					} else {
						MLN.error(this, "Attempted to load unknown female villager: " + value);
					}
				} else if (key.equalsIgnoreCase("shop")) {
					if (culture.shopBuys.containsKey(value) || culture.shopSells.containsKey(value) || culture.shopBuysOptional.containsKey(value)) {
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
									minResources.put(typeRes, Integer.parseInt(value.split("-")[0]));
									maxResources.put(typeRes, Integer.parseInt(value.split("-")[1]));
								} else {// Single value
									minResources.put(typeRes, Integer.parseInt(value));
									maxResources.put(typeRes, Integer.parseInt(value));
								}
							} catch (final Exception e) {
								MLN.printException("Exception while parsing res " + typeRes.key + " in custom file " + buildingKey + " of culture " + culture.key + ":", e);
							}
						}
					}

					if (!found) {
						MLN.error(this, "Could not recognise key on line: " + config);
					}
				}
			}
		}
	}

	/**
	 * Look for the resource blocks in the location and register them with the
	 * building. Also updates location to fit size to resources found.
	 */
	public void registerResources(final Building building, final BuildingLocation location) {
		final Map<TypeRes, List<Point>> resources = findResources(building.worldObj, location.pos);

		adjustLocationSize(location, resources);

		building.getResManager().setSleepingPos(location.pos.getAbove());
		location.sleepingPos = location.pos.getAbove();

		if (resources.containsKey(TypeRes.CHEST)) {
			building.getResManager().chests.clear();
			for (final Point chestP : resources.get(TypeRes.CHEST)) {

				// No notifications as will cause issues with large chests
				if (chestP.getBlock(building.worldObj).equals(Blocks.chest)) {
					final int meta = chestP.getMeta(building.worldObj);

					chestP.setBlock(building.worldObj, Mill.lockedChest, meta, false, false);
				}

				building.getResManager().chests.add(chestP);
			}
		}

		if (resources.containsKey(TypeRes.CRAFT) && resources.get(TypeRes.CRAFT).size() > 0) {
			location.craftingPos = resources.get(TypeRes.CRAFT).get(0);
			building.getResManager().setCraftingPos(resources.get(TypeRes.CRAFT).get(0));
		}

		registerSigns(building, resources);

		if (cropType != null && resources.containsKey(TypeRes.FIELD)) {
			building.getResManager().soils.clear();
			building.getResManager().soilTypes.clear();
			for (final Point p : resources.get(TypeRes.FIELD)) {
				building.getResManager().addSoilPoint(cropType, p);
			}
		}
		if (spawnType != null && resources.containsKey(TypeRes.SPAWN)) {
			building.getResManager().spawns.clear();
			building.getResManager().spawnTypes.clear();
			for (final Point p : resources.get(TypeRes.SPAWN)) {
				p.setBlock(building.worldObj, Blocks.air, 0, true, false);
				building.getResManager().addSpawnPoint(spawnType, p);
			}
		}
		if (resources.containsKey(TypeRes.SAPLING)) {
			building.getResManager().woodspawn.clear();
			for (final Point p : resources.get(TypeRes.SAPLING)) {
				building.getResManager().woodspawn.add(p);
			}
		}
		if (resources.containsKey(TypeRes.STALL)) {
			building.getResManager().stalls.clear();
			for (final Point p : resources.get(TypeRes.STALL)) {
				p.setBlock(building.worldObj, Blocks.air, 0, true, false);
				building.getResManager().stalls.add(p);
			}
		}
		if (resources.containsKey(TypeRes.MINING)) {
			building.getResManager().sources.clear();
			building.getResManager().sourceTypes.clear();
			for (final Point p : resources.get(TypeRes.MINING)) {
				building.getResManager().addSourcePoint(p.getBlock(building.worldObj), p);
			}
		}
		if (resources.containsKey(TypeRes.FURNACE)) {
			building.getResManager().furnaces.clear();
			for (final Point p : resources.get(TypeRes.FURNACE)) {
				building.getResManager().furnaces.add(p);
			}
		}
		if (resources.containsKey(TypeRes.MUDBRICK)) {
			building.getResManager().brickspot.clear();
			for (final Point p : resources.get(TypeRes.MUDBRICK)) {
				building.getResManager().brickspot.add(p);
			}
		}
		if (resources.containsKey(TypeRes.SUGAR)) {
			building.getResManager().sugarcanesoils.clear();
			for (final Point p : resources.get(TypeRes.SUGAR)) {
				building.getResManager().sugarcanesoils.add(p);
			}
		}
		if (resources.containsKey(TypeRes.FISHING)) {
			building.getResManager().fishingspots.clear();
			for (final Point p : resources.get(TypeRes.FISHING)) {
				p.setBlock(building.worldObj, Blocks.air, 0, true, false);
				building.getResManager().fishingspots.add(p);
			}
		}
		if (resources.containsKey(TypeRes.SILK)) {
			building.getResManager().silkwormblock.clear();
			for (final Point p : resources.get(TypeRes.SILK)) {
				p.setBlock(building.worldObj, Mill.wood_decoration, 3, true, false);
				building.getResManager().silkwormblock.add(p);
			}
		}
		if (resources.containsKey(TypeRes.SQUID)) {
			int squidSpawnPos = -1;
			for (int i = 0; i < building.getResManager().spawnTypes.size(); i++) {
				if (building.getResManager().spawnTypes.get(i).equals("Squid")) {
					squidSpawnPos = i;
				}
			}
			if (squidSpawnPos > -1) {
				building.getResManager().spawns.get(squidSpawnPos).clear();
			}

			for (final Point p : resources.get(TypeRes.SQUID)) {
				p.setBlock(building.worldObj, Blocks.water, 0, true, false);
				building.getResManager().addSpawnPoint("Squid", p);
			}
		}
		if (resources.containsKey(TypeRes.CACAO)) {
			int cocoaSoilPos = -1;
			for (int i = 0; i < building.getResManager().soilTypes.size(); i++) {
				if (building.getResManager().soilTypes.get(i).equals(Mill.CROP_CACAO)) {
					cocoaSoilPos = i;
				}
			}
			if (cocoaSoilPos > -1) {
				building.getResManager().soils.get(cocoaSoilPos).clear();
			}

			for (final Point p : resources.get(TypeRes.CACAO)) {
				building.getResManager().addSoilPoint(Mill.CROP_CACAO, p);
			}
		}
	}

	/**
	 * Special method to register sign as this is more complex, due to need to
	 * handle sign positions
	 * 
	 * @param building
	 * @param resources
	 */
	private void registerSigns(final Building building, final Map<TypeRes, List<Point>> resources) {

		building.getResManager().signs.clear();

		final Map<Integer, Point> signsWithPos = new HashMap<Integer, Point>();
		final List<Point> otherSigns = new ArrayList<Point>();
		if (resources.containsKey(TypeRes.SIGN)) {
			for (final Point signP : resources.get(TypeRes.SIGN)) {
				// Looking up sign entity to see if a pos is written on the
				// first line
				final TileEntitySign signEntity = signP.getSign(building.worldObj);

				int signPos = -1;

				if (signEntity != null) {
					try {
						// -1 since array is 0-based but players will put
						// 1-based numbers
						signPos = Integer.parseInt(signEntity.signText[0]) - 1;
					} catch (final Exception e) {

					}
				}

				if (signPos > -1 && !signsWithPos.containsKey(signPos)) {
					signsWithPos.put(signPos, signP);
				} else {
					otherSigns.add(signP);
				}

				if (signP.getBlock(building.worldObj).equals(Blocks.wall_sign)) {
					final int meta = signP.getMeta(building.worldObj);
					signP.setBlock(building.worldObj, Mill.panel, meta, true, false);
				}
			}
		}

		// Total sign number that of all signs we found regardless of pos
		final int signNumber = signsWithPos.size() + otherSigns.size();

		for (int i = 0; i < signNumber; i++) {
			building.getResManager().signs.add(null);
		}

		for (final Integer pos : signsWithPos.keySet()) {
			if (pos < signNumber) {
				building.getResManager().signs.set(pos, signsWithPos.get(pos));
			} else {
				otherSigns.add(signsWithPos.get(pos));// Invalid pos, putting
														// sign in regular list
			}
		}

		// Fill-up with the non-pos signs
		int posInOthers = 0;
		for (int i = 0; i < signNumber; i++) {
			if (building.getResManager().signs.get(i) == null) {
				building.getResManager().signs.set(i, otherSigns.get(posInOthers));
				posInOthers++;
			}
		}
	}
}
