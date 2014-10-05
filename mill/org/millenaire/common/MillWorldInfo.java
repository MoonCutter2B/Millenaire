package org.millenaire.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

/**
 * Server-side object representing what a village knows of the land around it
 * 
 * @author cedricdj
 *
 */
public class MillWorldInfo implements Cloneable {

	public class UpdateThread extends Thread {
		int x;
		int z;

		@Override
		public void run() {
			updateChunk(x, z);
		}
	}

	private static final int MAP_MARGIN = 10;

	private static final int BUILDING_MARGIN = 5;

	private static final int VALID_HEIGHT_DIFF = 10;

	public static final int UPDATE_FREQUENCY = 1000;

	public static boolean[][] booleanArrayDeepClone(final boolean[][] source) {

		final boolean[][] target = new boolean[source.length][];

		for (int i = 0; i < source.length; i++) {
			target[i] = source[i].clone();
		}

		return target;
	}

	public static byte[][] byteArrayDeepClone(final byte[][] source) {

		final byte[][] target = new byte[source.length][];

		for (int i = 0; i < source.length; i++) {
			target[i] = source[i].clone();
		}

		return target;
	}

	static public boolean isForbiddenBlockForConstruction(final Block block) {
		return block == Blocks.water || block == Blocks.flowing_water
				|| block == Blocks.ice || block == Blocks.flowing_lava
				|| block == Blocks.lava || block == Blocks.planks
				|| block == Blocks.cobblestone || block == Blocks.brick_block
				|| block == Blocks.chest || block == Blocks.glass
				|| block == Mill.earth_decoration
				|| block == Mill.stone_decoration
				|| block == Mill.wood_decoration;
	}

	public static short[][] shortArrayDeepClone(final short[][] source) {

		final short[][] target = new short[source.length][];

		for (int i = 0; i < source.length; i++) {
			target[i] = source[i].clone();
		}

		return target;
	}

	public int length = 0;
	public int width = 0;
	public int chunkStartX = 0, chunkStartZ = 0;
	public int mapStartX = 0, mapStartZ = 0;
	public int yBaseline = 0;
	public short[][] topGround;
	public short[][] constructionHeight;
	public short[][] spaceAbove;
	public boolean[][] danger;

	public boolean[][] buildingLoc;

	public boolean[][] canBuild;

	public boolean[][] buildingForbidden;

	public boolean[][] water;

	public boolean[][] tree;
	public boolean[][] buildTested = null;

	public boolean[][] topAdjusted;

	public boolean[][] path;

	public int frequency = 10;

	private List<BuildingLocation> buildingLocations = new ArrayList<BuildingLocation>();
	public BuildingLocation locationIP;

	public int nbLoc = 0;

	public World world;

	public int lastUpdatedX, lastUpdatedZ;

	private int updateCounter;

	public MillWorldInfo() {

	}

	@Override
	@SuppressWarnings("unchecked")
	public MillWorldInfo clone() throws CloneNotSupportedException {
		final MillWorldInfo o = (MillWorldInfo) super.clone();
		o.topGround = shortArrayDeepClone(topGround);
		o.constructionHeight = shortArrayDeepClone(constructionHeight);
		o.spaceAbove = shortArrayDeepClone(spaceAbove);
		o.danger = booleanArrayDeepClone(danger);
		o.buildingLoc = booleanArrayDeepClone(buildingLoc);
		o.canBuild = booleanArrayDeepClone(canBuild);
		o.buildingForbidden = booleanArrayDeepClone(buildingForbidden);
		o.water = booleanArrayDeepClone(water);
		o.tree = booleanArrayDeepClone(tree);
		o.path = booleanArrayDeepClone(path);
		o.buildingLocations = new ArrayList<BuildingLocation>();
		o.buildingLocations.addAll(buildingLocations);
		return o;
	}

