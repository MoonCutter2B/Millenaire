package org.millenaire.common.goal;

import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.construction.BuildingPlan;


public class GoalGetResourcesForBuild extends Goal {


	@Override
	public int actionDuration(MillVillager villager) {
		return 2000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return packDest(villager.getTownHall().getSellingPos(),villager.getTownHall());
	}


	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		if (!((villager.getTownHall().builder == null) && (villager.getTownHall().buildingLocationIP != null) && (villager.getTownHall().getBblocks() != null)))
			return false;

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (Goal.getResourcesForBuild.key.equals(v.goalKey) || Goal.construction.key.equals(v.goalKey))
				return false;
		}

		return true;
	}


	@Override
	public boolean isStillValidSpecific(MillVillager villager) throws Exception {


		if (!(villager.getTownHall().builder == null) && (villager.getTownHall().builder!=villager))
			return false;
		return true;
	}

	@Override
	public String nextGoal(MillVillager villager) {
		return Goal.construction.key;
	}

	@Override
	public void onAccept(MillVillager villager) {
		villager.getTownHall().builder=villager;
	}

	@Override
	public boolean performAction(MillVillager villager) {

		if (villager.getTownHall().getCurrentBuildingPlan() == null)
			return true;

		if (villager.getTownHall().canAffordBuild(villager.getTownHall().getCurrentBuildingPlan())) {
			final BuildingPlan plan = villager.getTownHall().getCurrentBuildingPlan();

			for (final InvItem key : plan.resCost.keySet()) {
				villager.takeFromBuilding(villager.getTownHall(), key.id(),key.meta, plan.resCost.get(key));
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 100;
	}
}
