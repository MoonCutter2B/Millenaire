package org.millenaire.common.goal.leasure;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;



public class GoalGoPray extends Goal {

	public GoalGoPray() {
		super();
	}

	@Override
	public int actionDuration(MillVillager villager) throws Exception {
		return 30000;
	}

	@Override
	public boolean allowRandomMoves() {
		return false;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		if (villager.canMeditate()) {
			final Building temple=villager.getTownHall().getFirstBuildingWithTag(Building.tagPujas);

			if (temple!=null)
				return packDest(temple.getCraftingPos(),temple);
		}

		return packDest(villager.getTownHall().getRandomLocationWithTag(Building.tagPraying));
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		if (villager.hasPrayedToday && !villager.canMeditate())
			return false;

		final Point p=villager.getTownHall().getRandomLocationWithTag(Building.tagPraying);

		if (p == null)
			return false;

		return (villager.getPos().distanceTo(p) > 5);
	}

	@Override
	public boolean performAction(MillVillager villager) {

		villager.hasPrayedToday=true;

		return true;

	}

	@Override
	public int priority(MillVillager villager) {

		if (villager.canMeditate())
			return 200;

		if (villager.isPriest())
			return MillCommonUtilities.randomInt(50)-25;

		return MillCommonUtilities.randomInt(20)-18;
	}

}
