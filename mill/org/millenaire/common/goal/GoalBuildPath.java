package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.construction.BuildingPlan.BuildingBlock;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalBuildPath extends Goal {

	public GoalBuildPath() {
		super();
		maxSimultaneousTotal=1;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager)
			throws Exception {

		BuildingBlock b=villager.getTownHall().getCurrentPathBuildingBlock();

		if (b!=null)
			return packDest(b.p);

		return null;
	}

	@Override
	public int actionDuration(MillVillager villager) {
		final int toolEfficiency=(int)villager.getBestShovel().getDigSpeed(new ItemStack(villager.getBestShovel(),1), Blocks.dirt, 0);

		return 100-(toolEfficiency*5);
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final BuildingBlock bblock=villager.getTownHall().getCurrentPathBuildingBlock();

		if (bblock==null)
			return true;

		if (MLN.LogVillagePaths>=MLN.DEBUG)
			MLN.debug(villager, "Building path block: "+bblock);

		bblock.pathBuild(villager.getTownHall());

		villager.getTownHall().pathsToBuildPathIndex++;

		BuildingBlock b=villager.getTownHall().getCurrentPathBuildingBlock();
		
		villager.swingItem();

		if (b!=null) {
			villager.setGoalDestPoint(b.p);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 50;
	}

	@Override
	public boolean stopMovingWhileWorking() {
		return false;
	}

	@Override
	public boolean unreachableDestination(MillVillager villager) throws Exception {

		performAction(villager);

		return true;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_BUILDING;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		return MLN.BuildVillagePaths && 
				villager.getTownHall().getCurrentPathBuildingBlock()!=null;
	}

	@Override
	public int range(MillVillager villager) {
		return ACTIVATION_RANGE+2;
	}

	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {
		return villager.getTownHall().getCurrentPathBuildingBlock()!=null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {

		final BuildingBlock bblock=villager.getTownHall().getCurrentPathBuildingBlock();

		if (bblock!=null) {
			if (bblock.block==Blocks.air)
				return villager.getBestShovelStack();
			else
				return new ItemStack[]{new ItemStack(Item.getItemFromBlock(bblock.block), 1, bblock.meta)};
		} else
			return  villager.getBestShovelStack();
	}
}
