package org.millenaire.common.building;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.MLN;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.WeightedChoice;

/**
 * Represents anything a village can build or did build, whether it is: - A
 * building already constructed - A building planned but not constructed (in
 * controlled villages) - A building being constructed - A building that could
 * be constructed in the future but isn't even planned yet - Custom buildings in
 * controlled villages
 * 
 * @author cedricdj
 * 
 */
public class BuildingProject implements WeightedChoice {

	/**
	 * Types of building projects a village have.
	 * 
	 * @author cedricdj
	 * 
	 */
	public static enum EnumProjects {
		CENTRE(0, "ui.buildingscentre"), START(1, "ui.buildingsstarting"), PLAYER(2, "ui.buildingsplayer"), CORE(3, "ui.buildingskey"), SECONDARY(4, "ui.buildingssecondary"), EXTRA(5,
				"ui.buildingsextra"), CUSTOMBUILDINGS(6, "ui.buildingcustom");

		public static EnumProjects getById(final int id) {
			for (final EnumProjects ep : EnumProjects.values()) {
				if (ep.id == id) {
					return ep;
				}
			}
			return null;
		}

		public final int id;
		public final String labelKey;

		EnumProjects(final int id, final String labelKey) {
			this.id = id;
			this.labelKey = labelKey;
		}

	}

	public static BuildingProject getRandomProject(final List<BuildingProject> possibleProjects) {
		return (BuildingProject) MillCommonUtilities.getWeightedChoice(possibleProjects, null);
	}

	public BuildingPlanSet planSet = null;

	public BuildingLocation location = null;

	public BuildingCustomPlan customBuildingPlan = null;

	public String key;

	public boolean isCustomBuilding = false;

	public BuildingProject() {

	}

	/**
	 * Creates a building project from a custom building
	 * 
	 * @param customPlan
	 * @param location
	 */
	public BuildingProject(final BuildingCustomPlan customPlan, final BuildingLocation location) {
		this.customBuildingPlan = customPlan;
		this.key = customBuildingPlan.buildingKey;
		this.location = location;
		this.isCustomBuilding = true;
	}

	/**
	 * Creates a building project from a plan set.
	 * 
	 * Used for "normal" buildings.
	 * 
	 * @param planSet
	 */
	public BuildingProject(final BuildingPlanSet planSet) {
		this.planSet = planSet;
		try {
			this.key = planSet.plans.get(0)[0].buildingKey;
		} catch (final Exception e) {
			MLN.printException("Error when getting projet for " + key + ", " + planSet + ":", e);
		}
	}

	@Override
	public int getChoiceWeight(final EntityPlayer player) {

		if (planSet == null) {
			return 0;
		}

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
		if (planSet != null) {
			return planSet.getFullName(player);
		} else if (customBuildingPlan != null) {
			return customBuildingPlan.getFullDisplayName();
		} else {
			return null;
		}
	}

	public String getGameName() {
		if (planSet != null) {
			return planSet.getGameName();
		} else if (customBuildingPlan != null) {
			return customBuildingPlan.getGameName();
		} else {
			return null;
		}
	}

	public int getLevelsNumber(final int variation) {
		if (planSet == null) {
			return 0;
		}

		if (variation >= planSet.plans.size()) {
			return 1;
		}
		return planSet.plans.get(variation).length;
	}

	public String getNativeName() {
		if (planSet != null) {
			return planSet.getNativeName();
		} else if (customBuildingPlan != null) {
			return customBuildingPlan.nativeName;
		} else {
			return null;
		}
	}

	public BuildingPlan getNextBuildingPlan() {

		if (planSet == null) {
			return null;
		}

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

		if (planSet == null) {
			return null;
		}

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
