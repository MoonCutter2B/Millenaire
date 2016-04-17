package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;

public class GoalIndianHarvestSugarCane extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		final List<Point> vp = new ArrayList<Point>();
		final List<Point> buildingp = new ArrayList<Point>();
		for (final Building plantation : villager.getTownHall().getBuildingsWithTag(Building.tagSugarPlantation)) {
			final Point p = plantation.getResManager().getSugarCaneHarvestLocation();
			if (p != null) {
				vp.add(p);
				buildingp.add(plantation.getPos());
			}
		}

		if (vp.isEmpty()) {
			return null;
		}

		Point p = vp.get(0);
		Point buildingP = buildingp.get(0);

		for (int i = 1; i < vp.size(); i++) {
			if (vp.get(i).horizontalDistanceToSquared(villager) < p.horizontalDistanceToSquared(villager)) {
				p = vp.get(i);
				buildingP = buildingp.get(i);
			}
		}
		return packDest(p, buildingP);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return villager.getBestHoeStack();
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		int nbsimultaneous = 0;
		for (final MillVillager v : villager.getTownHall().villagers) {
			if (v != villager && this.key.equals(v.goalKey)) {
				nbsimultaneous++;
			}
		}
		if (nbsimultaneous > 2) {
			return false;
		}

		boolean delayOver;
		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSugarPlantation)) {
			final int nb = kiln.getResManager().getNbSugarCaneHarvestLocation();

			if (nb > 0 && delayOver) {
				return true;
			}
			if (nb > 4) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		Point cropPoint = villager.getGoalDestPoint().getRelative(0, 3, 0);

		if (villager.getBlock(cropPoint) == Blocks.reeds) {
			villager.setBlockAndMetadata(cropPoint, Blocks.air, 0);

			int nbcrop = 1;
			final float irrigation = villager.getTownHall().getVillageIrrigation();
			final double rand = Math.random();
			if (rand < irrigation / 100) {
				nbcrop++;
			}

			villager.addToInv(Items.reeds, nbcrop);
		}

		cropPoint = villager.getGoalDestPoint().getRelative(0, 2, 0);

		if (villager.getBlock(cropPoint) == Blocks.reeds) {
			villager.setBlockAndMetadata(cropPoint, Blocks.air, 0);

			int nbcrop = 1;
			final float irrigation = villager.getTownHall().getVillageIrrigation();
			final double rand = Math.random();
			if (rand < irrigation / 100) {
				nbcrop++;
			}

			villager.swingItem();

			villager.addToInv(Items.reeds, nbcrop);
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		int p = 200 - villager.getTownHall().nbGoodAvailable(Items.reeds, 0, false, false) * 4;

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (this.key.equals(v.goalKey)) {
				p = p / 2;
			}
		}

		return p;
	}

}
