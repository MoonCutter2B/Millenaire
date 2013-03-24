package org.millenaire.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.Quest.QuestInstanceVillager;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;

public class UserProfile {

	public static final String OLD_PROFILE_SINGLE_PLAYER = "SinglePlayer";

	private static final int CULTURE_MAX_REPUTATION=64*64;
	private static final int CULTURE_MIN_REPUTATION=-10*64;

	public static final int UPDATE_ALL=1;
	public static final int UPDATE_REPUTATION=2;
	public static final int UPDATE_DIPLOMACY=3;
	public static final int UPDATE_ACTIONDATA=4;
	public static final int UPDATE_TAGS=5;
	public static final int UPDATE_LANGUAGE=6;
	public static final int UPDATE_GLOBAL_TAGS=7;

	public static UserProfile readProfile(MillWorld world, File dir) {

		final UserProfile profile = new UserProfile(world, dir.getName(),
				dir.getName());

		profile.loadProfileConfig(new File(profile.getDir(), "config.txt"));
		profile.loadProfileTags();
		profile.loadActionData(new File(profile.getDir(), "actiondata.txt"));
		profile.loadQuestInstances(new File(profile.getDir(), "quests.txt"));

		profile.loadLegacyFiles();

		return profile;
	}
	private final HashMap<Point, Integer> villageReputations = new HashMap<Point, Integer>();
	private final HashMap<Point, Byte> villageDiplomacy = new HashMap<Point, Byte>();
	private final HashMap<String, Integer> cultureReputations = new HashMap<String, Integer>();
	private final HashMap<String, Integer> cultureLanguages = new HashMap<String, Integer>();

	private final Vector<String> profileTags = new Vector<String>();

	public String key, playerName;
	public Vector<QuestInstance> questInstances = new Vector<QuestInstance>();
	public HashMap<Long, QuestInstance> villagersInQuests = new HashMap<Long, QuestInstance>();

	private final HashMap<String, String> actionData = new HashMap<String, String>();

	private final MillWorld mw;
	private boolean connectionActionDone=false;
	public boolean connected=false;

	private boolean showNewWorldMessageDone=false;

	public String releaseNumber=null;

	public HashMap<Point,Integer> panelsSent=new HashMap<Point,Integer>();
	public HashMap<Point,Long> buildingsSent=new HashMap<Point,Long>();


	public UserProfile(MillWorld world, String key, String name) {
		this.key = key;
		this.playerName = name;
		this.mw = world;
	}

	public void adjustDiplomacyPoint(Building b, int change) {

		int dp=0;

		if (villageDiplomacy.containsKey(b.getPos())) {
			dp=(villageDiplomacy.get(b.getPos()));
		}

		dp+=change;

		if (dp>5) {
			dp=5;
		}
		if (dp<0) {
			dp=0;
		}

		villageDiplomacy.put(b.getPos(), (byte)dp);

		saveProfileConfig();
		sendProfilePacket(UPDATE_DIPLOMACY);
	}

	public void adjustLanguage(String culture, int change) {

		if (cultureLanguages.containsKey(culture)) {
			cultureLanguages.put(culture, cultureLanguages.get(culture) + change);
		} else {
			cultureLanguages.put(culture, change);
		}

		saveProfileConfig();
		sendProfilePacket(UPDATE_LANGUAGE);
	}

	public void adjustReputation(Building b, int change) {

		if (b==null)
			return;

		if (villageReputations.containsKey(b.getPos())) {
			villageReputations.put(b.getPos(), villageReputations.get(b.getPos()) + change);
		} else {
			villageReputations.put(b.getPos(), change);
		}

		int rep=0;

		if (cultureReputations.containsKey(b.culture.key)) {
			rep=cultureReputations.get(b.culture.key);
		}

		rep+=change / 10;

		rep=Math.max(CULTURE_MIN_REPUTATION, rep);
		rep=Math.min(CULTURE_MAX_REPUTATION, rep);

		cultureReputations.put(b.culture.key,rep);

		if (rep<=CULTURE_MIN_REPUTATION) {

			int nbAwfulRep=0;

			for (final int cultureRep : cultureReputations.values()) {
				if (cultureRep<=CULTURE_MIN_REPUTATION) {
					nbAwfulRep++;
				}
			}

			if (nbAwfulRep>=3) {
				getPlayer().addStat(MillAchievements.attila, 1);
			}

		}


		saveProfileConfig();


		sendProfilePacket(UPDATE_REPUTATION);
	}

