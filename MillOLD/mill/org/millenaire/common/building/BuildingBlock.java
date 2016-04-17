package org.millenaire.common.building;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.millenaire.common.EntityMillDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class BuildingBlock {

	public static byte TAPESTRY = 1;
	public static byte OAKSPAWN = 2;
	public static byte PINESPAWN = 3;
	public static byte BIRCHSPAWN = 4;
	public static byte INDIANSTATUE = 5;
	public static byte PRESERVEGROUNDDEPTH = 6;
	public static byte CLEARTREE = 7;
	public static byte MAYANSTATUE = 8;
	public static byte SPAWNERSKELETON = 9;
	public static byte SPAWNERZOMBIE = 10;
	public static byte SPAWNERSPIDER = 11;
	public static byte SPAWNERCAVESPIDER = 12;
	public static byte SPAWNERCREEPER = 13;
	public static byte DISPENDERUNKNOWNPOWDER = 14;
	public static byte JUNGLESPAWN = 15;
	public static byte INVERTEDDOOR = 16;
	public static byte CLEARGROUND = 17;
	public static byte BYZANTINEICONSMALL = 18;
	public static byte BYZANTINEICONMEDIUM = 19;
	public static byte BYZANTINEICONLARGE = 20;
	public static byte PRESERVEGROUNDSURFACE = 21;
	public static byte SPAWNERBLAZE = 22;

	public static BuildingBlock read(final NBTTagCompound nbttagcompound, final String label) {
		final Point p = Point.read(nbttagcompound, label + "pos");
		return new BuildingBlock(p, nbttagcompound.getInteger(label + "bid"), nbttagcompound.getInteger(label + "meta"), nbttagcompound.getInteger(label + "special"));
	}

	public Block block;
	public byte meta;
	public Point p;

	public byte special;

	BuildingBlock(final Point p, final Block block) {
		this(p, block, 0);
	}

	public BuildingBlock(final Point p, final Block block, final int meta) {
		this.p = p;
		this.block = block;
		this.meta = (byte) meta;
		special = 0;
	}

	public BuildingBlock(final Point p, final Block block, final int meta, final int special) {
		this.p = p;
		this.block = block;
		this.meta = (byte) meta;
		this.special = (byte) special;
	}

	public BuildingBlock(final Point p, final int blockID, final int meta, final int special) {
		this.p = p;
		this.block = (Block) Block.blockRegistry.getObjectById(blockID);
		this.meta = (byte) meta;
		this.special = (byte) special;
	}

	public boolean alreadyDone(final World world) {

		if (special != 0) {
			return false;
		}

		final Block block = MillCommonUtilities.getBlock(world, p);

		if (this.block != block) {
			return false;
		}

		final int meta = MillCommonUtilities.getBlockMeta(world, p);

		if (this.meta != meta) {
			return false;
		}

		return true;
	}

	public void build(final World world, final boolean worldGeneration, final boolean wandimport) {

		try {

			final boolean notifyBlocks = true;

			final boolean playSound = !worldGeneration && !wandimport;

			if (special != BuildingBlock.PRESERVEGROUNDDEPTH && special != BuildingBlock.PRESERVEGROUNDSURFACE && special != BuildingBlock.CLEARTREE) {
				// preserve sign posts when importing
				if (!wandimport || block != Blocks.air || MillCommonUtilities.getBlock(world, p) != Blocks.standing_sign) {

					MillCommonUtilities.setBlockAndMetadata(world, p, block, meta, notifyBlocks, playSound);
				}
			}

			if (special == BuildingBlock.PRESERVEGROUNDDEPTH || special == BuildingBlock.PRESERVEGROUNDSURFACE) {
				Block block = MillCommonUtilities.getBlock(world, p);

				final boolean surface = special == BuildingBlock.PRESERVEGROUNDSURFACE;

				final Block validGroundBlock = MillCommonUtilities.getBlockIdValidGround(block, surface);

				if (validGroundBlock == null) {
					Point below = p.getBelow();
					Block targetblock = null;
					while (targetblock == null && below.getiY() > 0) {
						block = MillCommonUtilities.getBlock(world, below);
						if (MillCommonUtilities.getBlockIdValidGround(block, surface) != null) {
							targetblock = MillCommonUtilities.getBlockIdValidGround(block, surface);
						}
						below = below.getBelow();
					}

					if (targetblock == Blocks.dirt && worldGeneration) {
						targetblock = Blocks.grass;
					} else if (targetblock == Blocks.grass && !worldGeneration) {
						targetblock = Blocks.dirt;
					}

					if (targetblock == Blocks.air) {
						if (worldGeneration) {
							targetblock = Blocks.grass;
						} else {
							targetblock = Blocks.dirt;
						}
					}

					MillCommonUtilities.setBlockAndMetadata(world, p, targetblock, 0, notifyBlocks, playSound);
				} else if (worldGeneration && validGroundBlock == Blocks.dirt && MillCommonUtilities.getBlock(world, p.getAbove()) == null) {
					MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.grass, 0, notifyBlocks, playSound);
				} else if (validGroundBlock != block && !(validGroundBlock == Blocks.dirt && block == Blocks.grass)) {
					MillCommonUtilities.setBlockAndMetadata(world, p, validGroundBlock, 0, notifyBlocks, playSound);
				}
				// MLHelper.setBlockAndMetadata(world, p,
				// Blocks.brick_block.blockID, 0);
			} else if (special == BuildingBlock.CLEARTREE) {
				final Block block = MillCommonUtilities.getBlock(world, p);

				if (block == Blocks.log || block == Blocks.leaves) {
					MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.air, 0, notifyBlocks, playSound);

					final Block blockBelow = MillCommonUtilities.getBlock(world, p.getBelow());

					final Block targetBlock = MillCommonUtilities.getBlockIdValidGround(blockBelow, true);

					if (worldGeneration && targetBlock == Blocks.dirt) {
						MillCommonUtilities.setBlock(world, p.getBelow(), Blocks.grass, notifyBlocks, playSound);
					} else if (targetBlock != null) {
						MillCommonUtilities.setBlock(world, p.getBelow(), targetBlock, notifyBlocks, playSound);
					}
				}

			} else if (special == BuildingBlock.CLEARGROUND) {

				if (!wandimport || MillCommonUtilities.getBlock(world, p) != Blocks.standing_sign) {
					MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.air, 0, notifyBlocks, playSound);
				}

				final Block blockBelow = MillCommonUtilities.getBlock(world, p.getBelow());

				final Block targetBlock = MillCommonUtilities.getBlockIdValidGround(blockBelow, true);

				if (worldGeneration && targetBlock == Blocks.dirt) {
					MillCommonUtilities.setBlock(world, p.getBelow(), Blocks.grass, notifyBlocks, playSound);
				} else if (targetBlock != null) {
					MillCommonUtilities.setBlock(world, p.getBelow(), targetBlock, notifyBlocks, playSound);
				}

			} else if (special == BuildingBlock.TAPESTRY) {
				final EntityMillDecoration tapestry = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.NORMAN_TAPESTRY);
				if (tapestry.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(tapestry);
					}
				}
			} else if (special == BuildingBlock.INDIANSTATUE) {
				final EntityMillDecoration statue = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.INDIAN_STATUE);
				if (statue.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special == BuildingBlock.MAYANSTATUE) {
				final EntityMillDecoration statue = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.MAYAN_STATUE);
				if (statue.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special == BuildingBlock.BYZANTINEICONSMALL) {
				final EntityMillDecoration statue = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.BYZANTINE_ICON_SMALL);
				if (statue.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special == BuildingBlock.BYZANTINEICONMEDIUM) {
				final EntityMillDecoration statue = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.BYZANTINE_ICON_MEDIUM);
				if (statue.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special == BuildingBlock.BYZANTINEICONLARGE) {
				final EntityMillDecoration statue = EntityMillDecoration.createTapestry(world, p, EntityMillDecoration.BYZANTINE_ICON_LARGE);
				if (statue.onValidSurface()) {
					if (!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special == BuildingBlock.OAKSPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTrees(false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special == BuildingBlock.PINESPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTaiga2(false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special == BuildingBlock.BIRCHSPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenForest(false, true);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special == BuildingBlock.JUNGLESPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTrees(true, 4 + MillCommonUtilities.random.nextInt(7), 3, 3, false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special == BuildingBlock.SPAWNERSKELETON) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("Skeleton");
			} else if (special == BuildingBlock.SPAWNERZOMBIE) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("Zombie");
			} else if (special == BuildingBlock.SPAWNERSPIDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("Spider");
			} else if (special == BuildingBlock.SPAWNERCAVESPIDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("CaveSpider");
			} else if (special == BuildingBlock.SPAWNERCREEPER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("Creeper");
			} else if (special == BuildingBlock.SPAWNERBLAZE) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.mob_spawner, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) p.getTileEntity(world);
				tileentitymobspawner.func_145881_a().setEntityName("Blaze");

			} else if (special == BuildingBlock.DISPENDERUNKNOWNPOWDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Blocks.dispenser, 0);
				final TileEntityDispenser dispenser = p.getDispenser(world);
				MillCommonUtilities.putItemsInChest(dispenser, Mill.unknownPowder, 2);
			} else if (block == Blocks.wooden_door) {
				if (special == BuildingBlock.INVERTEDDOOR) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), block, 9, notifyBlocks, playSound);
				} else {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), block, 8, notifyBlocks, playSound);
				}
			} else if (block == Blocks.iron_door) {
				if (special == BuildingBlock.INVERTEDDOOR) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), block, 9, notifyBlocks, playSound);
				} else {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), block, 8, notifyBlocks, playSound);
				}
			} else if (block == Blocks.bed) {
				if ((meta & 3) == 0) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getEast(), block, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) == 1) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getSouth(), block, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) == 2) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getWest(), block, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) == 3) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getNorth(), block, meta - 8, notifyBlocks, playSound);
				}
			} else if (block == Blocks.stone_button) {
				final int newmeta = ((BlockButton) Blocks.stone_button).onBlockPlaced(world, p.getiX(), p.getiY(), p.getiZ(), 0, 0, 0, 0, 0);

				MillCommonUtilities.setBlockMetadata(world, p, newmeta, notifyBlocks);
			} else if (block == Blocks.water) {
				MillCommonUtilities.notifyBlock(world, p);
			} else if (block == Blocks.furnace) {
				setFurnaceMeta(world, p);
			} else if (block == Blocks.portal) {
				Blocks.portal.func_150000_e(world, p.getiX(), p.getiY(), p.getiZ());
			}
		} catch (final Exception e) {
			MLN.printException("Exception in BuildingBlock.build():", e);
		}
	}

	public void pathBuild(final Building th) {

		int targetPathLevel = 0;

		for (int i = 0; i < th.villageType.pathMaterial.size(); i++) {
			if ((th.villageType.pathMaterial.get(i).getBlock() == block || th.villageType.pathMaterial.get(i).getBlock() == Mill.path && block == Mill.pathSlab)
					&& th.villageType.pathMaterial.get(i).meta == meta) {
				targetPathLevel = i;
			}
		}

		final Block currentBlock = p.getBlock(th.worldObj);
		final int meta = p.getMeta(th.worldObj);

		/**
		 * //if there's a path above, clear it if (bidAbove==Mill.path.blockID
		 * || bidAbove==Mill.pathSlab.blockID) {
		 * p.getAbove().setBlock(th.worldObj, 0, 0, true, false); }
		 * 
		 * //if there's a path below, fill it if (bidBelow==Mill.path.blockID ||
		 * bidBelow==Mill.pathSlab.blockID) { if
		 * (MillCommonUtilities.getBlockIdValidGround(p.getRelative(0, -2,
		 * 0).getId(th.worldObj),true)>0) p.getBelow().setBlock(th.worldObj
		 * ,MillCommonUtilities.getBlockIdValidGround(p.getRelative(0, -2,
		 * 0).getId(th.worldObj),true), 0, true, false); else
		 * p.getBelow().setBlock(th.worldObj, Blocks.dirt.blockID, 0, true,
		 * false); }
		 **/

		if (currentBlock != Mill.path && currentBlock != Mill.pathSlab && MillCommonUtilities.canPathBeBuiltHere(currentBlock, meta)) {
			build(th.worldObj, false, false);
		} else if (currentBlock == Mill.path || currentBlock == Mill.pathSlab) {
			int currentPathLevel = Integer.MAX_VALUE;

			for (int i = 0; i < th.villageType.pathMaterial.size(); i++) {
				if (th.villageType.pathMaterial.get(i).meta == meta) {
					currentPathLevel = i;
				}
			}

			if (currentPathLevel < targetPathLevel) {
				build(th.worldObj, false, false);
			}

		}
	}

	private void setFurnaceMeta(final World world, final Point p) {

		final Block var5 = p.getRelative(0, 0, -1).getBlock(world);
		final Block var6 = p.getRelative(0, 0, 1).getBlock(world);
		final Block var7 = p.getRelative(-1, 0, 0).getBlock(world);
		final Block var8 = p.getRelative(1, 0, 0).getBlock(world);
		byte var9 = 3;

		if (var5.isOpaqueCube() && var5 != Blocks.furnace && var5 != Blocks.lit_furnace && !var6.isOpaqueCube()) {
			var9 = 3;
		}

		if (var6.isOpaqueCube() && var6 != Blocks.furnace && var6 != Blocks.lit_furnace && !var5.isOpaqueCube()) {
			var9 = 2;
		}

		if (var7.isOpaqueCube() && var7 != Blocks.furnace && var7 != Blocks.lit_furnace && !var8.isOpaqueCube()) {
			var9 = 5;
		}

		if (var8.isOpaqueCube() && var8 != Blocks.furnace && var8 != Blocks.lit_furnace && !var7.isOpaqueCube()) {
			var9 = 4;
		}

		MillCommonUtilities.setBlockMetadata(world, p, var9);
	}

	@Override
	public String toString() {
		return "(block: " + block + " meta: " + meta + " pos:" + p + ")";
	}

	public void write(final NBTTagCompound nbttagcompound, final String label) {
		nbttagcompound.setInteger(label + "bid", Block.getIdFromBlock(block));
		nbttagcompound.setInteger(label + "meta", meta);
		nbttagcompound.setInteger(label + "special", special);
		p.write(nbttagcompound, label + "pos");
	}

}