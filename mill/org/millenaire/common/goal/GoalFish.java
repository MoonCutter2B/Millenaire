package org.millenaire.common.goal;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalFish extends Goal {

	private static ItemStack[] fishingRod = new ItemStack[] { new ItemStack(Items.fishing_rod, 1) };

	public GoalFish() {
		buildingLimit.put(new InvItem(Items.fish, 0), 512);
		buildingLimit.put(new InvItem(Items.cooked_fished, 0), 512);
	}

	@Override
	public int actionDuration(final MillVillager villager) {

		return 25000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		final List<Building> vb = villager.getTownHall().getBuildingsWithTag(Building.tagFishingSpot);

		Building closest = null;

		for (final Building b : vb) {
			if (closest == null
					|| villager.getPos().horizontalDistanceToSquared(b.getResManager().getSleepingPos()) < villager.getPos().horizontalDistanceToSquared(closest.getResManager().getSleepingPos())) {
				closest = b;
			}
		}

		if (closest == null || closest.getResManager().fishingspots.size() == 0) {
			return null;
		}

		return packDest(closest.getResManager().fishingspots.get(MillCommonUtilities.randomInt(closest.getResManager().fishingspots.size())), closest);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) throws Exception {
		return fishingRod;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {

		for (final Building b : villager.getTownHall().getBuildings()) {
			if (b.getResManager().fishingspots.size() > 0) {
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
	public boolean performAction(final MillVillager villager) throws Exception {

		villager.addToInv(Items.fish, 1);

		villager.swingItem();

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {

		if (villager.getGoalBuildingDest() == null) {
			return 20;
		}

		return 100 - villager.getGoalBuildingDest().countGoods(Items.fish);
	}

	@Override
	public boolean stuckAction(final MillVillager villager) throws Exception {
		return performAction(villager);
	}

}
