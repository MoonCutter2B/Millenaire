package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;



public class GoalIndianPlantSugarCane extends Goal {

	private static ItemStack[] sugarcane=new ItemStack[]{new ItemStack(Items.reeds, 1)};

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		final Vector<Point> vp=new Vector<Point>();
		final Vector<Point> buildingp=new Vector<Point>();
		for (final Building plantation : villager.getTownHall().getBuildingsWithTag(Building.tagSugarPlantation)) {
			final Point p=plantation.getSugarCanePlantingLocation();
			if (p!=null) {
				vp.add(p);
				buildingp.add(plantation.getPos());
			}
		}

		if (vp.isEmpty())
			return null;

		Point p=vp.firstElement();
		Point buildingP=buildingp.firstElement();

		for (int i=1;i<vp.size();i++) {
			if (vp.get(i).horizontalDistanceToSquared(villager) < p.horizontalDistanceToSquared(villager)) {
				p=vp.get(i);
				buildingP=buildingp.get(i);
			}
		}
		return packDest(p,buildingP);
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
		return sugarcane;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {

		int nbsimultaneous=0;
		for (final MillVillager v : villager.getTownHall().villagers) {
			if ((v != villager) && this.key.equals(v.goalKey)) {
				nbsimultaneous++;
			}
		}
		if (nbsimultaneous>2)
			return false;

		boolean delayOver;
		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagSugarPlantation)) {
			final int nb=kiln.getNbSugarCanePlantingLocation();

			if ((nb>0) && delayOver)
				return true;
			if (nb>4)
				return true;
		}
		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) {

		Block block=villager.getBlock(villager.getGoalDestPoint());

		final Point cropPoint=villager.getGoalDestPoint().getAbove();

		block=villager.getBlock(cropPoint);
		if (((block == Blocks.air) || (block == Blocks.leaves))) {
			villager.setBlock(cropPoint,Blocks.reeds);
			
			villager.swingItem();
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		int p=120;

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (this.key.equals(v.goalKey)) {
				p=p/2;
			}
		}

		return p;
	}
}
