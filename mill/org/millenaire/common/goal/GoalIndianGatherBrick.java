package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;



public class GoalIndianGatherBrick extends Goal {

	public GoalIndianGatherBrick() {
		maxSimultaneousInBuilding=2;
		townhallLimit.put(new InvItem(Mill.stone_decoration.blockID,1), 4096);
	}
	
	
	@Override
	public int actionDuration(MillVillager villager) {
		return 1000;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		final Vector<Point> vp=new Vector<Point>();
		final Vector<Point> buildingp=new Vector<Point>();
		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagKiln)) {
			final Point p=kiln.getFullBrickLocation();
			if (p!=null) {
				vp.add(p);
				buildingp.add(kiln.getPos());
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
		return new ItemStack[]{new ItemStack(Item.pickaxeWood.itemID,1,0)};
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		boolean delayOver;
		if (!villager.lastGoalTime.containsKey(this)) {
			delayOver=true;
		} else {
			delayOver=(villager.worldObj.getWorldTime()>(villager.lastGoalTime.get(this)+STANDARD_DELAY));
		}

		for (final Building kiln : villager.getTownHall().getBuildingsWithTag(Building.tagKiln)) {
			final int nb=kiln.getNbFullBrickLocation();

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
		if ((MillCommonUtilities.getBlock(villager.worldObj, villager.getGoalDestPoint())==Mill.stone_decoration.blockID) && (MillCommonUtilities.getBlockMeta(villager.worldObj, villager.getGoalDestPoint())==1)) {
			villager.addToInv(Mill.stone_decoration.blockID, MillCommonUtilities.getBlockMeta(villager.worldObj, villager.getGoalDestPoint()), 1);
			villager.setBlockAndMetadata(villager.getGoalDestPoint(),0,0);
			
			villager.swingItem();
			return false;
		} else
			return true;

	}

	@Override
	public int priority(MillVillager villager) {

		int p=100-(villager.getTownHall().nbGoodAvailable(Mill.stone_decoration.blockID, 1, false, false)*2);

		for (final MillVillager v : villager.getTownHall().villagers) {
			if (this.key.equals(v.goalKey)) {
				p=p/2;
			}
		}

		return p;
	}

	@Override
	public boolean unreachableDestination(MillVillager villager) {
		return false;
	}

}
