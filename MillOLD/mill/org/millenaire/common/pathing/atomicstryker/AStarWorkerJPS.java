package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarWorkerJPS extends AStarWorker {
	/**
	 * Important preset value. Determines after how many non-jump-nodes in a
	 * direction an abort is executed, in order to prevent near-infinite loops
	 * in cases of unobstructed space pathfinding. After this distance is met,
	 * the reached node is perceived as jump node by default and treated as
	 * such.
	 */
	private final static int MAX_SKIP_DISTANCE = 25;

	/**
	 * Array of neighbouring XZ coordinates to any given coordinate Order: N,
	 * NE, E, SE, S, SW, W, NW
	 */
	private final static int[][] neighbourOffsets = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };

	private final PriorityQueue<AStarNode> openQueue;
	private AStarNode currentNode;

	public AStarWorkerJPS(final AStarPathPlanner creator) {
		super(creator);
		openQueue = new PriorityQueue<AStarNode>();
	}

	/**
	 * Adds a new AStarNode to the queue unless it is already among the closed
	 * nodes, in which case it only updates the closed Node with the new
	 * distance.
	 * 
	 * @param newNode
	 *            Node to be saved
	 */
	private void addOrUpdateNode(final AStarNode newNode) {
		boolean found = false;
		final Iterator<AStarNode> iter = closedNodes.iterator();
		while (iter.hasNext()) {
			final AStarNode toUpdate = iter.next();

			if (newNode.equals(toUpdate)) {
				toUpdate.updateDistance(newNode.getG(), newNode.parent);
				found = true;
				break;
			}
		}
		if (!found) {
			// System.out.println("offering: "+newNode);
			openQueue.offer(newNode);
		}
	}

	/**
	 * Traces the path back to our starting Node, interpolating new Nodes
	 * inbetween the Jump Poins as we go.
	 * 
	 * @param start
	 *            Node we start to trace back from, should be target Node
	 * @return list of adjacent AStarNodes from target to start Nodes
	 */
	private ArrayList<AStarNode> backTrace(final AStarNode start) {
		final ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
		foundpath.add(currentNode);

		int x;
		int y;
		int z;
		int px;
		int pz;
		int dx;
		int dz;

		while (!currentNode.equals(start)) {
			x = currentNode.x;
			y = currentNode.y;
			z = currentNode.z;
			px = currentNode.parent.x;
			pz = currentNode.parent.z;

			// normalized direction to parent
			dx = (px - x) / Math.max(Math.abs(x - px), 1);
			dz = (pz - z) / Math.max(Math.abs(z - pz), 1);

			x += dx;
			z += dz;
			// add interpolated nodes
			while (x != px || z != pz) {
				y = getGroundNodeHeight(x, y, z);
				foundpath.add(new AStarNode(x, y, z, 0, null));
				x += dx;
				z += dz;
			}

			foundpath.add(currentNode.parent);
			currentNode = currentNode.parent;
		}
		return foundpath;
	}

	/**
	 * Returns all walkable AStarNodes adjacent to an AStarNode. If the node has
	 * a parent, use the JPS algorithm to prune superfluous Neighbors from ever
	 * being considered.
	 * 
	 * @param node
	 *            AStarNode to check around
	 * @return List of AStarNodes around node
	 */
	private ArrayList<AStarNode> findNeighbours(final AStarNode node) {
		final ArrayList<AStarNode> r = new ArrayList<AStarNode>();

		final int x = node.x;
		final int y = node.y;
		final int z = node.z;
		final int dist = node.getG();

		if (node.parent != null) {
			final int px = node.parent.x;
			final int py = node.parent.y;
			final int pz = node.parent.z;
			final boolean stairs = py != y;
			int nY;

			// normalized direction from parent
			final int dx = (x - px) / Math.max(Math.abs(x - px), 1);
			final int dz = (z - pz) / Math.max(Math.abs(z - pz), 1);

			if (dx != 0 && dz != 0) // prune diagonal
			{
				if (stairs) {
					return getAllNeighborsWithoutParent(x, y, z, dx, dz, node);
				}

				int left = 0;
				int right = 0;
				nY = getGroundNodeHeight(x, y, z + dz);
				if (nY > 0) {
					left = nY;
					r.add(new AStarNode(x, nY, z + dz, dist + 1, node));
				}
				nY = getGroundNodeHeight(x + dx, y, z);
				if (nY > 0) {
					right = nY;
					r.add(new AStarNode(x + dx, nY, z, dist + 1, node));
				}
				if (left != 0 || right != 0) {
					r.add(new AStarNode(x + dx, Math.max(left, right), z + dz, dist + 2, node));
				}
				if (left != 0) {
					if (getGroundNodeHeight(x - dx, py, z) == 0) {
						r.add(new AStarNode(x - dx, left, z + dz, dist + 2, node));
					}
				}
				if (right != 0) {
					if (getGroundNodeHeight(x, py, z - dz) == 0) {
						r.add(new AStarNode(x + dx, right, z - dz, dist + 2, node));
					}
				}
			} else // prune straight
			{
				if (dx == 0) // z axis
				{
					nY = getGroundNodeHeight(x, y, z + dz);
					if (nY > 0) // is step left/right possible?
					{
						r.add(new AStarNode(x, nY, z + dz, dist + 1, node));
						if (stairs) {
							r.add(new AStarNode(x + 1, nY, z + dz, dist + 2, node));
							r.add(new AStarNode(x - 1, nY, z + dz, dist + 2, node));
						} else {
							int nnY = getGroundNodeHeight(x + 1, nY, z);
							if (nnY == 0) // top blocked or stairs?
							{
								r.add(new AStarNode(x + 1, nY, z + dz, dist + 2, node));
							}
							nnY = getGroundNodeHeight(x - 1, nY, z);
							if (nnY == 0) // below blocked or stairs?
							{
								r.add(new AStarNode(x - 1, nY, z + dz, dist + 2, node));
							}
						}
					}
				} else // x axis
				{
					nY = getGroundNodeHeight(x + dx, y, z);
					if (nY > 0) // is step up/down possible?
					{
						r.add(new AStarNode(x + dx, nY, z, dist + 1, node));
						if (stairs) {
							r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node));
							r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node));
						} else {
							int nnY = getGroundNodeHeight(x, nY, z + 1);
							if (nnY == 0) // right blocked?
							{
								r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node));
							}
							nnY = getGroundNodeHeight(x, nY, z - 1);
							if (nnY == 0) // left blocked?
							{
								r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node));
							}
						}
					}
				}
			}
		} else {
			for (final int[] offset : neighbourOffsets) // try all neighbours,
														// ding dong!
			{
				final int nY = getGroundNodeHeight(x + offset[0], y, z + offset[1]);
				if (nY > 0) {
					r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node));
				}
			}
		}

		return r;
	}

	/**
	 * An attempt to fix the 'V-turn weakness', we manually force-add all
	 * neighbors when a jump is both diagonal and non-horizontal. Does not add
	 * the neighbor in the direction we just came from.
	 * 
	 * @param x
	 *            jump origin coordinate
	 * @param y
	 *            jump origin coordinate
	 * @param z
	 *            jump origin coordinate
	 * @param dx
	 *            jump direction
	 * @param dz
	 *            jump direction
	 * @param node
	 *            AStarNode we jumped from
	 * @return
	 */
	private ArrayList<AStarNode> getAllNeighborsWithoutParent(final int x, final int y, final int z, final int dx, final int dz, final AStarNode node) {
		final ArrayList<AStarNode> r = new ArrayList<AStarNode>();
		for (final int[] offset : neighbourOffsets) {
			if (offset[0] == -dx && offset[1] == -dz) // disregard neighbor we
														// come from!
			{
				continue;
			}
			final int nY = getGroundNodeHeight(x + offset[0], y, z + offset[1]);
			if (nY > 0) {
				r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node));
			}
		}
		return r;
	}

	/**
	 * Attempts to find a viable Node (height) next to the coordinates provided,
	 * with a maximum allowed offset of 1.
	 * 
	 * @param xN
	 *            x coordinate
	 * @param yN
	 *            y coordinate
	 * @param zN
	 *            z coordinate
	 * @return y coordinate of a viable Node or 0 if no such Node can be found
	 */
	private int getGroundNodeHeight(final int xN, final int yN, final int zN) {
		if (AStarStatic.isViable(worldObj, xN, yN, zN, 0, boss.config)) {
			// System.out.println("getGroundNodeHeight ["+xN+"|"+yN+"|"+zN+"], result "+yN);
			return yN;
		}
		if (AStarStatic.isViable(worldObj, xN, yN - 1, zN, -1, boss.config)) {
			// System.out.println("getGroundNodeHeight ["+xN+"|"+yN+"|"+zN+"], result "+(yN-1));
			return yN - 1;
		}
		if (AStarStatic.isViable(worldObj, xN, yN + 1, zN, 1, boss.config)) {
			// System.out.println("getGroundNodeHeight ["+xN+"|"+yN+"|"+zN+"], result "+(yN+1));
			return yN + 1;
		}
		// System.out.println("getGroundNodeHeight ["+xN+"|"+yN+"|"+zN+"] found no close height");
		return 0;
	}

	@Override
	public ArrayList<AStarNode> getPath(final AStarNode start, final AStarNode end, final boolean searchMode) {
		openQueue.offer(start);
		targetNode = end;
		currentNode = start;

		// System.out.println("Start Node "+start.x+", "+start.y+", "+start.z);
		// System.out.println("Target Node "+end.x+", "+end.y+", "+end.z);

		while (!openQueue.isEmpty() && !shouldInterrupt()) {
			currentNode = openQueue.poll();
			// System.out.println("queue polled: "+currentNode);
			closedNodes.add(currentNode);

			// Kinniken
			// Modified ending condition to support tolerance
			if (isNodeEnd(currentNode, end) || identifySuccessors(currentNode)) {
				// we found the target! OH, MY!
				return backTrace(start);
			}
		}

		return null;
	}

	/**
	 * Finds all viable successor Nodes around a Node and does JPS in their
	 * directions.
	 * 
	 * @param node
	 *            AStarNode to find Jump Nodes from
	 * @return true if the target node was run over, false otherwise
	 */
	private boolean identifySuccessors(final AStarNode node) {
		final int x = node.x;
		final int y = node.y;
		final int z = node.z;

		final ArrayList<AStarNode> successors = findNeighbours(node);
		for (final AStarNode s : successors) {
			// System.out.println("checking neighbor: "+s);
			final AStarNode jumpPoint = jump(s.x, s.y, s.z, x, y, z);
			if (jumpPoint != null) {
				if (closedNodes.contains(jumpPoint)) {
					continue;
				}
				addOrUpdateNode(jumpPoint);
			}
		}

		return false;
	}

	/**
	 * Recursive Jumper as described by JPS algorithm
	 * 
	 * @param nextX
	 * @param nextY
	 * @param nextZ
	 * @param px
	 * @param py
	 * @param pz
	 * @return AStarNode of Jumping Point found, or null if none encountered
	 */
	private AStarNode jump(final int nextX, final int nextY, final int nextZ, final int px, int py, final int pz) {
		// System.out.printf("jump landed on [%d|%d|%d] from [%d|%d|%d], dir [%d|%d] \n",
		// nextX, nextY, nextZ, px, py, pz, nextX-px, nextZ-pz);
		final int x = nextX;
		int y = nextY;
		final int z = nextZ;
		final int dist = currentNode.getG() + Math.abs(x - currentNode.x) + Math.abs(y - currentNode.y) + Math.abs(z - currentNode.z);

		final int dx = x - px;
		final int dz = z - pz;
		py = y;
		y = getGroundNodeHeight(x, py, z);

		if (y == 0) {
			return null;
		} else if (isCoordsEnd(x, y, z, targetNode) || dist >= MAX_SKIP_DISTANCE) {
			return new AStarNode(x, y, z, dist, currentNode, targetNode);
		}

		// check for moving up or down along the path
		final int nxY = dx != 0 ? getGroundNodeHeight(x + dx, y, z) : 0;
		final int nzY = dz != 0 ? getGroundNodeHeight(x, y, z + dz) : 0;

		// check for forced neighbors along the diagonal
		if (dx != 0 && dz != 0) {
			if (getGroundNodeHeight(x - dx, y, z + dz) != 0 && getGroundNodeHeight(x - dx, py, z) == 0 || getGroundNodeHeight(x + dx, y, z - dz) != 0 && getGroundNodeHeight(x, py, z - dz) == 0) {
				return new AStarNode(x, y, z, dist, currentNode, targetNode);
			}
		}
		// horizontally/vertically
		else {
			if (dx != 0) { // moving along x
				if (nxY != y || getGroundNodeHeight(x, y, z + 1) == 0 && getGroundNodeHeight(x + dx, nxY, z + 1) != 0 || getGroundNodeHeight(x, y, z - 1) == 0
						&& getGroundNodeHeight(x + dx, nxY, z - 1) != 0) {
					return new AStarNode(x, y, z, dist, currentNode, targetNode);
				}
			} else { // moving along z
				if (nzY != y || getGroundNodeHeight(x + 1, y, z) == 0 && getGroundNodeHeight(x + 1, nzY, z + dz) != 0 || getGroundNodeHeight(x - 1, y, z) == 0
						&& getGroundNodeHeight(x - 1, nzY, z + dz) != 0) {
					return new AStarNode(x, y, z, dist, currentNode, targetNode);
				}
			}
		}

		// when moving diagonally, must check for vertical/horizontal jump
		// points
		if (dx != 0 && dz != 0) {
			final AStarNode jx = jump(x + dx, y, z, x, y, z);
			final AStarNode jy = jump(x, y, z + dz, x, y, z);
			if (jx != null || jy != null) {
				return new AStarNode(x, y, z, dist, currentNode, targetNode);
			}
		}

		// moving diagonally, must make sure one of the vertical/horizontal
		// neighbors is open to allow the path
		if (nxY != 0 || nzY != 0) {
			return jump(x + dx, y, z + dz, x, y, z);
		} else {
			return null;
		}
	}
}
