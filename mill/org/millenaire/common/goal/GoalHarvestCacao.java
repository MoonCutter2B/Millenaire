package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;



public class GoalHarvestCacao extends Goal {

	@Override
	public GoalInformation getDestination(MillVillager villager) {
Point p=villager.getHouse().getCocoaHarvestLocation();
		
		return packDest(p,villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return villager.getBestHoeStack();
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

		Point cropPoint=villager.getGoalDestPoint();
		
		if (cropPoint.getId(villager.worldObj)==Block.cocoaPlant.blockID) {
			int meta=cropPoint.getMeta(villager.worldObj);
			
			if (BlockCocoa.func_72219_c(meta)>=2) {
				villager.setBlockAndMetadata(cropPoint,0,0);

				int nbcrop=2;
				final float irrigation=villager.getTownHall().getVillageIrrigation();
				final double rand=Math.random();
				if (rand<(irrigation/100)) {
					nbcrop++;
				}
				villager.addToInv(Item.dyePowder.itemID, 3, nbcrop);
				
				villager.swingItem();
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 100;
	}

}
