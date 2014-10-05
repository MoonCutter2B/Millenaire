package org.millenaire.common.network;

import static io.netty.buffer.Unpooled.buffer;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.UserProfile;
import org.millenaire.common.building.Building;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.CommonGuiHandler;
import org.millenaire.common.forge.Mill;

public class ServerSender {

	public static void createAndSendPacketToPlayer(final DataOutput data,
			final EntityPlayer player) {

		if (((EntityPlayerMP) player).playerNetServerHandler == null) {
			MLN.printException(new MillenaireException(
					"Trying to send packet to a player with null playerNetServerHandler"));
			return;
		}

		final Packet packet = createServerPacket(data);
		((EntityPlayerMP) player).playerNetServerHandler.sendPacket(packet);
	}

	public static S3FPacketCustomPayload createServerPacket(
			final DataOutput data) {
		return new S3FPacketCustomPayload(ServerReceiver.PACKET_CHANNEL,
				((ByteBufOutputStream) data).buffer());
	}

	public static void displayControlledMilitaryGUI(final EntityPlayer player,
			final Building townHall) {
		townHall.sendBuildingPacket(player, false);

		townHall.sendBuildingPacket(player, false);

		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		for (final Point p : townHall.getKnownVillages()) {
			final Building b = mw.getBuilding(p);
			if (b != null) {
				b.sendBuildingPacket(player, false);
			}

		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_CONTROLLEDMILITARYPANEL);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayControlledMilitaryGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayControlledProjectGUI(final EntityPlayer player,
			final Building townHall) {
		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_CONTROLLEDPROJECTPANEL);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayControlledProjectGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayHireGUI(final EntityPlayer player,
			final MillVillager villager) {

		villager.getTownHall().sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_HIRE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException(
					ServerSender.class + ": Error in displayHireGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayMerchantTradeGUI(final EntityPlayer player,
			final MillVillager villager) {
		final DataOutput data = getNewByteBufOutputStream();

		final int[] ids = MillCommonUtilities.packLong(villager.villager_id);

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_MERCHANT);
			data.writeInt(ids[0]);
			data.writeInt(ids[1]);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayMerchantTradeGUI", e);
		}

		villager.getHouse().sendBuildingPacket(player, true);
		villager.getTownHall().sendBuildingPacket(player, true);
		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_MERCHANT,
				player.worldObj, ids[0], ids[1], 0);

	}

	public static void displayMillChest(final EntityPlayer player,
			final Point chestPos) {

		final TileEntityMillChest chest = chestPos
				.getMillChest(player.worldObj);

		if (chest == null) {
			return;
		}

		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		if (chest.buildingPos != null) {
			final Building building = mw.getBuilding(chest.buildingPos);
			if (building != null) {
				building.sendBuildingPacket(player, true);
			} else {
				chest.buildingPos = null;
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
			MLN.printException(ServerSender.class
					+ ": Error in displayMillChest", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_MILL_CHEST,
				player.worldObj, chestPos.getiX(), chestPos.getiY(),
				chestPos.getiZ());
	}

	public static void displayNegationWandGUI(final EntityPlayer player,
			final Building townHall) {

		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEGATIONWAND);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayNegationWandGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayNewBuildingProjectGUI(final EntityPlayer player,
			final Building townHall, final Point pos) {
		townHall.sendBuildingPacket(player, false);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEWBUILDING);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayNewBuildingProjectGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayNewVillageGUI(final EntityPlayer player,
			final Point pos) {
		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_NEWVILLAGE);
			StreamReadWrite.writeNullablePoint(pos, data);

		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayNewVillageGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayPanel(final EntityPlayer player,
			final Point signPos) {

		final TileEntityPanel panel = signPos.getPanel(player.worldObj);

		if (panel == null) {
			return;
		}

		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		if (panel.buildingPos != null) {
			final Building building = mw.getBuilding(panel.buildingPos);
			if (building != null) {
				building.sendBuildingPacket(player, true);
			}
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_PANEL);
			StreamReadWrite.writeNullablePoint(signPos, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class + ": Error in displayPanel",
					e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayQuestGUI(final EntityPlayer player,
			final MillVillager villager) {
		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_QUEST);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayQuestGUI", e);
		}
		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayVillageBookGUI(final EntityPlayer player,
			final Point p) {

		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		final Building th = mw.getBuilding(p);

		if (th == null) {
			return;
		}

		th.sendBuildingPacket(player, true);

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_VILLAGEBOOK);
			StreamReadWrite.writeNullablePoint(p, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayQuestGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

	}

	public static void displayVillageChiefGUI(final EntityPlayer player,
			final MillVillager chief) {

		if (chief.getTownHall() == null) {
			MLN.error(chief, "Needed to send chief's TH but TH is null.");
			return;
		}

		chief.getTownHall().sendBuildingPacket(player, false);

		final MillWorld mw = Mill.getMillWorld(player.worldObj);

		for (final Point p : chief.getTownHall().getKnownVillages()) {
			final Building b = mw.getBuilding(p);
			if (b != null) {
				b.sendBuildingPacket(player, false);
			}

		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_VILLAGECHIEF);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayVillageChiefGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void displayVillageTradeGUI(final EntityPlayer player,
			final Building building) {

		building.computeShopGoods(player);

		building.sendShopPacket(player);

		building.sendBuildingPacket(player, true);

		if (!building.isTownhall) {
			building.getTownHall().sendBuildingPacket(player, false);
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_OPENGUI);
			data.write(CommonGuiHandler.GUI_TRADE);
			StreamReadWrite.writeNullablePoint(building.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in displayVillageTradeGUI", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);

		player.openGui(Mill.instance, CommonGuiHandler.GUI_TRADE,
				player.worldObj, building.getPos().getiX(), building.getPos()
						.getiY(), building.getPos().getiZ());
	}

	public static DataOutput getNewByteBufOutputStream() {
		final DataOutput data = new ByteBufOutputStream(buffer());
		return data;
	}

	public static void sendAnimalBreeding(final EntityAnimal animal) {
		final DataOutput data = getNewByteBufOutputStream();

		final Point pos = new Point(animal);

		try {
			data.write(ServerReceiver.PACKET_ANIMALBREED);

			StreamReadWrite.writeNullablePoint(pos, data);
			data.writeInt(animal.getEntityId());

		} catch (final IOException e) {
			MLN.printException(animal + ": Error in sendAnimalBreeding", e);
		}

		final S3FPacketCustomPayload packet = createServerPacket(data);

		sendPacketToPlayersInRange(packet, pos, 50);
	}

	public static void sendChat(final EntityPlayer player,
			final EnumChatFormatting colour, final String s) {
		final ChatComponentText chat = new ChatComponentText(s);
		chat.getChatStyle().setColor(colour);
		player.addChatMessage(chat);
	}

	public static void sendLockedChestUpdatePacket(
			final TileEntityMillChest chest, final EntityPlayer player) {
		final DataOutput data = getNewByteBufOutputStream();

		final Point pos = new Point(chest.xCoord, chest.yCoord, chest.zCoord);

		try {

			data.write(ServerReceiver.PACKET_MILLCHEST);

			StreamReadWrite.writeNullablePoint(pos, data);
			StreamReadWrite.writeNullablePoint(chest.buildingPos, data);
			data.writeBoolean(MLN.DEV);

			data.writeByte(chest.getSizeInventory());
			for (int i = 0; i < chest.getSizeInventory(); i++) {
				StreamReadWrite.writeNullableItemStack(chest.getStackInSlot(i),
						data);
			}

		} catch (final IOException e) {
			MLN.printException(chest + ": Error in sendUpdatePacket", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void sendPacketToPlayer(final Packet packet,
			final EntityPlayer player) {
		((EntityPlayerMP) player).playerNetServerHandler.sendPacket(packet);
	}

	public static void sendPacketToPlayersInRange(final DataOutput data,
			final Point p, final int range) {
		sendPacketToPlayersInRange(createServerPacket(data), p, range);
	}

	public static void sendPacketToPlayersInRange(final Packet packet,
			final Point p, final int range) {
		final MinecraftServer server = MinecraftServer.getServer();

		if (server == null) {// when game is closing?
			MLN.error(null,
					"Wanted to send a packet in sendPacketToPlayersInRange but server is null.");
			return;
		}

		final ServerConfigurationManager config = server
				.getConfigurationManager();

		config.sendToAllNear(p.x, p.y, p.z, range, 0, packet);
	}

	public static void sendTranslatedSentence(final EntityPlayer player,
			final char colour, final String code, final String... values) {

		if (player == null) {
			return;
		}

		if (!(player instanceof EntityPlayerMP)) {
			return;
		}

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
			MLN.printException(ServerSender.class
					+ ": Error in sendTranslatedSentence", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void sendTranslatedSentenceInRange(final World world,
			final Point p, final int range, final char colour,
			final String key, final String... values) {
		for (final Object oplayer : world.playerEntities) {
			final EntityPlayer player = (EntityPlayer) oplayer;
			if (p.distanceTo(player) < range) {
				sendTranslatedSentence(player, colour, key, values);
			}
		}
	}

	public static void sendVillagerSentence(final EntityPlayer player,
			final MillVillager v) {

		if (player == null) {
			return;
		}

		if (!(player instanceof EntityPlayerMP)) {
			return;
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_VILLAGER_SENTENCE);
			data.writeLong(v.villager_id);

		} catch (final IOException e) {
			MLN.printException(ServerSender.class
					+ ": Error in sendVillagerSentence", e);
		}

		sendPacketToPlayer(createServerPacket(data), player);
	}

	public static void sendVillageSentenceInRange(final World world,
			final Point p, final int range, final MillVillager v) {
		for (final Object oplayer : world.playerEntities) {
			final EntityPlayer player = (EntityPlayer) oplayer;
			if (p.distanceTo(player) < range) {
				sendVillagerSentence(player, v);
			}
		}
	}

	public static void updatePanel(final MillWorld mw, final Point p,
			final String[][] lines, final int type, final Point buildingPos,
			final long villager_id) {

		if (lines == null) {
			return;
		}

		final TileEntityPanel panel = p.getPanel(mw.world);

		if (panel != null) {
			panel.panelType = type;
			panel.buildingPos = buildingPos;
			panel.villager_id = villager_id;
		}

		String key = "" + type + ";" + buildingPos + ";" + villager_id + ";";

		final List<EntityPlayer> receivers = new ArrayList<EntityPlayer>();

		for (int i = 0; i < lines.length; i++) {
			for (int j = 0; j < lines[i].length; j++) {
				key += ";" + lines[i][j];
			}
		}

		final int keyHash = key.hashCode();

		for (final EntityPlayer player : MillCommonUtilities
				.getServerPlayers(mw.world)) {
			if (p.distanceToSquared(player) < 16 * 16) {
				final UserProfile profile = MillCommonUtilities
						.getServerProfile(mw.world, player.getDisplayName());

				if (!profile.panelsSent.containsKey(p)
						|| !profile.panelsSent.get(p).equals(keyHash)) {
					receivers.add(player);
				}
			}
		}

		if (receivers.size() == 0) {
			return;
		}

		final DataOutput data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_PANELUPDATE);
			StreamReadWrite.writeNullablePoint(p, data);
			data.write(type);
			StreamReadWrite.writeNullablePoint(buildingPos, data);
			data.writeLong(villager_id);
			StreamReadWrite.writeStringStringArray(lines, data);
		} catch (final IOException e) {
			MLN.printException(ServerSender.class + ": Error in updatePanel", e);
		}

		final S3FPacketCustomPayload packet = createServerPacket(data);

		for (final EntityPlayer player : receivers) {
			sendPacketToPlayer(packet, player);
			final UserProfile profile = MillCommonUtilities.getServerProfile(
					player.worldObj, player.getDisplayName());
			profile.panelsSent.put(p, keyHash);
		}
	}
}
