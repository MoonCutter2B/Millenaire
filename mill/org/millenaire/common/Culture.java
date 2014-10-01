package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.Culture.CultureLanguage.Dialogue;
import org.millenaire.common.Culture.CultureLanguage.ReputationLevel;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.building.Building;
import org.millenaire.common.building.BuildingLocation;
import org.millenaire.common.construction.BuildingPlan;
import org.millenaire.common.construction.BuildingPlanSet;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.core.MillCommonUtilities.ExtFileFilter;
import org.millenaire.common.core.MillCommonUtilities.WeightedChoice;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;
import org.millenaire.common.network.StreamReadWrite;

public class Culture {

	public static class CultureLanguage {

		public static class Dialogue implements WeightedChoice {

			private static String adult = "adult", child = "child",
					male = "male", female = "female", hasspouse = "hasspouse",
					nospouse = "nospouse", vtype = "vtype",
					notvtype = "notvtype";

			private static String rel_spouse = "spouse", rel_parent = "parent",
					rel_child = "child", rel_sibling = "sibling";

			private static String tag_raining = "raining",
					tag_notraining = "notraining";

			public String key = null;
			private int weight = 10;
			private final List<String> villager1 = new ArrayList<String>();
			private final List<String> villager2 = new ArrayList<String>();
			private final List<String> relations = new ArrayList<String>();
			private final List<String> not_relations = new ArrayList<String>();
			private final List<String> buildings = new ArrayList<String>();
			private final List<String> not_buildings = new ArrayList<String>();
			private final List<String> villagers = new ArrayList<String>();
			private final List<String> not_villagers = new ArrayList<String>();
			private final List<String> tags = new ArrayList<String>();

			public final List<Integer> timeDelays = new ArrayList<Integer>();
			public final List<Integer> speechBy = new ArrayList<Integer>();

			Dialogue(final String config) {
				this.key = null;
				for (final String s : config.split(",")) {
					if (s.split(":").length > 1) {
						final String key = s.split(":")[0].trim();
						String val = s.split(":")[1].trim();

						if (s.split(":").length > 2) {
							val += ":" + s.split(":")[2];
						}

						if (key.equals("key")) {
							this.key = val;
						} else if (key.equals("weigth")) {
							weight = Integer.parseInt(val);
						} else if (key.equals("v1")) {
							villager1.add(val);
						} else if (key.equals("v2")) {
							villager2.add(val);
						} else if (key.equals("rel")) {
							relations.add(val);
						} else if (key.equals("notrel")) {
							not_relations.add(val);
						} else if (key.equals("building")) {
							buildings.add(val);
						} else if (key.equals("notbuilding")) {
							not_buildings.add(val);
						} else if (key.equals("villager")) {
							villagers.add(val);
						} else if (key.equals("notvillager")) {
							not_villagers.add(val);
						} else if (key.equals("tag")) {
							tags.add(val);
						} else {
							MLN.error(this, "Could not recognise key " + key
									+ " in dialogue declaration " + config);
						}

					}
				}
			}

