package org.millenaire.common.goal.generic;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.item.Goods;

public class GoalGenericHarvestCrop extends GoalGeneric {

	public static int getCropBlockRipeMeta(final String cropType) {
		return 7;
	}

	public static GoalGenericHarvestCrop loadGenericHarvestCropGoal(final File file) {

		final GoalGenericHarvestCrop g = new GoalGenericHarvestCrop();

		g.key = file.getName().split("\\.")[0].toLowerCase();

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0 && !line.startsWith("//")) {
					final String[] temp = line.split("=");
					if (temp.length != 2) {
						MLN.error(null, "Invalid line when loading generic harvest goal " + file.getName() + ": " + line);
					} else {
						final String key = temp[0].trim().toLowerCase();
						String value = temp[1].trim();

						if (!GoalGeneric.readGenericGoalConfigLine(g, key, value, file, line)) {
							if (key.equals("soilname")) {
								g.soilName = value.trim().toLowerCase();
							} else if (key.equals("croptype")) {
								g.cropType = value.trim().toLowerCase();
							} else if (key.equals("irrigationbonuscrop")) {
								value = value.trim().toLowerCase();
								if (Goods.goodsName.containsKey(value)) {
									g.irrigationBonusCrop = Goods.goodsName.get(value);
								} else {
									MLN.error(null, "Unknown irrigationbonuscrop in generic harvest goal " + file.getName() + ": " + line);
								}
							} else if (key.equals("harvestitem")) {
								final String[] temp2 = value.split(",");

								if (temp2.length != 2) {
									MLN.error(null, "harvestitem must take the form of harvestitem=goodname,chanceon100 (ex: wheat,50) in generic harbest goal " + file.getName() + ": " + line);
								} else {
									if (Goods.goodsName.containsKey(temp2[0])) {
										g.harvestItems.add(Goods.goodsName.get(temp2[0]));
										g.harvestItemsChance.add(Integer.parseInt(temp2[1]));
									} else {
										MLN.error(null, "Unknown harvestitem item in generic harvest goal " + file.getName() + ": " + line);
									}
								}
							} else {
								MLN.error(null, "Unknown line in generic harvest goal " + file.getName() + ": " + line);
							}
						}
					}
				}
			}

			if (g.soilName == null) {
				MLN.error(null, "The soilname is mandatory in custom harvest goals " + file.getName());
				return null;
			}
			if (g.cropType == null) {
				MLN.error(null, "The croptype is mandatory in custom harvest goals " + file.getName());
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

	public List<InvItem> harvestItems = new ArrayList<InvItem>();

	public List<Integer> harvestItemsChance = new ArrayList<Integer>();

	public InvItem irrigationBonusCrop = null;

	public GoalGenericHarvestCrop() {
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
						if (isValidHarvestSoil(villager.worldObj, p)) {
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
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) throws Exception {
		if (heldItems != null) {
			return heldItems;
		}

		return villager.getBestHoeStack();
	}

	@Override
	public boolean isDestPossibleSpecific(final MillVillager villager, final Building b) {
		return true;
	}

	@Override
	public boolean isPossibleGenericGoal(final MillVillager villager) throws Exception {
		return getDestination(villager) != null;
	}

	private boolean isValidHarvestSoil(final World world, final Point p) {
		return p.getAbove().getBlock(world) == GoalGenericPlantCrop.getCropBlock(cropType) && p.getAbove().getMeta(world) == getCropBlockRipeMeta(cropType);
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		if (isValidHarvestSoil(villager.worldObj, villager.getGoalDestPoint())) {

			if (irrigationBonusCrop != null) {
				final float irrigation = villager.getTownHall().getVillageIrrigation();
				final double rand = Math.random();
				if (rand < irrigation / 100) {
					villager.addToInv(irrigationBonusCrop, 1);
				}
			}

			for (int i = 0; i < harvestItems.size(); i++) {
				if (MillCommonUtilities.randomInt(100) < harvestItemsChance.get(i)) {
					villager.addToInv(harvestItems.get(i), 1);
				}
			}

			villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), Blocks.air, 0);

			villager.swingItem();
		}

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
