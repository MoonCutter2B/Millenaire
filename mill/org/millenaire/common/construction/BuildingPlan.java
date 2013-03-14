package org.millenaire.common.construction;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

import org.millenaire.common.Building;
import org.millenaire.common.BuildingLocation;
import org.millenaire.common.Culture;
import org.millenaire.common.EntityWallDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorld;
import org.millenaire.common.MillWorldInfo;
import org.millenaire.common.Point;
import org.millenaire.common.VillageType;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.AStarPathing;


public class BuildingPlan {

	static public class BuildingBlock {


		public static byte TAPESTRY = 1;
		public static byte OAKSPAWN = 2;
		public static byte PINESPAWN = 3;
		public static byte BIRCHSPAWN = 4;
		public static byte INDIANSTATUE = 5;
		public static byte PRESERVEGROUND = 6;
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

		public static BuildingBlock read(NBTTagCompound nbttagcompound,String label) {
			final Point p=Point.read(nbttagcompound, label+"pos");
			return new BuildingBlock(p,nbttagcompound.getInteger(label+"bid"),nbttagcompound.getInteger(label+"meta"),nbttagcompound.getInteger(label+"special"));
		}
		public short bid;
		public byte meta;
		public Point p;

		public byte special=0;



		BuildingBlock(Point p,int bid) {
			this(p,bid,0);
		}

		BuildingBlock(Point p,int bid,int meta) {
			this.p=p;
			this.bid=(short) bid;
			this.meta=(byte) meta;
		}

		public BuildingBlock(Point p,int bid,int meta,int special) {
			this.p=p;
			this.bid=(short) bid;
			this.meta=(byte) meta;
			this.special=(byte) special;
		}

		public boolean alreadyDone(World world) {

			if (special!=0)
				return false;

			final int bid=MillCommonUtilities.getBlock(world, p);

			if (this.bid!=bid)
				return false;

			final int meta=MillCommonUtilities.getBlockMeta(world, p);

			if (this.meta!=meta)
				return false;

			return true;
		}

