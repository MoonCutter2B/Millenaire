package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.item.Goods;

public class GoalMerchantVisitInn extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {
		return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {

		final List<ItemStack> items = new ArrayList<ItemStack>();

		for (final InvItem good : villager.getInventoryKeys()) {
			if (villager.countInv(good.getItem(), good.meta) > 0) {
				items.add(new ItemStack(good.getItem(), 1, good.meta));
			}
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {

		boolean delayOver;

		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver = true;
		} else {
			delayOver = villager.worldObj.getWorldTime() > villager.lastGoalTime.get(this) + STANDARD_DELAY;
		}

		int nb = 0;

		for (final InvItem good : villager.getInventoryKeys()) {
			final int nbcount = villager.countInv(good.getItem(), good.meta);
			if (nbcount > 0 && villager.getTownHall().nbGoodNeeded(good.getItem(), good.meta) == 0) {

				nb += nbcount;

				if (delayOver) {
					return true;
				}
				if (nb > 64) {
					return true;
				}
			}
		}

		for (final Goods good : villager.getTownHall().culture.goodsList) {
			if (villager.getHouse().countGoods(good.item.getItem(), good.item.meta) > 0
					&& villager.countInv(good.item.getItem(), good.item.meta) < villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta)) {
				if (MLN.LogMerchant >= MLN.MAJOR) {
					MLN.major(this, "Visiting the Inn to take imports");
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		String s = "";

		for (final InvItem good : villager.getInventoryKeys()) {
			if (villager.countInv(good.getItem(), good.meta) > 0 && villager.getTownHall().nbGoodNeeded(good.getItem(), good.meta) == 0) {
				final int nb = villager.putInBuilding(villager.getHouse(), good.getItem(), good.meta, 99999999);

				if (nb > 0) {
					s += ";" + good.getItem() + "/" + good.meta + "/" + nb;
				}
			}
		}
		if (s.length() > 0) {
			villager.getHouse().visitorsList.add("storedexports;" + villager.getName() + s);
		}

		s = "";

		for (final Goods good : villager.getTownHall().culture.goodsList) {
			final int nbNeeded = villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta);
			if (villager.countInv(good.item.getItem(), good.item.meta) < nbNeeded) {
				final int nb = villager.takeFromBuilding(villager.getHouse(), good.item.getItem(), good.item.meta, nbNeeded - villager.countInv(good.item.getItem(), good.item.meta));

				if (nb > 0) {
					s += ";" + good.item.getItem() + "/" + good.item.meta + "/" + nb;
				}
			}
		}

		if (s.length() > 0) {
			villager.getHouse().visitorsList.add("broughtimport;" + villager.getName() + s);
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

}
