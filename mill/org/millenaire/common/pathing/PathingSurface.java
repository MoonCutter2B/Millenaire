package org.millenaire.common.pathing;

import java.util.Collections;
import java.util.LinkedList;

/*
 * All references to tiles mean the exact tiles on which's surface the player walks!
 * thus this data structure has it's flaws, in particular we have to search the tiles before we can calculate the way(it's not that expensive anyway)
 */

public class PathingSurface {

	// Subclass of Chunk, we use this to save a lot of memory. In this case only
	// the tiles of the surface are saved,
	// all other tiles(which were null in the array) are not saved. Furthermore
	// are all tiles lost which are not reachable from the administration
	// building.
	public class ExtendedPathTile extends PathingPathCalcTile implements
			Comparable<ExtendedPathTile> {
		// saving all neighbors
		public LinkedList<ExtendedPathTile> neighbors;
		// we use this field later in the calculation of the path
		public short distance;
		// we use this key to effectively find tiles we search
		public int key;

		public ExtendedPathTile(final boolean walkable, final boolean lad,
				final short[] pos) {
			super(walkable, lad, pos);
			this.neighbors = new LinkedList<ExtendedPathTile>();
			this.key = pos[0] + (pos[1] << 10) + (pos[2] << 20);
			this.distance = Short.MAX_VALUE;
		}

		public ExtendedPathTile(final PathingPathCalcTile c) {
			super(c);
			this.neighbors = new LinkedList<ExtendedPathTile>();
			this.key = c.position[0] + (c.position[1] << 10)
					+ (c.position[2] << 20);
			this.distance = Short.MAX_VALUE;
		}

		@Override
		public int compareTo(final ExtendedPathTile arg0) {

			if (this.key == arg0.key) {
				return 0;
			} else if (this.key > arg0.key) {
				return 1;
			} else {
				return -1;
			}
		}

		@Override
		public boolean equals(final Object o) {

			if (o == null || !(o instanceof ExtendedPathTile)) {
				return false;
			}

			return this.key == ((ExtendedPathTile) o).key;
		}

		@Override
		public int hashCode() {
			return key;
		}
	}

	// used to save all tiles that are left in our linked data structure, we
	// need this to effectively find tiles
	// if we calculate ways from tiles which are not the central tile
	public LinkedList<ExtendedPathTile> alltiles;

