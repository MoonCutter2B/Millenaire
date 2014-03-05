package org.millenaire.common.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import org.millenaire.common.Building;
import org.millenaire.common.Culture;
import org.millenaire.common.GuiActions;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingProject;
import org.millenaire.common.core.DevModUtilities;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class ServerReceiver {

	public static final String PACKET_CHANNEL = "millenaire";

	public static final int PACKET_BUILDING = 2;
	public static final int PACKET_VILLAGER = 3;
	public static final int PACKET_MILLCHEST = 5;
	public static final int PACKET_MAPINFO = 7;
	public static final int PACKET_VILLAGELIST = 9;
	public static final int PACKET_SERVER_CONTENT = 10;
	public static final int PACKET_SHOP = 11;

	public static final int PACKET_TRANSLATED_CHAT = 100;
	public static final int PACKET_PROFILE = 101;
	public static final int PACKET_QUESTINSTANCE = 102;
	public static final int PACKET_QUESTINSTANCEDELETE = 103;
	public static final int PACKET_OPENGUI = 104;
	public static final int PACKET_PANELUPDATE = 106;
	public static final int PACKET_ANIMALBREED = 107;
	public static final int PACKET_VILLAGER_SENTENCE = 108;

	public static final int PACKET_GUIACTION = 200;
	public static final int PACKET_VILLAGELIST_REQUEST = 201;
	public static final int PACKET_DECLARERELEASENUMBER = 202;
	public static final int PACKET_MAPINFO_REQUEST = 203;
	public static final int PACKET_VILLAGERINTERACT_REQUEST = 204;
	public static final int PACKET_AVAILABLECONTENT = 205;
	public static final int PACKET_DEVCOMMAND = 206;


	public static final int GUIACTION_CHIEF_BUILDING = 1;
	public static final int GUIACTION_CHIEF_CROP = 2;
	public static final int GUIACTION_CHIEF_CONTROL = 3;
	public static final int GUIACTION_CHIEF_DIPLOMACY = 4;
	public static final int GUIACTION_CHIEF_SCROLL = 5;

	public static final int GUIACTION_QUEST_COMPLETESTEP = 10;
	public static final int GUIACTION_QUEST_REFUSE = 11;

	public static final int GUIACTION_NEWVILLAGE = 20;

	public static final int GUIACTION_HIRE_HIRE = 30;
	public static final int GUIACTION_HIRE_EXTEND = 31;
	public static final int GUIACTION_HIRE_RELEASE = 32;

	public static final int GUIACTION_NEGATION_WAND = 40;

	public static final int GUIACTION_NEW_BUILDING_PROJECT = 50;

	public static final int GUIACTION_PUJAS_CHANGE_ENCHANTMENT = 60;

	public static final int GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED = 70;
	public static final int GUIACTION_CONTROLLEDBUILDING_FORGET = 71;

	public static final int GUIACTION_SUMMONINGWANDUSE = 80;
	public static final int GUIACTION_MILLCHESTACTIVATE = 81;
	public static final int GUIACTION_IMPORTBUILDING = 82;
	
	public static final int GUIACTION_MILITARY_RELATIONS = 90;
	public static final int GUIACTION_MILITARY_RAID = 91;
	public static final int GUIACTION_MILITARY_CANCEL_RAID = 92;

	public static final int DEV_COMMAND_TOGGLE_AUTO_MOVE = 1;
	public static final int DEV_COMMAND_TEST_PATH = 2;

	@SubscribeEvent
	public void onPacketData(ServerCustomPacketEvent event)
	{

		if (Mill.serverWorlds.size()==0)//normally only if ML has not loaded properly
			return;
	
		ByteBufInputStream dataStream = new ByteBufInputStream(event.packet.payload());
		EntityPlayerMP sender = ((NetHandlerPlayServer)event.handler).playerEntity;
		MillWorld mw=Mill.getMillWorld(sender.worldObj);

		if (mw==null) {//possibly the player is in the Nether or something of that kind
			mw=Mill.serverWorlds.firstElement();
		}
		
		if (mw==null) {
			MLN.error(this, "ServerReceiver.onPacketData: could not find MillWorld.");
		}

		try {
			final int packettype=dataStream.read();

			if (MLN.LogNetwork>=MLN.DEBUG) {
				MLN.debug(this, "Receiving packet type "+packettype);
			}

			if (packettype==PACKET_GUIACTION) {
				readGuiActionPacket(sender,dataStream);
			} else if (packettype==PACKET_MAPINFO_REQUEST) {
				readMapInfoRequestPacket(sender,dataStream);
			} else if (packettype==PACKET_VILLAGELIST_REQUEST) {
				mw.displayVillageList(sender,dataStream.readBoolean());
			} else if (packettype==PACKET_DECLARERELEASENUMBER) {
				mw.getProfile(sender.getDisplayName()).receiveDeclareReleaseNumberPacket(dataStream);
			} else if (packettype==PACKET_VILLAGERINTERACT_REQUEST) {
				readVillagerInteractRequestPacket(sender,dataStream);
			} else if (packettype==PACKET_AVAILABLECONTENT) {
				readAvailableContentPacket(sender,dataStream);
			} else if (packettype==PACKET_DEVCOMMAND) {
				readDevCommandPacket(sender,dataStream);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void readAvailableContentPacket(EntityPlayer player,
			ByteBufInputStream ds) {

		final HashMap<String,Integer> nbStrings=new HashMap<String,Integer>();
		final HashMap<String,Integer> nbBuildingNames=new HashMap<String,Integer>();
		final HashMap<String,Integer> nbSentences=new HashMap<String,Integer>();

		final HashMap<String,Integer> nbFallbackStrings=new HashMap<String,Integer>();
		final HashMap<String,Integer> nbFallbackBuildingNames=new HashMap<String,Integer>();
		final HashMap<String,Integer> nbFallbackSentences=new HashMap<String,Integer>();

		final HashMap<String,Vector<String>> planSets=new HashMap<String,Vector<String>>();
		final HashMap<String,Vector<String>> villagers=new HashMap<String,Vector<String>>();
		final HashMap<String,Vector<String>> villages=new HashMap<String,Vector<String>>();
		final HashMap<String,Vector<String>> lonebuildings=new HashMap<String,Vector<String>>();

		try {

			final String clientMainLanguage=ds.readUTF();
			final String clientFallbackLanguage=ds.readUTF();

			final int nbCultures=ds.readShort();

			for (int i=0;i<nbCultures;i++) {
				final String key=ds.readUTF();

				nbStrings.put(key, (int) ds.readShort());
				nbBuildingNames.put(key, (int) ds.readShort());
				nbSentences.put(key, (int) ds.readShort());

				nbFallbackStrings.put(key, (int) ds.readShort());
				nbFallbackBuildingNames.put(key, (int) ds.readShort());
				nbFallbackSentences.put(key, (int) ds.readShort());

				Vector<String> v=new Vector<String>();
				int nb=ds.readShort();
				for (int j=0;j<nb;j++) {
					v.add(ds.readUTF());
				}
				planSets.put(key, v);

				v=new Vector<String>();
				nb=ds.readShort();
				for (int j=0;j<nb;j++) {
					v.add(ds.readUTF());
				}
				villagers.put(key, v);

				v=new Vector<String>();
				nb=ds.readShort();
				for (int j=0;j<nb;j++) {
					v.add(ds.readUTF());
				}
				villages.put(key, v);

				v=new Vector<String>();
				nb=ds.readShort();
				for (int j=0;j<nb;j++) {
					v.add(ds.readUTF());
				}
				lonebuildings.put(key, v);
			}

			final ByteBufOutputStream data = ServerSender.getNewByteBufOutputStream();

			data.write(PACKET_SERVER_CONTENT);

			data.writeShort(Culture.vectorCultures.size());

			for (final Culture culture : Culture.vectorCultures) {
				if (!nbStrings.containsKey(culture.key)) {
					culture.writeCultureMissingContentPackPacket(data, clientMainLanguage,clientFallbackLanguage,
							0,0,0,0,0,0,null, null, null, null);
				} else {
					culture.writeCultureMissingContentPackPacket(data,clientMainLanguage,clientFallbackLanguage,
							nbStrings.get(culture.key),nbBuildingNames.get(culture.key),nbSentences.get(culture.key),
							nbFallbackStrings.get(culture.key),nbFallbackBuildingNames.get(culture.key),nbFallbackSentences.get(culture.key),
							planSets.get(culture.key),
							villagers.get(culture.key), villages.get(culture.key), lonebuildings.get(culture.key));
				}
			}

			ServerSender.createAndSendPacketToPlayer(data, player);

		} catch (final IOException e) {
			MLN.printException("Error in readAvailableContentPacket: ", e);
		}
	}

	private void readDevCommandPacket(EntityPlayer player,ByteBufInputStream data) {

		try {
			final int commandId=data.read();

			if (commandId==DEV_COMMAND_TOGGLE_AUTO_MOVE) {
				DevModUtilities.toggleAutoMove(player);
			} else if (commandId==DEV_COMMAND_TEST_PATH) {
				DevModUtilities.testPaths(player);
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}

	}
	
	

	private void readGuiActionPacket(EntityPlayer player,ByteBufInputStream data) {

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		try {
			final int guiActionId=data.read();


			if (guiActionId==GUIACTION_CHIEF_BUILDING) {
				final MillVillager v=mw.villagers.get(data.readLong());
				if (v!=null) {
					GuiActions.villageChiefPerformBuilding(player, v, data.readUTF());
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+guiActionId);
				}
			} else if (guiActionId==GUIACTION_CHIEF_CROP) {
				final MillVillager v=mw.villagers.get(data.readLong());
				if (v!=null) {
					GuiActions.villageChiefPerformCrop(player, v, data.readUTF());
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+guiActionId);
				}
			} else if (guiActionId==GUIACTION_CHIEF_CONTROL) {
				final MillVillager v=mw.villagers.get(data.readLong());
				if (v!=null) {
					GuiActions.villageChiefPerformCultureControl(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+guiActionId);
				}
			} else if (guiActionId==GUIACTION_CHIEF_DIPLOMACY) {
				final MillVillager v=mw.villagers.get(data.readLong());
				if (v!=null) {
					GuiActions.villageChiefPerformDiplomacy(player, v, StreamReadWrite.readNullablePoint(data),data.readBoolean());
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+guiActionId);
				}
			} else if (guiActionId==GUIACTION_CHIEF_SCROLL) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.villageChiefPerformVillageScroll(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_QUEST_COMPLETESTEP) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.questCompleteStep(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_QUEST_REFUSE) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.questRefuse(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_NEWVILLAGE) {
				final String cultureKey=data.readUTF();
				final String villageType=data.readUTF();
				final Point pos=StreamReadWrite.readNullablePoint(data);
				GuiActions.newVillageCreation(player, pos, cultureKey, villageType);

			} else if (guiActionId==GUIACTION_NEGATION_WAND) {
				final Point pos=StreamReadWrite.readNullablePoint(data);
				final Building th=mw.getBuilding(pos);
				if (th!=null) {
					GuiActions.useNegationWand(player, th);
				}

			} else if (guiActionId==GUIACTION_SUMMONINGWANDUSE) {
				final Point pos=StreamReadWrite.readNullablePoint(data);
				GuiActions.useSummoningWand(player, pos);

			} else if (guiActionId==GUIACTION_IMPORTBUILDING) {
				final Point pos=StreamReadWrite.readNullablePoint(data);
				
				EntityPlayerMP playerMP=(EntityPlayerMP)player;
				
				if (!Mill.proxy.isTrueServer() || playerMP.mcServer.getConfigurationManager().isPlayerOpped(playerMP.getDisplayName()))
					BuildingPlan.importBuilding(player,Mill.serverWorlds.firstElement().world, pos);
				else
					ServerSender.sendTranslatedSentence(player,MLN.DARKRED, "ui.serverimportforbidden");

			} else if (guiActionId==GUIACTION_MILLCHESTACTIVATE) {
				final Point pos=StreamReadWrite.readNullablePoint(data);
				GuiActions.activateMillChest(player, pos);


			} else if (guiActionId==GUIACTION_PUJAS_CHANGE_ENCHANTMENT) {
				final Point pos=StreamReadWrite.readNullablePoint(data);
				final Building temple=mw.getBuilding(pos);
				if ((temple!=null) && (temple.pujas!=null)) {
					GuiActions.pujasChangeEnchantment(player, temple, data.readShort());
				}

			} else if (guiActionId==GUIACTION_NEW_BUILDING_PROJECT) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final Point pos=StreamReadWrite.readNullablePoint(data);
				final String planKey=data.readUTF();
				final Building th=mw.getBuilding(thPos);
				if (th!=null) {
					GuiActions.newBuilding(player, th, pos, planKey);
				}

			} else if (guiActionId==GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final String projectKey=data.readUTF();
				final Point projectPos=StreamReadWrite.readNullablePoint(data);
				final boolean allow=data.readBoolean();
				final Building th=mw.getBuilding(thPos);
				if (th!=null) {
					BuildingProject project=null;

					for (final BuildingProject p:th.getFlatProjectList()) {
						if (p.key.equals(projectKey) && (p.location!=null) && p.location.pos.equals(projectPos)) {
							project=p;
						}
					}

					if (project!=null) {
						GuiActions.controlledBuildingsToggleUpgrades(player, th, project, allow);
					}
				}

			} else if (guiActionId==GUIACTION_CONTROLLEDBUILDING_FORGET) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final String projectKey=data.readUTF();
				final Point projectPos=StreamReadWrite.readNullablePoint(data);
				final Building th=mw.getBuilding(thPos);
				if (th!=null) {
					BuildingProject project=null;

					for (final BuildingProject p:th.getFlatProjectList()) {
						if (p.key.equals(projectKey) && (p.location!=null) && p.location.pos.equals(projectPos)) {
							project=p;
						}
					}

					if (project!=null) {
						GuiActions.controlledBuildingsForgetBuilding(player, th, project);
					}
				}

			} else if (guiActionId==GUIACTION_HIRE_HIRE) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.hireHire(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_HIRE_EXTEND) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.hireExtend(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_HIRE_RELEASE) {
				final long vid=data.readLong();
				final MillVillager v=mw.villagers.get(vid);
				if (v!=null) {
					GuiActions.hireRelease(player, v);
				} else {
					MLN.error(this, "Unknown villager id in readGUIPacket: "+vid);
				}
			} else if (guiActionId==GUIACTION_MILITARY_RELATIONS) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final Point targetpos=StreamReadWrite.readNullablePoint(data);
				final int amount=data.readInt();
				final Building th=mw.getBuilding(thPos);
				if (th!=null) {
					GuiActions.controlledMilitaryDiplomacy(player, th, targetpos, amount);
				}
			} else if (guiActionId==GUIACTION_MILITARY_RAID) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final Point targetpos=StreamReadWrite.readNullablePoint(data);
				final Building th=mw.getBuilding(thPos);
				final Building target=mw.getBuilding(targetpos);
				if (th!=null) {
					GuiActions.controlledMilitaryPlanRaid(player, th, target);
				}
			} else if (guiActionId==GUIACTION_MILITARY_CANCEL_RAID) {
				final Point thPos=StreamReadWrite.readNullablePoint(data);
				final Building th=mw.getBuilding(thPos);
				if (th!=null) {
					GuiActions.controlledMilitaryCancelRaid(player, th);
				}
			} else {
				MLN.error(null, "Unknown Gui action: "+guiActionId);
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	private void readMapInfoRequestPacket(EntityPlayer player,ByteBufInputStream data) {

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		try {
			final Point p=StreamReadWrite.readNullablePoint(data);

			final Building townhall=mw.getBuilding(p);

			if (townhall!=null) {
				townhall.sendMapInfo(player);
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	private void readVillagerInteractRequestPacket(EntityPlayer player,ByteBufInputStream data) {

		final MillWorld mw=Mill.getMillWorld(player.worldObj);

		try {
			final long vid=data.readLong();

			if (mw.villagers.containsKey(vid)) {
				mw.villagers.get(vid).interactSpecial(player);
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}


}
