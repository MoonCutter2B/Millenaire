package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalPlantNetherWarts extends Goal {

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return packDest(villager.getHouse().getNetherWartsPlantingLocation(),villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return villager.getBestHoeStack();
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) {

		int blockId=villager.getBlock(villager.getGoalDestPoint());

		final Point cropPoint=villager.getGoalDestPoint().getAbove();

		blockId=villager.getBlock(cropPoint);
		if (blockId == 0) {
			villager.setBlockAndMetadata(cropPoint,Block.netherStalk.blockID,0);
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 100;
	}
}
