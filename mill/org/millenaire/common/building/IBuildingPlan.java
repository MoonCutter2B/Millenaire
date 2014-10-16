package org.millenaire.common.building;

import java.util.List;

import org.millenaire.common.Culture;

public interface IBuildingPlan {

	public abstract Culture getCulture();

	/**
	 * List of female residents populating building
	 */
	public abstract List<String> getFemaleResident();

	/**
	 * Name in player's language, if readable by player. Ex: Well
	 */
	public abstract String getGameName();

	/**
	 * List of male residents populating building
	 */
	public abstract List<String> getMaleResident();

	/**
	 * Name in culture's language. Ex: Puit
	 */
	public abstract String getNativeName();

}