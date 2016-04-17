package org.millenaire.client.network;

import static io.netty.buffer.Unpooled.buffer;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import org.millenaire.common.Culture;
import org.millenaire.common.GuiActions;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingProject;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.StreamReadWrite;

/**
 * Class dedicate to sending packets from the client to the server
 * 
 * @author cedricdj
 * 
 */
public class ClientSender {

	public static void activateMillChest(final EntityPlayer player, final Point pos) {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILLCHESTACTIVATE);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in activateMillChest", e);
		}

		createAndSendServerPacket(data);
	}

	public static void controlledBuildingsForgetBuilding(final EntityPlayer player, final Building townHall, final BuildingProject project) {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CONTROLLEDBUILDING_FORGET);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			data.writeUTF(project.key);
			StreamReadWrite.writeNullablePoint(project.location.pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledBuildingsToggleUpgrades", e);
		}

		createAndSendServerPacket(data);

		GuiActions.controlledBuildingsForgetBuilding(player, townHall, project);
	}

	public static void controlledBuildingsToggleUpgrades(final EntityPlayer player, final Building townHall, final BuildingProject project, final boolean allow) {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			data.writeUTF(project.key);
			StreamReadWrite.writeNullablePoint(project.location.pos, data);
			data.writeBoolean(allow);
		} catch (final IOException e) {
			MLN.printException("Error in controlledBuildingsToggleUpgrades", e);
		}

		createAndSendServerPacket(data);

		GuiActions.controlledBuildingsToggleUpgrades(player, townHall, project, allow);
	}

	public static void controlledMilitaryCancelRaid(final EntityPlayer player, final Building th) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_CANCEL_RAID);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryCancelRaid", e);
		}

		createAndSendServerPacket(data);

		// for immediate feedback
		GuiActions.controlledMilitaryCancelRaid(player, th);
	}

	public static void controlledMilitaryDiplomacy(final EntityPlayer player, final Building th, final Point target, final int amount) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_RELATIONS);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
			StreamReadWrite.writeNullablePoint(target, data);
			data.writeInt(amount);

		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryDiplomacy", e);
		}

		createAndSendServerPacket(data);

		// for immediate feedback
		GuiActions.controlledMilitaryDiplomacy(player, th, target, amount);
	}

	public static void controlledMilitaryPlanRaid(final EntityPlayer player, final Building th, final Point target) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_RAID);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
			StreamReadWrite.writeNullablePoint(target, data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryStartRaid", e);
		}

		createAndSendServerPacket(data);

		// for immediate feedback
		GuiActions.controlledMilitaryPlanRaid(player, th, th.mw.getBuilding(target));
	}

	private static void createAndSendServerPacket(final ByteBufOutputStream bytes) {
		sendPacketToServer(createServerPacket(bytes));
	}

	public static C17PacketCustomPayload createServerPacket(final ByteBufOutputStream data) {
		return new C17PacketCustomPayload(ServerReceiver.PACKET_CHANNEL, data.buffer());
	}

	public static void devCommand(final int devcommand) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_DEVCOMMAND);
			data.write(devcommand);
		} catch (final IOException e) {
			MLN.printException("Error in devCommand", e);
		}

		createAndSendServerPacket(data);
	}

	public static void displayVillageList(final boolean loneBuildings) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_VILLAGELIST_REQUEST);
			data.writeBoolean(loneBuildings);
		} catch (final IOException e) {
			MLN.printException("Error in displayVillageList", e);
		}

		createAndSendServerPacket(data);
	}

	public static ByteBufOutputStream getNewByteBufOutputStream() {
		return new ByteBufOutputStream(buffer());
	}

	public static void hireExtend(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_EXTEND);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireExtend", e);
		}

		createAndSendServerPacket(data);

		GuiActions.hireExtend(player, villager);
	}

	public static void hireHire(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_HIRE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireHire", e);
		}

		createAndSendServerPacket(data);

		GuiActions.hireHire(player, villager);
	}

	public static void hireRelease(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_RELEASE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireRelease", e);
		}

		createAndSendServerPacket(data);

		GuiActions.hireRelease(player, villager);
	}

	public static void importBuilding(final EntityPlayer player, final Point pos) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_IMPORTBUILDING);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in importBuilding", e);
		}

		createAndSendServerPacket(data);
	}

	public static void negationWand(final EntityPlayer player, final Building townHall) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEGATION_WAND);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException("Error in negationWand", e);
		}

		createAndSendServerPacket(data);
	}

	public static void newBuilding(final EntityPlayer player, final Building townHall, final Point pos, final String planKey) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEW_BUILDING_PROJECT);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			StreamReadWrite.writeNullablePoint(pos, data);
			data.writeUTF(planKey);
		} catch (final IOException e) {
			MLN.printException("Error in newBuilding", e);
		}

		createAndSendServerPacket(data);
	}

	public static void newCustomBuilding(final EntityPlayer player, final Building townHall, final Point pos, final String planKey) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			StreamReadWrite.writeNullablePoint(pos, data);
			data.writeUTF(planKey);
		} catch (final IOException e) {
			MLN.printException("Error in newCustomBuilding", e);
		}

		createAndSendServerPacket(data);
	}

	public static void newVillageCreation(final EntityPlayer player, final Point pos, final String cultureKey, final String villageTypeKey) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEWVILLAGE);
			data.writeUTF(cultureKey);
			data.writeUTF(villageTypeKey);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in newVillageCreation", e);
		}

		createAndSendServerPacket(data);
	}

	public static void pujasChangeEnchantment(final EntityPlayer player, final Building temple, final int enchantmentId) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_PUJAS_CHANGE_ENCHANTMENT);
			StreamReadWrite.writeNullablePoint(temple.getPos(), data);
			data.writeShort(enchantmentId);
		} catch (final IOException e) {
			MLN.printException("Error in pujasChangeEnchantment", e);
		}

		createAndSendServerPacket(data);
	}

	public static void questCompleteStep(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_QUEST_COMPLETESTEP);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in questCompleteStep", e);
		}

		createAndSendServerPacket(data);
	}

	public static void questRefuse(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_QUEST_REFUSE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in questRefuse", e);
		}

		createAndSendServerPacket(data);
	}

	public static void requestMapInfo(final Building townHall) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_MAPINFO_REQUEST);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException(townHall + ": Error in sendUpdatePacket", e);
		}

		createAndSendServerPacket(data);
	}

	public static void sendAvailableContent() {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_AVAILABLECONTENT);

			data.writeUTF(MLN.effective_language);
			data.writeUTF(MLN.fallback_language);

			data.writeShort(Culture.ListCultures.size());

			for (final Culture culture : Culture.ListCultures) {
				culture.writeCultureAvailableContentPacket(data);
			}

		} catch (final IOException e) {
			MLN.printException("Error in displayVillageList", e);
		}

		createAndSendServerPacket(data);
	}

	public static void sendPacketToServer(final Packet packet) {
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
	}

	public static void sendVersionInfo() {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_DECLARERELEASENUMBER);
			data.writeUTF(Mill.versionNumber);
		} catch (final IOException e) {
			MLN.printException("Error in sendVersionInfo", e);
		}

		createAndSendServerPacket(data);
	}

	public static void summoningWandUse(final EntityPlayer player, final Point pos) {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_SUMMONINGWANDUSE);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in summoningWandUse", e);
		}

		createAndSendServerPacket(data);
	}

	public static void updateCustomBuilding(final EntityPlayer player, final Building building) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT);
			StreamReadWrite.writeNullablePoint(building.getPos(), data);
		} catch (final IOException e) {
			MLN.printException("Error in updateCustomBuilding", e);
		}

		createAndSendServerPacket(data);
	}

	public static void villageChiefPerformBuilding(final EntityPlayer player, final MillVillager chief, final String planKey) {

		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_BUILDING);
			data.writeLong(chief.villager_id);
			data.writeUTF(planKey);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformBuilding", e);
		}

		createAndSendServerPacket(data);
	}

	public static void villageChiefPerformCrop(final EntityPlayer player, final MillVillager chief, final String value) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_CROP);
			data.writeLong(chief.villager_id);
			data.writeUTF(value);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformCrop", e);
		}

		createAndSendServerPacket(data);
	}

	public static void villageChiefPerformCultureControl(final EntityPlayer player, final MillVillager chief) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_CONTROL);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformCultureControl", e);
		}

		createAndSendServerPacket(data);
	}

	public static void villageChiefPerformDiplomacy(final EntityPlayer player, final MillVillager chief, final Point village, final boolean praise) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_DIPLOMACY);
			data.writeLong(chief.villager_id);
			StreamReadWrite.writeNullablePoint(village, data);
			data.writeBoolean(praise);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformDiplomacy", e);
		}

		createAndSendServerPacket(data);

		// also in local for immediate feedback
		GuiActions.villageChiefPerformDiplomacy(player, chief, village, praise);
	}

	public static void villageChiefPerformVillageScroll(final EntityPlayer player, final MillVillager chief) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_SCROLL);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformVillageScroll", e);
		}

		createAndSendServerPacket(data);
	}

	public static void villagerInteractSpecial(final EntityPlayer player, final MillVillager villager) {
		final ByteBufOutputStream data = getNewByteBufOutputStream();

		try {
			data.write(ServerReceiver.PACKET_VILLAGERINTERACT_REQUEST);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villagerInteractSpecial", e);
		}

		createAndSendServerPacket(data);
	}
}
