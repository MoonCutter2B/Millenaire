package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;


public class GoalDeliverResourcesShop extends Goal {

	@Override
	public int actionDuration(MillVillager villager) {
		return 2000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		return getDestination(villager,false);
	}

	public GoalInformation getDestination(MillVillager villager,boolean test) {

		boolean delayOver;

		if (!test) {
			delayOver=true;
		} else if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final Building shop : villager.getTownHall().getShops()) {

			int nb=0;

			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					final int nbcount=villager.countInv(item.getItem(),item.meta);
					if (nbcount>0) {
						nb+=nbcount;
						if (delayOver)
							return packDest(shop.getSellingPos(),shop);
						if (nb>16)
							return packDest(shop.getSellingPos(),shop);
					}
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {

		final Vector<ItemStack> items=new Vector<ItemStack>();

		Building shop=villager.getGoalBuildingDest();

		if( shop!=null) {
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					if (villager.countInv(item.getItem(),item.meta)>0) {
						items.add(new ItemStack(item.getItem(),1,item.meta));
					}
				}
			}
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		return (getDestination(villager,true)!=null);

	}

	@Override
	public boolean performAction(MillVillager villager) {

		Building shop=villager.getGoalBuildingDest();

		if( shop!=null) {
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					villager.putInBuilding(shop, item.getItem(),item.meta, 256);
				}
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		int priority=0;

		for (final Building shop : villager.getTownHall().getShops()) {
			if (villager.getCulture().shopNeeds.containsKey(shop.location.shop)) {
				for (final InvItem item : villager.getCulture().shopNeeds.get(shop.location.shop)) {
					priority+=villager.countInv(item.getItem(),item.meta)*10;
				}
			}
		}

		return priority;
	}

}
