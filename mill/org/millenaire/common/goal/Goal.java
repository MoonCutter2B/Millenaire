package org.millenaire.common.goal;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;

public abstract class Goal {

	public static final int STANDARD_DELAY=2000;

	public static  HashMap<String,Goal> goals;

	public static GoalBeSeller beSeller;
	public static Goal construction;
	public static Goal deliverGoodsHousehold;
	public static Goal getResourcesForBuild;
	public static Goal raidVillage;
	public static Goal defendVillage;
	public static Goal hide;
	public static Goal sleep;
	public static Goal gettool;

	public static final AStarConfig JPS_CONFIG_TIGHT=new AStarConfig(true,false,false,true);
	public static final AStarConfig JPS_CONFIG_WIDE=new AStarConfig(true,false,false,true,2,10);
	public static final AStarConfig JPS_CONFIG_BUILDING=new AStarConfig(true,false,false,true,2,20);

	protected static final Point[] EMPTY_DEST=new Point[]{null,null};

	public static void initGoals() {
		goals=new HashMap<String,Goal>();

		goals.put("gorest", new GoalGoRest());
		goals.put("godrink", new GoalGoDrink());
		goals.put("gopray", new GoalGoPray());

		goals.put("gathergoods", new GoalGatherGoods());
		goals.put("bringbackresourceshome", new GoalBringBackResourcesHome());
		gettool = new GoalGetTool();
		goals.put("getitemtokeep", gettool);
		goals.put("huntmonster", new GoalHuntMonster());
		goals.put("getgoodshousehold", new GoalGetGoodsForHousehold());

		sleep = new GoalSleep();
		goals.put("sleep", sleep);

		deliverGoodsHousehold =  new GoalDeliverGoodsHousehold();
		goals.put("delivergoodshousehold",deliverGoodsHousehold);
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


		GoalGeneric.loadGenericGoals();


		for (final String s : goals.keySet()) {
			goals.get(s).key=s;
		}

	}

	public String key;
	public boolean leasure=false;

	public HashMap<InvItem,Integer> buildingLimit=new HashMap<InvItem,Integer>();
	public HashMap<InvItem,Integer> townhallLimit=new HashMap<InvItem,Integer>();

	public int maxSimultaneousInBuilding=0;
	public int maxSimultaneousTotal=0;

	public InvItem balanceOutput1=null,balanceOutput2=null;

	protected static  int ACTIVATION_RANGE = 3;


	public Goal() {
	}

	public int actionDuration(MillVillager villager) throws Exception {
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

	public Point getCurrentGoalTarget(MillVillager villager) {

		if (villager.getGoalDestEntity()!=null)
			return new Point(villager.getGoalDestEntity());

		return villager.getGoalDestPoint();

	}

	public String gameName(MillVillager villager) {

		if ((villager!=null) && (getCurrentGoalTarget(villager)!=null) &&
				(getCurrentGoalTarget(villager).horizontalDistanceTo(villager)>range(villager)))
			return MLN.string("goal."+labelKeyWhileTravelling());

		return MLN.string("goal."+labelKey());
	}

	public abstract GoalInformation getDestination(MillVillager villager) throws Exception;

	public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
		return getHeldItemsTravelling(villager);
	}

