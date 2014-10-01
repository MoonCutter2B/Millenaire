package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Static parts of AStarPath calculation and translation
 * 
 * @author AtomicStryker
 */

public class AStarStatic {

	/**
	 * Array of standard 3D neighbour Block offsets and their 'reach cost' as
	 * fourth value
	 */
	final static int candidates[][] = { { 0, 0, -1, 1 }, { 0, 0, 1, 1 },
			{ 0, 1, 0, 1 }, { 1, 0, 0, 1 }, { -1, 0, 0, 1 }, { 1, 1, 0, 2 },
			{ -1, 1, 0, 2 }, { 0, 1, 1, 2 }, { 0, 1, -1, 2 }, { 1, -1, 0, 1 },
			{ -1, -1, 0, 1 }, { 0, -1, 1, 1 }, { 0, -1, -1, 1 } };

	final static int candidates_allowdrops[][] = { { 0, 0, -1, 1 },
			{ 0, 0, 1, 1 }, { 1, 0, 0, 1 }, { -1, 0, 0, 1 }, { 1, 1, 0, 2 },
			{ -1, 1, 0, 2 }, { 0, 1, 1, 2 }, { 0, 1, -1, 2 }, { 1, -1, 0, 1 },
			{ -1, -1, 0, 1 }, { 0, -1, 1, 1 }, { 0, -1, -1, 1 },
			{ 1, -2, 0, 1 }, { -1, -2, 0, 1 }, { 0, -2, 1, 1 },
			{ 0, -2, -1, 1 } };

	/**
	 * Computes the Array of AStarNodes around a target from which the target is
	 * in reaching distance, sorts this array in such a way that Nodes closer to
	 * the given worker coordinates come first. Does not check if the target is
	 * reachable at all. Can return an empty array if there is no possible way
	 * to stand near the target (if it's in sold earth for example).
	 * 
	 * @param worldObj
	 *            World instance
	 * @param workerX
	 *            worker coordinate
	 * @param workerY
	 *            worker coordinate
	 * @param workerZ
	 *            worker coordinate
	 * @param posX
	 *            Node coordinate
	 * @param posY
	 *            Node coordinate
	 * @param posZ
	 *            Node coordinate
	 * @return sorted Array of AStarNodes in accessing distance to the target
	 *         coordinates
	 */
	@SuppressWarnings("unchecked")
	public static AStarNode[] getAccessNodesSorted(final World worldObj,
			final int workerX, final int workerY, final int workerZ,
			final int posX, final int posY, final int posZ,
			final AStarConfig config) {
		final ArrayList<AStarNode> resultList = new ArrayList<AStarNode>();

		AStarNode check;
		for (int xIter = -2; xIter <= 2; xIter++) {
			for (int zIter = -2; zIter <= 2; zIter++) {
				for (int yIter = -3; yIter <= 2; yIter++) {
					check = new AStarNode(posX + xIter, posY + yIter, posZ
							+ zIter, Math.abs(xIter) + Math.abs(yIter), null);
					if (AStarStatic.isViable(worldObj, check, 1, config)) {
						resultList.add(check);
					}
				}
			}
		}

		Collections.sort(resultList);

		int count = 0;
		final AStarNode[] returnVal = new AStarNode[resultList.size()];
		while (!resultList.isEmpty() && (check = resultList.get(0)) != null) {
			returnVal[count] = check;
			resultList.remove(0);
			count++;
		}

		return returnVal;
	}

