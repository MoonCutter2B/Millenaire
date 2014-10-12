package org.millenaire.common.building;

import io.netty.buffer.ByteBufInputStream;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.Constants;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.StreamReadWrite;

public class BuildingResManager {

	public List<Point> brickspot = new ArrayList<Point>();
	public List<Point> chests = new ArrayList<Point>();

	public List<Point> fishingspots = new ArrayList<Point>();
	public List<Point> sugarcanesoils = new ArrayList<Point>();
	public List<Point> healingspots = new ArrayList<Point>();
	public List<Point> furnaces = new ArrayList<Point>();
	public List<Point> brewingStands = new ArrayList<Point>();

	public List<Point> signs = new ArrayList<Point>();
	public List<List<Point>> sources = new ArrayList<List<Point>>();
	public List<Block> sourceTypes = new ArrayList<Block>();
	public List<List<Point>> spawns = new ArrayList<List<Point>>();
	public List<String> spawnTypes = new ArrayList<String>();
	public List<List<Point>> mobSpawners = new ArrayList<List<Point>>();
	public List<String> mobSpawnerTypes = new ArrayList<String>();
	public List<List<Point>> soils = new ArrayList<List<Point>>();
	public List<String> soilTypes = new ArrayList<String>();

	public List<Point> stalls = new ArrayList<Point>();

	public List<Point> woodspawn = new ArrayList<Point>();

	public List<Point> netherwartsoils = new ArrayList<Point>();

	public List<Point> silkwormblock = new ArrayList<Point>();
	public List<Point> dispenderUnknownPowder = new ArrayList<Point>();

	private Point sleepingPos = null, sellingPos = null, craftingPos = null, defendingPos = null, shelterPos = null, pathStartPos = null, leasurePos = null;

	private final Building building;

	public BuildingResManager(final Building b) {
		building = b;
	}

	public void addMobSpawnerPoint(final String type, final Point p) {
		if (!mobSpawnerTypes.contains(type)) {
			final List<Point> spawnsPoint = new ArrayList<Point>();
			spawnsPoint.add(p);
			mobSpawners.add(spawnsPoint);
			mobSpawnerTypes.add(type);
		} else {
			for (int i = 0; i < mobSpawnerTypes.size(); i++) {
				if (mobSpawnerTypes.get(i).equals(type)) {
					if (!mobSpawners.get(i).contains(p)) {
						mobSpawners.get(i).add(p);
					}
				}
			}
		}
	}

	public void addSoilPoint(final String type, final Point p) {
		if (!soilTypes.contains(type)) {
			final List<Point> spawnsPoint = new ArrayList<Point>();
			spawnsPoint.add(p);
			soils.add(spawnsPoint);
			soilTypes.add(type);
		} else {
			for (int i = 0; i < soilTypes.size(); i++) {
				if (soilTypes.get(i).equals(type)) {
					if (!soils.get(i).contains(p)) {
						soils.get(i).add(p);
					}
				}
			}
		}
	}

	public void addSourcePoint(final Block block, final Point p) {
		if (!sourceTypes.contains(block)) {
			final List<Point> spawnsPoint = new ArrayList<Point>();
			spawnsPoint.add(p);
			sources.add(spawnsPoint);
			sourceTypes.add(block);
		} else {
			for (int i = 0; i < sourceTypes.size(); i++) {
				if (sourceTypes.get(i).equals(block)) {
					if (!sources.get(i).contains(p)) {
						sources.get(i).add(p);
					}
				}
			}
		}
	}

	public void addSpawnPoint(final String type, final Point p) {
		if (!spawnTypes.contains(type)) {
			final List<Point> spawnsPoint = new ArrayList<Point>();
			spawnsPoint.add(p);
			spawns.add(spawnsPoint);
			spawnTypes.add(type);
		} else {
			for (int i = 0; i < spawnTypes.size(); i++) {
				if (spawnTypes.get(i).equals(type)) {
					if (!spawns.get(i).contains(p)) {
						spawns.get(i).add(p);
					}
				}
			}
		}
	}

	public HashMap<InvItem, Integer> getChestsContent() {

		final HashMap<InvItem, Integer> contents = new HashMap<InvItem, Integer>();

		for (final Point p : chests) {
			final TileEntityChest chest = p.getMillChest(building.worldObj);
			if (chest != null) {
				for (int i = 0; i < chest.getSizeInventory(); i++) {
					final ItemStack stack = chest.getStackInSlot(i);
					if (stack != null) {
						InvItem key;
						try {
							key = new InvItem(stack);
							if (stack != null) {
								if (contents.containsKey(key)) {
									contents.put(key, stack.stackSize + contents.get(key));
								} else {
									contents.put(key, stack.stackSize);
								}
							}
						} catch (final MillenaireException e) {
							MLN.printException(e);
						}
					}
				}
			}
		}
		return contents;
	}

