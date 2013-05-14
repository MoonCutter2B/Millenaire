package org.millenaire.common.core;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathPoint;
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

		DevPathedEntity(World w,EntityPlayer p) {
			world=w;
			caller=p;
		}

		@Override
		public void onFoundPath(ArrayList<AStarNode> result) {

			final int meta=MillCommonUtilities.randomInt(16);

			for (final AStarNode node : result) {
				if ((node!=result.get(0)) && (node!=result.get(result.size()-1))) {
					MillCommonUtilities.setBlockAndMetadata(world, (new Point(node)).getBelow(), Block.cloth.blockID, meta);
				}
			}
		}

		@Override
		public void onNoPathAvailable() {
			ServerSender.sendChat(caller,MLN.DARKRED,"No path available.");
		}

	}

	private static HashMap<EntityPlayer, Integer> autoMoveDirection=new HashMap<EntityPlayer, Integer>();

	private static HashMap<EntityPlayer, Integer> autoMoveTarget=new HashMap<EntityPlayer, Integer>();
	public static void fillInFreeGoods(EntityPlayer player) {

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueLegs.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueBoots.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBlueHelmet.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorBluePlate.itemID, 1);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedLegs.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedBoots.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedHelmet.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseWarriorRedPlate.itemID, 1);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardLegs.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardBoots.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardHelmet.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.japaneseGuardPlate.itemID, 1);


		MillCommonUtilities.putItemsInChest(player.inventory, Mill.summoningWand.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.skoll_hati_amulet.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Item.pocketSundial.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanAxe.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanPickaxe.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.normanShovel.itemID, 1);
		MillCommonUtilities.putItemsInChest(player.inventory, Block.blockGold.blockID,0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory,Block.wood.blockID, 64);
		MillCommonUtilities.putItemsInChest(player.inventory,Item.coal.itemID, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Block.cobblestone.blockID, 128);
		MillCommonUtilities.putItemsInChest(player.inventory, Block.stone.blockID, 512);
		MillCommonUtilities.putItemsInChest(player.inventory, Block.sand.blockID, 128);
		MillCommonUtilities.putItemsInChest(player.inventory, Block.cloth.blockID, 64);

		MillCommonUtilities.putItemsInChest(player.inventory, Mill.calva.itemID,0, 2);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.chickencurry.itemID,2);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.rice.itemID,0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.maize.itemID,0, 64);
		MillCommonUtilities.putItemsInChest(player.inventory, Mill.turmeric.itemID,0, 64);

	}


	public static void runAutoMove(World world) {
		for (final Object o : world.playerEntities) {

			if (o instanceof EntityPlayer) {
				final EntityPlayer p=(EntityPlayer)o;

				if (autoMoveDirection.containsKey(p)) {

					if (autoMoveDirection.get(p)==1) {
						if (autoMoveTarget.get(p)<p.posX) {
							autoMoveDirection.put(p, -1);
							autoMoveTarget.put(p, (int) (p.posX-100000));
							ServerSender.sendChat(p,MLN.LIGHTGREEN,"Auto-move: turning back.");
						}
					} else if (autoMoveDirection.get(p)==-1) {
						if (autoMoveTarget.get(p)>p.posX) {
							autoMoveDirection.put(p, 1);
							autoMoveTarget.put(p, (int) (p.posX+100000));
							ServerSender.sendChat(p,MLN.LIGHTGREEN,"Auto-move: turning back again.");
						}
					}

					((EntityPlayerMP)p).playerNetServerHandler.setPlayerLocation(p.posX+(autoMoveDirection.get(p)*0.5), p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
				}
			}
		}
	}

	public static void testPaths(EntityPlayer player) {

		final Point centre=new Point(player);

		MLN.temp(null, "Attempting test path around: "+player);

		Point start=null;
		Point end=null;
		int toleranceMode=0;

		for (int i=0;(i<100) && ((start==null) || (end==null));i++) {
			for (int j=0;(j<100) && ((start==null) || (end==null));j++) {
				for (int k=0;(k<100) && ((start==null) || (end==null));k++) {
					for (int l=0;(l<8) && ((start==null) || (end==null));l++) {
						final Point p=centre.getRelative(i*(1-((l&1)*2)), j*(1-(l&2)), k*(1-((l&4)/2)));

						final int bid=MillCommonUtilities.getBlock(player.worldObj, p);

						if ((start==null) && (bid==Block.blockGold.blockID)) {
							start=p;
						}

						if ((end==null) && (bid==Block.blockIron.blockID)) {
							end=p.getAbove();
							toleranceMode=0;
						} else if ((end==null) && (bid==Block.blockDiamond.blockID)) {
							end=p.getAbove();
							toleranceMode=1;
						} else if ((end==null) && (bid==Block.blockLapis.blockID)) {
							end=p.getAbove();
							toleranceMode=2;
						}
					}
				}
			}
		}

		if ((start!=null) && (end !=null)) {


			final DevPathedEntity pathedEntity=new DevPathedEntity(player.worldObj,player);

			AStarConfig jpsConfig;

			if (toleranceMode==1) {
				jpsConfig = new AStarConfig(true,false,false,true,2,2);
			} else if (toleranceMode==2) {
				jpsConfig = new AStarConfig(true,false,false,true,2,20);
			} else {
				jpsConfig = new AStarConfig(true,false,false,true);
			}

			ServerSender.sendChat(player,MLN.DARKGREEN,"Calculating path. Tolerance H: "+jpsConfig.toleranceHorizontal);


			final AStarPathPlanner jpsPathPlanner=new AStarPathPlanner(player.worldObj, pathedEntity);
			jpsPathPlanner.getPath(start.getiX(), start.getiY(), start.getiZ(), end.getiX(), end.getiY(), end.getiZ(), jpsConfig);
		} else {
			ServerSender.sendChat(player,MLN.DARKRED,"Could not find start or end: "+start+" - "+end);
		}

	}

	public static void toggleAutoMove(EntityPlayer player) {

		if (autoMoveDirection.containsKey(player)) {
			autoMoveDirection.remove(player);
			autoMoveTarget.remove(player);
			ServerSender.sendChat(player,MLN.LIGHTGREEN,"Auto-move disabled");
		} else {
			autoMoveDirection.put(player, 1);
			autoMoveTarget.put(player,(int) (player.posX+100000));
			ServerSender.sendChat(player,MLN.LIGHTGREEN,"Auto-move enabled");
		}
	}

	public static void villagerInteractDev(EntityPlayer entityplayer, MillVillager villager) {
		if (villager.isChild()) {
			villager.growSize();
			ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": Size: "+villager.size+" gender: "+villager.gender);
			if ((entityplayer.inventory.getCurrentItem() != null) && (entityplayer.inventory.getCurrentItem().itemID == Mill.summoningWand.itemID)) {
				villager.size=20;
				villager.growSize();
			}
		}
		if (entityplayer.inventory.getCurrentItem() == null) {
			ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": Current goal: "+villager.getGoalLabel(villager.goalKey)+" Current pos: "+villager.getPos());
			ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": House: "+villager.housePoint+" Town Hall: "+villager.townHallPoint);
			ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": ID: "+villager.villager_id);
			if (villager.getRecord()!=null) {
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": Spouse: "+villager.getRecord().spousesName);
			}

			if ((villager.getPathDestPoint() != null) && (villager.pathEntity != null) && (villager.pathEntity.getCurrentPathLength()>1)) {
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": Dest: "+villager.getPathDestPoint()+" distance: "+villager.getPathDestPoint().distanceTo(villager)+" stuck: "+villager.longDistanceStuck+" jump:"+villager.pathEntity.getNextTargetPathPoint());
			} else {
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,villager.getName()+": No dest point.");
			}

			String s="";

			if (villager.getRecord()!=null) {
				for (final String tag : villager.getRecord().questTags) {
					s+=tag+" ";
				}
			}


			if (villager.mw.getProfile(entityplayer.username).villagersInQuests.containsKey(villager.villager_id)) {
				s+=" quest: "+villager.mw.getProfile(entityplayer.username).villagersInQuests.get(villager.villager_id).quest.key+"/"+villager.mw.getProfile(entityplayer.username).villagersInQuests.get(villager.villager_id).getCurrentVillager().id;
			}

			if (s!="") {
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,"Tags: "+s);
			}
			
			s="";
			
			for (InvItem key : villager.inventory.keySet()) {
				s+=key+":"+villager.inventory.get(key)+" ";
			}
			
			if (s!="") {
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,"Inv: "+s);
			}

		} else if ((entityplayer.inventory.getCurrentItem() != null) && (entityplayer.inventory.getCurrentItem().itemID == Block.sand.blockID)) {
			if (villager.hiredBy==null) {
				villager.hiredBy=entityplayer.username;
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,"Hired: "+entityplayer.username);
			} else {
				villager.hiredBy=null;
				ServerSender.sendChat(entityplayer,MLN.LIGHTGREEN,"No longer hired");
			}
		} else if ((entityplayer.inventory.getCurrentItem() != null) && (entityplayer.inventory.getCurrentItem().itemID == Block.dirt.blockID) && (villager.pathEntity!=null)) {
			final int meta=MillCommonUtilities.randomInt(16);

			for (final PathPoint p :villager.pathEntity.pointsCopy) {
				if (villager.worldObj.getBlockId(p.xCoord, p.yCoord-1, p.zCoord)!=Mill.lockedChest.blockID) {
					MillCommonUtilities.setBlockAndMetadata(villager.worldObj, (new Point(p)).getBelow(), Block.cloth.blockID, meta);
				}
			}
			PathPoint p = villager.pathEntity.getCurrentTargetPathPoint();
			if ((p!=null) && (villager.worldObj.getBlockId(p.xCoord, p.yCoord-1, p.zCoord)!=Mill.lockedChest.blockID)) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, (new Point(p)).getBelow(), Block.blockGold.blockID, 0);
			}
			p = villager.pathEntity.getNextTargetPathPoint();
			if ((p!=null) && (villager.worldObj.getBlockId(p.xCoord, p.yCoord-1, p.zCoord)!=Mill.lockedChest.blockID)) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, (new Point(p)).getBelow(), Block.blockDiamond.blockID, 0);
			}
			p = villager.pathEntity.getPreviousTargetPathPoint();
			if ((p!=null) && (villager.worldObj.getBlockId(p.xCoord, p.yCoord-1, p.zCoord)!=Mill.lockedChest.blockID)) {
				MillCommonUtilities.setBlockAndMetadata(villager.worldObj, (new Point(p)).getBelow(), Block.blockIron.blockID, 0);
			}
		}

		if (villager.hasChildren() && (entityplayer.inventory.getCurrentItem() != null) && (entityplayer.inventory.getCurrentItem().itemID == Mill.summoningWand.itemID)) {
			final MillVillager child=villager.getHouse().createChild(villager, villager.getTownHall(), villager.getRecord().spousesName);
			if (child!=null) {
				child.size=20;
				child.growSize();
			}
		}
	}

}
