package org.millenaire.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;

public class SpecialQuestActions {


	public static final String COMPLETE="_complete";
	public static final String EXPLORE_TAG="action_explore_";
	public static final String ENCHANTMENTTABLE="action_build_enchantment_table";
	public static final String UNDERWATER_GLASS="action_underwater_glass";
	public static final String UNDERWATER_DIVE="action_underwater_dive";
	public static final String TOPOFTHEWORLD="action_topoftheworld";
	public static final String BOTTOMOFTHEWORLD="action_bottomoftheworld";

	public static final String BOREHOLE="action_borehole";
	public static final String BOREHOLETNT="action_boreholetnt";
	public static final String BOREHOLETNTLIT="action_boreholetntlit";
	public static final String THEVOID="action_thevoid";

	public static final String MAYANSIEGE="action_mayansiege";

	private static void handleBorehole(MillWorld mw, EntityPlayer player) {

		if (!mw.getProfile(player.username).isTagSet(BOREHOLE) || mw.getProfile(player.username).isTagSet(BOREHOLE+COMPLETE))
			return;

		if (player.posY>10)
			return;

		int nbok=0;
		for (int x=(int) (player.posX-2);x<((int)player.posX+3);x++) {
			for (int z=(int) (player.posZ-2);z<((int)player.posZ+3);z++) {

				boolean ok=true,stop=false;

				for (int y=127;(y>0) && !stop;y--) {
					final int bid=mw.world.getBlockId(x, y, z);
					if (bid==Block.bedrock.blockID) {
						stop=true;
					} else if (bid!=0) {
						stop=true;
						ok=false;
					}
				}

				if (ok) {
					nbok++;
				}
			}
		}

		if (nbok>=25) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.borehole_success");
			mw.getProfile(player.username).clearTag(BOREHOLE);
			mw.getProfile(player.username).setTag(BOREHOLE+COMPLETE);
			mw.getProfile(player.username).setActionData(BOREHOLE+"_pos", new Point(player).getIntString());
			return;
		}

		final String maxKnownStr=mw.getProfile(player.username).getActionData(BOREHOLE+"_max");

		int maxKnown=0;

		if (maxKnownStr!=null) {
			maxKnown=Integer.parseInt(maxKnownStr);
		}

