package org.millenaire.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.millenaire.CommonUtilities;
import org.millenaire.MillCulture;
import org.millenaire.VillageGeography;
import org.millenaire.blocks.BlockDecorativeEarth;
import org.millenaire.blocks.BlockDecorativeStone;
import org.millenaire.blocks.BlockDecorativeWood;
import org.millenaire.blocks.BlockMillChest;
import org.millenaire.blocks.MillBlocks;
import org.millenaire.blocks.StoredPosition;
import org.millenaire.pathing.MillPathNavigate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BuildingPlan 
{
	public int length;
	public int width;
	public int height;
	public int lengthOffset;
	public int widthOffset;
	public int depth;
	public int areaToClear;
	
	public float minDistance, maxDistance;
	
	public boolean isUpdate = false;
	public boolean isSubBuilding = false;
	
	public List<ResourceCost> resCost;
	public EnumFacing buildingOrientation;
	public String nativeName;
	public String[] maleVillagerType;
	public String[] femaleVillagerType;
	
	IBlockState[][][] buildingArray;
	public List<String> subBuildings;
	public int pathLevel = 0;
	public int pathWidth = 2;
	public boolean rebuildPath = false;

	public BuildingPlan(MillCulture cultureIn, int level)
	{
		
		//computeCost();
	}
	
	public BuildingPlan(int level, int pathLevelIn, BuildingPlan parent)
	{
		length = parent.length;
		width = parent.width;
		height = parent.height;
		lengthOffset = parent.lengthOffset;
		widthOffset = parent.widthOffset;
		depth = parent.depth;
		areaToClear = parent.areaToClear;
		minDistance = parent.minDistance;
		maxDistance = parent.maxDistance;
		isUpdate = true;
		isSubBuilding = parent.isSubBuilding;
		buildingOrientation = parent.buildingOrientation;
		nativeName = parent.nativeName;
		maleVillagerType = parent.maleVillagerType;
		femaleVillagerType = parent.femaleVillagerType;
		pathWidth = parent.pathWidth;
		
		pathLevel = pathLevelIn;
		if(pathLevel != parent.pathLevel)
			rebuildPath = true;
		
		//computeCost();
	}

	public BuildingPlan setLengthWidth(int lenIn, int widIn)
	{
		this.length = lenIn;
		this.width = widIn;
		
		lengthOffset = (int) Math.floor(length * 0.5);
		widthOffset = (int) Math.floor(width * 0.5);
		
		return this;
	}
	
	public BuildingPlan setHeightDepth(int hiIn, int depIn)
	{
		this.height = hiIn;
		this.depth = depIn;
		
		return this;
	}

	public BuildingPlan setArea(int areaIn)
	{
		this.areaToClear = areaIn;
		
		return this;
	}
	
	public BuildingPlan setDistance(float minIn, float maxIn)
	{
		this.minDistance = minIn;
		this.maxDistance = maxIn;
		
		return this;
	}
	
	public BuildingPlan setSubBuilding(boolean subIn)
	{
		this.isSubBuilding = subIn;
		
		return this;
	}
	
	public BuildingPlan setOrientation(EnumFacing orientIn)
	{
		this.buildingOrientation = orientIn;
		
		return this;
	}

	public BuildingPlan setNameAndType(String nameIn, String[] maleIn, String[] femaleIn)
	{
		this.nativeName = nameIn;
		this.maleVillagerType = maleIn;
		this.femaleVillagerType = femaleIn;
		
		return this;
	}
	
	public BuildingPlan setPlan(IBlockState[][][] arrayIn)
	{
		this.buildingArray = arrayIn;
		computeCost();
		
		return this;
	}
	
	public BuildingLocation findBuildingLocation(VillageGeography vg, MillPathNavigate pathing, BlockPos center, int maxRadius, Random random, EnumFacing orientation)
	{
		final int ci = center.getX() - vg.mapStartX;
		final int cj = center.getZ() - vg.mapStartZ;

		int radius = (int) (maxRadius * minDistance);
		maxRadius = (int) (maxRadius * maxDistance);
		
		for (int i = 0; i < vg.length; i++) 
		{
			for (int j = 0; j < vg.width; j++) 
			{
				vg.buildTested[i][j] = false;
			}
		}

		while (radius < maxRadius) 
		{
			final int mini = Math.max(0, ci - radius);
			final int maxi = Math.min(vg.length - 1, ci + radius);
			final int minj = Math.max(0, cj - radius);
			final int maxj = Math.min(vg.width - 1, cj + radius);
			
			for (int i = mini; i < maxi; i++) 
			{
				if (cj - radius == minj) 
				{
					final LocationReturn lr = testLocation(vg, center, i, minj, orientation, pathing);

					if (lr.location != null) 
						return lr.location;
				}
				if (cj + radius == maxj) 
				{
					final LocationReturn lr = testLocation(vg, center, i, minj, orientation, pathing);

					if (lr.location != null) 
						return lr.location;
				}
			}
			
			for (int j = minj; j < maxj; j++) 
			{
				if (ci - radius == mini) 
				{
					final LocationReturn lr = testLocation(vg, center, j, minj, orientation, pathing);

					if (lr.location != null) 
						return lr.location;
				}
				if (ci + radius == maxi) 
				{
					final LocationReturn lr = testLocation(vg, center, j, minj, orientation, pathing);

					if (lr.location != null) 
						return lr.location;
				}
			}
			
			radius++;
		}
		
		System.out.println("building search unsuccessful");
		return null;
	}
	
	private void addToCost(ItemStack stack, int amount)
	{
		for(int i = 0; i > resCost.size(); i++)
		{
			if(ItemStack.areItemStacksEqual(stack, resCost.get(i).getStack()))
			{
				resCost.get(i).add(amount);
			}
			else
			{
				resCost.add(new ResourceCost(stack, amount));
			}
		}
	}
	
	private void addToCost(String odString, int amount)
	{
		for(int i = 0; i > resCost.size(); i++)
		{
			if(odString.matches(resCost.get(i).getString()))
			{
				resCost.get(i).add(amount);
			}
			else
			{
				resCost.add(new ResourceCost(odString, amount));
			}
		}
	}
	
	private boolean freeBuild(IBlockState state)
	{
		return state.getBlock() == Blocks.dirt || state.getBlock() == Blocks.water || state.getBlock() == Blocks.leaves || state.getBlock() == Blocks.leaves2 || state.getBlock() == Blocks.grass || state.getBlock() == Blocks.tallgrass || state.getBlock() == Blocks.red_flower || state.getBlock() == Blocks.yellow_flower || state.getBlock() == Blocks.double_plant || state.getBlock() == Blocks.deadbush
				|| state.getBlock() == MillBlocks.blockMillPath || state.getBlock() == MillBlocks.blockMillPathSlab || state.equals(MillBlocks.blockDecorativeEarth.getDefaultState().withProperty(BlockDecorativeEarth.VARIANT, BlockDecorativeEarth.EnumType.DIRTWALL));
	}
	
	private void computeCost()
	{
		resCost = new ArrayList<ResourceCost>();
		
		int plankCost = 0, plankOakCost = 0, plankSpruceCost = 0, plankBirchCost = 0, plankJungleCost = 0, plankAcaciaCost = 0, plankDarkCost = 0, glassPaneCost = 0, byzBricksHalf = 0;
		
		for (int i = 0; i < height; i++) 
		{
			for (int j = 0; j < length; j++) 
			{
				for (int k = 0; k < width; k++) 
				{
					final IBlockState state = buildingArray[i][j][k];

					if (state == null) 
					{
						System.err.println("BlockState is null at " + i + "/" + j + "/" + k);
					}

					if (state.getBlock() == Blocks.log && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.OAK)
						plankOakCost += 4;
					else if (state.getBlock() == Blocks.log && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.SPRUCE)
						plankSpruceCost += 4;
					else if (state.getBlock() == Blocks.log && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.BIRCH)
						plankBirchCost += 4;
					else if (state.getBlock() == Blocks.log && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE)
						plankJungleCost += 4;
					else if (state.getBlock() == Blocks.log && state.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.ACACIA)
						plankAcaciaCost += 4;
					else if (state.getBlock() == Blocks.log && state.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
						plankJungleCost += 4;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.OAK)
						plankOakCost++;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.SPRUCE)
						plankSpruceCost++;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.BIRCH)
						plankBirchCost++;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE)
						plankJungleCost++;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.ACACIA)
						plankAcaciaCost++;
					else if (state.getBlock() == Blocks.planks && state.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
						plankDarkCost++;
					else if (state.getBlock() == MillBlocks.byzantineTile)
						byzBricksHalf += 2;
					else if (state.getBlock() == MillBlocks.byzantineTileSlab)
						byzBricksHalf++;
					else if (state.getBlock() == MillBlocks.byzantineStoneTile) 
					{
						byzBricksHalf++;
						addToCost(new ItemStack(Blocks.stone), 1);
					}
					else if (state.getBlock() == Blocks.glass_pane || state.getBlock() == Blocks.stained_glass_pane)
						glassPaneCost++;
					else if (state.getBlock() == Blocks.glass || state.getBlock() == Blocks.stained_glass)
						glassPaneCost += 3;
					else if (state.getBlock() == Blocks.crafting_table)
						plankCost += 4;
					else if (state.getBlock() == Blocks.chest)
						plankCost += 8;
					else if (state.getBlock() == Blocks.furnace)
						addToCost(new ItemStack(Blocks.cobblestone), 8);
					else if (state.getBlock() == Blocks.torch)
						plankCost++;
					else if (state.getBlock() == Blocks.oak_fence)
						plankOakCost++;
					else if (state.getBlock() == Blocks.spruce_fence)
						plankSpruceCost++;
					else if (state.getBlock() == Blocks.birch_fence)
						plankBirchCost++;
					else if (state.getBlock() == Blocks.jungle_fence)
						plankJungleCost++;
					else if (state.getBlock() == Blocks.acacia_fence)
						plankAcaciaCost++;
					else if (state.getBlock() == Blocks.dark_oak_fence)
						plankDarkCost++;
					else if (state.getBlock() == Blocks.oak_fence_gate)
						plankOakCost += 4;
					else if (state.getBlock() == Blocks.spruce_fence_gate)
						plankSpruceCost += 4;
					else if (state.getBlock() == Blocks.birch_fence_gate)
						plankBirchCost += 4;
					else if (state.getBlock() == Blocks.jungle_fence_gate)
						plankJungleCost += 4;
					else if (state.getBlock() == Blocks.acacia_fence_gate)
						plankAcaciaCost += 4;
					else if (state.getBlock() == Blocks.dark_oak_fence_gate)
						plankDarkCost += 4;
					else if (state.getBlock() == Blocks.wooden_pressure_plate)
						plankCost += 2;
					else if (state.getBlock() == Blocks.stone_pressure_plate)
						addToCost(new ItemStack(Blocks.stone), 2);
					else if (state.getBlock() == Blocks.stonebrick)
						addToCost(new ItemStack(Blocks.stone), 1);
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.STONE)
						addToCost(new ItemStack(Blocks.stone), 1);
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.SAND)
						addToCost(new ItemStack(Blocks.sandstone), 1);
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.WOOD)
						plankCost++;
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.COBBLESTONE)
						addToCost(new ItemStack(Blocks.cobblestone), 1);
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.BRICK)
						addToCost(new ItemStack(Blocks.brick_block), 1);
					else if (state.getBlock() == Blocks.stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.SMOOTHBRICK)
						addToCost(new ItemStack(Blocks.stone), 1);
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.OAK)
						plankOakCost++;
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.SPRUCE)
						plankSpruceCost++;
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.BIRCH)
						plankBirchCost++;
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.JUNGLE)
						plankJungleCost++;
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.ACACIA)
						plankAcaciaCost++;
					else if (state.getBlock() == Blocks.wooden_slab && state.getValue(BlockWoodSlab.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
						plankDarkCost++;
					else if (state.getBlock() == Blocks.wool)
						addToCost(new ItemStack (Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 1);
					else if (state.getBlock() == Blocks.nether_wart)
						addToCost(new ItemStack(Items.nether_wart), 1);
					else if (state.getBlock() == Blocks.double_stone_slab && state.getValue(BlockStoneSlab.VARIANT) == BlockStoneSlab.EnumType.STONE)
						addToCost(new ItemStack(Blocks.stone), 1);
					else if (state.getBlock() == Blocks.iron_block)
						addToCost(new ItemStack(Items.iron_ingot), 9);
					else if (state.getBlock() == Blocks.anvil)
						addToCost(new ItemStack(Items.iron_ingot), 30);
					else if (state.getBlock() == Blocks.iron_bars)
						addToCost(new ItemStack(Items.iron_ingot), 1);
					else if (state.getBlock() == Blocks.gold_block)
						addToCost(new ItemStack(Items.gold_ingot), 9);
					else if (state.getBlock() == Blocks.cauldron)
						addToCost(new ItemStack(Items.iron_ingot), 7);
					else if (state.getBlock() == Blocks.cobblestone_wall)
						addToCost(new ItemStack(Blocks.cobblestone), 1);
					else if (state.getBlock() == MillBlocks.blockMillChest)
						plankCost += 8;
					else if (state.getBlock() == Blocks.oak_stairs)
						plankOakCost += 2;
					else if (state.getBlock() == Blocks.spruce_stairs)
						plankSpruceCost += 2;
					else if (state.getBlock() == Blocks.birch_stairs)
						plankBirchCost += 2;
					else if (state.getBlock() == Blocks.jungle_stairs)
						plankJungleCost += 2;
					else if (state.getBlock() == Blocks.acacia_stairs)
						plankAcaciaCost += 2;
					else if (state.getBlock() == Blocks.dark_oak_stairs)
						plankDarkCost += 2;
					else if (state.getBlock() == Blocks.stone_stairs)
						addToCost(new ItemStack(Blocks.cobblestone), 2);
					else if (state.getBlock() == Blocks.stone_brick_stairs)
						addToCost(new ItemStack(Blocks.stone), 2);
					else if (state.getBlock() == Blocks.sandstone_stairs)
						addToCost(new ItemStack(Blocks.sandstone), 2);
					else if (state.getBlock() == Blocks.brick_stairs)
						addToCost(new ItemStack(Blocks.brick_block), 2);
					else if (state.getBlock() == Blocks.ladder)
						plankCost += 2;
					else if (state.getBlock() == Blocks.standing_sign)
						plankCost += 7;
					else if (state.getBlock() == Blocks.wall_sign)
						plankCost += 7;
					//else if (state.getBlock() == BlockMillSign.)
					//	plankCost += 7;
					else if (state.getBlock() == Blocks.oak_door)
						plankOakCost += 2;
					else if (state.getBlock() == Blocks.spruce_door)
						plankSpruceCost += 2;
					else if (state.getBlock() == Blocks.birch_door)
						plankBirchCost += 2;
					else if (state.getBlock() == Blocks.jungle_door)
						plankJungleCost += 2;
					else if (state.getBlock() == Blocks.acacia_door)
						plankAcaciaCost += 2;
					else if (state.getBlock() == Blocks.dark_oak_door)
						plankDarkCost += 2;
					else if (state.getBlock() == Blocks.trapdoor)
						plankCost += 6;
					else if (state.getBlock() == Blocks.bed && state.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT)
					{
						plankCost += 3;
						addToCost(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 3);
					}					
					else if (state.getBlock() == MillBlocks.emptySericulture)
						plankCost += 4;
					else if (state.getBlock() != Blocks.air && !freeBuild(state))
					{
						addToCost(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)), 1);
					}
				}
			}
		}

		if (plankCost > 0) 
		{
			addToCost("logWood", (int) Math.max(Math.ceil(plankCost * 1.0 / 4), 1));
		}

		if (plankOakCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log, 1, Blocks.log.getMetaFromState(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK))), (int) Math.max(Math.ceil(plankOakCost * 1.0 / 4), 1));
		}

		if (plankSpruceCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log, 1, Blocks.log.getMetaFromState(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE))), (int) Math.max(Math.ceil(plankSpruceCost * 1.0 / 4), 1));
		}

		if (plankBirchCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log, 1, Blocks.log.getMetaFromState(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH))), (int) Math.max(Math.ceil(plankBirchCost * 1.0 / 4), 1));
		}

		if (plankJungleCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log, 1, Blocks.log.getMetaFromState(Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE))), (int) Math.max(Math.ceil(plankJungleCost * 1.0 / 4), 1));
		}
		
		if (plankAcaciaCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log2, 1, Blocks.log2.getMetaFromState(Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA))), (int) Math.max(Math.ceil(plankJungleCost * 1.0 / 4), 1));
		}
		
		if (plankDarkCost > 0) 
		{
			addToCost(new ItemStack(Blocks.log2, 1, Blocks.log2.getMetaFromState(Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK))), (int) Math.max(Math.ceil(plankJungleCost * 1.0 / 4), 1));
		}

		if (glassPaneCost > 0) {
			addToCost(new ItemStack(Blocks.glass), (int) Math.max(Math.ceil(glassPaneCost * 6.0 / 16), 1));
		}

		if (byzBricksHalf > 0) {
			addToCost(new ItemStack(MillBlocks.byzantineTile), (int) Math.max(Math.ceil(byzBricksHalf / 2), 1));
		}
	}
	
	private LocationReturn testLocation(VillageGeography vg, BlockPos center, int x, int z, EnumFacing facing, MillPathNavigate pathing)
	{
		EnumFacing orientation;
		
		final int relx = x + vg.mapStartX - center.getX();
		final int relz = z + vg.mapStartZ - center.getZ();

		vg.buildTested[x][z] = true;

		if (facing == null || facing.getIndex() < 2) 
		{
			if (relx * relx > relz * relz) 
			{
				if (relx > 0)
					orientation = EnumFacing.NORTH;
				else
					orientation = EnumFacing.SOUTH;
			} 
			else 
			{
				if (relz > 0)
					orientation = EnumFacing.EAST;
				else
					orientation = EnumFacing.WEST;
			}
		} 
		else 
		{
			orientation = facing;
		}
		
		orientation = EnumFacing.getFront((orientation.getHorizontalIndex() + buildingOrientation.getHorizontalIndex()) % 4);

		int xwidth;
		int zwidth;

		if (orientation == EnumFacing.NORTH || orientation == EnumFacing.SOUTH) 
		{
			xwidth = length + areaToClear * 2 + 2;
			zwidth = width + areaToClear * 2 + 2;
		} 
		else 
		{
			xwidth = width + areaToClear * 2 + 2;
			zwidth = length + areaToClear * 2 + 2;
		}

		int altitudeTotal = 0;
		int nbPoints = 0;
		int nbError = 0;

		int allowedErrors = 10;
		boolean hugeBuilding = false;

		if (xwidth * zwidth > 6000) 
		{
			allowedErrors = 1500;
			hugeBuilding = true;
		} 
		else if (xwidth * zwidth > 200) 
		{
			allowedErrors = xwidth * zwidth / 20;
		}

		boolean reachable = false;
		
		for (int i = 0; i <= (int) Math.floor(xwidth / 2); i++) 
		{
			for (int j = 0; j <= (int) Math.floor(zwidth / 2); j++) 
			{
				for (int k = 0; k < 4; k++) 
				{
					int ci, cj;
					if (k == 0) 
					{
						ci = x + i;
						cj = z + j;
					} 
					else if (k == 1) 
					{
						ci = x - i;
						cj = z + j;
					} 
					else if (k == 2) 
					{
						ci = x - i;
						cj = z - j;
					} 
					else 
					{
						ci = x + i;
						cj = z - j;
					}

					if (ci < 0 || cj < 0 || ci >= vg.length || cj >= vg.width) 
					{
						BlockPos p = new BlockPos(ci + vg.mapStartX, 64, cj + vg.mapStartZ);

						return new LocationReturn(LocationReturn.OUTSIDE_RADIUS, p);
					}

					if (vg.buildingLoc[ci][cj]) 
					{
						if (nbError > allowedErrors) 
						{
							final BlockPos p = new BlockPos(ci + vg.mapStartX, 64, cj + vg.mapStartZ);

							return new LocationReturn(LocationReturn.LOCATION_CLASH, p);
						} 
						else
							nbError += 5;
					} 
					else if (vg.buildingForbidden[ci][cj]) 
					{
						if (!hugeBuilding || nbError > allowedErrors) {

							final BlockPos p = new BlockPos(ci + vg.mapStartX, 64, cj + vg.mapStartZ);

							return new LocationReturn(LocationReturn.CONSTRUCTION_FORBIDDEN, p);
						} 
						else
							nbError++;
					} 
					else if (vg.danger[ci][cj]) 
					{
						if (nbError > allowedErrors) 
						{
							final BlockPos p = new BlockPos(ci + vg.mapStartX, 64, cj + vg.mapStartZ);

							return new LocationReturn(LocationReturn.DANGER, p);
						} 
						else
							nbError++;
					} 
					else if (!vg.canBuild[ci][cj]) 
					{
						if (nbError > allowedErrors) 
						{
							final BlockPos p = new BlockPos(ci + vg.mapStartX, 64, cj + vg.mapStartZ);

							return new LocationReturn(LocationReturn.WRONG_ALTITUDE, p);
						} 
						else
							nbError++;
					}

					if (pathing != null && pathing.tryMoveToXYZ(ci, 64, cj, 0.5D)) 
					{
						reachable = false;
					} 
					else 
					{
						reachable = true;
					}

					altitudeTotal += vg.constructionHeight[ci][cj];
					nbPoints++;
				}
			}
		}
		
		if (pathing != null && !reachable) 
		{
			return new LocationReturn(LocationReturn.NOT_REACHABLE, center);
		}

		final int altitude = (int) (altitudeTotal * 1.0f / nbPoints);

		final BuildingLocation l = new BuildingLocation(this, new BlockPos(x + vg.mapStartX, altitude, z + vg.mapStartZ), orientation);

		return new LocationReturn(l);
	}
	
	public BuildingBlock[] getBuildingPoints(World worldIn, BuildingLocation location, boolean villageGeneration)
	{
		final int x = location.position.getX();
		final int y = location.position.getY();
		final int z = location.position.getZ();
		List<BuildingBlock> bblocks = new ArrayList<BuildingBlock>();
		
		EnumFacing orientation = location.orientation;
		
		if (!isUpdate && !isSubBuilding) 
		{
			// filling above ground area with air

			for (int j = -areaToClear; j < length + areaToClear; j++) 
			{
				for (int k = -areaToClear; k < width + areaToClear; k++) 
				{
					for (int i = height + 50; i > -1; i--) 
					{
						final int ak = j % 2 == 0 ? k : width - k - 1;

						// how far from building we are in the margin (0=in
						// building itself)
						int offset = 0;

						if (j < 0) 
						{
							offset = -j;
						} 
						else if (j >= length - 1) 
						{
							offset = j - length + 1;
						}
						
						if (ak < 0 && -ak > offset) 
						{
							offset = -ak;
						} 
						else if (ak >= width - 1 && ak - width + 1 > offset) 
						{
							offset = ak - width + 1;
						}

						offset--;

						if (i >= offset - 1) 
						{
							// for each block away from building, one extra height allowed
							final BlockPos p = adjustForOrientation(x, y + i, z, j - lengthOffset, ak - widthOffset, orientation);
							bblocks.add(new BuildingBlock(null, p, BuildingBlock.CLEARGROUND));
						} 
						else 
						{
							final BlockPos p = adjustForOrientation(x, y + i, z, j - lengthOffset, k - widthOffset, orientation);
							bblocks.add(new BuildingBlock(null, p, BuildingBlock.CLEARTREE));
						}
					}
				}
			}

			// filling Foundations with Soil
			for (int j = -areaToClear; j < length + areaToClear; j++) 
			{
				for (int k = -areaToClear; k < width + areaToClear; k++) 
				{
					for (int i = -10 + depth; i < 0; i++) 
					{
						final int ak = j % 2 == 0 ? k : width - k - 1;

						// how far from building we are in the margin (0=in building itself)
						int offset = 0;

						if (j < 0) 
						{
							offset = -j;
						} else if (j >= length - 1) 
						{
							offset = j - length + 1;
						}
						if (ak < 0 && -ak > offset) 
						{
							offset = -ak;
						} 
						else if (ak >= width - 1 && ak - width + 1 > offset) 
						{
							offset = ak - width + 1;
						}

						offset--;

						if (-i > offset) 
						{
							final BlockPos p = adjustForOrientation(x, y + i, z, j - lengthOffset, k - widthOffset, orientation);
							bblocks.add(new BuildingBlock(null, p, BuildingBlock.PRESERVEGROUNDDEPTH));
						} 
						else if (-i == offset) 
						{
							final BlockPos p = adjustForOrientation(x, y + i, z, j - lengthOffset, k - widthOffset, orientation);
							bblocks.add(new BuildingBlock(null, p, BuildingBlock.PRESERVEGROUNDSURFACE));
						} 
						else 
						{
							final BlockPos p = adjustForOrientation(x, y + i, z, j - lengthOffset, k - widthOffset, orientation);
							bblocks.add(new BuildingBlock(null, p, BuildingBlock.CLEARTREE));
						}
					}
				}
			}
		}
		
		//Starting with Deletion
		for (int i = height - 1; i >= 0; i--) 
		{
			for (int j = 0; j < length; j++) 
			{
				for (int k = 0; k < width; k++) 
				{
					int ak = j % 2 == 0 ? k : width - k - 1;

					IBlockState state = buildingArray[i][j][ak];

					BlockPos p = adjustForOrientation(x, y + i + depth, z, j - lengthOffset, ak - widthOffset, orientation);

					if (state.getBlock() == Blocks.air) 
					{
						bblocks.add(new BuildingBlock(state, p));
					}
				}
			}
		}
		
		//Standard Blocks
		for (int i = 0; i < height; i++) 
		{
			for (int j = 0; j < length; j++) 
			{
				for (int k = 0; k < width; k++) 
				{
					int ak = j % 2 == 0 ? k : width - k - 1;
					int ai = i + depth < 0 ? -i - depth - 1 : i;

					IBlockState state = buildingArray[ai][j][ak];

					BlockPos p = adjustForOrientation(x, y + ai + depth, z, j - lengthOffset, ak - widthOffset, orientation);
					for(IProperty prop : state.getProperties().keySet())
			        {
			            if(prop.getName().equals("facing"))
			            {
			            	if(((EnumFacing)state.getValue(prop)).getAxis() != EnumFacing.Axis.Y)
			            		state = state.withProperty(prop, EnumFacing.getHorizontal(((EnumFacing)state.getValue(prop)).getHorizontalIndex() + orientation.getHorizontalIndex() % 4));
			            }
			            else if(prop.getName().equals("axis"))
			            {
			            	EnumFacing.Axis orientedAxis;
			            	
		            		if(orientation == EnumFacing.NORTH || orientation == EnumFacing.SOUTH)
		            			orientedAxis = EnumFacing.Axis.X;
		            		else if(orientation == EnumFacing.EAST || orientation == EnumFacing.WEST)
		            			orientedAxis = EnumFacing.Axis.Z;
		            		else
		            		{
		            			orientedAxis = EnumFacing.Axis.Y;
		            			System.err.println("How the f#%$ did orientation end up vertical?");
		            		}
		            		if(!orientedAxis.apply(buildingOrientation))
		            		{
		            			if(!(state.getBlock() instanceof BlockLog))
		            			{
		            				if(state.getValue(prop) == EnumFacing.Axis.X)
		            					state = state.withProperty(prop, EnumFacing.Axis.Z);
		            				else if(state.getValue(prop) == EnumFacing.Axis.Z)
		            					state = state.withProperty(prop, EnumFacing.Axis.X);
		            			}
		            			else
		            			{
		            				if(state.getValue(prop) == BlockLog.EnumAxis.X)
		            					state = state.withProperty(prop, BlockLog.EnumAxis.Z);
		            				else if(state.getValue(prop) == BlockLog.EnumAxis.Z)
		            					state = state.withProperty(prop, BlockLog.EnumAxis.X);
		            			}
		            		}
			            }
			        }
					
					setReferencePositions(state, p, location);

					if (state.getBlock() != null && state.getBlock() != Blocks.air && firstPass(state)) 
					{
						if(state.getBlock() == Blocks.farmland)
							state = Blocks.dirt.getDefaultState();
						
						bblocks.add(new BuildingBlock(state, p));
					}
				}
			}
		}
		
		//Decorative and Other Blocks
		for (int i = 0; i < height; i++) 
		{
			for (int j = 0; j < length; j++) 
			{
				for (int k = 0; k < width; k++) 
				{
					int ak = j % 2 == 0 ? k : width - k - 1;
					int ai = i + depth < 0 ? -i - depth - 1 : i;

					IBlockState state = buildingArray[ai][j][ak];

					BlockPos p = adjustForOrientation(x, y + ai + depth, z, j - lengthOffset, ak - widthOffset, orientation);
					for(IProperty prop : state.getProperties().keySet())
			        {
			            if(prop.getName().equals("facing"))
			            {
			            	if(((EnumFacing)state.getValue(prop)).getAxis() != EnumFacing.Axis.Y)
			            		state = state.withProperty(prop, EnumFacing.getHorizontal(((EnumFacing)state.getValue(prop)).getHorizontalIndex() + orientation.getHorizontalIndex() % 4));
			            }
			            else if(prop.getName().equals("axis"))
			            {
			            	EnumFacing.Axis orientedAxis;
			            	
		            		if(orientation == EnumFacing.NORTH || orientation == EnumFacing.SOUTH)
		            			orientedAxis = EnumFacing.Axis.X;
		            		else if(orientation == EnumFacing.EAST || orientation == EnumFacing.WEST)
		            			orientedAxis = EnumFacing.Axis.Z;
		            		else
		            		{
		            			orientedAxis = EnumFacing.Axis.Y;
		            			System.err.println("How the f#%$ did orientation end up vertical?");
		            		}
		            		if(!orientedAxis.apply(buildingOrientation))
		            		{
		            			if(!(state.getBlock() instanceof BlockLog))
		            			{
		            				if(state.getValue(prop) == EnumFacing.Axis.X)
		            					state = state.withProperty(prop, EnumFacing.Axis.Z);
		            				else if(state.getValue(prop) == EnumFacing.Axis.Z)
		            					state = state.withProperty(prop, EnumFacing.Axis.X);
		            			}
		            			else
		            			{
		            				if(state.getValue(prop) == BlockLog.EnumAxis.X)
		            					state = state.withProperty(prop, BlockLog.EnumAxis.Z);
		            				else if(state.getValue(prop) == BlockLog.EnumAxis.Z)
		            					state = state.withProperty(prop, BlockLog.EnumAxis.X);
		            			}
		            		}
			            }
			        }

					if (state.getBlock() != null && !firstPass(state)) 
					{
						bblocks.add(new BuildingBlock(state, p));
					}
				}
			}
		}
		
		final HashMap<BlockPos, BuildingBlock> bbmap = new HashMap<BlockPos, BuildingBlock>();

		final boolean[] toDelete = new boolean[bblocks.size()];

		for (int i = 0; i < bblocks.size(); i++) 
		{
			final BuildingBlock bb = bblocks.get(i);
			
			Block block = bb.blockState.getBlock();
			IBlockState state = bb.blockState;
			int special = bb.specialBlock;
			
			if (bbmap.containsKey(bb.position)) 
			{
				block = bbmap.get(bb.position).blockState.getBlock();
				state = bbmap.get(bb.position).blockState;
				special = bbmap.get(bb.position).specialBlock;
			}
			else
			{
				block = worldIn.getBlockState(bb.position).getBlock();
				state = worldIn.getBlockState(bb.position);
				special = 0;
			}
			
			if ((state == bb.blockState && special == 0 || block == Blocks.grass && bb.blockState.getBlock() == Blocks.dirt) && bb.specialBlock == 0) 
				toDelete[i] = true;
			else if (bb.specialBlock == BuildingBlock.CLEARTREE && block != Blocks.log && block != Blocks.leaves)
				toDelete[i] = true;
			else if (bb.specialBlock == BuildingBlock.CLEARGROUND && (block == null || block == Blocks.air))
				toDelete[i] = true;
			else if (bb.specialBlock == BuildingBlock.PRESERVEGROUNDDEPTH && CommonUtilities.getValidGroundBlock(block, false) == block)
				toDelete[i] = true;
			else if (bb.specialBlock == BuildingBlock.PRESERVEGROUNDSURFACE && CommonUtilities.getValidGroundBlock(block, true) == block)
				toDelete[i] = true;
			else 
			{
				bbmap.put(bb.position, bb);
				toDelete[i] = false;
			}
		}
		
		for (int i = 0; i < toDelete.length; i++) 
		{
			if (toDelete[i])
				bblocks.remove(i);
		}
		
		BuildingBlock[] abblocks = new BuildingBlock[bblocks.size()];

		for (int i = 0; i < bblocks.size(); i++) 
		{
			abblocks[i] = bblocks.get(i);
		}
		
		return abblocks;
	}

	private static BlockPos adjustForOrientation(final int x, final int y, final int z, final int xoffset, final int zoffset, final EnumFacing orientation)
	{
		BlockPos pos = new BlockPos(x, y, z);
		if (orientation == EnumFacing.SOUTH)
			pos = new BlockPos(x + xoffset, y, z + zoffset);
		else if (orientation == EnumFacing.WEST)
			pos = new BlockPos(x + zoffset, y, z - xoffset);
		else if (orientation == EnumFacing.NORTH)
			pos = new BlockPos(x - xoffset, y, z - zoffset);
		else if (orientation == EnumFacing.EAST)
			pos = new BlockPos(x - zoffset, y, z + xoffset);

		return pos;
	}
	
	private boolean firstPass(IBlockState state) {
		return state.getBlock().getCreativeTabToDisplayOn() == CreativeTabs.tabBlock || state.getBlock() instanceof BlockDecorativeEarth || state.getBlock() instanceof BlockDecorativeWood || state.getBlock() instanceof BlockDecorativeStone || state.getBlock() == MillBlocks.byzantineStoneTile || state.getBlock() == MillBlocks.byzantineTile || state.getBlock() == MillBlocks.byzantineTileSlab || state.getBlock() == MillBlocks.byzantineTileSlabDouble;
	}
	
	private void setReferencePositions(IBlockState state, BlockPos pos, BuildingLocation location)
	{
		if(state.getBlock() instanceof BlockMillChest)
			location.chestPos.add(pos);
		else if(state.getBlock() == MillBlocks.storedPosition && state.getValue(StoredPosition.VARIANT) == StoredPosition.EnumType.TRADEPOS)
			location.tradePos = (pos);
		//else if BlockMillSign add to Sign Positions
		else if(state.getBlock() instanceof BlockBed && state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD)
			location.sleepPos.add(pos);
		else if(state.getBlock() == MillBlocks.storedPosition && state.getValue(StoredPosition.VARIANT) == StoredPosition.EnumType.SLEEPPOS)
			location.sleepPos.add(pos);
		else if(state.getBlock() == MillBlocks.storedPosition && state.getValue(StoredPosition.VARIANT) == StoredPosition.EnumType.SOURCEPOS)
			location.sourcePos.add(pos);
		else if(state.getBlock() == Blocks.furnace)
			location.craftPos.add(pos);
		else if(state.getBlock() == Blocks.crafting_table)
			location.craftPos.add(pos);
		else if(state.getBlock() == Blocks.iron_block)
			location.craftPos.add(pos);
		else if(state.getBlock() == Blocks.anvil)
			location.craftPos.add(pos);
		else if(state.getBlock() == MillBlocks.storedPosition && state.getValue(StoredPosition.VARIANT) == StoredPosition.EnumType.HIDEPOS)
			location.hidePos.add(pos);
		else if(state.getBlock() == MillBlocks.storedPosition && state.getValue(StoredPosition.VARIANT) == StoredPosition.EnumType.DEFENDPOS)
			location.defendPos.add(pos);
	}
	
    //////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static class LocationReturn 
	{
		public static final int OUTSIDE_RADIUS = 1;
		public static final int LOCATION_CLASH = 2;
		public static final int CONSTRUCTION_FORBIDDEN = 3;
		public static final int WRONG_ALTITUDE = 4;
		public static final int DANGER = 5;
		public static final int NOT_REACHABLE = 4;

		public BuildingLocation location;
		public int errorCode;
		public BlockPos errorPos;

		public LocationReturn(final BuildingLocation l) 
		{
			location = l;
			errorCode = 0;
			errorPos = null;
		}

		public LocationReturn(final int error, final BlockPos pos) 
		{
			location = null;
			errorCode = error;
			errorPos = pos;
		}

	}
	
	//
	public static class ResourceCost
	{
		int amount;
		ItemStack stack;
		String odString;
		
		public ResourceCost(ItemStack stackIn, int amountIn)
		{
			amount = amountIn;
			stack = stackIn;
			odString = null;
		}
		
		public ResourceCost(String nameIn, int amountIn)
		{
			amount = amountIn;
			stack = null;
			odString = nameIn;
		}
		
		public ItemStack getStack()
		{
			return stack;
		}
		
		public String getString()
		{
			return odString;
		}
		
		public void add (int amountIn)
		{
			amount += amountIn;
		}
		
		public int getCost(ItemStack stackIn)
		{
			if(stackIn.getIsItemStackEqual(stack))
				return amount;
			else
			{
				List<ItemStack> odStack = OreDictionary.getOres(odString, true);
				
				if(odStack.isEmpty())
				{
					System.err.println("Error! - Resource computed with unidentifed OreID.");
					return 0;
				}

				for (ItemStack anOdStack : odStack) {
					if (stackIn.getIsItemStackEqual(anOdStack))
						return amount;
				}
			}
			
			return 0;
		}
	}
}
