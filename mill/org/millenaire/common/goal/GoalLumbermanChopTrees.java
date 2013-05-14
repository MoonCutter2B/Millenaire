package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorldInfo;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;



public class GoalLumbermanChopTrees extends Goal {
	
	public GoalLumbermanChopTrees() {
		this.maxSimultaneousInBuilding=1;
		this.townhallLimit.put(new InvItem(Block.wood.blockID,-1), 4096);
	}

	@Override
	public int actionDuration(MillVillager villager) {
		final int toolEfficiency=(int)villager.getBestAxe().efficiencyOnProperMaterial;
		return 1000-(toolEfficiency*40);
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {

		final Vector<Point> vp=new Vector<Point>();
		final Vector<Point> buildingp=new Vector<Point>();
		for (final Building grove : villager.getTownHall().getBuildingsWithTag(Building.tagGrove)) {
			final Point p=grove.getWoodLocation();
			if (p!=null) {
				vp.add(p);
				buildingp.add(grove.getPos());
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
		return villager.getBestAxeStack();
	}


	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) {
		for (final Building grove : villager.getTownHall().getBuildingsWithTag(Building.tagGrove)) {
			if (grove.getWoodCount()>16)
				return true;
		}

		return false;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		boolean woodFound=false;

		if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
			MLN.debug(this, "Attempting to gather wood at: "+villager.getGoalDestPoint());
		}

		final MillWorldInfo winfo=villager.getTownHall().winfo;

		for (int i=12;i>-12;i--) {
			for (int j=-3;j<4;j++) {
				for (int k=-3;k<4;k++) {
					final Point p=villager.getGoalDestPoint().getRelative(j, i, k);

					if (!winfo.isConstructionOrLoggingForbiddenHere(p)) {

						final int blockId=villager.getBlock(p);

						if ((blockId == Block.wood.blockID) || (blockId==Block.leaves.blockID)) {
							if (!woodFound) {
								if (blockId == Block.wood.blockID) {
									final int meta=villager.getBlockMeta(p);
									villager.setBlock(p,0);
									
									villager.swingItem();
									
									villager.addToInv(Block.wood.blockID, meta, 1);
									woodFound=true;

									if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
										MLN.debug(this, "Gathered wood at: "+villager.getGoalDestPoint());
									}
								} else {

									if (MillCommonUtilities.randomInt(4) == 0) {
										villager.addToInv(Block.sapling.blockID,MillCommonUtilities.getBlockMeta(villager.worldObj, p) & 3, 1);
									}
									villager.setBlock(p,0);
									
									villager.swingItem();

									if (villager.gathersApples() && MillCommonUtilities.chanceOn(16)) {
										villager.addToInv(Mill.ciderapple.itemID, 1);
									}


									if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
										MLN.debug(this,"Destroyed leaves at: "+villager.getGoalDestPoint());
									}
								}
							} else {
								if ((MLN.LogLumberman>=MLN.DEBUG) && villager.extraLog) {
									MLN.debug(this,"More wood found.");
								}
								return false;//still more wood to cut
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 100-villager.countInv(Block.wood.blockID, -1);
	}

	@Override
	public int range(MillVillager villager) {
		return ACTIVATION_RANGE+2;
	}
}
