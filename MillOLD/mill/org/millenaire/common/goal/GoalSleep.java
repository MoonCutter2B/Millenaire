package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;

public class GoalSleep extends Goal {

	@Override
	public int actionDuration(final MillVillager villager) throws Exception {
		return 50;
	}

	@Override
	public boolean allowRandomMoves() throws Exception {
		return false;
	}

	@Override
	public boolean canBeDoneAtNight() {
		return true;
	}

	@Override
	public boolean canBeDoneInDayTime() {
		return false;
	}

	@Override
	public GoalInformation getDestination(final MillVillager villager) throws Exception {

		final World world = villager.worldObj;

		final Point sleepPos = villager.getHouse().getResManager().getSleepingPos();

		final List<Point> beds = new ArrayList<Point>();

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 6; k++) {
					for (int l = 0; l < 8; l++) {
						final Point p = sleepPos.getRelative(i * (1 - (l & 1) * 2), j * (1 - (l & 2)), k * (1 - (l & 4) / 2));

						final Block block = MillCommonUtilities.getBlock(world, p);

						if (block == Blocks.bed) {
							final int meta = MillCommonUtilities.getBlockMeta(world, p);

							if (!BlockBed.isBlockHeadOfBed(meta)) {

								boolean alreadyTaken = false;

								for (final MillVillager v : villager.getHouse().villagers) {
									if (v != villager && v.getGoalDestPoint() != null) {
										if (v.getGoalDestPoint().equals(p)) {
											alreadyTaken = true;
										}
									}
								}

								if (!alreadyTaken) {
									beds.add(p);
								}
							}
						}
					}
				}
			}
		}

		if (beds.size() > 0) {
			return packDest(beds.get(0), villager.getHouse());
		}

		final List<Point> feetPos = new ArrayList<Point>();

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 6; k++) {
					for (int l = 0; l < 8; l++) {
						Point p = sleepPos.getRelative(i * (1 - (l & 1) * 2), j * (1 - (l & 2)), k * (1 - (l & 4) / 2));

						// must be non-passable with two passable above
						if (!p.isBlockPassable(world) && p.getAbove().isBlockPassable(world) && p.getRelative(0, 2, 0).isBlockPassable(world)) {

							Point topBlock = MillCommonUtilities.findTopNonPassableBlock(world, p.getiX(), p.getiZ());

							// must be a roof above (to avoid villagers sleeping
							// outdoor)
							if (topBlock != null && topBlock.y > p.y + 1) {

								final float angle = villager.getBedOrientationInDegrees();

								int dx = 0, dz = 0;

								if (angle == 0) {
									dx = 1;
								} else if (angle == 90) {
									dz = 1;
								} else if (angle == 180) {
									dx = -1;
								} else if (angle == 270) {
									dz = -1;
								}

								final Point p2 = p.getRelative(dx, 0, dz);

								if (!p2.isBlockPassable(world) && p2.getAbove().isBlockPassable(world) && p2.getRelative(0, 2, 0).isBlockPassable(world)) {

									topBlock = MillCommonUtilities.findTopNonPassableBlock(world, p2.getiX(), p2.getiZ());

									if (topBlock != null && topBlock.y > p2.y + 1) {

										p = p.getAbove();

										boolean alreadyTaken = false;

										for (final MillVillager v : villager.getHouse().villagers) {
											if (v != villager && v.getGoalDestPoint() != null) {
												if (v.getGoalDestPoint().equals(p)) {
													alreadyTaken = true;
												}
												if (v.getGoalDestPoint().equals(p.getRelative(1, 0, 0))) {
													alreadyTaken = true;
												}
												if (v.getGoalDestPoint().equals(p.getRelative(0, 0, 1))) {
													alreadyTaken = true;
												}
												if (v.getGoalDestPoint().equals(p.getRelative(-1, 0, 0))) {
													alreadyTaken = true;
												}
												if (v.getGoalDestPoint().equals(p.getRelative(0, 0, -1))) {
													alreadyTaken = true;
												}
											}
										}
										if (!alreadyTaken) {
											feetPos.add(p);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		for (final MillVillager v : villager.getHouse().villagers) {
			if (v != villager && v.getGoalDestPoint() != null) {
				feetPos.remove(v.getGoalDestPoint());
				feetPos.remove(v.getGoalDestPoint().getRelative(1, 0, 0));
				feetPos.remove(v.getGoalDestPoint().getRelative(0, 0, 1));
				feetPos.remove(v.getGoalDestPoint().getRelative(-1, 0, 0));
				feetPos.remove(v.getGoalDestPoint().getRelative(0, 0, -1));
			}
		}

		if (feetPos.size() > 0) {
			return packDest(feetPos.get(0), villager.getHouse());
		}

		return packDest(sleepPos, villager.getHouse());
	}

	@Override
	public String labelKeyWhileTravelling(final MillVillager villager) {
		return key + "_travelling";
	}

	@Override
	public boolean performAction(final MillVillager villager) throws Exception {

		if (!villager.nightActionPerformed) {
			villager.nightActionPerformed = villager.performNightAction();
		}

		villager.shouldLieDown = true;

		final float angle = villager.getBedOrientationInDegrees();

		double dx = 0.5, dz = 0.5, fdx = 0, fdz = 0;

		if (angle == 0) {
			dx = 0.95;
			fdx = -10;
		} else if (angle == 90) {
			dz = 0.95;
			fdz = -10;
		} else if (angle == 180) {
			dx = 0.05;
			fdx = 10;
		} else if (angle == 270) {
			dz = 0.05;
			fdz = 10;
		}

		float floatingHeight;

		if (villager.getBlock(villager.getGoalDestPoint()) == Blocks.bed) {
			floatingHeight = 0.7f;
		} else {
			floatingHeight = 0.2f;
		}

		villager.setPosition(villager.getGoalDestPoint().x + dx, villager.getGoalDestPoint().y + floatingHeight, villager.getGoalDestPoint().z + dz);
		villager.facePoint(villager.getPos().getRelative(fdx, 1, fdz), 100, 100);

		return false;
	}

	@Override
	public int priority(final MillVillager villager) throws Exception {
		return 50;
	}

	@Override
	public int range(final MillVillager villager) {
		return 2;
	}

	@Override
	public boolean shouldVillagerLieDown() {
		return true;
	}
}
