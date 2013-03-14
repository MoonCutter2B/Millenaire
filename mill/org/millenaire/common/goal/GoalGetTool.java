package org.millenaire.common.goal;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;


public class GoalGetTool extends Goal {
	
	public GoalGetTool() {
		maxSimultaneousInBuilding=2;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		for (final Building shop : villager.getTownHall().getShops()) {
			for (final InvItem key : villager.getToolsNeeded()) {
				if ((villager.countInv(key.id(),key.meta) == 0) && (shop.countGoods(key.id(),key.meta)>0) && validateDest(villager,shop))
					return packDest(shop.getSellingPos(),shop);
			}
		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {

		if (villager.getToolsNeeded().length==0)
			return false;


		for (final Building shop : villager.getTownHall().getShops()) {
			for (final InvItem key : villager.getToolsNeeded()) {
				if ((villager.countInv(key.id(),key.meta) == 0) && (shop.countGoods(key.id(),key.meta)>0) && validateDest(villager,shop)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final Building shop=villager.getGoalBuildingDest();

		if (shop==null)
			return true;


		for (final InvItem key : villager.getToolsNeeded()) {
			if ((villager.countInv(key.id(),key.meta) == 0) && (shop.countGoods(key.id(),key.meta)>0)) {
				villager.takeFromBuilding(shop, key.id(),key.meta, 1);
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 100;
	}


}
