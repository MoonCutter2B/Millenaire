package org.millenaire.common.pathing.atomicstryker;

/**
 * Configuration class to hold various settings added to the pathing engine
 * 
 * @author Kinniken
 * 
 */

public class AStarConfig {

	// if yes, wooden doors and fence gates are considered passable
	// note that the entity then must be able to open and close them in-game
	public boolean canUseDoors = false;
	// if no, finished paths will not include diagonal moves
	public boolean canTakeDiagonals = false;
	// whether the path can includes "drops" (beyond the normal one block)
	public boolean allowDropping = false;
	// whether the path can cross water
	public boolean canSwim = false;
	// whether there is any tolerance when it comes to reaching the goal
	public boolean tolerance = false;
	// if above is set to true, the acceptable tolerances:
	// along the x and z axis (inclusive):
	public int toleranceHorizontal = 0;
	// along the y axis:
	public int toleranceVertical = 0;

	public AStarConfig(final boolean canUseDoors, final boolean makePathDiagonals, final boolean allowDropping, final boolean canSwim) {
		this.canUseDoors = canUseDoors;
		this.canTakeDiagonals = makePathDiagonals;
		this.allowDropping = allowDropping;
	}

	public AStarConfig(final boolean canUseDoors, final boolean makePathDiagonals, final boolean allowDropping, final boolean canSwim, final int toleranceHorizontal, final int toleranceVertical) {
		this.canUseDoors = canUseDoors;
		this.canTakeDiagonals = makePathDiagonals;
		this.allowDropping = allowDropping;
		this.toleranceHorizontal = toleranceHorizontal;
		this.toleranceVertical = toleranceVertical;
		tolerance = true;

	}

}
