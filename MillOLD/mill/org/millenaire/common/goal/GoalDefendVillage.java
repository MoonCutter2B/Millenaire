package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;

public class GoalDefendVillage extends Goal {

	@Override
	public boolean canBeDoneAtNight() {
		return true;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		if (villager.getPos().distanceToSquared(villager.getTownHall().getResManager().getDefendingPos()) <= 9) {
			return null;
		}

		return packDest(villager.getTownHall().getResManager().getDefendingPos(), villager.getTownHall());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return new ItemStack[] { villager.getWeapon() };
	}

	@Override
	public boolean isFightingGoal() {
		return true;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return true;
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