			public void checkData(final Culture culture, final String language) {

				for (final String s : villager1) {
					if (!s.equals(adult) && !s.equals(child) && !s.equals(male)
							&& !s.equals(female) && !s.equals(hasspouse)
							&& !s.equals(nospouse)
							&& !s.startsWith(vtype + ":")
							&& !s.startsWith(notvtype + ":")) {
						MLN.error(culture, language
								+ ": Unknown v1 setting in dialogue " + key
								+ ": " + s);
					}

					if (s.startsWith(vtype + ":")
							|| s.startsWith(notvtype + ":")) {
						final String s2 = s.split(":")[1].trim();

						for (String vtype : s2.split("-")) {
							vtype = vtype.trim();
							if (!culture.villagerTypes.containsKey(vtype)) {
								MLN.error(
										culture,
										language
												+ ": Unknown villager type in dialogue "
												+ key + ": " + s);
							}
						}
					}
				}
				for (final String s : villager2) {
					if (!s.equals(adult) && !s.equals(child) && !s.equals(male)
							&& !s.equals(female) && !s.equals(hasspouse)
							&& !s.equals(nospouse)
							&& !s.startsWith(vtype + ":")
							&& !s.startsWith(notvtype + ":")) {
						MLN.error(culture, language
								+ ": Unknown v2 setting in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : relations) {
					if (!s.equals(rel_spouse) && !s.equals(rel_parent)
							&& !s.equals(rel_child) && !s.equals(rel_sibling)) {
						MLN.error(culture, language
								+ ": Unknown rel setting in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : not_relations) {
					if (!s.equals(rel_spouse) && !s.equals(rel_parent)
							&& !s.equals(rel_child) && !s.equals(rel_sibling)) {
						MLN.error(culture, language
								+ ": Unknown notrel setting in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : tags) {
					if (!s.equals(tag_raining) && !s.equals(tag_notraining)) {
						MLN.error(culture, language
								+ ": Unknown tag in dialogue " + key + ": " + s);
					}
				}
				for (final String s : buildings) {
					if (!culture.planSet.containsKey(s)) {
						MLN.error(culture, language
								+ ": Unknown building in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : not_buildings) {
					if (!culture.planSet.containsKey(s)) {
						MLN.error(culture, language
								+ ": Unknown notbuilding in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : villagers) {
					if (!culture.villagerTypes.containsKey(s)) {
						MLN.error(culture, language
								+ ": Unknown villager in dialogue " + key
								+ ": " + s);
					}
				}
				for (final String s : not_villagers) {
					if (!culture.villagerTypes.containsKey(s)) {
						MLN.error(culture, language
								+ ": Unknown notvillager in dialogue " + key
								+ ": " + s);
					}
				}
			}

			public boolean compareWith(final Dialogue d,
					final List<String> errors) throws IOException {

				boolean differentConfig = false;

				if (weight != d.weight) {
					differentConfig = true;
				}

				if (!sameLists(villager1, d.villager1)) {
					differentConfig = true;
				}
				if (!sameLists(villager2, d.villager2)) {
					differentConfig = true;
				}
				if (!sameLists(relations, d.relations)) {
					differentConfig = true;
				}
				if (!sameLists(not_relations, d.not_relations)) {
					differentConfig = true;
				}
				if (!sameLists(buildings, d.buildings)) {
					differentConfig = true;
				}
				if (!sameLists(not_buildings, d.not_buildings)) {
					differentConfig = true;
				}
				if (!sameLists(villagers, d.villagers)) {
					differentConfig = true;
				}
				if (!sameLists(not_villagers, d.not_villagers)) {
					differentConfig = true;
				}
				if (!sameLists(tags, d.tags)) {
					differentConfig = true;
				}

				if (differentConfig) {
					errors.add("Dialogue has different configurations: " + key);
				}

				boolean differentSentences;

				if (timeDelays.size() != d.timeDelays.size()) {
					differentSentences = true;
					errors.add("Dialogue has different sentence numbers: "
							+ key);
				} else {
					differentSentences = !sameLists(timeDelays, d.timeDelays)
							|| !sameLists(speechBy, d.speechBy);

					if (differentSentences) {
						errors.add("Dialogue has different sentence settings: "
								+ key);
					}
				}

				return !differentSentences && !differentConfig;

			}

			@Override
			public int getChoiceWeight(final EntityPlayer player) {
				return weight;
			}

			private boolean isBuildingCompatible(final Building townHall) {

				for (final String s : buildings) {
					boolean found = false;

					for (final BuildingLocation bl : townHall.getLocations()) {
						if (bl.key.equals(s)) {
							found = true;
						}
					}

					if (!found) {
						return false;
					}
				}

				for (final String s : not_buildings) {
					for (final BuildingLocation bl : townHall.getLocations()) {
						if (bl.key.equals(s)) {
							return false;
						}
					}
				}

				return true;
			}

			private boolean isCompatible(final List<String> req,
					final MillVillager v) {

				if (v.getRecord() == null) {
					return false;
				}

				for (final String s : req) {

					final String key = s.split(":")[0];
					String val = null;

					if (s.split(":").length > 1) {
						val = s.split(":")[1];
					}

					if (key.equals(adult)) {
						if (v.vtype.isChild) {
							return false;
						}
					} else if (key.equals(child)) {
						if (!v.vtype.isChild) {
							return false;
						}
					} else if (key.equals(male)) {
						if (v.vtype.gender != MillVillager.MALE) {
							return false;
						}
					} else if (key.equals(female)) {
						if (v.vtype.gender != MillVillager.FEMALE) {
							return false;
						}
					} else if (key.equals(vtype)) {
						boolean found = false;
						for (final String type : val.split("-")) {
							if (type.equals(v.vtype.key)) {
								found = true;
							}
						}
						if (!found) {
							return false;
						}
					} else if (key.equals(notvtype)) {
						for (final String type : val.split("-")) {
							if (type.equals(v.vtype.key)) {
								return false;
							}
						}
					} else if (key.equals(hasspouse)) {
						if (v.getRecord().spousesName == null
								|| v.getRecord().spousesName.equals("")) {
							return false;
						}
					} else if (v.getRecord().spousesName != null
							&& key.equals(nospouse)) {
						if (!v.getRecord().spousesName.equals("")) {
							return false;
						}
					} else if (key.equals(female)) {
						if (v.vtype.gender != MillVillager.FEMALE) {
							return false;
						}
					}
				}
				return true;
			}

			private boolean isRelCompatible(final MillVillager v1,
					final MillVillager v2) {

				for (final String s : relations) {

					final String key = s.split(":")[0];

					if (key.equals(rel_spouse)) {
						if (v1.getSpouse() != v2) {
							return false;
						}
					} else if (key.equals(rel_parent)) {
						if (!v1.getRecord().fathersName.equals(v2.getName())
								&& !v1.getRecord().mothersName.equals(v2
										.getName())) {
							return false;
						}
					} else if (key.equals(rel_child)) {
						if (!v2.getRecord().fathersName.equals(v1.getName())
								&& !v2.getRecord().mothersName.equals(v1
										.getName())) {
							return false;
						}
					} else if (key.equals(rel_sibling)) {
						if (!v2.getRecord().mothersName
								.equals(v1.getRecord().mothersName)) {
							return false;
						}
					}
				}

				for (final String s : not_relations) {

					final String key = s.split(":")[0];

					if (key.equals(rel_spouse)) {
						if (v1.getSpouse() == v2) {
							return false;
						}
					} else if (key.equals(rel_parent)) {
						if (v1.getRecord().fathersName.equals(v2.getName())
								|| v1.getRecord().mothersName.equals(v2
										.getName())) {
							return false;
						}
					} else if (key.equals(rel_child)) {
						if (v2.getRecord().fathersName.equals(v1.getName())
								|| v2.getRecord().mothersName.equals(v1
										.getName())) {
							return false;
						}
					} else if (key.equals(rel_sibling)) {
						if (v2.getRecord().mothersName
								.equals(v1.getRecord().mothersName)) {
							return false;
						}
					}
				}

				return true;
			}

			private boolean isTagCompatible(final Building townHall) {

				for (final String s : tags) {
					if (s.equals(tag_raining)) {
						if (!townHall.worldObj.isRaining()) {
							return false;
						}
					} else if (s.equals(tag_notraining)) {
						if (townHall.worldObj.isRaining()) {
							return false;
						}
					}
				}

				return true;
			}

			public boolean isValidFor(final MillVillager v1,
					final MillVillager v2) {
				return isCompatible(villager1, v1)
						&& isCompatible(villager2, v2)
						&& isRelCompatible(v1, v2)
						&& isBuildingCompatible(v1.getTownHall())
						&& isVillagersCompatible(v1.getTownHall())
						&& isTagCompatible(v1.getTownHall());
			}

			private boolean isVillagersCompatible(final Building townHall) {

				for (final String s : villagers) {
					boolean found = false;

					for (final VillagerRecord vr : townHall.vrecords) {
						if (vr.type.equals(s)) {
							found = true;
						}
					}

					if (!found) {
						return false;
					}
				}

				for (final String s : not_villagers) {
					for (final VillagerRecord vr : townHall.vrecords) {
						if (vr.type.equals(s)) {
							return false;
						}
					}
				}

				return true;
			}

			private boolean sameLists(final List<?> v, final List<?> v2) {

				if (v.size() != v2.size()) {
					return false;
				}

				for (int i = 0; i < v.size(); i++) {
					if (!v.get(i).equals(v2.get(i))) {
						return false;
					}
				}

				return true;
			}

			public int validRoleFor(final MillVillager v) {

				if (isCompatible(villager1, v)) {
					return 1;
				}
				if (isCompatible(villager2, v)) {
					return 2;
				}

				return 0;
			}

		}

		public static class ReputationLevel implements
				Comparable<ReputationLevel> {
			private final String label, desc;
			public int level;

			public ReputationLevel(final File file, final String line) {
				try {
					level = MillCommonUtilities.readInteger(line.split(";")[0]);
				} catch (final Exception e) {
					level = 0;
					MLN.error(null,
							"Error when reading reputation line in file "
									+ file.getAbsolutePath() + ": " + line
									+ " : " + e.getMessage());
				}
				label = line.split(";")[1];
				desc = line.split(";")[2];
			}

			@Override
			public int compareTo(final ReputationLevel o) {
				return level - o.level;
			}

			@Override
			public boolean equals(final Object o) {
				return super.equals(o);
			}

			@Override
			public int hashCode() {
				return super.hashCode();
			}
		}

		public Culture culture;
		public String language;

		public boolean serverContent;
		public HashMap<String, List<String>> sentences = new HashMap<String, List<String>>();
		public HashMap<String, String> buildingNames = new HashMap<String, String>();
		public HashMap<String, String> strings = new HashMap<String, String>();
		public HashMap<String, Dialogue> dialogues = new HashMap<String, Dialogue>();

		public List<ReputationLevel> reputationLevels = new ArrayList<ReputationLevel>();

		public CultureLanguage(final Culture c, final String l,
				final boolean serverContent) {
			culture = c;
			language = l;
			this.serverContent = serverContent;
		}

		public int[] compareWithLanguage(final CultureLanguage ref,
				final BufferedWriter writer) throws Exception {

			int translationsDone = 0, translationsMissing = 0;

			final List<String> errors = new ArrayList<String>();
			List<String> keys = new ArrayList<String>(ref.strings.keySet());
			Collections.sort(keys);

			for (final String key : keys) {
				if (!strings.containsKey(key)) {
					errors.add("String missing for culture " + culture.key
							+ ": " + key);
					translationsMissing++;
				} else {
					translationsDone++;
				}
			}

			if (errors.size() > 0) {
				writer.write("List of gaps found in culture strings for "
						+ culture.key + ": " + MLN.EOL + MLN.EOL);

				for (final String s : errors) {
					writer.write(s + MLN.EOL);
				}
				writer.write(MLN.EOL);
			}

			for (final Goods g : culture.goodsList) {
				if (g.desc != null && !strings.containsKey(g.desc)
						&& !ref.strings.containsKey(g.desc)) {
					errors.add("Trading good desc missing in both languages for item: "
							+ g.name + ", desc key: " + g.desc);
				}
			}

			errors.clear();
			keys = new ArrayList<String>(ref.sentences.keySet());
			Collections.sort(keys);

			for (final String key : keys) {
				if (!key.startsWith("villager.chat_")) {// dialogue sentences
														// are handled
														// seperately
					if (!sentences.containsKey(key)) {
						errors.add("Sentences missing for culture "
								+ culture.key + ": " + key);
						translationsMissing++;
					} else if (sentences.get(key).size() != ref.sentences.get(
							key).size()) {
						errors.add("Different number of sentences for culture "
								+ culture.key + ": " + key);
						translationsMissing++;
					} else {
						translationsDone++;
					}
				}
			}

			if (errors.size() > 0) {
				writer.write("List of gaps found in culture sentences for "
						+ culture.key + ": " + MLN.EOL + MLN.EOL);

				for (final String s : errors) {
					writer.write(s + MLN.EOL);
				}
				writer.write(MLN.EOL);
			}

			keys = new ArrayList<String>(ref.dialogues.keySet());
			Collections.sort(keys);

			errors.clear();

			for (final String key : keys) {

				if (!dialogues.containsKey(key)) {
					errors.add("Dialogue missing for culture " + culture.key
							+ ": " + key);
					translationsMissing++;
				} else {
					final boolean matches = dialogues.get(key).compareWith(
							ref.dialogues.get(key), errors);

					if (matches) {
						translationsDone++;
					} else {
						translationsMissing++;
					}
				}
			}

			if (errors.size() > 0) {
				writer.write("List of gaps found in culture dialogues for "
						+ culture.key + ": " + MLN.EOL + MLN.EOL);

				for (final String s : errors) {
					writer.write(s + MLN.EOL);
				}
				writer.write(MLN.EOL);
			}

			errors.clear();
			keys = new ArrayList<String>(ref.buildingNames.keySet());
			Collections.sort(keys);

			for (final String key : keys) {
				if (!buildingNames.containsKey(key)) {
					errors.add("Building name missing for culture "
							+ culture.key + ": " + key);
					translationsMissing++;
				} else {
					translationsDone++;
				}
			}

			for (final BuildingPlanSet set : culture.planSet.values()) {
				for (final BuildingPlan[] plans : set.plans) {
					final String planNameLC = plans[0].planName.toLowerCase();
					if (!buildingNames.containsKey(planNameLC)
							&& !ref.buildingNames.containsKey(planNameLC)) {
						errors.add("Building name missing for culture "
								+ culture.key + " in both languages: "
								+ planNameLC);
					}

					if (plans[0].shop != null
							&& !strings.containsKey("shop." + plans[0].shop)
							&& !ref.strings
									.containsKey("shop." + plans[0].shop)) {
						errors.add("Shop name missing for culture "
								+ culture.key + " in both languages: "
								+ "shop." + plans[0].shop);
					}

				}
			}

			for (final VillagerType vt : culture.listVillagerTypes) {

				if (!strings.containsKey("villager." + vt.key)
						&& !ref.strings.containsKey("villager." + vt.key)) {
					errors.add("Villager name missing for culture "
							+ culture.key + " in both languages: "
							+ "villager." + vt.key);
				}

			}

			if (errors.size() > 0) {
				writer.write("List of gaps found in culture building names for "
						+ culture.key + ": " + MLN.EOL + MLN.EOL);

				for (final String s : errors) {
					writer.write(s + MLN.EOL);
				}
				writer.write(MLN.EOL);
			}

			if (reputationLevels.size() != ref.reputationLevels.size()) {
				translationsMissing += ref.reputationLevels.size()
						- reputationLevels.size();
				writer.write("Different number of reputation levels for culture "
						+ culture.key
						+ ": "
						+ reputationLevels.size()
						+ " in "
						+ language
						+ ", "
						+ ref.reputationLevels.size()
						+ " in " + ref.language + "." + MLN.EOL + MLN.EOL);
			} else {
				translationsDone += ref.reputationLevels.size();
			}

			return new int[] { translationsDone, translationsMissing };
		}

		public Dialogue getDialogue(final MillVillager v1, final MillVillager v2) {

			final List<Dialogue> possibleDialogues = new ArrayList<Dialogue>();

			for (final Dialogue d : dialogues.values()) {
				if (d.isValidFor(v1, v2)) {
					possibleDialogues.add(d);
				} else if (d.isValidFor(v2, v1)) {
					possibleDialogues.add(d);
				}
			}

			if (possibleDialogues.isEmpty()) {
				return null;
			}

			final WeightedChoice wc = MillCommonUtilities.getWeightedChoice(
					possibleDialogues, null);

			return (Dialogue) wc;
		}

		public ReputationLevel getReputationLevel(final int reputation) {

			if (reputationLevels.size() == 0) {
				return null;
			}

			int i = reputationLevels.size() - 1;
			while (i > 0 && reputationLevels.get(i).level > reputation) {
				i--;
			}
			return reputationLevels.get(i);
		}

		private void loadBuildingNames(final List<File> languageDirs) {

			for (final File languageDir : languageDirs) {

				File file = new File(new File(languageDir, language),
						culture.key + "_buildings.txt");

				if (!file.exists()) {
					file = new File(new File(languageDir,
							language.split("_")[0]), culture.key
							+ "_buildings.txt");
				}

				if (file.exists()) {
					readBuildingNameFile(file);
				}
			}

			for (final BuildingPlanSet set : culture.ListPlanSets) {
				for (final BuildingPlan[] plans : set.plans) {
					for (final BuildingPlan plan : plans) {
						loadBuildingPlanName(plan);
					}
				}
			}
		}

		private void loadBuildingPlanName(final BuildingPlan plan) {

			final String planNameLC = plan.planName.toLowerCase();

			for (final String key : plan.names.keySet()) {
				if (key.equalsIgnoreCase("english")) {
					if (language.equals("en")) {
						buildingNames.put(planNameLC, plan.names.get(key));
					}
				} else if (key.startsWith("name_")
						&& (key.endsWith("_" + language) || key.endsWith("_"
								+ language.split("_")[0]))) {
					buildingNames.put(planNameLC, plan.names.get(key));
				}
			}
		}

		private void loadCultureStrings(final List<File> languageDirs) {

			for (final File languageDir : languageDirs) {

				File file = new File(new File(languageDir, language),
						culture.key + "_strings.txt");

				if (!file.exists()) {
					file = new File(new File(languageDir,
							language.split("_")[0]), culture.key
							+ "_strings.txt");
				}

				if (file.exists()) {
					readCultureStringFile(file);
				}
			}
		}

		private void loadDialogues(final List<File> languageDirs) {

			for (final File languageDir : languageDirs) {

				File file = new File(new File(languageDir, language),
						culture.key + "_dialogues.txt");

				if (!file.exists()) {
					file = new File(new File(languageDir,
							language.split("_")[0]), culture.key
							+ "_dialogues.txt");
				}

				if (file.exists()) {
					readDialoguesFile(file);
				}
			}
		}

		public void loadFromDisk(final List<File> languageDirs) {
			loadBuildingNames(languageDirs);
			loadCultureStrings(languageDirs);
			loadSentences(languageDirs);
			loadDialogues(languageDirs);
			loadReputations(languageDirs);

			if (!culture.loadedLanguages.containsKey(language)) {
				culture.loadedLanguages.put(language, this);
			}

		}

		private void loadReputationFile(final File file) {

			try {
				final BufferedReader reader = MillCommonUtilities
						.getReader(file);
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.split(";").length > 2) {
						reputationLevels.add(new ReputationLevel(file, line));
					}
				}
			} catch (final Exception e) {
				MLN.printException(e);
			}
			Collections.sort(reputationLevels);
		}

		private void loadReputations(final List<File> languageDirs) {

			for (final File languageDir : languageDirs) {

				File file = new File(new File(languageDir, language),
						culture.key + "_reputation.txt");

				if (!file.exists()) {
					file = new File(new File(languageDir,
							language.split("_")[0]), culture.key
							+ "_reputation.txt");
				}

				if (file.exists()) {
					loadReputationFile(file);
				}
			}
		}

		private void loadSentences(final List<File> languageDirs) {

			for (final File languageDir : languageDirs) {

				File file = new File(new File(languageDir, language),
						culture.key + "_sentences.txt");

				if (!file.exists()) {
					file = new File(new File(languageDir,
							language.split("_")[0]), culture.key
							+ "_sentences.txt");
				}

				if (file.exists()) {
					readSentenceFile(file);
				}
			}
		}

		private void readBuildingNameFile(final File file) {
			try {
				final BufferedReader reader = MillCommonUtilities
						.getReader(file);

				String line;

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0 && !line.startsWith("//")) {
						final String[] temp = line.trim().split("=");
						if (temp.length == 2) {

							final String key = temp[0].toLowerCase();
							final String value = temp[1].trim();

							buildingNames.put(key, value);

							if (MLN.LogTranslation >= MLN.MINOR) {
								MLN.minor(this, "Loading name: " + value
										+ " for " + key);
							}
						} else if (temp.length == 1) {
							final String key = temp[0].toLowerCase();

							buildingNames.put(key, "");

							if (MLN.LogTranslation >= MLN.MINOR) {
								MLN.minor(this, "Loading empty name for " + key);
							}
						}
					}
				}
				reader.close();
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}

		private void readCultureStringFile(final File file) {

			try {
				final BufferedReader reader = MillCommonUtilities
						.getReader(file);

				String line;

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0 && !line.startsWith("//")) {
						final String[] temp = line.trim().split("=");
						if (temp.length == 2) {

							final String key = temp[0].toLowerCase();
							final String value = temp[1].trim();

							strings.put(key, value);
							if (MLN.LogTranslation >= MLN.MINOR) {
								MLN.minor(this, "Loading name: " + value
										+ " for " + key);
							}
						} else if (temp.length == 1) {
							final String key = temp[0].toLowerCase();

							strings.put(key, "");
							if (MLN.LogTranslation >= MLN.MINOR) {
								MLN.minor(this, "Loading empty name for " + key);
							}
						}
					}
				}
				reader.close();
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}

		private boolean readDialoguesFile(final File file) {

			try {
				final BufferedReader reader = MillCommonUtilities
						.getReader(file);

				String line;

				Dialogue dialogue = null;

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0 && !line.startsWith("//")) {

						line = line.trim();

						if (line.startsWith("newchat;")
								&& line.split(";").length == 2) {

							if (dialogue != null) {
								if (dialogue.speechBy.size() > 0) {
									if (dialogues.containsKey(dialogue.key)) {
										MLN.error(
												culture,
												language
														+ ": Trying to register two dialogues with the same key: "
														+ dialogue.key);
									} else {
										dialogue.checkData(culture, language);
										dialogues.put(dialogue.key, dialogue);
									}

								} else {
									MLN.error(culture, "In dialogue file "
											+ file.getAbsolutePath()
											+ " dialogue " + dialogue.key
											+ " has no sentences.");
								}
							}

							final String s = line.split(";")[1].trim();

							dialogue = new Dialogue(s);

							if (dialogue.key == null) {
								MLN.error(culture, language
										+ ": Could not read dialogue line: "
										+ line);
								dialogue = null;
							}

						} else if (dialogue != null
								&& line.split(";").length == 3) {
							final String[] temp = line.split(";");

							dialogue.speechBy
									.add(temp[0].trim().equals("v2") ? 2 : 1);
							dialogue.timeDelays.add(Integer.parseInt(temp[1]
									.trim()));

							final List<String> sentence = new ArrayList<String>();
							sentence.add(temp[2]);

							sentences.put("villager.chat_" + dialogue.key + "_"
									+ (dialogue.speechBy.size() - 1), sentence);

						} else if (line.trim().length() > 0) {
							MLN.error(culture, language + ": In dialogue file "
									+ file.getAbsolutePath()
									+ " the following line is invalid: " + line);
						}
					}
				}

				if (dialogue.speechBy.size() > 0) {
					if (dialogues.containsKey(dialogue.key)) {
						MLN.error(
								culture,
								language
										+ ": Trying to register two dialogues with the same key: "
										+ dialogue.key);
					} else {
						dialogue.checkData(culture, language);
						dialogues.put(dialogue.key, dialogue);
					}

				} else {
					MLN.error(
							culture,
							language + ": In dialogue file "
									+ file.getAbsolutePath() + " dialogue "
									+ dialogue.key + " has no sentences.");
				}

				reader.close();
			} catch (final Exception e) {
				MLN.printException(e);
				return false;
			}

			return true;
		}

		private boolean readSentenceFile(final File file) {

			try {
				final BufferedReader reader = MillCommonUtilities
						.getReader(file);

				String line;

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0 && !line.startsWith("//")) {
						final String[] temp = line.split("=");
						if (temp.length == 2) {

							final String key = temp[0].toLowerCase();
							final String value = temp[1].trim();

							if (sentences.containsKey(key)) {
								sentences.get(key).add(value);
							} else {
								final List<String> v = new ArrayList<String>();
								v.add(value);
								sentences.put(key, v);
							}
						}
					}
				}
				reader.close();
			} catch (final Exception e) {
				MLN.printException(e);
				return false;
			}

			return true;
		}
	}

