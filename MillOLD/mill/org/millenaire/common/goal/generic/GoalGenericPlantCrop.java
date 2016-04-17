package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;

public class GoalGenericPlantCrop extends GoalGeneric {

	public static Block getCropBlock(final String cropType) {
		if (cropType.equals(Mill.CROP_WHEAT)) {
			return Blocks.wheat;
		}
		if (cropType.equals(Mill.CROP_CARROT)) {
			return Blocks.carrots;
		}
		if (cropType.equals(Mill.CROP_POTATO)) {
			return Blocks.potatoes;
		}
		if (cropType.equals("rice")) {
			return Mill.cropRice;
		}
		if (cropType.equals("turmeric")) {
			return Mill.cropTurmeric;
		}
		if (cropType.equals("maize")) {
			return Mill.cropMaize;
		}
		if (cropType.equals("vine")) {
			return Mill.cropVine;
		}
		return null;
	}

	public static int getCropBlockMeta(final String cropType) {
		return 0;
	}

	public static GoalGenericPlantCrop loadGenericPlantCropGoal(final File file) {

		final GoalGenericPlantCrop g = new GoalGenericPlantCrop();

		g.key = file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0 && !line.startsWith("//")) {
					final String[] temp = line.split("=");
					if (temp.length != 2) {
						MLN.error(null, "Invalid line when loading generic plating goal " + file.getName() + ": " + line);
					} else {
						final String key = temp[0].trim().toLowerCase();
						String value = temp[1].trim();

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("soilname")) {
								g.soilName = value.trim().toLowerCase();
							} else if (key.equals("croptype")) {
								g.cropType = value.trim().toLowerCase();
							} else if (key.equals("seed")) {
								value = value.trim().toLowerCase();
								if (Goods.goodsName.containsKey(value)) {
									g.seedItem = Goods.goodsName.get(value);
								} else {
									MLN.error(null, "Unknown seed in generic planting goal " + file.getName() + ": " + line);
								}
							} else {
								MLN.error(null, "Unknown line in generic planting goal " + file.getName() + ": " + line);
							}
						}
					}
				}
			}

			if (g.soilName == null) {
				MLN.error(null, "The soilname is mandatory in custom planting goals " + file.getName());
				return null;
			}
			if (g.cropType == null) {
				MLN.error(null, "The croptype is mandatory in custom planting goals " + file.getName());
				return null;
			}

			reader.close();
		} catch (final Exception e) {
			MLN.printException(e);

			return null;
		}

		return g;
	}

	public String soilName = null, cropType = null;

	public InvItem seedItem = null;

	public GoalGenericPlantCrop() {
		super();
		duration = 100;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws MillenaireException {

		Point dest = null;
		Building destBuilding = null;

		final List<Building> buildings = getBuildings(villager);

		for (final Building buildingDest : buildings) {

			if (isDestPossible(villager, buildingDest)) {

				final List<Point> soils = buildingDest.getResManager().getSoilPoints(soilName);

				if (soils != null) {
					for (final Point p : soils) {
						if (isValidPlantingLocation(villager.worldObj, p)) {
							if (dest == null || p.distanceTo(villager) < dest.distanceTo(villager)) {
								dest = p;
								destBuilding = buildingDest;
							}
						}
					}
				}
			}
		}
		if (dest == null) {
			return null;
		}

		return packDest(dest, destBuilding);
	}

	@Override
	public boolean isDestPossibleSpecific(final MillVillager villager, final Building b) {
		if (seedItem != null && b.countGoods(seedItem) + villager.countInv(seedItem) == 0) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isPossibleGenericGoal(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	private boolean isValidPlantingLocation(final World world, final Point p) {
		if ((p.getAbove().getBlock(world) == Blocks.air || p.getAbove().getBlock(world) == Blocks.snow || p.getAbove().getBlock(world) == Blocks.leaves)
				&& (p.getBlock(world) == Blocks.grass || p.getBlock(world) == Blocks.dirt || p.getBlock(world) == Blocks.farmland)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		final Building dest = villager.getGoalBuildingDest();

		if (dest == null) {
			return true;
		}

		if (!isValidPlantingLocation(villager.worldObj, villager.getGoalDestPoint())) {
			return true;
		}

		if (seedItem != null) {
			final int taken = villager.takeFromInv(seedItem, 1);
			if (taken == 0) {
				dest.takeGoods(seedItem, 1);
			}
		}

		if (villager.getGoalDestPoint().getBlock(villager.worldObj) != Blocks.farmland) {
			villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.farmland, 0);
		}

		villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), getCropBlock(cropType), getCropBlockMeta(cropType));

		villager.swingItem();

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws MillenaireException {

		final GoalInformation info = getDestination(villager);

		if (info == null || info.getDest() == null) {
			return -1;
		}

		return (int) (30 - villager.getPos().distanceTo(info.getDest()));
	}
}
