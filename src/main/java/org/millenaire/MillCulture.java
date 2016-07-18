package org.millenaire;

import java.util.HashMap;
import java.util.Random;

public class MillCulture 
{
	public final String cultureName;
	//Entry 0 is male child, entry 1 is female child
	VillagerType[] villagerTypes;
	String[] vocalizations;
	HashMap<String, String[]> nameLists = new HashMap<String, String[]>();
	
	public MillCulture(String nameIn)
	{
		cultureName = nameIn;
	}
	
	public MillCulture addNameList(String title, String[] list)
	{
		this.nameLists.put(title, list);
		
		return this;
	}
	
	public MillCulture setVillagerTypes(VillagerType[] typeIn)
	{
		this.villagerTypes = typeIn;
		return this;
	}
	
	public static MillCulture getCulture(String nameIn)
	{
		if(nameIn.equals("norman"))
			return normanCulture;
		
		System.err.println("Villager written to NBT with incorrect culture.  Something broke.");
		return null;
	}
	
	public VillagerType getVillagerType(String typeIn)
	{
		for(int i = 0; i < villagerTypes.length; i++)
		{
			if(villagerTypes[i].id.equalsIgnoreCase(typeIn))
				return villagerTypes[i];
		}
		
		System.err.println("villagerType " + typeIn + " not found in " + cultureName + " culture.");
		return null;
	}

	public VillagerType getChildType(int gender)
	{
		if(gender == 0)
			return villagerTypes[0];
		else
			return villagerTypes[1];
	}
	
