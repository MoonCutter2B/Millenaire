package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.building.Building;
import org.millenaire.common.item.Goods;

public class GoalMerchantVisitBuilding extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager)
			throws Exception {

		for (final Goods good : villager.getTownHall().culture.goodsList) {
			if (villager.countInv(good.item.getItem(), good.item.meta) > 0
					&& villager.getTownHall().nbGoodNeeded(good.item.getItem(),
							good.item.meta) > 0) {

				if (MLN.LogMerchant >= MLN.DEBUG) {
					MLN.debug(
							villager,
							"TH needs "
									+ villager.getTownHall()
											.nbGoodNeeded(good.item.getItem(),
													good.item.meta)
									+ " good "
									+ good.item.getName()
									+ ", merchant has "
									+ villager.countInv(good.item.getItem(),
											good.item.meta));
				}
				return packDest(villager.getTownHall().getResManager()
						.getSellingPos(), villager.getTownHall());
			}
		}

		final HashMap<Goods, Integer> neededGoods = villager.getTownHall()
				.getImportsNeededbyOtherVillages();

		for (final Building shop : villager.getTownHall().getBuildings()) {
			for (final Goods good : villager.getTownHall().culture.goodsList) {
				if (!shop.isInn
						&& shop.nbGoodAvailable(good.item.getItem(),
								good.item.meta, true, false) > 0
						&& neededGoods.containsKey(good)
						&& neededGoods.get(good) > villager
								.getHouse()
								.countGoods(good.item.getItem(), good.item.meta)
								+ villager.countInv(good.item.getItem(),
										good.item.meta)) {

					if (MLN.LogMerchant >= MLN.DEBUG) {
						MLN.debug(
								villager,
								"Shop "
										+ shop
										+ " has "
										+ shop.nbGoodAvailable(
												good.item.getItem(),
												good.item.meta, true, false)
										+ " good to pick up.");
					}

					return packDest(shop.getResManager().getSellingPos(), shop);
				}
			}
		}

		return null;
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {

		final List<ItemStack> items = new ArrayList<ItemStack>();

		for (final InvItem item : villager.getInventoryKeys()) {
			if (villager.countInv(item) > 0) {
				items.add(new ItemStack(item.getItem(), 1, item.meta));
			}
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager)
			throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final Building shop = villager.getGoalBuildingDest();
		final HashMap<Goods, Integer> neededGoods = villager.getTownHall()
				.getImportsNeededbyOtherVillages();

		if (shop == null || shop.isInn) {
			return true;
		}

		if (shop.isTownhall) {
			for (final Goods good : villager.getTownHall().culture.goodsList) {
				final int nbNeeded = shop.nbGoodNeeded(good.item.getItem(),
						good.item.meta);
				if (nbNeeded > 0) {
					final int nb = villager.putInBuilding(shop,
							good.item.getItem(), good.item.meta, nbNeeded);
					if (nb > 0 && MLN.LogMerchant >= MLN.MINOR) {
						MLN.minor(shop, villager + " delivered " + nb + " "
								+ good.getName() + ".");
					}
				}
			}
		}

		for (final Goods good : villager.getTownHall().culture.goodsList) {
			if (neededGoods.containsKey(good)) {
				if (shop.nbGoodAvailable(good.item.getItem(), good.item.meta,
						true, false) > 0
						&& villager.getHouse().countGoods(good.item.getItem(),
								good.item.meta)
								+ villager.countInv(good.item.getItem(),
										good.item.meta) < neededGoods.get(good)) {

					int nb = Math.min(
							shop.nbGoodAvailable(good.item.getItem(),
									good.item.meta, true, false),
							neededGoods.get(good)
									- villager.getHouse()
											.countGoods(good.item.getItem(),
													good.item.meta)
									- villager.countInv(good.item.getItem(),
											good.item.meta));
					nb = villager.takeFromBuilding(shop, good.item.getItem(),
							good.item.meta, nb);
					if (MLN.LogMerchant >= MLN.MINOR) {
						MLN.minor(shop,
								villager + " took " + nb + " " + good.getName()
										+ " for trading.");
					}
				}
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

}
