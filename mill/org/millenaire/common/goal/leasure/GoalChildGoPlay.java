package org.millenaire.common.goal.leasure;

import org.millenaire.common.MillVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;



public class GoalChildGoPlay extends Goal {

	public GoalChildGoPlay() {
		super();
		leasure=true;
	}

	@Override
	public int actionDuration(MillVillager villager) {
		return 10000;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return null;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		return true;
	}


	@Override
	public boolean performAction(MillVillager villager) {
		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return MillCommonUtilities.randomInt(5);
	}

}