	private static final int LANGUAGE_FLUENT = 500;
	private static final int LANGUAGE_MODERATE = 200;
	private static final int LANGUAGE_BEGINNER = 100;
	public static List<Culture> ListCultures = new ArrayList<Culture>();
	private static HashMap<String, Culture> cultures = new HashMap<String, Culture>();

	private static HashMap<String, Culture> serverCultures = new HashMap<String, Culture>();

	public static HashMap<String, String> oldShopConversion = new HashMap<String, String>();

	public static Culture getCultureByName(final String name) {
		if (cultures.containsKey(name)) {
			return cultures.get(name);
		}

		if (serverCultures.containsKey(name)) {
			return serverCultures.get(name);
		}

		if (Mill.isDistantClient()) {
			final Culture culture = new Culture(name);
			serverCultures.put(name, culture);
			return culture;
		}

		return null;
	}

	public static Culture getRandomCulture() {
		return (Culture) MillCommonUtilities.getWeightedChoice(ListCultures,
				null);
	}

	public static boolean loadCultures() {

		final ArrayList<File> culturesDirs = new ArrayList<File>();

		for (final File dir : Mill.loadingDirs) {
			final File cultureDir = new File(dir, "cultures");

			if (cultureDir.exists()) {
				culturesDirs.add(cultureDir);
			}
		}

		final File customcultureDir = new File(Mill.proxy.getCustomDir(),
				"custom cultures");

		if (customcultureDir.exists()) {
			culturesDirs.add(customcultureDir);
		}

		@SuppressWarnings("unchecked")
		final List<File> culturesDirsBis = (ArrayList<File>) culturesDirs
				.clone();

		for (final File culturesDir : culturesDirsBis) {
			for (final File cultureDir : culturesDir.listFiles()) {
				if (cultureDir.exists() && cultureDir.isDirectory()
						&& !cultureDir.getName().startsWith(".")
						&& !cultures.containsKey(cultureDir.getName())) {

					if (MLN.LogCulture >= MLN.MAJOR) {
						MLN.major(cultureDir,
								"Loading culture: " + cultureDir.getName());
					}

					final Culture culture = new Culture(cultureDir.getName());
					culture.initialise(culturesDirs);
					cultures.put(culture.key, culture);
					ListCultures.add(culture);
				}
			}
		}

		if (MLN.LogCulture >= MLN.MAJOR) {
			MLN.major(null, "Finished loading cultures.");
		}

		return false;
	}

