package org.millenaire.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;

public class SpecialQuestActions {

	public static final String COMPLETE = "_complete";
	public static final String EXPLORE_TAG = "action_explore_";
	public static final String ENCHANTMENTTABLE = "action_build_enchantment_table";
	public static final String UNDERWATER_GLASS = "action_underwater_glass";
	public static final String UNDERWATER_DIVE = "action_underwater_dive";
	public static final String TOPOFTHEWORLD = "action_topoftheworld";
	public static final String BOTTOMOFTHEWORLD = "action_bottomoftheworld";

	public static final String BOREHOLE = "action_borehole";
	public static final String BOREHOLETNT = "action_boreholetnt";
	public static final String BOREHOLETNTLIT = "action_boreholetntlit";
	public static final String THEVOID = "action_thevoid";

	public static final String MAYANSIEGE = "action_mayansiege";

	private static void handleBorehole(final MillWorld mw,
			final EntityPlayer player) {

		if (!mw.getProfile(player.getDisplayName()).isTagSet(BOREHOLE)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						BOREHOLE + COMPLETE)) {
			return;
		}

		if (player.posY > 10) {
			return;
		}

		int nbok = 0;
		for (int x = (int) (player.posX - 2); x < (int) player.posX + 3; x++) {
			for (int z = (int) (player.posZ - 2); z < (int) player.posZ + 3; z++) {

				boolean ok = true, stop = false;

				for (int y = 127; y > 0 && !stop; y--) {
					final Block block = mw.world.getBlock(x, y, z);
					if (block == Blocks.bedrock) {
						stop = true;
					} else if (block != Blocks.air) {
						stop = true;
						ok = false;
					}
				}

				if (ok) {
					nbok++;
				}
			}
		}

