package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorldInfo;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalLumbermanChopTrees extends Goal {

	public GoalLumbermanChopTrees() {
		this.maxSimultaneousInBuilding = 1;
		this.townhallLimit.put(new InvItem(Blocks.log, -1), 4096);
	}

	@Override
	public int actionDuration(final MillVillager villager) {
		final int toolEfficiency = (int) villager.getBestAxe().getDigSpeed(
				new ItemStack(villager.getBestAxe(), 1), Blocks.log, 0);
		;
		return 1000 - toolEfficiency * 40;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {

		final List<Point> vp = new ArrayList<Point>();
		final List<Point> buildingp = new ArrayList<Point>();
		for (final Building grove : villager.getTownHall().getBuildingsWithTag(
				Building.tagGrove)) {
			if (grove.getWoodCount() > 4) {
				final Point p = grove.getWoodLocation();
				if (p != null) {
					vp.add(p);
					buildingp.add(grove.getPos());
				}
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
		return villager.getBestAxeStack();
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_CHOPLUMBER;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		if (villager.countInv(Blocks.log, -1) > 64) {
			return false;
		}

		if (getDestination(villager) == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		boolean woodFound = false;

		if (MLN.LogLumberman >= MLN.DEBUG) {
			MLN.debug(
					this,
					"Attempting to gather wood at: "
							+ villager.getGoalDestPoint());
		}

		final MillWorldInfo winfo = villager.getTownHall().winfo;

		for (int i = 12; i > -12; i--) {
			for (int j = -3; j < 4; j++) {
				for (int k = -3; k < 4; k++) {
					final Point p = villager.getGoalDestPoint().getRelative(j,
							i, k);

					if (!winfo.isConstructionOrLoggingForbiddenHere(p)) {

						final Block block = villager.getBlock(p);

						if (block == Blocks.log || block == Blocks.leaves) {
							if (!woodFound) {
								if (block == Blocks.log) {
									final int meta = villager.getBlockMeta(p) & 3;
									villager.setBlock(p, Blocks.air);

									villager.swingItem();

									villager.addToInv(Blocks.log, meta, 1);
									woodFound = true;

									if (MLN.LogLumberman >= MLN.DEBUG) {
										MLN.debug(this, "Gathered wood at: "
												+ villager.getGoalDestPoint());
									}
								} else {

									if (MillCommonUtilities.randomInt(4) == 0) {
										villager.addToInv(
												Blocks.sapling,
												MillCommonUtilities
														.getBlockMeta(
																villager.worldObj,
																p) & 3, 1);
									}
									villager.setBlock(p, Blocks.air);

									villager.swingItem();

									if (villager.gathersApples()
											&& MillCommonUtilities.chanceOn(16)) {
										villager.addToInv(Mill.ciderapple, 1);
									}

									if (MLN.LogLumberman >= MLN.DEBUG
											&& villager.extraLog) {
										MLN.debug(this, "Destroyed leaves at: "
												+ villager.getGoalDestPoint());
									}
								}
							} else {
								if (MLN.LogLumberman >= MLN.DEBUG
										&& villager.extraLog) {
									MLN.debug(this, "More wood found.");
								}
								return false;// still more wood to cut
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return Math.max(10, 125 - villager.countInv(Blocks.log, -1));
	}

	@Override
	public int range(final MillVillager villager) {
		return ACTIVATION_RANGE + 2;
	}
}
