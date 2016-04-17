package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.millenaire.common.InvItem;
import org.millenaire.common.MillVillager;

public class GoalDeliverGoodsHousehold extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) throws Exception {

		final List<ItemStack> items = new ArrayList<ItemStack>();

		for (final MillVillager v : villager.getHouse().villagers) {

			for (final InvItem key : v.requiresGoods().keySet()) {
				if (villager.countInv(key.getItem(), key.meta) > 0) {
					items.add(new ItemStack(key.getItem(), 1, key.meta));
				}
			}

		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		for (final MillVillager v : villager.getHouse().villagers) {
			for (final InvItem key : v.requiresGoods().keySet()) {
				villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256);
			}

		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

}
