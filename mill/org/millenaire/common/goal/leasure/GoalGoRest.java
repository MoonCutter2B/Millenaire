package org.millenaire.common.goal.leasure;

import org.millenaire.common.MillVillager;
import org.millenaire.common.goal.Goal;

public class GoalGoRest extends Goal {

	public GoalGoRest() {
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
		return packDest(villager.getHouse().getResManager().getSleepingPos());
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {
		return villager.getHouse().getPos().distanceTo(villager) > 5;
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 0;
	}

	@Override
	public int range(final MillVillager villager) {
		return 10;
	}

}