		if (nbok>maxKnown) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.borehole_nblineok",""+nbok);
			mw.getProfile(player.username).setActionData(BOREHOLE+"_max", ""+nbok);
		}
	}

	private static void handleBoreholeTNT(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(BOREHOLETNT) || mw.getProfile(player.username).isTagSet(BOREHOLETNT+COMPLETE))
			return;

		final String pStr=mw.getProfile(player.username).getActionData(BOREHOLE+"_pos");

		if (pStr==null)
			return;

		final Point p=new Point(pStr);

		if (p.distanceToSquared(player)>25)
			return;

		int nbTNT=0;

		for (int x=(p.getiX()-2);x<(p.getiX()+3);x++) {
			for (int z=(p.getiZ()-2);z<(p.getiZ()+3);z++) {
				boolean obsidian=false;
				for (int y=6;y>0;y--) {
					final int bid=mw.world.getBlockId(x, y, z);
					if (bid==Block.obsidian.blockID) {
						obsidian=true;
					} else if (obsidian && (bid==Block.tnt.blockID)) {
						nbTNT++;
					}
				}
			}
		}

		if (nbTNT>=20) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.boreholetnt_success");
			mw.getProfile(player.username).clearTag(BOREHOLETNT);
			mw.getProfile(player.username).setTag(BOREHOLETNT+COMPLETE);
			mw.getProfile(player.username).setTag(BOREHOLETNTLIT);
			mw.getProfile(player.username).clearActionData(BOREHOLETNT+"_max");
			return;
		} else if (nbTNT==0)
			return;

		final String maxKnownStr=mw.getProfile(player.username).getActionData(BOREHOLETNT+"_max");

		int maxKnown=0;

		if (maxKnownStr!=null) {
			maxKnown=Integer.parseInt(maxKnownStr);
		}

		if (nbTNT>maxKnown) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.boreholetnt_nbtnt",""+nbTNT);
			mw.getProfile(player.username).setActionData(BOREHOLETNT+"_max", ""+nbTNT);
		}

	}

	private static void handleBoreholeTNTLit(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(BOREHOLETNTLIT) || mw.getProfile(player.username).isTagSet(BOREHOLETNTLIT+COMPLETE))
			return;

		final Point p=new Point(mw.getProfile(player.username).getActionData(BOREHOLE+"_pos"));

		final int nbtnt=mw.world.getEntitiesWithinAABB(EntityTNTPrimed.class, AxisAlignedBB.getBoundingBox(p.x, p.y, p.z,
				p.x + 1, p.y + 1, p.z + 1).expand(8D, 4D, 8D)).size();

		if (nbtnt>0) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.boreholetntlit_success");
			mw.getProfile(player.username).clearTag(BOREHOLETNTLIT);
			mw.getProfile(player.username).setTag(BOREHOLETNTLIT+COMPLETE);
			return;
		}

	}

	private static void handleBottomOfTheWorld(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(BOTTOMOFTHEWORLD) || mw.getProfile(player.username).isTagSet(BOTTOMOFTHEWORLD+COMPLETE))
			return;

		if (player.posY<4) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.bottomoftheworld_success");
			mw.getProfile(player.username).clearTag(BOTTOMOFTHEWORLD);
			mw.getProfile(player.username).setTag(BOTTOMOFTHEWORLD+COMPLETE);
			return;
		}
	}

	private static void handleContinuousExplore(MillWorld mw, EntityPlayer player, long worldTime, String biome, String mob, int nbMob, int minTravel) {

		if (!mw.getProfile(player.username).isTagSet(EXPLORE_TAG+biome) || mw.getProfile(player.username).isTagSet(EXPLORE_TAG+biome+COMPLETE))
			return;

		if (mw.world.isDaytime())
			return;

		String biomeName=mw.world.getWorldChunkManager().getBiomeGenAt((int)player.posX, (int)player.posZ).biomeName.toLowerCase();
		if (biomeName.equals("extreme hills")) {
			biomeName="mountain";
		}
		if (!biomeName.equals(biome))
			return;

		final int surface=MillCommonUtilities.findTopSoilBlock(mw.world, (int)player.posX, (int)player.posZ);
		if (player.posY<=(surface-2))
			return;

		final String testnbstr=mw.getProfile(player.username).getActionData(biome+"_explore_nbcomplete");

		int nbtest=0;
		if (testnbstr!=null) {
			nbtest=Integer.parseInt(testnbstr);

			for (int i=1;i<=nbtest;i++) {
				final String pointstr=mw.getProfile(player.username).getActionData(biome+"_explore_point"+i);
				if (pointstr!=null) {
					final Point p=new Point(pointstr);
					if (p.horizontalDistanceTo(player)<minTravel)
						return;
				}
			}
		}

		nbtest++;

		if (nbtest>=20) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions."+biome+"_success");
			mw.getProfile(player.username).clearActionData(biome+"_explore_nbcomplete");
			for (int i=1;i<=10;i++) {
				mw.getProfile(player.username).clearActionData(biome+"_explore_point"+i);
			}
			mw.getProfile(player.username).clearTag(EXPLORE_TAG+biome);
			mw.getProfile(player.username).setTag(EXPLORE_TAG+biome+COMPLETE);
			return;
		}

		mw.getProfile(player.username).setActionData(biome+"_explore_point"+nbtest, new Point(player).getIntString());
		mw.getProfile(player.username).setActionData(biome+"_explore_nbcomplete", ""+nbtest);
		ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions."+biome+"_continue",""+(nbtest*5));

		MillCommonUtilities.spawnMobsAround(mw.world, new Point(player), 20, mob, 2, 4);

	}

	private static void handleEnchantmentTable(MillWorld mw, EntityPlayer player) {

		if (!mw.getProfile(player.username).isTagSet(ENCHANTMENTTABLE) || mw.getProfile(player.username).isTagSet(ENCHANTMENTTABLE+COMPLETE))
			return;

		boolean closeEnough=false;

		for (int i=0;i<mw.loneBuildingsList.types.size();i++) {
			if (mw.loneBuildingsList.types.get(i).equals("sadhutree")) {
				if (mw.loneBuildingsList.pos.get(i).distanceToSquared(player)<100) {
					closeEnough=true;
				}
			}
		}

		if (!closeEnough)
			return;

		for (int x=(int)player.posX-5;x<((int)player.posX+5);x++) {
			for (int z=(int)player.posZ-5;z<((int)player.posZ+5);z++) {
				for (int y=(int)player.posY-3;y<((int)player.posY+3);y++) {

					final int bid=mw.world.getBlockId(x, y, z);

					if (bid==Block.enchantmentTable.blockID) {

						int nbBookShelves=0;

						for (int dx = -1; dx <= 1; dx++)
						{
							for (int dz = -1; dz <= 1; dz++)
							{
								if (((dx == 0) && (dz == 0)) || !mw.world.isAirBlock(x + dx, y, z + dz) || !mw.world.isAirBlock(x + dx, y + 1, z + dz))
								{
									continue;
								}

								if (mw.world.getBlockId(x + (dx * 2), y, z + (dz * 2)) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}

								if (mw.world.getBlockId(x + (dx * 2), y + 1, z + (dz * 2)) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}

								if ((dz == 0) ||(dx == 0))
								{
									continue;
								}

								if (mw.world.getBlockId(x + (dx * 2), y, z + dz) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}

								if (mw.world.getBlockId(x + (dx * 2), y + 1, z + dz) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}

								if (mw.world.getBlockId(x + dx, y, z + (dz * 2)) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}

								if (mw.world.getBlockId(x + dx, y + 1, z + (dz * 2)) == Block.bookShelf.blockID)
								{
									nbBookShelves++;
								}
							}
						}

						if (nbBookShelves>0) {
							ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.enchantmenttable_success");
							mw.getProfile(player.username).clearTag(ENCHANTMENTTABLE);
							mw.getProfile(player.username).setTag(ENCHANTMENTTABLE+COMPLETE);
							return;
						}

					}

				}
			}
		}
	}


	private static void handleTheVoid(MillWorld mw, EntityPlayer player) {

		if (!mw.getProfile(player.username).isTagSet(THEVOID) || mw.getProfile(player.username).isTagSet(THEVOID+COMPLETE))
			return;

		if (player.posY>30)
			return;

		for (int i=-5;i<5;i++) {
			for (int j=-5;j<5;j++) {

				final int bid=mw.world.getBlockId((int)player.posX+i, 0, (int)player.posZ+j);

				if (bid==0) {
					ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.thevoid_success");
					mw.getProfile(player.username).clearTag(THEVOID);
					mw.getProfile(player.username).setTag(THEVOID+COMPLETE);
					return;
				}

			}
		}
	}

	private static void handleTopOfTheWorld(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(TOPOFTHEWORLD) || mw.getProfile(player.username).isTagSet(TOPOFTHEWORLD+COMPLETE))
			return;

		if (player.posY>250) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.topoftheworld_success");
			mw.getProfile(player.username).clearTag(TOPOFTHEWORLD);
			mw.getProfile(player.username).setTag(TOPOFTHEWORLD+COMPLETE);
			return;
		}
	}

	private static void handleUnderwaterDive(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(UNDERWATER_DIVE) || mw.getProfile(player.username).isTagSet(UNDERWATER_DIVE+COMPLETE))
			return;

		Point p=new Point(player);

		int nbWater=0;

		while (MillCommonUtilities.getBlock(mw.world, p)==Block.waterStill.blockID) {
			nbWater++;
			p=p.getAbove();
		}

		if (nbWater>12) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.underwaterdive_success");
			mw.getProfile(player.username).clearTag(UNDERWATER_DIVE);
			mw.getProfile(player.username).setTag(UNDERWATER_DIVE+COMPLETE);
			return;
		}
	}

	private static void handleUnderwaterGlass(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(UNDERWATER_GLASS) || mw.getProfile(player.username).isTagSet(UNDERWATER_GLASS+COMPLETE))
			return;

		Point p=new Point(player);

		int bid=MillCommonUtilities.getBlock(mw.world, p);

		while ((bid==0) || ((bid>0) && !MillCommonUtilities.isBlockOpaqueCube(bid) && (bid!=Block.glass.blockID) && (bid!=Block.thinGlass.blockID))) {
			p=p.getAbove();
			bid=MillCommonUtilities.getBlock(mw.world, p);
		}

		bid=MillCommonUtilities.getBlock(mw.world, p);

		if ((bid!=Block.glass.blockID) && (bid!=Block.thinGlass.blockID))
			return;
		p=p.getAbove();
		int nbWater=0;

		while (MillCommonUtilities.getBlock(mw.world, p)==Block.waterStill.blockID) {
			nbWater++;
			p=p.getAbove();
		}

		if (nbWater>15) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.underwaterglass_success");
			mw.getProfile(player.username).clearTag(UNDERWATER_GLASS);
			mw.getProfile(player.username).setTag(UNDERWATER_GLASS+COMPLETE);
			return;
		}

		if (nbWater>1) {
			ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.underwaterglass_notdeepenough");
		}
	}

	private static void handleMayanSiege(MillWorld mw, EntityPlayer player) {
		if (!mw.getProfile(player.username).isTagSet(MAYANSIEGE) || mw.getProfile(player.username).isTagSet(MAYANSIEGE+COMPLETE))
			return;

		final String siegeStatus=mw.getProfile(player.username).getActionData("mayan_siege_status");

		if (siegeStatus==null) {//start siege			
			for (Point p : mw.loneBuildingsList.pos) {
				Building b=mw.getBuilding(p);
				if (b!=null) {
					if (b.villageType.key.equals("questpyramid") && p.distanceTo(player)<50) {

						int nbGhasts=0,nbBlazes=0,nbSkel=0;

						for (int i=0;i<12;i++) {
							Point spawn=b.location.pos.getRelative(-10+MillCommonUtilities.randomInt(20), 20, -10+MillCommonUtilities.randomInt(20));
							EntityTargetedGhast ent=(EntityTargetedGhast)MillCommonUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_GHAST);
							if (ent!=null) {
								ent.target=b.location.pos.getRelative(0, 20, 0);
								nbGhasts++;
							}
						}

						for (int i=0;i<12;i++) {

							Point spawn=b.location.pos.getRelative(-5+MillCommonUtilities.randomInt(10), 15, -5+MillCommonUtilities.randomInt(10));
							EntityTargetedBlaze ent=(EntityTargetedBlaze)MillCommonUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_BLAZE);
							if (ent!=null) {
								ent.target=b.location.pos.getRelative(0, 10, 0);
								nbBlazes++;
							}
						}

						for (int i=0;i<5;i++) {
							Point spawn=b.location.pos.getRelative(5, 12, -5+MillCommonUtilities.randomInt(10));
							Entity ent=MillCommonUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_WITHERSKELETON);
							if (ent!=null) {
								nbSkel++;
							}
							spawn=b.location.pos.getRelative(-5, 12, -5+MillCommonUtilities.randomInt(10));
							ent=MillCommonUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_WITHERSKELETON);
							if (ent!=null) {
								nbSkel++;
							}
						}
						
						mw.getProfile(player.username).setActionData("mayan_siege_status", "started");
						mw.getProfile(player.username).setActionData("mayan_siege_ghasts", ""+nbGhasts);
						mw.getProfile(player.username).setActionData("mayan_siege_blazes", ""+nbBlazes);
						mw.getProfile(player.username).setActionData("mayan_siege_skeletons", ""+nbSkel);

						ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.mayan_siege_start",""+nbGhasts,""+nbBlazes,""+nbSkel);
					}
				}
			}
		} else if (siegeStatus.equals("started")) {

			for (Point p : mw.loneBuildingsList.pos) {
				Building b=mw.getBuilding(p);
				if (b!=null) {
					if (b.villageType.key.equals("questpyramid") && p.distanceTo(player)<50) {
						List<Entity> mobs = MillCommonUtilities
								.getEntitiesWithinAABB(mw.world,
										EntityTargetedGhast.class, b.location.pos, 128,128);

						int nbGhasts=mobs.size();

						mobs = MillCommonUtilities
								.getEntitiesWithinAABB(mw.world,
										EntityTargetedBlaze.class, b.location.pos, 128,128);

						int nbBlazes=mobs.size();
						
						mobs = MillCommonUtilities
								.getEntitiesWithinAABB(mw.world,
										EntityTargetedWitherSkeleton.class, b.location.pos, 128,128);

						int nbSkel=mobs.size();

						if (nbGhasts==0 && nbBlazes==0 && nbSkel==0) {
							mw.getProfile(player.username).setActionData("mayan_siege_status", "finished");
							mw.getProfile(player.username).setTag(MAYANSIEGE+COMPLETE);
							ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.mayan_siege_success");
						} else {
							int oldGhasts=Integer.parseInt(mw.getProfile(player.username).getActionData("mayan_siege_ghasts"));
							int oldBlazes=Integer.parseInt(mw.getProfile(player.username).getActionData("mayan_siege_blazes"));
							int oldSkel=Integer.parseInt(mw.getProfile(player.username).getActionData("mayan_siege_skeletons"));

							if (oldGhasts!=nbGhasts || oldBlazes!=nbBlazes || oldSkel!=nbSkel) {
								ServerSender.sendTranslatedSentence(player,MLN.LIGHTGREY,"actions.mayan_siege_update",""+nbGhasts,""+nbBlazes,""+nbSkel);
								mw.getProfile(player.username).setActionData("mayan_siege_ghasts", ""+nbGhasts);
								mw.getProfile(player.username).setActionData("mayan_siege_blazes",""+nbBlazes);
								mw.getProfile(player.username).setActionData("mayan_siege_skeletons",""+nbSkel);
							}
						}
					}
				}
			}
		}

	}

	public static void onTick(MillWorld mw, EntityPlayer player) {

		long startTime;
		if (mw.lastWorldUpdate>0) {
			startTime=Math.max(mw.lastWorldUpdate+1, mw.world.getWorldTime()-10);
		} else {
			startTime=mw.world.getWorldTime();
		}

		for (long worldTime=startTime;worldTime<=mw.world.getWorldTime();worldTime++) {
			if ((worldTime % 250) == 0) {
				handleContinuousExplore(mw,player,worldTime,MLN.questBiomeForest,"Zombie",2,15);
				handleContinuousExplore(mw,player,worldTime,MLN.questBiomeDesert,"Skeleton",2,15);
				handleContinuousExplore(mw,player,worldTime,MLN.questBiomeMountain,"Spider",2,10);
			}
			if ((worldTime % 500) == 0) {
				handleUnderwaterGlass(mw,player);
			}
			if ((worldTime % 100) == 0) {
				handleUnderwaterDive(mw,player);
				handleTopOfTheWorld(mw,player);
				handleBottomOfTheWorld(mw,player);
				handleBorehole(mw,player);
				handleBoreholeTNT(mw,player);
				handleTheVoid(mw,player);
				handleEnchantmentTable(mw,player);

			}
			if ((worldTime % 10) == 0) {
				handleBoreholeTNTLit(mw,player);
				handleMayanSiege(mw,player);
			}
		}
	}
}
