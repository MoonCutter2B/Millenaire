package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalChildBecomeAdult extends Goal {

	public GoalChildBecomeAdult() {
		maxSimultaneousInBuilding = 1;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws MillenaireException {

		if (villager.size < 20) {
			return null;
		}

		final List<Point> possibleDest = new ArrayList<Point>();
		final List<Point> possibleDestBuilding = new ArrayList<Point>();
		int maxPriority = 0;

		for (final Building house : villager.getTownHall().getBuildings()) {

			if (house != null && !house.equals(villager.getHouse()) && house.isHouse()) {

				if (house.canChildMoveIn(villager.gender, villager.familyName) && house.location.priorityMoveIn >= maxPriority && validateDest(villager, house)) {
					if (house.location.priorityMoveIn > maxPriority) {
						possibleDest.clear();
						possibleDestBuilding.clear();
						maxPriority = house.location.priorityMoveIn;
					}
					possibleDest.add(house.getResManager().getSleepingPos());
					possibleDestBuilding.add(house.getPos());
				}
			}
		}

		if (possibleDest.size() > 0) {

			final int rand = MillCommonUtilities.randomInt(possibleDest.size());

			return packDest(possibleDest.get(rand), possibleDestBuilding.get(rand));

		}

		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws MillenaireException {
		return getDestination(villager) != null;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws MillenaireException {

		final Building house = villager.getGoalBuildingDest();

		if (house != null) {
			if (house.canChildMoveIn(villager.gender, villager.familyName)) {
				if (MLN.LogChildren >= MLN.MAJOR) {
					MLN.major(this, "Adding new adult to house of type " + house.location + ". Gender: " + villager.gender);
				}
				house.addAdult(villager);
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 100;
	}

}