	public Point getCocoaHarvestLocation() {
		for (int i = 0; i < soilTypes.size(); i++) {
			if (soilTypes.get(i).equals(Mill.CROP_CACAO)) {
				for (final Point p : soils.get(i)) {
					if (p.getBlock(building.worldObj) == Blocks.cocoa) {
						final int meta = p.getMeta(building.worldObj);

						if (BlockCocoa.func_149987_c(meta) >= 2) {
							return p;
						}
					}

				}
			}
		}

		return null;
	}

	public Point getCocoaPlantingLocation() {
		for (int i = 0; i < soilTypes.size(); i++) {
			if (soilTypes.get(i).equals(Mill.CROP_CACAO)) {
				for (final Point p : soils.get(i)) {
					if (p.getBlock(building.worldObj) == Blocks.air) {
						if (p.getNorth().getBlock(building.worldObj) == Blocks.log && BlockLog.func_150165_c(p.getNorth().getMeta(building.worldObj)) == 3) {
							return p;
						}
						if (p.getEast().getBlock(building.worldObj) == Blocks.log && BlockLog.func_150165_c(p.getEast().getMeta(building.worldObj)) == 3) {
							return p;
						}
						if (p.getSouth().getBlock(building.worldObj) == Blocks.log && BlockLog.func_150165_c(p.getSouth().getMeta(building.worldObj)) == 3) {
							return p;
						}
						if (p.getWest().getBlock(building.worldObj) == Blocks.log && BlockLog.func_150165_c(p.getWest().getMeta(building.worldObj)) == 3) {
							return p;
						}
					}

				}
			}
		}

		return null;
	}

	public Point getCraftingPos() {
		if (craftingPos != null) {
			return craftingPos;
		}

		if (sellingPos != null) {
			return sellingPos;
		}

		return sleepingPos;
	}

	// Where to regroup to defend the village
	public Point getDefendingPos() {
		if (defendingPos != null) {
			return defendingPos;
		}

		if (sellingPos != null) {
			return sellingPos;
		}

		return sleepingPos;
	}

	public Point getEmptyBrickLocation() {
		if (brickspot.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(brickspot.size());
		for (int i = start; i < brickspot.size(); i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Blocks.air) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Blocks.air) {
				return p;
			}
		}

