package org.millenaire.common.goal;

import org.millenaire.common.MillVillager;

public class GoalHide extends Goal {

	@Override
	public boolean canBeDoneAtNight() {
		return true;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		if (villager.getPos().distanceToSquared(villager.getTownHall().getResManager().getShelterPos()) <= 9) {
			return null;
		}

		return packDest(villager.getTownHall().getResManager().getShelterPos(), villager.getTownHall());
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public boolean isStillValidSpecific(final MillVillager villager) throws Exception {

		return villager.getTownHall().underAttack;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 0;
	}

}
