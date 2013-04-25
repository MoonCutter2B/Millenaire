package org.millenaire.common.goal;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;



public class GoalGetResourcesForShops extends Goal {

	@Override
	public int actionDuration(MillVillager villager) {
		return 2000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return getDestination(villager,false);
	}

	public GoalInformation getDestination(MillVillager villager, boolean test) {

		boolean delayOver;

		if (!test) {
			delayOver=true;
		} else if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final Building shop : villager.getTownHall().getShops()) {
			int nb=0;
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {

					if (shop!=villager.getHouse()) {

						final int nbcount=villager.getHouse().nbGoodAvailable(item.id(), item.meta, false, true);
						if (nbcount>0) {

							nb+=nbcount;
							if (delayOver || nb>16)
								return packDest(villager.getHouse().getSellingPos(),villager.getHouse());

						}
					}
					if ((villager.getTownHall()!=shop) && (villager.getTownHall().nbGoodAvailable(item.id(),item.meta,false, true)>0)) {
						nb+=villager.getTownHall().nbGoodAvailable(item.id(),item.meta,false, true);

						if (delayOver || nb>16)
							return packDest(villager.getTownHall().getSellingPos(),villager.getTownHall());

					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		return (getDestination(villager,true)!=null);
	}

	@Override
	public boolean performAction(MillVillager villager) {

		final Building dest=villager.getGoalBuildingDest();

		if (dest==null) {
			MLN.error(villager, "Invalid destination for GoalGetResourcesForShops goal: "+villager.getGoalBuildingDestPoint()+" (house: "+villager.getHouse().getPos()+", TH: "+villager.getTownHall().getPos()+"), pathDestPoint: "+villager.getGoalDestPoint());
			return true;
		}

		
		for (final Building shop : villager.getTownHall().getShops()) {
			if (!shop.getPos().equals(villager.getGoalDestPoint()) && villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					villager.takeFromBuilding(dest, item.id(), item.meta, dest.nbGoodAvailable(item.id(), item.meta, false, true));
				}
			}
		}
		return true;
	}


	@Override
	public int priority(MillVillager villager) {

		int priority=0;

		for (final Building shop : villager.getTownHall().getShops()) {
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					priority+=villager.getHouse().countGoods(item.id(),item.meta)*5;
					if (villager.getTownHall()!=shop) {
						priority+=villager.getTownHall().countGoods(item.id(),item.meta)*5;
					}
				}
			}
		}

		return priority;
	}

}
