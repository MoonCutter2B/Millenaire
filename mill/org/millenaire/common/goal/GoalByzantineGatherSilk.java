package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class GoalByzantineGatherSilk extends Goal {

	private static ItemStack[] shears = { new ItemStack(Items.shears, 1) };

	public GoalByzantineGatherSilk() {
		maxSimultaneousInBuilding = 2;
		try {
			buildingLimit.put(new InvItem(Mill.silk), 128);
			townhallLimit.put(new InvItem(Mill.silk), 128);
		} catch (final MillenaireException e) {
			MLN.printException(e);
		}

	}

	@Override
	public int actionDuration(final MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {

		final List<Point> vp = new ArrayList<Point>();
		final List<Point> buildingp = new ArrayList<Point>();
		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSilkwormFarm)) {
			final Point p = kiln.getResManager().getSilkwormHarvestLocation();
			if (p != null) {
				vp.add(p);
				buildingp.add(kiln.getPos());
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
		return shears;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		boolean delayOver;
		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSilkwormFarm)) {
			final int nb = kiln.getResManager().getNbSilkWormHarvestLocation();

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
		if (MillCommonUtilities.getBlock(villager.worldObj, villager.getGoalDestPoint()) == Mill.wood_decoration
				&& MillCommonUtilities.getBlockMeta(villager.worldObj, villager.getGoalDestPoint()) == 4) {
			villager.addToInv(Mill.silk, 0, 1);
			villager.setBlockAndMetadata(villager.getGoalDestPoint(), Mill.wood_decoration, 3);

			villager.swingItem();

			return false;
		} else {
			return true;
		}

	}

	@Override
	public int priority(final MillVillager villager) {
		int p;
		try {
			p = 100 - villager.getTownHall().nbGoodAvailable(new InvItem(Mill.silk, 1), false, false) * 2;
		} catch (final MillenaireException e) {
			MLN.printException(e);
			p = 0;
		}
		for (final MillVillager v : villager.getTownHall().villagers) {
			if (this.key.equals(v.goalKey)) {
				p = p / 2;
			}
		}

		return p;
	}

	@Override
	public boolean unreachableDestination(final MillVillager villager) {
		return false;
	}

}
