package org.millenaire.common.goal;

import net.minecraft.entity.item.EntityItem;

import org.millenaire.common.InvItem;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public class GoalGatherGoods extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		final EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
		if (item == null) {
			return null;
		}

		return packDest(new Point(item));
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		if (villager.getGoodsToCollect().length == 0) {
			return false;
		}

		final EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
		return item != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {
		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 100;
	}

	@Override
	public int range(final MillVillager villager) {
		return 5;
	}

	@Override
	public boolean stuckAction(final MillVillager villager) {
		final InvItem[] goods = villager.getGoodsToCollect();

		if (goods != null) {
			final EntityItem item = MillCommonUtilities.getClosestItemVertical(villager.worldObj, villager.getGoalDestPoint(), goods, 3, 20);
			if (item != null) {
				item.setDead();
				villager.addToInv(item.getEntityItem().getItem(), item.getEntityItem().getItemDamage(), 1);
				return true;
			}
		}
		return false;
	}
}