	public static void readCultureMissingContentPacket(
			final ByteBufInputStream data) {
		String key;
		try {
			key = data.readUTF();
			final Culture culture = getCultureByName(key);

			final CultureLanguage main = new CultureLanguage(culture,
					MLN.effective_language, true);
			final CultureLanguage fallback = new CultureLanguage(culture,
					MLN.fallback_language, true);

			culture.mainLanguageServer = main;
			culture.fallbackLanguageServer = fallback;

			final String playerName = Mill.proxy.getTheSinglePlayer()
					.getDisplayName();

			final CultureLanguage[] langs = new CultureLanguage[] { main,
					fallback };

			for (final CultureLanguage lang : langs) {
				HashMap<String, String> strings = StreamReadWrite
						.readStringStringMap(data);
				for (final String k : strings.keySet()) {
					if (!lang.strings.containsKey(k)) {
						lang.strings.put(k,
								strings.get(k)
										.replaceAll("\\$name", playerName));
					}
				}

				strings = StreamReadWrite.readStringStringMap(data);
				for (final String k : strings.keySet()) {
					if (!lang.buildingNames.containsKey(k)) {
						lang.buildingNames.put(k,
								strings.get(k)
										.replaceAll("\\$name", playerName));
					}
				}

				final HashMap<String, List<String>> sentences = StreamReadWrite
						.readStringStringListMap(data);
				for (final String k : sentences.keySet()) {
					if (!lang.sentences.containsKey(k)) {
						final List<String> v = new ArrayList<String>();
						for (final String s : sentences.get(k)) {
							v.add(s.replaceAll("\\$name", playerName));
						}
						lang.sentences.put(k, v);
					}
				}
			}

			int nb = data.readShort();
			for (int i = 0; i < nb; i++) {
				key = data.readUTF();
				final BuildingPlanSet set = culture.getBuildingPlanSet(key);
				set.readBuildingPlanSetInfoPacket(data);
			}

			nb = data.readShort();
			for (int i = 0; i < nb; i++) {
				key = data.readUTF();
				final VillagerType vtype = culture.getVillagerType(key);
				vtype.readVillagerTypeInfoPacket(data);
			}

			nb = data.readShort();
			for (int i = 0; i < nb; i++) {
				key = data.readUTF();
				final VillageType vtype = culture.getVillageType(key);
				vtype.readVillageTypeInfoPacket(data);
			}

			nb = data.readShort();
			for (int i = 0; i < nb; i++) {
				key = data.readUTF();
				final VillageType vtype = culture.getLoneBuildingType(key);
				vtype.readVillageTypeInfoPacket(data);
			}

		} catch (final IOException e) {
			MLN.printException("Error in readCultureInfoPacket: ", e);
		}
	}

