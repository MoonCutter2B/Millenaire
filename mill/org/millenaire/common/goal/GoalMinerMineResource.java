package org.millenaire.common.goal;

import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;


public class GoalMinerMineResource extends Goal {

	@Override
	public int actionDuration(MillVillager villager) {

		final Block block=villager.getBlock(villager.getGoalDestPoint());

		if ((block == Blocks.stone) || (block == Blocks.sandstone)) {
			final int toolEfficiency=(int)villager.getBestPickaxe().getDigSpeed(new ItemStack(villager.getBestPickaxe(),1), Blocks.sandstone, 0);

			return 7000-(200*toolEfficiency);

		} else if ((block == Blocks.sand) || (block == Blocks.clay) || (block == Blocks.gravel)) {
			final int toolEfficiency=(int)villager.getBestShovel().getDigSpeed(new ItemStack(villager.getBestShovel(),1), Blocks.sand, 0);

			return 7000-(200*toolEfficiency);
		}
		return 0;
	}
	
	@Override
	public boolean swingArms() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) throws Exception {

		final Vector<Vector<Point>> sources=villager.getHouse().sources;

		final Vector<Point> validSources=new Vector<Point>();

		for (int i=0;i<sources.size();i++) {
			for (int j=0;j<sources.get(i).size();j++) {
				final Block block=villager.getBlock(sources.get(i).get(j));

				if ((block == Blocks.stone) || (block == Blocks.sandstone) || (block == Blocks.sand)
						|| (block == Blocks.clay) || (block == Blocks.gravel)) {
					validSources.add(sources.get(i).get(j));
				}

			}
		}

		if (validSources.isEmpty())
			return null;

		return packDest(validSources.get(MillCommonUtilities.randomInt(validSources.size())),villager.getHouse());
	}

	@Override
	public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {

		if ((villager.getBlock(villager.getGoalDestPoint())==Blocks.sand) || (villager.getBlock(villager.getGoalDestPoint())==Blocks.clay) || (villager.getBlock(villager.getGoalDestPoint())==Blocks.gravel))
			return villager.getBestShovelStack();

		return villager.getBestPickaxeStack();
	}

	@Override
	public AStarConfig getPathingConfig() {
		return JPS_CONFIG_WIDE;
	}

	@Override
	public int range(MillVillager villager) {
		return 5;
	}

	@Override
	public boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return getDestination(villager)!=null;
	}

	@Override
	public boolean lookAtGoal() {
		return true;
	}

	@Override
	public boolean performAction(MillVillager villager) throws Exception {

		final Block block=villager.getBlock(villager.getGoalDestPoint());

		if (block == Blocks.sand) {
			villager.addToInv(Blocks.sand, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Blocks.sand,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gathered sand at: "+villager.getGoalDestPoint());
			}
		} else if (block==Blocks.stone) {
			villager.addToInv(Blocks.cobblestone, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Blocks.stone,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather cobblestone at: "+villager.getGoalDestPoint());
			}
		} else if (block==Blocks.sandstone) {
			villager.addToInv(Blocks.sandstone, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Blocks.sandstone,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather sand stone at: "+villager.getGoalDestPoint());
			}
		} else if (block==Blocks.clay) {
			villager.addToInv(Items.clay_ball, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Blocks.clay,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather clay at: "+villager.getGoalDestPoint());
			}
		} else if (block==Blocks.gravel) {
			villager.addToInv(Blocks.gravel, 1);

			MillCommonUtilities.playSoundBlockBreaking(villager.worldObj,villager.getGoalDestPoint(),Blocks.gravel,4.0f);

			if ((MLN.LogMiner>=MLN.DEBUG) && villager.extraLog) {
				MLN.debug(this, "Gather gravel at: "+villager.getGoalDestPoint());
			}
		}

		return true;
	}

	@Override
	public int priority(MillVillager villager) throws Exception {
		return 30;
	}

	@Override
	public boolean stuckAction(MillVillager villager)  throws Exception {
		return performAction(villager);
	}
}