	public static double getDistanceBetweenCoords(final int x, final int y,
			final int z, final int posX, final int posY, final int posZ) {
		return Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2)
				+ Math.pow(z - posZ, 2));
	}

	/**
	 * Calculates the Euclidian distance between 2 AStarNode instances
	 * 
	 * @param a
	 *            Node
	 * @param b
	 *            Node
	 * @return Euclidian Distance between the 2 given Nodes
	 */
	public static double getDistanceBetweenNodes(final AStarNode a,
			final AStarNode b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)
				+ Math.pow(a.z - b.z, 2));
	}

	/**
	 * Returns the absolute movement speed of an Entity in coordinate space
	 * 
	 * @param entLiving
	 *            Entity
	 * @return euclidian List length of Entity movement List
	 */
	public static double getEntityLandSpeed(final EntityLiving entLiving) {
		return Math.sqrt(entLiving.motionX * entLiving.motionX
				+ entLiving.motionZ * entLiving.motionZ);
	}

	public static int getIntCoordFromDoubleCoord(final double input) {
		return MathHelper.floor_double(input);
	}

	public static boolean isLadder(final World world, final Block b,
			final int x, final int y, final int z) {
		if (b != null) {
			return b.isLadder(world, x, y, z, null);
		}
		return false;
	}

	/**
	 * Determines if an Entity can pass through a Block
	 * 
	 * @param worldObj
	 *            World
	 * @param ix
	 *            coordinate
	 * @param iy
	 *            coordinate
	 * @param iz
	 *            coordinate
	 * @return true if the Block is passable, false otherwise
	 */
	public static boolean isPassableBlock(final World worldObj, final int ix,
			final int iy, final int iz, final AStarConfig config) {
		// Kinniken
		// Added test for fences
		// Note that test isn't perfect (entities can walk on the top of fences,
		// this is forbidden here)
		if (iy > 0) {
			final Block block = worldObj.getBlock(ix, iy - 1, iz);

			if (block == Blocks.fence || block == Blocks.iron_bars
					|| block == Blocks.nether_brick_fence) {
				return false;
			}
		}

		final Block block = worldObj.getBlock(ix, iy, iz);
		if (block != null) {
			// Kinniken
			// Allows passage through wooden doors and fence gates
			if (config.canUseDoors) {
				if (block == Blocks.wooden_door || block == Blocks.fence_gate) {
					return true;
				}
			}

			return !block.getMaterial().isSolid();
		}

		return true;
	}

	/**
	 * AStarNode wrapper for isViable
	 */
	public static boolean isViable(final World worldObj,
			final AStarNode target, final int yoffset, final AStarConfig config) {
		return isViable(worldObj, target.x, target.y, target.z, yoffset, config);
	}

	/**
	 * Determines whether or not an AStarNode is traversable Checks if a 2 Block
	 * high nonblocked space exists with this Node as bottom Also checks if you
	 * can reach this node without your head passing through a solid overhang
	 * (vertical diagonal)
	 * 
	 * @param worldObj
	 *            World to check in
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param z
	 *            coordinate
	 * @param yoffset
	 *            Height offset relative to the previous Node
	 * @return true if the target coordinates can be passed as 2 block high
	 *         entity, false otherwise
	 */
	public static boolean isViable(final World worldObj, final int x,
			final int y, final int z, int yoffset, final AStarConfig config) {
		final Block block = worldObj.getBlock(x, y, z);

		if (block == Blocks.ladder
				&& isPassableBlock(worldObj, x, y + 1, z, config)) {
			return true;
		}

		if (!isPassableBlock(worldObj, x, y, z, config)
				|| !isPassableBlock(worldObj, x, y + 1, z, config)
				|| isPassableBlock(worldObj, x, y - 1, z, config)
				&& (block != Blocks.water || block != Blocks.flowing_water)) {
			return false;
		}

		if (!config.canSwim
				&& (block == Blocks.water || block == Blocks.flowing_water)) {
			return false;
		}

		if (yoffset < 0) {
			yoffset *= -1;
		}
		int ycheckhigher = 1;
		while (ycheckhigher <= yoffset) {
			if (!isPassableBlock(worldObj, x, y + yoffset, z, config)) {
				return false;
			}
			ycheckhigher++;
		}

		return true;
	}

	/**
	 * Converts an ArrayList of AStarNodes into an MC style PathEntity
	 * 
	 * @param input
	 *            List of AStarNodes
	 * @return MC pathing compatible PathEntity
	 */
	public static AS_PathEntity translateAStarPathtoPathEntity(
			final World world, List<AStarNode> input, final AStarConfig config) {
		if (!config.canTakeDiagonals) {
			final List<AStarNode> oldInput = input;
			input = new ArrayList<AStarNode>();
			for (int i = 0; i < oldInput.size() - 1; i++) {
				input.add(oldInput.get(i));
				if (oldInput.get(i).x != oldInput.get(i + 1).x
						&& oldInput.get(i).z != oldInput.get(i + 1).z) {// diagonal
					if (oldInput.get(i).y == oldInput.get(i + 1).y) {// only
																		// "flat"
																		// diagonals
						if (!isPassableBlock(world, oldInput.get(i).x,
								oldInput.get(i).y - 1, oldInput.get(i + 1).z,
								config)
								&& isPassableBlock(world, oldInput.get(i).x,
										oldInput.get(i).y,
										oldInput.get(i + 1).z, config)
								&& isPassableBlock(world, oldInput.get(i).x,
										oldInput.get(i).y + 1,
										oldInput.get(i + 1).z, config)) {
							final AStarNode newNode = new AStarNode(
									oldInput.get(i).x, oldInput.get(i).y,
									oldInput.get(i + 1).z, 0, null);
							input.add(newNode);
						} else {// if it didn't work one way assume it does the
								// other
							final AStarNode newNode = new AStarNode(
									oldInput.get(i + 1).x, oldInput.get(i).y,
									oldInput.get(i).z, 0, null);
							input.add(newNode);
						}
					}
				}
			}
		}

		final AS_PathPoint[] points = new AS_PathPoint[input.size()];
		AStarNode reading;
		int i = 0;
		int size = input.size();
		// System.out.println("Translating AStar Path with "+size+" Hops:");

		while (size > 0) {
			reading = input.get(size - 1);
			points[i] = new AS_PathPoint(reading.x, reading.y, reading.z);
			points[i].isFirst = i == 0;
			points[i].setIndex(i);
			points[i].setTotalPathDistance(i);
			points[i].setDistanceToNext(1F);
			points[i].setDistanceToTarget(size);

			if (i > 0) {
				points[i].setPrevious(points[i - 1]);
			}
			// System.out.println("PathPoint: ["+reading.x+"|"+reading.y+"|"+reading.z+"]");

			input.remove(size - 1);
			size--;
			i++;
		}

		// System.out.println("Translated AStar PathEntity with length: "+
		// points.length);

		return new AS_PathEntity(points);
	}
}