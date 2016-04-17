package org.millenaire.common.goal;

import net.minecraft.block.BlockCocoa;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalHarvestCacao extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager) {
		final Point p = villager.getHouse().getResManager().getCocoaHarvestLocation();

		return packDest(p, villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return villager.getBestHoeStack();
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager) {

		return getDestination(villager).getDest() != null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(final MillVillager villager) {

		final Point cropPoint = villager.getGoalDestPoint();

		if (cropPoint.getBlock(villager.worldObj) == Blocks.cocoa) {
			final int meta = cropPoint.getMeta(villager.worldObj);

			if (BlockCocoa.func_149987_c(meta) >= 2) {
				villager.setBlockAndMetadata(cropPoint, Blocks.air, 0);

				int nbcrop = 2;
				final float irrigation = villager.getTownHall().getVillageIrrigation();
				final double rand = Math.random();
				if (rand < irrigation / 100) {
					nbcrop++;
				}
				villager.addToInv(Items.dye, 3, nbcrop);

				villager.swingItem();
			}
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 100;
	}

}
