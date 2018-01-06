package org.millenaire;

import java.util.ArrayList;
import java.util.List;

import org.millenaire.blocks.MillBlocks;
import org.millenaire.building.BuildingLocation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class VillageGeography 
{
	private static final int MAP_MARGIN = 10;
	private static final int BUILDING_MARGIN = 5;
	private static final int VALIDHEIGHTDIFF = 10;

	public int length = 0;
	public int width = 0;
	private int chunkStartX = 0, chunkStartZ = 0;
	public int mapStartX = 0, mapStartZ = 0;
	private int yBaseline = 0;

	private short[][] topGround;
	public short[][] constructionHeight;
	private short[][] spaceAbove;
	
	public boolean[][] danger;
	public boolean[][] buildingForbidden;
	public boolean[][] canBuild;

	public boolean[][] buildingLoc;

	private boolean[][] water;
	private boolean[][] tree;
	
	public boolean[][] buildTested = null;

	private boolean[][] topAdjusted;

	public boolean[][] path;

	private int frequency = 10;
	private List<BuildingLocation> buildingLocations = new ArrayList<BuildingLocation>();
	private BuildingLocation locationIP;

	public int nbLoc = 0;

	public World world;

	private int lastUpdatedX, lastUpdatedZ;

	private int updateCounter;
	
	public VillageGeography()
	{
		
	}
	
	private void createWorldInfo(final List<BuildingLocation> locations, final BuildingLocation blIP, final int pstartX, final int pstartZ, final int endX, final int endZ)
	{
		chunkStartX = pstartX >> 4;
		chunkStartZ = pstartZ >> 4;
		mapStartX = chunkStartX << 4;
		mapStartZ = chunkStartZ << 4;

		length = ((endX >> 4) + 1 << 4) - mapStartX;
		width = ((endZ >> 4) + 1 << 4) - mapStartZ;

		frequency = (int) Math.max(1000 * 1.0 / (length * width / 256), 10);

		if (frequency == 0) 
		{
			System.err.println("Null frequency in createWorldInfo.");
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

		buildingLocations = new ArrayList<>();

		for (int i = 0; i < length; i++) 
		{
			for (int j = 0; j < width; j++) 
			{
				buildingLoc[i][j] = false;
				canBuild[i][j] = false;
			}
		}

		for (final BuildingLocation location : locations) 
		{
			registerBuildingLocation(location);
		}

		locationIP = blIP;
		if (locationIP != null) 
		{
			registerBuildingLocation(locationIP);
		}

		for (int i = 0; i < length; i += 16) 
		{
			for (int j = 0; j < width; j += 16) 
			{
				updateChunk(i, j);
			}
		}
		lastUpdatedX = 0;
		lastUpdatedZ = 0;
	}

	private static boolean isForbiddenBlockForConstruction(final Block block)
	{
		return block == Blocks.water || block == Blocks.flowing_water || block == Blocks.ice || block == Blocks.flowing_lava || block == Blocks.lava || block == Blocks.planks || block == Blocks.cobblestone || block == Blocks.brick_block || block == Blocks.chest || block == Blocks.glass || block == Blocks.stonebrick || block == Blocks.prismarine
				|| block instanceof BlockWall || block instanceof BlockFence || block == MillBlocks.blockDecorativeEarth || block == MillBlocks.blockDecorativeStone || block == MillBlocks.blockDecorativeWood || block == MillBlocks.byzantineTile || block == MillBlocks.byzantineTileSlab || block == MillBlocks.byzantineStoneTile || block == MillBlocks.paperWall || block == MillBlocks.emptySericulture;
	}
	
	private void registerBuildingLocation(final BuildingLocation bl) 
	{
		buildingLocations.add(bl);

		final int sx = Math.max(bl.minxMargin - mapStartX, 0);
		final int sz = Math.max(bl.minzMargin - mapStartZ, 0);
		final int ex = Math.min(bl.maxxMargin - mapStartX, length + 1);
		final int ez = Math.min(bl.maxzMargin - mapStartZ, width + 1);

		for (int i = sx; i < ex; i++) 
		{
			for (int j = sz; j < ez; j++) 
			{
				buildingLoc[i][j] = true;
			}
		}
	}
	
	public boolean update(final World world, final List<BuildingLocation> locations, final BuildingLocation blIP, final BlockPos center, final int radius)
	{
		this.world = world;
		this.yBaseline = center.getY();
		locationIP = blIP;

		if (buildingLocations != null && buildingLocations.size() > 0 && buildingLocations.size() == locations.size()) 
		{
			buildingLocations = locations;
			updateNextChunk();
			return false;
		}

		int startX = center.getX(), startZ = center.getZ(), endX = center.getX(), endZ = center.getZ();

		for (final BuildingLocation location : locations) 
		{
			if (location != null) 
			{
				if (location.position.getX() - location.length / 2 < startX) 
				{
					startX = location.position.getX() - location.length / 2;
				}
				if (location.position.getX() + location.length / 2 > endX) 
				{
					endX = location.position.getX() + location.length / 2;
				}
				if (location.position.getZ() - location.width / 2 < startZ) 
				{
					startZ = location.position.getZ() - location.width / 2;
				}
				if (location.position.getZ() + location.width / 2 > endZ) 
				{
					endZ = location.position.getZ() + location.width / 2;
				}
			}
		}

		if (blIP != null) 
		{
			if (blIP.position.getX() - blIP.length / 2 < startX) 
			{
				startX = blIP.position.getX() - blIP.length / 2;
			}
			if (blIP.position.getX() + blIP.length / 2 > endX) 
			{
				endX = blIP.position.getX() + blIP.length / 2;
			}
			if (blIP.position.getZ() - blIP.width / 2 < startZ) 
			{
				startZ = blIP.position.getZ() - blIP.width / 2;
			}
			if (blIP.position.getZ() + blIP.width / 2 > endZ) 
			{
				endZ = blIP.position.getZ() + blIP.width / 2;
			}
		}

		startX = Math.min(startX - BUILDING_MARGIN, center.getX() - radius - MAP_MARGIN);
		startZ = Math.min(startZ - BUILDING_MARGIN, center.getZ() - radius - MAP_MARGIN);
		endX = Math.max(endX + BUILDING_MARGIN, center.getX() + radius + MAP_MARGIN);
		endZ = Math.max(endZ + BUILDING_MARGIN, center.getZ() + radius + MAP_MARGIN);

		final int chunkStartXTemp = startX >> 4;
		final int chunkStartZTemp = startZ >> 4;
		final int mapStartXTemp = chunkStartXTemp << 4;
		final int mapStartZTemp = chunkStartZTemp << 4;
		final int lengthTemp = ((endX >> 4) + 1 << 4) - mapStartXTemp;
		final int widthTemp = ((endZ >> 4) + 1 << 4) - mapStartZTemp;

		if (lengthTemp != length || widthTemp != width) 
		{
			createWorldInfo(locations, blIP, startX, startZ, endX, endZ);
			return true;
		} 
		else 
		{
			buildingLocations = new ArrayList<>();
			for (final BuildingLocation location : locations) 
			{
				registerBuildingLocation(location);
			}

			updateNextChunk();
			return false;
		}
	}
	
	private void updateChunk(final int startX, final int startZ) 
	{
		// We have to test not just for this chunk but the surrounding ones also
		// as we need to do some operations that involve
		// neighbouring blocks
		for (int i = -1; i < 2; i++) 
		{
			for (int j = -1; j < 2; j++) 
			{
				if (!world.getChunkProvider().chunkExists((startX + mapStartX >> 4) + i, (startZ + mapStartZ >> 4) + j))
				{
					world.getChunkProvider().provideChunk((startX + mapStartX >> 4) + i, (startZ + mapStartZ >> 4) + j);
				}
			}
		}

		final Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(startX + mapStartX, yBaseline ,startZ + mapStartZ));

		for (int i = 0; i < 16; i++) 
		{
			for (int j = 0; j < 16; j++) 
			{
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

				while (y >= miny && !isBlockIdGround(tblock)) 
				{
					if (isBlockIdGroundOrCeiling(tblock))
					{
						ceilingSize++;
					}
					else
					{
						ceilingSize = 0;
					}

					y--;

					if (ceilingSize > 3) 
					{
						break;
					}

					tblock = chunk.getBlock(i, y, j);
				}

				constructionHeight[mx][mz] = y;

				boolean heightDone = false;

				if (y <= maxy && y > 1) 
				{
					block = chunk.getBlock(i, y, j);
				}
				else
				{
					block = null;
				}
				//System.out.println("y is " + constructionHeight[mx][mz]);

				boolean onground = true;// used to continue looking for surface
										// if starting in water
				short lastLiquid = -1;

				while (block != null && (isBlockSolid(block) || block instanceof BlockLiquid || !onground)) 
				{
					if (block == Blocks.log) 
					{
						heightDone = true;
					} 
					else if (!heightDone) // everything solid but wood counts
					{
						constructionHeight[mx][mz]++;
					} 
					else 
					{
						heightDone = true;
					}

					if (isForbiddenBlockForConstruction(block)) 
					{
						buildingForbidden[mx][mz] = true;
					}

					if (block instanceof BlockLiquid) 
					{
						onground = false;
						lastLiquid = y;
					} 
					else if (isBlockSolid(block)) 
					{
						onground = true;
					}

					y++;

					if (y <= maxy && y > 1) 
					{
						block = chunk.getBlock(i, y, j);
					} 
					else 
					{
						block = null;
					}
				}
				//System.out.println("constHeight is now at " + constructionHeight[mx][mz]);

				if (!onground)
				{
					y = lastLiquid;
				}

				while (y <= maxy && y > 1 && !(!isBlockSolid(chunk.getBlock(i, y, j)) && !isBlockSolid(chunk.getBlock(i, y + 1, j)))) 
				{
					y++;
				}

				y = (byte) Math.max(1, y);

				topGround[mx][mz] = y;
				spaceAbove[mx][mz] = 0;

				final Block soilBlock = chunk.getBlock(i, y - 1, j);
				block = chunk.getBlock(i, y, j);

                water[mx][mz] = (block == Blocks.flowing_water || block == Blocks.water);

				tree[mx][mz] = (soilBlock == Blocks.log);

				path[mx][mz] = (soilBlock == MillBlocks.blockMillPath || soilBlock == MillBlocks.blockMillPathSlab || soilBlock == MillBlocks.blockMillPathSlabDouble);

				boolean blocked = false;

				if (!(soilBlock instanceof BlockFence) && !(soilBlock instanceof BlockWall) && !isBlockSolid(block) && block != Blocks.flowing_water && soilBlock != Blocks.water) 
				{
					spaceAbove[mx][mz] = 1;
				} 
				else 
				{
					blocked = true;
				}

				if (block == Blocks.flowing_lava || block == Blocks.lava) 
				{
					danger[mx][mz] = true;
				} 
				else 
				{
					danger[mx][mz] = false;
					for (final Block forbiddenBlock : Millenaire.instance.forbiddenBlocks) 
					{
                        danger[mx][mz] = (forbiddenBlock == block);
                        danger[mx][mz] = (soilBlock == block);
					}
				}

				if (!danger[mx][mz] && !buildingLoc[mx][mz]) 
				{
					if (constructionHeight[mx][mz] > yBaseline - VALIDHEIGHTDIFF && constructionHeight[mx][mz] < yBaseline + VALIDHEIGHTDIFF) 
					{
						canBuild[mx][mz] = true;
					}
				}

				buildingForbidden[mx][mz] = isForbiddenBlockForConstruction(block);

				y++;

				while (y < maxy && y > 0) 
				{
					block = chunk.getBlock(i, y, j);

					if (!blocked && spaceAbove[mx][mz] < 3 && !isBlockSolid(block)) 
					{
						spaceAbove[mx][mz]++;
					} 
					else 
					{
						blocked = true;
					}

                    buildingForbidden[mx][mz] = (isForbiddenBlockForConstruction(block));

					y++;
				}

                canBuild[mx][mz] = !(buildingForbidden[mx][mz]);
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

		while (gapFilled) 
		{
			gapFilled = false;
			for (int i = -5; i < 21; i++) 
			{
				for (int j = -5; j < 21; j++) 
				{
					final int mx = i + startX;
					final int mz = j + startZ;

					if (mz >= 0 && mz < width) 
					{
						if (mx > 1 && mx < length - 1) 
						{
							if (Math.abs(topGround[mx - 1][mz] - topGround[mx + 1][mz]) < 2 && (topGround[mx - 1][mz] + 2 < topGround[mx][mz] || topGround[mx + 1][mz] + 2 < topGround[mx][mz])) 
							{
								final short ntg = topGround[mx - 1][mz];
								final boolean samesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg, startZ + mapStartZ + j)).getBlock());
								final boolean belowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 1, startZ + mapStartZ + j)).getBlock());
								final boolean below2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 2, startZ + mapStartZ + j)).getBlock());
								final boolean abovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 1, startZ + mapStartZ + j)).getBlock());
								final boolean above2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 2, startZ + mapStartZ + j)).getBlock());
								final boolean above3solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 3, startZ + mapStartZ + j)).getBlock());

								// check if same level works
								if (Math.abs(topGround[mx - 1][mz] - topGround[mx + 1][mz]) < 2 && belowsolid && !samesolid && !abovesolid) 
								{
									topGround[mx][mz] = ntg;
									if (!above2solid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} 
								else if (topGround[mx + 1][mz] <= topGround[mx - 1][mz] && below2solid && !belowsolid && !samesolid && !abovesolid) 
								{
									topGround[mx][mz] = (short) (ntg - 1);
									if (!abovesolid) {
										spaceAbove[mx][mz] = 3;
									} else {
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} else if (topGround[mx + 1][mz] >= topGround[mx - 1][mz] && samesolid && !abovesolid && !above2solid) 
								{
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
					if (mx >= 0 && mx < length) 
					{
						if (mz > 1 && mz < width - 1) 
						{
							if (Math.abs(topGround[mx][mz - 1] - topGround[mx][mz + 1]) < 3 && (topGround[mx][mz - 1] + 2 < topGround[mx][mz] || topGround[mx][mz + 1] + 2 < topGround[mx][mz])) 
							{
								final short ntg = topGround[mx][mz - 1];
								final boolean samesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg, startZ + mapStartZ + j)).getBlock());
								final boolean belowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 1, startZ + mapStartZ + j)).getBlock());
								final boolean below2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 2, startZ + mapStartZ + j)).getBlock());
								final boolean abovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 1, startZ + mapStartZ + j)).getBlock());
								final boolean above2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 2, startZ + mapStartZ + j)).getBlock());
								final boolean above3solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 3, startZ + mapStartZ + j)).getBlock());

								// check if same level works
								if (Math.abs(topGround[mx][mz - 1] - topGround[mx][mz + 1]) < 2 && belowsolid && !samesolid && !abovesolid) 
								{
									topGround[mx][mz] = ntg;
									if (!above2solid)
									{
										spaceAbove[mx][mz] = 3;
									} 
									else 
									{
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} 
								else if (topGround[mx][mz + 1] <= topGround[mx][mz - 1] && below2solid && !belowsolid && !samesolid && !abovesolid) 
								{
									topGround[mx][mz] = (short) (ntg - 1);
									if (!abovesolid) 
									{
										spaceAbove[mx][mz] = 3;
									} 
									else 
									{
										spaceAbove[mx][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								} 
								else if (topGround[mx][mz + 1] >= topGround[mx][mz - 1] && samesolid && !abovesolid && !above2solid) 
								{
									topGround[mx][mz] = (short) (ntg + 1);
									if (!above3solid) 
									{
										spaceAbove[mx][mz] = 3;
									} 
									else 
									{
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
			for (int i = -5; i < 21; i++) 
			{
				for (int j = -5; j < 21; j++) 
				{
					final int mx = i + startX;
					final int mz = j + startZ;

					if (mz >= 0 && mz < width) {
						if (mx > 1 && mx < length - 2) {
							if (topGround[mx - 1][mz] == topGround[mx + 2][mz] && topGround[mx - 1][mz] < topGround[mx][mz] && topGround[mx - 1][mz] < topGround[mx + 1][mz]) 
							{
								final short ntg = topGround[mx - 1][mz];
								final boolean samesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg, startZ + mapStartZ + j)).getBlock());
								final boolean belowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 1, startZ + mapStartZ + j)).getBlock());
								final boolean abovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 1, startZ + mapStartZ + j)).getBlock());
								final boolean above2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 2, startZ + mapStartZ + j)).getBlock());

								// using the world obj because we might be
								// beyond the chunk
								final boolean nextsamesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i + 1, ntg, startZ + mapStartZ + j)).getBlock());
								final boolean nextbelowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i + 1, ntg - 1, startZ + mapStartZ + j)).getBlock());
								final boolean nextabovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i + 1, ntg + 1, startZ + mapStartZ + j)).getBlock());
								final boolean nextabove2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i + 1, ntg + 2, startZ + mapStartZ + j)).getBlock());

								// check if same level works
								if (belowsolid && nextbelowsolid && !samesolid && !nextsamesolid && !abovesolid && !nextabovesolid) 
								{
									topGround[mx][mz] = ntg;
									topGround[mx + 1][mz] = ntg;
									if (!above2solid) 
									{
										spaceAbove[mx][mz] = 3;
									} 
									else 
									{
										spaceAbove[mx][mz] = 2;
									}

									if (!nextabove2solid) 
									{
										spaceAbove[mx + 1][mz] = 3;
									} 
									else 
									{
										spaceAbove[mx + 1][mz] = 2;
									}
									gapFilled = true;
									topAdjusted[mx][mz] = true;
								}
							}
						}
					}
					if (mx >= 0 && mx < length) 
					{
						if (mz > 1 && mz < width - 2) 
						{
							if (topGround[mx][mz - 1] == topGround[mx][mz + 2] && topGround[mx][mz - 1] < topGround[mx][mz] && topGround[mx][mz - 1] < topGround[mx][mz + 1]) 
							{
								final short ntg = topGround[mx][mz - 1];
								final boolean samesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg, startZ + mapStartZ + j)).getBlock());
								final boolean belowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 1, startZ + mapStartZ + j)).getBlock());
								final boolean abovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 1, startZ + mapStartZ + j)).getBlock());
								final boolean above2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 2, startZ + mapStartZ + j)).getBlock());

								// using the world obj because we might be
								// beyond the chunk
								final boolean nextsamesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg, startZ + mapStartZ + j + 1)).getBlock());
								final boolean nextbelowsolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg - 1, startZ + mapStartZ + j + 1)).getBlock());
								final boolean nextabovesolid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 1, startZ + mapStartZ + j + 1)).getBlock());
								final boolean nextabove2solid = isBlockSolid(world.getBlockState(new BlockPos(startX + mapStartX + i, ntg + 2, startZ + mapStartZ + j + 1)).getBlock());

								// check if same level works
								if (belowsolid && nextbelowsolid && !samesolid && !nextsamesolid && !abovesolid && !nextabovesolid) {
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
	
	private void updateNextChunk()
	{

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

    private static boolean isBlockIdGround(final Block b)
	{
        return (b == Blocks.bedrock || b == Blocks.clay || b == Blocks.dirt ||
                b == Blocks.grass || b == Blocks.gravel || b == Blocks.obsidian ||
                b == Blocks.sand || b == Blocks.farmland);
	}

    private static boolean isBlockIdGroundOrCeiling(final Block b)
	{
		return (b == Blocks.stone || b == Blocks.sandstone);
	}
	
	private static boolean isBlockSolid(Block block)
	{
		return block.isFullCube() || block == Blocks.glass || block == Blocks.glass_pane || block instanceof BlockSlab || block instanceof BlockStairs || block instanceof BlockFence || block instanceof BlockWall || block == MillBlocks.paperWall;
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public class UpdateThread extends Thread 
	{
		int x;
		int z;

		@Override
		public void run() { updateChunk(x, z); }
	}
}