		return null;
	}

	public Point getFullBrickLocation() {
		if (brickspot.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(brickspot.size());
		for (int i = start; i < brickspot.size(); i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.stone_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 1) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.stone_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 1) {
				return p;
			}
		}

		return null;
	}

	public Point getLeasurePos() {

		if (leasurePos != null) {
			return leasurePos;
		}

		return getSellingPos();
	}

	public int getNbEmptyBrickLocation() {
		if (brickspot.size() == 0) {
			return 0;
		}

		int nb = 0;

		for (int i = 0; i < brickspot.size(); i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Blocks.air) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbFullBrickLocation() {
		if (brickspot.size() == 0) {
			return 0;
		}

		int nb = 0;

		for (int i = 0; i < brickspot.size(); i++) {
			final Point p = brickspot.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.stone_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 1) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbNetherWartHarvestLocation() {
		if (netherwartsoils.size() == 0) {
			return 0;
		}

		int nb = 0;
		for (int i = 0; i < netherwartsoils.size(); i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.nether_wart && MillCommonUtilities.getBlockMeta(building.worldObj, p.getAbove()) >= 3) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbNetherWartPlantingLocation() {
		if (netherwartsoils.size() == 0) {
			return 0;
		}

		int nb = 0;
		for (int i = 0; i < netherwartsoils.size(); i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbSilkWormHarvestLocation() {
		if (silkwormblock.size() == 0) {
			return 0;
		}

		int nb = 0;
		for (int i = 0; i < silkwormblock.size(); i++) {
			final Point p = silkwormblock.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.wood_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 4) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbSugarCaneHarvestLocation() {
		if (sugarcanesoils.size() == 0) {
			return 0;
		}

		int nb = 0;
		for (int i = 0; i < sugarcanesoils.size(); i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getRelative(0, 2, 0)) == Blocks.reeds) {
				nb++;
			}
		}

		return nb;
	}

	public int getNbSugarCanePlantingLocation() {
		if (sugarcanesoils.size() == 0) {
			return 0;
		}

		int nb = 0;
		for (int i = 0; i < sugarcanesoils.size(); i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air) {
				nb++;
			}
		}

		return nb;
	}

	public Point getNetherWartsHarvestLocation() {
		if (netherwartsoils.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(netherwartsoils.size());
		for (int i = start; i < netherwartsoils.size(); i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.nether_wart && MillCommonUtilities.getBlockMeta(building.worldObj, p.getAbove()) == 3) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.nether_wart && MillCommonUtilities.getBlockMeta(building.worldObj, p.getAbove()) == 3) {
				return p;
			}
		}

		return null;
	}

	public Point getNetherWartsPlantingLocation() {
		if (netherwartsoils.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(netherwartsoils.size());
		for (int i = start; i < netherwartsoils.size(); i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air && MillCommonUtilities.getBlock(building.worldObj, p) == Blocks.soul_sand) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = netherwartsoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air && MillCommonUtilities.getBlock(building.worldObj, p) == Blocks.soul_sand) {
				return p;
			}
		}

		return null;
	}

	public Point getPathStartPos() {

		if (pathStartPos != null) {
			return pathStartPos;
		}

		return getSellingPos();
	}

	public Point getPlantingLocation() {
		for (final Point p : woodspawn) {
			final Block block = MillCommonUtilities.getBlock(building.worldObj, p);
			if (block == Blocks.air || block == Blocks.snow) {
				return p;
			}
		}
		return null;
	}

	public Point getSellingPos() {
		if (sellingPos != null) {
			return sellingPos;
		}

		return sleepingPos;
	}

	// Where to take shelter in case of raid
	// Defaults to sleeping pos unless specified
	public Point getShelterPos() {
		if (shelterPos != null) {
			return shelterPos;
		}

		return sleepingPos;
	}

	public Point getSilkwormHarvestLocation() {
		if (silkwormblock.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(silkwormblock.size());
		for (int i = start; i < silkwormblock.size(); i++) {
			final Point p = silkwormblock.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.wood_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 4) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = silkwormblock.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p) == Mill.wood_decoration && MillCommonUtilities.getBlockMeta(building.worldObj, p) == 4) {
				return p;
			}
		}

		return null;
	}

	public Point getSleepingPos() {
		return sleepingPos;
	}

	public List<Point> getSoilPoints(final String type) {

		for (int i = 0; i < soilTypes.size(); i++) {
			if (soilTypes.get(i).equals(type)) {
				return soils.get(i);
			}
		}

		return null;
	}

	public Point getSugarCaneHarvestLocation() {
		if (sugarcanesoils.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(sugarcanesoils.size());
		for (int i = start; i < sugarcanesoils.size(); i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getRelative(0, 2, 0)) == Blocks.reeds) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getRelative(0, 2, 0)) == Blocks.reeds) {
				return p;
			}
		}

		return null;
	}

	public Point getSugarCanePlantingLocation() {
		if (sugarcanesoils.size() == 0) {
			return null;
		}

		final int start = MillCommonUtilities.randomInt(sugarcanesoils.size());
		for (int i = start; i < sugarcanesoils.size(); i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air) {
				return p;
			}
		}

		for (int i = 0; i < start; i++) {
			final Point p = sugarcanesoils.get(i);
			if (MillCommonUtilities.getBlock(building.worldObj, p.getAbove()) == Blocks.air) {
				return p;
			}
		}

		return null;
	}

	public void readDataStream(final ByteBufInputStream ds) throws IOException {
		chests = StreamReadWrite.readPointList(ds);
		furnaces = StreamReadWrite.readPointList(ds);
		signs = StreamReadWrite.readPointList(ds);

		for (final Point p : chests) {
			final TileEntityMillChest chest = p.getMillChest(building.mw.world);
			if (chest != null) {
				chest.buildingPos = building.getPos();
			}
		}
	}

	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		sleepingPos = Point.read(nbttagcompound, "spawnPos");
		sellingPos = Point.read(nbttagcompound, "sellingPos");
		craftingPos = Point.read(nbttagcompound, "craftingPos");
		defendingPos = Point.read(nbttagcompound, "defendingPos");
		shelterPos = Point.read(nbttagcompound, "shelterPos");
		pathStartPos = Point.read(nbttagcompound, "pathStartPos");
		leasurePos = Point.read(nbttagcompound, "leasurePos");

		if (sleepingPos == null) {
			sleepingPos = building.getPos().getAbove();
		}

		NBTTagList nbttaglist = nbttagcompound.getTagList("chests", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				if (!chests.contains(p)) {
					chests.add(p);
				}
			}
		}

		if (!chests.contains(building.getPos())) {
			chests.add(0, building.getPos());
		}

		nbttaglist = nbttagcompound.getTagList("furnaces", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				furnaces.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("brewingStands", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				brewingStands.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("signs", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				signs.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("netherwartsoils", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				netherwartsoils.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("silkwormblock", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				silkwormblock.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("sugarcanesoils", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				sugarcanesoils.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("fishingspots", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				fishingspots.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("healingspots", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				healingspots.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("stalls", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				stalls.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("woodspawn", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				woodspawn.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("brickspot", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			final Point p = Point.read(nbttagcompound1, "pos");
			if (p != null) {
				brickspot.add(p);
			}
		}

		nbttaglist = nbttagcompound.getTagList("spawns", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

			String spawnType = nbttagcompound1.getString("type");

			// convertion for pre-4.3 tags
			if (spawnType.equals("ml_FarmPig")) {
				spawnType = "Pig";
			} else if (spawnType.equals("ml_FarmCow")) {
				spawnType = "Cow";
			} else if (spawnType.equals("ml_FarmChicken")) {
				spawnType = "Chicken";
			} else if (spawnType.equals("ml_FarmSheep")) {
				spawnType = "Sheep";
			}

			spawnTypes.add(spawnType);
			final List<Point> v = new ArrayList<Point>();

			final NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", Constants.NBT.TAG_COMPOUND);
			for (int j = 0; j < nbttaglist2.tagCount(); j++) {
				final NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
				final Point p = Point.read(nbttagcompound2, "pos");
				if (p != null) {
					v.add(p);
					if (MLN.LogHybernation >= MLN.MINOR) {
						MLN.minor(this, "Loaded spawn point: " + p);
					}
				}
			}
			spawns.add(v);
			if (MLN.LogHybernation >= MLN.MINOR) {
				MLN.minor(this, "Loaded " + v.size() + " spawn points for " + spawnTypes.get(i));
			}
		}

		nbttaglist = nbttagcompound.getTagList("mobspawns", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

			mobSpawnerTypes.add(nbttagcompound1.getString("type"));
			final List<Point> v = new ArrayList<Point>();

			final NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", Constants.NBT.TAG_COMPOUND);
			for (int j = 0; j < nbttaglist2.tagCount(); j++) {
				final NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
				final Point p = Point.read(nbttagcompound2, "pos");
				if (p != null) {
					v.add(p);
					if (MLN.LogHybernation >= MLN.MINOR) {
						MLN.minor(this, "Loaded spawn point: " + p);
					}
				}
			}
			mobSpawners.add(v);
			if (MLN.LogHybernation >= MLN.MINOR) {
				MLN.minor(this, "Loaded " + v.size() + " mob spawn points for " + spawnTypes.get(i));
			}
		}

		nbttaglist = nbttagcompound.getTagList("sources", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

			sourceTypes.add(Block.getBlockById(nbttagcompound1.getInteger("type")));
			final List<Point> v = new ArrayList<Point>();

			final NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", Constants.NBT.TAG_COMPOUND);
			for (int j = 0; j < nbttaglist2.tagCount(); j++) {
				final NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
				final Point p = Point.read(nbttagcompound2, "pos");
				if (p != null) {
					v.add(p);
					if (MLN.LogHybernation >= MLN.DEBUG) {
						MLN.debug(this, "Loaded source point: " + p);
					}
				}
			}
			sources.add(v);
			if (MLN.LogHybernation >= MLN.MAJOR) {
				MLN.debug(this, "Loaded " + v.size() + " sources points for " + sourceTypes.get(i).getUnlocalizedName());
			}
		}

		nbttaglist = nbttagcompound.getTagList("genericsoils", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);

			final String type = nbttagcompound1.getString("type");

			final NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", Constants.NBT.TAG_COMPOUND);
			for (int j = 0; j < nbttaglist2.tagCount(); j++) {
				final NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
				final Point p = Point.read(nbttagcompound2, "pos");
				if (p != null) {
					addSoilPoint(type, p);
				}
			}
		}

		for (final Point p : chests) {
			if (building.worldObj.blockExists(p.getiX(), p.getiY(), p.getiZ()) && p.getMillChest(building.worldObj) != null) {
				p.getMillChest(building.worldObj).buildingPos = building.getPos();
			}
		}

	}

	public void sendBuildingPacket(final DataOutput data) throws IOException {
		StreamReadWrite.writePointList(chests, data);
		StreamReadWrite.writePointList(furnaces, data);
		StreamReadWrite.writePointList(signs, data);
	}

	public void setCraftingPos(final Point p) {
		craftingPos = p;
	}

	public void setDefendingPos(final Point p) {
		defendingPos = p;
	}

	public void setLeasurePos(final Point p) {
		leasurePos = p;
	}

	public void setPathStartPos(final Point p) {
		pathStartPos = p;
	}

	public void setSellingPos(final Point p) {
		sellingPos = p;
	}

	public void setShelterPos(final Point p) {
		shelterPos = p;
	}

	public void setSleepingPos(final Point p) {
		sleepingPos = p;
	}

	public void writeToNBT(final NBTTagCompound nbttagcompound) {
		if (sleepingPos != null) {
			sleepingPos.write(nbttagcompound, "spawnPos");
		}
		if (sellingPos != null) {
			sellingPos.write(nbttagcompound, "sellingPos");
		}
		if (craftingPos != null) {
			craftingPos.write(nbttagcompound, "craftingPos");
		}
		if (defendingPos != null) {
			defendingPos.write(nbttagcompound, "defendingPos");
		}
		if (shelterPos != null) {
			shelterPos.write(nbttagcompound, "shelterPos");
		}
		if (pathStartPos != null) {
			pathStartPos.write(nbttagcompound, "pathStartPos");
		}
		if (leasurePos != null) {
			leasurePos.write(nbttagcompound, "leasurePos");
		}

		NBTTagList nbttaglist;

		nbttaglist = new NBTTagList();
		for (final Point p : signs) {
			if (p != null) {
				final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				p.write(nbttagcompound1, "pos");
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("signs", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : netherwartsoils) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("netherwartsoils", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : silkwormblock) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("silkwormblock", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : sugarcanesoils) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("sugarcanesoils", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : fishingspots) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("fishingspots", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : healingspots) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("healingspots", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : stalls) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("stalls", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : woodspawn) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("woodspawn", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : brickspot) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("brickspot", nbttaglist);

		nbttaglist = new NBTTagList();
		for (int i = 0; i < spawns.size(); i++) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("type", spawnTypes.get(i));
			final NBTTagList nbttaglist2 = new NBTTagList();
			for (final Point p : spawns.get(i)) {
				final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				p.write(nbttagcompound2, "pos");
				nbttaglist2.appendTag(nbttagcompound2);
			}
			nbttagcompound1.setTag("points", nbttaglist2);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("spawns", nbttaglist);

		nbttaglist = new NBTTagList();
		for (int i = 0; i < soilTypes.size(); i++) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("type", soilTypes.get(i));
			final NBTTagList nbttaglist2 = new NBTTagList();
			for (final Point p : soils.get(i)) {
				final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				p.write(nbttagcompound2, "pos");
				nbttaglist2.appendTag(nbttagcompound2);
			}
			nbttagcompound1.setTag("points", nbttaglist2);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("genericsoils", nbttaglist);

		nbttaglist = new NBTTagList();
		for (int i = 0; i < mobSpawners.size(); i++) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setString("type", mobSpawnerTypes.get(i));
			final NBTTagList nbttaglist2 = new NBTTagList();
			for (final Point p : mobSpawners.get(i)) {
				final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				p.write(nbttagcompound2, "pos");
				nbttaglist2.appendTag(nbttagcompound2);
			}
			nbttagcompound1.setTag("points", nbttaglist2);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("mobspawns", nbttaglist);

		nbttaglist = new NBTTagList();
		for (int i = 0; i < sources.size(); i++) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setInteger("type", Block.getIdFromBlock(sourceTypes.get(i)));
			final NBTTagList nbttaglist2 = new NBTTagList();
			for (final Point p : sources.get(i)) {
				final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				p.write(nbttagcompound2, "pos");
				nbttaglist2.appendTag(nbttagcompound2);
			}
			nbttagcompound1.setTag("points", nbttaglist2);
			nbttaglist.appendTag(nbttagcompound1);
		}
		nbttagcompound.setTag("sources", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : chests) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("chests", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : furnaces) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("furnaces", nbttaglist);

		nbttaglist = new NBTTagList();
		for (final Point p : brewingStands) {
			final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			p.write(nbttagcompound1, "pos");
			nbttaglist.appendTag(nbttagcompound1);

		}
		nbttagcompound.setTag("brewingStands", nbttaglist);
	}

}