	// input: three dimensional array[x][y][z](x=width, y=height, z=depth) of
	// tiles and the tile which is the central tile to calculate and only save
	// possible ways
	// IMPORTANT: An entry of the array has to be null if the tile is passable
	// (air,grass,flowers...)!
	//
	public PathingSurface(final PathingPathCalcTile[][][] region,
			final PathingPathCalcTile ct) {
		// Array used to save the surface of the region used for creation.
		// A tile get's part of the surface if a player is able to be standing
		// on it(every solid tile above
		// which two tiles are empty(null)), those tiles are not necessarily to
		// be reached from every other tile!
		final ExtendedPathTile[][][] surface = new ExtendedPathTile[region.length][region[0].length][region[0][0].length];
		ExtendedPathTile centraltile;

		// checking every tile for fitting our surface definition
		for (int i = 0; i < region.length; i++) {
			for (int j = 0; j < region[0].length - 2; j++) {
				for (int k = 0; k < region[0][0].length; k++) {
					// given condition is supposed to be true if the checked
					// tile is not null, both spaces above the tile are empty or
					// the tile is a ladder
					if (region[i][j][k] != null && // CDJ note: added () arround
													// the || condition to
													// ensure that
													// region[i][j][k] is
													// non-null. Or should chck
													// be addd before third
													// part?
													// HD note: you are right,
													// there was a () missing.
							(j + 2 < region[0].length
									&& region[i][j + 1][k] == null
									&& region[i][j + 2][k] == null || region[i][j][k].ladder
									&& (region[i][j + 1][k] == null || region[i][j + 1][k].ladder))) {
						surface[i][j][k] = new ExtendedPathTile(region[i][j][k]);
					} else {
						surface[i][j][k] = null;
					}
				}
			}
		}
		centraltile = new ExtendedPathTile(ct);

		// now we reduce the 3d surface-array to our linked data structure
		alltiles = new LinkedList<ExtendedPathTile>();
		// the tile we currently work with
		ExtendedPathTile current;
		// a list that stores all tiles that have not yet been worked on
		final LinkedList<ExtendedPathTile> toprocess = new LinkedList<ExtendedPathTile>();

		if (surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]] != null) {
			toprocess
					.add(surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]]);
			surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]].distance -= 1;
		}

		while (!toprocess.isEmpty()) {
			// retrieve and remove the first object of the list
			current = toprocess.pollFirst();
			alltiles.add(current);
			final short i = current.position[0];
			final short j = current.position[1];
			final short k = current.position[2];

			// iterating through all heights
			for (byte t = -1; t <= 1; t++) {
				// a tile below a ladder always gets added if it's not empty, a
				// tile above only if it is itself a ladder and has no solid
				// tile above
				if (surface[i][j][k].ladder) {
					if (j + t >= 0 && j + t < surface[0].length) {
						if (surface[i][j + 1][k].ladder) {
							if (surface[i][j + t][k].distance == Short.MAX_VALUE) {
								toprocess.add(surface[i][j + t][k]);
								surface[i][j + t][k].distance -= 1;
							}
							current.neighbors.add(surface[i][j + t][k]);
						}
						if (surface[i][j - 1][k] != null) {
							if (surface[i][j + t][k].distance == Short.MAX_VALUE) {
								toprocess.add(surface[i][j + t][k]);
								surface[i][j + t][k].distance -= 1;
							}
							current.neighbors.add(surface[i][j + t][k]);
						}
					}
				} else {
					// tile right with all elevations
					if (j + t >= 0 && j + t < surface[0].length) {
						if (i + 1 < surface.length) {
							if (surface[i + 1][j + t][k] != null) {
								// if the tile is a ladder, we only see it as
								// neighbor if the tile is one field
								// higher(because we don't walk on the surface
								// of a ladder)
								// we've already checked that above any viewed
								// ladder field is at least 1 field empty
								if (surface[i + 1][j + t][k].ladder) {
									if (t == 1
											|| t == 0
											&& surface[i + 1][j + t + 2][k] == null) {
										if (surface[i + 1][j + t][k].distance == Short.MAX_VALUE) {
											toprocess
													.add(surface[i + 1][j + t][k]);
											surface[i + 1][j + t][k].distance -= 1;
										}
										current.neighbors.add(surface[i + 1][j
												+ t][k]);
									}
								} else {
									if (surface[i + 1][j + t][k].distance == Short.MAX_VALUE) {
										toprocess.add(surface[i + 1][j + t][k]);
										surface[i + 1][j + t][k].distance -= 1;
									}
									// checking when adding neighbors if 3 tiles
									// are empty if we change height
									if (t == 0) {
										current.neighbors.add(surface[i + 1][j
												+ t][k]);
									} else if (t == 1
											&& surface[i][j + 3][k] == null) {
										current.neighbors.add(surface[i + 1][j
												+ t][k]);
									} else if (t == -1
											&& surface[i + 1][j + t + 3][k] == null) {
										current.neighbors.add(surface[i + 1][j
												+ t][k]);
									}
								}
							}
						}
						// tile left with all elevations
						if (i - 1 >= 0) {
							if (surface[i - 1][j + t][k] != null) {
								if (surface[i - 1][j + t][k].ladder) {
									if (t == 1
											|| t == 0
											&& surface[i - 1][j + t + 2][k] == null) {
										if (surface[i - 1][j + t][k].distance == Short.MAX_VALUE) {
											toprocess
													.add(surface[i - 1][j + t][k]);
											surface[i - 1][j + t][k].distance -= 1;
										}
										current.neighbors.add(surface[i - 1][j
												+ t][k]);
									}
								} else {
									if (surface[i - 1][j + t][k].distance == Short.MAX_VALUE) {
										toprocess.add(surface[i - 1][j + t][k]);
										surface[i - 1][j + t][k].distance -= 1;
									}
									if (t == 0) {
										current.neighbors.add(surface[i - 1][j
												+ t][k]);
									} else if (t == 1
											&& surface[i][j + 3][k] == null) {
										current.neighbors.add(surface[i - 1][j
												+ t][k]);
									} else if (t == -1
											&& surface[i - 1][j + t + 3][k] == null) {
										current.neighbors.add(surface[i - 1][j
												+ t][k]);
									}
								}
							}
						}
						// tile behind with all elevations
						if (k - 1 >= 0) {
							if (surface[i][j + t][k - 1] != null) {
								if (surface[i][j + t][k - 1].ladder) {
									if (t == 1
											|| t == 0
											&& surface[i][j + t + 2][k - 1] == null) {
										if (surface[i][j + t][k - 1].distance == Short.MAX_VALUE) {
											toprocess
													.add(surface[i][j + t][k - 1]);
											surface[i][j + t][k - 1].distance -= 1;
										}
										current.neighbors
												.add(surface[i][j + t][k - 1]);
									}
								} else {
									if (surface[i][j + t][k - 1].distance == Short.MAX_VALUE) {
										toprocess.add(surface[i][j + t][k - 1]);
										surface[i][j + t][k - 1].distance -= 1;
									}
									if (t == 0) {
										current.neighbors
												.add(surface[i][j + t][k - 1]);
									} else if (t == 1
											&& surface[i][j + 3][k] == null) {
										current.neighbors
												.add(surface[i][j + t][k - 1]);
									} else if (t == -1
											&& surface[i][j + t + 3][k - 1] == null) {
										current.neighbors
												.add(surface[i][j + t][k - 1]);
									}
								}
							}
						}
						// tile in front with all elevations
						if (k + 1 < surface[0][0].length) {
							if (surface[i][j + t][k + 1] != null) {
								if (surface[i][j + t][k + 1].ladder) {
									if (t == 1
											|| t == 0
											&& surface[i][j + t + 2][k + 1] == null) {
										if (surface[i][j + t][k + 1].distance == Short.MAX_VALUE) {
											toprocess
													.add(surface[i][j + t][k + 1]);
											surface[i][j + t][k + 1].distance -= 1;
										}
										current.neighbors
												.add(surface[i][j + t][k + 1]);
									}
								} else {
									if (surface[i][j + t][k + 1].distance == Short.MAX_VALUE) {
										toprocess.add(surface[i][j + t][k + 1]);
										surface[i][j + t][k + 1].distance -= 1;
									}
									if (t == 0) {
										current.neighbors
												.add(surface[i][j + t][k + 1]);
									} else if (t == 1
											&& surface[i][j + 3][k] == null) {
										current.neighbors
												.add(surface[i][j + t][k + 1]);
									} else if (t == -1
											&& surface[i][j + t + 3][k + 1] == null) {
										current.neighbors
												.add(surface[i][j + t][k + 1]);
									}
								}
							}
						}
					}
				}
			}
		}
		// sort the List, so that we can find the start faster
		Collections.sort(alltiles);
	}

	public boolean contains(final short[] pos) {
		boolean contains = false;

		// the key of the start position
		final int targetkey = pos[0] + (pos[1] << 10) + (pos[2] << 20);
		int currentindex = alltiles.size() / 2;
		// needed for binary search
		int change = currentindex;
		// the tile we currently work with
		ExtendedPathTile current = alltiles.get(currentindex);

		if (current.key == alltiles.get(0).key) {
			current = alltiles.get(0);
		} else {
			while (current.key != targetkey && change > 1) {

				if (current.key > targetkey) {
					currentindex = currentindex - change / 2;
					change = (change + 1) / 2;
				}
				if (current.key < targetkey) {
					currentindex = currentindex + change / 2;
					change = (change + 1) / 2;
				}
				current = alltiles.get(currentindex);
			}

		}

		if (current.position[0] == pos[0] && current.position[1] == pos[1]
				&& current.position[2] == pos[2]) {
			contains = true;
		}
		return contains;
	}

	public boolean containsSimple(final short[] pos) {
		for (final ExtendedPathTile tile : alltiles) {
			if (tile.position[0] == pos[0] && tile.position[1] == pos[1]
					&& tile.position[2] == pos[2]) {
				return true;
			}
		}
		return false;
	}

	// Input: the coordinates of start and target position as an array (x,y,z)
	// of shorts.
	// Note: the target location is always the field on which the player stands
	// at the end of his journey.
	// for example: we are going outside to get wood, the start location is the
	// tile on which we currently stand,
	// or if we are currently using a ladder, the field we are currently in. The
	// target location is always a field
	// next to the tree, not the tree itself
	// Output: LinkedList of coordinates in order from the start to the target
	// Returns null if there is no way
	public LinkedList<short[]> getPath(final short[] start, final short[] target) {
		// saves the way in coordinates
		final LinkedList<short[]> way = new LinkedList<short[]>();

		// the key of the start position
		final int targetkey = target[0] + (target[1] << 10) + (target[2] << 20);
		int currentindex = alltiles.size() / 2;
		// needed for binary search
		int change = currentindex;
		// the chunk we currently work with
		ExtendedPathTile current = alltiles.get(currentindex);

		// implementing a modified binary search to find the starting tile if it
		// is in our surface

		if (targetkey == alltiles.get(0).key) {
			current = alltiles.get(0);
		} else {
			while (current.key != targetkey && change > 1) {

				if (current.key > targetkey) {
					currentindex = currentindex - change / 2;
					change = (change + 1) / 2;
				}
				if (current.key < targetkey) {
					currentindex = currentindex + change / 2;
					change = (change + 1) / 2;
				}
				current = alltiles.get(currentindex);
			}
		}

		// Linked List to save the Chunks whose neighbors we have not yet
		// evaluated
		final LinkedList<ExtendedPathTile> processing = new LinkedList<ExtendedPathTile>();
		final LinkedList<ExtendedPathTile> processing2 = new LinkedList<ExtendedPathTile>();

		Boolean wayfound = false;
		if (current.position[0] == target[0]
				&& current.position[1] == target[1]
				&& current.position[2] == target[2]) {
			processing.add(current);
			processing2.add(current);
			current.distance = 0;
		} else {
			return null;
		}

		// calculating distances for all tiles
		while (!processing.isEmpty()) {
			current = processing.pollFirst();
			for (int i = 0; i < current.neighbors.size(); i++) {
				if (current.neighbors.get(i).distance > current.distance + 1) {
					current.neighbors.get(i).distance = (short) (current.distance + 1);
					processing.add(current.neighbors.get(i));
					processing2.add(current.neighbors.get(i));
				}
			}
			// breaking if we reached the target
			if (current.position[0] == start[0]
					&& current.position[1] == start[1]
					&& current.position[2] == start[2]) {
				wayfound = true;
				break;
			}
		}

		// variable to remind which chunk was on the way before the current
		// tiles
		ExtendedPathTile nexttile = current;

		// now we find our way(backwards from start to target)
		if (wayfound) {
			way.addLast(current.position);
			while (current.distance > 0) {
				for (int i = 0; i < current.neighbors.size(); i++) {
					if (current.neighbors.get(i).distance < nexttile.distance) {
						nexttile = current.neighbors.get(i);
					}
				}
				current = nexttile;
				way.addLast(current.position);
			}
		}
		// resetting distances
		while (!processing2.isEmpty()) {
			current = processing2.pollFirst();
			current.distance = Short.MAX_VALUE - 1;
		}
		return way;
	}
}
