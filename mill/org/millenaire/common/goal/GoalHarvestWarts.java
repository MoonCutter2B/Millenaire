package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalHarvestWarts extends Goal {

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return packDest(villager.getHouse().getNetherWartsHarvestLocation(),villager.getHouse());
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

		final Point cropPoint=villager.getGoalDestPoint().getAbove();

		if ((villager.getBlock(cropPoint)==Blocks.nether_wart) && (villager.getBlockMeta(cropPoint)==3)) {
			villager.setBlockAndMetadata(cropPoint,Blocks.air,0);
			villager.getHouse().storeGoods(Items.nether_wart, 1);
			
			villager.swingItem();
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		final int p=100-(villager.getHouse().countGoods(Items.nether_wart)*4);

		return p;
	}

}