	public void changeProfileKey(String newKey) {
		MillCommonUtilities.deleteDir(getDir());
		key=newKey;
		saveProfile();
	}

	public void clearActionData(String key) {
		if (actionData.containsKey(key)) {
			actionData.remove(key);
			saveActionData();
			sendProfilePacket(UPDATE_ACTIONDATA);
		}
	}

	private void clearFarAwayPanels() {
		final Vector<Point> farAway=new Vector<Point>();

		final EntityPlayer player=getPlayer();


		for (final Point p : panelsSent.keySet()) {
			if (p.distanceToSquared(player)>(30*30)) {
				farAway.add(p);
			}
		}

		for (final Point p : farAway) {
			panelsSent.remove(p);
		}
	}

	public void clearTag(String tag) {
		if (profileTags.contains(tag)) {
			profileTags.remove(tag);
			saveProfileTags();
			sendProfilePacket(UPDATE_TAGS);
		}
	}

	public void connectUser() {
		connected=true;
		connectionActionDone=false;
	}

	private void deleteQuestInstances(long id) {
		final Vector<Long> toDelete=new Vector<Long>();
		for (final long vid : villagersInQuests.keySet()) {
			if (villagersInQuests.get(vid).uniqueid==id) {
				toDelete.add(vid);
			}
		}

		for (final long vid : toDelete) {
			villagersInQuests.remove(vid);
		}

		for (int i=questInstances.size()-1;i>=0;i--) {
			if (questInstances.get(i).uniqueid==id) {
				questInstances.remove(i);
			}
		}
	}

	public void disconnectUser() {
		connected=false;
		panelsSent.clear();
		buildingsSent.clear();

		if (MLN.Network>=MLN.MAJOR) {
			MLN.major(this, "Disconnected user.");
		}
	}

	public String getActionData(String key) {
		return actionData.get(key);
	}


	public int getCultureLanguageKnowledge(String key) {
		if (cultureLanguages.containsKey(key))
			return cultureLanguages.get(key);

		return 0;
	}

	public int getCultureReputation(String key) {
		if (cultureReputations.containsKey(key))
			return cultureReputations.get(key);

		return 0;
	}

	public int getDiplomacyPoints(Building b) {

		int dp = 0;
		if (villageDiplomacy.containsKey(b.getPos())) {
			dp = villageDiplomacy.get(b.getPos());
		}

		return dp;
	}

