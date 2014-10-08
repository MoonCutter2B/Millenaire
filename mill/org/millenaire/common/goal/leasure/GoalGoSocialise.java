package org.millenaire.common.goal.leasure;

import java.util.ArrayList;
import java.util.List;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;

public class GoalGoSocialise extends Goal {

	public GoalGoSocialise() {
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

		Point dest = null;
		Building destB = null;

		final List<Building> possibleDests = new ArrayList<Building>();

		for (final Building b : villager.getTownHall().getBuildings()) {
			if (b.location.tags.contains(Building.tagLeasure)) {
				possibleDests.add(b);
			}

		}

		if (possibleDests.isEmpty()) {
			return null;
		}

		destB = possibleDests.get(MillCommonUtilities.randomInt(possibleDests.size()));
		dest = destB.getResManager().getLeasurePos();

		if (dest == null) {
			dest = villager.getTownHall().getResManager().getLeasurePos();
			destB = villager.getTownHall();
		}

		return packDest(dest, destB);
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 5;
	}

	@Override
	public int range(final MillVillager villager) {
		return 5;
	}

}