		if (nbok >= 25) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.borehole_success");
			mw.getProfile(player.getDisplayName()).clearTag(BOREHOLE);
			mw.getProfile(player.getDisplayName()).setTag(BOREHOLE + COMPLETE);
			mw.getProfile(player.getDisplayName()).setActionData(
					BOREHOLE + "_pos", new Point(player).getIntString());
			return;
		}

		final String maxKnownStr = mw.getProfile(player.getDisplayName())
				.getActionData(BOREHOLE + "_max");

		int maxKnown = 0;

		if (maxKnownStr != null) {
			maxKnown = Integer.parseInt(maxKnownStr);
		}

		if (nbok > maxKnown) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.borehole_nblineok", "" + nbok);
			mw.getProfile(player.getDisplayName()).setActionData(
					BOREHOLE + "_max", "" + nbok);
		}
	}

	private static void handleBoreholeTNT(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(BOREHOLETNT)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						BOREHOLETNT + COMPLETE)) {
			return;
		}

		final String pStr = mw.getProfile(player.getDisplayName())
				.getActionData(BOREHOLE + "_pos");

		if (pStr == null) {
			return;
		}

		final Point p = new Point(pStr);

		if (p.distanceToSquared(player) > 25) {
			return;
		}

		int nbTNT = 0;

		for (int x = p.getiX() - 2; x < p.getiX() + 3; x++) {
			for (int z = p.getiZ() - 2; z < p.getiZ() + 3; z++) {
				boolean obsidian = false;
				for (int y = 6; y > 0; y--) {
					final Block block = mw.world.getBlock(x, y, z);
					if (block == Blocks.obsidian) {
						obsidian = true;
					} else if (obsidian && block == Blocks.tnt) {
						nbTNT++;
					}
				}
			}
		}

		if (nbTNT >= 20) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.boreholetnt_success");
			mw.getProfile(player.getDisplayName()).clearTag(BOREHOLETNT);
			mw.getProfile(player.getDisplayName()).setTag(
					BOREHOLETNT + COMPLETE);
			mw.getProfile(player.getDisplayName()).setTag(BOREHOLETNTLIT);
			mw.getProfile(player.getDisplayName()).clearActionData(
					BOREHOLETNT + "_max");
			return;
		} else if (nbTNT == 0) {
			return;
		}

		final String maxKnownStr = mw.getProfile(player.getDisplayName())
				.getActionData(BOREHOLETNT + "_max");

		int maxKnown = 0;

		if (maxKnownStr != null) {
			maxKnown = Integer.parseInt(maxKnownStr);
		}

		if (nbTNT > maxKnown) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.boreholetnt_nbtnt", "" + nbTNT);
			mw.getProfile(player.getDisplayName()).setActionData(
					BOREHOLETNT + "_max", "" + nbTNT);
		}

	}

	private static void handleBoreholeTNTLit(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(BOREHOLETNTLIT)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						BOREHOLETNTLIT + COMPLETE)) {
			return;
		}

		final Point p = new Point(mw.getProfile(player.getDisplayName())
				.getActionData(BOREHOLE + "_pos"));

		final int nbtnt = mw.world.getEntitiesWithinAABB(
				EntityTNTPrimed.class,
				AxisAlignedBB.getBoundingBox(p.x, p.y, p.z, p.x + 1, p.y + 1,
						p.z + 1).expand(8D, 4D, 8D)).size();

		if (nbtnt > 0) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.boreholetntlit_success");
			mw.getProfile(player.getDisplayName()).clearTag(BOREHOLETNTLIT);
			mw.getProfile(player.getDisplayName()).setTag(
					BOREHOLETNTLIT + COMPLETE);
			return;
		}

	}

	private static void handleBottomOfTheWorld(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(BOTTOMOFTHEWORLD)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						BOTTOMOFTHEWORLD + COMPLETE)) {
			return;
		}

		if (player.posY < 4) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.bottomoftheworld_success");
			mw.getProfile(player.getDisplayName()).clearTag(BOTTOMOFTHEWORLD);
			mw.getProfile(player.getDisplayName()).setTag(
					BOTTOMOFTHEWORLD + COMPLETE);
			return;
		}
	}

	private static void handleContinuousExplore(final MillWorld mw,
			final EntityPlayer player, final long worldTime,
			final String biome, final String mob, final int nbMob,
			final int minTravel) {

		if (!mw.getProfile(player.getDisplayName()).isTagSet(
				EXPLORE_TAG + biome)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						EXPLORE_TAG + biome + COMPLETE)) {
			return;
		}

		if (mw.world.isDaytime()) {
			return;
		}

		String biomeName = mw.world.getWorldChunkManager().getBiomeGenAt(
				(int) player.posX, (int) player.posZ).biomeName.toLowerCase();
		if (biomeName.equals("extreme hills")) {
			biomeName = "mountain";
		}
		if (!biomeName.equals(biome)) {
			return;
		}

		final int surface = MillCommonUtilities.findTopSoilBlock(mw.world,
				(int) player.posX, (int) player.posZ);
		if (player.posY <= surface - 2) {
			return;
		}

		final String testnbstr = mw.getProfile(player.getDisplayName())
				.getActionData(biome + "_explore_nbcomplete");

		int nbtest = 0;
		if (testnbstr != null) {
			nbtest = Integer.parseInt(testnbstr);

			for (int i = 1; i <= nbtest; i++) {
				final String pointstr = mw.getProfile(player.getDisplayName())
						.getActionData(biome + "_explore_point" + i);
				if (pointstr != null) {
					final Point p = new Point(pointstr);
					if (p.horizontalDistanceTo(player) < minTravel) {
						return;
					}
				}
			}
		}

		nbtest++;

		if (nbtest >= 20) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions." + biome + "_success");
			mw.getProfile(player.getDisplayName()).clearActionData(
					biome + "_explore_nbcomplete");
			for (int i = 1; i <= 10; i++) {
				mw.getProfile(player.getDisplayName()).clearActionData(
						biome + "_explore_point" + i);
			}
			mw.getProfile(player.getDisplayName())
					.clearTag(EXPLORE_TAG + biome);
			mw.getProfile(player.getDisplayName()).setTag(
					EXPLORE_TAG + biome + COMPLETE);
			return;
		}

		mw.getProfile(player.getDisplayName()).setActionData(
				biome + "_explore_point" + nbtest,
				new Point(player).getIntString());
		mw.getProfile(player.getDisplayName()).setActionData(
				biome + "_explore_nbcomplete", "" + nbtest);
		ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY, "actions."
				+ biome + "_continue", "" + nbtest * 5);

		MillCommonUtilities.spawnMobsAround(mw.world, new Point(player), 20,
				mob, 2, 4);

	}

	private static void handleEnchantmentTable(final MillWorld mw,
			final EntityPlayer player) {

		if (!mw.getProfile(player.getDisplayName()).isTagSet(ENCHANTMENTTABLE)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						ENCHANTMENTTABLE + COMPLETE)) {
			return;
		}

		boolean closeEnough = false;

		for (int i = 0; i < mw.loneBuildingsList.types.size(); i++) {
			if (mw.loneBuildingsList.types.get(i).equals("sadhutree")) {
				if (mw.loneBuildingsList.pos.get(i).distanceToSquared(player) < 100) {
					closeEnough = true;
				}
			}
		}

		if (!closeEnough) {
			return;
		}

		for (int x = (int) player.posX - 5; x < (int) player.posX + 5; x++) {
			for (int z = (int) player.posZ - 5; z < (int) player.posZ + 5; z++) {
				for (int y = (int) player.posY - 3; y < (int) player.posY + 3; y++) {

					final Block block = mw.world.getBlock(x, y, z);

					if (block == Blocks.enchanting_table) {

						int nbBookShelves = 0;

						for (int dx = -1; dx <= 1; dx++) {
							for (int dz = -1; dz <= 1; dz++) {
								if (dx == 0
										&& dz == 0
										|| !mw.world.isAirBlock(x + dx, y, z
												+ dz)
										|| !mw.world.isAirBlock(x + dx, y + 1,
												z + dz)) {
									continue;
								}

								if (mw.world
										.getBlock(x + dx * 2, y, z + dz * 2) == Blocks.bookshelf) {
									nbBookShelves++;
								}

								if (mw.world.getBlock(x + dx * 2, y + 1, z + dz
										* 2) == Blocks.bookshelf) {
									nbBookShelves++;
								}

								if (dz == 0 || dx == 0) {
									continue;
								}

								if (mw.world.getBlock(x + dx * 2, y, z + dz) == Blocks.bookshelf) {
									nbBookShelves++;
								}

								if (mw.world
										.getBlock(x + dx * 2, y + 1, z + dz) == Blocks.bookshelf) {
									nbBookShelves++;
								}

								if (mw.world.getBlock(x + dx, y, z + dz * 2) == Blocks.bookshelf) {
									nbBookShelves++;
								}

								if (mw.world
										.getBlock(x + dx, y + 1, z + dz * 2) == Blocks.bookshelf) {
									nbBookShelves++;
								}
							}
						}

						if (nbBookShelves > 0) {
							ServerSender.sendTranslatedSentence(player,
									MLN.LIGHTGREY,
									"actions.enchantmenttable_success");
							mw.getProfile(player.getDisplayName()).clearTag(
									ENCHANTMENTTABLE);
							mw.getProfile(player.getDisplayName()).setTag(
									ENCHANTMENTTABLE + COMPLETE);
							return;
						}

					}

				}
			}
		}
	}

	private static void handleMayanSiege(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(MAYANSIEGE)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						MAYANSIEGE + COMPLETE)) {
			return;
		}

		final String siegeStatus = mw.getProfile(player.getDisplayName())
				.getActionData("mayan_siege_status");

		if (siegeStatus == null) {// start siege
			for (final Point p : mw.loneBuildingsList.pos) {
				final Building b = mw.getBuilding(p);
				if (b != null) {
					if (b.villageType.key.equals("questpyramid")
							&& p.distanceTo(player) < 50) {

						int nbGhasts = 0, nbBlazes = 0, nbSkel = 0;

						for (int i = 0; i < 12; i++) {
							final Point spawn = b.location.pos.getRelative(-10
									+ MillCommonUtilities.randomInt(20), 20,
									-10 + MillCommonUtilities.randomInt(20));
							final EntityTargetedGhast ent = (EntityTargetedGhast) MillCommonUtilities
									.spawnMobsSpawner(mw.world, spawn,
											Mill.ENTITY_TARGETED_GHAST);
							if (ent != null) {
								ent.target = b.location.pos.getRelative(0, 20,
										0);
								nbGhasts++;
							}
						}

						for (int i = 0; i < 12; i++) {

							final Point spawn = b.location.pos.getRelative(-5
									+ MillCommonUtilities.randomInt(10), 15, -5
									+ MillCommonUtilities.randomInt(10));
							final EntityTargetedBlaze ent = (EntityTargetedBlaze) MillCommonUtilities
									.spawnMobsSpawner(mw.world, spawn,
											Mill.ENTITY_TARGETED_BLAZE);
							if (ent != null) {
								ent.target = b.location.pos.getRelative(0, 10,
										0);
								nbBlazes++;
							}
						}

						for (int i = 0; i < 5; i++) {
							Point spawn = b.location.pos.getRelative(5, 12, -5
									+ MillCommonUtilities.randomInt(10));
							Entity ent = MillCommonUtilities.spawnMobsSpawner(
									mw.world, spawn,
									Mill.ENTITY_TARGETED_WITHERSKELETON);
							if (ent != null) {
								nbSkel++;
							}
							spawn = b.location.pos.getRelative(-5, 12, -5
									+ MillCommonUtilities.randomInt(10));
							ent = MillCommonUtilities.spawnMobsSpawner(
									mw.world, spawn,
									Mill.ENTITY_TARGETED_WITHERSKELETON);
							if (ent != null) {
								nbSkel++;
							}
						}

						mw.getProfile(player.getDisplayName()).setActionData(
								"mayan_siege_status", "started");
						mw.getProfile(player.getDisplayName()).setActionData(
								"mayan_siege_ghasts", "" + nbGhasts);
						mw.getProfile(player.getDisplayName()).setActionData(
								"mayan_siege_blazes", "" + nbBlazes);
						mw.getProfile(player.getDisplayName()).setActionData(
								"mayan_siege_skeletons", "" + nbSkel);

						ServerSender.sendTranslatedSentence(player,
								MLN.LIGHTGREY, "actions.mayan_siege_start", ""
										+ nbGhasts, "" + nbBlazes, "" + nbSkel);
					}
				}
			}
		} else if (siegeStatus.equals("started")) {

			for (final Point p : mw.loneBuildingsList.pos) {
				final Building b = mw.getBuilding(p);
				if (b != null) {
					if (b.villageType.key.equals("questpyramid")
							&& p.distanceTo(player) < 50) {
						List<Entity> mobs = MillCommonUtilities
								.getEntitiesWithinAABB(mw.world,
										EntityTargetedGhast.class,
										b.location.pos, 128, 128);

						final int nbGhasts = mobs.size();

						mobs = MillCommonUtilities.getEntitiesWithinAABB(
								mw.world, EntityTargetedBlaze.class,
								b.location.pos, 128, 128);

						final int nbBlazes = mobs.size();

						mobs = MillCommonUtilities.getEntitiesWithinAABB(
								mw.world, EntityTargetedWitherSkeleton.class,
								b.location.pos, 128, 128);

						final int nbSkel = mobs.size();

						if (nbGhasts == 0 && nbBlazes == 0 && nbSkel == 0) {
							mw.getProfile(player.getDisplayName())
									.setActionData("mayan_siege_status",
											"finished");
							mw.getProfile(player.getDisplayName()).setTag(
									MAYANSIEGE + COMPLETE);
							ServerSender.sendTranslatedSentence(player,
									MLN.LIGHTGREY,
									"actions.mayan_siege_success");
						} else {
							final int oldGhasts = Integer.parseInt(mw
									.getProfile(player.getDisplayName())
									.getActionData("mayan_siege_ghasts"));
							final int oldBlazes = Integer.parseInt(mw
									.getProfile(player.getDisplayName())
									.getActionData("mayan_siege_blazes"));
							final int oldSkel = Integer.parseInt(mw.getProfile(
									player.getDisplayName()).getActionData(
									"mayan_siege_skeletons"));

							if (oldGhasts != nbGhasts || oldBlazes != nbBlazes
									|| oldSkel != nbSkel) {
								ServerSender.sendTranslatedSentence(player,
										MLN.LIGHTGREY,
										"actions.mayan_siege_update", ""
												+ nbGhasts, "" + nbBlazes, ""
												+ nbSkel);
								mw.getProfile(player.getDisplayName())
										.setActionData("mayan_siege_ghasts",
												"" + nbGhasts);
								mw.getProfile(player.getDisplayName())
										.setActionData("mayan_siege_blazes",
												"" + nbBlazes);
								mw.getProfile(player.getDisplayName())
										.setActionData("mayan_siege_skeletons",
												"" + nbSkel);
							}
						}
					}
				}
			}
		}

	}

	private static void handleTheVoid(final MillWorld mw,
			final EntityPlayer player) {

		if (!mw.getProfile(player.getDisplayName()).isTagSet(THEVOID)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						THEVOID + COMPLETE)) {
			return;
		}

		if (player.posY > 30) {
			return;
		}

		for (int i = -5; i < 5; i++) {
			for (int j = -5; j < 5; j++) {

				final Block block = mw.world.getBlock((int) player.posX + i, 0,
						(int) player.posZ + j);

				if (block == Blocks.air) {
					ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
							"actions.thevoid_success");
					mw.getProfile(player.getDisplayName()).clearTag(THEVOID);
					mw.getProfile(player.getDisplayName()).setTag(
							THEVOID + COMPLETE);
					return;
				}

			}
		}
	}

	private static void handleTopOfTheWorld(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(TOPOFTHEWORLD)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						TOPOFTHEWORLD + COMPLETE)) {
			return;
		}

		if (player.posY > 250) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.topoftheworld_success");
			mw.getProfile(player.getDisplayName()).clearTag(TOPOFTHEWORLD);
			mw.getProfile(player.getDisplayName()).setTag(
					TOPOFTHEWORLD + COMPLETE);
			return;
		}
	}

	private static void handleUnderwaterDive(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(UNDERWATER_DIVE)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						UNDERWATER_DIVE + COMPLETE)) {
			return;
		}

		Point p = new Point(player);

		int nbWater = 0;

		while (MillCommonUtilities.getBlock(mw.world, p) == Blocks.water) {
			nbWater++;
			p = p.getAbove();
		}

		if (nbWater > 12) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.underwaterdive_success");
			mw.getProfile(player.getDisplayName()).clearTag(UNDERWATER_DIVE);
			mw.getProfile(player.getDisplayName()).setTag(
					UNDERWATER_DIVE + COMPLETE);
			return;
		}
	}

	private static void handleUnderwaterGlass(final MillWorld mw,
			final EntityPlayer player) {
		if (!mw.getProfile(player.getDisplayName()).isTagSet(UNDERWATER_GLASS)
				|| mw.getProfile(player.getDisplayName()).isTagSet(
						UNDERWATER_GLASS + COMPLETE)) {
			return;
		}

		Point p = new Point(player);

		Block block = MillCommonUtilities.getBlock(mw.world, p);

		while (block != null && !MillCommonUtilities.isBlockOpaqueCube(block)
				&& block != Blocks.glass && block != Blocks.glass_pane) {
			p = p.getAbove();
			block = MillCommonUtilities.getBlock(mw.world, p);
		}

		block = MillCommonUtilities.getBlock(mw.world, p);

		if (block != Blocks.glass && block != Blocks.glass_pane) {
			return;
		}
		p = p.getAbove();
		int nbWater = 0;

		while (MillCommonUtilities.getBlock(mw.world, p) == Blocks.water) {
			nbWater++;
			p = p.getAbove();
		}

		if (nbWater > 15) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.underwaterglass_success");
			mw.getProfile(player.getDisplayName()).clearTag(UNDERWATER_GLASS);
			mw.getProfile(player.getDisplayName()).setTag(
					UNDERWATER_GLASS + COMPLETE);
			return;
		}

		if (nbWater > 1) {
			ServerSender.sendTranslatedSentence(player, MLN.LIGHTGREY,
					"actions.underwaterglass_notdeepenough");
		}
	}

	public static void onTick(final MillWorld mw, final EntityPlayer player) {

		long startTime;
		if (mw.lastWorldUpdate > 0) {
			startTime = Math.max(mw.lastWorldUpdate + 1,
					mw.world.getWorldTime() - 10);
		} else {
			startTime = mw.world.getWorldTime();
		}

		for (long worldTime = startTime; worldTime <= mw.world.getWorldTime(); worldTime++) {
			if (worldTime % 250 == 0) {
				handleContinuousExplore(mw, player, worldTime,
						MLN.questBiomeForest, "Zombie", 2, 15);
				handleContinuousExplore(mw, player, worldTime,
						MLN.questBiomeDesert, "Skeleton", 2, 15);
				handleContinuousExplore(mw, player, worldTime,
						MLN.questBiomeMountain, "Spider", 2, 10);
			}
			if (worldTime % 500 == 0) {
				handleUnderwaterGlass(mw, player);
			}
			if (worldTime % 100 == 0) {
				handleUnderwaterDive(mw, player);
				handleTopOfTheWorld(mw, player);
				handleBottomOfTheWorld(mw, player);
				handleBorehole(mw, player);
				handleBoreholeTNT(mw, player);
				handleTheVoid(mw, player);
				handleEnchantmentTable(mw, player);

			}
			if (worldTime % 10 == 0) {
				handleBoreholeTNTLit(mw, player);
				handleMayanSiege(mw, player);
			}
		}
	}
}
