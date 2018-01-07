package org.millenaire.building;

import org.millenaire.CommonUtilities;
import org.millenaire.items.MillItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BuildingBlock 
{
	public static byte OAKSPAWN = 1;
	public static byte SPRUCESPAWN = 2;
	public static byte BIRCHSPAWN = 3;
	public static byte JUNGLESPAWN = 4;
	public static byte ACACIASPAWN = 5;
	public static byte PRESERVEGROUNDDEPTH = 6;
	public static byte PRESERVEGROUNDSURFACE = 7;
	public static byte CLEARTREE = 8;
	public static byte CLEARGROUND = 9;
	public static byte SPAWNERSKELETON = 10;
	public static byte SPAWNERZOMBIE = 11;
	public static byte SPAWNERSPIDER = 12;
	public static byte SPAWNERCAVESPIDER = 13;
	public static byte SPAWNERCREEPER = 14;
	public static byte SPAWNERBLAZE = 15;
	public static byte DISPENDERUNKNOWNPOWDER = 16;
	public static byte TAPESTRY = 17;
	public static byte BYZANTINEICONSMALL = 18;
	public static byte BYZANTINEICONMEDIUM = 19;
	public static byte BYZANTINEICONLARGE = 20;
	public static byte INDIANSTATUE = 21;
	public static byte MAYANSTATUE = 22;
	
	public IBlockState blockState;
	public BlockPos position;
	public byte specialBlock;
	
	BuildingBlock(IBlockState state, BlockPos pos, byte special)
	{
		blockState = state;
		position = pos;
		specialBlock = special;
	}

	BuildingBlock(IBlockState state, BlockPos pos)
	{
		blockState = state;
		position = pos;
		specialBlock = 0;
	}
	
	public void build(World worldIn, boolean onGeneration)
	{
		if (specialBlock != BuildingBlock.PRESERVEGROUNDDEPTH && specialBlock != BuildingBlock.PRESERVEGROUNDSURFACE && specialBlock != BuildingBlock.CLEARTREE) 
		{
			worldIn.setBlockState(position, blockState);
			String soundName = blockState.getBlock().stepSound.getPlaceSound();
			worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);
		}
		
		if (specialBlock == BuildingBlock.PRESERVEGROUNDDEPTH || specialBlock == BuildingBlock.PRESERVEGROUNDSURFACE) 
		{
			Block block = worldIn.getBlockState(position).getBlock();

			final boolean surface = specialBlock == BuildingBlock.PRESERVEGROUNDSURFACE;

			final Block validGroundBlock = CommonUtilities.getValidGroundBlock(block, surface);

			if (validGroundBlock == null) 
			{
				BlockPos below = position.down();
				Block targetblock = null;
				while (targetblock == null && below.getY() > 0) 
				{
					block = worldIn.getBlockState(below).getBlock();
					if (CommonUtilities.getValidGroundBlock(block, surface) != null)
						targetblock = CommonUtilities.getValidGroundBlock(block, surface);
					below = below.down();
				}

				if (targetblock == Blocks.dirt && onGeneration) 
				{
					targetblock = Blocks.grass;
				} 
				else if (targetblock == Blocks.grass && !onGeneration) 
				{
					targetblock = Blocks.dirt;
				}

				if (targetblock == Blocks.air) 
				{
					if (onGeneration) 
						targetblock = Blocks.grass;
					else
						targetblock = Blocks.dirt;
				}

				assert targetblock != null;
				worldIn.setBlockState(position, targetblock.getDefaultState());
				String soundName = targetblock.stepSound.getPlaceSound();
				worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);
			} 
			else if (onGeneration && validGroundBlock == Blocks.dirt && worldIn.getBlockState(position.up()) == null) 
			{
				worldIn.setBlockState(position, Blocks.grass.getDefaultState());
				String soundName = Blocks.grass.stepSound.getPlaceSound();
				worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);
			} 
			else if (validGroundBlock != block && !(validGroundBlock == Blocks.dirt && block == Blocks.grass)) 
			{
				worldIn.setBlockState(position, validGroundBlock.getDefaultState());
				String soundName = validGroundBlock.stepSound.getPlaceSound();
				worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);
			}
		}
		else if (specialBlock == BuildingBlock.CLEARTREE) 
		{
			Block block = worldIn.getBlockState(position).getBlock();

			if (block == Blocks.log || block == Blocks.leaves) 
			{
				worldIn.setBlockToAir(position);
				String soundName = block.stepSound.getBreakSound();
				worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);

				final Block blockBelow = worldIn.getBlockState(position.down()).getBlock();

				final Block targetBlock = CommonUtilities.getValidGroundBlock(blockBelow, true);

				if (onGeneration && targetBlock == Blocks.dirt) 
				{
					worldIn.setBlockState(position.down(), Blocks.grass.getDefaultState());
				} 
				else if (targetBlock != null) 
				{
					worldIn.setBlockState(position.down(), targetBlock.getDefaultState());
				}
			}

		} 
		else if (specialBlock == BuildingBlock.CLEARGROUND) 
		{
			Block block = worldIn.getBlockState(position).getBlock();
			
			worldIn.setBlockToAir(position);
			String soundName = block.stepSound.getBreakSound();
			worldIn.playSoundEffect(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, soundName, 0.3F, 0.6F);

			final Block blockBelow = worldIn.getBlockState(position.down()).getBlock();

			final Block targetBlock = CommonUtilities.getValidGroundBlock(blockBelow, true);

			if (onGeneration && targetBlock == Blocks.dirt) 
			{
				worldIn.setBlockState(position.down(), Blocks.grass.getDefaultState());
			} 
			else if (targetBlock != null) 
			{
				worldIn.setBlockState(position.down(), targetBlock.getDefaultState());
			}
		}
		else if (specialBlock == BuildingBlock.OAKSPAWN) 
		{
			if (onGeneration) 
			{
				WorldGenerator wg = new WorldGenTrees(false);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
			else
			{
				WorldGenerator wg = new WorldGenTrees(true, 4 + CommonUtilities.random.nextInt(7), Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK), Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK), false);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
		} 
		else if (specialBlock == BuildingBlock.SPRUCESPAWN) 
		{
			if (onGeneration) 
			{
				WorldGenerator wg = new WorldGenTaiga2(false);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
			else
			{
				WorldGenerator wg = new WorldGenTaiga2(true);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
		} 
		else if (specialBlock == BuildingBlock.BIRCHSPAWN) 
		{
			if (onGeneration) 
			{
				WorldGenerator wg = new WorldGenForest(true, false);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
			else
			{
				WorldGenerator wg = new WorldGenForest(false, true);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
		} 
		else if (specialBlock == BuildingBlock.JUNGLESPAWN) 
		{
			if (onGeneration) 
			{
				WorldGenerator wg = new WorldGenTrees(false, 4 + CommonUtilities.random.nextInt(7), Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE), Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE), true);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
			else
			{
				WorldGenerator wg = new WorldGenTrees(true, 4 + CommonUtilities.random.nextInt(7), Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE), Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE), true);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
		}
		else if (specialBlock == BuildingBlock.ACACIASPAWN) 
		{
			if (onGeneration) 
			{
				WorldGenerator wg = new WorldGenSavannaTree(false);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
			else
			{
				WorldGenerator wg = new WorldGenSavannaTree(true);
				wg.generate(worldIn, CommonUtilities.random, position);
			}
		}
		else if (specialBlock == BuildingBlock.SPAWNERSKELETON) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("Skeleton");
		} 
		else if (specialBlock == BuildingBlock.SPAWNERZOMBIE) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("Zombie");
		} 
		else if (specialBlock == BuildingBlock.SPAWNERSPIDER) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("Spider");
		} 
		else if (specialBlock == BuildingBlock.SPAWNERCAVESPIDER) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("CaveSpider");
		} 
		else if (specialBlock == BuildingBlock.SPAWNERCREEPER) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("Creeper");
		} 
		else if (specialBlock == BuildingBlock.SPAWNERBLAZE) 
		{
			worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState());
			final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) worldIn.getTileEntity(position);
			tileentitymobspawner.getSpawnerBaseLogic().setEntityName("Blaze");
		} 
		else if (specialBlock == BuildingBlock.DISPENDERUNKNOWNPOWDER) 
		{
			worldIn.setBlockState(position, Blocks.dispenser.getDefaultState());
			final TileEntityDispenser dispenser = (TileEntityDispenser)worldIn.getTileEntity(position);
			dispenser.addItemStack(new ItemStack(MillItems.unknownPowder, 2));
		}
	}
	
	public void buildPath()
	{
		//Make code to build paths
	}
}
