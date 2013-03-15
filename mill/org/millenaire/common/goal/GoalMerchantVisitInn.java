package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.item.Goods;


public class GoalMerchantVisitInn extends Goal {


	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {
		return packDest(villager.getHouse().getSellingPos(),villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {

		final Vector<ItemStack> items=new Vector<ItemStack>();

		for (final InvItem good : villager.getInventoryKeys()) {
			if (villager.countInv(good.id(),good.meta)>0) {
				items.add(new ItemStack(good.id(),1,good.meta));
			}
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {

		boolean delayOver;

		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		int nb=0;

		for (final InvItem good : villager.getInventoryKeys()) {
			final int nbcount=villager.countInv(good.id(),good.meta);
			if ((nbcount>0) && (villager.getTownHall().nbGoodNeeded(good.id(), good.meta)==0)) {

				nb+=nbcount;

				if (delayOver)
					return true;
				if (nb>64)
					return true;
			}
		}

		for (final Goods good : villager.getTownHall().culture.goodsVector) {
			if ((villager.getHouse().countGoods(good.item.id(),good.item.meta)>0) && (villager.countInv(good.item.id(),good.item.meta)<villager.getTownHall().nbGoodNeeded(good.item.id(), good.item.meta))) {
				if (MLN.Merchant>=MLN.MAJOR) {
					MLN.major(this, "Visiting the Inn to take imports");
				}
				return true;
			}
		}

		return false;
	}


	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		String s="";

		for (final InvItem good : villager.getInventoryKeys()) {
			if ((villager.countInv(good.id(),good.meta)>0) && (villager.getTownHall().nbGoodNeeded(good.id(), good.meta)==0)) {
				final int nb=villager.putInBuilding(villager.getHouse(), good.id(),good.meta, 99999999);

				if (nb>0) {
					s+=";"+good.id()+"/"+good.meta+"/"+nb;
				}
			}
		}
		if (s.length()>0) {
			villager.getHouse().visitorsList.add("storedexports;"+villager.getName()+s);
		}

		s="";

		for (final Goods good : villager.getTownHall().culture.goodsVector) {
			final int nbNeeded=villager.getTownHall().nbGoodNeeded(good.item.id(), good.item.meta);
			if (villager.countInv(good.item.id(),good.item.meta)<nbNeeded) {
				final int nb=villager.takeFromBuilding(villager.getHouse(), good.item.id(),good.item.meta, nbNeeded-villager.countInv(good.item.id(),good.item.meta));

				if (nb>0) {
					s+=";"+good.item.id()+"/"+good.item.meta+"/"+nb;
				}
			}
		}

		if (s.length()>0) {
			villager.getHouse().visitorsList.add("broughtimport;"+villager.getName()+s);
		}


		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 100;
	}

}