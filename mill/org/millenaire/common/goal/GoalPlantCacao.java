package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;



public class GoalPlantCacao extends Goal {

	private static ItemStack[] cacao=new ItemStack[]{new ItemStack(Block.cocoaPlant, 1)};

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		
		Point p=villager.getHouse().getCocoaPlantingLocation();
		
		return packDest(p,villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return cacao;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		return getDestination(villager).getDest()!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) {

		int blockId=villager.getBlock(villager.getGoalDestPoint());

		final Point cropPoint=villager.getGoalDestPoint();

		blockId=villager.getBlock(cropPoint);
		if (blockId == 0) {
			villager.setBlockAndMetadata(cropPoint,Block.cocoaPlant.blockID,getCocoaMeta(villager.worldObj,cropPoint));
			
			villager.swingItem();
		}

		return true;
	}
	
	private int getCocoaMeta(World world, Point p) {

		final int var5 = p.getRelative(0, 0, -1).getId(world);
		final int var6 = p.getRelative(0, 0, 1).getId(world);
		final int var7 = p.getRelative(-1, 0, 0).getId(world);
		final int var8 = p.getRelative(1, 0, 0).getId(world);
		byte meta = 0;

		if (var5==Block.wood.blockID)
		{
			meta = 2;
		}

		if (var6==Block.wood.blockID)
		{
			meta = 0;
		}

		if (var7==Block.wood.blockID)
		{
			meta = 1;
		}

		if (var8==Block.wood.blockID)
		{
			meta = 3;
		}

		return meta;
	}

	@Override
	public int priority(MillVillager villager) {
		return 120;
	}
}
