package org.millenaire.client.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.millenaire.common.Building;
import org.millenaire.common.Culture;
import org.millenaire.common.GuiActions;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.StreamReadWrite;

public class ClientSender {

	public static INetworkManager networkManager=null;

	public static void activateMillChest(EntityPlayer player,
			Point pos) {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILLCHESTACTIVATE);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in activateMillChest", e);
		}

		createAndSendServerPacket(bytes);
	}

	public static void controlledBuildingsForgetBuilding(EntityPlayer player,
			Building townHall, BuildingProject project) {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CONTROLLEDBUILDING_FORGET);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			data.writeUTF(project.key);
			StreamReadWrite.writeNullablePoint(project.location.pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledBuildingsToggleUpgrades", e);
		}

		createAndSendServerPacket(bytes);

		GuiActions.controlledBuildingsForgetBuilding(player, townHall, project);
	}

	public static void controlledBuildingsToggleUpgrades(EntityPlayer player,
			Building townHall, BuildingProject project, boolean allow) {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

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

		createAndSendServerPacket(bytes);

		GuiActions.controlledBuildingsToggleUpgrades(player, townHall, project, allow);
	}

	private static void createAndSendServerPacket(ByteArrayOutputStream bytes) {


		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		sendPacketToServer(packet);
	}


	public static void devCommand(int devcommand) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_DEVCOMMAND);
			data.write(devcommand);
		} catch (final IOException e) {
			MLN.printException("Error in devCommand", e);
		}

		createAndSendServerPacket(bytes);
	}

	public static void displayVillageList(boolean loneBuildings) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_VILLAGELIST_REQUEST);
			data.writeBoolean(loneBuildings);
		} catch (final IOException e) {
			MLN.printException("Error in displayVillageList", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void hireExtend(EntityPlayer player, MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_EXTEND);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireExtend", e);
		}

		createAndSendServerPacket(bytes);

		GuiActions.hireExtend(player, villager);
	}


	public static void hireHire(EntityPlayer player, MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_HIRE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireHire", e);
		}

		createAndSendServerPacket(bytes);

		GuiActions.hireHire(player, villager);
	}


	public static void hireRelease(EntityPlayer player, MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_HIRE_RELEASE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in hireRelease", e);
		}

		createAndSendServerPacket(bytes);

		GuiActions.hireRelease(player, villager);
	}


	public static void importBuilding(EntityPlayer player, Point pos) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_IMPORTBUILDING);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in importBuilding", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void negationWand(EntityPlayer player, Building townHall) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEGATION_WAND);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
		} catch (final IOException e) {
			MLN.printException("Error in negationWand", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void newBuilding(EntityPlayer player, Building townHall, Point pos,
			String planKey) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEW_BUILDING_PROJECT);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
			StreamReadWrite.writeNullablePoint(pos, data);
			data.writeUTF(planKey);
		} catch (final IOException e) {
			MLN.printException("Error in newBuilding", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void newVillageCreation(EntityPlayer player, Point pos,
			String cultureKey, String villageTypeKey) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_NEWVILLAGE);
			data.writeUTF(cultureKey);
			data.writeUTF(villageTypeKey);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in newVillageCreation", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void pujasChangeEnchantment(EntityPlayer player, Building temple,
			int enchantmentId) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_PUJAS_CHANGE_ENCHANTMENT);
			StreamReadWrite.writeNullablePoint(temple.getPos(), data);
			data.writeShort(enchantmentId);
		} catch (final IOException e) {
			MLN.printException("Error in pujasChangeEnchantment", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void questCompleteStep(EntityPlayer player, MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_QUEST_COMPLETESTEP);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in questCompleteStep", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void questRefuse(EntityPlayer player, MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_QUEST_REFUSE);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in questRefuse", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void requestMapInfo(Building townHall) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream(bytes);

		try {
			ds.write(ServerReceiver.PACKET_MAPINFO_REQUEST);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), ds);
		} catch (final IOException e) {
			MLN.printException(townHall+": Error in sendUpdatePacket", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		sendPacketToServer(packet);
	}


	public static void sendAvailableContent() {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_AVAILABLECONTENT);

			data.writeUTF(MLN.effective_language);
			data.writeUTF(MLN.fallback_language);


			data.writeShort(Culture.vectorCultures.size());

			for (final Culture culture : Culture.vectorCultures) {
				culture.writeCultureAvailableContentPacket(data);
			}

		} catch (final IOException e) {
			MLN.printException("Error in displayVillageList", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		sendPacketToServer(packet);
	}


	public static void sendPacketToServer(Packet250CustomPayload packet) {
		networkManager.addToSendQueue(packet);
	}

	public static void sendVersionInfo() {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_DECLARERELEASENUMBER);
			data.writeUTF(Mill.versionNumber);
		} catch (final IOException e) {
			MLN.printException("Error in sendVersionInfo", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		sendPacketToServer(packet);
	}


	public static void summoningWandUse(EntityPlayer player,
			Point pos) {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_SUMMONINGWANDUSE);
			StreamReadWrite.writeNullablePoint(pos, data);
		} catch (final IOException e) {
			MLN.printException("Error in summoningWandUse", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void villageChiefPerformBuilding(EntityPlayer player,
			MillVillager chief, String planKey) {

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_BUILDING);
			data.writeLong(chief.villager_id);
			data.writeUTF(planKey);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformBuilding", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void villageChiefPerformCrop(EntityPlayer player,
			MillVillager chief, String value) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_CROP);
			data.writeLong(chief.villager_id);
			data.writeUTF(value);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformCrop", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void villageChiefPerformCultureControl(EntityPlayer player,
			MillVillager chief) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_CONTROL);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformCultureControl", e);
		}

		createAndSendServerPacket(bytes);
	}


	public static void villageChiefPerformDiplomacy(EntityPlayer player,
			MillVillager chief, Point village, boolean praise) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_DIPLOMACY);
			data.writeLong(chief.villager_id);
			StreamReadWrite.writeNullablePoint(village, data);
			data.writeBoolean(praise);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformDiplomacy", e);
		}

		createAndSendServerPacket(bytes);

		//also in local for immediate feedback
		GuiActions.villageChiefPerformDiplomacy(player, chief, village, praise);
	}

	public static void villageChiefPerformVillageScroll(EntityPlayer player,
			MillVillager chief) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_CHIEF_SCROLL);
			data.writeLong(chief.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villageChiefPerformVillageScroll", e);
		}

		createAndSendServerPacket(bytes);
	}
	
	public static void controlledMilitaryDiplomacy(EntityPlayer player,
			Building th,Point target,int amount) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_RELATIONS);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
			StreamReadWrite.writeNullablePoint(target, data);
			data.writeInt(amount);
			
			
			
		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryDiplomacy", e);
		}

		createAndSendServerPacket(bytes);
		
		//for immediate feedback
		GuiActions.controlledMilitaryDiplomacy(player, th, target, amount);
	}
	
	public static void controlledMilitaryPlanRaid(EntityPlayer player,
			Building th,Point target) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_RAID);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
			StreamReadWrite.writeNullablePoint(target, data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryStartRaid", e);
		}

		createAndSendServerPacket(bytes);
		
		//for immediate feedback
		GuiActions.controlledMilitaryPlanRaid(player, th, th.mw.getBuilding(target));
	}
	
	public static void controlledMilitaryCancelRaid(EntityPlayer player,
			Building th) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_GUIACTION);
			data.write(ServerReceiver.GUIACTION_MILITARY_CANCEL_RAID);
			StreamReadWrite.writeNullablePoint(th.getPos(), data);
		} catch (final IOException e) {
			MLN.printException("Error in controlledMilitaryCancelRaid", e);
		}

		createAndSendServerPacket(bytes);
		
		//for immediate feedback
		GuiActions.controlledMilitaryCancelRaid(player, th);
	}

	public static void villagerInteractSpecial(EntityPlayer player,
			MillVillager villager) {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_VILLAGERINTERACT_REQUEST);
			data.writeLong(villager.villager_id);
		} catch (final IOException e) {
			MLN.printException("Error in villagerInteractSpecial", e);
		}

		createAndSendServerPacket(bytes);
	}
}
