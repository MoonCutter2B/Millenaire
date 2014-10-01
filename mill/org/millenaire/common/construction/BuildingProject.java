package org.millenaire.common.construction;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.MLN;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.WeightedChoice;

public class BuildingProject implements WeightedChoice {

	public static BuildingProject getRandomProject(
			final List<BuildingProject> possibleProjects) {
		return (BuildingProject) MillCommonUtilities.getWeightedChoice(
				possibleProjects, null);
	}

	public BuildingPlanSet planSet = null;

	public BuildingLocation location = null;

	public String key;

	public BuildingProject() {

	}

	public BuildingProject(final BuildingPlanSet planSet) {
		this.planSet = planSet;
		try {
			this.key = planSet.plans.get(0)[0].buildingKey;
		} catch (final Exception e) {
			MLN.printException("Error when getting projet for " + key + ", "
					+ planSet + ":", e);
		}
	}

	@Override
	public int getChoiceWeight(final EntityPlayer player) {
		if (location == null || location.level < 0) {
			return planSet.plans.get(0)[0].priority;
		} else {
			if (location.level + 1 < planSet.plans.get(location.getVariation()).length) {
				return planSet.plans.get(location.getVariation())[location.level + 1].priority;
			} else {
				return 0;
			}
		}
	}

	public String getFullName(final EntityPlayer player) {
		return planSet.getFullName(player);
	}

	public String getGameName() {
		return planSet.getGameName();
	}

	public int getLevelsNumber(final int variation) {
		if (variation >= planSet.plans.size()) {
			return 1;
		}
		return planSet.plans.get(variation).length;
	}

	public String getNativeName() {
		return planSet.getNativeName();
	}

	public BuildingPlan getNextBuildingPlan() {

		if (location == null) {
			return planSet.getRandomStartingPlan();
		}

		if (location.level < planSet.plans.get(location.getVariation()).length) {
			return planSet.plans.get(location.getVariation())[location.level + 1];
		} else {
			return null;
		}
	}

	public BuildingPlan getPlan(final int variation, final int level) {
		if (variation >= planSet.plans.size()) {
			return null;
		}
		if (level >= planSet.plans.get(variation).length) {
			return null;
		}
		return planSet.plans.get(variation)[level];
	}

	@Override
	public String toString() {
		return "Project " + key + " location: " + location;
	}

}
