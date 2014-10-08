package org.millenaire.common.core;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlanner;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;

public class DevModUtilities {

	private static class DevPathedEntity implements IAStarPathedEntity {

		World world;
		EntityPlayer caller;

		DevPathedEntity(final World w, final EntityPlayer p) {
			world = w;
			caller = p;
		}

		@Override
		public void onFoundPath(final List<AStarNode> result) {

			final int meta = MillCommonUtilities.randomInt(16);

			for (final AStarNode node : result) {
				if (node != result.get(0) && node != result.get(result.size() - 1)) {
					MillCommonUtilities.setBlockAndMetadata(world, new Point(node).getBelow(), Blocks.wool, meta);
				}
			}
		}

		@Override
		public void onNoPathAvailable() {
			ServerSender.sendChat(caller, EnumChatFormatting.DARK_RED, "No path available.");
		}

	}

	private static HashMap<EntityPlayer, Integer> autoMoveDirection = new HashMap<EntityPlayer, Integer>();

	private static HashMap<EntityPlayer, Integer> autoMoveTarget = new HashMap<EntityPlayer, Integer>();

	public static void fillInFreeGoods(final EntityPlayer player) {

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueLegs, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueBoots, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueHelmet, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBluePlate, 1);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedLegs, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedBoots, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedHelmet, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedPlate, 1);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardLegs, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardBoots, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardHelmet, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardPlate, 1);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.summoningWand, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.skoll_hati_amulet, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Items.clock, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanAxe, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanPickaxe, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanShovel, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.gold_block, 0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.log, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Items.coal, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.cobblestone, 128);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.stone, 512);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.sand, 128);
		MillCommonUtilities.putItemsInChest(player.inventory, Blocks.wool, 64);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.calva, 0, 2);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.chickencurry, 2);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.rice, 0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.maize, 0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.turmeric, 0, 64);

	}

	public static void runAutoMove(final World world) {
		for (final Object o : world.playerEntities) {

			if (o instanceof EntityPlayer) {
				final EntityPlayer p = (EntityPlayer) o;

				if (autoMoveDirection.containsKey(p)) {

					if (autoMoveDirection.get(p) == 1) {
						if (autoMoveTarget.get(p) < p.posX) {
							autoMoveDirection.put(p, -1);
							autoMoveTarget.put(p, (int) (p.posX - 100000));
							ServerSender.sendChat(p, EnumChatFormatting.GREEN, "Auto-move: turning back.");
						}
					} else if (autoMoveDirection.get(p) == -1) {
						if (autoMoveTarget.get(p) > p.posX) {
							autoMoveDirection.put(p, 1);
							autoMoveTarget.put(p, (int) (p.posX + 100000));
							ServerSender.sendChat(p, EnumChatFormatting.GREEN, "Auto-move: turning back again.");
						}
					}

					((EntityPlayerMP) p).playerNetServerHandler.setPlayerLocation(p.posX + autoMoveDirection.get(p) * 0.5, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
				}
			}
		}
	}

	public static void testPaths(final EntityPlayer player) {

		final Point centre = new Point(player);

		MLN.temp(null, "Attempting test path around: " + player);

		Point start = null;
		Point end = null;
		int toleranceMode = 0;

		for (int i = 0; i < 100 && (start == null || end == null); i++) {
			for (int j = 0; j < 100 && (start == null || end == null); j++) {
				for (int k = 0; k < 100 && (start == null || end == null); k++) {
					for (int l = 0; l < 8 && (start == null || end == null); l++) {
						final Point p = centre.getRelative(i * (1 - (l & 1) * 2), j * (1 - (l & 2)), k * (1 - (l & 4) / 2));

						final Block block = MillCommonUtilities.getBlock(player.worldObj, p);

						if (start == null && block == Blocks.gold_block) {
							start = p;
						}

						if (end == null && block == Blocks.iron_block) {
							end = p.getAbove();
							toleranceMode = 0;
						} else if (end == null && block == Blocks.diamond_block) {
							end = p.getAbove();
							toleranceMode = 1;
						} else if (end == null && block == Blocks.lapis_block) {
							end = p.getAbove();
							toleranceMode = 2;
						}
					}
				}
			}
		}

		if (start != null && end != null) {

			final DevPathedEntity pathedEntity = new DevPathedEntity(player.worldObj, player);

			AStarConfig jpsConfig;

			if (toleranceMode == 1) {
				jpsConfig = new AStarConfig(true, false, false, true, 2, 2);
			} else if (toleranceMode == 2) {
				jpsConfig = new AStarConfig(true, false, false, true, 2, 20);
			} else {
				jpsConfig = new AStarConfig(true, false, false, true);
			}

			ServerSender.sendChat(player, EnumChatFormatting.DARK_GREEN, "Calculating path. Tolerance H: " + jpsConfig.toleranceHorizontal);

			final AStarPathPlanner jpsPathPlanner = new AStarPathPlanner(player.worldObj, pathedEntity);
			jpsPathPlanner.getPath(start.getiX(), start.getiY(), start.getiZ(), end.getiX(), end.getiY(), end.getiZ(), jpsConfig);
		} else {
			ServerSender.sendChat(player, EnumChatFormatting.DARK_RED, "Could not find start or end: " + start + " - " + end);
		}

	}

	public static void toggleAutoMove(final EntityPlayer player) {

		if (autoMoveDirection.containsKey(player)) {
			autoMoveDirection.remove(player);
			autoMoveTarget.remove(player);
			ServerSender.sendChat(player, EnumChatFormatting.GREEN, "Auto-move disabled");
		} else {
			autoMoveDirection.put(player, 1);
			autoMoveTarget.put(player, (int) (player.posX + 100000));
			ServerSender.sendChat(player, EnumChatFormatting.GREEN, "Auto-move enabled");
		}
	}

	public static void villagerInteractDev(final EntityPlayer entityplayer, final MillVillager villager) {
		if (villager.isChild()) {
			villager.growSize();
			ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": Size: " + villager.size + " gender: " + villager.gender);
			if (entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() == Mill.summoningWand) {
				villager.size = MillVillager.MAX_CHILD_SIZE;
				villager.growSize();
			}
		}
		if (entityplayer.inventory.getCurrentItem() == null) {
			ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": Current goal: " + villager.getGoalLabel(villager.goalKey) + " Current pos: " + villager.getPos());
			ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": House: " + villager.housePoint + " Town Hall: " + villager.townHallPoint);
			ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": ID: " + villager.villager_id);
			if (villager.getRecord() != null) {
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": Spouse: " + villager.getRecord().spousesName);
			}

			if (villager.getPathDestPoint() != null && villager.pathEntity != null && villager.pathEntity.getCurrentPathLength() > 1) {
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN,
						villager.getName() + ": Dest: " + villager.getPathDestPoint() + " distance: " + villager.getPathDestPoint().distanceTo(villager) + " stuck: " + villager.longDistanceStuck
								+ " jump:" + villager.pathEntity.getNextTargetPathPoint());
			} else {
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, villager.getName() + ": No dest point.");
			}

			String s = "";

			if (villager.getRecord() != null) {
				for (final String tag : villager.getRecord().questTags) {
					s += tag + " ";
				}
			}

			if (villager.mw.getProfile(entityplayer.getDisplayName()).villagersInQuests.containsKey(villager.villager_id)) {
				s += " quest: " + villager.mw.getProfile(entityplayer.getDisplayName()).villagersInQuests.get(villager.villager_id).quest.key + "/"
						+ villager.mw.getProfile(entityplayer.getDisplayName()).villagersInQuests.get(villager.villager_id).getCurrentVillager().id;
			}

			if (s != null && s.length() > 0) {
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, "Tags: " + s);
			}

			s = "";

			for (final InvItem key : villager.inventory.keySet()) {
				s += key + ":" + villager.inventory.get(key) + " ";
			}

			if (s != null && s.length() > 0) {
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, "Inv: " + s);
			}

		} else if (entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Blocks.sand)) {
			if (villager.hiredBy == null) {
				villager.hiredBy = entityplayer.getDisplayName();
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, "Hired: " + entityplayer.getDisplayName());
			} else {
				villager.hiredBy = null;
				ServerSender.sendChat(entityplayer, EnumChatFormatting.GREEN, "No longer hired");
			}
		} else if (entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Blocks.dirt) && villager.pathEntity != null) {
			final int meta = MillCommonUtilities.randomInt(16);

			for (final PathPoint p : villager.pathEntity.pointsCopy) {
				if (villager.worldObj.getBlock(p.xCoord, p.yCoord - 1, p.zCoord) != Mill.lockedChest) {
					MillCommonUtilities.setBlockAndMetadata(villager.worldObj, new Point(p).getBelow(), Blocks.wool, meta);
				}
			}
			PathPoint p = villager.pathEntity.getCurrentTargetPathPoint();
			if (p != null && villager.worldObj.getBlock(p.xCoord, p.yCoord - 1, p.zCoord) != Mill.lockedChest) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, new Point(p).getBelow(), Blocks.gold_block, 0);
			}
			p = villager.pathEntity.getNextTargetPathPoint();
			if (p != null && villager.worldObj.getBlock(p.xCoord, p.yCoord - 1, p.zCoord) != Mill.lockedChest) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, new Point(p).getBelow(), Blocks.diamond_block, 0);
			}
			p = villager.pathEntity.getPreviousTargetPathPoint();
			if (p != null && villager.worldObj.getBlock(p.xCoord, p.yCoord - 1, p.zCoord) != Mill.lockedChest) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, new Point(p).getBelow(), Blocks.iron_block, 0);
			}
		}

		if (villager.hasChildren() && entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() == Mill.summoningWand) {
			final MillVillager child = villager.getHouse().createChild(villager, villager.getTownHall(), villager.getRecord().spousesName);
			if (child != null) {
				child.size = 20;
				child.growSize();
			}
		}
	}

}