	public static void refreshLists() {

		ListCultures.clear();

		for (final String k : cultures.keySet()) {
			final Culture c = cultures.get(k);
			ListCultures.add(c);
		}

		for (final String k : serverCultures.keySet()) {
			final Culture c = serverCultures.get(k);
			ListCultures.add(c);
		}

		for (final Culture c : ListCultures) {

			c.ListPlanSets.clear();
			for (final String key : c.planSet.keySet()) {
				c.ListPlanSets.add(c.planSet.get(key));
			}
			for (final String key : c.serverPlanSet.keySet()) {
				c.ListPlanSets.add(c.serverPlanSet.get(key));
			}

			c.listVillagerTypes.clear();
			for (final String key : c.villagerTypes.keySet()) {
				c.listVillagerTypes.add(c.villagerTypes.get(key));
			}
			for (final String key : c.serverVillagerTypes.keySet()) {
				c.listVillagerTypes.add(c.serverVillagerTypes.get(key));
			}

			c.listVillageTypes.clear();
			for (final String key : c.villageTypes.keySet()) {
				c.listVillageTypes.add(c.villageTypes.get(key));
			}
			for (final String key : c.serverVillageTypes.keySet()) {
				c.listVillageTypes.add(c.serverVillageTypes.get(key));
			}

			c.listLoneBuildingTypes.clear();
			for (final String key : c.loneBuildingTypes.keySet()) {
				c.listLoneBuildingTypes.add(c.loneBuildingTypes.get(key));
			}
			for (final String key : c.serverLoneBuildingTypes.keySet()) {
				c.listLoneBuildingTypes.add(c.serverLoneBuildingTypes.get(key));
			}
		}
	}

	public static void removeServerContent() {

		serverCultures.clear();

		for (final String k : cultures.keySet()) {
			final Culture c = cultures.get(k);
			c.serverPlanSet.clear();
			c.serverVillageTypes.clear();
			c.serverVillagerTypes.clear();
			c.serverLoneBuildingTypes.clear();

			c.mainLanguageServer = null;
			c.fallbackLanguageServer = null;

		}

		refreshLists();
	}

	private CultureLanguage mainLanguage, fallbackLanguage;

	private CultureLanguage mainLanguageServer, fallbackLanguageServer;

	private final HashMap<String, CultureLanguage> loadedLanguages = new HashMap<String, CultureLanguage>();

	static {
		oldShopConversion.put("indianarmyforge", "armyforge");
		oldShopConversion.put("indianforge", "forge");
		oldShopConversion.put("indiantownhall", "townhall");
	}

	public String key;

	public String qualifierSeparator = " ";

	private HashMap<String, BuildingPlanSet> planSet = new HashMap<String, BuildingPlanSet>();

	private final HashMap<String, BuildingPlanSet> serverPlanSet = new HashMap<String, BuildingPlanSet>();

	public List<BuildingPlanSet> ListPlanSets = new ArrayList<BuildingPlanSet>();

	private final HashMap<String, VillageType> villageTypes = new HashMap<String, VillageType>();

	private final HashMap<String, VillageType> serverVillageTypes = new HashMap<String, VillageType>();

	public List<VillageType> listVillageTypes = new ArrayList<VillageType>();

	private final HashMap<String, VillageType> loneBuildingTypes = new HashMap<String, VillageType>();
	private final HashMap<String, VillageType> serverLoneBuildingTypes = new HashMap<String, VillageType>();
	public List<VillageType> listLoneBuildingTypes = new ArrayList<VillageType>();
	public final HashMap<String, VillagerType> villagerTypes = new HashMap<String, VillagerType>();
	private final HashMap<String, VillagerType> serverVillagerTypes = new HashMap<String, VillagerType>();
	public List<VillagerType> listVillagerTypes = new ArrayList<VillagerType>();
	private final HashMap<String, List<String>> nameLists = new HashMap<String, List<String>>();

	public HashMap<String, List<Goods>> shopSells = new HashMap<String, List<Goods>>();
	public HashMap<String, List<Goods>> shopBuys = new HashMap<String, List<Goods>>();
	public HashMap<String, List<Goods>> shopBuysOptional = new HashMap<String, List<Goods>>();
	public HashMap<String, List<InvItem>> shopNeeds = new HashMap<String, List<InvItem>>();

	public List<Goods> goodsList = new ArrayList<Goods>();
	public HashMap<String, Goods> goods = new HashMap<String, Goods>();
	public HashMap<InvItem, Goods> goodsByItem = new HashMap<InvItem, Goods>();

	public List<String> knownCrops = new ArrayList<String>();

	public Culture(final String s) {
		key = s;
	}

	public boolean canReadBuildingNames() {

		if (Mill.proxy.getClientProfile() == null) {
			return true;
		}

		return !MLN.languageLearning
				|| Mill.proxy.getClientProfile().getCultureLanguageKnowledge(
						key) >= LANGUAGE_BEGINNER;
	}

	public boolean canReadDialogues(final String username) {
		if (Mill.proxy.getClientProfile() == null) {
			return true;
		}

		return !MLN.languageLearning
				|| Mill.proxy.getClientProfile().getCultureLanguageKnowledge(
						key) >= LANGUAGE_FLUENT;
	}

	public boolean canReadVillagerNames(final String username) {
		if (Mill.proxy.getClientProfile() == null) {
			return true;
		}

		return !MLN.languageLearning
				|| Mill.proxy.getClientProfile().getCultureLanguageKnowledge(
						key) >= LANGUAGE_MODERATE;
	}

	public int[] compareCultureLanguages(final String main, final String ref,
			final BufferedWriter writer) throws Exception {

		CultureLanguage maincl = null, refcl = null;

		if (loadedLanguages.containsKey(main)) {
			maincl = loadedLanguages.get(main);
		}

		if (loadedLanguages.containsKey(ref)) {
			refcl = loadedLanguages.get(ref);
		}

		if (refcl == null) {
			return new int[] { 0, 0 };
		}

		if (maincl == null) {
			writer.write("Data for culture " + key + " is missing." + MLN.EOL
					+ MLN.EOL);

			return new int[] {
					0,
					refcl.buildingNames.size() + refcl.reputationLevels.size()
							+ refcl.sentences.size() + refcl.strings.size() };
		}

		return maincl.compareWithLanguage(refcl, writer);
	}

	public String getBuildingGameName(final BuildingPlan plan) {

		final String planNameLC = plan.planName.toLowerCase();

		if (mainLanguage != null
				&& mainLanguage.buildingNames.containsKey(planNameLC)) {
			return mainLanguage.buildingNames.get(planNameLC);
		} else if (mainLanguageServer != null
				&& mainLanguageServer.buildingNames.containsKey(planNameLC)) {
			return mainLanguageServer.buildingNames.get(planNameLC);
		} else if (fallbackLanguage != null
				&& fallbackLanguage.buildingNames.containsKey(planNameLC)) {
			return fallbackLanguage.buildingNames.get(planNameLC);
		} else if (fallbackLanguageServer != null
				&& fallbackLanguageServer.buildingNames.containsKey(planNameLC)) {
			return fallbackLanguageServer.buildingNames.get(planNameLC);
		}

		if (plan.parent != null) {
			return getBuildingGameName(plan.parent);
		}

		if (MLN.LogTranslation >= MLN.MAJOR || MLN.generateTranslationGap) {
			MLN.major(this, "Could not find the building name for :"
					+ plan.planName);
		}

		return null;
	}

	public BuildingPlanSet getBuildingPlanSet(final String key) {
		if (planSet.containsKey(key)) {
			return planSet.get(key);
		}

		if (serverPlanSet.containsKey(key)) {
			return serverPlanSet.get(key);
		}

		if (Mill.isDistantClient()) {
			final BuildingPlanSet set = new BuildingPlanSet(this, key, null);
			serverPlanSet.put(key, set);
			return set;
		}
		return null;
	}

	public int getChoiceWeight() {
		return 10;
	}

	public String getCultureGameName() {
		return getCultureString("culture." + key);
	}

