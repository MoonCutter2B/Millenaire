package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.building.BuildingBlock;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalBuildPath extends Goal {

	public GoalBuildPath() {
		super();
		maxSimultaneousTotal = 1;
	}

	@Override
	public int actionDuration(final MillVillager villager) {
		final int toolEfficiency = (int) villager.getBestShovel().getDigSpeed(
				new ItemStack(villager.getBestShovel(), 1), Blocks.dirt, 0);

		return 100 - toolEfficiency * 5;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager)
			throws Exception {

		final BuildingBlock b = villager.getTownHall()
				.getCurrentPathBuildingBlock();

		if (b != null) {
			return packDest(b.p);
		}

		return null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {

		final BuildingBlock bblock = villager.getTownHall()
				.getCurrentPathBuildingBlock();

		if (bblock != null) {
			if (bblock.block == Blocks.air) {
				return villager.getBestShovelStack();
			} else {
				return new ItemStack[] { new ItemStack(
						Item.getItemFromBlock(bblock.block), 1, bblock.meta) };
			}
		} else {
			return villager.getBestShovelStack();
		}
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_BUILDING;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {
		return MLN.BuildVillagePaths
				&& villager.getTownHall().getCurrentPathBuildingBlock() != null;
	}

	@Override
	public boolean isStillValidSpecific(final MillVillager villager)
			throws Exception {
		return villager.getTownHall().getCurrentPathBuildingBlock() != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final BuildingBlock bblock = villager.getTownHall()
				.getCurrentPathBuildingBlock();

		if (bblock == null) {
			return true;
		}

		if (MLN.LogVillagePaths >= MLN.DEBUG) {
			MLN.debug(villager, "Building path block: " + bblock);
		}

		bblock.pathBuild(villager.getTownHall());

		villager.getTownHall().pathsToBuildPathIndex++;

		final BuildingBlock b = villager.getTownHall()
				.getCurrentPathBuildingBlock();

		villager.swingItem();

		if (b != null) {
			villager.setGoalDestPoint(b.p);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 50;
	}

	@Override
	public int range(final MillVillager villager) {
		return ACTIVATION_RANGE + 2;
	}

	@Override
	public boolean stopMovingWhileWorking() {
		return false;
	}

	@Override
	public boolean unreachableDestination(final MillVillager villager)
			throws Exception {

		performAction(villager);

		return true;
	}
}
