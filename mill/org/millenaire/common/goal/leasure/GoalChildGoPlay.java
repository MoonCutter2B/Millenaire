package org.millenaire.common.goal.leasure;

import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;

public class GoalChildGoPlay extends Goal {

	public GoalChildGoPlay() {
		super();
		leasure = true;
	}

	@Override
	public int actionDuration(final MillVillager villager) {
		return 10000;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return MillCommonUtilities.randomInt(5);
	}

}
