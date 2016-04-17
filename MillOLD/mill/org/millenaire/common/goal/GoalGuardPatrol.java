package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalGuardPatrol extends Goal {

	GoalGuardPatrol() {
		super();
		leasure = true;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		return packDest(villager.getTownHall().getRandomLocationWithTag(Building.tagPatrol));
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return new ItemStack[] { villager.getWeapon() };
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		if (villager.lastGoalTime.containsKey(this) && villager.lastGoalTime.get(this) > villager.worldObj.getWorldTime() + STANDARD_DELAY) {
			return false;
		}

		final Point p = villager.getTownHall().getRandomLocationWithTag(Building.tagPatrol);

		if (p == null) {
			return false;
		}

		return villager.getPos().distanceTo(p) > 5;
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		return MillCommonUtilities.chanceOn(20 * 30);
	}

	@Override
	public int priority(final MillVillager villager) {
		return MillCommonUtilities.randomInt(20) - 10;
	}

	@Override
	public int range(final MillVillager villager) {

		return 100;
	}

}
