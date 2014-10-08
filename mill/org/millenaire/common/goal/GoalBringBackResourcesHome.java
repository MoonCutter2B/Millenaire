package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;

public class GoalBringBackResourcesHome extends Goal {

	@Override
	public int actionDuration(final MillVillager villager) {
		return 2000;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {

		final List<ItemStack> items = new ArrayList<ItemStack>();

		for (final InvItem key : villager.getInventoryKeys()) {
			for (final InvItem key2 : villager.getGoodsToBringBackHome()) {
				if (key2.equals(key)) {
					items.add(new ItemStack(key.getItem(), 1, key.meta));
				}
			}
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		if (villager.getGoodsToBringBackHome().length == 0) {
			return false;
		}

		int nb = 0;

		boolean delayOver;

		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		for (final InvItem key : villager.getInventoryKeys()) {
			if (villager.countInv(key) > 0) {
				for (final InvItem key2 : villager.getGoodsToBringBackHome()) {
					if (key2.matches(key)) {
						nb += villager.countInv(key);

						if (delayOver) {
							return true;
						}
						if (nb > 16) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		for (final InvItem key : villager.getInventoryKeys()) {
			for (final InvItem key2 : villager.getGoodsToBringBackHome()) {
				if (key2.matches(key)) {
					villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256);
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {

		int nbGoods = 0;

		for (final InvItem key : villager.getInventoryKeys()) {
			for (final InvItem key2 : villager.getGoodsToBringBackHome()) {
				if (key2.matches(key)) {
					nbGoods += villager.countInv(key);
				}
			}
		}

		return 10 + nbGoods * 3;
	}
}
