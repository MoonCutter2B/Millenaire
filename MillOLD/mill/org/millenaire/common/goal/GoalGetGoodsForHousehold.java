package org.millenaire.common.goal;

import java.util.HashMap;
import java.util.List;

import org.millenaire.common.InvItem;
import org.millenaire.common.MillVillager;
import org.millenaire.common.building.Building;

public class GoalGetGoodsForHousehold extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		return getDestination(villager, false);
	}

	public GoalInformation getDestination(final MillVillager villager, final boolean test) throws Exception {
		List<Building> buildings = null;

		boolean delayOver;

		if (!test) {
			delayOver = true;
		} else if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem, Integer> goods = v.requiresGoods();

			int nb = 0;

			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.getItem(), key.meta) < goods.get(key) / 2) {
					if (buildings == null) {
						buildings = villager.getTownHall().getBuildings();
					}

					for (final Building building : buildings) {
						final int nbav = building.nbGoodAvailable(key, false, false);
						if (nbav > 0 && building != villager.getHouse()) {
							nb += nbav;

							if (delayOver || nb > 16) {
								return packDest(building.getResManager().getSellingPos(), building);
							}
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {

		return getDestination(villager, true) != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public String nextGoal(final MillVillager villager) throws Exception {
		return Goal.deliverGoodsHousehold.key;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final Building shop = villager.getGoalBuildingDest();

		if (shop == null || shop == villager.getHouse()) {
			return true;
		}

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem, Integer> goods = v.requiresGoods();

			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.getItem(), key.meta) < goods.get(key)) {

					final int nb = Math.min(shop.nbGoodAvailable(key, false, false), goods.get(key));

					villager.takeFromBuilding(shop, key.getItem(), key.meta, nb);
				}
			}

		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {

		int nb = 0;

		final List<Building> shops = villager.getTownHall().getShops();

		for (final MillVillager v : villager.getHouse().villagers) {

			final HashMap<InvItem, Integer> goods = v.requiresGoods();

			for (final InvItem key : goods.keySet()) {
				if (villager.getHouse().countGoods(key.getItem(), key.meta) < goods.get(key) / 2) {
					for (final Building shop : shops) {
						final int nbav = shop.nbGoodAvailable(key, false, false);
						if (nbav > 0 && shop != villager.getHouse()) {
							nb += nbav;
						}
					}
				}
			}
		}

		return nb * 20;
	}

}
