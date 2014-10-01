package org.millenaire.common.goal.leasure;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;

public class GoalGoPray extends Goal {

	public GoalGoPray() {
		super();
	}

	@Override
	public int actionDuration(final MillVillager villager) throws Exception {
		return 30000;
	}

	@Override
	public boolean allowRandomMoves() {
		return false;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {

		if (villager.canMeditate()) {
			final Building temple = villager.getTownHall()
					.getFirstBuildingWithTag(Building.tagPujas);

			if (temple != null) {
				return packDest(temple.getResManager().getCraftingPos(), temple);
			}
		}

		if (villager.canPerformSacrifices()) {
			final Building temple = villager.getTownHall()
					.getFirstBuildingWithTag(Building.tagSacrifices);

			if (temple != null) {
				return packDest(temple.getResManager().getCraftingPos(), temple);
			}
		}

		return packDest(villager.getTownHall().getRandomLocationWithTag(
				Building.tagPraying));
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		if (villager.hasPrayedToday && !villager.canMeditate()
				&& !villager.canPerformSacrifices()) {
			return false;
		}

		final Point p = villager.getTownHall().getRandomLocationWithTag(
				Building.tagPraying);

		if (p == null) {
			return false;
		}

		return villager.getPos().distanceTo(p) > 5;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		villager.hasPrayedToday = true;

		return true;

	}

	@Override
	public int priority(final MillVillager villager) {

		if (villager.canMeditate() || villager.canPerformSacrifices()) {
			return 200;
		}

		if (villager.isPriest()) {
			return MillCommonUtilities.randomInt(50) - 25;
		}

		return MillCommonUtilities.randomInt(20) - 18;
	}

}
