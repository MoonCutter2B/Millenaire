package org.millenaire.common.goal;

import net.minecraft.item.Item;

import org.millenaire.common.InvItem;
import org.millenaire.common.MillVillager;
import org.millenaire.common.building.Building;

public class GoalGetTool extends Goal {

	private static final Item[][] classes = new Item[][] { MillVillager.axes, MillVillager.pickaxes, MillVillager.shovels, MillVillager.hoes, MillVillager.helmets, MillVillager.chestplates,
			MillVillager.legs, MillVillager.boots, MillVillager.weaponsSwords, MillVillager.weaponsRanged };

	public GoalGetTool() {
		maxSimultaneousInBuilding = 2;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		for (final Building shop : villager.getTownHall().getShops()) {
			for (final InvItem key : villager.getToolsNeeded()) {
				if (villager.countInv(key.getItem(), key.meta) == 0 && shop.countGoods(key.getItem(), key.meta) > 0 && !hasBetterTool(villager, key) && validateDest(villager, shop)) {
					return packDest(shop.getResManager().getSellingPos(), shop);
				}
			}
		}

		return null;
	}

	private boolean hasBetterTool(final MillVillager villager, final InvItem key) {

		if (key.meta > 0) {
			return false;
		}

		for (final Item[] toolclass : classes) {

			int targetPos = -1;

			for (int i = 0; i < toolclass.length && targetPos == -1; i++) {
				if (toolclass[i] == key.getItem()) {
					targetPos = i;
				}
			}

			if (targetPos != -1) {
				for (int i = 0; i < targetPos; i++) {
					if (villager.countInv(toolclass[i], 0) > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) throws Exception {

		if (villager.getToolsNeeded().length == 0) {
			return false;
		}

		for (final Building shop : villager.getTownHall().getShops()) {
			for (final InvItem key : villager.getToolsNeeded()) {
				if (villager.countInv(key.getItem(), key.meta) == 0 && shop.countGoods(key.getItem(), key.meta) > 0 && !hasBetterTool(villager, key) && validateDest(villager, shop)) {
					return true;
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
	public boolean performAction(final MillVillager villager) throws Exception {

		final Building shop = villager.getGoalBuildingDest();

		if (shop == null) {
			return true;
		}

		for (final InvItem key : villager.getToolsNeeded()) {
			if (villager.countInv(key.getItem(), key.meta) == 0 && shop.countGoods(key.getItem(), key.meta) > 0 && !hasBetterTool(villager, key)) {
				villager.takeFromBuilding(shop, key.getItem(), key.meta, 1);
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

}