	private File getDir() {
		final File dir = new File(new File(mw.millenaireDir, "profiles"), key);

		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public EntityPlayer getPlayer() {
		return mw.world.getPlayerEntityByName(key);
	}

	public int getReputation(Building b) {

		int rep = 0;
		if (villageReputations.containsKey(b.getPos())) {
			rep = villageReputations.get(b.getPos());
		}

		if ((b.culture!=null) && cultureReputations.containsKey(b.culture.key)) {
			rep += cultureReputations.get(b.culture.key);
		}

		return rep;
	}

	public Vector<String> getWorldQuestStatus() {

		final Vector<String> res=new Vector<String>();

		boolean remaining=false;

		for (int i=0;i<Quest.WORLD_MISSION_NB.length;i++) {
			final String status=getActionData(Quest.WORLD_MISSION_KEYS[i]+"queststatus");
			final String chapterName=MLN.string("quest.cqchapter"+Quest.WORLD_MISSION_KEYS[i]);

			if (status==null) {
				res.add(MLN.string("quest.cqchapternotstarted",chapterName));
				res.add("");
				res.add(MLN.string("quest.cq"+Quest.WORLD_MISSION_KEYS[i]+"startexplanation"));
				remaining=true;
			} else {
				final int mission=Integer.parseInt(status);

				final int nbMission=Quest.WORLD_MISSION_NB[i];

				if (mission>=nbMission) {
					res.add(MLN.string("quest.cqchaptercompleted",chapterName));
				} else {

					res.add(MLN.string("quest.cqchapterinprogress",chapterName,""+mission,""+nbMission));
					remaining=true;
				}
			}
			res.add("");
		}

		if (!remaining) {
			res.add(MLN.string("quest.cqallcompleted"));
			res.add("");
			res.add(MLN.string("quest.cqcheckforupdates"));
		}

		return res;
	}

	private String getWorldQuestStatusShort() {
		String res=MLN.string("quest.creationqueststatusshort")+" ";
		for (int i=0;i<Quest.WORLD_MISSION_NB.length;i++) {
			final String status=getActionData(Quest.WORLD_MISSION_KEYS[i]+"queststatus");
			final String chapterName=MLN.string("quest.cqchapter"+Quest.WORLD_MISSION_KEYS[i]);

			if (status==null) {
				res+=MLN.string("quest.cqchapternotstartedshort",chapterName)+" ";
			} else {
				final int mission=Integer.parseInt(status);

				final int nbMission=Quest.WORLD_MISSION_NB[i];

				if (mission>=nbMission) {
					res+=MLN.string("quest.cqchaptercompletedshort",chapterName)+" ";
				} else {
					res+=MLN.string("quest.cqchapterinprogressshort",chapterName,""+mission,""+nbMission)+" ";
				}
			}
		}

		return res+" "+MLN.string("quest.cqcheckquestlistandhelp",Mill.proxy.getQuestKeyName());
	}

	public boolean isTagSet(String tag) {
		return profileTags.contains(tag);
	}

	public boolean isWorldQuestFinished() {
		boolean remaining=false;

		for (int i=0;i<Quest.WORLD_MISSION_NB.length;i++) {
			final String status=getActionData(Quest.WORLD_MISSION_KEYS[i]+"queststatus");
			if (status==null) {
				remaining=true;
			} else {
				final int mission=Integer.parseInt(status);

				final int nbMission=Quest.WORLD_MISSION_NB[i];

				if (mission>=nbMission) {
				} else {
					remaining=true;
				}
			}
		}

		return !remaining;
	}

	private void loadActionData(File dataFile) {
		actionData.clear();

		if (dataFile.exists()) {
			try {

				final BufferedReader reader = MillCommonUtilities.getReader(dataFile);
				String line = reader.readLine();

				while (line != null) {
					if ((line.trim().length() > 0)
							&& (line.split(":").length == 2)) {
						actionData.put(line.split(":")[0], line.split(":")[1]);
					}
					line = reader.readLine();
				}

				if (MLN.WorldGeneration >= MLN.MAJOR) {
					MLN.major(null, "Loaded " + actionData.size()
							+ " action data.");
				}

			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	/**
	 * Method to read files from non-MP Millenaire version, where there was no
	 * profile system
	 * 
	 * @param mw
	 */
	private void loadLegacyFiles() {

		final File millenaireDir = mw.millenaireDir;

		final File questDataFile = new File(millenaireDir, "quests.txt");

		if (questDataFile.exists()) {
			loadQuestInstances(questDataFile);

			for (final String tag : mw.globalTags) {
				setTag(tag);
			}

			final File dataFile = new File(millenaireDir, "actiondata.txt");

			if (dataFile != null) {
				loadActionData(dataFile);
			}

			final File configFile = new File(millenaireDir, "config.txt");

			if (configFile != null) {
				loadProfileConfig(configFile);
			}

			saveProfile();

			questDataFile.delete();
			if (dataFile != null) {
				dataFile.delete();
			}
		}
	}

	private void loadProfileConfig(File configFile) {
		if ((configFile != null) && configFile.exists()) {
			try {

				final BufferedReader reader = MillCommonUtilities.getReader(configFile);

				String line;

				while ((line = reader.readLine()) != null) {
					if ((line.trim().length() > 0) && !line.startsWith("//")) {
						final String[] temp = line.split("=");
						if (temp.length == 2) {

							final String key = temp[0];
							final String value = temp[1];

							if (key.equalsIgnoreCase("culture_reputation")) {
								final String c = value.split(",")[0];
								final int level = Integer.parseInt(value
										.split(",")[1]);
								cultureReputations.put(c, level);
							} else if (key.equalsIgnoreCase("culture_language")) {
								final String c = value.split(",")[0];
								final int level = Integer.parseInt(value
										.split(",")[1]);
								cultureLanguages.put(c, level);
							} else if (key.equalsIgnoreCase("village_reputations")) {
								final Point p = new Point(value.split(",")[0]);
								final int level = Integer.parseInt(value
										.split(",")[1]);
								villageReputations.put(p, level);
							} else if (key.equalsIgnoreCase("village_diplomacy")) {
								final Point p = new Point(value.split(",")[0]);
								final int level = Integer.parseInt(value
										.split(",")[1]);
								villageDiplomacy.put(p, (byte)level);
							}

						}
					}
				}
				reader.close();

			} catch (final IOException e) {
				MLN.printException(e);
			}
		}

		if (MLN.WorldGeneration >= MLN.MAJOR) {
			MLN.major(null, "Config loaded. generateVillages: "
					+ MLN.generateVillages);
		}
	}

	private void loadProfileTags() {
		final File tagsFile = new File(getDir(), "tags.txt");

		profileTags.clear();

		if (tagsFile.exists()) {
			try {

				final BufferedReader reader = MillCommonUtilities.getReader(tagsFile);
				String line = reader.readLine();

				while (line != null) {

					if (line.trim().length() > 0) {
						profileTags.add(line.trim());
					}
					line = reader.readLine();
				}

				if (MLN.WorldGeneration >= MLN.MAJOR) {
					MLN.major(this, "Loaded " + profileTags.size() + " tags.");
				}

			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	private void loadQuestInstances(File questDataFile) {
		questInstances.clear();
		villagersInQuests.clear();

		try {
			if ((questDataFile != null) && questDataFile.exists()) {
				final BufferedReader reader = MillCommonUtilities
						.getReader(questDataFile);

				String line;

				while ((line = reader.readLine()) != null) {
					if ((line.trim().length() > 0) && !line.startsWith("//")) {
						final QuestInstance qi = QuestInstance.loadFromString(
								mw, line, this);
						if (qi != null) {
							questInstances.add(qi);
							for (final QuestInstanceVillager qiv : qi.villagers
									.values()) {
								villagersInQuests.put(qiv.id, qi);
							}
						}
					}
				}
				reader.close();
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}

	}

	public void receiveDeclareReleaseNumberPacket(DataInputStream ds) {
		try {
			releaseNumber=ds.readUTF();

			if (MLN.Network>=MLN.MAJOR) {
				MLN.major(this, "Declared release number: "+releaseNumber);
			}

		} catch (final IOException e) {
			MLN.printException("Error in receiveDeclareReleaseNumberPacket", e);
		}
	}

	public void receiveProfilePacket(DataInputStream ds) {

		if (MLN.Network>=MLN.MINOR) {
			MLN.minor(null, "Receiving profile packet");
		}

		try {

			final int updateType=ds.read();

			int nb;

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_REPUTATION)) {
				nb=ds.readInt();
				villageReputations.clear();
				for (int i=0;i<nb;i++) {
					villageReputations.put(StreamReadWrite.readNullablePoint(ds), ds.readInt());
				}

				nb=ds.readInt();
				cultureReputations.clear();
				for (int i=0;i<nb;i++) {
					final String culture=ds.readUTF();
					final int rep=ds.readInt();
					cultureReputations.put(culture,rep );
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_LANGUAGE)) {
				nb=ds.readInt();
				cultureLanguages.clear();
				for (int i=0;i<nb;i++) {
					cultureLanguages.put(ds.readUTF(), ds.readInt());
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_DIPLOMACY)) {
				nb=ds.readInt();
				villageDiplomacy.clear();
				for (int i=0;i<nb;i++) {
					villageDiplomacy.put(StreamReadWrite.readNullablePoint(ds), ds.readByte());
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_ACTIONDATA)) {
				nb=ds.readInt();
				actionData.clear();
				for (int i=0;i<nb;i++) {
					actionData.put(ds.readUTF(), StreamReadWrite.readNullableString(ds));
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_TAGS)) {
				nb=ds.readInt();
				profileTags.clear();
				for (int i=0;i<nb;i++) {
					profileTags.add(ds.readUTF());
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_GLOBAL_TAGS)) {
				nb=ds.readInt();
				mw.globalTags.clear();
				for (int i=0;i<nb;i++) {
					mw.globalTags.add(ds.readUTF());
				}
			}

			showNewWorldMessage();



		} catch (final IOException e) {
			MLN.printException("Error in receiveProfilePacket", e);
		}
	}

	public void receiveQuestInstanceDeletePacket(DataInputStream ds) {
		try {
			deleteQuestInstances(ds.readLong());
		} catch (final IOException e) {
			MLN.printException("Error in receiveQuestInstanceDeletePacket", e);
		}
	}

	public void receiveQuestInstancePacket(DataInputStream ds) {
		try {
			final QuestInstance qi=StreamReadWrite.readNullableQuestInstance(mw,ds);

			deleteQuestInstances(qi.uniqueid);

			questInstances.add(qi);

			for (final String key : qi.villagers.keySet()) {
				villagersInQuests.put(qi.villagers.get(key).id, qi);
			}
		} catch (final IOException e) {
			MLN.printException("Error in receiveQuestInstancePacket", e);
		}
	}

	private void saveActionData() {

		if (mw.world.isRemote)
			return;

		final File configFile = new File(getDir(), "actiondata.txt");

		try {

			final BufferedWriter writer = MillCommonUtilities.getWriter(configFile);

			for (final String key : actionData.keySet()) {
				writer.write(key + ":" + actionData.get(key) + MLN.EOL);
			}
			writer.flush();

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public void saveProfile() {

		if (mw.world.isRemote)
			return;

		saveProfileConfig();
		saveProfileTags();
		saveQuestInstances();
		saveActionData();
	}

	private void saveProfileConfig() {

		if (mw.world.isRemote)
			return;

		final File configFile = new File(getDir(), "config.txt");

		try {

			final BufferedWriter writer = MillCommonUtilities.getWriter(configFile);

			for (final String c : cultureReputations.keySet()) {
				writer.write("culture_reputation=" + c + ","
						+ cultureReputations.get(c) + MLN.EOL);
			}

			for (final String c : cultureLanguages.keySet()) {
				writer.write("culture_language=" + c + ","
						+ cultureLanguages.get(c) + MLN.EOL);
			}

			for (final Point p : villageReputations.keySet()) {
				writer.write("village_reputations=" + p + ","
						+ villageReputations.get(p) + MLN.EOL);
			}

			for (final Point p : villageDiplomacy.keySet()) {
				writer.write("village_diplomacy=" + p + ","
						+ villageDiplomacy.get(p) + MLN.EOL);
			}

			writer.flush();

		} catch (final IOException e) {
			MLN.printException(e);
		}

	}

	private void saveProfileTags() {

		if (mw.world.isRemote)
			return;

		final File configFile = new File(getDir(), "tags.txt");

		try {

			final BufferedWriter writer = MillCommonUtilities.getWriter(configFile);

			for (final String tag : profileTags) {
				writer.write(tag + MLN.EOL);
			}
			writer.flush();

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public void saveQuestInstances() {

		if (mw.world.isRemote)
			return;

		final File questDataFile = new File(getDir(), "quests.txt");

		try {
			final BufferedWriter writer = MillCommonUtilities.getWriter(questDataFile);
			for (final QuestInstance qi : questInstances) {
				writer.write(qi.writeToString() + MLN.EOL);
			}
			writer.flush();
		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public void sendInitialPackets() {

		if (MLN.Network>=MLN.MAJOR) {
			MLN.major(this, "Sending initial packets.");
		}


		sendProfilePacket(UPDATE_ALL);

		for (final QuestInstance qi : questInstances) {
			sendQuestInstancePacket(qi);
		}

		//world.sendVillageListPacket(getPlayer());
	}

	public void sendProfilePacket(int updateType) {

		if (this.mw.world.isRemote)
			return;

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_PROFILE);
			data.write(updateType);

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_REPUTATION)) {
				data.writeInt(villageReputations.size());
				for (final Point p : villageReputations.keySet()) {
					StreamReadWrite.writeNullablePoint(p, data);
					data.writeInt(villageReputations.get(p));
				}

				data.writeInt(cultureReputations.size());
				for (final String culture : cultureReputations.keySet()) {
					data.writeUTF(culture);
					data.writeInt(cultureReputations.get(culture));
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_LANGUAGE)) {
				data.writeInt(cultureLanguages.size());
				for (final String culture : cultureLanguages.keySet()) {
					data.writeUTF(culture);
					data.writeInt(cultureLanguages.get(culture));
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_DIPLOMACY)) {
				data.writeInt(villageDiplomacy.size());
				for (final Point p : villageDiplomacy.keySet()) {
					StreamReadWrite.writeNullablePoint(p, data);
					data.write(villageDiplomacy.get(p));
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_ACTIONDATA)) {
				data.writeInt(actionData.size());
				for (final String key : actionData.keySet()) {
					data.writeUTF(key);
					StreamReadWrite.writeNullableString(actionData.get(key), data);
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_TAGS)) {
				data.writeInt(profileTags.size());
				for (final String tag : profileTags) {
					data.writeUTF(tag);
				}
			}

			if ((updateType==UPDATE_ALL) || (updateType==UPDATE_GLOBAL_TAGS)) {
				data.writeInt(mw.globalTags.size());
				for (final String tag : mw.globalTags) {
					data.writeUTF(tag);
				}
			}


		} catch (final IOException e) {
			MLN.printException(this+": Error in sendProfilePacket", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;



		ServerSender.sendPacketToPlayer(packet, key);
	}

	public void sendQuestInstanceDeletePacket(long id) {

		if (mw.world.isRemote)
			return;

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_QUESTINSTANCEDELETE);
			data.writeLong(id);
		} catch (final IOException e) {
			MLN.printException(this+": Error in sendQuestInstanceDeletePacket", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		ServerSender.sendPacketToPlayer(packet, key);
	}

	public void sendQuestInstancePacket(QuestInstance qi) {

		if (mw.world.isRemote)
			return;

		//first step, make sure all the villages involved are sent as well
		for (final QuestInstanceVillager qiv : qi.villagers.values()) {
			final Building th=qiv.getTownHall(mw.world);
			if (th!=null) {
				if (!buildingsSent.containsKey(th.getPos())) {
					th.sendBuildingPacket(getPlayer(), false);
				}
			}
		}

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.write(ServerReceiver.PACKET_QUESTINSTANCE);
			StreamReadWrite.writeNullableQuestInstance(qi, data);

		} catch (final IOException e) {
			MLN.printException(this+": Error in sendQuestInstancePacket", e);
		}

		final Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ServerReceiver.PACKET_CHANNEL;
		packet.data = bytes.toByteArray();
		packet.length = packet.data.length;

		if (MLN.Network>=MLN.MINOR) {
			MLN.minor(null, "Sending quest instance packet");
		}

		ServerSender.sendPacketToPlayer(packet, key);
	}

	public void setActionData(String key, String value) {
		if (!actionData.containsKey(key) || !actionData.get(key).equals(value)) {
			actionData.put(key, value);
			saveActionData();
			sendProfilePacket(UPDATE_ACTIONDATA);
		}
	}

	public void setTag(String tag) {
		if (!profileTags.contains(tag)) {
			profileTags.add(tag);
			saveProfileTags();
			sendProfilePacket(UPDATE_TAGS);
		}
	}

	public void showNewWorldMessage() {
		if (!showNewWorldMessageDone) {
			ServerSender.sendChat(getPlayer(), MLN.YELLOW,getWorldQuestStatusShort());
			showNewWorldMessageDone=true;
		}
	}


	public void testQuests() {

		if (!mw.world.isRemote) {
			boolean change = false;

			for (int i = questInstances.size() - 1; i >= 0; i--) {
				final QuestInstance qi = questInstances.get(i);
				change = change | qi.checkStatus(mw.world);
			}

			for (final Quest q : Quest.quests.values()) {
				final QuestInstance qi=q.testQuest(mw, this);
				change = change | (qi!=null);

				if (qi!=null) {
					sendQuestInstancePacket(qi);
				}
			}
			if (change) {
				saveQuestInstances();
			}
		}
	}

	@Override
	public String toString() {
		return "Profile: "+key+"/"+playerName;
	}

	public void updateProfile() {
		if (connected) {
			clearFarAwayPanels();

			if (!connectionActionDone && !mw.world.isRemote) {
				sendInitialPackets();
				connectionActionDone=true;
			}
			if (((mw.world.getWorldTime()%1000)==0) && mw.world.isDaytime()) {
				testQuests();
			}
		}
	}

}
