package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;

public class GoalDeliverGoodsHousehold extends Goal {


	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		return packDest(villager.getHouse().getSellingPos(),villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {

		final Vector<ItemStack> items=new Vector<ItemStack>();

		for (final MillVillager v : villager.getHouse().villagers) {

			for (final InvItem key : v.requiresGoods().keySet()) {
				if (villager.countInv(key.id(),key.meta)>0) {
					items.add(new ItemStack(key.id(),1,key.meta));
				}
			}

		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return false;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		for (final MillVillager v : villager.getHouse().villagers) {
			for (final InvItem key : v.requiresGoods().keySet()) {
				villager.putInBuilding(villager.getHouse(), key.id(),key.meta, 256);
			}

		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 100;
	}

}
