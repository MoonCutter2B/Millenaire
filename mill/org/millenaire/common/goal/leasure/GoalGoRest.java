package org.millenaire.common.goal.leasure;

import org.millenaire.common.MillVillager;
import org.millenaire.common.goal.Goal;



public class GoalGoRest extends Goal {

	public GoalGoRest() {
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
		return packDest(villager.getHouse().getSleepingPos());
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		return (villager.getHouse().getPos().distanceTo(villager) > 5);
	}

	@Override
	public boolean performAction(MillVillager villager) {
		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 0;
	}

	@Override
	public int range(MillVillager villager) {
		return 10;
	}

}