	private void createWorldInfo(final List<BuildingLocation> locations,
			final BuildingLocation blIP, final int pstartX, final int pstartZ,
			final int endX, final int endZ) throws MillenaireException {

		if (MLN.LogWorldInfo >= MLN.MINOR) {
			MLN.minor(this, "Creating world info: " + pstartX + "/" + pstartZ
					+ "/" + endX + "/" + endZ);
		}

		chunkStartX = pstartX >> 4;
		chunkStartZ = pstartZ >> 4;
		mapStartX = chunkStartX << 4;
		mapStartZ = chunkStartZ << 4;

		length = ((endX >> 4) + 1 << 4) - mapStartX;
		width = ((endZ >> 4) + 1 << 4) - mapStartZ;

		frequency = (int) Math.max(UPDATE_FREQUENCY * 1.0
				/ (length * width / 256), 10);

		if (frequency == 0) {
			throw new MillenaireException("Null frequency in createWorldInfo.");
		}

		if (MLN.LogWorldInfo >= MLN.MAJOR) {
			MLN.major(this, "Creating world info: " + mapStartX + "/"
					+ mapStartZ + "/" + length + "/" + width);
		}

		topGround = new short[length][width];
		constructionHeight = new short[length][width];
		spaceAbove = new short[length][width];
		danger = new boolean[length][width];
		buildingLoc = new boolean[length][width];
		buildingForbidden = new boolean[length][width];
		canBuild = new boolean[length][width];
		buildTested = new boolean[length][width];
		water = new boolean[length][width];
		tree = new boolean[length][width];
		path = new boolean[length][width];
		topAdjusted = new boolean[length][width];

		buildingLocations = new ArrayList<BuildingLocation>();

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++) {
				buildingLoc[i][j] = false;
				canBuild[i][j] = false;
			}
		}

		for (final BuildingLocation location : locations) {
			registerBuildingLocation(location);

		}

		locationIP = blIP;
		if (locationIP != null) {
			registerBuildingLocation(locationIP);
		}

		for (int i = 0; i < length; i += 16) {
			for (int j = 0; j < width; j += 16) {
				updateChunk(i, j);
			}
		}
		lastUpdatedX = 0;
		lastUpdatedZ = 0;
	}

	public BuildingLocation getLocationAtCoord(final Point p) {
		if (locationIP != null && locationIP.isInside(p)) {
			return locationIP;
		}

		for (final BuildingLocation bl : buildingLocations) {
			if (bl.isInside(p)) {
				return bl;
			}
		}

		return null;
	}

	public boolean isConstructionOrLoggingForbiddenHere(final Point p) {

		if (p.getiX() < mapStartX || p.getiZ() < mapStartZ
				|| p.getiX() >= mapStartX + length
				|| p.getiZ() >= mapStartZ + width) {
			return false;
		}

		return buildingForbidden[p.getiX() - mapStartX][p.getiZ() - mapStartZ];
	}

	private void registerBuildingLocation(final BuildingLocation bl) {

		if (MLN.LogWorldInfo >= MLN.MAJOR) {
			MLN.major(this, "Registering building location: " + bl);
		}

		buildingLocations.add(bl);

		final int sx = Math.max(bl.minxMargin - mapStartX, 0);
		final int sz = Math.max(bl.minzMargin - mapStartZ, 0);
		final int ex = Math.min(bl.maxxMargin - mapStartX, length + 1);
		final int ez = Math.min(bl.maxzMargin - mapStartZ, width + 1);

		for (int i = sx; i < ex; i++) {
			for (int j = sz; j < ez; j++) {
				buildingLoc[i][j] = true;
			}
		}
	}

	public void removeBuildingLocation(final BuildingLocation bl) {
		for (final BuildingLocation l : buildingLocations) {
			if (l.isLocationSamePlace(bl)) {
				buildingLocations.remove(l);
				break;
			}
		}

		final int sx = Math.max(bl.minxMargin - mapStartX, 0);
		final int sz = Math.max(bl.minzMargin - mapStartZ, 0);
		final int ex = Math.min(bl.maxxMargin - mapStartX, length);
		final int ez = Math.min(bl.maxzMargin - mapStartZ, width);

		for (int i = sx; i < ex; i++) {
			for (int j = sz; j < ez; j++) {
				buildingLoc[i][j] = false;
			}
		}
	}

	public boolean update(final World world,
			final List<BuildingLocation> locations,
			final BuildingLocation blIP, final Point centre, final int radius)
			throws MillenaireException {

		this.world = world;
		this.yBaseline = centre.getiY();
		locationIP = blIP;

		if (buildingLocations != null && buildingLocations.size() > 0
				&& buildingLocations.size() == locations.size()) {
			buildingLocations = locations;
			updateNextChunk();
			return false;
		}

		int startX = centre.getiX(), startZ = centre.getiZ(), endX = centre
				.getiX(), endZ = centre.getiZ();

		BuildingLocation blStartX = null, blStartZ = null, blEndX = null, blEndZ = null;

		for (final BuildingLocation location : locations) {
			if (location != null) {
				if (location.pos.getiX() - location.length / 2 < startX) {
					startX = location.pos.getiX() - location.length / 2;
					blStartX = location;
				}
				if (location.pos.getiX() + location.length / 2 > endX) {
					endX = location.pos.getiX() + location.length / 2;
					blEndX = location;
				}
				if (location.pos.getiZ() - location.width / 2 < startZ) {
					startZ = location.pos.getiZ() - location.width / 2;
					blStartZ = location;
				}
				if (location.pos.getiZ() + location.width / 2 > endZ) {
					endZ = location.pos.getiZ() + location.width / 2;
					blEndZ = location;
				}
			}
		}

		if (blIP != null) {
			if (blIP.pos.getiX() - blIP.length / 2 < startX) {
				startX = blIP.pos.getiX() - blIP.length / 2;
				blStartX = blIP;
			}
			if (blIP.pos.getiX() + blIP.length / 2 > endX) {
				endX = blIP.pos.getiX() + blIP.length / 2;
				blEndX = blIP;
			}
			if (blIP.pos.getiZ() - blIP.width / 2 < startZ) {
				startZ = blIP.pos.getiZ() - blIP.width / 2;
				blStartZ = blIP;
			}
			if (blIP.pos.getiZ() + blIP.width / 2 > endZ) {
				endZ = blIP.pos.getiZ() + blIP.width / 2;
				blEndZ = blIP;
			}
		}

		if (MLN.LogWorldInfo >= MLN.MAJOR) {

			MLN.major(this, "WorldInfo Centre: " + centre);

			if (startX - BUILDING_MARGIN < centre.getiX() - radius - MAP_MARGIN) {
				MLN.major(this,
						"Pushing startX down by "
								+ (startX - BUILDING_MARGIN - (centre.getiX()
										- radius - MAP_MARGIN)) + " due to "
								+ blStartX);
			} else {
				MLN.major(this, "Using default value of "
						+ (centre.getiX() - radius - MAP_MARGIN)
						+ " for startX");
			}

			if (startZ - BUILDING_MARGIN < centre.getiZ() - radius - MAP_MARGIN) {
				MLN.major(this,
						"Pushing startZ down by "
								+ (startZ - BUILDING_MARGIN - (centre.getiZ()
										- radius - MAP_MARGIN)) + " due to "
								+ blStartZ);
			} else {
				MLN.major(this, "Using default value of "
						+ (centre.getiZ() - radius - MAP_MARGIN)
						+ " for startZ");
			}

			if (endX + BUILDING_MARGIN > centre.getiX() + radius + MAP_MARGIN) {
				MLN.major(this,
						"Pushing endX up by "
								+ (endX + BUILDING_MARGIN - (centre.getiX()
										+ radius + MAP_MARGIN)) + " due to "
								+ blEndX);
			} else {
				MLN.major(this, "Using default value of "
						+ (centre.getiX() + radius + MAP_MARGIN) + " for endX");
			}

			if (endZ + BUILDING_MARGIN > centre.getiZ() + radius + MAP_MARGIN) {
				MLN.major(this,
						"Pushing endZ up by "
								+ (endZ + BUILDING_MARGIN - (centre.getiZ()
										+ radius + MAP_MARGIN)) + " due to "
								+ blEndZ);
			} else {
				MLN.major(this, "Using default value of "
						+ (centre.getiZ() + radius + MAP_MARGIN) + " for endZ");
			}

		}

		startX = Math.min(startX - BUILDING_MARGIN, centre.getiX() - radius
				- MAP_MARGIN);
		startZ = Math.min(startZ - BUILDING_MARGIN, centre.getiZ() - radius
				- MAP_MARGIN);
		endX = Math.max(endX + BUILDING_MARGIN, centre.getiX() + radius
				+ MAP_MARGIN);
		endZ = Math.max(endZ + BUILDING_MARGIN, centre.getiZ() + radius
				+ MAP_MARGIN);

		if (MLN.LogWorldInfo >= MLN.MAJOR) {
			MLN.major(this, "Values: " + startX + "/" + startZ + "/" + endX
					+ "/" + endZ);
		}

		final int chunkStartXTemp = startX >> 4;
		final int chunkStartZTemp = startZ >> 4;
		final int mapStartXTemp = chunkStartXTemp << 4;
		final int mapStartZTemp = chunkStartZTemp << 4;
		final int lengthTemp = ((endX >> 4) + 1 << 4) - mapStartXTemp;
		final int widthTemp = ((endZ >> 4) + 1 << 4) - mapStartZTemp;

		if (MLN.LogWorldInfo >= MLN.MAJOR) {
			MLN.major(this, "Values after chunks: " + mapStartXTemp + "/"
					+ mapStartZTemp + "/" + (mapStartXTemp + lengthTemp) + "/"
					+ (mapStartZTemp + widthTemp));
		}

		if (lengthTemp != length || widthTemp != width) {
			createWorldInfo(locations, blIP, startX, startZ, endX, endZ);
			return true;
		} else {

			buildingLocations = new ArrayList<BuildingLocation>();
			for (final BuildingLocation location : locations) {
				registerBuildingLocation(location);
			}

			updateNextChunk();
			return false;
		}
	}

	private void updateChunk(final int startX, final int startZ) {

		// We have to test not just for this chunk but the surrounding ones also
		// as we need to do some operations that involve
		// neighbouring blocks
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (!world.getChunkProvider().chunkExists(
						(startX + mapStartX >> 4) + i,
						(startZ + mapStartZ >> 4) + j)) {
					if (MLN.LogWorldInfo >= MLN.DEBUG) {
						MLN.debug(this, "Chunk is not loaded.");
					}
					return;
				}
			}
		}

		final Chunk chunk = world.getChunkFromBlockCoords(startX + mapStartX,
				startZ + mapStartZ);

		if (MLN.LogWorldInfo >= MLN.DEBUG) {
			MLN.debug(this, "Updating chunk: " + startX + "/" + startZ + "/"
					+ yBaseline + "/" + chunk.xPosition + "/" + chunk.zPosition);
		}

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				final short miny = (short) Math.max(yBaseline - 25, 1);
				final short maxy = (short) Math.min(yBaseline + 25, 255);

				final int mx = i + startX;
				final int mz = j + startZ;

				canBuild[mx][mz] = false;
				buildingForbidden[mx][mz] = false;
				water[mx][mz] = false;
				topAdjusted[mx][mz] = false;

				short y = maxy;

				Block block;

				short ceilingSize = 0;
				Block tblock = chunk.getBlock(i, y, j);

				while (y >= miny
						&& !MillCommonUtilities.isBlockIdGround(tblock)) {
					if (MillCommonUtilities.isBlockIdGroundOrCeiling(tblock)) {
						ceilingSize++;
					} else {
						ceilingSize = 0;
					}

					y--;

					if (ceilingSize > 3) {
						break;
					}

					tblock = chunk.getBlock(i, y, j);
				}

				constructionHeight[mx][mz] = y;

				boolean heightDone = false;

				if (y <= maxy && y > 1) {
					block = chunk.getBlock(i, y, j);
				} else {
					block = null;
				}

				boolean onground = true;// used to continue looking for surface
										// if starting in water
				short lastLiquid = -1;

				while (block != null
						&& (MillCommonUtilities.isBlockIdSolid(block)
								|| MillCommonUtilities.isBlockIdLiquid(block) || !onground)) {

					if (block == Blocks.log) {
						heightDone = true;
					} else if (!heightDone) {// everything solid but wood counts
						constructionHeight[mx][mz]++;
					} else {
						heightDone = true;
					}

					if (isForbiddenBlockForConstruction(block)) {
						buildingForbidden[mx][mz] = true;
					}

					if (MillCommonUtilities.isBlockIdLiquid(block)) {
						onground = false;
						lastLiquid = y;
					} else if (MillCommonUtilities.isBlockIdSolid(block)) {
						onground = true;
					}

					y++;

					if (y <= maxy && y > 1) {
						block = chunk.getBlock(i, y, j);
					} else {
						block = null;
					}
				}

				if (onground == false) {
					y = lastLiquid;
				}

				while (y <= maxy
						&& y > 1
						&& !(!MillCommonUtilities.isBlockIdSolid(chunk
								.getBlock(i, y, j)) && !MillCommonUtilities
								.isBlockIdSolid(chunk.getBlock(i, y + 1, j)))) {
					y++;
				}

				y = (byte) Math.max(1, y);

				topGround[mx][mz] = y;
				spaceAbove[mx][mz] = 0;

				final Block soilBlock = chunk.getBlock(i, y - 1, j);
				block = chunk.getBlock(i, y, j);

				if (block == Blocks.flowing_water || block == Blocks.water) {
					water[mx][mz] = true;
				}

				if (soilBlock == Blocks.log) {
					tree[mx][mz] = true;
				} else {
					tree[mx][mz] = false;
				}

				if (soilBlock == Mill.path || soilBlock == Mill.pathSlab) {
					path[mx][mz] = true;
				} else {
					path[mx][mz] = false;
				}

				boolean blocked = false;

				if (!(soilBlock == Blocks.fence)
						&& !MillCommonUtilities.isBlockIdSolid(block)
						&& block != Blocks.flowing_water
						&& soilBlock != Blocks.water) {
					spaceAbove[mx][mz] = 1;
				} else {
					blocked = true;
				}

				if (block == Blocks.flowing_lava || block == Blocks.lava) {
					if (MLN.LogWorldInfo >= MLN.DEBUG) {
						MLN.debug(this, "Found danger: " + block);
					}
					danger[mx][mz] = true;
				} else {
					danger[mx][mz] = false;
					for (final Block forbiddenBlock : MLN.forbiddenBlocks) {
						if (forbiddenBlock == block) {
							danger[mx][mz] = true;
						}
						if (soilBlock == block) {
							danger[mx][mz] = true;
						}
					}
				}

				if (!danger[mx][mz] && !buildingLoc[mx][mz]) {
					if (constructionHeight[mx][mz] > yBaseline
							- VALID_HEIGHT_DIFF
							&& constructionHeight[mx][mz] < yBaseline
									+ VALID_HEIGHT_DIFF) {
						canBuild[mx][mz] = true;
					}
				}

				if (isForbiddenBlockForConstruction(block)) {
					buildingForbidden[mx][mz] = true;
				}

				y++;

				while (y < maxy && y > 0) {
					block = chunk.getBlock(i, y, j);

					if (!blocked && spaceAbove[mx][mz] < 3
							&& !MillCommonUtilities.isBlockIdSolid(block)) {
						spaceAbove[mx][mz]++;
					} else {
						blocked = true;
					}

					if (isForbiddenBlockForConstruction(block)) {
						buildingForbidden[mx][mz] = true;
					}

					y++;
				}

				if (buildingForbidden[mx][mz]) {
					canBuild[mx][mz] = false;
				}
			}
		}

		/*
		 * New method: attempt to "bridge" gaps in topground (especially
		 * doorways)
		 * 
		 * First, gaps one block large, possibly with difference in level up to
		 * 2
		 */

		boolean gapFilled = true;

		while (gapFilled) {
			gapFilled = false;
			for (int i = -5; i < 21; i++) {
				for (int j = -5; j < 21; j++) {
					final int mx = i + startX;
					final int mz = j + startZ;

					if (mz >= 0 && mz < width) {
						if (mx > 1 && mx < length - 1) {
							if (Math.abs(topGround[mx - 1][mz]
									- topGround[mx + 1][mz]) < 2
									&& (topGround[mx - 1][mz] + 2 < topGround[mx][mz] || topGround[mx + 1][mz] + 2 < topGround[mx][mz])) {

								final short ntg = topGround[mx - 1][mz];
								final boolean samesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg, startZ
												+ mapStartZ + j));
								final boolean belowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 1,
												startZ + mapStartZ + j));
								final boolean below2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 2,
												startZ + mapStartZ + j));
								final boolean abovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 1,
												startZ + mapStartZ + j));
								final boolean above2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 2,
												startZ + mapStartZ + j));
								final boolean above3solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 3,
												startZ + mapStartZ + j));

								// check if same level works
								if (Math.abs(topGround[mx - 1][mz]
										- topGround[mx + 1][mz]) < 2
										&& belowsolid
										&& !samesolid
										&& !abovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Vert 1 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = ntg;
									if (!above2solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} else if (topGround[mx + 1][mz] <= topGround[mx - 1][mz]
										&& below2solid
										&& !belowsolid
										&& !samesolid && !abovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Vert 1 space down: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = (short) (ntg - 1);
									if (!abovesolid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} else if (topGround[mx + 1][mz] >= topGround[mx - 1][mz]
										&& samesolid
										&& !abovesolid
										&& !above2solid) {
									// MLN.temp(this,
									// i+"/"+j+" Vert 1 space up: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = (short) (ntg + 1);
									if (!above3solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								}
							}
						}
					}
					if (mx >= 0 && mx < length) {
						if (mz > 1 && mz < width - 1) {
							if (Math.abs(topGround[mx][mz - 1]
									- topGround[mx][mz + 1]) < 3
									&& (topGround[mx][mz - 1] + 2 < topGround[mx][mz] || topGround[mx][mz + 1] + 2 < topGround[mx][mz])) {

								final short ntg = topGround[mx][mz - 1];
								final boolean samesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg, startZ
												+ mapStartZ + j));
								final boolean belowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 1,
												startZ + mapStartZ + j));
								final boolean below2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 2,
												startZ + mapStartZ + j));
								final boolean abovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 1,
												startZ + mapStartZ + j));
								final boolean above2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 2,
												startZ + mapStartZ + j));
								final boolean above3solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 3,
												startZ + mapStartZ + j));

								// check if same level works
								if (Math.abs(topGround[mx][mz - 1]
										- topGround[mx][mz + 1]) < 2
										&& belowsolid
										&& !samesolid
										&& !abovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Hor 1 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = ntg;
									if (!above2solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} else if (topGround[mx][mz + 1] <= topGround[mx][mz - 1]
										&& below2solid
										&& !belowsolid
										&& !samesolid && !abovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Hor 1 space down: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = (short) (ntg - 1);
									if (!abovesolid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} else if (topGround[mx][mz + 1] >= topGround[mx][mz - 1]
										&& samesolid
										&& !abovesolid
										&& !above2solid) {
									// MLN.temp(this,
									// i+"/"+j+" Hor 1 space up: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = (short) (ntg + 1);
									if (!above3solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								}
							}
						}
					}
				}
			}

			/*
			 * Then, gaps two blocks large, on the same level (for instance,
			 * passage between a double-size wall)
			 */
			for (int i = -5; i < 21; i++) {
				for (int j = -5; j < 21; j++) {
					final int mx = i + startX;
					final int mz = j + startZ;

					if (mz >= 0 && mz < width) {
						if (mx > 1 && mx < length - 2) {
							if (topGround[mx - 1][mz] == topGround[mx + 2][mz]
									&& topGround[mx - 1][mz] < topGround[mx][mz]
									&& topGround[mx - 1][mz] < topGround[mx + 1][mz]) {

								final short ntg = topGround[mx - 1][mz];
								final boolean samesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg, startZ
												+ mapStartZ + j));
								final boolean belowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 1,
												startZ + mapStartZ + j));
								final boolean abovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 1,
												startZ + mapStartZ + j));
								final boolean above2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 2,
												startZ + mapStartZ + j));

								// using the world obj because we might be
								// beyond the chunk
								final boolean nextsamesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i + 1, ntg,
												startZ + mapStartZ + j));
								final boolean nextbelowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i + 1, ntg - 1,
												startZ + mapStartZ + j));
								final boolean nextabovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i + 1, ntg + 1,
												startZ + mapStartZ + j));
								final boolean nextabove2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i + 1, ntg + 2,
												startZ + mapStartZ + j));

								// check if same level works
								if (belowsolid && nextbelowsolid && !samesolid
										&& !nextsamesolid && !abovesolid
										&& !nextabovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Vert 2 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = ntg;
									topGround[mx + 1][mz] = ntg;
									if (!above2solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}

									if (!nextabove2solid) {
										spaceAbove[mx + 1][mz] = 3;
									} else {
										spaceAbove[mx + 1][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								}
							}
						}
					}
					if (mx >= 0 && mx < length) {
						if (mz > 1 && mz < width - 2) {
							if (topGround[mx][mz - 1] == topGround[mx][mz + 2]
									&& topGround[mx][mz - 1] < topGround[mx][mz]
									&& topGround[mx][mz - 1] < topGround[mx][mz + 1]) {

								final short ntg = topGround[mx][mz - 1];
								final boolean samesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg, startZ
												+ mapStartZ + j));
								final boolean belowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 1,
												startZ + mapStartZ + j));
								final boolean abovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 1,
												startZ + mapStartZ + j));
								final boolean above2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 2,
												startZ + mapStartZ + j));

								// using the world obj because we might be
								// beyond the chunk
								final boolean nextsamesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg, startZ
												+ mapStartZ + j + 1));
								final boolean nextbelowsolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg - 1,
												startZ + mapStartZ + j + 1));
								final boolean nextabovesolid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 1,
												startZ + mapStartZ + j + 1));
								final boolean nextabove2solid = MillCommonUtilities
										.isBlockIdSolid(world.getBlock(startX
												+ mapStartX + i, ntg + 2,
												startZ + mapStartZ + j + 1));

								// check if same level works
								if (belowsolid && nextbelowsolid && !samesolid
										&& !nextsamesolid && !abovesolid
										&& !nextabovesolid) {
									// MLN.temp(this,
									// i+"/"+j+" Hor 2 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz] = ntg;
									topGround[mx][mz + 1] = ntg;
									if (!above2solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}

									if (!nextabove2solid) {
										spaceAbove[mx][mz + 1] = 3;
									} else {
										spaceAbove[mx][mz + 1] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {

				final int mx = i + startX;
				final int mz = j + startZ;

				if (danger[mx][mz]) {
					for (int k = -2; k < 3; k++) {
						for (int l = -2; l < 3; l++) {
							if (k >= 0 && l >= 0 && k < length && l < width) {
								spaceAbove[mx][mz] = 0;
							}
						}
					}
				}
			}
		}
	}

	public void updateNextChunk() {

		updateCounter = (updateCounter + 1) % frequency;

		if (updateCounter != 0) {
			return;
		}

		lastUpdatedX++;
		if (lastUpdatedX * 16 >= length) {
			lastUpdatedX = 0;
			lastUpdatedZ++;
		}

		if (lastUpdatedZ * 16 >= width) {
			lastUpdatedZ = 0;
		}

		final UpdateThread thread = new UpdateThread();
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.x = lastUpdatedX << 4;
		thread.z = lastUpdatedZ << 4;

		thread.start();
	}
}
