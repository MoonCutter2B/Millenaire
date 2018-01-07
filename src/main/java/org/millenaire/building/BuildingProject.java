package org.millenaire.building;

public class BuildingProject
{
	/**
	 * Building's internal name
	 */
	public String ID;
	/**
	 * Pretty self-explainatory
	 */
	public int lvl;
	
	/**
	 * This is just here to make the JSON parser work correctly. USE OTHER CONSTRUCTOR!
	 */
	public BuildingProject() {
		
	}
	
	/**
	 * The best constructor
	 */
	public BuildingProject(String buildingID, int level) {
		this.ID = buildingID;
		this.lvl = level;
	}
}