	public String getCultureString(String key) {
		key = key.toLowerCase();
		if (mainLanguage != null && mainLanguage.strings.containsKey(key)) {
			return mainLanguage.strings.get(key);
		} else if (MLN.getRawStringMainOnly(key, false) != null) {
			return MLN.getRawStringMainOnly(key, false);
		} else if (mainLanguageServer != null
				&& mainLanguageServer.strings.containsKey(key)) {
			return mainLanguageServer.strings.get(key);
		} else if (fallbackLanguage != null
				&& fallbackLanguage.strings.containsKey(key)) {
			return fallbackLanguage.strings.get(key);
		} else if (MLN.getRawStringFallbackOnly(key, false) != null) {
			return MLN.getRawStringFallbackOnly(key, false);
		} else if (fallbackLanguageServer != null
				&& fallbackLanguageServer.strings.containsKey(key)) {
			return fallbackLanguageServer.strings.get(key);
		}
		return key;
	}

	public Dialogue getDialog(final MillVillager v1, final MillVillager v2) {

		Dialogue d = mainLanguage.getDialogue(v1, v2);

		if (d != null) {
			return d;
		}

		if (mainLanguageServer != null) {
			d = mainLanguageServer.getDialogue(v1, v2);
		}

		if (d != null) {
			return d;
		}

		if (fallbackLanguage != null) {
			d = fallbackLanguage.getDialogue(v1, v2);
		}

		if (d != null) {
			return d;
		}

		if (fallbackLanguageServer != null) {
			d = fallbackLanguageServer.getDialogue(v1, v2);
		}

		if (d != null) {
			return d;
		}

		return null;
	}

	public Dialogue getDialogue(final String key) {

		if (mainLanguage.dialogues.containsKey(key)) {
			return mainLanguage.dialogues.get(key);
		}

		if (mainLanguageServer != null
				&& mainLanguageServer.dialogues.containsKey(key)) {
			return mainLanguageServer.dialogues.get(key);
		}

		if (fallbackLanguage != null
				&& fallbackLanguage.dialogues.containsKey(key)) {
			return fallbackLanguage.dialogues.get(key);
		}

		if (fallbackLanguageServer != null
				&& fallbackLanguageServer.dialogues.containsKey(key)) {
			return fallbackLanguageServer.dialogues.get(key);
		}

		return null;
	}

	public String getLanguageLevelString() {

		if (Mill.proxy.getClientProfile() == null) {
			return MLN.string("culturelanguage.minimal");
		}

		if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(key) >= LANGUAGE_FLUENT) {
			return MLN.string("culturelanguage.fluent");
		}
		if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(key) >= LANGUAGE_MODERATE) {
			return MLN.string("culturelanguage.moderate");
		}
		if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(key) >= LANGUAGE_BEGINNER) {
			return MLN.string("culturelanguage.beginner");
		}

