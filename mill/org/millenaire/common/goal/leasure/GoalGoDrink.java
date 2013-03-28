package org.millenaire.common.goal.leasure;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;



public class GoalGoDrink extends Goal {

	public GoalGoDrink() {
		super();
	}

	@Override
	public int actionDuration(MillVillager villager) throws Exception {
		return 10000;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return packDest(villager.getTownHall().getRandomLocationWithTag(Building.tagDrinking));
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		if ((villager.worldObj.getWorldTime()%24000) < 10000)
			return false;

		if (villager.hasDrunkToday)
			return false;

		final Point p=villager.getTownHall().getRandomLocationWithTag(Building.tagDrinking);

		if (p == null)
			return false;

		return (villager.getPos().distanceTo(p) > 5);
	}

	@Override
	public boolean performAction(MillVillager villager) {
		villager.hasDrunkToday=true;
		return MillCommonUtilities.chanceOn(20*30);
	}

	@Override
	public int priority(MillVillager villager) {

		if (villager.isReligious())
			return MillCommonUtilities.randomInt(20)-10;

		return MillCommonUtilities.randomInt(10)-7;
	}

}