		public void build(World world, boolean worldGeneration, boolean wandimport) {

			final boolean notifyBlocks=true;

			final boolean playSound=!worldGeneration && !wandimport;

			if ((special!=BuildingBlock.PRESERVEGROUND) && (special!=BuildingBlock.CLEARTREE)) {
				//preserve sign posts when importing
				if (!wandimport || (bid!=0) || (MillCommonUtilities.getBlock(world, p)!=Block.signPost.blockID)) {

					MillCommonUtilities.setBlockAndMetadata(world, p, bid, meta, notifyBlocks, playSound);
				}
			}

			if (special==BuildingBlock.PRESERVEGROUND) {
				int bid=MillCommonUtilities.getBlock(world, p);

				if (!MillCommonUtilities.isBlockIdValidGround(bid)) {
					Point below=p.getBelow();
					int targetbid=0;
					while ((targetbid==0) && (below.getiY()>0)) {
						bid=MillCommonUtilities.getBlock(world, below);
						if (MillCommonUtilities.isBlockIdValidGround(bid)) {
							targetbid=bid;
						}
						below=below.getBelow();
					}

					if ((targetbid==Block.dirt.blockID) && worldGeneration) {
						targetbid=Block.grass.blockID;
					} else if ((targetbid==Block.grass.blockID) && !worldGeneration) {
						targetbid=Block.dirt.blockID;
					}

					if (targetbid==0) {
						if (worldGeneration) {
							targetbid=Block.grass.blockID;
						} else {
							targetbid=Block.dirt.blockID;
						}
					}

					MillCommonUtilities.setBlockAndMetadata(world, p, targetbid, 0, notifyBlocks, playSound);
				} else if (worldGeneration && (bid==Block.dirt.blockID) && (MillCommonUtilities.getBlock(world, p.getAbove())==0)) {
					MillCommonUtilities.setBlockAndMetadata(world, p, Block.grass.blockID, 0, notifyBlocks, playSound);
				}
				//MLHelper.setBlockAndMetadata(world, p, Block.brick.blockID, 0);
			} else if (special==BuildingBlock.CLEARTREE) {
				final int bid=MillCommonUtilities.getBlock(world, p);

				if ((bid==Block.wood.blockID) || (bid==Block.leaves.blockID)) {
					MillCommonUtilities.setBlockAndMetadata(world, p, 0, 0, notifyBlocks, playSound);

					if (worldGeneration) {
						final int bidBelow=MillCommonUtilities.getBlock(world, p.getBelow());

						if ((bidBelow==Block.dirt.blockID)) {
							MillCommonUtilities.setBlock(world, p.getBelow(), Block.grass.blockID, notifyBlocks, playSound);
						}
					}
				}



			} else if (special==BuildingBlock.CLEARGROUND) {

				if (!wandimport || (MillCommonUtilities.getBlock(world, p)!=Block.signPost.blockID)) {
					MillCommonUtilities.setBlockAndMetadata(world, p, 0, 0, notifyBlocks, playSound);
				}

				if (worldGeneration) {
					final int bidBelow=MillCommonUtilities.getBlock(world, p.getBelow());
					if ((bidBelow==Block.dirt.blockID)) {
						MillCommonUtilities.setBlock(world, p.getBelow(), Block.grass.blockID, notifyBlocks, playSound);
					}
				}

			} else if (special==BuildingBlock.TAPESTRY) {
				final EntityWallDecoration tapestry=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.NORMAN_TAPESTRY);
				if(tapestry.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(tapestry);
					}
				}
			} else if (special==BuildingBlock.INDIANSTATUE) {
				final EntityWallDecoration statue=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.INDIAN_STATUE);
				if(statue.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special==BuildingBlock.MAYANSTATUE) {
				final EntityWallDecoration statue=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.MAYAN_STATUE);
				if(statue.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special==BuildingBlock.BYZANTINEICONSMALL) {
				final EntityWallDecoration statue=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.BYZANTINE_ICON_SMALL);
				if(statue.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special==BuildingBlock.BYZANTINEICONMEDIUM) {
				final EntityWallDecoration statue=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.BYZANTINE_ICON_MEDIUM);
				if(statue.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special==BuildingBlock.BYZANTINEICONLARGE) {
				final EntityWallDecoration statue=EntityWallDecoration.createTapestry(world, p, EntityWallDecoration.BYZANTINE_ICON_LARGE);
				if(statue.onValidSurface()) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(statue);
					}
				}
			} else if (special==BuildingBlock.OAKSPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTrees(false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special==BuildingBlock.PINESPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTaiga2(false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special==BuildingBlock.BIRCHSPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenForest(false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special==BuildingBlock.JUNGLESPAWN) {
				if (worldGeneration) {
					final WorldGenerator wg = new WorldGenTrees(true, 4 + MillCommonUtilities.random.nextInt(7), 3, 3, false);
					wg.generate(world, MillCommonUtilities.random, p.getiX(), p.getiY(), p.getiZ());
				}
			} else if (special==BuildingBlock.SPAWNERSKELETON) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.mobSpawner.blockID, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)p.getTileEntity(world);
				tileentitymobspawner.func_98049_a().func_98272_a("Skeleton");
			} else if (special==BuildingBlock.SPAWNERZOMBIE) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.mobSpawner.blockID, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)p.getTileEntity(world);
				tileentitymobspawner.func_98049_a().func_98272_a("Zombie");
			} else if (special==BuildingBlock.SPAWNERSPIDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.mobSpawner.blockID, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)p.getTileEntity(world);
				tileentitymobspawner.func_98049_a().func_98272_a("Spider");
			} else if (special==BuildingBlock.SPAWNERCAVESPIDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.mobSpawner.blockID, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)p.getTileEntity(world);
				tileentitymobspawner.func_98049_a().func_98272_a("CaveSpider");
			} else if (special==BuildingBlock.SPAWNERCREEPER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.mobSpawner.blockID, 0);
				final TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)p.getTileEntity(world);
				tileentitymobspawner.func_98049_a().func_98272_a("Creeper");
			} else if (special==BuildingBlock.DISPENDERUNKNOWNPOWDER) {
				MillCommonUtilities.setBlockAndMetadata(world, p, Block.dispenser.blockID, 0);
				final TileEntityDispenser dispenser=p.getDispenser(world);
				MillCommonUtilities.putItemsInChest(dispenser, Mill.unknownPowder.itemID, 2);
			} else if (bid==Block.doorWood.blockID) {
				if (special==BuildingBlock.INVERTEDDOOR) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), bid, 9, notifyBlocks, playSound);
				} else {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), bid, 8, notifyBlocks, playSound);
				}
			} else if (bid==Block.doorSteel.blockID) {
				if (special==BuildingBlock.INVERTEDDOOR) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), bid, 9, notifyBlocks, playSound);
				} else {
					MillCommonUtilities.setBlockAndMetadata(world, p.getAbove(), bid, 8, notifyBlocks, playSound);
				}
			} else if (bid==Block.bed.blockID) {
				if ((meta & 3) ==0) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getEast(), bid, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) ==1) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getSouth(), bid, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) ==2) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getWest(), bid, meta - 8, notifyBlocks, playSound);
				}
				if ((meta & 3) ==3) {
					MillCommonUtilities.setBlockAndMetadata(world, p.getNorth(), bid, meta - 8, notifyBlocks, playSound);
				}
			} else if (bid==Block.stoneButton.blockID) {
				final int newmeta=((BlockButton)Block.stoneButton).onBlockPlaced(world, p.getiX(),p.getiY(),p.getiZ(), 0, 0, 0, 0, 0);

				MillCommonUtilities.setBlockMetadata(world, p, newmeta, notifyBlocks);
			} else if (bid==Block.waterStill.blockID) {
				MillCommonUtilities.notifyBlock(world, p);
			} else if (bid==Block.furnaceIdle.blockID) {
				setFurnaceMeta(world,p);
			}
		}


		private void setFurnaceMeta(World world, Point p) {

			final int var5 = p.getRelative(0, 0, -1).getId(world);
			final int var6 = p.getRelative(0, 0, 1).getId(world);
			final int var7 = p.getRelative(-1, 0, 0).getId(world);
			final int var8 = p.getRelative(1, 0, 0).getId(world);
			byte var9 = 3;

			if (Block.opaqueCubeLookup[var5] && (var5!=Block.furnaceIdle.blockID) && (var5!=Block.furnaceBurning.blockID) && !Block.opaqueCubeLookup[var6])
			{
				var9 = 3;
			}

			if (Block.opaqueCubeLookup[var6] && (var6!=Block.furnaceIdle.blockID) && (var6!=Block.furnaceBurning.blockID) && !Block.opaqueCubeLookup[var5])
			{
				var9 = 2;
			}

			if (Block.opaqueCubeLookup[var7] && (var7!=Block.furnaceIdle.blockID) && (var7!=Block.furnaceBurning.blockID) && !Block.opaqueCubeLookup[var8])
			{
				var9 = 5;
			}

			if (Block.opaqueCubeLookup[var8] && (var8!=Block.furnaceIdle.blockID) && (var8!=Block.furnaceBurning.blockID) && !Block.opaqueCubeLookup[var7])
			{
				var9 = 4;
			}

			MillCommonUtilities.setBlockMetadata(world, p, var9);
		}

		@Override
		public String toString() {
			return "(id: "+bid+" meta: "+meta+" pos:"+p+")";
		}

		public void write(NBTTagCompound nbttagcompound,String label) {
			nbttagcompound.setInteger(label+"bid", bid);
			nbttagcompound.setInteger(label+"meta", meta);
			nbttagcompound.setInteger(label+"special", special);
			p.write(nbttagcompound, label+"pos");
		}


	}
	private static class BuildingFileFiler implements FilenameFilter {

		String end;

		public BuildingFileFiler(String ending) {
			end=ending;
		}

		@Override
		public boolean accept(File file, String name) {

			if (!name.endsWith(end))
				return false;

			if (name.startsWith("."))
				return false;

			return true;
		}
	}

	public static class LocationBuildingPair {
		public Building building;
		public BuildingLocation location;

		public LocationBuildingPair(Building b, BuildingLocation l) {
			building=b;
			location=l;
		}
	}
	public static class LocationReturn {

		public static final int OUTSIDE_RADIUS=1;
		public static final int LOCATION_CLASH=2;
		public static final int CONSTRUCTION_FORBIDEN=3;
		public static final int WRONG_ALTITUDE=4;
		public static final int DANGER=5;
		public static final int NOT_REACHABLE=4;

		public BuildingLocation location;
		public int errorCode;
		public Point errorPos;

		public LocationReturn(BuildingLocation l) {
			location=l;
			errorCode=0;
			errorPos=null;
		}

		public LocationReturn(int error,Point p) {
			location=null;
			errorCode=error;
			errorPos=p;
		}

	}

	public static class PointType {

		static PointType readColourPoint(String s) throws MillenaireException {

			final String[] params=s.split(";", -1);

			if (params.length != 5)
				throw new MillenaireException("Line "+s+" in blocklist.txt does not have five fields.");

			final String[] rgb=params[4].split("/", -1);

			if (rgb.length != 3)
				throw new MillenaireException("Colour in line "+s+" does not have three values.");

			final int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);

			if (MLN.LogBuildingPlan>=MLN.MAJOR) {
				MLN.major(null, "Loading colour point: "+getColourString(colour)+", "+params[0]);
			}

			params[1]=MillCommonUtilities.swapConfigBlockId(params[1]);

			if (Integer.parseInt(params[1]) == -1)
				return new PointType(colour,params[0]);
			else
				return new PointType(colour,Integer.parseInt(params[1]),Integer.parseInt(params[2]),Boolean.parseBoolean(params[3]));

		}
		int colour=-1,blockId=-1,meta=-1;
		char letter;
		String name=null;

		boolean secondStep=false;

		public PointType(char letter,int blockId,int meta,boolean secondStep) {
			this.letter=letter;
			this.blockId=blockId;
			this.meta=meta;
			this.secondStep=secondStep;
		}

		public PointType(char letter,String name) {
			this.name=name;
			this.letter=letter;
		}

		public PointType(int colour,int blockId,int meta,boolean secondStep) {
			this.colour=colour;
			this.blockId=blockId;
			this.meta=meta;
			this.secondStep=secondStep;
		}

		public PointType(int colour,String name) {
			this.name=name;
			this.colour=colour;
		}

		public boolean isSubType(String type) {
			if (name==null)
				return false;
			return name.startsWith(type);
		}

		public boolean isType(String type) {
			return type.equals(name);
		}

		@Override
		public String toString() {
			return name+"/"+colour+"/"+blockId+"/"+meta+"/"+MillCommonUtilities.getPointHash(blockId, meta);
		}
	}
	public static class StartingGood {

		public InvItem item;
		public double probability;
		public int fixedNumber,randomNumber;

		StartingGood(InvItem item,double probability,int fixedNumber, int randomNumber) {
			this.item=item;
			this.probability=probability;
			this.fixedNumber=fixedNumber;
			this.randomNumber=randomNumber;
		}
	}

	public static final String bempty="empty", bpreserveground="preserveground",ballbuttrees="allbuttrees",bgrass="grass",bsoil="soil",bricesoil="ricesoil",
			bturmericsoil="turmericsoil",bmaizesoil="maizesoil",bcarrotsoil="carrotsoil",bpotatosoil="potatosoil",bsugarcanesoil="sugarcanesoil",bnetherwartsoil="netherwartsoil",
			bvinesoil="vinesoil",bsilkwormblock="silkwormblock",
			blockedchest="lockedchest",bmainchest="mainchest",
			bsleepingPos="sleepingPos",bsellingPos="sellingPos",bcraftingPos="craftingPos",
			bdefendingPos="defendingPos",bshelterPos="shelterPos",

			blogoakvert="logoakvert",blogoakhor="logoakhor",blogpinevert="logpinevert",blogpinehor="logpinehor",
			blogbirchvert="logbirchvert",blogbirchhor="logbirchhor",blogjunglevert="logjunglevert",blogjunglehor="logjunglehor",

			bstonestairGuess="stonestairGuess",bladderGuess="ladderGuess",bsignwallGuess="signwallGuess",

			bwoodstairsOakGuess="woodstairsOakGuess",
			
			bwoodstairsOakTop="woodstairsOakTop",bwoodstairsOakBottom="woodstairsOakBottom",bwoodstairsOakLeft="woodstairsOakLeft",bwoodstairsOakRight="woodstairsOakRight",
			bwoodstairsPineTop="woodstairsPineTop",bwoodstairsPineBottom="woodstairsPineBottom",bwoodstairsPineLeft="woodstairsPineLeft",bwoodstairsPineRight="woodstairsPineRight",
			bwoodstairsBirchTop="woodstairsBirchTop",bwoodstairsBirchBottom="woodstairsBirchBottom",bwoodstairsBirchLeft="woodstairsBirchLeft",bwoodstairsBirchRight="woodstairsBirchRight",
			bwoodstairsJungleTop="woodstairsJungleTop",bwoodstairsJungleBottom="woodstairsJungleBottom",bwoodstairsJungleLeft="woodstairsJungleLeft",bwoodstairsJungleRight="woodstairsJungleRight",

			bstonestairsTop="stonestairsTop",bstonestairsBottom="stonestairsBottom",bstonestairsLeft="stonestairsLeft",bstonestairsRight="stonestairsRight",
			bstonebrickstairsTop="stonebrickstairsTop",bstonebrickstairsBottom="stonebrickstairsBottom",bstonebrickstairsLeft="stonebrickstairsLeft",bstonebrickstairsRight="stonebrickstairsRight",
			bbrickstairsTop="brickstairsTop",bbrickstairsBottom="brickstairsBottom",bbrickstairsLeft="brickstairsLeft",bbrickstairsRight="brickstairsRight",
			bsandstonestairsTop="sandstonestairsTop",bsandstonestairsBottom="sandstonestairsBottom",bsandstonestairsLeft="sandstonestairsLeft",bsandstonestairsRight="sandstonestairsRight",



			bwoodstairsOakInvTop="woodstairsOakInvTop",bwoodstairsOakInvBottom="woodstairsOakInvBottom",bwoodstairsOakInvLeft="woodstairsOakInvLeft",bwoodstairsOakInvRight="woodstairsOakInvRight",
			bwoodstairsPineInvTop="woodstairsPineInvTop",bwoodstairsPineInvBottom="woodstairsPineInvBottom",bwoodstairsPineInvLeft="woodstairsPineInvLeft",bwoodstairsPineInvRight="woodstairsPineInvRight",
			bwoodstairsBirchInvTop="woodstairsBirchInvTop",bwoodstairsBirchInvBottom="woodstairsBirchInvBottom",bwoodstairsBirchInvLeft="woodstairsBirchInvLeft",bwoodstairsBirchInvRight="woodstairsBirchInvRight",
			bwoodstairsJungleInvTop="woodstairsJungleInvTop",bwoodstairsJungleInvBottom="woodstairsJungleInvBottom",bwoodstairsJungleInvLeft="woodstairsJungleInvLeft",bwoodstairsJungleInvRight="woodstairsJungleInvRight",
									
			
			
			bstonestairsInvTop="stonestairsInvTop",bstonestairsInvBottom="stonestairsInvBottom",bstonestairsInvLeft="stonestairsInvLeft",bstonestairsInvRight="stonestairsInvRight",
			bstonebrickstairsInvTop="stonebrickstairsInvTop",bstonebrickstairsInvBottom="stonebrickstairsInvBottom",bstonebrickstairsInvLeft="stonebrickstairsInvLeft",bstonebrickstairsInvRight="stonebrickstairsInvRight",
			bbrickstairsInvTop="brickstairsInvTop",bbrickstairsInvBottom="brickstairsInvBottom",bbrickstairsInvLeft="brickstairsInvLeft",bbrickstairsInvRight="brickstairsInvRight",
			bsandstonestairsInvTop="sandstonestairsInvTop",bsandstonestairsInvBottom="sandstonestairsInvBottom",bsandstonestairsInvLeft="sandstonestairsInvLeft",bsandstonestairsInvRight="sandstonestairsInvRight",

			bbyzantinetiles_bottomtop="byzantinetiles_bottomtop",bbyzantinetiles_leftright="byzantinetiles_leftright",
			bbyzantinestonetiles_bottomtop="byzantinestonetiles_bottomtop",bbyzantinestonetiles_leftright="byzantinestonetiles_leftright",
			bbyzantineslab_bottomtop="byzantinetileslab_bottomtop",bbyzantineslab_leftright="byzantinetileslab_leftright",
			bbyzantineslab_bottomtop_inv="byzantinetileslab_bottomtop_inv",bbyzantineslab_leftright_inv="byzantinetileslab_leftright_inv",


			bsignpostTop="signpostTop",bsignpostBottom="signpostBottom",bsignpostLeft="signpostLeft",bsignpostRight="signpostRight",
			bsignwallTop="signwallTop",bsignwallBottom="signwallBottom",bsignwallLeft="signwallLeft",bsignwallRight="signwallRight",
			bladderTop="ladderTop",bladderBottom="ladderBottom",bladderLeft="ladderLeft",bladderRight="ladderRight",
			bcowspawn="cowspawn",bsheepspawn="sheepspawn",bchickenspawn="chickenspawn",bpigspawn="pigspawn",
			bstonesource="stonesource",bsandsource="sandsource",bsandstonesource="sandstonesource",bclaysource="claysource",bgravelsource="gravelsource",bfurnace="furnace",
			bfreestone="freestone",bfreesand="freesand",bfreesandstone="freesandstone",bfreegravel="freegravel",btapestry="tapestry",bstall="stall",bfreewool="freewool",
			bdoorTop="doorTop",bdoorBottom="doorBottom",bdoorRight="doorRight",bdoorLeft="doorLeft",
			birondoorTop="irondoorTop",birondoorBottom="irondoorBottom",birondoorRight="irondoorRight",birondoorLeft="irondoorLeft",
			btrapdoorTop="trapdoorTop",btrapdoorBottom="trapdoorBottom",btrapdoorRight="trapdoorRight",btrapdoorLeft="trapdoorLeft",
			bfenceGateHorizontal="fencegateHorizontal",bfenceGateVertical="fencegateVertical",
			bbedTop="bedTop",bbedBottom="bedBottom",bbedRight="bedRight",bbedLeft="bedLeft",boakspawn="oakspawn",
			bpinespawn="pinespawn",bbirchspawn="birchspawn",bjunglespawn="junglespawn",
			bbrickspot="brickspot",bindianstatue="indianstatue",bmayanstatue="mayanstatue",
			bbyzantineiconsmall="byzantineiconsmall",bbyzantineiconmedium="byzantineiconmedium",bbyzantineiconlarge="byzantineiconlarge",
			bfishingspot="fishingspot",
			bspawnerskeleton="spawnerskeleton",bspawnerzombie="spawnerzombie",bspawnerspider="spawnerspider",
			bspawnercavespider="spawnercavespider",bspawnercreeper="spawnercreeper",bdispenserunknownpowder="dispenserunknownpowder",
			bhealingspot="healingspot",
			bplainSignGuess="plainSignGuess",bbrewingstand="brewingstand";

	public static HashMap<Character,PointType> charPoints;

	public static HashMap<Integer,PointType> colourPoints=new HashMap<Integer,PointType>();

	public static HashMap<Integer,PointType> reverseColourPoints=new HashMap<Integer,PointType>();

	public static final int EAST_FACING=3;

	private static final String EOL = System.getProperty("line.separator");

	public static boolean loadingDone=false;

	public static final int NORTH_FACING=0;

	private static final String prefixWoodstairOak="woodstairsOak",prefixWoodstairPine="woodstairsPine",
			prefixWoodstairBirch="woodstairsBirch",prefixWoodstairJungle="woodstairsJungle",prefixStonestair="stonestair",prefixBrickstair="brickstairs",
			prefixBrickStonestair="stonebrickstairs",prefixSandStoneStair="standstonestairs",prefixLadder="ladder",prefixSign="sign",
			prefixDoor="door",prefixTrapdoor="trapdoor",prefixBed="bed";

	public static final int SOUTH_FACING=2;
	public static final int WEST_FACING=1;
	public static String TYPE_SUBBUILDING="subbuilding";
	public static Point adjustForOrientation(int x, int y,int z, int xoffset, int zoffset,int orientation) {
		Point pos=null;
		if (orientation==0) {
			pos=new Point(x+xoffset, y, z+zoffset);
		} else if (orientation==1) {
			pos=new Point(x+zoffset, y, z-xoffset);
		} else if (orientation==2) {
			pos=new Point(x-xoffset, y, z-zoffset);
		} else if (orientation==3) {
			pos=new Point(x-zoffset, y, z+xoffset);
		}

		return pos;
	}

	//client-side method
	public static void exportBuilding(EntityPlayer player,World world,Point startPoint) {

		try {

			final TileEntitySign sign=startPoint.getSign(world);

			if (sign==null)
				return;

			if ((sign.signText[0]==null) || (sign.signText[0].length()==0)) {
				Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errornoname");
				return;
			}

			final String planName=sign.signText[0];

			int xEnd=startPoint.getiX()+1;
			boolean found=false;
			while (!found && (xEnd<(startPoint.getiX()+257))) {
				final int bid=world.getBlockId(xEnd, startPoint.getiY(), startPoint.getiZ());

				if (bid==Block.signPost.blockID) {
					found=true;
					break;
				}
				xEnd++;
			}

			if (!found) {
				Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errornoendsigneast");
				return;
			}

			int zEnd=startPoint.getiZ()+1;
			found=false;
			while (!found && (zEnd<(startPoint.getiZ()+257))) {
				final int bid=world.getBlockId(startPoint.getiX(), startPoint.getiY(), zEnd);

				if (bid==Block.signPost.blockID) {
					found=true;
					break;
				}
				zEnd++;
			}

			if (!found) {
				Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errornoendsignsouth");
				return;
			}

			final int length=xEnd-startPoint.getiX()-1;
			final int width=zEnd-startPoint.getiZ()-1;

			final File exportDir=new File(Mill.proxy.getCustomDir(),"exports");
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}

			final File buildingFile=new File(exportDir,planName+"_A.txt");

			PointType[][][] existingPoints=null;
			int existingMinLevel=0;
			int upgradeLevel=0;

			if (buildingFile.exists()) {

				final BuildingPlanSet existingSet=new BuildingPlanSet(null,buildingFile.getName().substring(0, buildingFile.getName().length()-6),exportDir);
				existingSet.loadPictPlans(true);

				if (existingSet.plans.firstElement()[0].length!=length) {
					Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errorlength",""+length,""+existingSet.plans.firstElement()[0].length);
					return;
				}
				if (existingSet.plans.firstElement()[0].width!=width) {
					Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errorwidth",""+width,""+existingSet.plans.firstElement()[0].width);
					return;
				}

				existingPoints=existingSet.getConsolidatedPlan(0, existingSet.plans.firstElement().length-1);
				existingMinLevel=existingSet.getMinLevel(0, existingSet.plans.firstElement().length-1);
				upgradeLevel=existingSet.plans.firstElement().length;
			}


			int startLevel=-1;

			if ((sign.signText[2]!=null) && (sign.signText[2].length()>0)) {
				try {
					startLevel=Integer.parseInt(sign.signText[2]);
				} catch (final Exception e) {
					Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errorstartinglevel");
				}
			} else {
				Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.WHITE, "export.defaultstartinglevel");
			}

			boolean exportSnow=false;

			if ((sign.signText[3]!=null) && (sign.signText[3].equals("snow"))) {
				exportSnow=true;
			}

			final Vector<PointType[][]> export=new Vector<PointType[][]>();
			boolean stop=false;

			int j=0;
			while (!stop) {
				final PointType[][] level=new PointType[length][width];

				boolean blockFound=false;

				for (int i=0;i<length;i++) {
					for (int k=0;k<width;k++) {
						level[i][k]=null;

						final int bid=world.getBlockId(i+startPoint.getiX()+1, j+startPoint.getiY()+startLevel, k+startPoint.getiZ()+1);
						final int meta=world.getBlockMetadata(i+startPoint.getiX()+1, j+startPoint.getiY()+startLevel, k+startPoint.getiZ()+1);

						if (bid!=0) {
							blockFound=true;
						}

						final PointType pt=reverseColourPoints.get(MillCommonUtilities.getPointHash(bid, meta));

						if (pt!=null) {

							if (!exportSnow && (pt.blockId!=Block.snow.blockID)) {//snow doesn't get exported as 99% of the case the player did not want it

								//does the block exist in an earlier upgrade?
								PointType existing=null;

								if ((existingPoints!=null) && ((j+startLevel)>=existingMinLevel) &&
										((j+startLevel)<(existingMinLevel+existingPoints.length))) {
									existing=existingPoints[(j+startLevel)-existingMinLevel][i][k];
									if (existing==null) {
										MLN.major(null, "Existing pixel is null");
									}
								}

								if (existing==null) {
									if ((pt.name!=null) || (pt.blockId!=0) || (upgradeLevel!=0)) {
										level[i][k]=pt;
									}
								} else {
									if ((existing!=pt) && !(existing.isType(bempty) && (pt.blockId==0))) {
										level[i][k]=pt;
									}
								}
							}
						} else {
							Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.ORANGE, "export.errorunknownblockid",""+bid+"/"+meta+"/"+MillCommonUtilities.getPointHash(bid, meta));
						}
					}
				}

				if (blockFound) {
					export.add(level);
				} else {
					stop=true;
				}

				j++;

				if ((j+startPoint.getiY()+startLevel)>=256) {
					stop=true;
				}
			}

			final BufferedImage pict=new BufferedImage((export.size()*width)+(export.size()-1)
					,length, BufferedImage.TYPE_INT_RGB);
			final Graphics2D graphics=pict.createGraphics();

			graphics.setColor(new Color(0xB2FFB1));
			graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());

			for (j=0;j<export.size();j++) {
				final PointType[][] level=export.get(j);
				for (int i=0;i<length;i++) {
					for (int k=0;k<width;k++) {
						int colour=0xffffff;
						final PointType pt=level[i][k];
						if (pt!=null) {
							colour=pt.colour;
						}

						graphics.setColor(new Color(colour));
						graphics.fillRect((j*width)+j+(width-k-1), i, 1, 1);
					}
				}
			}

			final String fileName=planName+"_A"+upgradeLevel+".png";

			ImageIO.write(pict, "png", new File(exportDir,fileName));

			if (upgradeLevel==0) {
				final BufferedWriter writer=MillCommonUtilities.getWriter(new File(exportDir,planName+"_A.txt"));

				writer.write("native:nameinvillagelangue;name_en:inenglish;name_fr:enfrancais;around:4;startLevel:"+startLevel+";orientation:3;width:"+width+";length:"+length);
				writer.close();
			} else {
				final BufferedReader reader=MillCommonUtilities.getReader(new File(exportDir,planName+"_A.txt"));

				final Vector<String> existing=new Vector<String>();
				String line=reader.readLine();

				while (line != null) {
					existing.add(line);
					line=reader.readLine();
				}
				reader.close();

				for (int i=existing.size();i<=upgradeLevel;i++) {
					existing.add("");
				}

				existing.insertElementAt("startLevel:"+startLevel, upgradeLevel);

				final BufferedWriter writer=MillCommonUtilities.getWriter(new File(exportDir,planName+"_A.txt"));

				for (final String s : existing) {
					writer.write(s+MLN.EOL);
				}

				writer.close();
			}

			Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(),MLN.WHITE, "export.buildingexported",planName);

		} catch (final Exception e) {
			MLN.printException("Error when trying to store a building: ", e);
		}
	}

	static public void generateBuildingRes() {
		final File file = new File(Mill.proxy.getBaseDir(),"resources used.txt");

		try {
			final BufferedWriter writer=MillCommonUtilities.getWriter(file);


			if (MLN.DEV) {
				generateSignBuildings(writer);
			}


			for (final Culture culture : Culture.vectorCultures) {
				writer.write(culture.key+": "+EOL);
				generateVillageTypeListing(writer,culture.vectorVillageTypes);
				writer.write(EOL);
				generateVillageTypeListing(writer,culture.vectorLoneBuildingTypes);
			}

			writer.write(EOL);
			writer.write(EOL);

			for (final Culture culture : Culture.vectorCultures) {



				for (final BuildingPlanSet set: culture.vectorPlanSets) {

					writer.write(set.plans.get(0)[0].nativeName+EOL+set.plans.get(0)[0].buildingKey+EOL+EOL);
					writer.write("==Requirements=="+EOL);

					for (final BuildingPlan[] plans : set.plans) {
						if (set.plans.size()>1) {
							writer.write("===Variation "+(char)('A'+plans[0].variation)+"==="+EOL);
						}

						for (final BuildingPlan plan : plans) {
							if (plan.level==0) {
								writer.write("Initial Construction"+EOL+EOL);
							} else {
								writer.write("Upgrade "+plan.level+EOL+EOL);
							}

							writer.write("{| border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 300px;\""+EOL);
							writer.write("! scope=\"col\"|Resource"+EOL);
							writer.write("! scope=\"col\"|Quantity"+EOL);
							for (final InvItem key : plan.resCost.keySet()) {
								writer.write("|-"+EOL);
								writer.write("| style=\"text-align: center; \"|"+key.getName()+EOL);
								writer.write("| style=\"text-align: center; \"|"+plan.resCost.get(key)+EOL);
							}
							writer.write("|}"+EOL+EOL+EOL);
						}
					}
				}
			}

			writer.close();

		} catch (final Exception e) {
			MLN.printException(e);
		}

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(null, "Wrote resources used.txt");
		}

	}


	private static void generateSignBuildings(BufferedWriter writer) throws Exception {

		writer.write(EOL+EOL+EOL+"Buildings with signs (not panels):"+EOL+EOL+EOL);

		for (final Culture culture : Culture.vectorCultures) {

			for (final BuildingPlanSet set: culture.vectorPlanSets) {
				for (final BuildingPlan[] plans : set.plans) {
					for (final BuildingPlan plan : plans) {
						if (!plan.tags.contains("hof")) {
							for (PointType[][] level : plan.plan) {
								for (PointType[] row : level) {
									for (PointType pt : row) {
										if (pt!=null && pt.name!=null && pt.name.startsWith(bplainSignGuess)) {
											writer.write("Sign in "+plan.toString()+EOL);
											MLN.temp(null, "Sign in "+plan.toString());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		writer.write(EOL+EOL+EOL);
	}

	static private void generateColourSheet() {

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(null, "Generating colour sheet.");
		}

		final BufferedImage pict=new BufferedImage(200, (colourPoints.size()*20)+25, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics=pict.createGraphics();

		graphics.setColor(new Color(0xffffff));
		graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());

		graphics.setColor(new Color(0));
		graphics.drawString("Generated colour sheet.", 5, 20);

		int pos=1;

		for (final File loadDir : Mill.loadingDirs) {

			final File mainList = new File(loadDir,"blocklist.txt");

			if (mainList.exists()) {
				pos=generateColourSheetHandleFile(graphics,pos,mainList);
			}
		}

		try {
			ImageIO.write(pict, "png", new File(Mill.proxy.getBaseDir(),"Colour Sheet.png"));

		} catch (final Exception e) {
			MLN.printException(e);
		}


		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(null, "Finished generating colour sheet.");
		}
	}

	static private int generateColourSheetHandleFile(Graphics2D graphics, int pos, File file) {

		BufferedReader reader;
		try {
			reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {

					final String[] params=line.split(";", -1);
					final String[] rgb=params[4].split("/", -1);

					final int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);

					graphics.setColor(new Color(0));
					graphics.drawString(params[0], 20, 17+(20*pos));

					graphics.setColor(new Color(colour));
					graphics.fillRect(0, 5+(20*pos), 15, 15);

					pos++;

				}
			}

		} catch (final Exception e) {
			MLN.printException(e);
		}

		return pos;
	}

	static private void generateVillageTypeListing(BufferedWriter writer, Vector<VillageType> villages) throws IOException {
		for (final VillageType villageType : villages) {

			final HashMap<InvItem,Integer> cultureRes=new HashMap<InvItem,Integer>();

			for (final Vector<BuildingProject> projects : villageType.getBuildingProjects()) {
				for (final BuildingProject project : projects) {
					for (final BuildingPlan[] plans : project.planSet.plans) {
						for (final BuildingPlan plan : plans) {
							for (final InvItem key : plan.resCost.keySet()) {
								if (cultureRes.containsKey(key)) {
									cultureRes.put(key, cultureRes.get(key)+plan.resCost.get(key));
								} else {
									cultureRes.put(key, plan.resCost.get(key));
								}
							}
						}
					}
				}
			}

			writer.write(villageType.key+" resource use: "+EOL);
			for (final InvItem key : cultureRes.keySet()) {
				writer.write(key.getName()+": "+cultureRes.get(key)+EOL);
			}
			writer.write(EOL);
		}
	}

	static private String getColourString(int colour) {
		return ((colour & 0xff0000) >> 16)+"/"+((colour & 0x00ff00) >> 8)+"/"+((colour & 0x0000ff) >> 0)+"/"+Integer.toHexString(colour);
	}
	public static void importBuilding(EntityPlayer player,World world,Point startPoint) {

		try {

			final TileEntitySign sign=startPoint.getSign(world);

			if (sign==null)
				return;

			if ((sign.signText[0]==null) || (sign.signText[0].length()==0)) {
				ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"import.errornoname");
				return;
			}

			final String planName=sign.signText[0];

			final File exportDir=new File(Mill.proxy.getCustomDir(),"exports");
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}

			final File buildingFile=new File(exportDir,planName+"_A.txt");

			if (!buildingFile.exists()) {
				ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"import.errornotfound");
				return;
			}

			final BuildingPlanSet existingSet=new BuildingPlanSet(null,buildingFile.getName().substring(0, buildingFile.getName().length()-6),exportDir);
			existingSet.loadPictPlans(true);

			int upgradeLevel=0;

			if ((sign.signText[1]!=null) && (sign.signText[1].length()>0)) {
				try {
					upgradeLevel=Integer.parseInt(sign.signText[1]);
					ServerSender.sendTranslatedSentence(player,MLN.WHITE,"import.buildingupto",""+upgradeLevel);
				} catch (final Exception e) {
					ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"import.errorinvalidupgradelevel");
					return;
				}
			} else {
				ServerSender.sendTranslatedSentence(player,MLN.WHITE,"import.buildinginitialphase");
			}

			if (upgradeLevel>=existingSet.plans.firstElement().length) {
				ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"import.errorupgradeleveltoohigh");
				return;
			}

			if ((sign.signText[2]!=null) && (sign.signText[2].equals("x2"))) {
				for (final BuildingPlan[] plans : existingSet.plans) {
					for (final BuildingPlan plan : plans) {
						final PointType[][][] newPlan=new PointType[plan.plan.length*2][plan.plan[0].length][plan.plan[0][0].length];

						for (int i=0;i<plan.plan.length;i++) {
							for (int j=0;j<plan.plan[0].length;j++) {
								for (int k=0;k<plan.plan[0][0].length;k++) {
									newPlan[i*2][j][k]=plan.plan[i][j][k];
									newPlan[(i*2)+1][j][k]=plan.plan[i][j][k];
								}
							}
						}

						plan.plan=newPlan;
						plan.nbfloors*=2;
					}
				}
				ServerSender.sendTranslatedSentence(player,MLN.WHITE,"import.doublevertical");
				MLN.major(null, "Building height: "+existingSet.plans.get(0)[0].plan.length);
			}


			final BuildingPlan plan=existingSet.plans.firstElement()[0];
			final BuildingLocation location=new BuildingLocation(plan, startPoint.getRelative((plan.length/2)+1, 0, (plan.width/2)+1), 0);

			for (int i=0;i<=upgradeLevel;i++) {
				ServerSender.sendTranslatedSentence(player,MLN.WHITE,"import.buildinglevel",""+i);
				existingSet.buildLocation(Mill.getMillWorld(world),null, location, true, false, null, true,null);
				location.level++;
			}

		} catch (final Exception e) {
			MLN.printException("Error when importing a building:", e);
		}
	}

	public static boolean loadBuildingPoints() {

		for (final File loadDir : Mill.loadingDirs) {

			final File mainList = new File(loadDir,"blocklist.txt");

			if (mainList.exists()) {
				if (loadBuildingPointsFile(mainList))
					return true;//error
			}
		}

		loadReverseBuildingPoints();

		if (MLN.generateColourSheet) {
			generateColourSheet();
		}

		return false;
	}

	private static boolean loadBuildingPointsFile(File file) {

		try {

			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line=reader.readLine()) != null) {
				if ((line.trim().length() > 0) && !line.startsWith("//")) {
					final PointType cp=PointType.readColourPoint(line);
					for (final PointType cp2 : colourPoints.values()) {
						if (cp2.colour==cp.colour)
							throw new MillenaireException("Colour "+getColourString(cp.colour)+" in line <"+line+"> is already taken.");
					}
					colourPoints.put(cp.colour, cp);
				}
			}
		} catch (final Exception e) {
			MLN.printException(e);
			return true;
		}


		charPoints=new HashMap<Character,PointType>();
		charPoints.put('-', new PointType('-',bempty));
		charPoints.put('*', new PointType('*',bmainchest));
		charPoints.put('G', new PointType('G',bgrass));
		charPoints.put('s', new PointType('s',bsoil));
		charPoints.put('H', new PointType('H',blockedchest));
		charPoints.put('X', new PointType('X',bsleepingPos));
		charPoints.put('t', new PointType('t',bwoodstairsOakGuess));
		charPoints.put('a', new PointType('a',bstonestairGuess));
		charPoints.put('L', new PointType('L',bladderGuess));
		charPoints.put('S', new PointType('S',bsignwallGuess));


		charPoints.put('/', new PointType('/',0,0,false));
		charPoints.put('d', new PointType('d',Block.dirt.blockID,0,false));
		charPoints.put('p', new PointType('p',Block.planks.blockID,0,false));
		charPoints.put('g', new PointType('g',Block.glass.blockID,0,false));
		charPoints.put('c', new PointType('c',Block.cobblestone.blockID,0,false));
		charPoints.put('C', new PointType('C',Block.workbench.blockID,0,false));
		charPoints.put('F', new PointType('F',Block.furnaceIdle.blockID,0,false));
		charPoints.put('W', new PointType('W',Block.cloth.blockID,0,false));
		charPoints.put('o', new PointType('o',Block.stone.blockID,0,false));
		charPoints.put('h', new PointType('h',Block.stairCompactCobblestone.blockID,0,false));
		charPoints.put('I', new PointType('I',Block.blockSteel.blockID,0,false));
		charPoints.put('l', new PointType('h',Block.stoneSingleSlab.blockID,0,false));
		charPoints.put('T', new PointType('T',Block.torchWood.blockID,0,true));
		charPoints.put('f', new PointType('f',Block.fence.blockID,0,true));
		charPoints.put('w', new PointType('w',Block.waterStill.blockID,0,true));
		return false;
	}
	public static HashMap<String,BuildingPlanSet> loadPlans(Vector<File> culturesDirs,Culture culture) {

		final HashMap<String,BuildingPlanSet> plans=new HashMap<String,BuildingPlanSet>();

		final Vector<File> dirs=new Vector<File>();
		final Vector<Boolean> isolatedDirs=new Vector<Boolean>();

		for (final File cultureDir : culturesDirs) {

			final File buildingsDir=new File(cultureDir,"buildings");

			final File coreDir=new File(buildingsDir,"core");
			final File extraDir=new File(buildingsDir,"extra");
			final File isolatedDir=new File(buildingsDir,"lone");

			if (coreDir.exists()) {
				dirs.add(coreDir);
				isolatedDirs.add(false);
			}

			if (extraDir.exists()) {
				dirs.add(extraDir);
				isolatedDirs.add(false);
			}

			if (isolatedDir.exists()) {
				dirs.add(isolatedDir);
				isolatedDirs.add(true);
			}

		}

		final File customDir=new File(new File(new File(Mill.proxy.getCustomDir(),"cultures"),culture.key),"custom buildings");

		if (customDir.exists()) {
			dirs.add(customDir);
			isolatedDirs.add(false);
		}

		final BuildingFileFiler textPlans=new BuildingFileFiler("_A0.txt");
		final BuildingFileFiler pictPlans=new BuildingFileFiler("_A.txt");

		for (int i=0;i<dirs.size();i++) {

			for (final File file : dirs.get(i).listFiles(textPlans)) {

				try {
					if (MLN.LogBuildingPlan>=MLN.MAJOR) {
						MLN.major(file, "Loading building: "+file.getAbsolutePath());
					}

					final BuildingPlanSet set=new BuildingPlanSet(culture,file.getName().split("_")[0],dirs.get(i));
					set.loadPlans(culture,false);
					if (isolatedDirs.get(i)) {
						set.max=0;
					}
					plans.put(set.key,set);
				} catch (final Exception e) {
					MLN.printException("Error when loading "+file.getAbsolutePath(),e);
				}
			}

			for (final File file : dirs.get(i).listFiles(pictPlans)) {
				try {
					if (MLN.LogBuildingPlan>=MLN.MAJOR) {
						MLN.major(file, "Loading pict building: "+file.getAbsolutePath());
					}

					final BuildingPlanSet set=new BuildingPlanSet(culture,file.getName().substring(0, file.getName().length()-6),dirs.get(i));
					set.loadPictPlans(false);
					if (isolatedDirs.get(i)) {
						set.max=0;
					}
					plans.put(set.key,set);
				} catch (final Exception e) {
					MLN.printException("Exception when loading "+file.getName()+" plan set in culture "+culture.key+":",e);
				}
			}

		}

		return plans;
	}
	public static HashMap<String,BuildingPlanSet> loadPlanSetMap(Vector<BuildingPlanSet> planSets) {

		final HashMap<String,BuildingPlanSet> map=new HashMap<String,BuildingPlanSet>();

		for (final BuildingPlanSet set : planSets) {
			map.put(set.key, set);
		}

		return map;
	}
	private static void loadReverseBuildingPoints() {

		//First all the "normal" blocks:
		for (final PointType pt : colourPoints.values()) {
			if (pt.name==null) {
				reverseColourPoints.put(MillCommonUtilities.getPointHash(pt.blockId, pt.meta), pt);

				if ((pt.blockId==Block.torchWood.blockID) ||
						(pt.blockId==Block.torchRedstoneIdle.blockID) || (pt.blockId==Block.leaves.blockID)) {
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(pt.blockId, i), pt);
					}
				}

			}
		}

		//Then the special ones (so they can override normal ones)
		for (final PointType pt : colourPoints.values()) {
			if (pt.name!=null) {
				if (pt.name.equals(bpreserveground)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.dirt.blockID, 0), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.grass.blockID, 0), pt);
				}  else if (pt.name.equals(blockedchest)) {
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.chest.blockID, i), pt);
					}
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.lockedChest.blockID, i), pt);
					}


				}  else if (pt.name.equals(blogoakhor)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 8), pt);
				}  else if (pt.name.equals(blogoakvert)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 4), pt);

				}  else if (pt.name.equals(blogpinehor)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 8+1), pt);
				}  else if (pt.name.equals(blogpinevert)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 4+1), pt);

				}  else if (pt.name.equals(blogbirchhor)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 8+2), pt);
				}  else if (pt.name.equals(blogbirchvert)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 4+2), pt);

				}  else if (pt.name.equals(blogjunglehor)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 8+3), pt);
				}  else if (pt.name.equals(blogjunglevert)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.wood.blockID, 4+3), pt);



					//Regular stairs:
				}  else if (pt.name.equals(bwoodstairsOakTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 1), pt);
				}  else if (pt.name.equals(bwoodstairsOakBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 0), pt);
				}  else if (pt.name.equals(bwoodstairsOakLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 2), pt);
				}  else if (pt.name.equals(bwoodstairsOakRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 3), pt);
					
				}  else if (pt.name.equals(bwoodstairsPineTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 1), pt);
				}  else if (pt.name.equals(bwoodstairsPineBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 0), pt);
				}  else if (pt.name.equals(bwoodstairsPineLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 2), pt);
				}  else if (pt.name.equals(bwoodstairsPineRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 3), pt);
					
				}  else if (pt.name.equals(bwoodstairsBirchTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 1), pt);
				}  else if (pt.name.equals(bwoodstairsBirchBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 0), pt);
				}  else if (pt.name.equals(bwoodstairsBirchLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 2), pt);
				}  else if (pt.name.equals(bwoodstairsBirchRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 3), pt);
					
				}  else if (pt.name.equals(bwoodstairsJungleTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 1), pt);
				}  else if (pt.name.equals(bwoodstairsJungleBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 0), pt);
				}  else if (pt.name.equals(bwoodstairsJungleLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 2), pt);
				}  else if (pt.name.equals(bwoodstairsJungleRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 3), pt);

				}  else if (pt.name.equals(bstonestairsTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 1), pt);
				}  else if (pt.name.equals(bstonestairsBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 0), pt);
				}  else if (pt.name.equals(bstonestairsLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 2), pt);
				}  else if (pt.name.equals(bstonestairsRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 3), pt);

				}  else if (pt.name.equals(bstonebrickstairsTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 1), pt);
				}  else if (pt.name.equals(bstonebrickstairsBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 0), pt);
				}  else if (pt.name.equals(bstonebrickstairsLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 2), pt);
				}  else if (pt.name.equals(bstonebrickstairsRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 3), pt);

				}  else if (pt.name.equals(bbrickstairsTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 1), pt);
				}  else if (pt.name.equals(bbrickstairsBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 0), pt);
				}  else if (pt.name.equals(bbrickstairsLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 2), pt);
				}  else if (pt.name.equals(bbrickstairsRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 3), pt);

				}  else if (pt.name.equals(bsandstonestairsTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 1), pt);
				}  else if (pt.name.equals(bsandstonestairsBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 0), pt);
				}  else if (pt.name.equals(bsandstonestairsLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 2), pt);
				}  else if (pt.name.equals(bsandstonestairsRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 3), pt);

					//Inversed stairs:
				}  else if (pt.name.equals(bwoodstairsOakInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 5), pt);
				}  else if (pt.name.equals(bwoodstairsOakInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 4), pt);
				}  else if (pt.name.equals(bwoodstairsOakInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 6), pt);
				}  else if (pt.name.equals(bwoodstairsOakInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactPlanks.blockID, 7), pt);
					
				}  else if (pt.name.equals(bwoodstairsPineInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 5), pt);
				}  else if (pt.name.equals(bwoodstairsPineInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 4), pt);
				}  else if (pt.name.equals(bwoodstairsPineInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 6), pt);
				}  else if (pt.name.equals(bwoodstairsPineInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodSpruce.blockID, 7), pt);
					
				}  else if (pt.name.equals(bwoodstairsBirchInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 5), pt);
				}  else if (pt.name.equals(bwoodstairsBirchInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 4), pt);
				}  else if (pt.name.equals(bwoodstairsBirchInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 6), pt);
				}  else if (pt.name.equals(bwoodstairsBirchInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodBirch.blockID, 7), pt);
					
				}  else if (pt.name.equals(bwoodstairsJungleInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 5), pt);
				}  else if (pt.name.equals(bwoodstairsJungleInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 4), pt);
				}  else if (pt.name.equals(bwoodstairsJungleInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 6), pt);
				}  else if (pt.name.equals(bwoodstairsJungleInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsWoodJungle.blockID, 7), pt);

				}  else if (pt.name.equals(bstonestairsInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 5), pt);
				}  else if (pt.name.equals(bstonestairsInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 4), pt);
				}  else if (pt.name.equals(bstonestairsInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 6), pt);
				}  else if (pt.name.equals(bstonestairsInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairCompactCobblestone.blockID, 7), pt);

				}  else if (pt.name.equals(bstonebrickstairsInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 5), pt);
				}  else if (pt.name.equals(bstonebrickstairsInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 4), pt);
				}  else if (pt.name.equals(bstonebrickstairsInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 6), pt);
				}  else if (pt.name.equals(bstonebrickstairsInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsStoneBrickSmooth.blockID, 7), pt);

				}  else if (pt.name.equals(bbrickstairsInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 5), pt);
				}  else if (pt.name.equals(bbrickstairsInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 4), pt);
				}  else if (pt.name.equals(bbrickstairsInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 6), pt);
				}  else if (pt.name.equals(bbrickstairsInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsBrick.blockID, 7), pt);

				}  else if (pt.name.equals(bsandstonestairsInvTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 5), pt);
				}  else if (pt.name.equals(bsandstonestairsInvBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 4), pt);
				}  else if (pt.name.equals(bsandstonestairsInvLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 6), pt);
				}  else if (pt.name.equals(bsandstonestairsInvRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.stairsSandStone.blockID, 7), pt);


				}  else if (pt.name.equals(bbyzantinetiles_bottomtop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tiles.blockID, 1), pt);
				}  else if (pt.name.equals(bbyzantinetiles_leftright)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tiles.blockID, 0), pt);
				}  else if (pt.name.equals(bbyzantinestonetiles_bottomtop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_stone_tiles.blockID, 1), pt);
				}  else if (pt.name.equals(bbyzantinestonetiles_leftright)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_stone_tiles.blockID, 0), pt);

				}  else if (pt.name.equals(bbyzantineslab_bottomtop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tile_slab.blockID, 1), pt);
				}  else if (pt.name.equals(bbyzantineslab_leftright)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tile_slab.blockID, 0), pt);
				}  else if (pt.name.equals(bbyzantineslab_bottomtop_inv)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tile_slab.blockID, 9), pt);
				}  else if (pt.name.equals(bbyzantineslab_leftright_inv)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Mill.byzantine_tile_slab.blockID, 8), pt);


				}  else if (pt.name.equals(bsignpostTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signPost.blockID, 5), pt);
				}  else if (pt.name.equals(bsignpostBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signPost.blockID, 4), pt);
				}  else if (pt.name.equals(bsignpostLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signPost.blockID, 2), pt);
				}  else if (pt.name.equals(bsignpostRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signPost.blockID, 3), pt);

				}  else if (pt.name.equals(bsignwallTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signWall.blockID, 5), pt);
				}  else if (pt.name.equals(bsignwallBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signWall.blockID, 4), pt);
				}  else if (pt.name.equals(bsignwallLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signWall.blockID, 2), pt);
				}  else if (pt.name.equals(bsignwallRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signWall.blockID, 3), pt);

				}  else if (pt.name.equals(bladderTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.ladder.blockID, 5), pt);
				}  else if (pt.name.equals(bladderBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.ladder.blockID, 4), pt);
				}  else if (pt.name.equals(bladderLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.ladder.blockID, 2), pt);
				}  else if (pt.name.equals(bladderRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.ladder.blockID, 3), pt);

				}  else if (pt.name.equals(bfurnace)) {
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.furnaceIdle.blockID, i), pt);
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.furnaceBurning.blockID, i), pt);
					}
				}  else if (pt.name.equals(bbrewingstand)) {
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.brewingStand.blockID, i), pt);
					}
				}  else if (pt.name.equals(bdoorTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 0), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 7), pt);
				}  else if (pt.name.equals(bdoorBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 2), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 5), pt);
				}  else if (pt.name.equals(bdoorLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 3), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 6), pt);
				}  else if (pt.name.equals(bdoorRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 1), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorWood.blockID, 4), pt);

				}  else if (pt.name.equals(birondoorTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 0), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 7), pt);
				}  else if (pt.name.equals(birondoorBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 2), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 5), pt);
				}  else if (pt.name.equals(birondoorLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 3), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 6), pt);
				}  else if (pt.name.equals(birondoorRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 1), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.doorSteel.blockID, 4), pt);

				}  else if (pt.name.equals(btrapdoorTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.trapdoor.blockID, 1), pt);
				}  else if (pt.name.equals(btrapdoorBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.trapdoor.blockID, 0), pt);
				}  else if (pt.name.equals(btrapdoorLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.trapdoor.blockID, 3), pt);
				}  else if (pt.name.equals(btrapdoorRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.trapdoor.blockID, 2), pt);




				}  else if (pt.name.equals(bfenceGateHorizontal)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 1), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 3), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 5), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 7), pt);
				}  else if (pt.name.equals(bfenceGateVertical)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 0), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 2), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 4), pt);
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.fenceGate.blockID, 6), pt);


				}  else if (pt.name.equals(bbedTop)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.bed.blockID, 8), pt);
				}  else if (pt.name.equals(bbedBottom)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.bed.blockID, 10), pt);
				}  else if (pt.name.equals(bbedLeft)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.bed.blockID, 9), pt);
				}  else if (pt.name.equals(bbedRight)) {
					reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.bed.blockID, 11), pt);

				}	else if (pt.name.equals(bplainSignGuess)) {
					for (int i=0;i<16;i++) {
						reverseColourPoints.put(MillCommonUtilities.getPointHash(Block.signWall.blockID, i), pt);
					}
				}
			}
		}

	}
	public int areaToClear,firstLevel,length,width,nbfloors,lengthOffset,widthOffset,buildingOrientation;
	public String nativeName,  shop;
	public final HashMap<String,String> names=new HashMap<String,String>();
	public Vector<String> maleResident=new Vector<String>();
	public Vector<String> femaleResident=new Vector<String>();
	public boolean isUpdate=false;
	public int level;
	public int max, priority,reputation,price,priorityMoveIn;
	public float minDistance,maxDistance;
	public String requiredTag=null;
	public int nbBlocksToPut=0;
	public PointType[][][] plan=null;
	public String planName="",buildingKey,type=null;
	public HashMap<InvItem,Integer> resCost;
	public int[] signOrder;
	public Vector<String> tags;
	public int variation;
	public Vector<String> subBuildings;
	public Vector<String> startingSubBuildings;
	public boolean showTownHallSigns=true;
	public Vector<StartingGood> startingGoods;
	public String exploreTag=null;
	public int irrigation=0;
	public Culture culture;

	public BuildingPlan parent;

	public BuildingPlan(File dir, String buildingKey,int level,int variation,BuildingPlan parent,Culture c,boolean importPlan) throws Exception {

		char varChar='A';
		varChar+=variation;
		planName=buildingKey+"_"+varChar+""+level;
		this.buildingKey=buildingKey;
		this.isUpdate=(level > 0);
		this.level=level;
		this.variation=variation;
		culture=c;

		final File file = new File(dir,planName+".txt");

		initialiseConfig(parent);

		final BufferedReader reader = MillCommonUtilities.getReader(file);

		String line = null;

		line=reader.readLine();

		readConfigLine(file,line,importPlan);

		line=reader.readLine();//skipping blank line before building

		final Vector<Vector<String>> textPlan=new Vector<Vector<String>>();

		Vector<String> v=new Vector<String>();
		textPlan.add(v);

		while ((line=reader.readLine()) != null) {
			if (line.trim().equals("")) {
				v=new Vector<String>();
				textPlan.add(v);
			} else {
				v.add(line);
			}
		}

		length=textPlan.elementAt(0).size();
		lengthOffset=(int)Math.floor(length*0.5);
		width=textPlan.elementAt(0).elementAt(0).length();
		widthOffset=(int)Math.floor(width*0.5);
		nbfloors=textPlan.size();

		int i=0;
		for (final Vector<String> floor : textPlan) {
			if (floor.size() != length)
				throw new MillenaireException(planName+": "+"Floor "+i+" is "+floor.size()+" long, "+length+" expected.");

			int j=0;
			for (final String s : floor) {

				if (s.length() != width)
					throw new MillenaireException(planName+": "+"Line "+j+" in floor "+i+" is "+s.length()+" wide, "+width+" expected.");

				j++;
			}
			i++;
		}

		reader.close();

		plan=new PointType[nbfloors][length][width];

		for (i=0;i<textPlan.size();i++) {
			for (int j=0;j<length;j++) {
				for (int k=0;k<width;k++) {
					if (!charPoints.containsKey(textPlan.get(i).get(j).charAt(width-k-1)))
						throw new MillenaireException(planName+": In floor "+i+" line "+j+" char "+(width-k-1)+" unknow character: "+textPlan.get(i).get(j).charAt(width-k-1));
					plan[i][j][k]=charPoints.get(textPlan.get(i).get(j).charAt((width-k-1)));
					if (plan[i][j][k]==null)
						throw new MillenaireException(planName+": In floor "+i+" line "+j+" char "+(width-k-1)+" null PointType for: "+textPlan.get(i).get(j).charAt(width-k-1));
				}
			}
		}

		computeCost();

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(this, "Loaded plan "+buildingKey+"_"+level+": "+nativeName+" pop: "+maleResident+"/"+femaleResident+" / priority: "+priority);
		}
	}

	public BuildingPlan(File dir, String buildingKey,int level,int variation, BuildingPlan parent, String configLine,Culture c,boolean importPlan) throws Exception {

		char varChar='A';
		varChar+=variation;
		planName=buildingKey+"_"+varChar+""+level;
		this.buildingKey=buildingKey;
		this.isUpdate=(level > 0);
		this.level=level;
		this.variation=variation;
		culture=c;

		initialiseConfig(parent);

		final File file = new File(dir,planName+".png");

		if (configLine != null) {
			readConfigLine(file,configLine,importPlan);
		}

		final BufferedImage PNGFile=ImageIO.read(file);

		final BufferedImage pictPlan=new BufferedImage(PNGFile.getWidth(),PNGFile.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D fig = pictPlan.createGraphics();
		fig.drawImage(PNGFile, 0, 0, null);
		fig.dispose();
		pictPlan.flush();

		lengthOffset=(int)Math.floor(length*0.5);
		widthOffset=(int)Math.floor(width*0.5);

		if (pictPlan.getHeight() != length)
			throw new MillenaireException(planName+": "+"Expected length is "+length+" but file height is "+pictPlan.getHeight());

		final float fnbfloors = (pictPlan.getWidth() + 1f) / (width + 1f);

		if (Math.round(fnbfloors) != fnbfloors)
			throw new MillenaireException(planName+": With a width of "+width+", getting non-integer floor number: "+fnbfloors);

		nbfloors=(int)fnbfloors;

		plan=new PointType[nbfloors][length][width];

		if ((pictPlan.getType() != BufferedImage.TYPE_3BYTE_BGR) && (pictPlan.getType() != BufferedImage.TYPE_4BYTE_ABGR)) {
			MLN.error(this, "Picture "+planName+".png could not be loaded as type TYPE_3BYTE_BGR or TYPE_4BYTE_ABGR but instead as: "+pictPlan.getType());
		}

		boolean alphaLayer=false;

		if (pictPlan.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			alphaLayer=true;
		}

		boolean sleepingPos=false,mainChest=false;;

		for (int i=0;i<nbfloors;i++) {
			for (int j=0;j<length;j++) {
				for (int k=0;k<width;k++) {

					int colour=pictPlan.getRGB((i*width)+i+(width-k-1), j);

					if (alphaLayer) {
						if ((colour & 0xFF000000) != 0xff000000) {
							//MLLog.major(this, MLLog.BuildingPlan, "Discarding pixel "+(i*width+i+(width-k-1))+"/"+j+" with colour "+getColourString(colour)+" and alpha "+Integer.toHexString(colour & 0xFF000000));
							colour = 0x00FFFFFF;//white, empty pixel
						}
						else {
							colour = colour & 0x00FFFFFF;//discard alpha layer
						}
					} else {
						colour = colour & 0x00FFFFFF;
					}


					if (!colourPoints.containsKey(colour)) {
						MLN.error(this, planName+": Unknown colour "+getColourString(colour)+" at: "+((i*width)+i+(width-k-1))+"/"+j+", skipping it.");
						colour = 0x00FFFFFF;//white, empty pixel
					}

					plan[i][j][k]=colourPoints.get(colour);

					if (bsleepingPos.equals(plan[i][j][k].name)) {
						sleepingPos=true;
					}

					if (bmainchest.equals(plan[i][j][k].name)) {
						mainChest=true;
					}

					if ((plan[i][j][k].name!=null) && plan[i][j][k].name.equals(bmainchest) && (level>0)) {
						MLN.error(this, "Main chest detected at "+((i*width)+i+(width-k-1))+"/"+j+" but we are in an upgrade. Removing it.");
						plan[i][j][k]=colourPoints.get(0x00FFFFFF);
					}
				}
			}
		}


		computeCost();

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(this, "Loaded plan "+buildingKey+"_"+level+": "+nativeName+" pop: "+maleResident+"/"+femaleResident+"/priority:"+priority);
		}

		if ((maleResident.size()>0) && (femaleResident.size()>0) && (level==0) && !sleepingPos) {
			MLN.error(this, "Has residents but the sleeping pos is missing!");
		}

		if (!mainChest && (level==0) && ((maleResident.size()>0) || (femaleResident.size()>0) || (tags.size()>0))) {
			MLN.error(this, "Has residents or tags but the mainchest is missing!");
		}

		int pigs=0,sheep=0,chicken=0,cow=0;

		for (int i=0;i<nbfloors;i++) {
			for (int j=0;j<length;j++) {
				for (int k=0;k<width;k++) {
					if (plan[i][j][k].isType(bchickenspawn) && !tags.contains("chicken")) {
						MLN.warning(this, "Building has chicken spawn but no chicken tag.");
					} else if (plan[i][j][k].isType(bcowspawn) && !tags.contains("cattle")) {
						MLN.warning(this, "Building has cattle spawn but no cattle tag.");
					} else if (plan[i][j][k].isType(bsheepspawn) && !tags.contains("sheeps")) {
						MLN.warning(this, "Building has sheeps spawn but no sheeps tag.");
					} else if (plan[i][j][k].isType(bpigspawn) && !tags.contains("pigs")) {
						MLN.warning(this, "Building has pig spawn but no pig tag.");
					}
					if (plan[i][j][k].isType(bchickenspawn)) {
						chicken++;
					} else if (plan[i][j][k].isType(bcowspawn)) {
						cow++;
					} else if (plan[i][j][k].isType(bsheepspawn)) {
						sheep++;
					} else if (plan[i][j][k].isType(bpigspawn)) {
						pigs++;
					}
				}
			}
		}

		if (chicken%2==1) {
			MLN.warning(this, "Odd number of chicken spawn: "+chicken);
		}
		if (sheep%2==1) {
			MLN.warning(this, "Odd number of sheep spawn: "+sheep);
		}
		if (cow%2==1) {
			MLN.warning(this, "Odd number of cow spawn: "+cow);
		}
		if (pigs%2==1) {
			MLN.warning(this, "Odd number of pigs spawn: "+pigs);
		}

	}

	public BuildingPlan(String buildingKey,int level,int variation,Culture c) {
		this.buildingKey=buildingKey;
		this.isUpdate=(level > 0);
		this.level=level;
		this.variation=variation;
		culture=c;
	}

	private void addToCost(int id,int nb) {
		addToCost(id,0,nb);
	}

	private void addToCost(int id,int meta,int nb) {
		final InvItem key=new InvItem(id,meta);

		if (resCost.containsKey(key)) {
			nb+=resCost.get(key);
			resCost.put(key, nb);
		} else {
			resCost.put(key, nb);
		}
	}

	public Vector<LocationBuildingPair> build(MillWorld mw,VillageType villageType, BuildingLocation location, boolean villageGeneration, boolean townHall, Point townHallPos, boolean wandimport,EntityPlayer owner,boolean rushBuilding) {

		if (!townHall && (townHallPos==null) && !wandimport) {
			MLN.error(this, "Building is not TH and does not have TH's position.");
		}

		final World worldObj=mw.world;

		final Vector<LocationBuildingPair> buildings=new Vector<LocationBuildingPair>();

		final BuildingBlock[] bblocks=getBuildingPoints(worldObj, location, villageGeneration);


		for (final BuildingBlock bblock : bblocks) {
			bblock.build(worldObj,villageGeneration,wandimport);
		}

		if (tags.contains(Building.tagHoF)) {

			int signNb=0;
			final Vector<String> hofData=MLN.getHoFData();

			for (int z=location.pos.getiZ()-(width/2);z<(location.pos.getiZ()+(width/2));z++) {
				for (int x=location.pos.getiX()+(length/2);x>=(location.pos.getiX()-(length/2));x--) {

					for (int y=location.pos.getiY()+plan.length;y>=location.pos.getiY();y--) {
						if (worldObj.getBlockId(x, y, z)==Block.signWall.blockID) {
							final TileEntitySign sign=new Point(x,y,z).getSign(worldObj);
							if (sign!=null) {
								if (signNb<hofData.size()) {
									final String[] lines=hofData.get(signNb).split(";");
									for (int i=0;i<Math.min(4, lines.length);i++) {
										//first line is guy's name, so not translated
										if ((i==0) || (lines[i].length()==0)) {
											sign.signText[i]=lines[i];
										} else {
											sign.signText[i]=MLN.string(lines[i]);
										}

									}
								}
								signNb++;
							}
						}
					}
				}
			}
		}

		if ((bblocks.length>0) && (bblocks[bblocks.length-1].bid == Mill.lockedChest.blockID) && !wandimport) {

			final Building building=new Building(mw, culture, villageType, location, townHall, villageGeneration, bblocks[bblocks.length-1].p, townHallPos);

			if (MLN.WorldGeneration>=MLN.MINOR) {
				MLN.minor(this, "Building "+planName+" at "+location);
			}

			referenceBuildingPoints(worldObj, building,location);
			building.initialise(owner,villageGeneration || rushBuilding);

			building.fillStartingGoods();

			buildings.add(new LocationBuildingPair(building,location));

			//filling it for starting sub-buildings
			if (townHall) {
				townHallPos=building.getPos();
			}
		}

		if (culture!=null) {

			for (final String sb : startingSubBuildings) {
				final BuildingPlan plan=culture.getBuildingPlanSet(sb).getRandomStartingPlan();
				final BuildingLocation l = location.createLocationForStartingSubBuilding(sb);
				final Vector<LocationBuildingPair> vb=plan.build(mw,villageType, l, villageGeneration, false, townHallPos, false, owner,rushBuilding);
				location.subBuildings.add(sb);

				for (final LocationBuildingPair p : vb) {
					buildings.add(p);
				}
			}
		}

		worldObj.markBlockRangeForRenderUpdate(	location.pos.getiX()-(length/2)-5, location.pos.getiY()-plan.length-5, location.pos.getiZ()-(width/2)-5,
				location.pos.getiX()+(length/2)+5, location.pos.getiY()-5, location.pos.getiZ()+(width/2)+5);

		return buildings;

	}



	private void computeCost() throws MillenaireException {

		resCost=new HashMap<InvItem,Integer>();

		int plankCost=0,plankOakCost=0,plankPineCost=0,plankBirchCost=0,plankJungleCost=0,glassPaneCost=0,byzBricksHalf=0;

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final PointType p=plan[i][j][k];

					if (p==null)
						throw new MillenaireException("PointType null at "+i+"/"+j+"/"+k);

					if ((p.blockId==Block.wood.blockID) && ((p.meta & 3)==0)) {
						plankOakCost+=4;
					} else if ((p.blockId==Block.wood.blockID) && ((p.meta & 3)==1)) {
						plankPineCost+=4;
					} else if ((p.blockId==Block.wood.blockID) && ((p.meta & 3)==2)) {
						plankBirchCost+=4;
					} else if ((p.blockId==Block.wood.blockID) && ((p.meta & 3)==3)) {
						plankJungleCost+=4;
					} else if ((p.blockId==Block.planks.blockID) && (p.meta==0)) {
						plankOakCost++;
					} else if ((p.blockId==Block.planks.blockID) && (p.meta==1)) {
						plankPineCost++;
					} else if ((p.blockId==Block.planks.blockID) && (p.meta==2)) {
						plankBirchCost++;
					} else if ((p.blockId==Block.planks.blockID) && (p.meta==3)) {
						plankJungleCost++;

					} else if (p.blockId==Mill.byzantine_tiles.blockID) {
						byzBricksHalf+=2;
					} else if (p.blockId==Mill.byzantine_tile_slab.blockID) {
						byzBricksHalf++;
					} else if (p.blockId==Mill.byzantine_stone_tiles.blockID) {
						byzBricksHalf++;
						addToCost(Block.stone.blockID,1);

					} else if (p.blockId==Block.thinGlass.blockID) {
						glassPaneCost++;
					} else if (p.blockId==Block.workbench.blockID) {
						plankCost+=4;
					} else if (p.blockId==Block.chest.blockID) {
						plankCost+=8;
					} else if (p.blockId==Block.furnaceIdle.blockID) {
						addToCost(Block.cobblestone.blockID,8);
					} else if (p.blockId==Block.torchWood.blockID) {
						plankCost++;
					} else if (p.blockId==Block.fence.blockID) {
						plankCost++;
					} else if (p.blockId==Block.fenceGate.blockID) {
						plankCost+=4;
					} else if (p.blockId==Block.pressurePlatePlanks.blockID) {
						plankCost+=2;
					} else if (p.blockId==Block.pressurePlateStone.blockID) {
						addToCost(Block.stone.blockID,2);
					} else if ((p.blockId==Block.stoneBrick.blockID) && (p.meta==0)) {//only normal stone bricks are auto-crafted from stone
						addToCost(Block.stone.blockID,1);
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==0)) {
						addToCost(Block.stone.blockID,1);
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==1)) {
						addToCost(Block.sandStone.blockID,1);
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==2)) {
						plankCost++;
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==3)) {
						addToCost(Block.cobblestone.blockID,1);
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==4)) {
						addToCost(Block.brick.blockID,1);
					} else if ((p.blockId==Block.stoneSingleSlab.blockID) && ((p.meta & 7)==5)) {
						addToCost(Block.stone.blockID,1);

					} else if ((p.blockId==Block.woodSingleSlab.blockID) && ((p.meta & 7)==0)) {
						plankOakCost++;
					} else if ((p.blockId==Block.woodSingleSlab.blockID) && ((p.meta & 7)==1)) {
						plankPineCost++;
					} else if ((p.blockId==Block.woodSingleSlab.blockID) && ((p.meta & 7)==2)) {
						plankBirchCost++;
					} else if ((p.blockId==Block.woodSingleSlab.blockID) && ((p.meta & 7)==3)) {
						plankJungleCost++;

					} else if ((p.blockId==Block.cloth.blockID)) {
						addToCost(Block.cloth.blockID,1);


					} else if (p.blockId==Block.stoneDoubleSlab.blockID) {
						addToCost(Block.stone.blockID,1);
					} else if (p.blockId==Block.blockSteel.blockID) {
						addToCost(Item.ingotIron.itemID,9);
					} else if (p.blockId==Block.fenceIron.blockID) {
						addToCost(Item.ingotIron.itemID,1);
					} else if (p.blockId==Block.blockGold.blockID) {
						addToCost(Item.ingotGold.itemID,9);
					} else if (p.blockId==Block.cauldron.blockID) {
						addToCost(Item.ingotIron.itemID,7);
					} else if (p.blockId==Block.cobblestoneWall.blockID) {
						addToCost(Block.cobblestone.blockID,1);
					} else if (p.isType(blockedchest)) {
						plankCost+=8;
					} else if (p.isType(bfurnace)) {
						addToCost(Block.cobblestone.blockID,8);
					} else if (p.isType(bmainchest)) {
						plankCost+=8;
					} else if (p.isSubType(prefixWoodstairOak)) {
						plankOakCost+=2;
					} else if (p.isSubType(prefixWoodstairPine)) {
						plankPineCost+=2;
					} else if (p.isSubType(prefixWoodstairBirch)) {
						plankBirchCost+=2;
					} else if (p.isSubType(prefixWoodstairJungle)) {
						plankJungleCost+=2;
					} else if (p.isSubType(prefixStonestair)) {
						addToCost(Block.cobblestone.blockID,2);
					} else if (p.isSubType(prefixBrickStonestair)) {
						addToCost(Block.stone.blockID,2);
					} else if (p.isSubType(prefixSandStoneStair)) {
						addToCost(Block.sandStone.blockID,2);
					} else if (p.isSubType(prefixBrickstair)) {
						addToCost(Block.brick.blockID,2);
					} else if (p.isSubType(prefixLadder)) {
						plankCost+=2;
					} else if (p.isSubType(prefixSign)) {
						plankCost+=7;
					} else if (p.isSubType(prefixDoor)) {
						plankCost+=6;
					} else if (p.isSubType(prefixTrapdoor)) {
						plankCost+=6;
					} else if (p.isSubType(prefixBed)) {
						plankCost+=3;
						addToCost(Block.cloth.blockID,0,3);
					} else if (p.isType(btapestry)) {
						addToCost(Mill.tapestry.itemID,1);
					} else if (p.isType(bindianstatue)) {
						addToCost(Mill.indianstatue.itemID,1);
					} else if (p.isType(bmayanstatue)) {
						addToCost(Mill.mayanstatue.itemID,1);
					} else if (p.isType(bbyzantineiconsmall)) {
						addToCost(Mill.byzantineiconsmall.itemID,1);
					} else if (p.isType(bbyzantineiconmedium)) {
						addToCost(Mill.byzantineiconmedium.itemID,1);
					} else if (p.isType(bbyzantineiconmedium)) {
						addToCost(Mill.byzantineiconmedium.itemID,1);
					} else if (p.isType(bsilkwormblock)) {
						plankCost+=4;

					} else if (p.isType(bbyzantinetiles_bottomtop)) {
						byzBricksHalf+=2;
					} else if (p.isType(bbyzantinetiles_leftright)) {
						byzBricksHalf+=2;

					} else if (p.isType(bbyzantinestonetiles_bottomtop)) {
						byzBricksHalf++;
						addToCost(Block.stone.blockID,1);
					} else if (p.isType(bbyzantinestonetiles_leftright)) {
						byzBricksHalf++;
						addToCost(Block.stone.blockID,1);

					} else if (p.isType(bbyzantineslab_bottomtop)) {
						byzBricksHalf++;
					} else if (p.isType(bbyzantineslab_leftright)) {
						byzBricksHalf++;
					} else if (p.isType(bbyzantineslab_bottomtop_inv)) {
						byzBricksHalf++;
					} else if (p.isType(bbyzantineslab_leftright_inv)) {
						byzBricksHalf++;


					} else if ((p.blockId > 0) && !Goods.freeGoods.contains(new InvItem(p.blockId,p.meta))
							&& !Goods.freeGoods.contains(new InvItem(p.blockId,-1))) {
						addToCost(p.blockId,p.meta,1);
					}
				}
			}
		}


		if (plankCost > 0) {
			addToCost(Block.wood.blockID,-1,(int)Math.max(Math.ceil((plankCost*1.0)/4),1));
		}

		if (plankOakCost > 0) {
			addToCost(Block.wood.blockID,0,(int)Math.max(Math.ceil((plankOakCost*1.0)/4),1));
		}

		if (plankPineCost > 0) {
			addToCost(Block.wood.blockID,1,(int)Math.max(Math.ceil((plankPineCost*1.0)/4),1));
		}

		if (plankBirchCost > 0) {
			addToCost(Block.wood.blockID,2,(int)Math.max(Math.ceil((plankBirchCost*1.0)/4),1));
		}

		if (plankJungleCost > 0) {
			addToCost(Block.wood.blockID,3,(int)Math.max(Math.ceil((plankJungleCost*1.0)/4),1));
		}

		if (glassPaneCost > 0) {
			addToCost(Block.glass.blockID,-1,(int)Math.max(Math.ceil((glassPaneCost*6.0)/16),1));
		}


		if (byzBricksHalf > 0) {
			addToCost(Mill.byzantine_tiles.blockID,-1,(int)Math.max(Math.ceil(byzBricksHalf/2),1));
		}

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(this, "Loaded plan for "+planName+".");
		}

	}

	public BuildingLocation findBuildingLocation(MillWorldInfo winfo,AStarPathing pathing,Point centre, int maxRadius,Random random,int porientation) {

		final long startTime = System.nanoTime();

		final int ci=centre.getiX()-winfo.mapStartX;
		final int cj=centre.getiZ()-winfo.mapStartZ;


		int radius=(int) (maxRadius*minDistance);
		maxRadius=(int) (maxRadius*maxDistance);

		if (MLN.WorldGeneration>=MLN.MAJOR) {
			MLN.major(this, "testBuildWorldInfo: Called to test for building "+planName+" around "+centre+"("+ci+"/"+cj+"), start radius: "+radius+", max radius: "+maxRadius);
		}

		for (int i=0;i<winfo.length;i++) {
			for (int j=0;j<winfo.width;j++) {
				winfo.buildTested[i][j]=false;
			}
		}

		while (radius<maxRadius) {

			final int mini=Math.max(0, ci-radius);
			final int maxi=Math.min(winfo.length-1, ci+radius);
			final int minj=Math.max(0, cj-radius);
			final int maxj=Math.min(winfo.width-1, cj+radius);

			if (MLN.WorldGeneration>=MLN.DEBUG) {
				MLN.debug(this, "Testing square: "+mini+"/"+minj+" to "+maxi+"/"+maxj);
			}


			for (int i=mini;i<maxi;i++) {
				if ((cj-radius)==minj) {
					final LocationReturn lr=testSpot(winfo,pathing,centre,i,minj,random,porientation);

					if (lr.location!=null) {
						if (MLN.LogBuildingPlan>=MLN.MINOR) {
							MLN.minor(this,"Time taken for location search: "+(((double)(System.nanoTime()-startTime))/1000000));
						}
						return lr.location;
					}
				}
				if ((cj+radius)==maxj) {
					final LocationReturn lr=testSpot(winfo,pathing,centre,i,maxj,random,porientation);
					if (lr.location!=null) {
						if (MLN.LogBuildingPlan>=MLN.MINOR) {
							MLN.minor(this,"Time taken for location search: "+(((double)(System.nanoTime()-startTime))/1000000));
						}
						return lr.location;
					}
				}
			}

			for (int j=minj;j<maxj;j++) {
				if ((ci-radius)==mini) {
					final LocationReturn lr=testSpot(winfo,pathing,centre,mini,j,random,porientation);
					if (lr.location!=null) {
						if (MLN.LogBuildingPlan>=MLN.MINOR) {
							MLN.minor(this,"Time taken for location search: "+(((double)(System.nanoTime()-startTime))/1000000));
						}
						return lr.location;
					}
				}
				if ((ci+radius)==maxi) {
					final LocationReturn lr=testSpot(winfo,pathing,centre,maxi,j,random,porientation);
					if (lr.location!=null) {
						if (MLN.LogBuildingPlan>=MLN.MINOR) {
							MLN.minor(this,"Time taken for location search: "+(((double)(System.nanoTime()-startTime))/1000000));
						}
						return lr.location;
					}
				}
			}

			radius++;
		}

		if (MLN.WorldGeneration>=MLN.MAJOR) {
			MLN.major(this, "Could not find acceptable location (radius: "+radius+")");
		}


		if (MLN.LogBuildingPlan>=MLN.MINOR) {
			MLN.minor(this,"Time taken for unsuccessful location search: "+(((double)(System.nanoTime()-startTime))/1000000));
		}

		return null;
	}

	private int getBedMeta(int direction,int orientation) {
		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 1;
		else if (faces==1)
			return 2;
		else if (faces==2)
			return 3;
		else//faces north
			return 0;
	}

	public int getBlock(World worldObj,Point p) {
		return MillCommonUtilities.getBlock(worldObj, p);
	}



	public BuildingBlock[] getBuildingPoints(World world,BuildingLocation location,boolean villageGeneration) {

		final int x=location.pos.getiX();
		final int y=location.pos.getiY();
		final int z=location.pos.getiZ();
		final int orientation=location.orientation;

		nbBlocksToPut=0;

		//"guess" of the size needed
		final int approximateBlocks=(length+(areaToClear*2))*(width+(areaToClear*2))*(nbfloors+50);

		final Vector<BuildingBlock> bblocks=new Vector<BuildingBlock>(approximateBlocks,100);

		if (MLN.WorldGeneration>=MLN.MINOR) {
			MLN.minor(this, "Getting blocks for "+planName+" at "+x+"/"+y+"/"+z+"/"+orientation);
		}

		if (!isUpdate && !TYPE_SUBBUILDING.equals(type) && !location.bedrocklevel) {
			//filling above ground area with air

			for (int j =  -areaToClear;j<(length+areaToClear);j++) {
				for (int k = -areaToClear;k<(width+areaToClear);k++) {
					for (int i = nbfloors+50;i>-1;i--) {
						final int ak = ((j % 2) == 0) ? k : width-k-1;

						//how far from building we are in the margin (0=in building itself)
						int offset=0;

						if (j<0) {
							offset=-j;
						} else if (j>=(length-1)) {
							offset=(j-length)+1;
						}
						if ((ak<0) && (-ak>offset)) {
							offset=-ak;
						} else if ((ak>=(width-1)) && (((ak-width)+1)>offset)) {
							offset=(ak-width)+1;
						}

						offset--;

						if (i>=(offset-1)) {//for each block away from building, one extra height allowed
							final Point p=adjustForOrientation(x, y+i, z,j-lengthOffset,ak-widthOffset,orientation);
							bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.CLEARGROUND));
						} else {
							final Point p=adjustForOrientation(x, y+i, z,j-lengthOffset,k-widthOffset,orientation);
							bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.CLEARTREE));
						}
					}
				}
			}

			//filling foundations with soil

			for (int j =  -areaToClear;j<(length+areaToClear);j++) {
				for (int k = -areaToClear;k<(width+areaToClear);k++) {
					for (int i = -10+firstLevel;i<0;i++) {
						final int ak = ((j % 2) == 0) ? k : width-k-1;

						//how far from building we are in the margin (0=in building itself)
						int offset=0;

						if (j<0) {
							offset=-j;
						} else if (j>=(length-1)) {
							offset=(j-length)+1;
						}
						if ((ak<0) && (-ak>offset)) {
							offset=-ak;
						} else if ((ak>=(width-1)) && (((ak-width)+1)>offset)) {
							offset=(ak-width)+1;
						}

						offset--;

						if (-i>=offset) {
							final Point p=adjustForOrientation(x, y+i, z,j-lengthOffset,k-widthOffset,orientation);
							bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.PRESERVEGROUND));
							nbBlocksToPut++;
						} else {
							final Point p=adjustForOrientation(x, y+i, z,j-lengthOffset,k-widthOffset,orientation);
							bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.CLEARTREE));
						}
					}
				}
			}
		}

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final PointType pt=plan[i][j][k];
					if (pt.isType(bpreserveground)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.PRESERVEGROUND));
						nbBlocksToPut++;
					} else if (pt.isType(ballbuttrees)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.CLEARTREE));
						nbBlocksToPut++;
					}
				}
			}
		}

		//Starting with deletion
		for (int i = nbfloors-1;i>=0;i--) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final int ak = ((j % 2) == 0) ? k : width-k-1;

					final PointType pt=plan[i][j][ak];

					final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,ak-widthOffset,orientation);

					if (pt.blockId==0) {
						bblocks.add(new BuildingBlock(p,0,0));
					}
				}
			}
		}

		//the building itself
		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {

					final int ak = ((j % 2) == 0) ? k : width-k-1;
					final int ai = ((i+firstLevel)<0) ? -i-firstLevel-1 : i;


					final PointType pt=plan[ai][j][ak];
					int b=-1,m=0;

					final Point p=adjustForOrientation(x, y+ai+firstLevel, z,j-lengthOffset,ak-widthOffset,orientation);

					if ((pt.blockId > 0) && !pt.secondStep) {//standard block
						b=pt.blockId;
						m=pt.meta;
					} else if (pt.isType(bempty) && !isUpdate && !TYPE_SUBBUILDING.equals(type)) {
						b=0;
					} else if (pt.isType(bgrass) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bgrass) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bsoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bricesoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bturmericsoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bmaizesoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bcarrotsoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bpotatosoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bsugarcanesoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bvinesoil) && villageGeneration) {
						b=Block.grass.blockID;
					} else if (pt.isType(bsoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bricesoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bturmericsoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bmaizesoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bpotatosoil) && villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bcarrotsoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bsugarcanesoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bvinesoil) && !villageGeneration) {
						b=Block.dirt.blockID;
					} else if (pt.isType(bnetherwartsoil)) {
						b=Block.slowSand.blockID;
					} else if (pt.isType(bsilkwormblock)) {
						b=Mill.wood_decoration.blockID;
						m=3;
					} else if (pt.isType(blockedchest)) {
						b=Mill.lockedChest.blockID;
					} else if (pt.isType(bfurnace)) {
						b=Block.furnaceIdle.blockID;
					} else if (pt.isType(bbrewingstand)) {
						b=Block.brewingStand.blockID;


					} else if (pt.isType(blogoakhor)) {
						b=Block.wood.blockID;
						m=getWoodMeta(0,orientation);
					} else if (pt.isType(blogoakvert)) {
						b=Block.wood.blockID;
						m=getWoodMeta(1,orientation);

					} else if (pt.isType(blogpinehor)) {
						b=Block.wood.blockID;
						m=1+getWoodMeta(0,orientation);
					} else if (pt.isType(blogpinevert)) {
						b=Block.wood.blockID;
						m=1+getWoodMeta(1,orientation);

					} else if (pt.isType(blogbirchhor)) {
						b=Block.wood.blockID;
						m=2+getWoodMeta(0,orientation);
					} else if (pt.isType(blogbirchvert)) {
						b=Block.wood.blockID;
						m=2+getWoodMeta(1,orientation);

					} else if (pt.isType(blogjunglehor)) {
						b=Block.wood.blockID;
						m=3+getWoodMeta(0,orientation);
					} else if (pt.isType(blogjunglevert)) {
						b=Block.wood.blockID;
						m=3+getWoodMeta(1,orientation);


					} else if (pt.isType(bwoodstairsOakTop)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bwoodstairsOakRight)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bwoodstairsOakBottom)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bwoodstairsOakLeft)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(3,orientation);
						
					} else if (pt.isType(bwoodstairsPineTop)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bwoodstairsPineRight)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bwoodstairsPineBottom)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bwoodstairsPineLeft)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(3,orientation);
						
					} else if (pt.isType(bwoodstairsBirchTop)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bwoodstairsBirchRight)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bwoodstairsBirchBottom)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bwoodstairsBirchLeft)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(3,orientation);
						
					} else if (pt.isType(bwoodstairsJungleTop)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bwoodstairsJungleRight)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bwoodstairsJungleBottom)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bwoodstairsJungleLeft)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(3,orientation);
						
						
						
						
						
					}  else if (pt.isType(bstonestairsTop)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bstonestairsRight)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bstonestairsBottom)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bstonestairsLeft)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(3,orientation);
					}  else if (pt.isType(bstonebrickstairsTop)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bstonebrickstairsRight)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bstonebrickstairsBottom)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bstonebrickstairsLeft)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(3,orientation);
					}  else if (pt.isType(bbrickstairsTop)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bbrickstairsRight)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bbrickstairsBottom)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bbrickstairsLeft)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(3,orientation);
					}  else if (pt.isType(bsandstonestairsTop)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(0,orientation);
					} else if (pt.isType(bsandstonestairsRight)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(1,orientation);
					} else if (pt.isType(bsandstonestairsBottom)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(2,orientation);
					} else if (pt.isType(bsandstonestairsLeft)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(3,orientation);



					} else if (pt.isType(bwoodstairsOakInvTop)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bwoodstairsOakInvRight)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bwoodstairsOakInvBottom)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bwoodstairsOakInvLeft)) {
						b=Block.stairCompactPlanks.blockID;
						m=getStairMeta(3,orientation)+4;
						
					} else if (pt.isType(bwoodstairsPineInvTop)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bwoodstairsPineInvRight)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bwoodstairsPineInvBottom)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bwoodstairsPineInvLeft)) {
						b=Block.stairsWoodSpruce.blockID;
						m=getStairMeta(3,orientation)+4;
						
					} else if (pt.isType(bwoodstairsBirchInvTop)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bwoodstairsBirchInvRight)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bwoodstairsBirchInvBottom)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bwoodstairsBirchInvLeft)) {
						b=Block.stairsWoodBirch.blockID;
						m=getStairMeta(3,orientation)+4;
						
					} else if (pt.isType(bwoodstairsJungleInvTop)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bwoodstairsJungleInvRight)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bwoodstairsJungleInvBottom)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bwoodstairsJungleInvLeft)) {
						b=Block.stairsWoodJungle.blockID;
						m=getStairMeta(3,orientation)+4;
						
						
						
						
					}  else if (pt.isType(bstonestairsInvTop)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bstonestairsInvRight)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bstonestairsInvBottom)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bstonestairsInvLeft)) {
						b=Block.stairCompactCobblestone.blockID;
						m=getStairMeta(3,orientation)+4;
					}  else if (pt.isType(bstonebrickstairsInvTop)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bstonebrickstairsInvRight)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bstonebrickstairsInvBottom)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bstonebrickstairsInvLeft)) {
						b=Block.stairsStoneBrickSmooth.blockID;
						m=getStairMeta(3,orientation)+4;
					}  else if (pt.isType(bbrickstairsInvTop)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bbrickstairsInvRight)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bbrickstairsInvBottom)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bbrickstairsInvLeft)) {
						b=Block.stairsBrick.blockID;
						m=getStairMeta(3,orientation)+4;
					}  else if (pt.isType(bsandstonestairsInvTop)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(0,orientation)+4;
					} else if (pt.isType(bsandstonestairsInvRight)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(1,orientation)+4;
					} else if (pt.isType(bsandstonestairsInvBottom)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(2,orientation)+4;
					} else if (pt.isType(bsandstonestairsInvLeft)) {
						b=Block.stairsSandStone.blockID;
						m=getStairMeta(3,orientation)+4;

					} else if (pt.isType(bbyzantinetiles_bottomtop)) {
						b=Mill.byzantine_tiles.blockID;
						m=getOrientedBlockMeta(0,orientation);
					} else if (pt.isType(bbyzantinetiles_leftright)) {
						b=Mill.byzantine_tiles.blockID;
						m=getOrientedBlockMeta(1,orientation);
					} else if (pt.isType(bbyzantinestonetiles_bottomtop)) {
						b=Mill.byzantine_stone_tiles.blockID;
						m=getOrientedBlockMeta(0,orientation);
					} else if (pt.isType(bbyzantinestonetiles_leftright)) {
						b=Mill.byzantine_stone_tiles.blockID;
						m=getOrientedBlockMeta(1,orientation);

					} else if (pt.isType(bbyzantineslab_bottomtop)) {
						b=Mill.byzantine_tile_slab.blockID;
						m=getOrientedBlockMeta(0,orientation);
					} else if (pt.isType(bbyzantineslab_leftright)) {
						b=Mill.byzantine_tile_slab.blockID;
						m=getOrientedBlockMeta(1,orientation);
					} else if (pt.isType(bbyzantineslab_bottomtop_inv)) {
						b=Mill.byzantine_tile_slab.blockID;
						m=getOrientedBlockMeta(0,orientation)+8;
					} else if (pt.isType(bbyzantineslab_leftright_inv)) {
						b=Mill.byzantine_tile_slab.blockID;
						m=getOrientedBlockMeta(1,orientation)+8;



					} else if (pt.isType(bsignpostTop)) {
						b=Block.signPost.blockID;
						m=getSignOrLadderMeta(0,orientation);
					} else if (pt.isType(bsignpostRight)) {
						b=Block.signPost.blockID;
						m=getSignOrLadderMeta(1,orientation);
					} else if (pt.isType(bsignpostBottom)) {
						b=Block.signPost.blockID;
						m=getSignOrLadderMeta(2,orientation);
					} else if (pt.isType(bsignpostLeft)) {
						b=Block.signPost.blockID;
						m=getSignOrLadderMeta(3,orientation);
					} else if (pt.isType(bsleepingPos)) {
						b=0;
						location.sleepingPos=p;
					} else if (pt.isType(bsellingPos)) {
						b=0;
						location.sellingPos=p;
					} else if (pt.isType(bcraftingPos)) {
						b=0;
						location.craftingPos=p;
					} else if (pt.isType(bshelterPos)) {
						b=0;
						location.shelterPos=p;
					} else if (pt.isType(bdefendingPos)) {
						b=0;
						location.defendingPos=p;
					} else if (pt.isType(bsandsource)) {
						b=Block.sand.blockID;
					} else if (pt.isType(bsandstonesource)) {
						b=Block.sandStone.blockID;
					} else if (pt.isType(bclaysource)) {
						b=Block.blockClay.blockID;
					} else if (pt.isType(bgravelsource)) {
						b=Block.gravel.blockID;
					} else if (pt.isType(bstonesource)) {
						b=Block.stone.blockID;
					} else if (pt.isType(bfreesand)) {
						b=Block.sand.blockID;
					} else if (pt.isType(bfreesandstone)) {
						b=Block.sandStone.blockID;
					} else if (pt.isType(bfreegravel)) {
						b=Block.gravel.blockID;
					} else if (pt.isType(bfreewool)) {
						b=Block.cloth.blockID;
					} else if (pt.isType(bfreestone)) {
						b=Block.stone.blockID;
					}


					if (b >-1) {
						bblocks.add(new BuildingBlock(p,b,m));
						nbBlocksToPut++;
					}

				}
			}
		}

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {

					final int ak = ((j % 2) == 0) ? k : width-k-1;
					final int ai = ((i+firstLevel)<0) ? -i-firstLevel-1 : i;

					final PointType pt=plan[ai][j][ak];
					int b=-1,m=0;
					final Point p=adjustForOrientation(x, y+ai+firstLevel, z,j-lengthOffset,ak-widthOffset,orientation);

					if ((pt.blockId != -1) && pt.secondStep) {//standard block
						b=pt.blockId;
						m=pt.meta;
					} else if (pt.isType(bwoodstairsOakGuess)) {
						b=Block.stairCompactPlanks.blockID;
						m=-1;
					} else if (pt.isType(bstonestairGuess)) {
						b=Block.stairCompactCobblestone.blockID;
						m=-1;
					} else if (pt.isType(bladderGuess)) {
						b=Block.ladder.blockID;
						m=-1;
					} else if (pt.isType(bsignwallGuess)) {
						b=Mill.panel.blockID;
						m=guessSignMeta(bblocks,p);
					} else if (pt.isType(bplainSignGuess)) {
						b=Block.signWall.blockID;
						m=guessSignMeta(bblocks,p);
					} else if (pt.isType(bsignwallTop)) {
						b=Mill.panel.blockID;
						m=getSignOrLadderMeta(0,orientation);
					} else if (pt.isType(bsignwallRight)) {
						b=Mill.panel.blockID;
						m=getSignOrLadderMeta(3,orientation);
					} else if (pt.isType(bsignwallBottom)) {
						b=Mill.panel.blockID;
						m=getSignOrLadderMeta(2,orientation);
					} else if (pt.isType(bsignwallLeft)) {
						b=Mill.panel.blockID;
						m=getSignOrLadderMeta(1,orientation);
					} else if (pt.isType(bladderTop)) {
						b=Block.ladder.blockID;
						m=getSignOrLadderMeta(0,orientation);
					} else if (pt.isType(bladderRight)) {
						b=Block.ladder.blockID;
						m=getSignOrLadderMeta(3,orientation);
					} else if (pt.isType(bladderBottom)) {
						b=Block.ladder.blockID;
						m=getSignOrLadderMeta(2,orientation);
					} else if (pt.isType(bladderLeft)) {
						b=Block.ladder.blockID;
						m=getSignOrLadderMeta(1,orientation);
					} else if (pt.isType(bdoorTop)) {
						b=Block.doorWood.blockID;
						m=getDoorMeta(0,orientation);
					} else if (pt.isType(bdoorRight)) {
						b=Block.doorWood.blockID;
						m=getDoorMeta(1,orientation);
					} else if (pt.isType(bdoorBottom)) {
						b=Block.doorWood.blockID;
						m=getDoorMeta(2,orientation);
					} else if (pt.isType(bdoorLeft)) {
						b=Block.doorWood.blockID;
						m=getDoorMeta(3,orientation);

					} else if (pt.isType(birondoorTop)) {
						b=Block.doorSteel.blockID;
						m=getDoorMeta(0,orientation);
					} else if (pt.isType(birondoorRight)) {
						b=Block.doorSteel.blockID;
						m=getDoorMeta(1,orientation);
					} else if (pt.isType(birondoorBottom)) {
						b=Block.doorSteel.blockID;
						m=getDoorMeta(2,orientation);
					} else if (pt.isType(birondoorLeft)) {
						b=Block.doorSteel.blockID;
						m=getDoorMeta(3,orientation);

					} else if (pt.isType(btrapdoorTop)) {
						b=Block.trapdoor.blockID;
						m=getTrapdoorMeta(0,orientation);
					} else if (pt.isType(btrapdoorRight)) {
						b=Block.trapdoor.blockID;
						m=getTrapdoorMeta(1,orientation);
					} else if (pt.isType(btrapdoorBottom)) {
						b=Block.trapdoor.blockID;
						m=getTrapdoorMeta(2,orientation);
					} else if (pt.isType(btrapdoorLeft)) {
						b=Block.trapdoor.blockID;
						m=getTrapdoorMeta(3,orientation);


					} else if (pt.isType(bfenceGateHorizontal)) {
						b=Block.fenceGate.blockID;
						m=getFenceGateMeta(0,orientation);
					} else if (pt.isType(bfenceGateVertical)) {
						b=Block.fenceGate.blockID;
						m=getFenceGateMeta(1,orientation);


					} else if (pt.isType(bbedTop)) {
						b=Block.bed.blockID;
						m=getBedMeta(0,orientation)+8;
					} else if (pt.isType(bbedRight)) {
						b=Block.bed.blockID;
						m=getBedMeta(1,orientation)+8;
					} else if (pt.isType(bbedBottom)) {
						b=Block.bed.blockID;
						m=getBedMeta(2,orientation)+8;
					} else if (pt.isType(bbedLeft)) {
						b=Block.bed.blockID;
						m=getBedMeta(3,orientation)+8;
					}

					if (b > -1) {
						bblocks.add(new BuildingBlock(p,b,m));
						nbBlocksToPut++;
					}
				}
			}
		}

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final PointType pt=plan[i][j][k];
					if (pt.isType(btapestry)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.TAPESTRY));
						nbBlocksToPut++;
					} else if (pt.isType(bindianstatue)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.INDIANSTATUE));
						nbBlocksToPut++;
					} else if (pt.isType(bmayanstatue)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.MAYANSTATUE));
						nbBlocksToPut++;
					} else if (pt.isType(bbyzantineiconsmall)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.BYZANTINEICONSMALL));
						nbBlocksToPut++;
					} else if (pt.isType(bbyzantineiconmedium)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.BYZANTINEICONMEDIUM));
						nbBlocksToPut++;
					} else if (pt.isType(bbyzantineiconlarge)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.BYZANTINEICONLARGE));
						nbBlocksToPut++;
					} else if (pt.isType(boakspawn)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.OAKSPAWN));
						nbBlocksToPut++;
					} else if (pt.isType(bpinespawn)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.PINESPAWN));
						nbBlocksToPut++;
					} else if (pt.isType(bbirchspawn)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.BIRCHSPAWN));
						nbBlocksToPut++;
					} else if (pt.isType(bjunglespawn)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.JUNGLESPAWN));
						nbBlocksToPut++;
					} else if (pt.isType(bspawnerskeleton)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.SPAWNERSKELETON));
						nbBlocksToPut++;
					} else if (pt.isType(bspawnerzombie)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.SPAWNERZOMBIE));
						nbBlocksToPut++;
					} else if (pt.isType(bspawnerspider)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.SPAWNERSPIDER));
						nbBlocksToPut++;
					} else if (pt.isType(bspawnercavespider)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.SPAWNERCAVESPIDER));
						nbBlocksToPut++;
					} else if (pt.isType(bspawnercreeper)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.SPAWNERCREEPER));
						nbBlocksToPut++;
					} else if (pt.isType(bdispenserunknownpowder)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						bblocks.add(new BuildingBlock(p,0,0,BuildingBlock.DISPENDERUNKNOWNPOWDER));
						nbBlocksToPut++;
					}
				}
			}
		}

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final PointType pt=plan[i][j][k];
					if (pt.isType(bmainchest)) {
						final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);
						location.chestPos=p;
						bblocks.add(new BuildingBlock(p,Mill.lockedChest.blockID,1));
						nbBlocksToPut++;
					}
				}
			}
		}

		setMetadata(bblocks);

		if (location.sleepingPos==null) {
			location.sleepingPos=location.chestPos;
		}

		final HashMap<Point,BuildingBlock> bbmap=new HashMap<Point,BuildingBlock>();

		final boolean[] toDelete=new boolean[bblocks.size()];

		for (int i=0;i<bblocks.size();i++) {
			final BuildingBlock bb=bblocks.get(i);

			int bid;
			int bmeta;
			int special;

			if (bbmap.containsKey(bb.p)) {
				bid=bbmap.get(bb.p).bid;
				bmeta=bbmap.get(bb.p).meta;
				special=bbmap.get(bb.p).special;
			} else {
				bid=MillCommonUtilities.getBlock(world, bb.p);
				bmeta=MillCommonUtilities.getBlockMeta(world, bb.p);
				special=0;
			}

			if ((((bid == bb.bid) && (bmeta == bb.meta) && (special==0))
					|| ((bid == Block.grass.blockID) && (bb.bid == Block.dirt.blockID))) && (bb.special==0)) {
				toDelete[i]=true;
			} else if ((bb.special==BuildingBlock.CLEARTREE) && (bid!=Block.wood.blockID) && (bid!=Block.leaves.blockID)) {
				toDelete[i]=true;
			} else if ((bb.special==BuildingBlock.CLEARGROUND) && (bid==0)) {
				toDelete[i]=true;
			} else if ((bb.special==BuildingBlock.PRESERVEGROUND) && MillCommonUtilities.isBlockIdValidGround(bid)) {
				toDelete[i]=true;
			} else {
				bbmap.put(bb.p, bb);
				toDelete[i]=false;
			}
		}

		for (int i=toDelete.length-1;i>=0;i--) {
			if (toDelete[i]) {
				bblocks.remove(i);
			}
		}


		final BuildingBlock[] abblocks = new BuildingBlock[bblocks.size()];

		for (int i=0;i<bblocks.size();i++) {
			abblocks[i]=bblocks.get(i);
		}

		if (abblocks.length==0) {
			MLN.error(this, "BBlocks size is zero.");
		}

		return abblocks;
	}

	private int getDoorMeta(int direction,int orientation) {
		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 0;
		else if (faces==1)
			return 1;
		else if (faces==2)
			return 2;
		else//faces north
			return 3;
	}

	private int getFenceGateMeta(int direction,int orientation) {
		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 1;
		else if (faces==1)
			return 0;
		else if (faces==2)
			return 1;
		else//faces north
			return 0;
	}

	public String getGameName() {
		if (culture.canReadBuildingNames())
			return culture.getBuildingGameName(this);
		return "";
	}

	public String getGameNameKey() {
		return "_buildingGame:"+culture.key+":"+buildingKey+":"+variation+":"+level;
	}


	public String getNativeDisplayName(EntityPlayer player) {
		String name=nativeName;
		if ((getGameName()!=null) && (getGameName().length()>0)) {
			name+=" ("+getGameName()+")";
		}
		return name;
	}

	private int getOrientedBlockMeta(int direction,int orientation) {

		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 1;
		else if (faces==1)
			return 0;
		else if (faces==2)
			return 1;
		else//faces north
			return 0;
	}

	private int getSignOrLadderMeta(int direction,int orientation) {

		final int faces=(direction+orientation)%4;

		if (faces==0)
			return 5;
		else if (faces==1)
			return 2;
		else if (faces==2)
			return 4;
		else//faces north
			return 3;
	}

	private int getStairMeta(int direction,int orientation) {

		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 1;
		else if (faces==1)
			return 3;
		else if (faces==2)
			return 0;
		else//faces north
			return 2;
	}

	private int getTrapdoorMeta(int direction,int orientation) {
		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 3;
		else if (faces==1)
			return 0;
		else if (faces==2)
			return 2;
		else//faces north
			return 1;
	}

	private int getWoodMeta(int direction,int orientation) {

		final int faces=((direction+4)-orientation)%4;

		if (faces==0)
			return 8;
		else if (faces==1)
			return 4;
		else if (faces==2)
			return 8;
		else//faces north
			return 4;
	}



	private int guessSignMeta(Vector<BuildingBlock> bblocks, Point p) {
		boolean westOpen=true,eastOpen=true,northOpen=true,southOpen=true;
		int m=0;

		final Point west=p.getRelative(-1, 0, 0);
		final Point east=p.getRelative(1, 0, 0);
		final Point south=p.getRelative(0, 0, -1);
		final Point north=p.getRelative(0, 0, 1);

		for (final BuildingBlock block : bblocks) {

			if (block.p.sameBlock(west) && isBlockOpaqueCube(block.bid)) {
				westOpen=false;
			} else if (block.p.sameBlock(east) && isBlockOpaqueCube(block.bid)) {
				eastOpen=false;
			} else if (block.p.sameBlock(south) && isBlockOpaqueCube(block.bid)) {
				southOpen=false;
			} else if (block.p.sameBlock(north) && isBlockOpaqueCube(block.bid)) {
				northOpen=false;
			}
		}

		if (!northOpen) {
			if (MLN.LogBuildingPlan>=MLN.DEBUG) {
				MLN.debug(this, planName+": Putting sign again north wall");
			}
			m=2;
		} else if(!southOpen) {
			if (MLN.LogBuildingPlan>=MLN.DEBUG) {
				MLN.debug(this, planName+": Putting sign again south wall");
			}
			m=3;
		} else if(!eastOpen) {
			if (MLN.LogBuildingPlan>=MLN.DEBUG) {
				MLN.debug(this, planName+": Putting sign again east wall");
			}
			m=4;
		} else if(!westOpen) {
			if (MLN.LogBuildingPlan>=MLN.DEBUG) {
				MLN.debug(this, planName+": Putting sign again west wall");
			}
			m=5;
		} else {
			if (MLN.LogBuildingPlan>=MLN.MAJOR) {
				MLN.major(this, planName+": No idea where to put sign.");
			}
			m=2;
		}
		return m;
	}

	private void initialiseConfig(BuildingPlan parent) {
		if (parent == null) {
			max=1;
			priority=1;
			priorityMoveIn=10;
			nativeName=null;
			areaToClear=1;
			firstLevel=0;
			buildingOrientation=1;
			signOrder=new int[]{0};
			tags=new Vector<String>();
			shop=null;
			minDistance=0;
			maxDistance=1;
			reputation=0;
			price=0;
			subBuildings=new Vector<String>();
			startingSubBuildings=new Vector<String>();
			startingGoods=new Vector<StartingGood>();
			showTownHallSigns=true;
		} else {
			max=parent.max;
			priority=parent.priority;
			priorityMoveIn=parent.priorityMoveIn;
			nativeName=parent.nativeName;
			areaToClear=parent.areaToClear;
			firstLevel=parent.firstLevel;
			buildingOrientation=parent.buildingOrientation;
			signOrder=parent.signOrder;
			tags=new Vector<String>(parent.tags);
			maleResident=parent.maleResident;
			femaleResident=parent.femaleResident;
			shop=parent.shop;
			width=parent.width;
			length=parent.length;
			minDistance=parent.minDistance;
			maxDistance=parent.maxDistance;
			reputation=parent.reputation;
			price=parent.price;
			subBuildings=new Vector<String>(parent.subBuildings);
			startingSubBuildings=new Vector<String>();
			startingGoods=new Vector<StartingGood>();
			this.parent=parent;
			if (MLN.LogBuildingPlan>=MLN.MINOR) {
				String s="";
				for (final String s2 : subBuildings) {
					s+=s2+" ";
				}
				if (s.length()>0) {
					MLN.minor(this, "Copied sub-buildings from parent: "+s);
				}
			}
			showTownHallSigns=parent.showTownHallSigns;
			exploreTag=parent.exploreTag;
			irrigation=parent.irrigation;
		}
	}

	public boolean isBlockOpaqueCube(int blockId)
	{
		return MillCommonUtilities.isBlockOpaqueCube(blockId);
	}

	public boolean isBuildable(int blockId) {
		return ((blockId == 0) || (blockId == Block.leaves.blockID)	 || (blockId == Block.wood.blockID) || (blockId == Block.mushroomBrown.blockID)
				|| (blockId == Block.mushroomRed.blockID)	 || (blockId == Block.plantRed.blockID) || (blockId == Block.plantYellow.blockID));
	}

	public boolean mapIsOpaqueBlock(Map<Point,BuildingBlock>map, Point p) {
		return (map.containsKey(p) && isBlockOpaqueCube(map.get(p).bid));
	}

	public boolean mapIsStairBlock(Map<Point,BuildingBlock>map, Point p) {
		if (!map.containsKey(p))
			return false;

		final int bid=map.get(p).bid;

		return ((bid == Block.stairCompactCobblestone.blockID) || (bid == Block.stairCompactPlanks.blockID));
	}

	private void readConfigLine(File file, String line, boolean importPlan) {

		final String[] configs=line.split(";", -1);

		for (final String config : configs) {
			if (config.split(":").length==2) {
				final String key=config.split(":")[0].toLowerCase();
				final String value=config.split(":")[1];

				if (key.equalsIgnoreCase("max")) {
					max=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("priority")) {
					priority=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("moveinpriority")) {
					priorityMoveIn=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("french") || key.equalsIgnoreCase("native")) {
					nativeName=value;
				} else if (key.equalsIgnoreCase("english") || key.startsWith("name_")) {
					names.put(key, value);
				} else if (key.equalsIgnoreCase("around")) {
					areaToClear=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("startLevel")) {
					firstLevel=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("orientation")) {
					buildingOrientation=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("reputation")) {
					try {
						reputation=MillCommonUtilities.readInteger(value);
					} catch (final Exception e) {
						reputation=0;
						MLN.error(null, "Error when reading reputation line in "+file.getName()+": "+line+" : "+ e.getMessage());
					}
				} else if (key.equalsIgnoreCase("price")) {
					try {
						price=MillCommonUtilities.readInteger(value);
					} catch(final Exception e) {
						price=0;
						MLN.error(this, "Error when reading reputation line in "+file.getName()+": "+line+" : "+ e.getMessage());
					}
				} else if (key.equalsIgnoreCase("length")) {
					length=Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("width")) {
					width=Integer.parseInt(value);
				} else if (!importPlan && key.equalsIgnoreCase("male")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())
							|| MillVillager.oldVillagers.containsKey(value.toLowerCase())) {
						maleResident.add(value.toLowerCase());
					} else {
						MLN.error(this, "Attempted to load unknown male villager: "+value);
					}
				} else if (!importPlan && key.equalsIgnoreCase("female")) {
					if (culture.villagerTypes.containsKey(value.toLowerCase())
							|| MillVillager.oldVillagers.containsKey(value.toLowerCase())) {
						femaleResident.add(value.toLowerCase());
					} else {
						MLN.error(this, "Attempted to load unknown female villager: "+value);
					}
				} else if (key.equalsIgnoreCase("exploretag")) {
					exploreTag=value.toLowerCase();
				} else if (key.equalsIgnoreCase("requiredTag")) {
					requiredTag=value.toLowerCase();
				} else if (key.equalsIgnoreCase("irrigation")) {
					irrigation=Integer.parseInt(value);
				} else if (!importPlan && key.equalsIgnoreCase("shop")) {
					if (culture!=null) {//culture is null only when using the import feature
						if ((culture.shopBuys.containsKey(value) || culture.shopSells.containsKey(value))) {
							shop=value;
						} else {
							MLN.error(this, "Undefined shop type: "+value);
						}
					}
				} else if (key.equalsIgnoreCase("minDistance")) {
					minDistance=Float.parseFloat(value)/100;
				} else if (key.equalsIgnoreCase("maxDistance")) {
					maxDistance=Float.parseFloat(value)/100;
				} else if (key.equalsIgnoreCase("signs")) {
					final String[] temp=value.split(",");
					if (temp[0].length() > 0) {
						signOrder=new int[temp.length];
						for (int i=0;i<temp.length;i++) {
							signOrder[i]=Integer.parseInt(temp[i]);
						}
					}
				} else if (key.equalsIgnoreCase("tag")) {
					tags.add(value);
				} else if (key.equalsIgnoreCase("subbuilding")) {
					subBuildings.add(value);
				} else if (key.equalsIgnoreCase("startingsubbuilding")) {
					startingSubBuildings.add(value);
				} else if (!importPlan && key.equalsIgnoreCase("startinggood")) {
					final String[] temp=value.split(",");
					if (temp.length!=4) {
						MLN.error(this, "Error when reading starting good: expected four fields, found "+temp.length+": "+value);
					} else {

						final String s=temp[0];
						if (!Goods.goodsName.containsKey(s)) {
							MLN.error(this, "Error when reading starting good: unknown good: "+s);
						} else {
							final StartingGood sg=new StartingGood(Goods.goodsName.get(s),Double.parseDouble(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]));
							startingGoods.add(sg);
						}

					}

				} else if (key.equalsIgnoreCase("type")) {
					type=value.toLowerCase();
				} else if (key.equalsIgnoreCase("showtownhallsigns")) {
					showTownHallSigns=Boolean.parseBoolean(value);
				} else if (!importPlan){
					MLN.error(this, "Could not recognise key on line: "+config);
				}
			}
		}

		if (TYPE_SUBBUILDING.equals(type)) {
			max=0;
		}


		if (priority<1) {
			MLN.error(this, "Null or negative weight found in config!");
		}


		if (MLN.LogBuildingPlan>=MLN.DEBUG) {
			String s="";
			for (final String s2 : subBuildings) {
				s+=s2+" ";
			}
			if (s.length()>0) {
				MLN.minor(this, "Sub-buildings after read: "+s);
			}
		}
	}

	public void referenceBuildingPoints(World worldObj, Building building, BuildingLocation location) {

		final int x=location.pos.getiX();
		final int y=location.pos.getiY();
		final int z=location.pos.getiZ();
		final int orientation=location.orientation;

		int signNb=0;
		if (signOrder.length > 0) {
			building.signs.setSize(signOrder.length);
		}

		for (int i = 0;i<nbfloors;i++) {
			for (int j =  0;j<length;j++) {
				for (int k = 0;k<width;k++) {
					final PointType pt=plan[i][j][k];

					final Point p=adjustForOrientation(x, y+i+firstLevel, z,j-lengthOffset,k-widthOffset,orientation);

					if (pt.isType(bsoil)) {
						building.addSoilPoint(Mill.CROP_WHEAT, p);
					} else if (pt.isType(bricesoil)) {
						building.addSoilPoint(Mill.CROP_RICE, p);
					} else if (pt.isType(bturmericsoil)) {
						building.addSoilPoint(Mill.CROP_TURMERIC, p);
					} else if (pt.isType(bmaizesoil)) {
						building.addSoilPoint(Mill.CROP_MAIZE, p);
					} else if (pt.isType(bcarrotsoil)) {
						building.addSoilPoint(Mill.CROP_CARROT, p);
					} else if (pt.isType(bpotatosoil)) {
						building.addSoilPoint(Mill.CROP_POTATO, p);
					} else if (pt.isType(bsugarcanesoil)) {
						if (!building.sugarcanesoils.contains(p)) {
							building.sugarcanesoils.add(p);
						}
					} else if (pt.isType(bnetherwartsoil)) {
						if (!building.netherwartsoils.contains(p)) {
							building.netherwartsoils.add(p);
						}
					} else if (pt.isType(bvinesoil)) {
						building.addSoilPoint(Mill.CROP_VINE, p);
					} else if (pt.isType(bsilkwormblock)) {
						if (!building.silkwormblock.contains(p)) {
							building.silkwormblock.add(p);
						}
					} else if (pt.isType(bstall)) {
						if (!building.stalls.contains(p)) {
							building.stalls.add(p);
						}
					} else if (pt.isType(boakspawn)) {
						if (!building.woodspawn.contains(p)) {
							building.woodspawn.add(p);
						}
					} else if (pt.isType(bpinespawn)) {
						if (!building.woodspawn.contains(p)) {
							building.woodspawn.add(p);
						}
					} else if (pt.isType(bbirchspawn)) {
						if (!building.woodspawn.contains(p)) {
							building.woodspawn.add(p);
						}
					} else if (pt.isType(bjunglespawn)) {
						if (!building.woodspawn.contains(p)) {
							building.woodspawn.add(p);
						}
					} else if (pt.isType(bbrickspot)) {
						if (!building.brickspot.contains(p)) {
							building.brickspot.add(p);
						}
					} else if (pt.isType(bchickenspawn)) {
						building.addSpawnPoint(Mill.ENTITY_CHICKEN, p);
					} else if (pt.isType(bcowspawn)) {
						building.addSpawnPoint(Mill.ENTITY_COW, p);
					} else if (pt.isType(bpigspawn)) {
						building.addSpawnPoint(Mill.ENTITY_PIG, p);
					} else if (pt.isType(bsheepspawn)) {
						building.addSpawnPoint(Mill.ENTITY_SHEEP, p);
					} else if (pt.isType(bstonesource)) {
						building.addSourcePoint(Block.stone.blockID, p);
					} else if (pt.isType(bsandsource)) {
						building.addSourcePoint(Block.sand.blockID, p);
					} else if (pt.isType(bsandstonesource)) {
						building.addSourcePoint(Block.sandStone.blockID, p);
					} else if (pt.isType(bclaysource)) {
						building.addSourcePoint(Block.blockClay.blockID, p);
					} else if (pt.isType(bgravelsource)) {
						building.addSourcePoint(Block.gravel.blockID, p);
					} else if (pt.isType(bspawnerskeleton)) {
						building.addMobSpawnerPoint(Mill.ENTITY_SKELETON, p);
					} else if (pt.isType(bspawnerzombie)) {
						building.addMobSpawnerPoint(Mill.ENTITY_ZOMBIE, p);
					} else if (pt.isType(bspawnerspider)) {
						building.addMobSpawnerPoint(Mill.ENTITY_SPIDER, p);
					} else if (pt.isType(bspawnercavespider)) {
						building.addMobSpawnerPoint(Mill.ENTITY_CAVESPIDER, p);
					} else if (pt.isType(bspawnercreeper)) {
						building.addMobSpawnerPoint(Mill.ENTITY_CREEPER, p);
					} else if (pt.isType(bdispenserunknownpowder)) {
						if (!building.dispenderUnknownPowder.contains(p)) {
							building.dispenderUnknownPowder.add(p);
						}
					} else if (pt.isType(bfishingspot)) {
						if (!building.fishingspots.contains(p)) {
							building.fishingspots.add(p);
						}
					} else if (pt.isType(bhealingspot)) {
						if (!building.healingspots.contains(p)) {
							building.healingspots.add(p);
						}
					} else if (pt.isType(blockedchest) || pt.isType(bmainchest)) {
						if (!building.chests.contains(p)) {
							building.chests.add(p);
						}
					} else if (pt.isType(bfurnace)) {
						if (!building.furnaces.contains(p)) {
							building.furnaces.add(p);
						}
					} else if (pt.isType(bbrewingstand)) {
						if (!building.brewingStands.contains(p)) {
							building.brewingStands.add(p);
						}
					} else if (pt.isType(bsleepingPos)) {
						building.setSleepingPos(p);
					} else if (pt.isType(bsellingPos)) {
						building.setSellingPos(p);
					} else if (pt.isType(bcraftingPos)) {
						building.setCraftingPos(p);
					} else if (pt.isType(bdefendingPos)) {
						building.setDefendingPos(p);
					} else if (pt.isType(bshelterPos)) {
						building.setShelterPos(p);
					} else if (pt.isType(bsignwallGuess) || pt.isType(bsignwallTop) || pt.isType(bsignwallBottom)
							|| pt.isType(bsignwallLeft) || pt.isType(bsignwallRight)) {
						building.signs.set(signOrder[signNb], p);
						signNb++;
					}
				}
			}
		}
	}

	public void setMetadata(Vector<BuildingBlock> bblocks) {
		final Vector<BuildingBlock> stairs= new Vector<BuildingBlock>();
		final Vector<BuildingBlock> ladders=new Vector<BuildingBlock>();
		final Vector<BuildingBlock> doors=new Vector<BuildingBlock>();

		final HashMap<Point,BuildingBlock> map=new HashMap<Point,BuildingBlock>();

		for (final BuildingBlock block : bblocks) {
			map.put(block.p, block);
			if ((block.bid == Block.ladder.blockID) && (block.meta==-1)) {
				ladders.add(block);
			} else if (block.bid == Block.doorWood.blockID) {
				doors.add(block);
			} else if (((block.bid == Block.stairCompactCobblestone.blockID)
					|| (block.bid == Block.stairCompactPlanks.blockID))  && (block.meta==-1)) {
				block.meta=-1;
				stairs.add(block);
			}
		}

		boolean[] northValid=new boolean[ladders.size()];
		boolean[] southValid=new boolean[ladders.size()];
		boolean[] westValid=new boolean[ladders.size()];
		boolean[] eastValid=new boolean[ladders.size()];

		int i=0;
		for (final BuildingBlock ladder : ladders) {
			northValid[i]=(mapIsOpaqueBlock(map,ladder.p.getNorth()));
			southValid[i]=(mapIsOpaqueBlock(map,ladder.p.getSouth()));
			westValid[i]=(mapIsOpaqueBlock(map,ladder.p.getWest()));
			eastValid[i]=(mapIsOpaqueBlock(map,ladder.p.getEast()));

			//Starting by setting the meta for ladders with only one possibility
			if (northValid[i] && !southValid[i] && !westValid[i] && !eastValid[i]) {
				ladder.meta=5;
			}
			if (!northValid[i] && southValid[i] && !westValid[i] && !eastValid[i]) {
				ladder.meta=4;
			}
			if (!northValid[i] && !southValid[i] && westValid[i] && !eastValid[i]) {
				ladder.meta=2;
			}
			if (!northValid[i] && !southValid[i] && !westValid[i] && eastValid[i]) {
				ladder.meta=3;
			}
			i++;
		}

		boolean goOn=true;
		//ladders with a set meta "spread" it to neighbours
		while (goOn) {
			goOn=false;
			i=0;
			for (final BuildingBlock ladder : ladders) {
				if (ladder.meta == 0) {
					if (MLN.LogBuildingPlan>=MLN.MAJOR) {
						MLN.major(this,buildingKey+": ladder "+ladder+" has no metada, trying to find neighbours.");
						if (map.containsKey(ladder.p.getAbove())) {
							MLN.major(this, buildingKey+": Above: "+map.get(ladder.p.getAbove()));
						}

						if (map.containsKey(ladder.p.getBelow())) {
							MLN.major(this, buildingKey+": Below: "+map.get(ladder.p.getBelow()));
						}
					}

					if (map.containsKey(ladder.p.getAbove())) {
						final BuildingBlock b=map.get(ladder.p.getAbove());
						if ((b.bid == Block.ladder.blockID) && (b.meta != 0)) {
							if ((b.meta==5) && northValid[i]) {
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==4) && southValid[i]) {
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==3) && westValid[i]) {
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==2) && eastValid[i]) {
								ladder.meta=b.meta;
								goOn=true;
							}
						}
					}
					if ((ladder.meta == 0) && map.containsKey(ladder.p.getBelow())) {
						if (MLN.LogBuildingPlan>=MLN.MAJOR) {
							MLN.major(this, buildingKey+": trying ladder below. "+northValid[i]+"/"+southValid[i]+"/"+westValid[i]+"/"+eastValid[i]);
						}
						final BuildingBlock b=map.get(ladder.p.getBelow());
						if ((b.bid == Block.ladder.blockID) && (b.meta != 0)) {
							if ((b.meta==5) && northValid[i]) {
								if (MLN.LogBuildingPlan>=MLN.MAJOR) {
									MLN.major(this, buildingKey+": copying blow: north");
								}
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==4) && southValid[i]) {
								if (MLN.LogBuildingPlan>=MLN.MAJOR) {
									MLN.major(this,buildingKey+": copying blow: south");
								}
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==3) && westValid[i]) {
								if (MLN.LogBuildingPlan>=MLN.MAJOR) {
									MLN.major(this,buildingKey+": copying blow: west");
								}
								ladder.meta=b.meta;
								goOn=true;
							} else if ((b.meta==2) && eastValid[i]) {
								if (MLN.LogBuildingPlan>=MLN.MAJOR) {
									MLN.major(this, buildingKey+": copying blow: east");
								}
								ladder.meta=b.meta;
								goOn=true;
							}
						}
					}
				}
				i++;
			}
		}

		//now for stairs
		northValid=new boolean[stairs.size()];
		southValid=new boolean[stairs.size()];
		westValid=new boolean[stairs.size()];
		eastValid=new boolean[stairs.size()];

		i=0;
		for (final BuildingBlock stair : stairs) {
			northValid[i]=(!mapIsOpaqueBlock(map,stair.p.getSouth()) && (!mapIsOpaqueBlock(map,stair.p.getNorth().getAbove()) || mapIsOpaqueBlock(map,stair.p.getNorth().getAbove())));
			southValid[i]=(!mapIsOpaqueBlock(map,stair.p.getNorth()) && (!mapIsOpaqueBlock(map,stair.p.getSouth().getAbove()) || mapIsOpaqueBlock(map,stair.p.getSouth().getAbove())));
			westValid[i]=(!mapIsOpaqueBlock(map,stair.p.getEast()) && (!mapIsOpaqueBlock(map,stair.p.getWest().getAbove()) || mapIsOpaqueBlock(map,stair.p.getWest().getAbove())));
			eastValid[i]=(!mapIsOpaqueBlock(map,stair.p.getWest()) && (!mapIsOpaqueBlock(map,stair.p.getEast().getAbove()) || mapIsOpaqueBlock(map,stair.p.getEast().getAbove())));

			if (MLN.LogBuildingPlan>=MLN.MAJOR) {
				if (northValid[i]) {
					MLN.major(this, buildingKey+": northValid");
				} else if (southValid[i]) {
					MLN.major(this,  buildingKey+": southValid");
				} else if (westValid[i]) {
					MLN.major(this, buildingKey+": westValid");
				} else if (eastValid[i]) {
					MLN.major(this,  buildingKey+": eastValid");
				} else {
					MLN.major(this, buildingKey+": none valid");
				}
			}

			if (northValid[i]) {
				stair.meta=1;
			} else if (southValid[i]) {
				stair.meta=0;
			} else if (westValid[i]) {
				stair.meta=3;
			} else if (eastValid[i]) {
				stair.meta=2;
			} else {
				stair.meta=0;
			}
			i++;
		}

		for (final BuildingBlock door : doors) {
			final int orientation=door.meta & 3;
			if (orientation == 2) {
				if ((!map.containsKey(door.p.getWest()) || (map.get(door.p.getWest()).bid==0) || (map.get(door.p.getWest()).bid==Block.doorWood.blockID)) && map.containsKey(door.p.getEast())) {
					door.special=BuildingBlock.INVERTEDDOOR;
				}
			} else if (orientation == 3) {
				if ((!map.containsKey(door.p.getNorth()) || (map.get(door.p.getNorth()).bid==0) || (map.get(door.p.getNorth()).bid==Block.doorWood.blockID)) && map.containsKey(door.p.getSouth())) {
					door.special=BuildingBlock.INVERTEDDOOR;
				}
			} else if (orientation == 0) {
				if ((!map.containsKey(door.p.getEast()) || (map.get(door.p.getEast()).bid==0) || (map.get(door.p.getEast()).bid==Block.doorWood.blockID)) && map.containsKey(door.p.getWest())) {
					door.special=BuildingBlock.INVERTEDDOOR;
				}
			} else if (orientation == 1) {
				if ((!map.containsKey(door.p.getSouth()) || (map.get(door.p.getSouth()).bid==0) || (map.get(door.p.getSouth()).bid==Block.doorWood.blockID)) && map.containsKey(door.p.getNorth())) {
					door.special=BuildingBlock.INVERTEDDOOR;
				}
			}
		}
	}

	public LocationReturn testSpot(MillWorldInfo winfo, AStarPathing pathing, Point centre, int x, int z, Random random, int porientation) {

		int orientation;

		final int relx=(x+winfo.mapStartX)-centre.getiX();
		final int relz=(z+winfo.mapStartZ)-centre.getiZ();

		winfo.buildTested[x][z]=true;

		if (MLN.WorldGeneration>=MLN.DEBUG) {
			MLN.debug(this, "Testing: "+x+"/"+z);
		}


		if (porientation==-1) {
			if ((relx*relx) > (relz*relz)) {
				if (relx > 0) {
					orientation=NORTH_FACING;
				} else {
					orientation=SOUTH_FACING;
				}
			} else {
				if (relz > 0) {
					orientation=EAST_FACING;
				} else {
					orientation=WEST_FACING;
				}
			}
		} else {
			orientation=porientation;
		}

		orientation=(orientation+buildingOrientation) % 4;

		int xwidth;
		int zwidth;

		if ((orientation==NORTH_FACING) || (orientation==SOUTH_FACING)) {
			xwidth=length+(areaToClear*2)+2;
			zwidth=width+(areaToClear*2)+2;
		} else {
			xwidth=width+(areaToClear*2)+2;
			zwidth=length+(areaToClear*2)+2;
		}

		int altitudeTotal=0;
		int nbPoints=0;
		int nbError=0;

		int allowedErrors=10;
		boolean hugeBuilding=false;

		if ((xwidth*zwidth)>6000) {
			allowedErrors=1500;
			hugeBuilding=true;
		} else if ((xwidth*zwidth)>200) {
			allowedErrors=(xwidth*zwidth)/20;
		}

		boolean reachable=false;

		for (int i=0;i<=(int)Math.floor(xwidth/2);i++) {
			for (int j=0;j<=(int)Math.floor(zwidth/2);j++) {
				for (int k=0;k<4;k++) {
					int ci,cj;
					if (k==0) {
						ci=x+i;
						cj=z+j;
					} else if (k==1) {
						ci=x-i;
						cj=z+j;
					} else if (k==2) {
						ci=x-i;
						cj=z-j;
					} else {
						ci=x+i;
						cj=z-j;
					}

					if ((ci < 0) || (cj< 0) || (ci>=winfo.length) || (cj>=winfo.width)) {
						final Point p=new Point(ci+winfo.mapStartX,64,cj+winfo.mapStartZ);

						return new LocationReturn(LocationReturn.OUTSIDE_RADIUS,p);
					}

					if (winfo.buildingLoc[ci][cj]) {
						if (nbError>allowedErrors) {
							final Point p=new Point(ci+winfo.mapStartX,64,cj+winfo.mapStartZ);

							return new LocationReturn(LocationReturn.LOCATION_CLASH,p);
						} else {
							nbError+=5;
						}
					} else if (winfo.buildingForbidden[ci][cj]) {
						if (!hugeBuilding || (nbError>allowedErrors)) {

							final Point p=new Point(ci+winfo.mapStartX,64,cj+winfo.mapStartZ);

							return new LocationReturn(LocationReturn.CONSTRUCTION_FORBIDEN,p);
						} else {
							nbError++;
						}
					} else if (winfo.danger[ci][cj]) {
						if (nbError>allowedErrors) {
							final Point p=new Point(ci+winfo.mapStartX,64,cj+winfo.mapStartZ);

							return new LocationReturn(LocationReturn.DANGER,p);
						} else {
							nbError++;
						}
					} else if (!winfo.canBuild[ci][cj]) {
						if (nbError>allowedErrors) {
							final Point p=new Point(ci+winfo.mapStartX,64,cj+winfo.mapStartZ);

							return new LocationReturn(LocationReturn.WRONG_ALTITUDE,p);
						} else {
							nbError++;
						}
					}

					if ((pathing !=null) && (pathing.regions[ci][cj]!=pathing.thRegion)) {
						reachable=false;
					} else {
						reachable=true;
					}

					altitudeTotal+=winfo.constructionHeight[ci][cj];
					nbPoints++;
				}
			}
		}

		if ((pathing !=null) && !reachable)
			return new LocationReturn(LocationReturn.NOT_REACHABLE,centre);

		final int altitude=(int) ((altitudeTotal*1.0f)/(nbPoints));

		final BuildingLocation l=new BuildingLocation(this,new Point(x+winfo.mapStartX,altitude,z+winfo.mapStartZ),orientation);

		return new LocationReturn(l);
	}

	public LocationReturn testSpotBedrock(World world,int cx,int cz) {
		for (int x=cx-width-2;x<(cx+width+2);x++) {
			for (int z=cz-length-2;z<(cz+length+2);z++) {
				for (int y=0;y<(plan.length+2);y++) {
					final int bid=world.getBlockId(x,y,z);

					if ((bid!=Block.bedrock.blockID) && (bid!=Block.stone.blockID) && (bid!=Block.dirt.blockID) && (bid!=Block.gravel.blockID) &&
							(bid!=Block.oreCoal.blockID) && (bid!=Block.oreDiamond.blockID) && (bid!=Block.oreGold.blockID) && (bid!=Block.oreIron.blockID)
							&& (bid!=Block.oreLapis.blockID) && (bid!=Block.oreRedstone.blockID))
						return new LocationReturn(LocationReturn.CONSTRUCTION_FORBIDEN,null);
				}
			}
		}

		final BuildingLocation l=new BuildingLocation(this,new Point(cx,2,cz),0);
		l.bedrocklevel=true;
		return new LocationReturn(l);
	}

	@Override
	public String toString() {
		if (culture!=null)
			return culture.key+":"+planName;

		return "null culture:"+planName;
	}

}



