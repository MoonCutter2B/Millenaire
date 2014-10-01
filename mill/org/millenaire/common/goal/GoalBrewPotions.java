package org.millenaire.common.goal;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalBrewPotions extends Goal {

	@Override
	public GoalInformation getDestination(final MillVillager villager)
			throws Exception {

		final int nbWarts = villager.getHouse().countGoods(Items.nether_wart);
		final int nbBottles = villager.getHouse()
				.countGoods(Items.glass_bottle);
		final int nbPotions = villager.getHouse().countGoods(Items.potionitem,
				-1);

		for (final Point p : villager.getHouse().getResManager().brewingStands) {

			final TileEntityBrewingStand brewingStand = p
					.getBrewingStand(villager.worldObj);

			if (brewingStand != null && brewingStand.getBrewTime() == 0) {
				if (brewingStand.getStackInSlot(3) == null && nbWarts > 0
						&& nbPotions < 64) {
					return packDest(p, villager.getHouse());
				}

				if (nbBottles > 2
						&& (brewingStand.getStackInSlot(0) == null
								|| brewingStand.getStackInSlot(1) == null || brewingStand
								.getStackInSlot(2) == null) && nbPotions < 64) {
					return packDest(p, villager.getHouse());
				}

				for (int i = 0; i < 3; i++) {
					if (brewingStand.getStackInSlot(i) != null
							&& brewingStand.getStackInSlot(i).getItem() == Items.potionitem
							&& brewingStand.getStackInSlot(i).getItemDamage() == 16) {
						return packDest(p, villager.getHouse());
					}
				}

			}
		}
		return null;
	}

	@Override
	public boolean isPossibleSpecific(final MillVillager villager)
			throws Exception {
		return getDestination(villager) != null;
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		final int nbWarts = villager.getHouse().countGoods(Items.nether_wart);
		final int nbBottles = villager.getHouse()
				.countGoods(Items.glass_bottle);
		final int nbPotions = villager.getHouse().countGoods(Items.potionitem);

		final TileEntityBrewingStand brewingStand = villager.getGoalDestPoint()
				.getBrewingStand(villager.worldObj);

		if (brewingStand == null) {
			return true;
		}

		if (brewingStand.getBrewTime() == 0) {
			if (brewingStand.getStackInSlot(3) == null && nbWarts > 0
					&& nbPotions < 64) {
				brewingStand.setInventorySlotContents(3, new ItemStack(
						Items.nether_wart, 1));
				villager.getHouse().takeGoods(Items.nether_wart, 1);
			}

			if (nbBottles > 2 && nbPotions < 64) {
				for (int i = 0; i < 3; i++) {
					if (brewingStand.getStackInSlot(i) == null) {
						brewingStand.setInventorySlotContents(i, new ItemStack(
								Items.potionitem, 1));
						villager.getHouse().takeGoods(Items.glass_bottle, 1);
					}
				}
			}

			for (int i = 0; i < 3; i++) {
				if (brewingStand.getStackInSlot(i) != null
						&& brewingStand.getStackInSlot(i).getItem() == Items.potionitem
						&& brewingStand.getStackInSlot(i).getItemDamage() == 16) {
					brewingStand.setInventorySlotContents(i, null);
					villager.getHouse().storeGoods(Items.potionitem, 16, 1);
				}
			}

		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 100;
	}

	@Override
	public boolean swingArms() {
		return true;
	}
}
