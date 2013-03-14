package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;



public class GoalDefendVillage extends Goal {

	@Override
	public boolean canBeDoneAtNight() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		if (villager.getPos().distanceToSquared(villager.getTownHall().getDefendingPos())<=9)
			return null;

		return packDest(villager.getTownHall().getDefendingPos(),villager.getTownHall());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return new ItemStack[]{villager.getWeapon()};
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return true;
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
