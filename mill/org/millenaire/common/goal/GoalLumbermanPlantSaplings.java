package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalLumbermanPlantSaplings extends Goal {

	public GoalLumbermanPlantSaplings() {
		this.maxSimultaneousInBuilding = 1;
	}

	@Override
	public int actionDuration(final MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {

		final List<Point> vp = new ArrayList<Point>();
		final List<Point> buildingp = new ArrayList<Point>();
		for (final Building grove : villager.getTownHall().getBuildingsWithTag(
				Building.tagGrove)) {
			final Point p = grove.getResManager().getPlantingLocation();
			if (p != null) {
				vp.add(p);
				buildingp.add(grove.getPos());
			}
		}

		if (vp.isEmpty()) {
			return null;
		}

		Point p = vp.get(0);
		Point buildingP = buildingp.get(0);

		for (int i = 1; i < vp.size(); i++) {
			if (vp.get(i).horizontalDistanceToSquared(villager) < p
					.horizontalDistanceToSquared(villager)) {
				p = vp.get(i);
				buildingP = buildingp.get(i);
			}
		}
		return packDest(p, buildingP);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {

		int meta = 0;
		if (villager.takeFromInv(Blocks.sapling, 0, 1) == 1) {
			meta = 0;
		} else if (villager.takeFromInv(Blocks.sapling, 1, 1) == 1) {
			meta = 1;
		} else if (villager.takeFromInv(Blocks.sapling, 2, 1) == 1) {
			meta = 2;
		} else if (villager.takeFromInv(Blocks.sapling, 3, 1) == 1) {
			meta = 3;
		}

		return new ItemStack[] { new ItemStack(Blocks.sapling, 1, meta) };
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		for (final Building grove : villager.getTownHall().getBuildingsWithTag(
				Building.tagGrove)) {
			final Point p = grove.getResManager().getPlantingLocation();
			if (p != null) {
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

		final Block block = MillCommonUtilities.getBlock(villager.worldObj,
				villager.getGoalDestPoint());
		if (block == Blocks.air || block == Blocks.snow) {

			final int metaStart = MillCommonUtilities.randomInt(4);
			int chosenMeta = -1;

			for (int i = metaStart; i < 4 && chosenMeta == -1; i++) {
				if (villager.takeFromInv(Blocks.sapling, i, 1) == 1) {
					chosenMeta = i;
				}
			}

			for (int i = 0; i < metaStart && chosenMeta == -1; i++) {
				if (villager.takeFromInv(Blocks.sapling, i, 1) == 1) {
					chosenMeta = i;
				}
			}

			if (chosenMeta == -1) {
				chosenMeta = 0;
			}

			villager.setBlockAndMetadata(villager.getGoalDestPoint(),
					Blocks.sapling, chosenMeta);

			villager.swingItem();

			if (MLN.LogLumberman >= MLN.DEBUG && villager.extraLog) {
				MLN.debug(this, "Planted at: " + villager.getGoalDestPoint());
			}
		} else if (MLN.LogLumberman >= MLN.DEBUG && villager.extraLog) {
			MLN.debug(this,
					"Failed to plant at: " + villager.getGoalDestPoint());
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 120;
	}

	@Override
	public int range(final MillVillager villager) {
		return 5;
	}

}
