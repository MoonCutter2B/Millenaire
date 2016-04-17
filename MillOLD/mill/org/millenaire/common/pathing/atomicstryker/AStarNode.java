package org.millenaire.common.pathing.atomicstryker;

/**
 * Path Node class for AstarPath
 * 
 * 
 * @author AtomicStryker
 */

@SuppressWarnings("rawtypes")
public class AStarNode implements Comparable {
	final public int x;
	final public int y;
	final public int z;
	final AStarNode target;

	public AStarNode parent;

	/**
	 * AStar G value, the total distance from the start Node to this Node
	 */
	private int g;

	/**
	 * AStar H value, cost to goal estimated value, sometimes called heuristic
	 * value
	 */
	private double h;

	public AStarNode(final int ix, final int iy, final int iz) {
		x = ix;
		y = iy;
		z = iz;
		g = 0;
		parent = null;
		target = null;
	}

	/**
	 * AStarNode constructor
	 * 
	 * @param ix
	 *            x coordinate
	 * @param iy
	 *            y coordinate
	 * @param iz
	 *            z coordinate
	 * @param dist
	 *            Node reaching distance from start
	 * @param p
	 *            parent Node
	 */
	public AStarNode(final int ix, final int iy, final int iz, final int dist, final AStarNode p) {
		x = ix;
		y = iy;
		z = iz;
		g = dist;
		parent = p;
		target = null;
	}

	public AStarNode(final int ix, final int iy, final int iz, final int dist, final AStarNode p, final AStarNode t) {
		x = ix;
		y = iy;
		z = iz;
		g = dist;
		parent = p;
		target = t;
		updateTargetCostEstimate();
	}

	@Override
	public AStarNode clone() {
		return new AStarNode(x, y, z, g, parent);
	}

	@Override
	public int compareTo(final Object o) {
		if (o instanceof AStarNode) {
			final AStarNode other = (AStarNode) o;
			if (getF() < other.getF()) {
				return -1;
			} else if (getF() > other.getF()) {
				return 1;
			}
		}

		return 0;
	}

	@Override
	public boolean equals(final Object checkagainst) {
		if (checkagainst instanceof AStarNode) {
			final AStarNode check = (AStarNode) checkagainst;
			if (check.x == x && check.y == y && check.z == z) {
				return true;
			}
		}

		return false;
	}

	public double getF() {
		return g + h;
	}

	public int getG() {
		return g;
	}

	@Override
	public int hashCode() {
		return x << 16 ^ z ^ y << 24;
	}

	@Override
	public String toString() {
		if (parent == null) {
			return String.format("[%d|%d|%d], dist %d, F: %f", x, y, z, g, getF());
		} else {
			return String.format("[%d|%d|%d], dist %d, parent [%d|%d|%d], F: %f", x, y, z, g, parent.x, parent.y, parent.z, getF());
		}
	}

	/**
	 * Tries to update this Node instance with a new Nodechain to it, but checks
	 * if that improves the Node cost first
	 * 
	 * @param checkingDistance
	 *            new G distance if the update is accepted
	 * @param parentOtherNode
	 *            new parent Node if the update is accepted
	 * @return true if the new cost is lower and the update was accepted, false
	 *         otherwise
	 */
	public boolean updateDistance(final int checkingDistance, final AStarNode parentOtherNode) {
		if (checkingDistance < g) {
			g = checkingDistance;
			parent = parentOtherNode;
			updateTargetCostEstimate();
			return true;
		}

		return false;
	}

	/**
	 * Computes the H or heuristic value by estimating the total cost from here
	 * to the target Node (if it exists).
	 */
	private void updateTargetCostEstimate() {
		if (target != null) {
			// we prefer "less distance to target" over "short path" by a huge
			// factor, here 10!
			h = g + AStarStatic.getDistanceBetweenNodes(this, target) * 10;
		} else {
			h = 0;
		}
	}
}