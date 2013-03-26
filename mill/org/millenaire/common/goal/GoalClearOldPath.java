package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalClearOldPath extends Goal {

	public GoalClearOldPath() {
		super();
		maxSimultaneousTotal=1;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager)
			throws Exception {

		Point p=villager.getTownHall().getCurrentClearPathPoint();

		if (p!=null)
			return packDest(p);

		return null;
	}

	@Override
	public int actionDuration(MillVillager villager) {
		final int toolEfficiency=(int)villager.getBestShovel().efficiencyOnProperMaterial;

		return 100-(toolEfficiency*5);
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		Point p=villager.getTownHall().getCurrentClearPathPoint();

		if (p==null)
			return true;

		if (MLN.VillagePaths>=MLN.DEBUG)
			MLN.debug(villager, "Clearing old path block: "+p);

		int bid=p.getId(villager.worldObj);

		if (bid==Mill.pathSlab.blockID) {
			p.setBlock(villager.worldObj, 0, 0, true, false);		
		} else if (bid==Mill.path.blockID) {

			int bidBelow=p.getBelow().getId(villager.worldObj);
			if (MillCommonUtilities.getBlockIdValidGround(bidBelow,true)>0)
				p.setBlock(villager.worldObj, MillCommonUtilities.getBlockIdValidGround(bidBelow,true), 0, true, false);
			else
				p.setBlock(villager.worldObj, Block.dirt.blockID, 0, true, false);		
		}

		villager.getTownHall().oldPathPointsToClearIndex++;

		p=villager.getTownHall().getCurrentClearPathPoint();

		if (p!=null) {
			villager.setGoalDestPoint(p);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 40;
	}

	@Override
	public int range(MillVillager villager) {
		return ACTIVATION_RANGE+2;
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
		return villager.getTownHall().getCurrentClearPathPoint()!=null;
	}

	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {
		return villager.getTownHall().getCurrentClearPathPoint()!=null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return  villager.getBestShovelStack();
	}
}