		return MLN.string("culturelanguage.minimal");
	}

	public VillageType getLoneBuildingType(final String key) {
		if (loneBuildingTypes.containsKey(key)) {
			return loneBuildingTypes.get(key);
		}

		if (serverLoneBuildingTypes.containsKey(key)) {
			return serverLoneBuildingTypes.get(key);
		}

		if (Mill.isDistantClient()) {
			final VillageType vtype = new VillageType(this, key, false);
			serverLoneBuildingTypes.put(key, vtype);
			return vtype;
		}
		return null;
	}

	public List<BuildingPlanSet> getPlanSetsWithTag(final String tag) {
		final List<BuildingPlanSet> sets = new ArrayList<BuildingPlanSet>();

		for (final BuildingPlanSet set : ListPlanSets) {
			if (set.plans.get(0)[0].tags.contains(tag)) {
				sets.add(set);
			}
		}
		return sets;
	}

	public VillagerType getRandomForeignMerchant() {

		final List<VillagerType> foreignMerchants = new ArrayList<VillagerType>();

		for (final VillagerType v : listVillagerTypes) {
			if (v.isForeignMerchant) {
				foreignMerchants.add(v);
			}
		}

		if (foreignMerchants.size() == 0) {
			return null;
		}

		return (VillagerType) MillCommonUtilities.getWeightedChoice(
				foreignMerchants, null);
	}

	public String getRandomNameFromList(final String listName) {
		final List<String> list = nameLists.get(listName);
		if (list == null) {
			MLN.error(this, "Could not find name list: " + listName);
			return null;
		}
		return list.get(MillCommonUtilities.randomInt(list.size()));
	}

	public VillageType getRandomVillage() {
		return (VillageType) MillCommonUtilities.getWeightedChoice(
				listVillageTypes, null);
	}

	public ReputationLevel getReputationLevel(final int reputation) {

		ReputationLevel rlevel = null;

		if (mainLanguage != null) {
			rlevel = mainLanguage.getReputationLevel(reputation);
		}

		if (rlevel != null) {
			return rlevel;
		}

		if (fallbackLanguage != null) {
			return fallbackLanguage.getReputationLevel(reputation);
		}

		return null;
	}

	public String getReputationLevelDesc(final int reputation) {
		final ReputationLevel rlevel = getReputationLevel(reputation);

		if (rlevel != null) {
			return rlevel.desc;
		}

		return "";
	}

	public String getReputationLevelLabel(final int reputation) {
		final ReputationLevel rlevel = getReputationLevel(reputation);

		if (rlevel != null) {
			return rlevel.label;
		}

		return "";
	}

	public String getReputationString() {

		if (Mill.proxy.getClientProfile() == null) {
			return MLN.string("culturereputation.neutral");
		}

		final int reputation = Mill.proxy.getClientProfile()
				.getCultureReputation(key);

		if (reputation < 0) {
			if (reputation <= -10 * 64) {
				return MLN.string("culturereputation.scourgeofgod");
			} else if (reputation < -2 * 64) {
				return MLN.string("culturereputation.dreadful");
			} else {
				return MLN.string("culturereputation.bad");
			}
		} else {
			if (reputation > 32 * 64) {
				return MLN.string("culturereputation.stellar");
			}
			if (reputation > 16 * 64) {
				return MLN.string("culturereputation.excellent");
			}
			if (reputation > 8 * 64) {
				return MLN.string("culturereputation.good");
			}
			if (reputation > 4 * 64) {
				return MLN.string("culturereputation.decent");
			}
		}

		return MLN.string("culturereputation.neutral");
	}

	public List<String> getSentences(final String key) {

		if (mainLanguage != null && mainLanguage.sentences.containsKey(key)) {
			return mainLanguage.sentences.get(key);
		}

		if (mainLanguageServer != null
				&& mainLanguageServer.sentences.containsKey(key)) {
			return mainLanguageServer.sentences.get(key);
		}

		if (fallbackLanguage != null
				&& fallbackLanguage.sentences.containsKey(key)) {
			return fallbackLanguage.sentences.get(key);
		}

		if (fallbackLanguageServer != null
				&& fallbackLanguageServer.sentences.containsKey(key)) {
			return fallbackLanguageServer.sentences.get(key);
		}

		return null;
	}

	public VillagerType getVillagerType(final String key) {
		if (villagerTypes.containsKey(key)) {
			return villagerTypes.get(key);
		}

		if (serverVillagerTypes.containsKey(key)) {
			return serverVillagerTypes.get(key);
		}

		if (Mill.isDistantClient()) {
			final VillagerType vtype = new VillagerType(this, key);
			serverVillagerTypes.put(key, vtype);
			return vtype;
		}
		return null;
	}

	public VillageType getVillageType(final String key) {

		if (villageTypes.containsKey(key)) {
			return villageTypes.get(key);
		}

		if (serverVillageTypes.containsKey(key)) {
			return serverVillageTypes.get(key);
		}

		if (Mill.isDistantClient()) {
			final VillageType vtype = new VillageType(this, key, false);
			serverVillageTypes.put(key, vtype);
			return vtype;
		}
		return null;
	}

	public boolean hasSentences(final String key) {
		return getSentences(key) != null;
	}

	public boolean initialise(final List<File> culturesDirs) {

		final List<File> thisCultureDirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {

			final File dir = new File(culturesDir, key);

			if (dir.exists()) {
				thisCultureDirs.add(dir);
			}

		}

		try {

			readConfig(thisCultureDirs);
			loadNameLists(thisCultureDirs);
			loadGoods(thisCultureDirs);
			loadShops(thisCultureDirs);
			loadVillagerTypes(thisCultureDirs);

			planSet = BuildingPlan.loadPlans(thisCultureDirs, this);

			if (planSet == null) {
				return false;
			}

			ListPlanSets.addAll(planSet.values());

			if (MLN.LogBuildingPlan >= MLN.MAJOR) {
				for (final BuildingPlanSet set : ListPlanSets) {
					MLN.major(set, "Loaded plan set: " + set.key);
				}
			}

			listVillageTypes = VillageType.loadVillages(thisCultureDirs, this);

			if (listVillageTypes == null) {
				return false;
			}

			for (final VillageType v : listVillageTypes) {
				villageTypes.put(v.key, v);
			}

			listLoneBuildingTypes = VillageType.loadLoneBuildings(
					thisCultureDirs, this);

			for (final VillageType v : listLoneBuildingTypes) {
				loneBuildingTypes.put(v.key, v);
			}

			if (MLN.LogCulture >= MLN.MAJOR) {
				MLN.major(this, "Finished loading culture.");
			}
			return true;

		} catch (final Exception e) {

			MLN.printException("Error when loading culture: ", e);

			return false;
		}
	}

	private void loadGoods(final List<File> culturesDirs) {

		final List<File> files = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "traded_goods.txt");

			if (dir.exists()) {
				files.add(dir);
			}
		}

		final File dir = new File(new File(new File(Mill.proxy.getCustomDir(),
				"cultures"), key), "traded_goods.txt");

		if (dir.exists()) {
			files.add(dir);
		}

		for (final File file : files) {
			try {

				if (!file.exists()) {
					file.createNewFile();
				}

				final BufferedReader reader = MillCommonUtilities
						.getReader(file);

				String line;

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0 && !line.startsWith("//")) {

						try {

							final String[] values = line.split(",");

							final String name = values[0].toLowerCase();

							if (Goods.goodsName.containsKey(name)) {
								final InvItem item = Goods.goodsName.get(name);
								final int sellingPrice = values.length > 1
										&& !values[1].isEmpty() ? MillCommonUtilities
										.readInteger(values[1]) : 0;
								final int buyingPrice = values.length > 2
										&& !values[2].isEmpty() ? MillCommonUtilities
										.readInteger(values[2]) : 0;
								final int reservedQuantity = values.length > 3
										&& !values[3].isEmpty() ? MillCommonUtilities
										.readInteger(values[3]) : 0;
								final int targetQuantity = values.length > 4
										&& !values[4].isEmpty() ? MillCommonUtilities
										.readInteger(values[4]) : 0;
								final int foreignMerchantPrice = values.length > 5
										&& !values[5].isEmpty() ? MillCommonUtilities
										.readInteger(values[5]) : 0;
								final boolean autoGenerate = values.length > 6
										&& !values[6].isEmpty() ? Boolean
										.parseBoolean(values[6]) : false;
								final String tag = values.length > 7
										&& !values[7].isEmpty() ? values[7]
										: null;
								final int minReputation = values.length > 8
										&& !values[8].isEmpty() ? MillCommonUtilities
										.readInteger(values[8])
										: Integer.MIN_VALUE;
								final String desc = values.length > 9
										&& !values[9].isEmpty() ? values[9]
										: null;

								final Goods good = new Goods(name, item,
										sellingPrice, buyingPrice,
										reservedQuantity, targetQuantity,
										foreignMerchantPrice, autoGenerate,
										tag, minReputation, desc);

								if (goods.containsKey(name)
										|| goodsByItem.containsKey(good.item)) {
									MLN.error(
											this,
											"Good "
													+ name
													+ " is present twice in the goods list.");
								}

								goods.put(name, good);
								goodsByItem.put(good.item, good);
								goodsList.remove(good);
								goodsList.add(good);

								if (MLN.LogCulture >= MLN.MINOR) {
									MLN.minor(this, "Loaded traded good: "
											+ name + " prices: " + sellingPrice
											+ "/" + buyingPrice);
								}

							} else {
								MLN.error(this, "Unknown good on line: " + line);
							}
						} catch (final Exception e) {
							MLN.printException(
									"Exception when trying to read trade good on line: "
											+ line, e);
						}
					}
				}
				reader.close();

			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	private CultureLanguage loadLanguage(final List<File> languageDirs,
			final String key) {

		if (loadedLanguages.containsKey(key)) {
			return loadedLanguages.get(key);
		}

		final CultureLanguage lang = new CultureLanguage(this, key, false);

		final List<File> languageDirsWithCusto = new ArrayList<File>(
				languageDirs);

		final File dircusto = new File(new File(new File(
				Mill.proxy.getCustomDir(), "custom cultures"), key),
				"languages");

		if (dircusto.exists()) {
			languageDirsWithCusto.add(dircusto);
		}

		lang.loadFromDisk(languageDirsWithCusto);

		return lang;
	}

	public void loadLanguages(final List<File> languageDirs,
			final String effective_language, final String fallback_language) {

		mainLanguage = loadLanguage(languageDirs, effective_language);
		if (!effective_language.equals(fallback_language)) {
			fallbackLanguage = loadLanguage(languageDirs, fallback_language);
		} else {
			fallbackLanguage = mainLanguage;
		}

		final File mainDir = languageDirs.get(0);

		for (final File lang : mainDir.listFiles()) {
			if (lang.isDirectory() && !lang.isHidden()) {
				final String key = lang.getName().toLowerCase();

				if (!loadedLanguages.containsKey(key)) {
					loadLanguage(languageDirs, key);
				}
			}
		}
	}

	private void loadNameLists(final List<File> culturesDirs) {

		final List<File> listDirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "namelists");

			if (dir.exists()) {
				listDirs.add(dir);
			}
		}

		final File dir = new File(new File(new File(Mill.proxy.getCustomDir(),
				"cultures"), key), "custom namelists");

		if (dir.exists()) {
			listDirs.add(dir);
		}

		for (final File lists : listDirs) {
			try {
				for (final File file : lists
						.listFiles(new ExtFileFilter("txt"))) {

					final List<String> list = new ArrayList<String>();

					final BufferedReader reader = MillCommonUtilities
							.getReader(file);
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.length() > 0) {
							list.add(line);
						}
					}

					nameLists.put(file.getName().split("\\.")[0], list);
				}
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	private void loadShop(final File file) {

		try {
			final BufferedReader reader = MillCommonUtilities.getReader(file);

			String line;

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0 && !line.startsWith("//")) {
					final String[] temp = line.split("=");
					if (temp.length != 2) {
						MLN.error(null, "Invalid line when loading shop "
								+ file.getName() + ": " + line);
					} else {

						final String key = temp[0].toLowerCase();
						final String value = temp[1].toLowerCase();

						if (key.equals("buys")) {
							final List<Goods> buys = new ArrayList<Goods>();

							for (final String name : value.split(",")) {
								if (goods.containsKey(name)) {
									buys.add(goods.get(name));
									if (MLN.LogSelling >= MLN.MINOR) {
										MLN.minor(
												this,
												"Loaded buying good " + name
														+ " for shop "
														+ file.getName());
									}
								} else {
									MLN.error(this,
											"Unknown good when loading shop "
													+ file.getName() + ": "
													+ name);
								}
							}
							shopBuys.put(file.getName().split("\\.")[0], buys);
						} else if (key.equals("buysoptional")) {
							final List<Goods> buys = new ArrayList<Goods>();

							for (final String name : value.split(",")) {
								if (goods.containsKey(name)) {
									buys.add(goods.get(name));
									if (MLN.LogSelling >= MLN.MINOR) {
										MLN.minor(this,
												"Loaded optional buying good "
														+ name + " for shop "
														+ file.getName());
									}
								} else {
									MLN.error(this,
											"Unknown good when loading shop "
													+ file.getName() + ": "
													+ name);
								}
							}
							shopBuysOptional.put(
									file.getName().split("\\.")[0], buys);
						} else if (key.equals("sells")) {
							final List<Goods> sells = new ArrayList<Goods>();

							for (final String name : value.split(",")) {
								if (goods.containsKey(name)) {
									sells.add(goods.get(name));
								} else {
									MLN.error(this,
											"Unknown good when loading shop "
													+ file.getName() + ": "
													+ name);
								}
							}
							shopSells
									.put(file.getName().split("\\.")[0], sells);
						} else if (key.equals("deliverto")) {
							final List<InvItem> needs = new ArrayList<InvItem>();

							for (final String name : value.split(",")) {
								if (Goods.goodsName.containsKey(name)) {
									needs.add(Goods.goodsName.get(name));
								} else {
									MLN.error(this,
											"Unknown good when loading shop "
													+ file.getName() + ": "
													+ name);
								}
							}
							shopNeeds
									.put(file.getName().split("\\.")[0], needs);
						} else {
							MLN.error(this,
									"Unknown parameter when loading shop "
											+ file.getName() + ": " + line);
						}

					}
				}
			}
			reader.close();
		} catch (final Exception e) {
			MLN.printException(e);
		}

	}

	private void loadShops(final List<File> culturesDirs) {

		final List<File> dirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "shops");

			if (dir.exists()) {
				dirs.add(dir);
			}
		}

		final File dircusto = new File(new File(new File(
				Mill.proxy.getCustomDir(), "cultures"), key), "shops");

		if (dircusto.exists()) {
			dirs.add(dircusto);
		}

		for (final File dir : dirs) {
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				for (final File file : dir.listFiles(new ExtFileFilter("txt"))) {
					loadShop(file);
				}
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	private void loadVillagerTypes(final List<File> culturesDirs) {

		final List<File> dirs = new ArrayList<File>();

		for (final File culturesDir : culturesDirs) {
			final File dir = new File(culturesDir, "villagers");

			if (dir.exists()) {
				dirs.add(dir);
			}
		}

		final File dircusto = new File(new File(new File(
				Mill.proxy.getCustomDir(), "cultures"), key),
				"custom villagers");

		if (dircusto.exists()) {
			dirs.add(dircusto);
		}

		for (final File dir : dirs) {
			try {
				for (final File file : dir.listFiles(new ExtFileFilter("txt"))) {

					final VillagerType vtype = VillagerType.loadVillagerType(
							file, this);

					if (vtype != null) {
						villagerTypes.put(vtype.key, vtype);
						listVillagerTypes.add(vtype);
					}
				}
			} catch (final Exception e) {
				MLN.printException(e);
			}
		}
	}

	private void readConfig(final List<File> culturesDirs) {

		try {

			for (final File cultureDir : culturesDirs) {

				final File file = new File(cultureDir, "culture.txt");

				if (file.exists()) {

					final BufferedReader reader = MillCommonUtilities
							.getReader(file);

					String line;

					while ((line = reader.readLine()) != null) {
						if (line.trim().length() > 0 && !line.startsWith("//")) {
							final String[] temp = line.split("=");
							if (temp.length == 2) {

								final String key = temp[0];
								final String value = temp[1];
								if (key.equalsIgnoreCase("qualifierSeparator")) {
									qualifierSeparator = value;
								} else if (key.equalsIgnoreCase("knownCrop")) {
									knownCrops.add(value.trim().toLowerCase());
								}
							}
						}
					}
					reader.close();
				}
			}

		} catch (final Exception e) {
			MLN.printException(e);
		}
	}

	@Override
	public String toString() {
		return "Culture: " + key;
	}

	public void writeCultureAvailableContentPacket(
			final ByteBufOutputStream data) throws IOException {

		data.writeUTF(key);

		data.writeShort(mainLanguage.strings.size());
		data.writeShort(mainLanguage.buildingNames.size());
		data.writeShort(mainLanguage.sentences.size());

		data.writeShort(fallbackLanguage.strings.size());
		data.writeShort(fallbackLanguage.buildingNames.size());
		data.writeShort(fallbackLanguage.sentences.size());

		data.writeShort(ListPlanSets.size());
		for (final BuildingPlanSet set : ListPlanSets) {
			data.writeUTF(set.key);
		}

		data.writeShort(villagerTypes.size());
		for (final String key : villagerTypes.keySet()) {
			final VillagerType vtype = villagerTypes.get(key);
			data.writeUTF(vtype.key);
		}

		data.writeShort(villageTypes.size());
		for (final String key : villageTypes.keySet()) {
			final VillageType vtype = villageTypes.get(key);
			data.writeUTF(vtype.key);
		}

		data.writeShort(loneBuildingTypes.size());
		for (final String key : loneBuildingTypes.keySet()) {
			final VillageType vtype = loneBuildingTypes.get(key);
			data.writeUTF(vtype.key);
		}
	}

	public void writeCultureMissingContentPackPacket(final DataOutput data,
			final String mainLanguage, final String fallbackLanguage,
			final int nbStrings, final int nbBuildingNames,
			final int nbSentences, final int nbFallbackStrings,
			final int nbFallbackBuildingNames, final int nbFallbackSentences,
			final List<String> planSetAvailable,
			final List<String> villagerAvailable,
			final List<String> villagesAvailable,
			final List<String> loneBuildingsAvailable) throws IOException {
		data.writeUTF(key);

		CultureLanguage clientMain = null, clientFallback = null;

		if (loadedLanguages.containsKey(mainLanguage)) {
			clientMain = loadedLanguages.get(mainLanguage);
		} else if (loadedLanguages.containsKey(mainLanguage.split("_")[0])) {
			clientMain = loadedLanguages.get(mainLanguage.split("_")[0]);
		}

		if (loadedLanguages.containsKey(fallbackLanguage)) {
			clientFallback = loadedLanguages.get(fallbackLanguage);
		} else if (loadedLanguages.containsKey(fallbackLanguage.split("_")[0])) {
			clientFallback = loadedLanguages
					.get(fallbackLanguage.split("_")[0]);
		}

		if (clientMain != null && clientMain.strings.size() > nbStrings) {
			StreamReadWrite.writeStringStringMap(clientMain.strings, data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}
		if (clientMain != null
				&& clientMain.buildingNames.size() > nbBuildingNames) {
			StreamReadWrite
					.writeStringStringMap(clientMain.buildingNames, data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}
		if (clientMain != null && clientMain.sentences.size() > nbSentences) {
			StreamReadWrite
					.writeStringStringListMap(clientMain.sentences, data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}

		if (clientFallback != null
				&& clientFallback.strings.size() > nbFallbackStrings) {
			StreamReadWrite.writeStringStringMap(clientFallback.strings, data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}
		if (clientFallback != null
				&& clientFallback.buildingNames.size() > nbFallbackBuildingNames) {
			StreamReadWrite.writeStringStringMap(clientFallback.buildingNames,
					data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}
		if (clientFallback != null
				&& clientFallback.sentences.size() > nbFallbackSentences) {
			StreamReadWrite.writeStringStringListMap(clientFallback.sentences,
					data);
		} else {
			StreamReadWrite.writeStringStringMap(null, data);
		}

		int nbToWrite = 0;

		for (final BuildingPlanSet set : ListPlanSets) {
			if (planSetAvailable == null || !planSetAvailable.contains(set.key)) {
				nbToWrite++;
			}
		}

		data.writeShort(nbToWrite);
		for (final BuildingPlanSet set : ListPlanSets) {
			if (planSetAvailable == null || !planSetAvailable.contains(set.key)) {
				set.writeBuildingPlanSetInfo(data);
			}
		}

		nbToWrite = 0;
		for (final String key : villagerTypes.keySet()) {
			if (villagerAvailable == null || !villagerAvailable.contains(key)) {
				nbToWrite++;
			}
		}

		data.writeShort(nbToWrite);
		for (final String key : villagerTypes.keySet()) {
			if (villagerAvailable == null || !villagerAvailable.contains(key)) {
				final VillagerType vtype = villagerTypes.get(key);
				vtype.writeVillagerTypeInfo(data);
			}
		}

		nbToWrite = 0;
		for (final String key : villageTypes.keySet()) {
			if (villagesAvailable == null || !villagesAvailable.contains(key)) {
				nbToWrite++;
			}
		}

		data.writeShort(nbToWrite);
		for (final String key : villageTypes.keySet()) {
			if (villagesAvailable == null || !villagesAvailable.contains(key)) {
				final VillageType vtype = villageTypes.get(key);
				vtype.writeVillageTypeInfo(data);
			}
		}
		nbToWrite = 0;
		for (final String key : loneBuildingTypes.keySet()) {
			if (loneBuildingsAvailable == null
					|| !loneBuildingsAvailable.contains(key)) {
				nbToWrite++;
			}
		}

		data.writeShort(nbToWrite);
		for (final String key : loneBuildingTypes.keySet()) {
			if (loneBuildingsAvailable == null
					|| !loneBuildingsAvailable.contains(key)) {
				final VillageType vtype = loneBuildingTypes.get(key);
				vtype.writeVillageTypeInfo(data);
			}
		}
	}
}
