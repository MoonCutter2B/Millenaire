package org.millenaire.common.goal;

import org.millenaire.common.MillVillager;


public class GoalHide extends Goal {

	@Override
	public boolean canBeDoneAtNight() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		if (villager.getPos().distanceToSquared(villager.getTownHall().getShelterPos())<=9)
			return null;

		return packDest(villager.getTownHall().getShelterPos(),villager.getTownHall());
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {

		return villager.getTownHall().underAttack;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 0;
	}

}
