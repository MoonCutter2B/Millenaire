package org.millenaire.common.network;

import static io.netty.buffer.Unpooled.buffer;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Vector;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.CommonGuiHandler;
import org.millenaire.common.forge.Mill;

public class ServerSender {

	public static void displayControlledProjectGUI(EntityPlayer player,Building townHall) {
		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_CONTROLLEDPROJECTPANEL);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayControlledProjectGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayControlledMilitaryGUI(EntityPlayer player,Building townHall) {
		townHall.sendBuildingPacket(player, false);
		
		townHall.sendBuildingPacket(player, false);

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		for (final Point p : townHall.getKnownVillages()) {
			final Building b = mw.getBuilding(p);
			if (b!=null) {
				b.sendBuildingPacket(player, false);
			}

		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_CONTROLLEDMILITARYPANEL);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayControlledMilitaryGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayHireGUI(EntityPlayer player, MillVillager villager) {

		villager.getTownHall().sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_HIRE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayHireGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayMerchantTradeGUI(EntityPlayer player, MillVillager villager) {
		final DataOutput data = getNewByteBufOutputStream();

		final int[] ids=MillCommonUtilities.packLong(villager.villager_id);

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_MERCHANT);
			data.writeInt(ids[0]);
			data.writeInt(ids[1]);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayMerchantTradeGUI", e);
		}


		villager.getHouse().sendBuildingPacket(player,true);
		villager.getTownHall().sendBuildingPacket(player,true);
		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_MERCHANT,player.worldObj,ids[0],ids[1],0);

	}


	public static void displayMillChest(EntityPlayer player, Point chestPos) {

		final TileEntityMillChest chest=chestPos.getMillChest(player.worldObj);

		if (chest==null)
			return;

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		if (chest.buildingPos!=null) {
			final Building building=mw.getBuilding(chest.buildingPos);
			if (building!=null) {
				building.sendBuildingPacket(player,true);
			} else {
				chest.buildingPos=null;
				chest.sendUpdatePacket(player);
			}
		} else {
			chest.sendUpdatePacket(player);
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_MILL_CHEST);
			StreamReadWrite.writeNullablePoint(chestPos, data);
			data.writeBoolean(chest.isLockedFor(player));
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayMillChest", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_MILL_CHEST, player.worldObj,chestPos.getiX(),chestPos.getiY(),chestPos.getiZ());
	}



	public static void displayNegationWandGUI(EntityPlayer player,Building townHall) {

		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEGATIONWAND);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayNegationWandGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayNewBuildingProjectGUI(EntityPlayer player,Building townHall, Point pos) {
		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEWBUILDING);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayNewBuildingProjectGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayNewVillageGUI(EntityPlayer player, Point pos) {
		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEWVILLAGE);
			StreamReadWrite.writeNullablePoint(pos, data);
			
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayNewVillageGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayPanel(EntityPlayer player, Point signPos) {

		final TileEntityPanel panel=signPos.getPanel(player.worldObj);

		if (panel==null)
			return;

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		if (panel.buildingPos!=null) {
			final Building building=mw.getBuilding(panel.buildingPos);
			if (building!=null) {
				building.sendBuildingPacket(player,true);
			}
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_PANEL);
			StreamReadWrite.writeNullablePoint(signPos, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayPanel", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayQuestGUI(EntityPlayer player, MillVillager villager) {
		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_QUEST);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayQuestGUI", e);
		}
		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayVillageBookGUI(EntityPlayer player,Point p) {

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		final Building th=mw.getBuilding(p);

		if (th==null)
			return;

		th.sendBuildingPacket(player, true);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_VILLAGEBOOK);
			StreamReadWrite.writeNullablePoint(p, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayQuestGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

	}


	public static void displayVillageChiefGUI(EntityPlayer player, MillVillager chief) {

		if (chief.getTownHall()==null) {
			MLN.error(chief, "Needed to send chief's TH but TH is null.");
			return;
		}

		chief.getTownHall().sendBuildingPacket(player, false);

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		for (final Point p : chief.getTownHall().getKnownVillages()) {
			final Building b = mw.getBuilding(p);
			if (b!=null) {
				b.sendBuildingPacket(player, false);
			}

		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_VILLAGECHIEF);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayVillageChiefGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}


	public static void displayVillageTradeGUI(EntityPlayer player, Building building) {
		
		building.computeShopGoods(player);

		building.sendShopPacket(player);
		
		building.sendBuildingPacket(player,true);

		if (!building.isTownhall) {
			building.getTownHall().sendBuildingPacket(player, false);
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_TRADE);
			StreamReadWrite.writeNullablePoint(building.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in displayVillageTradeGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_TRADE, player.worldObj,building.getPos().getiX(),building.getPos().getiY(),building.getPos().getiZ());
	}

	public static void sendChat(EntityPlayer player, EnumChatFormatting colour, String s) {
		ChatComponentText chat=new ChatComponentText(s);
		chat.getChatStyle().setColor(colour);
		player.addChatMessage(chat);
	}


	public static void sendLockedChestUpdatePacket(TileEntityMillChest chest,EntityPlayer player) {
		final DataOutput data = getNewByteBufOutputStream();

		final Point pos=new Point(chest.xCoord,chest.yCoord,chest.zCoord);

		try {

			data.write(ServerReceiver.PACKET_MILLCHEST);

			StreamReadWrite.writeNullablePoint(pos, data);
			StreamReadWrite.writeNullablePoint(chest.buildingPos, data);
			data.writeBoolean(MLN.DEV);

			data.writeByte(chest.getSizeInventory());
			for (int i=0;i<chest.getSizeInventory();i++) {
				StreamReadWrite.writeNullableItemStack(chest.getStackInSlot(i), data);
			}

		} catch (final IOException e) {
			MLN.printException(chest+": Error in sendUpdatePacket", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}
	
	public static void sendAnimalBreeding(EntityAnimal animal) {
		final DataOutput data = getNewByteBufOutputStream();

		final Point pos=new Point(animal);

		try {
			data.write(ServerReceiver.PACKET_ANIMALBREED);

			StreamReadWrite.writeNullablePoint(pos, data);
			data.writeInt(animal.getEntityId());
			
		} catch (final IOException e) {
			MLN.printException(animal+": Error in sendAnimalBreeding", e);
		}

		S3FPacketCustomPayload packet = createServerPacket(data);

		sendPacketToPlayersInRange(packet,pos,50);
	}
	
	public static void sendPacketToPlayersInRange(DataOutput data,Point p,int range) {
		sendPacketToPlayersInRange(createServerPacket(data),p,range);
	}

	public static void sendPacketToPlayersInRange(Packet packet,Point p,int range) {
		final MinecraftServer server=MinecraftServer.getServer();

		if (server==null) {//when game is closing?
			MLN.error(null, "Wanted to send a packet in sendPacketToPlayersInRange but server is null.");
			return;
		}		

		final ServerConfigurationManager config=server.getConfigurationManager();

		config.sendToAllNear(p.x, p.y, p.z, range, 0, packet);
	}

	public static void sendTranslatedSentence(EntityPlayer player, char colour,String code,
			String... values) {

		if (player==null)
			return;

		if (!(player instanceof EntityPlayerMP))
			return;

		final DataOutput data = getNewByteBufOutputStream();
		
		try {
			data.write(ServerReceiver.PACKET_TRANSLATED_CHAT);
			data.writeChar(colour);
			data.writeUTF(code);

			data.write(values.length);
			for (final String value : values) {
				StreamReadWrite.writeNullableString(value, data);
			}

		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in sendTranslatedSentence", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}
	
	public static void sendVillagerSentence(EntityPlayer player,MillVillager v) {

		if (player==null)
			return;

		if (!(player instanceof EntityPlayerMP))
			return;

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_VILLAGER_SENTENCE);
			data.writeLong(v.villager_id);

		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in sendVillagerSentence", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void sendTranslatedSentenceInRange(World world,Point p,int range,char colour,String key,String... values) {
		for (final Object oplayer : world.playerEntities) {
			final EntityPlayer player=(EntityPlayer)oplayer;
			if (p.distanceTo(player)<range) {
				sendTranslatedSentence(player, colour,key, values);
			}
		}
	}
	
	public static void sendVillageSentenceInRange(World world,Point p,int range,MillVillager v) {
		for (final Object oplayer : world.playerEntities) {
			final EntityPlayer player=(EntityPlayer)oplayer;
			if (p.distanceTo(player)<range) {
				sendVillagerSentence(player,v);
			}
		}
	}

	public static void updatePanel(MillWorld mw,Point p, String[][] lines, int type,
			Point buildingPos, long villager_id) {

		if (lines==null)
			return;

		final TileEntityPanel panel=p.getPanel(mw.world);

		if (panel!=null) {
			panel.panelType=type;
			panel.buildingPos=buildingPos;
			panel.villager_id=villager_id;
		}


		String key=""+type+";"+buildingPos+";"+villager_id+";";

		final Vector<EntityPlayer> receivers=new Vector<EntityPlayer>();

		for (int i=0;i<lines.length;i++) {
			for (int j=0;j<lines[i].length;j++) {
				key+=";"+lines[i][j];
			}
		}

		final int keyHash=key.hashCode();

		for (final EntityPlayer player : MillCommonUtilities.getServerPlayers(mw.world)) {
			if (p.distanceToSquared(player)<(16*16)) {
				final UserProfile profile=MillCommonUtilities.getServerProfile(mw.world,player.getDisplayName());

				if (!profile.panelsSent.containsKey(p) || !profile.panelsSent.get(p).equals(keyHash)) {
					receivers.add(player);
				}
			}
		}

		if (receivers.size()==0)
			return;

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_PANELUPDATE);
			StreamReadWrite.writeNullablePoint(p, data);
			data.write(type);
			StreamReadWrite.writeNullablePoint(buildingPos, data);
			data.writeLong(villager_id);
			StreamReadWrite.writeStringStringArray(lines, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class+": Error in updatePanel", e);
		}

		S3FPacketCustomPayload packet = createServerPacket(data);
		
		for (final EntityPlayer player : receivers) {
			sendPacketToPlayer(packet,player);
			final UserProfile profile=MillCommonUtilities.getServerProfile(player.worldObj,player.getDisplayName());
			profile.panelsSent.put(p, keyHash);
		}
	}
	
	public static DataOutput getNewByteBufOutputStream() {
		DataOutput data=new ByteBufOutputStream(buffer());
		return data;
	}
	
	public static S3FPacketCustomPayload createServerPacket(DataOutput data) {
		return new S3FPacketCustomPayload(ServerReceiver.PACKET_CHANNEL, ((ByteBufOutputStream) data).buffer());
	}
	
	public static void sendPacketToPlayer(Packet packet, EntityPlayer player) {
		((EntityPlayerMP)player).playerNetServerHandler.sendPacket(packet);
	}
	
	public static void createAndSendPacketToPlayer(DataOutput data, EntityPlayer player) {
		Packet packet=createServerPacket(data);
		((EntityPlayerMP)player).playerNetServerHandler.sendPacket(packet);
	}
}
