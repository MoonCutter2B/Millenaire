package org.millenaire.common.goal;

import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalForeignMerchantKeepStall extends Goal {

	@Override
	public int actionDuration(final MillVillager villager) throws Exception {
		return 60000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		if (villager.foreignMerchantStallId >= villager.getHouse().getResManager().stalls.size()) {
			return null;
		}

		return packDest(villager.getHouse().getResManager().stalls.get(villager.foreignMerchantStallId), villager.getHouse());
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return true;
	}

	@Override
	public boolean lookAtPlayer() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {
		return MillCommonUtilities.chanceOn(20 * 30);
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return MillCommonUtilities.randomInt(50);
	}

}
