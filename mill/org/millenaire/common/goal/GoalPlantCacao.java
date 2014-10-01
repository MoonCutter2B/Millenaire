package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;

public class GoalPlantCacao extends Goal {

	private static ItemStack[] cacao = new ItemStack[] { new ItemStack(
			Blocks.cocoa, 1) };

	private int getCocoaMeta(final World world, final Point p) {

		final Block var5 = p.getRelative(0, 0, -1).getBlock(world);
		final Block var6 = p.getRelative(0, 0, 1).getBlock(world);
		final Block var7 = p.getRelative(-1, 0, 0).getBlock(world);
		final Block var8 = p.getRelative(1, 0, 0).getBlock(world);
		byte meta = 0;

		if (var5 == Blocks.log) {
			meta = 2;
		}

		if (var6 == Blocks.log) {
			meta = 0;
		}

		if (var7 == Blocks.log) {
			meta = 1;
		}

		if (var8 == Blocks.log) {
			meta = 3;
		}

		return meta;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) {

		final Point p = villager.getHouse().getResManager()
				.getCocoaPlantingLocation();

		return packDest(p, villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) {
		return cacao;
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

		Block block = villager.getBlock(villager.getGoalDestPoint());

		final Point cropPoint = villager.getGoalDestPoint();

		block = villager.getBlock(cropPoint);
		if (block == Blocks.air) {
			villager.setBlockAndMetadata(cropPoint, Blocks.cocoa,
					getCocoaMeta(villager.worldObj, cropPoint));

			villager.swingItem();
		}

		return true;
	}

	@Override
	public int priority(final MillVillager villager) {
		return 120;
	}
}
