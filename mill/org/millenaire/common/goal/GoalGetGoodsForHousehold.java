package org.millenaire.common.goal;

import java.util.HashMap;
import java.util.Vector;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;


public class GoalGetGoodsForHousehold extends Goal {

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		return getDestination(villager,false);
	}

	public GoalInformation getDestination(MillVillager villager, boolean test) throws Exception {
		Vector<Building> buildings=null;

		boolean delayOver;

		if (!test) {
			delayOver=true;
		} else if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem,Integer> goods=v.requiresGoods();

			int nb=0;

			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.id(),key.meta)<(goods.get(key)/2)) {
					if (buildings==null) {
						buildings=villager.getTownHall().getBuildings();
					}

					for (final Building building : buildings) {
						final int nbav=building.nbGoodAvailable(key.id(),key.meta,false, false);
						if ((nbav>0) &&  (building!=villager.getHouse())) {
							nb+=nbav;

							if (delayOver || nb>16)
								return packDest(building.getSellingPos(),building);
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {

		return (getDestination(villager,true)!=null);
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public String nextGoal(MillVillager villager) throws Exception {
		return Goal.deliverGoodsHousehold.key;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final Building shop=villager.getGoalBuildingDest();

		if ((shop==null) || (shop==villager.getHouse()))
			return true;

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem,Integer> goods=v.requiresGoods();

			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.id(),key.meta)<goods.get(key)) {

					final int nb=Math.min(shop.nbGoodAvailable(key.id(),key.meta,false, false),goods.get(key));

					villager.takeFromBuilding(shop, key.id(),key.meta, nb);
				}
			}

		}

		return true;
	}


	@Override
	public int priority(MillVillager villager) throws Exception {

		int nb=0;

		final Vector<Building> shops=villager.getTownHall().getShops();

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem,Integer> goods=v.requiresGoods();



			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.id(),key.meta)<(goods.get(key)/2)) {
					for (final Building shop : shops) {
						final int nbav=shop.nbGoodAvailable(key.id(),key.meta,false, false);
						if ((nbav>0) &&  (shop!=villager.getHouse())) {
							nb+=nbav;
						}
					}
				}
			}
		}


		return nb*20;
	}

}
