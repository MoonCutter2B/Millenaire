package org.millenaire.common.goal.leasure;

import java.util.Vector;

import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.goal.Goal;



public class GoalGoSocialise extends Goal {

	public GoalGoSocialise() {
		super();
		leasure=true;
	}

	@Override
	public int actionDuration(MillVillager villager) {
		return 10000;
	}

	@Override
	public boolean allowRandomMoves() {
		return true;
	}

	@Override
	public GoalInformation getDestination(MillVillager villager) {
		
		Point dest=null;
		Building destB=null;
		
		Vector<Building> possibleDests=new Vector<Building>();
		
		for (Building b : villager.getTownHall().getBuildings()) {
			if (b.location.tags.contains(Building.tagLeasure))
					possibleDests.add(b);	
			
		}
	
		if (possibleDests.isEmpty())
			return null;
	
		destB=possibleDests.get(MillCommonUtilities.randomInt(possibleDests.size()));
		dest=destB.getLeasurePos();
		
		if (dest==null) {
			dest=villager.getTownHall().getLeasurePos();
			destB=villager.getTownHall();
		}
		
		return packDest(dest,destB);
	}

	@Override
	public boolean performAction(MillVillager villager) {
		return true;
	}

	@Override
	public int priority(MillVillager villager) {
		return 5;
	}

	@Override
	public int range(MillVillager villager) {
		return 5;
	}

}
