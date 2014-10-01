package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalHarvestWarts extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		return packDest(villager.getHouse().getResManager()
				.getNetherWartsHarvestLocation(), villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return villager.getBestHoeStack();
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {
		return getDestination(villager) != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		final Point cropPoint = villager.getGoalDestPoint().getAbove();

		if (villager.getBlock(cropPoint) == Blocks.nether_wart
				&& villager.getBlockMeta(cropPoint) == 3) {
			villager.setBlockAndMetadata(cropPoint, Blocks.air, 0);
			villager.getHouse().storeGoods(Items.nether_wart, 1);

			villager.swingItem();
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		final int p = 100 - villager.getHouse().countGoods(Items.nether_wart) * 4;

		return p;
	}

}
