package org.millenaire.common.goal;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.goal.leasure.GoalChildGoPlay;
import org.millenaire.common.goal.leasure.GoalGoChat;
import org.millenaire.common.goal.leasure.GoalGoDrink;
import org.millenaire.common.goal.leasure.GoalGoPray;
import org.millenaire.common.goal.leasure.GoalGoRest;
import org.millenaire.common.goal.leasure.GoalGoSocialise;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public abstract class Goal {

	public static class GoalInformation {

		private Point dest, destBuildingPos;
		private Entity targetEnt;

		public GoalInformation(final Point dest, final Point buildingPos, final Entity targetEnt) {
			this.dest = dest;
			this.destBuildingPos = buildingPos;
			this.targetEnt = targetEnt;
		}

		public Point getDest() {
			return dest;
		}

		public Point getDestBuildingPos() {
			return destBuildingPos;
		}

		public Entity getTargetEnt() {
			return targetEnt;
		}

		public void setDest(final Point dest) {
			this.dest = dest;
		}

		public void setDestBuildingPos(final Point destBuildingPos) {
			this.destBuildingPos = destBuildingPos;
		}

		public void setTargetEnt(final Entity targetEnt) {
			this.targetEnt = targetEnt;
		}
	}

	public static final int STANDARD_DELAY = 2000;

	public static HashMap<String, Goal> goals;
	public static GoalBeSeller beSeller;
	public static Goal construction;
	public static Goal deliverGoodsHousehold;
	public static Goal getResourcesForBuild;
	public static Goal raidVillage;
	public static Goal defendVillage;
	public static Goal hide;
	public static Goal sleep;
	public static Goal gettool;

	public static Goal gosocialise;
	public static final AStarConfig JPS_CONFIG_TIGHT = new AStarConfig(true, false, false, true);
	public static final AStarConfig JPS_CONFIG_WIDE = new AStarConfig(true, false, false, true, 2, 10);
	public static final AStarConfig JPS_CONFIG_BUILDING = new AStarConfig(true, false, false, true, 2, 20);
	public static final AStarConfig JPS_CONFIG_CHOPLUMBER = new AStarConfig(true, false, false, true, 4, 20);

	public static final AStarConfig JPS_CONFIG_SLAUGHTERSQUIDS = new AStarConfig(true, false, false, true, 6, 4);

	protected static final Point[] EMPTY_DEST = new Point[] { null, null };

	public static void initGoals() {
		goals = new HashMap<String, Goal>();

		goals.put("gorest", new GoalGoRest());
		goals.put("godrink", new GoalGoDrink());
		goals.put("gopray", new GoalGoPray());

		gosocialise = new GoalGoSocialise();
		goals.put("gosocialise", gosocialise);
		goals.put("chat", new GoalGoChat());

		goals.put("gathergoods", new GoalGatherGoods());
		goals.put("bringbackresourceshome", new GoalBringBackResourcesHome());
		gettool = new GoalGetTool();
		goals.put("getitemtokeep", gettool);
		goals.put("huntmonster", new GoalHuntMonster());
		goals.put("getgoodshousehold", new GoalGetGoodsForHousehold());

		sleep = new GoalSleep();
		goals.put("sleep", sleep);

		deliverGoodsHousehold = new GoalDeliverGoodsHousehold();
		goals.put("delivergoodshousehold", deliverGoodsHousehold);
		goals.put("gethousethresources", new GoalGetResourcesForShops());
		goals.put("deliverresourcesshop", new GoalDeliverResourcesShop());

		goals.put("choptrees", new GoalLumbermanChopTrees());
		goals.put("plantsaplings", new GoalLumbermanPlantSaplings());

		getResourcesForBuild = new GoalGetResourcesForBuild();
		goals.put("getresourcesforbuild", getResourcesForBuild);
		beSeller = new GoalBeSeller();
		goals.put("beseller", beSeller);

		construction = new GoalConstructionStepByStep();
		goals.put("construction", construction);
		goals.put("buildpath", new GoalBuildPath());
		goals.put("clearoldpath", new GoalClearOldPath());

		raidVillage = new GoalRaidVillage();
		goals.put("raidvillage", raidVillage);
		defendVillage = new GoalDefendVillage();
		goals.put("defendvillage", defendVillage);
		hide = new GoalHide();
		goals.put("hide", hide);

		goals.put("goplay", new GoalChildGoPlay());
		goals.put("becomeadult", new GoalChildBecomeAdult());
		goals.put("patrol", new GoalGuardPatrol());
		goals.put("shearsheep", new GoalShearSheep());
		goals.put("breed", new GoalBreedAnimals());
		goals.put("mining", new GoalMinerMineResource());
		goals.put("visitinn", new GoalMerchantVisitInn());
		goals.put("visitbuilding", new GoalMerchantVisitBuilding());

		goals.put("keepstall", new GoalForeignMerchantKeepStall());
		goals.put("drybrick", new GoalIndianDryBrick());
		goals.put("gatherbrick", new GoalIndianGatherBrick());
		goals.put("plantsugarcane", new GoalIndianPlantSugarCane());
		goals.put("harvestsugarcane", new GoalIndianHarvestSugarCane());
		goals.put("performpujas", new GoalPerformPuja());
		goals.put("bepujaperformer", new GoalBePujaPerformer());

		goals.put("fish", new GoalFish());

		goals.put("harvestwarts", new GoalHarvestWarts());
		goals.put("plantwarts", new GoalPlantNetherWarts());
		goals.put("brewpotions", new GoalBrewPotions());

		goals.put("gathersilk", new GoalByzantineGatherSilk());

		goals.put("plantcocoa", new GoalPlantCacao());
		goals.put("harvestcocoa", new GoalHarvestCacao());

		GoalGeneric.loadGenericGoals();

		for (final String s : goals.keySet()) {
			goals.get(s).key = s;
		}

	}

	public String key;

	public boolean leasure = false;
	public HashMap<InvItem, Integer> buildingLimit = new HashMap<InvItem, Integer>();

	public HashMap<InvItem, Integer> townhallLimit = new HashMap<InvItem, Integer>();
	public int maxSimultaneousInBuilding = 0;

	public int maxSimultaneousTotal = 0;

	public InvItem balanceOutput1 = null, balanceOutput2 = null;

	protected static int ACTIVATION_RANGE = 3;

	public Goal() {
	}

	public int actionDuration(final MillVillager villager) throws Exception {
		return 500;
	}

	public boolean allowRandomMoves() throws Exception {
		return false;
	}

	public boolean canBeDoneAtNight() {
		return false;
	}

	public boolean canBeDoneInDayTime() {
		return true;
	}

	public String gameName(final MillVillager villager) {

		if (villager != null && getCurrentGoalTarget(villager) != null && getCurrentGoalTarget(villager).horizontalDistanceTo(villager) > range(villager)) {
			return MLN.string("goal." + labelKeyWhileTravelling(villager));
		}

		return MLN.string("goal." + labelKey(villager));
	}

	public Point getCurrentGoalTarget(final MillVillager villager) {

		if (villager.getGoalDestEntity() != null) {
			return new Point(villager.getGoalDestEntity());
		}

		return villager.getGoalDestPoint();

	}

	public abstract GoalInformation getDestination(MillVillager villager) throws Exception;

	public ItemStack[] getHeldItemsDestination(final MillVillager villager) throws Exception {
		return getHeldItemsTravelling(villager);
	}

	public ItemStack[] getHeldItemsTravelling(final MillVillager villager) throws Exception {
		return null;
	}

	public AStarConfig getPathingConfig() {
		return MillVillager.DEFAULT_JPS_CONFIG;
	}

	public Entity getTargetEntity(final MillVillager villager) {
		return null;
	}

	public boolean isFightingGoal() {
		return false;
	}

	public final boolean isPossible(final MillVillager villager) {

		try {

			if (villager.worldObj.isDaytime() && !canBeDoneInDayTime()) {
				return false;
			}

			if (!villager.worldObj.isDaytime() && !canBeDoneAtNight()) {
				return false;
			}

			// MLN.validateResourceMap(townhallLimit);
			for (final InvItem item : townhallLimit.keySet()) {
				if (villager.getTownHall().countGoods(item) > townhallLimit.get(item)) {
					return false;
				}
			}

			if (balanceOutput1 != null) {
				if (villager.getTownHall().nbGoodAvailable(new InvItem(balanceOutput1.item, balanceOutput1.meta), false, false) < villager.getTownHall().nbGoodAvailable(
						new InvItem(balanceOutput2.item, balanceOutput2.meta), false, false)) {
					return false;
				}
			}

			if (maxSimultaneousTotal > 0) {// 0=no limit

				int nbSame = 0;

				for (final MillVillager v : villager.getTownHall().villagers) {
					if (v != villager && this.key.equals(v.goalKey)) {
						nbSame++;
					}
				}

				if (nbSame >= maxSimultaneousTotal) {
					return false;
				}
			}

			return isPossibleSpecific(villager);

		} catch (final Exception e) {
			MLN.printException("Exception in isPossible() for goal: " + this.key + " and villager: " + villager, e);
			return false;
		}
	}

	protected boolean isPossibleSpecific(final MillVillager villager) throws Exception {
		return true;
	}

	public final boolean isStillValid(final MillVillager villager) throws Exception {

		if (villager.worldObj.isDaytime() && !canBeDoneInDayTime()) {
			return false;
		}

		if (!villager.worldObj.isDaytime() && !canBeDoneAtNight()) {
			return false;
		}

		if (leasure) {
			for (final Goal g : villager.getGoals()) {
				if (g.leasure == false && g.isPossible(villager)) {
					return false;
				}
			}

		}

		if (villager.getGoalDestPoint() == null && villager.getGoalDestEntity() == null) {
			return false;
		}

		return isStillValidSpecific(villager);
	}

	protected boolean isStillValidSpecific(final MillVillager villager) throws Exception {
		return true;
	}

	public String labelKey(final MillVillager villager) {
		return key;
	}

	public String labelKeyWhileTravelling(final MillVillager villager) {
		return key;
	}

	public boolean lookAtGoal() {
		return false;
	}

	public boolean lookAtPlayer() {
		return false;
	}

	public String nextGoal(final MillVillager villager) throws Exception {
		return null;
	}

	public void onAccept(final MillVillager villager) throws Exception {
	}

	public void onComplete(final MillVillager villager) throws Exception {
	}

	protected GoalInformation packDest(final Point p) {
		return new GoalInformation(p, null, null);
	}

	protected GoalInformation packDest(final Point p, final Building b) {
		return new GoalInformation(p, b.getPos(), null);
	}

	protected GoalInformation packDest(final Point p, final Building b, final Entity ent) {
		if (b == null) {
			return new GoalInformation(p, null, ent);
		}

		return new GoalInformation(p, b.getPos(), ent);
	}

	protected GoalInformation packDest(final Point p, final Point p2) {
		return new GoalInformation(p, p2, null);
	}

	public abstract boolean performAction(MillVillager villager) throws Exception;

	public abstract int priority(MillVillager villager) throws Exception;

	public int range(final MillVillager villager) {
		return ACTIVATION_RANGE;
	}

	public String sentenceKey() {
		return key;
	}

	public void setVillagerDest(final MillVillager villager) throws Exception {
		villager.setGoalInformation(getDestination(villager));
	}

	public boolean shouldVillagerLieDown() {
		return false;
	}

	public boolean stopMovingWhileWorking() {
		return true;
	}

	public boolean stuckAction(final MillVillager villager) throws Exception {
		return false;
	}

	public long stuckDelay(final MillVillager villager) {
		return 10000;
	}

	public boolean swingArms() {
		return false;
	}

	public boolean swingArms(final MillVillager villager) {

		if (villager != null && getCurrentGoalTarget(villager) != null && getCurrentGoalTarget(villager).horizontalDistanceTo(villager) > range(villager)) {
			return this.swingArmsWhileTravelling();
		}

		return this.swingArms();
	}

	public boolean swingArmsWhileTravelling() {
		return false;
	}

	@Override
	public String toString() {
		return "goal:" + key;
	}

	public boolean unreachableDestination(final MillVillager villager) throws Exception {

		if (villager.getGoalDestPoint() == null && villager.getGoalDestEntity() == null) {
			return false;
		}

		final int[] jumpTo = MillCommonUtilities.getJumpDestination(villager.worldObj, villager.getPathDestPoint().getiX(),
				villager.getTownHall().getAltitude(villager.getPathDestPoint().getiX(), villager.getPathDestPoint().getiZ()), villager.getPathDestPoint().getiZ());

		if (jumpTo != null) {
			if (MLN.LogPathing >= MLN.MINOR && villager.extraLog) {
				MLN.minor(this, "Dest unreachable. Jumping " + villager + " from " + villager.getPos() + " to " + jumpTo[0] + "/" + jumpTo[1] + "/" + jumpTo[2]);
			}
			villager.setPosition(jumpTo[0] + 0.5, jumpTo[1] + 0.5, jumpTo[2] + 0.5);
			return true;

		} else {
			if (MLN.LogPathing >= MLN.MINOR && villager.extraLog) {
				MLN.minor(this, "Dest unreachable. Couldn't jump " + villager + " from " + villager.getPos() + " to " + villager.getPathDestPoint());
			}
			return false;
		}
	}

	public boolean validateDest(final MillVillager villager, final Building dest) throws MillenaireException {

		if (dest == null) {
			throw new MillenaireException("Given null dest in validateDest for goal: " + this.key);
		}

		for (final InvItem item : buildingLimit.keySet()) {
			if (dest.countGoods(item) > buildingLimit.get(item)) {
				return false;
			}
		}

		int nbSameBuilding = 0;

		if (maxSimultaneousInBuilding > 0) {// 0=no limit

			for (final MillVillager v : villager.getTownHall().villagers) {
				if (v != villager && this.key.equals(v.goalKey)) {
					if (v.getGoalBuildingDest() == dest) {
						nbSameBuilding++;
					}
				}
			}

			if (nbSameBuilding >= maxSimultaneousInBuilding) {
				return false;
			}
		}

		return true;
	}
}