	public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
		return null;
	}

	public AStarConfig getPathingConfig() {
		return MillVillager.DEFAULT_JPS_CONFIG;
	}

	public boolean isFightingGoal() {
		return false;
	}

	public final boolean isPossible(MillVillager villager) throws Exception {

		if (villager.worldObj.isDaytime() && !canBeDoneInDayTime())
			return false;

		if (!villager.worldObj.isDaytime() && !canBeDoneAtNight())
			return false;

		for (final InvItem item : townhallLimit.keySet()) {
			if (villager.getTownHall().countGoods(item)>townhallLimit.get(item))
				return false;
		}

		if (balanceOutput1!=null) {
			if (villager.getTownHall().nbGoodAvailable(balanceOutput1.id(), balanceOutput1.meta, false)<
					villager.getTownHall().nbGoodAvailable(balanceOutput2.id(), balanceOutput2.meta, false))
				return false;
		}


		if (maxSimultaneousTotal>0) {//0=no limit

			int nbSame=0;

			for (final MillVillager v : villager.getTownHall().villagers) {
				if ((v != villager) && this.key.equals(v.goalKey)) {
					nbSame++;
				}
			}

			if (nbSame>=maxSimultaneousTotal)
				return false;
		}

		return isPossibleSpecific(villager);
	}

	protected boolean isPossibleSpecific(MillVillager villager) throws Exception {
		return true;
	}

	public boolean validateDest(MillVillager villager, Building dest) {
		for (final InvItem item : buildingLimit.keySet()) {
			if (dest.countGoods(item)>buildingLimit.get(item))
				return false;
		}

		int nbSameBuilding=0;

		if (maxSimultaneousInBuilding>0) {//0=no limit

			for (final MillVillager v : villager.getTownHall().villagers) {
				if ((v != villager) && this.key.equals(v.goalKey)) {
					if (v.getGoalBuildingDest()==dest) {
						nbSameBuilding++;
					}
				}
			}

			if (nbSameBuilding>=maxSimultaneousInBuilding)
				return false;
		}

		return true;
	}

	public final boolean isStillValid(MillVillager villager) throws Exception {

		if (villager.worldObj.isDaytime() && !canBeDoneInDayTime())
			return false;

		if (!villager.worldObj.isDaytime() && !canBeDoneAtNight())
			return false;

		if (leasure) {
			for (final Goal g : villager.getGoals()) {
				if ((g.leasure==false) && g.isPossible(villager))
					return false;
			}

		}

		if (villager.getGoalDestPoint() == null && villager.getGoalDestEntity()==null)
			return false;

		return isStillValidSpecific(villager);
	}

	protected boolean isStillValidSpecific(MillVillager villager) throws Exception  {
		return true;
	}

	public String labelKey() {
		return key;
	}

	public String labelKeyWhileTravelling() {
		return key;
	}

	public boolean lookAtGoal() {
		return false;
	}

	public boolean lookAtPlayer() {
		return false;
	}

	public String nextGoal(MillVillager villager) throws Exception {
		return null;
	}

	public void onAccept(MillVillager villager) throws Exception {}

	public void onComplete(MillVillager villager) throws Exception {}

	public abstract boolean performAction(MillVillager villager) throws Exception;

	public abstract int priority(MillVillager villager) throws Exception;

	public int range(MillVillager villager) {
		return ACTIVATION_RANGE;
	}

	public String sentenceKey() {
		return key;
	}

	public boolean shouldVillagerLieDown() {
		return false;
	}

	public boolean stopMovingWhileWorking() {
		return true;
	}

	public boolean stuckAction(MillVillager villager) throws Exception {
		return false;
	}

	public long stuckDelay(MillVillager villager) {
		return 10000;
	}

	@Override
	public String toString() {
		return "goal:"+key;
	}

	public boolean unreachableDestination(MillVillager villager) throws Exception {

		if (villager.getGoalDestPoint()==null && villager.getGoalDestEntity()==null)
			return false;

		final int[] jumpTo=MillCommonUtilities.getJumpDestination(villager.worldObj,
				villager.getPathDestPoint().getiX(), villager.getTownHall().getAltitude(villager.getPathDestPoint().getiX(),villager.getPathDestPoint().getiZ()), villager.getPathDestPoint().getiZ());

		if (jumpTo != null) {
			if ((MLN.Pathing>=MLN.MINOR) && villager.extraLog) {
				MLN.minor(this, "Dest unreachable. Jumping "+villager+" from "+villager.getPos()+" to "+jumpTo[0]+"/"+jumpTo[1]+"/"+jumpTo[2]);
			}
			villager.setPosition(jumpTo[0]+0.5,jumpTo[1]+0.5,jumpTo[2]+0.5);
			return true;

		} else {
			if ((MLN.Pathing>=MLN.MINOR) && villager.extraLog) {
				MLN.minor(this,"Dest unreachable. Couldn't jump "+villager+" from "+villager.getPos()+" to "+villager.getPathDestPoint());
			}
			return false;
		}
	}

	public Entity getTargetEntity(MillVillager villager) {
		return null;
	}

	public void setVillagerDest(MillVillager villager) throws Exception {
		villager.setGoalInformation(getDestination(villager));
	}

	protected GoalInformation packDest(Point p) {
		return new GoalInformation(p,null,null);
	}

	protected GoalInformation packDest(Point p, Point p2) {
		return new GoalInformation(p,p2,null);
	}

	protected GoalInformation packDest(Point p, Building b) {
		return new GoalInformation(p,b.getPos(),null);
	}

	protected GoalInformation packDest(Point p, Building b,Entity ent) {
		if (b==null)
			return new GoalInformation(p,null,ent);

		return new GoalInformation(p,b.getPos(),ent);
	}

	public static class GoalInformation {

		private Point dest,destBuildingPos;
		private Entity targetEnt;

		public GoalInformation(Point dest,Point buildingPos,Entity targetEnt) {
			this.dest=dest;
			this.destBuildingPos=buildingPos;
			this.targetEnt=targetEnt;
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

		public void setDest(Point dest) {
			this.dest = dest;
		}

		public void setDestBuildingPos(Point destBuildingPos) {
			this.destBuildingPos = destBuildingPos;
		}

		public void setTargetEnt(Entity targetEnt) {
			this.targetEnt = targetEnt;
		}
	}
}
