package org.millenaire.common.goal;

import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;


public class GoalForeignMerchantKeepStall extends Goal {

	@Override
	public int actionDuration(MillVillager villager) throws Exception {
		return 60000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		if (villager.foreignMerchantStallId>=villager.getHouse().stalls.size())
			return null;

		return packDest(villager.getHouse().stalls.get(villager.foreignMerchantStallId),villager.getHouse());
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return true;
	}

	@Override
	public boolean lookAtPlayer() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {
		return MillCommonUtilities.chanceOn(20*30);
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return MillCommonUtilities.randomInt(50);
	}

}
