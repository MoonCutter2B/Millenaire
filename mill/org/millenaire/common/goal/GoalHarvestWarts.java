package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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

		if ((villager.getBlock(cropPoint)==Block.netherStalk.blockID) && (villager.getBlockMeta(cropPoint)==3)) {
			villager.setBlockAndMetadata(cropPoint,0,0);
			villager.getHouse().storeGoods(Item.netherStalkSeeds.itemID, 1);
			
			villager.swingItem();
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		final int p=100-(villager.getHouse().countGoods(Item.netherStalkSeeds.itemID)*4);

		return p;
	}

}