	public String getVocalSentence(String vTypeIn)
	{
		return "Hi.  How are ya.";
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static MillCulture normanCulture;
	public static MillCulture hindiCulture;
	public static MillCulture mayanCulture;
	public static MillCulture japaneseCulture;
	public static MillCulture byzantineCulture;
	
	public static MillCulture millDefault;
	
	public static void preinitialize()
	{
		//Norman Initialization
		normanCulture= new MillCulture("norman").addNameList("familyNames", new String[]{"Waldemar", "Vilfrid", "Thorstein", "Tankred", "Svenning", "Sigvald", "Sigmar", "Roland", "Reginald", "Radulf", "Otvard", "Odomar", "Norbert", "Manfred", "Lothar", "Lambert", "Klothar", "Ingmar", "Hubert", "Gildwin", "Gervin", "Gerald", "Froward", "Fredegar", "Falko", "Elfride", "Erwin", "Ditmar", "Didrik", "Bernhard", "Answald", "Adalrik"})
				.addNameList("nobleFamilyNames", new String[]{"de Bayeux", "de Conteville", "de Mortain", "de Falaise", "de Ryes"})
				.addNameList("maleNames", new String[]{"Answald", "Arnbjorn", "Almut", "Arnvald", "Baldrik", "Dankrad", "Dltwin", "Erwin", "Elfride", "Frank", "Froward", "Gerulf", "Gildwin", "Grim", "Hagbard", "Hartmod", "Helge", "Henrik", "Ingvald", "Karl", "Klothar", "Lothar", "Ludvig", "Norbert", "Odomar", "Radulf", "Richard", "Robert", "Roland", "Sigfred", "Tankred", "Thorgal", "Wilhelm"})
				.addNameList("femaleNames", new String[]{"Alfgard", "Alwine", "Bathilde", "Bernhilde", "Borglinde", "Dithilde", "Frida", "Gisela", "Herleva", "Hermine", "Irmine", "Matilde", "Ottilia", "Ragnhild", "Sighild", "Sigrune", "Solvej", "Thilda", "Ulrika", "Valborg"});
	
		normanCulture.setVillagerTypes(new VillagerType[]{new VillagerType("normanBoy", "Garçon", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanBoy0", "millenaire:textures/entity/norman/normanBoy1"}),
				new VillagerType("normanGirl", "Fille", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanGirl0", "millenaire:textures/entity/norman/normanGirl1"}),
				new VillagerType("normanAbbot", "Abbé", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanAbbot0"}),
				new VillagerType("normanLoneAbbot", "Abbé", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanAbbot0"}),
				new VillagerType("normanGuildMaster", "Maître de Guilde", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanGuildMaster0"}),
				new VillagerType("normanSenechal", "Sénéchal", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanSenechal0", "millenaire:textures/entity/norman/normanSenechal1"}),
				new VillagerType("normanKnight", "Chevalier", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanKnight0"}),
				new VillagerType("normanLady", "Dame", 1, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanLady0", "millenaire:textures/entity/norman/normanLady1"}),
				new VillagerType("normanCarpenter", "Charpentier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanCarpenter0"}),
				new VillagerType("normanFarmer", "Fermier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanFarmer0", "millenaire:textures/entity/norman/normanFarmer1"}),
				new VillagerType("normanCattleFarmerMale", "Eleveur Bovin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerMale0"}),
				new VillagerType("normanCattleFarmerFemale", "Eleveuse Bovine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerFemale0"}),
				new VillagerType("normanGuard", "Garde", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanGuard0", "millenaire:textures/entity/norman/normanGuard1"}),
				new VillagerType("normanLumberman", "Bûcheron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanLumberman0", "millenaire:textures/entity/norman/normanLumberman1", "millenaire:textures/entity/norman/normanLumberman2", "millenaire:textures/entity/norman/normanLumberman3"}),
				new VillagerType("normanLoneLumberman", "Bûcheron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanLumberman0", "millenaire:textures/entity/norman/normanLumberman1", "millenaire:textures/entity/norman/normanLumberman2", "millenaire:textures/entity/norman/normanLumberman3"}),
				new VillagerType("normanLoneMiller", "Meunier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanFarmer0", "millenaire:textures/entity/norman/normanFarmer1"}),
				new VillagerType("normanMiner", "Mineur", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanMiner0", "millenaire:textures/entity/norman/normanMiner1"}),
				new VillagerType("normanLoneMiner", "Mineur", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanMiner0", "millenaire:textures/entity/norman/normanMiner1"}),
				new VillagerType("normanMonk", "Moine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanMonk0"}),
				new VillagerType("normanLoneMonk", "Moine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanMonk0"}),
				new VillagerType("normanMerchant", "Marchant", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanMerchant0", "millenaire:textures/entity/norman/normanMerchant1", "millenaire:textures/entity/norman/normanMerchant2"}),
				new VillagerType("normanFoodMerchant", "Marchand de Nourriture", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanFoodMerchant0"}),
				new VillagerType("normanPlantMerchant", "Herboriste", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanPlantMerchant0"}),
				new VillagerType("normanPigherdMale", "Eleveur Porcin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerMale0"}),
				new VillagerType("normanPigherdFemale", "Eleveuse Porcine", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerFemale0"}),
				new VillagerType("normanPriest", "Prêtre", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanPriest0", "millenaire:textures/entity/norman/normanPriest1"}),
				new VillagerType("normanShepherdMale", "Eleveur Ovin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerMale0"}),
				new VillagerType("normanShepherdFemale", "Eleveuse Ovine", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanCattleFarmerFemale0"}),
				new VillagerType("normanBlacksmith", "Forgeron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanBlacksmith0"}),
				new VillagerType("normanWife", "Villageoise", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entity/norman/normanWife0", "millenaire:textures/entity/norman/normanWife1"}),
				new VillagerType("normanAlchemist", "Alchimiste", 0, new String[]{"Vif-argent"}, new String[]{"Guillaume"}, new String[]{"millenaire:textures/entity/norman/normanAlchemist0"}),
				new VillagerType("normanAlchemistAssistant", "Alchimiste", 0, new String[]{"Ulric"}, new String[]{"Robert"}, new String[]{"millenaire:textures/entity/norman/normanAlchemist0"}),
				new VillagerType("normanAlchemist Apprentice", "Apprenti Alchimiste", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanApprentice0"}),
				new VillagerType("normanBandit", "Bandit", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanBandit0", "millenaire:textures/entity/norman/normanBandit1"}),
				new VillagerType("normanArmoredBandit", "ArmoredBandit", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/norman/normanArmoredBandit0", "millenaire:textures/entity/norman/normanArmoredBandit1"})});
		
		//Hindi Initialization
		hindiCulture = new MillCulture("hindi").addNameList("highCasteFamilyNames", new String[]{"Sinha", "Kuwar", "Kuwar", "Mishra", "Pandey", "Jha", "Khatri"})
				.addNameList("lowCasteFamilyNames", new String[]{"Sharma", "Paswan", "Karmakar", "Yadav", "Prasad", "Baghel", "Agariya", "Badhik", "Badi", "Baheliya", "Baiga", "Bajaniya", "Bajgi", "Balai", "Balmiki", "Bangali", "Banmanus", "Bansphor", "Barwar", "Basor", "Bawariya", "Bhantu", "Bhuiya", "Chamar", "Chero", "Dabgar", "Dhangar", "Dhanuk", "Dharkar", "Dhobi", "Domar", "Dusadh", "Gharami", "Ghasiya", "Gond", "Gual", "Habura", "Hari", "Hela", "Kalabaz", "Kanjar", "Kapariya", "Karwal", "Khairaha", "Khatik", "Kharot", "Kori", "Korwa", "Lal Begi", "Majhwar", "Mazhabi", "Musahar", "Nat", "Pankha", "Parahiya", "Pasi", "Patari", "Rawat", "Sahariya", "Sanaurhiya", "Sansiya", "Shilpkar", "Turaiha"})
				.addNameList("highCasteFemaleNames", new String[]{"Abha", "Aditi", "Deepti", "Manasi", "Jyoti", "Shobhana", "Shobha", "Akhila", "Amrita", "Anjali", "Anupama", "Aparajita", "Shalini", "Soumya", "Lavanya"})
				.addNameList("lowCasteFemaleNames", new String[]{"Abha", "Aditi", "Deepti", "Manasi", "Jyoti", "Shobhana", "Shobha", "Rani", "Mayuri", "Geeta", "Seeta", "Chanda", "Titli", "Vimla", "Sudha", "Suman", "Suneeta", "Babli", "Kamala"})
				.addNameList("maleNames", new String[]{"Ravi", "Rajiv", "Santosh", "Akash", "Akhil", "Raj", "Rahul", "Rohit", "Laxman", "Gopal", "Vishnu", "Ashok", "Akshay", "Chetan", "Dilip", "Deepak", "Govind", "Hari", "Harsh", "Kamal", "Madhav"});
		
		hindiCulture.setVillagerTypes(new VillagerType[]{new VillagerType("hindiBoy", "Larka", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiBoy0", "millenaire:textures/entity/hindi/hindiBoy1", "millenaire:textures/entity/hindi/hindiBoy2", "millenaire:textures/entity/hindi/hindiBoy3"}),
				new VillagerType("hindiGirl", "Larki", 1, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiGirl0", "millenaire:textures/entity/hindi/hindiGirl1"}),
				new VillagerType("hindiRaja", "Raja", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiRaja0", "millenaire:textures/entity/hindi/hindiRaja1"}),
				new VillagerType("hindiRajputGeneral", "Rajput Senapati", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiRajputLeader0", "millenaire:textures/entity/hindi/hindiRajputLeader1"}),
				new VillagerType("hindiVillageChief", "Gaanv ka Mukhiya", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiChief0", "millenaire:textures/entity/hindi/hindiChief1"}),
				new VillagerType("hindiRani", "Rani", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), new String[]{"Rani"}, new String[]{"millenaire:textures/entity/hindi/hindiRichWoman0", "millenaire:textures/entity/hindi/hindiRichWoman1", "millenaire:textures/entity/hindi/hindiRichWoman2", "millenaire:textures/entity/hindi/hindiRichWoman3"}),
				new VillagerType("hindiRichWoman", "Malkin", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiRichWoman0", "millenaire:textures/entity/hindi/hindiRichWoman1", "millenaire:textures/entity/hindi/hindiRichWoman2", "millenaire:textures/entity/hindi/hindiRichWoman3"}),
				new VillagerType("hindiAdivasiPeasant", "Adivasi Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPeasant0", "millenaire:textures/entity/hindi/hindiPeasant1", "millenaire:textures/entity/hindi/hindiPeasant2", "millenaire:textures/entity/hindi/hindiPeasant3"}),
				new VillagerType("hindiAdivasiPeasantWife", "Mahilaa Adivasi", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPeasantWife0", "millenaire:textures/entity/hindi/hindiPeasantWife1", "millenaire:textures/entity/hindi/hindiPeasantWife2", "millenaire:textures/entity/hindi/hindiPeasantWife3", "millenaire:textures/entity/hindi/hindiPeasantWife4", "millenaire:textures/entity/hindi/hindiPeasantWife5", "millenaire:textures/entity/hindi/hindiPeasantWife6"}),
				new VillagerType("hindiArmySmith", "Sena ka Loohaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiArmySmith0"}),
				new VillagerType("hindiLumberman", "Larkarhara", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiLumberman0", "millenaire:textures/entity/hindi/hindiLumberman1", "millenaire:textures/entity/hindi/hindiLumberman2", "millenaire:textures/entity/hindi/hindiLumberman3"}),
				new VillagerType("hindiMerchant", "Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiMerchant0", "millenaire:textures/entity/hindi/hindiMerchant1"}),
				new VillagerType("hindiMerchantAdivasi", "Adivasi Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiMerchant0", "millenaire:textures/entity/hindi/hindiMerchant1"}),
				new VillagerType("hindiMerchantVillageWoman", "Gaanv ki Murkha", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPeasantWife0", "millenaire:textures/entity/hindi/hindiPeasantWife1", "millenaire:textures/entity/hindi/hindiPeasantWife2", "millenaire:textures/entity/hindi/hindiPeasantWife3", "millenaire:textures/entity/hindi/hindiPeasantWife4", "millenaire:textures/entity/hindi/hindiPeasantWife5", "millenaire:textures/entity/hindi/hindiPeasantWife6"}),
				new VillagerType("hindiLoneTrader", "Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiMerchant0", "millenaire:textures/entity/hindi/hindiMerchant1"}),
				new VillagerType("hindiMiner", "Khanik", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiLumberman0", "millenaire:textures/entity/hindi/hindiLumberman1", "millenaire:textures/entity/hindi/hindiLumberman2", "millenaire:textures/entity/hindi/hindiLumberman3"}),
				new VillagerType("hindiPriest", "Pandit", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPriest0", "millenaire:textures/entity/hindi/hindiPriest1"}),
				new VillagerType("hindiPanditayin", "Panditayin", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiRichWoman0", "millenaire:textures/entity/hindi/hindiRichWoman1", "millenaire:textures/entity/hindi/hindiRichWoman2", "millenaire:textures/entity/hindi/hindiRichWoman3"}),
				new VillagerType("hindiPeasant", "Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPeasant0", "millenaire:textures/entity/hindi/hindiPeasant1", "millenaire:textures/entity/hindi/hindiPeasant2", "millenaire:textures/entity/hindi/hindiPeasant3"}),
				new VillagerType("hindiPeasantWife", "Mahilaa Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/hindi/hindiPeasantWife0", "millenaire:textures/entity/hindi/hindiPeasantWife1", "millenaire:textures/entity/hindi/hindiPeasantWife2", "millenaire:textures/entity/hindi/hindiPeasantWife3", "millenaire:textures/entity/hindi/hindiPeasantWife4", "millenaire:textures/entity/hindi/hindiPeasantWife5", "millenaire:textures/entity/hindi/hindiPeasantWife6"}),
				new VillagerType("hindiSculptor", "Muurtikaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiSculptor0"}),
				new VillagerType("hindiSmith", "Loohaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiSmith0"}),
				new VillagerType("hindiRajputSoldier", "Rajput Sainik", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/hindi/hindiRajputWarrior0", "millenaire:textures/entity/hindi/hindiRajputWarrior1", "millenaire:textures/entity/hindi/hindiRajputWarrior2"}),
				new VillagerType("hindiSadhu", "Sadhu", 0, new String[]{"Vidya"}, new String[]{"Sadhu"}, new String[]{"millenaire:textures/entity/hindi/hindiSadhu0"})});
		
		//Mayan Initialization
		mayanCulture = new MillCulture("mayan").addNameList("highCasteFamilyNames", new String[]{"Yax Pasaj Chan Yoaat", "Ukit Took'", "K'inich Yak K'uk Mo'", "K'u Ix", "B'alam Nan", "Chan Imix K'awiil", "Waxaklajuun Ub'aah K'awiil"})
				.addNameList("lowCasteFamilyNames", new String[]{"Ichik", "Ikan", "Acat", "Ah Bolom Tzacab", "Ah Cancum", "Ah Chun Cann", "Ah Ciliz", "Ah Cuxtal", "Ah Huluneb", "Ah Kin", "Ah Kumix Uinicob", "Ah Mun", "Ah Muzencab", "Ah Patnar Uinicob", "Ah Peku", "Ah Puch", "Ah Uinicir Dz'acab", "Ah Uuc Ticab", "Backlum Chaam", "Bolontiku", "Camazots", "Chamer", "Chaob", "Chibirias", "Cit-Bolon-Tum", "Cocijo", "Colel Cab", "Cum Hau", "Hanhau", "Hunapu", "Huncame", "Hunhau", "Hurukan", "Ix Chebel Yax", "Ixzaluoh", "Kan-Xib-Yui", "Kinich-Ahau", "Cizin", "Nohochacyum", "Tlacolotl", "Vucub-Caquix", "Xmucane", "Xpiyacoc", "Zipakna", "Kabrakan", "Zots", "Yum Caax", "Colop U Uichkin", "Ab Kin Zoc", "Cacoch", "Cauac", "Mulac", "Naum", "Chiccan", "Ah Kunchil", "Ahpop-Achi", "Atlacatl", "Hunyg", "Ak", "Xipe-Topec"})
				.addNameList("highCasteFemaleNames", new String[]{"Itzel", "Ixchab", "Ixchel", "Ixchup", "Malinali", "Meztli", "Nhutalu", "Quibock-Nicte", "Tzytzyan", "Ysalane", "Zafrina", "Eme", "Yohl Ik'nal", "Emetaly", "Ichika"})
				.addNameList("lowCasteFemaleNames", new String[]{"Arana", "Nictha", "Tamay", "Can", "Chan", "Be", "Cantun", "Canche", "Chi", "Chuc", "Coyoc", "Dzib", "Dzul", "Ehuan", "Hoil", "Hau", "May", "Pool", "Zapo", "Ucan", "Pech", "Camal", "Xiu", "Canul", "Cocom", "Tun"})
				.addNameList("maleNames", new String[]{"Acan", "Ac Yanto", "Ah Kin Xoc", "Ah Tabai", "Bacab", "Balam", "Buluc Chabtan", "Chac Uayab Xoc", "Chantico", "Ekchuah", "Nachancan", "Gucumatz", "Hun-Hunapu", "Itzamna", "Ix", "Ixtab", "Kucumatz", "Tepeu", "Tohil", "Xbalanque", "Kukulcan"});
		
		mayanCulture.setVillagerTypes(new VillagerType[]{new VillagerType("mayanBoy", "Mijin", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanBoy0", "millenaire:textures/entity/mayan/mayanBoy1", "millenaire:textures/entity/mayan/mayanBoy2", "millenaire:textures/entity/mayan/mayanBoy3"}),
				new VillagerType("mayanGirl", "Mijin", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanGirl0", "millenaire:textures/entity/mayan/mayanGirl1"}),
				new VillagerType("mayanChieftain", "Ajaw", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanChieftain0", "millenaire:textures/entity/mayan/mayanChieftain1"}),
				new VillagerType("mayanKing", "Ajaw", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanKing0", "millenaire:textures/entity/mayan/mayanKing1"}),
				new VillagerType("mayanLeader", "Chanan", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanLeader0", "millenaire:textures/entity/mayan/mayanLeader1"}),
				new VillagerType("mayanRichWoman", "Ix", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanRichWoman0", "millenaire:textures/entity/mayan/mayanRichWoman1", "millenaire:textures/entity/mayan/mayanRichWoman2", "millenaire:textures/entity/mayan/mayanRichWoman3"}),
				new VillagerType("mayanArmySmith", "Ah Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanArmySmith0"}),
				new VillagerType("mayanChickenFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanChickenFarmer0"}),
				new VillagerType("mayanCocoaFarmer", "Ka'kau' We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanFarmer0", "millenaire:textures/entity/mayan/mayanFarmer1"}),
				new VillagerType("mayanCrafter", "Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanCrafter0"}),
				new VillagerType("mayanObsidianCrafter", "Ta'as Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanCrafter0"}),
				new VillagerType("mayanFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanFarmer0", "millenaire:textures/entity/mayan/mayanFarmer1"}),
				new VillagerType("mayanLoneFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanFarmer0", "millenaire:textures/entity/mayan/mayanFarmer1"}),
				new VillagerType("mayanLoneFarmerWife", "Atan", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanPeasantWife0", "millenaire:textures/entity/mayan/mayanPeasantWife1", "millenaire:textures/entity/mayan/mayanPeasantWife2", "millenaire:textures/entity/mayan/mayanPeasantWife3", "millenaire:textures/entity/mayan/mayanPeasantWife4", "millenaire:textures/entity/mayan/mayanPeasantWife5", "millenaire:textures/entity/mayan/mayanPeasantWife6"}),
				new VillagerType("mayanLumberman", "Te'xu'", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanLumberman0", "millenaire:textures/entity/mayan/mayanLumberman1", "millenaire:textures/entity/mayan/mayanLumberman2", "millenaire:textures/entity/mayan/mayanLumberman3"}),
				new VillagerType("mayanMerchant", "Ajpay", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanMerchant0", "millenaire:textures/entity/mayan/mayanMerchant1"}),
				new VillagerType("mayanMerchantFarmer", "Nuunjul We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanFarmer0", "millenaire:textures/entity/mayan/mayanFarmer1"}),
				new VillagerType("mayanMerchantHunter", "Nuunjul Aaj Inic", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanLumberman0", "millenaire:textures/entity/mayan/mayanLumberman1", "millenaire:textures/entity/mayan/mayanLumberman2", "millenaire:textures/entity/mayan/mayanLumberman3"}),
				new VillagerType("mayanMerchantShaman", "Nuunjul Aj K'in", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanShaman0", "millenaire:textures/entity/mayan/mayanShaman1"}),
				new VillagerType("mayanMiner", "Pan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanLumberman0", "millenaire:textures/entity/mayan/mayanLumberman1", "millenaire:textures/entity/mayan/mayanLumberman2", "millenaire:textures/entity/mayan/mayanLumberman3"}),
				new VillagerType("mayanPeasant", "Winik", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanPeasant0", "millenaire:textures/entity/mayan/mayanPeasant1", "millenaire:textures/entity/mayan/mayanPeasant2", "millenaire:textures/entity/mayan/mayanPeasant3"}),
				new VillagerType("mayanPeasantWife", "Atan", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanPeasantWife0", "millenaire:textures/entity/mayan/mayanPeasantWife1", "millenaire:textures/entity/mayan/mayanPeasantWife2", "millenaire:textures/entity/mayan/mayanPeasantWife3", "millenaire:textures/entity/mayan/mayanPeasantWife4", "millenaire:textures/entity/mayan/mayanPeasantWife5", "millenaire:textures/entity/mayan/mayanPeasantWife6"}),
				new VillagerType("mayanSculptor", "Uxul", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanSculptor0"}),
				new VillagerType("mayanShaman", "Aj K'in", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanShaman0", "millenaire:textures/entity/mayan/mayanShaman1"}),
				new VillagerType("mayanShamanWife", "Aj", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanRichWoman0", "millenaire:textures/entity/mayan/mayanRichWoman1", "millenaire:textures/entity/mayan/mayanRichWoman2", "millenaire:textures/entity/mayan/mayanRichWoman3"}),
				new VillagerType("mayanLoneShaman", "Aj K'in", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanShaman0", "millenaire:textures/entity/mayan/mayanShaman1"}),
				new VillagerType("mayanLoneShamanWife", "Aj", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanRichWoman0", "millenaire:textures/entity/mayan/mayanRichWoman1", "millenaire:textures/entity/mayan/mayanRichWoman2", "millenaire:textures/entity/mayan/mayanRichWoman3"}),
				new VillagerType("mayanWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanWarrior0", "millenaire:textures/entity/mayan/mayanWarrior1", "millenaire:textures/entity/mayan/mayanWarrior2"}),
				new VillagerType("mayanFallenKing", "Keban Ajaw", 0, new String[]{"K'u Ix"}, new String[]{"Ixtab"}, new String[]{"millenaire:textures/entity/mayan/mayanKing0"}),
				new VillagerType("mayanArmyEliteWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanWarrior0", "millenaire:textures/entity/mayan/mayanWarrior1", "millenaire:textures/entity/mayan/mayanWarrior2"}),
				new VillagerType("mayanArmyLieutenant", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanWarrior0", "millenaire:textures/entity/mayan/mayanWarrior1", "millenaire:textures/entity/mayan/mayanWarrior2"}),
				new VillagerType("mayanArmyScout", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanWarrior0", "millenaire:textures/entity/mayan/mayanWarrior1", "millenaire:textures/entity/mayan/mayanWarrior2"}),
				new VillagerType("mayanArmyWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanWarrior0", "millenaire:textures/entity/mayan/mayanWarrior1", "millenaire:textures/entity/mayan/mayanWarrior2"}),
				new VillagerType("mayanQuestShaman", "Aj K'in", 0, new String[]{"Uchben"}, new String[]{"Tohil"}, new String[]{"millenaire:textures/entity/mayan/mayanShaman0", "millenaire:textures/entity/mayan/mayanShaman1"}),
				new VillagerType("mayanBanditMale", "K'as Mijin", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanBanditMale0", "millenaire:textures/entity/mayan/mayanBanditMale1"}),
				new VillagerType("mayanBanditFemale", "K'as Aj", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/mayan/mayanBanditFemale0", "millenaire:textures/entity/mayan/mayanBanditFemale1"}),
				new VillagerType("mayanBanditWarrior", "K'as Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/mayan/mayanBanditWarrior0", "millenaire:textures/entity/mayan/mayanBanditWarrior1", "millenaire:textures/entity/mayan/mayanBanditWarrior2"})});
		
		//Japanese Initialization
		japaneseCulture = new MillCulture("japanese").addNameList("highCasteFamilyNames", new String[]{"Minamoto", "Fujiwara", "Taira", "Shikibu", "Ki", "Ariwara", "Tachibana", "Soga"})
				.addNameList("lowCasteFamilyNames", new String[]{"Date", "Sakuma", "Sato", "Suzuki", "Takahashi", "Watanabe", "Nakamura", "Kobayashi", "Yoshida", "Mori", "Hattori", "Mako", "Hashiba", "Sasaki", "Ito", "Kudo", "Kimura", "Narita", "Chiba", "Kikuchi", "Endo", "Arai", "Yamamoto", "Yamada", "Fukazawa", "Mochizuki", "Kato", "Nishimura", "Maeda", "Tanaka", "Fujii", "Ochi", "Oonishi", "Yamashita", "Hamada", "Komatsu", "Inoue", "Ono", "Goto", "Abe", "Sakamoto", "Matsumoto", "Kai", "Kuroki", "Kawano", "Hidaka", "Arakaki", "Miyagi", "Oshiro", "Higa", "Murakami", "Yamaguchi", "Kinjo", "Hideyoshi"})
				.addNameList("highCasteFemaleNames", new String[]{"Takako", "Murasaki", "Izumi", "Komachi"})
				.addNameList("lowCasteFemaleNames", new String[]{"Akane", "Ami", "Asuka", "Aya", "Ayano", "Hina", "Kana", "Mai", "Mayu", "Miki", "Misaki", "Miyu", "Mizuki", "Nana", "Nanami", "Natsumi", "Reina", "Riko", "Rin", "Saika", "Saki", "Sakura", "Yui", "Yuukama"})
				.addNameList("maleNames", new String[]{"Akira", "Eiichi", "Entarou", "Gaku", "Gojirou", "Hachitarou", "Hajime", "Haruki", "Hideki", "Hiro", "Hitoshi", "Ichirou", "Itsuo", "Jin", "Kenichi", "Kentaro", "Mineo", "Mitsuru", "Noato", "Osamu", "Reijiro", "Renzo", "Saburo", "Shingo", "Shinjiro", "Shinya", "Shouji", "Tadashi", "Takuji", "Tai", "Toyotomi", "Tsuyoshi", "Yuichiro", "Yuijiro"});
		
		japaneseCulture.setVillagerTypes(new VillagerType[]{new VillagerType("japaneseBoy", "Danshi", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/japanese/japaneseBoy0", "millenaire:textures/entity/japanese/japaneseBoy1"}),
				new VillagerType("japaneseGirl", "Jou", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entity/japanese/japaneseGirl0", "millenaire:textures/entity/japanese/japaneseGirl1"}),
				new VillagerType("japaneseBrewer", "Touji", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/japanese/japaneseBrewer0"}),
				new VillagerType("japaneseFarmerChief", "Chokan", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/japanese/japanesePeasant1"}),
				new VillagerType("japaneseKuge", "Kokushi", 0, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/japanese/japaneseOverseer0", "millenaire:textures/entity/japanese/japaneseOverseer1"}),
				new VillagerType("japaneseSamuraiGeneral", "Shukun", 0, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entity/japanese/japaneseWarriorMaster0"})
		});
	}
}
