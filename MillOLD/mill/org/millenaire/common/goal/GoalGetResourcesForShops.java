package org.millenaire.common.goal;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.building.Building;

public class GoalGetResourcesForShops extends Goal {

	@Override
	public int actionDuration(final MillVillager villager) {
		return 2000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		return getDestination(villager, false);
	}

	public GoalInformation getDestination(final MillVillager villager, final boolean test) {

		boolean delayOver;

		if (!test) {
			delayOver = true;
		} else if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		for (final Building shop : villager.getTownHall().getShops()) {
			int nb = 0;
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {

					if (shop != villager.getHouse()) {

						final int nbcount = villager.getHouse().nbGoodAvailable(item, false, true);
						if (nbcount > 0) {

							nb += nbcount;
							if (delayOver || nb > 16) {
								return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
							}

						}
					}
					if (villager.getTownHall() != shop && villager.getTownHall().nbGoodAvailable(item, false, true) > 0) {
						nb += villager.getTownHall().nbGoodAvailable(item, false, true);

						if (delayOver || nb > 16) {
							return packDest(villager.getTownHall().getResManager().getSellingPos(), villager.getTownHall());
						}

					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {
		return getDestination(villager, true) != null;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		final Building dest = villager.getGoalBuildingDest();

		if (dest == null) {
			MLN.error(villager, "Invalid destination for GoalGetResourcesForShops goal: " + villager.getGoalBuildingDestPoint() + " (house: " + villager.getHouse().getPos() + ", TH: "
					+ villager.getTownHall().getPos() + "), pathDestPoint: " + villager.getGoalDestPoint());
			return true;
		}

		for (final Building shop : villager.getTownHall().getShops()) {
			if (!shop.getPos().equals(villager.getGoalDestPoint()) && villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					villager.takeFromBuilding(dest, item.getItem(), item.meta, dest.nbGoodAvailable(item, false, true));
				}
			}
		}
		return true;
	}

	@Override
	public int priority(final MillVillager villager) {

		int priority = 0;

		for (final Building shop : villager.getTownHall().getShops()) {
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					priority += villager.getHouse().countGoods(item.getItem(), item.meta) * 5;
					if (villager.getTownHall() != shop) {
						priority += villager.getTownHall().countGoods(item.getItem(), item.meta) * 5;
					}
				}
			}
		}

		return priority;
	}

}
