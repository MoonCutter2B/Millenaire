package org.millenaire;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.millenaire.building.BuildingPlan;
import org.millenaire.building.BuildingProject;
import org.millenaire.building.BuildingTypes;
import org.millenaire.util.JsonHelper;
import org.millenaire.util.JsonHelper.VillageTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.server.MinecraftServer;

public class MillCulture 
{
	public final String cultureName;
	//Entry 0 is male child, entry 1 is female child
	private VillagerType[] villagerTypes;
	private VillageType[] villageTypes;
	private BuildingPlan[] loneBuildings;
	private String[] vocalizations;
	private HashMap<String, String[]> nameLists = new HashMap<String, String[]>();

	private MillCulture(String nameIn)
	{
		cultureName = nameIn;
	}
	
	private MillCulture addNameList(String title, String[] list)
	{
		this.nameLists.put(title, list);
		return this;
	}
	
	private MillCulture setVillagerTypes(VillagerType[] typeIn)
	{
		this.villagerTypes = typeIn;
		return this;
	}
	
	public MillCulture setVillageTypes(VillageType[] typeIn)
	{
		this.villageTypes = typeIn;
		return this;
	}
	
	public MillCulture setLoneBuildings(BuildingPlan[] loneIn)
	{
		this.loneBuildings = loneIn;
		return this;
	}
	
	public VillagerType[] getVillagerTypes() { return this.villagerTypes; }
	
	public VillagerType getVillagerType(String typeIn)
	{
		for (VillagerType villagerType : villagerTypes) 
		{
			if (villagerType.id.equalsIgnoreCase(typeIn)) 
			{
				return villagerType;
			}
		}
		
		System.err.println("villagerType " + typeIn + " not found in " + cultureName + " culture.");
		return null;
	}

	public VillagerType getChildType(int gender)
	{
		if(gender == 0)
		{
			return villagerTypes[0];
		}
		else
		{
			return villagerTypes[1];
		}
	}
	
	public VillageType getVillageType(String typeIn)
	{
		for (VillageType villageType : villageTypes) 
		{
			if (villageType.id.equalsIgnoreCase(typeIn)) 
			{
				return villageType;
			}
		}
		
		System.err.println("villageType " + typeIn + " not found in " + cultureName + " culture.");
		return null;
	}
	
	public VillageType getRandomVillageType()
	{
		Random rand = new Random();
		int i = rand.nextInt(villageTypes.length);
		
		return villageTypes[i];
	}
	
	public String getVillageName() { return "Whoville"; }
	
	public String getVocalSentence(String vTypeIn) { return "Hi.  How are ya."; }
	
	//Remember to catch the Exception and handle it when calling getCulture
	public static MillCulture getCulture(String nameIn) throws Exception
	{
		switch (nameIn) {
			case "norman":
			    return normanCulture;
            case "hindi":
                return hindiCulture;
            case "mayan":
                return mayanCulture;
            case "japanese":
                return japaneseCulture;
            case "byzantine":
                return byzantineCulture;
            default:
                throw new Exception("getCulture called with incorrect culture.");
		}
	}
	
	public void exportVillages(JsonHelper.VillageTypes villagetypes) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//System.out.println(gson.toJson(villagetypes));
		File f = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + File.separator + "millenaire" + File.separator + "exports" + File.separator);
		File f1 = new File(f, "villages.json");
		try {
			f.mkdirs();
			f1.createNewFile();
			String g = gson.toJson(villagetypes);
			FileWriter fw = new FileWriter(f1);
			fw.write(g);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void loadVillageTypes() {
		Gson gson = new Gson();
		InputStream is = MillCulture.class.getClassLoader().getResourceAsStream("assets/millenaire/cultures/" + this.cultureName.toLowerCase() + "/villages.json");
		VillageTypes vt = gson.fromJson(new InputStreamReader(is), VillageTypes.class);
		this.villageTypes = vt.types;
		
		BuildingTypes.cacheBuildingTypes(normanCulture);
	}
	
	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static MillCulture normanCulture;
	private static MillCulture hindiCulture;
    private static MillCulture mayanCulture;
    private static MillCulture japaneseCulture;
    private static MillCulture byzantineCulture;

	//public static MillCulture millDefault;

	public static void preinitialize()
	{
		//Norman Initialization
		normanCulture= new MillCulture("norman").addNameList("familyNames", new String[]{"Waldemar", "Vilfrid", "Thorstein", "Tankred", "Svenning", "Sigvald", "Sigmar", "Roland", "Reginald", "Radulf", "Otvard", "Odomar", "Norbert", "Manfred", "Lothar", "Lambert", "Klothar", "Ingmar", "Hubert", "Gildwin", "Gervin", "Gerald", "Froward", "Fredegar", "Falko", "Elfride", "Erwin", "Ditmar", "Didrik", "Bernhard", "Answald", "Adalrik"})
				.addNameList("nobleFamilyNames", new String[]{"de Bayeux", "de Conteville", "de Mortain", "de Falaise", "de Ryes"})
				.addNameList("maleNames", new String[]{"Answald", "Arnbjorn", "Almut", "Arnvald", "Baldrik", "Dankrad", "Dltwin", "Erwin", "Elfride", "Frank", "Froward", "Gerulf", "Gildwin", "Grim", "Hagbard", "Hartmod", "Helge", "Henrik", "Ingvald", "Karl", "Klothar", "Lothar", "Ludvig", "Norbert", "Odomar", "Radulf", "Richard", "Robert", "Roland", "Sigfred", "Tankred", "Thorgal", "Wilhelm"})
				.addNameList("femaleNames", new String[]{"Alfgard", "Alwine", "Bathilde", "Bernhilde", "Borglinde", "Dithilde", "Frida", "Gisela", "Herleva", "Hermine", "Irmine", "Matilde", "Ottilia", "Ragnhild", "Sighild", "Sigrune", "Solvej", "Thilda", "Ulrika", "Valborg"});
	
		normanCulture.setVillagerTypes(
				new VillagerType[]{new VillagerType("normanBoy", "GarÃ§on", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanBoy0.png", "millenaire:textures/entities/norman/normanBoy1.png"}, false, false, 0),
				new VillagerType("normanGirl", "Fille", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanGirl0.png", "millenaire:textures/entities/norman/normanGirl1.png"}, false, false, 0),
				new VillagerType("normanAbbot", "AbbÃ©", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanAbbot0.png"}, true, false, 0),
				new VillagerType("normanLoneAbbot", "AbbÃ©", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanAbbot0.png"}, false, false, 0),
				new VillagerType("normanGuildMaster", "MaÃ®tre de Guilde", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanGuildMaster0.png"}, true, false, 0),
				new VillagerType("normanSenechal", "SÃ©nÃ©chal", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanSenechal0.png", "millenaire:textures/entities/norman/normanSenechal1.png"}, true, false, 0),
				new VillagerType("normanKnight", "Chevalier", 0, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanKnight0.png"}, true, false, 0),
				new VillagerType("normanLady", "Dame", 1, normanCulture.nameLists.get("nobleFamilyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanLady0.png", "millenaire:textures/entities/norman/normanLady1.png"}, false, false, 0),
				new VillagerType("normanCarpenter", "Charpentier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanCarpenter0.png"}, false, false, 0),
				new VillagerType("normanFarmer", "Fermier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanFarmer0.png", "millenaire:textures/entities/norman/normanFarmer1.png"}, false, false, 0),
				new VillagerType("normanCattleFarmerMale", "Eleveur Bovin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerMale0.png"}, false, false, 0),
				new VillagerType("normanCattleFarmerFemale", "Eleveuse Bovine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerFemale0.png"}, false, true, 0),
				new VillagerType("normanGuard", "Garde", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanGuard0.png", "millenaire:textures/entities/norman/normanGuard1.png"}, false, false, 32),
				new VillagerType("normanLumberman", "BÃ»cheron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanLumberman0.png", "millenaire:textures/entities/norman/normanLumberman1.png", "millenaire:textures/entities/norman/normanLumberman2.png", "millenaire:textures/entities/norman/normanLumberman3.png"}, false, false, 16),
				new VillagerType("normanLoneLumberman", "BÃ»cheron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanLumberman0.png", "millenaire:textures/entities/norman/normanLumberman1.png", "millenaire:textures/entities/norman/normanLumberman2.png", "millenaire:textures/entities/norman/normanLumberman3.png"}, false, false, 16),
				new VillagerType("normanLoneMiller", "Meunier", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanFarmer0.png", "millenaire:textures/entities/norman/normanFarmer1.png"}, false, false, 0),
				new VillagerType("normanMiner", "Mineur", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanMiner0.png", "millenaire:textures/entities/norman/normanMiner1.png"}, false, false, 0),
				new VillagerType("normanLoneMiner", "Mineur", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanMiner0.png", "millenaire:textures/entities/norman/normanMiner1.png"}, false, false, 0),
				new VillagerType("normanMonk", "Moine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanMonk0.png"}, false, false, 0),
				new VillagerType("normanLoneMonk", "Moine", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanMonk0.png"}, false, false, 0),
				new VillagerType("normanMerchant", "Marchant", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanMerchant0.png", "millenaire:textures/entities/norman/normanMerchant1.png", "millenaire:textures/entities/norman/normanMerchant2.png"}, false, false, 0),
				new VillagerType("normanFoodMerchant", "Marchand de Nourriture", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanFoodMerchant0.png"}, false, false, 0),
				new VillagerType("normanPlantMerchant", "Herboriste", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanPlantMerchant0.png"}, false, false, 0),
				new VillagerType("normanPigherdMale", "Eleveur Porcin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerMale0.png"}, false, false, 0),
				new VillagerType("normanPigherdFemale", "Eleveuse Porcine", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerFemale0.png"}, false, true, 0),
				new VillagerType("normanPriest", "PrÃªtre", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanPriest0.png", "millenaire:textures/entities/norman/normanPriest1.png"}, false, false, 0),
				new VillagerType("normanShepherdMale", "Eleveur Ovin", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerMale0.png"}, false, false, 0),
				new VillagerType("normanShepherdFemale", "Eleveuse Ovine", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanCattleFarmerFemale0.png"}, false, true, 0),
				new VillagerType("normanBlacksmith", "Forgeron", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanBlacksmith0.png"}, false, false, 0),
				new VillagerType("normanWife", "Villageoise", 1, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/norman/normanWife0.png", "millenaire:textures/entities/norman/normanWife1.png"}, false, true, 0),
				new VillagerType("normanAlchemist", "Alchimiste", 0, new String[]{"Vif-argent"}, new String[]{"Guillaume"}, new String[]{"millenaire:textures/entities/norman/normanAlchemist0.png"}, false, false, 0),
				new VillagerType("normanAlchemistAssistant", "Assistant", 0, new String[]{"Ulric"}, new String[]{"Robert"}, new String[]{"millenaire:textures/entities/norman/normanAlchemistAssistant0.png"}, false, false, 0),
				new VillagerType("normanAlchemistApprentice", "Apprenti Alchimiste", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanApprentice0.png"}, false, false, 0),
				new VillagerType("normanBandit", "Bandit", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanBandit0.png", "millenaire:textures/entities/norman/normanBandit1.png"}, false, false, 0),
				new VillagerType("normanArmoredBandit", "ArmoredBandit", 0, normanCulture.nameLists.get("familyNames"), normanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/norman/normanArmoredBandit0.png", "millenaire:textures/entities/norman/normanArmoredBandit1.png"}, false, false, 0)
		});

		/*normanCulture.setVillageTypes(new VillageType[]{
				new VillageType("test").setBuildingTypes(new BuildingPlan[]{BuildingProject.normanCommunauteA0}, new BuildingPlan[]{BuildingProject.testBuilding}, new BuildingPlan[]{BuildingProject.testBuilding}).setStartingBuildings(new BuildingPlan[]{BuildingProject.normanCommunauteA0})
		});*/
		
		//Hindi Initialization
		hindiCulture = new MillCulture("hindi").addNameList("highCasteFamilyNames", new String[]{"Sinha", "Kuwar", "Kuwar", "Mishra", "Pandey", "Jha", "Khatri"})
				.addNameList("lowCasteFamilyNames", new String[]{"Sharma", "Paswan", "Karmakar", "Yadav", "Prasad", "Baghel", "Agariya", "Badhik", "Badi", "Baheliya", "Baiga", "Bajaniya", "Bajgi", "Balai", "Balmiki", "Bangali", "Banmanus", "Bansphor", "Barwar", "Basor", "Bawariya", "Bhantu", "Bhuiya", "Chamar", "Chero", "Dabgar", "Dhangar", "Dhanuk", "Dharkar", "Dhobi", "Domar", "Dusadh", "Gharami", "Ghasiya", "Gond", "Gual", "Habura", "Hari", "Hela", "Kalabaz", "Kanjar", "Kapariya", "Karwal", "Khairaha", "Khatik", "Kharot", "Kori", "Korwa", "Lal Begi", "Majhwar", "Mazhabi", "Musahar", "Nat", "Pankha", "Parahiya", "Pasi", "Patari", "Rawat", "Sahariya", "Sanaurhiya", "Sansiya", "Shilpkar", "Turaiha"})
				.addNameList("highCasteFemaleNames", new String[]{"Abha", "Aditi", "Deepti", "Manasi", "Jyoti", "Shobhana", "Shobha", "Akhila", "Amrita", "Anjali", "Anupama", "Aparajita", "Shalini", "Soumya", "Lavanya"})
				.addNameList("lowCasteFemaleNames", new String[]{"Abha", "Aditi", "Deepti", "Manasi", "Jyoti", "Shobhana", "Shobha", "Rani", "Mayuri", "Geeta", "Seeta", "Chanda", "Titli", "Vimla", "Sudha", "Suman", "Suneeta", "Babli", "Kamala"})
				.addNameList("maleNames", new String[]{"Ravi", "Rajiv", "Santosh", "Akash", "Akhil", "Raj", "Rahul", "Rohit", "Laxman", "Gopal", "Vishnu", "Ashok", "Akshay", "Chetan", "Dilip", "Deepak", "Govind", "Hari", "Harsh", "Kamal", "Madhav"});
		
		hindiCulture.setVillagerTypes(new VillagerType[]{
				new VillagerType("hindiBoy", "Larka", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiBoy0.png", "millenaire:textures/entities/hindi/hindiBoy1.png", "millenaire:textures/entities/hindi/hindiBoy2.png", "millenaire:textures/entities/hindi/hindiBoy3.png"}, false, false, 0),
				new VillagerType("hindiGirl", "Larki", 1, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiGirl0.png", "millenaire:textures/entities/hindi/hindiGirl1.png"}, false, false, 0),
				new VillagerType("hindiRaja", "Raja", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiRaja0.png", "millenaire:textures/entities/hindi/hindiRaja1.png"}, true, false, 0),
				new VillagerType("hindiRajputGeneral", "Rajput Senapati", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiRajputLeader0.png", "millenaire:textures/entities/hindi/hindiRajputLeader1.png"}, true, false, 0),
				new VillagerType("hindiVillageChief", "Gaanv ka Mukhiya", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiChief0.png", "millenaire:textures/entities/hindi/hindiChief1.png"}, true, false, 0),
				new VillagerType("hindiRani", "Rani", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), new String[]{"Rani"}, new String[]{"millenaire:textures/entities/hindi/hindiRichWoman0.png", "millenaire:textures/entities/hindi/hindiRichWoman1.png", "millenaire:textures/entities/hindi/hindiRichWoman2.png", "millenaire:textures/entities/hindi/hindiRichWoman3.png"}, false, false, 0),
				new VillagerType("hindiRichWoman", "Malkin", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiRichWoman0.png", "millenaire:textures/entities/hindi/hindiRichWoman1.png", "millenaire:textures/entities/hindi/hindiRichWoman2.png", "millenaire:textures/entities/hindi/hindiRichWoman3.png"}, false, false, 0),
				new VillagerType("hindiAdivasiPeasant", "Adivasi Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPeasant0.png", "millenaire:textures/entities/hindi/hindiPeasant1.png", "millenaire:textures/entities/hindi/hindiPeasant2.png", "millenaire:textures/entities/hindi/hindiPeasant3.png"}, false, false, 0),
				new VillagerType("hindiAdivasiPeasantWife", "Mahilaa Adivasi", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPeasantWife0.png", "millenaire:textures/entities/hindi/hindiPeasantWife1.png", "millenaire:textures/entities/hindi/hindiPeasantWife2.png", "millenaire:textures/entities/hindi/hindiPeasantWife3.png", "millenaire:textures/entities/hindi/hindiPeasantWife4.png", "millenaire:textures/entities/hindi/hindiPeasantWife5.png", "millenaire:textures/entities/hindi/hindiPeasantWife6.png"}, false, false, 0),
				new VillagerType("hindiArmySmith", "Sena ka Loohaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiArmySmith0.png"}, false, false, 0),
				new VillagerType("hindiLumberman", "Larkarhara", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiLumberman0.png", "millenaire:textures/entities/hindi/hindiLumberman1.png", "millenaire:textures/entities/hindi/hindiLumberman2.png", "millenaire:textures/entities/hindi/hindiLumberman3.png"}, false, false, 16),
				new VillagerType("hindiMerchant", "Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiMerchant0.png", "millenaire:textures/entities/hindi/hindiMerchant1.png"}, false, false, 0),
				new VillagerType("hindiMerchantAdivasi", "Adivasi Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiMerchant0.png", "millenaire:textures/entities/hindi/hindiMerchant1.png"}, false, false, 0),
				new VillagerType("hindiMerchantVillageWoman", "Gaanv ki Murkha", 1, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPeasantWife0.png", "millenaire:textures/entities/hindi/hindiPeasantWife1.png", "millenaire:textures/entities/hindi/hindiPeasantWife2.png", "millenaire:textures/entities/hindi/hindiPeasantWife3.png", "millenaire:textures/entities/hindi/hindiPeasantWife4.png", "millenaire:textures/entities/hindi/hindiPeasantWife5.png", "millenaire:textures/entities/hindi/hindiPeasantWife6.png"}, false, false, 0),
				new VillagerType("hindiLoneTrader", "Vyapari", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiMerchant0.png", "millenaire:textures/entities/hindi/hindiMerchant1.png"}, false, false, 0),
				new VillagerType("hindiMiner", "Khanik", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiLumberman0.png", "millenaire:textures/entities/hindi/hindiLumberman1.png", "millenaire:textures/entities/hindi/hindiLumberman2.png", "millenaire:textures/entities/hindi/hindiLumberman3.png"}, false, false, 0),
				new VillagerType("hindiPriest", "Pandit", 0, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPriest0.png", "millenaire:textures/entities/hindi/hindiPriest1.png"}, false, false, 0),
				new VillagerType("hindiPanditayin", "Panditayin", 1, hindiCulture.nameLists.get("highCasteFamilyNames"), hindiCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiRichWoman0.png", "millenaire:textures/entities/hindi/hindiRichWoman1.png", "millenaire:textures/entities/hindi/hindiRichWoman2.png", "millenaire:textures/entities/hindi/hindiRichWoman3.png"}, false, false, 0),
				new VillagerType("hindiPeasant", "Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPeasant0.png", "millenaire:textures/entities/hindi/hindiPeasant1.png", "millenaire:textures/entities/hindi/hindiPeasant2.png", "millenaire:textures/entities/hindi/hindiPeasant3.png"}, false, true, 0),
				new VillagerType("hindiPeasantWife", "Mahilaa Kisaan", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/hindi/hindiPeasantWife0.png", "millenaire:textures/entities/hindi/hindiPeasantWife1.png", "millenaire:textures/entities/hindi/hindiPeasantWife2.png", "millenaire:textures/entities/hindi/hindiPeasantWife3.png", "millenaire:textures/entities/hindi/hindiPeasantWife4.png", "millenaire:textures/entities/hindi/hindiPeasantWife5.png", "millenaire:textures/entities/hindi/hindiPeasantWife6.png"}, false, false, 0),
				new VillagerType("hindiSculptor", "Muurtikaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiSculptor0.png"}, false, false, 0),
				new VillagerType("hindiSmith", "Loohaar", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiSmith0.png"}, false, false, 0),
				new VillagerType("hindiRajputSoldier", "Rajput Sainik", 0, hindiCulture.nameLists.get("lowCasteFamilyNames"), hindiCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/hindi/hindiRajputWarrior0.png", "millenaire:textures/entities/hindi/hindiRajputWarrior1.png", "millenaire:textures/entities/hindi/hindiRajputWarrior2.png"}, false, false, 32),
				new VillagerType("hindiSadhu", "Sadhu", 0, new String[]{"Vidya"}, new String[]{"Sadhu"}, new String[]{"millenaire:textures/entities/hindi/hindiSadhu0.png"}, false, false, 0)
		});
		
		//Mayan Initialization
		mayanCulture = new MillCulture("mayan").addNameList("highCasteFamilyNames", new String[]{"Yax Pasaj Chan Yoaat", "Ukit Took'", "K'inich Yak K'uk Mo'", "K'u Ix", "B'alam Nan", "Chan Imix K'awiil", "Waxaklajuun Ub'aah K'awiil"})
				.addNameList("lowCasteFamilyNames", new String[]{"Ichik", "Ikan", "Acat", "Ah Bolom Tzacab", "Ah Cancum", "Ah Chun Cann", "Ah Ciliz", "Ah Cuxtal", "Ah Huluneb", "Ah Kin", "Ah Kumix Uinicob", "Ah Mun", "Ah Muzencab", "Ah Patnar Uinicob", "Ah Peku", "Ah Puch", "Ah Uinicir Dz'acab", "Ah Uuc Ticab", "Backlum Chaam", "Bolontiku", "Camazots", "Chamer", "Chaob", "Chibirias", "Cit-Bolon-Tum", "Cocijo", "Colel Cab", "Cum Hau", "Hanhau", "Hunapu", "Huncame", "Hunhau", "Hurukan", "Ix Chebel Yax", "Ixzaluoh", "Kan-Xib-Yui", "Kinich-Ahau", "Cizin", "Nohochacyum", "Tlacolotl", "Vucub-Caquix", "Xmucane", "Xpiyacoc", "Zipakna", "Kabrakan", "Zots", "Yum Caax", "Colop U Uichkin", "Ab Kin Zoc", "Cacoch", "Cauac", "Mulac", "Naum", "Chiccan", "Ah Kunchil", "Ahpop-Achi", "Atlacatl", "Hunyg", "Ak", "Xipe-Topec"})
				.addNameList("highCasteFemaleNames", new String[]{"Itzel", "Ixchab", "Ixchel", "Ixchup", "Malinali", "Meztli", "Nhutalu", "Quibock-Nicte", "Tzytzyan", "Ysalane", "Zafrina", "Eme", "Yohl Ik'nal", "Emetaly", "Ichika"})
				.addNameList("lowCasteFemaleNames", new String[]{"Arana", "Nictha", "Tamay", "Can", "Chan", "Be", "Cantun", "Canche", "Chi", "Chuc", "Coyoc", "Dzib", "Dzul", "Ehuan", "Hoil", "Hau", "May", "Pool", "Zapo", "Ucan", "Pech", "Camal", "Xiu", "Canul", "Cocom", "Tun"})
				.addNameList("maleNames", new String[]{"Acan", "Ac Yanto", "Ah Kin Xoc", "Ah Tabai", "Bacab", "Balam", "Buluc Chabtan", "Chac Uayab Xoc", "Chantico", "Ekchuah", "Nachancan", "Gucumatz", "Hun-Hunapu", "Itzamna", "Ix", "Ixtab", "Kucumatz", "Tepeu", "Tohil", "Xbalanque", "Kukulcan"});
		
		mayanCulture.setVillagerTypes(new VillagerType[]{
				new VillagerType("mayanBoy", "Mijin", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanBoy0.png", "millenaire:textures/entities/mayan/mayanBoy1.png", "millenaire:textures/entities/mayan/mayanBoy2.png", "millenaire:textures/entities/mayan/mayanBoy3.png"}, false, false, 0),
				new VillagerType("mayanGirl", "Mijin", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanGirl0.png", "millenaire:textures/entities/mayan/mayanGirl1.png"}, false, false, 0),
				new VillagerType("mayanChieftain", "Ajaw", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanChieftain0.png", "millenaire:textures/entities/mayan/mayanChieftain1.png"}, true, false, 0),
				new VillagerType("mayanKing", "Ajaw", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanKing0.png", "millenaire:textures/entities/mayan/mayanKing1.png"}, true, false, 0),
				new VillagerType("mayanLeader", "Chanan", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanLeader0.png", "millenaire:textures/entities/mayan/mayanLeader1.png"}, true, false, 0),
				new VillagerType("mayanRichWoman", "Ix", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanRichWoman0.png", "millenaire:textures/entities/mayan/mayanRichWoman1.png", "millenaire:textures/entities/mayan/mayanRichWoman2.png", "millenaire:textures/entities/mayan/mayanRichWoman3.png"}, true, false, 0),
				new VillagerType("mayanArmySmith", "Ah Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanArmySmith0.png"}, false, false, 0),
				new VillagerType("mayanChickenFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanChickenFarmer0.png"}, false, false, 0),
				new VillagerType("mayanCocoaFarmer", "Ka'kau' We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanFarmer0.png", "millenaire:textures/entities/mayan/mayanFarmer1.png"}, false, false, 0),
				new VillagerType("mayanCrafter", "Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanCrafter0.png"}, false, false, 0),
				new VillagerType("mayanObsidianCrafter", "Ta'as Tz'on", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanCrafter0.png"}, false, false, 0),
				new VillagerType("mayanFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanFarmer0.png", "millenaire:textures/entities/mayan/mayanFarmer1.png"}, false, false, 0),
				new VillagerType("mayanLoneFarmer", "We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanFarmer0.png", "millenaire:textures/entities/mayan/mayanFarmer1.png"}, false, false, 0),
				new VillagerType("mayanLoneFarmerWife", "Atan", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanPeasantWife0.png", "millenaire:textures/entities/mayan/mayanPeasantWife1.png", "millenaire:textures/entities/mayan/mayanPeasantWife2.png", "millenaire:textures/entities/mayan/mayanPeasantWife3.png", "millenaire:textures/entities/mayan/mayanPeasantWife4.png", "millenaire:textures/entities/mayan/mayanPeasantWife5.png", "millenaire:textures/entities/mayan/mayanPeasantWife6.png"}, false, false, 0),
				new VillagerType("mayanLumberman", "Te'xu'", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanLumberman0.png", "millenaire:textures/entities/mayan/mayanLumberman1.png", "millenaire:textures/entities/mayan/mayanLumberman2.png", "millenaire:textures/entities/mayan/mayanLumberman3.png"}, false, false, 16),
				new VillagerType("mayanMerchant", "Ajpay", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanMerchant0.png", "millenaire:textures/entities/mayan/mayanMerchant1.png"}, false, false, 0),
				new VillagerType("mayanMerchantFarmer", "Nuunjul We'matz", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanFarmer0.png", "millenaire:textures/entities/mayan/mayanFarmer1.png"}, false, false, 0),
				new VillagerType("mayanMerchantHunter", "Nuunjul Aaj Inic", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanLumberman0.png", "millenaire:textures/entities/mayan/mayanLumberman1.png", "millenaire:textures/entities/mayan/mayanLumberman2.png", "millenaire:textures/entities/mayan/mayanLumberman3.png"}, false, false, 0),
				new VillagerType("mayanMerchantShaman", "Nuunjul Aj K'in", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanShaman0.png", "millenaire:textures/entities/mayan/mayanShaman1.png"}, false, false, 0),
				new VillagerType("mayanMiner", "Pan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanLumberman0.png", "millenaire:textures/entities/mayan/mayanLumberman1.png", "millenaire:textures/entities/mayan/mayanLumberman2.png", "millenaire:textures/entities/mayan/mayanLumberman3.png"}, false, false, 0),
				new VillagerType("mayanPeasant", "Winik", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanPeasant0.png", "millenaire:textures/entities/mayan/mayanPeasant1.png", "millenaire:textures/entities/mayan/mayanPeasant2.png", "millenaire:textures/entities/mayan/mayanPeasant3.png"}, false, true, 0),
				new VillagerType("mayanPeasantWife", "Atan", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanPeasantWife0.png", "millenaire:textures/entities/mayan/mayanPeasantWife1.png", "millenaire:textures/entities/mayan/mayanPeasantWife2.png", "millenaire:textures/entities/mayan/mayanPeasantWife3.png", "millenaire:textures/entities/mayan/mayanPeasantWife4.png", "millenaire:textures/entities/mayan/mayanPeasantWife5.png", "millenaire:textures/entities/mayan/mayanPeasantWife6.png"}, false, false, 0),
				new VillagerType("mayanSculptor", "Uxul", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanSculptor0.png"}, false, false, 0),
				new VillagerType("mayanShaman", "Aj K'in", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanShaman0.png", "millenaire:textures/entities/mayan/mayanShaman1.png"}, false, false, 0),
				new VillagerType("mayanShamanWife", "Aj", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanRichWoman0.png", "millenaire:textures/entities/mayan/mayanRichWoman1.png", "millenaire:textures/entities/mayan/mayanRichWoman2.png", "millenaire:textures/entities/mayan/mayanRichWoman3.png"}, false, false, 0),
				new VillagerType("mayanLoneShaman", "Aj K'in", 0, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanShaman0.png", "millenaire:textures/entities/mayan/mayanShaman1.png"}, false, false, 0),
				new VillagerType("mayanLoneShamanWife", "Aj", 1, mayanCulture.nameLists.get("highCasteFamilyNames"), mayanCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanRichWoman0.png", "millenaire:textures/entities/mayan/mayanRichWoman1.png", "millenaire:textures/entities/mayan/mayanRichWoman2.png", "millenaire:textures/entities/mayan/mayanRichWoman3.png"}, false, false, 0),
				new VillagerType("mayanWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanWarrior0.png", "millenaire:textures/entities/mayan/mayanWarrior1.png", "millenaire:textures/entities/mayan/mayanWarrior2.png"}, false, false, 32),
				new VillagerType("mayanFallenKing", "Keban Ajaw", 0, new String[]{"K'u Ix"}, new String[]{"Ixtab"}, new String[]{"millenaire:textures/entities/mayan/mayanKing0.png"}, false, false, 0),
				new VillagerType("mayanArmyEliteWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanWarrior0.png", "millenaire:textures/entities/mayan/mayanWarrior1.png", "millenaire:textures/entities/mayan/mayanWarrior2.png"}, false, false, 0),
				new VillagerType("mayanArmyLieutenant", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanWarrior0.png", "millenaire:textures/entities/mayan/mayanWarrior1.png", "millenaire:textures/entities/mayan/mayanWarrior2.png"}, false, false, 0),
				new VillagerType("mayanArmyScout", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanWarrior0.png", "millenaire:textures/entities/mayan/mayanWarrior1.png", "millenaire:textures/entities/mayan/mayanWarrior2.png"}, false, false, 0),
				new VillagerType("mayanArmyWarrior", "Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanWarrior0.png", "millenaire:textures/entities/mayan/mayanWarrior1.png", "millenaire:textures/entities/mayan/mayanWarrior2.png"}, false, false, 0),
				new VillagerType("mayanQuestShaman", "Aj K'in", 0, new String[]{"Uchben"}, new String[]{"Tohil"}, new String[]{"millenaire:textures/entities/mayan/mayanShaman0.png", "millenaire:textures/entities/mayan/mayanShaman1.png"}, false, false, 0),
				new VillagerType("mayanBanditMale", "K'as Mijin", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanBanditMale0.png", "millenaire:textures/entities/mayan/mayanBanditMale1.png"}, false, false, 0),
				new VillagerType("mayanBanditFemale", "K'as Aj", 1, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/mayan/mayanBanditFemale0.png", "millenaire:textures/entities/mayan/mayanBanditFemale1.png"}, false, false, 0),
				new VillagerType("mayanBanditWarrior", "K'as Kanan", 0, mayanCulture.nameLists.get("lowCasteFamilyNames"), mayanCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/mayan/mayanBanditWarrior0.png", "millenaire:textures/entities/mayan/mayanBanditWarrior1.png", "millenaire:textures/entities/mayan/mayanBanditWarrior2.png"}, false, false, 0)
		});
		
		//Japanese Initialization
		japaneseCulture = new MillCulture("japanese").addNameList("highCasteFamilyNames", new String[]{"Minamoto", "Fujiwara", "Taira", "Shikibu", "Ki", "Ariwara", "Tachibana", "Soga"})
				.addNameList("lowCasteFamilyNames", new String[]{"Date", "Sakuma", "Sato", "Suzuki", "Takahashi", "Watanabe", "Nakamura", "Kobayashi", "Yoshida", "Mori", "Hattori", "Mako", "Hashiba", "Sasaki", "Ito", "Kudo", "Kimura", "Narita", "Chiba", "Kikuchi", "Endo", "Arai", "Yamamoto", "Yamada", "Fukazawa", "Mochizuki", "Kato", "Nishimura", "Maeda", "Tanaka", "Fujii", "Ochi", "Oonishi", "Yamashita", "Hamada", "Komatsu", "Inoue", "Ono", "Goto", "Abe", "Sakamoto", "Matsumoto", "Kai", "Kuroki", "Kawano", "Hidaka", "Arakaki", "Miyagi", "Oshiro", "Higa", "Murakami", "Yamaguchi", "Kinjo", "Hideyoshi"})
				.addNameList("highCasteFemaleNames", new String[]{"Takako", "Murasaki", "Izumi", "Komachi"})
				.addNameList("lowCasteFemaleNames", new String[]{"Akane", "Ami", "Asuka", "Aya", "Ayano", "Hina", "Kana", "Mai", "Mayu", "Miki", "Misaki", "Miyu", "Mizuki", "Nana", "Nanami", "Natsumi", "Reina", "Riko", "Rin", "Saika", "Saki", "Sakura", "Yui", "Yuukama"})
				.addNameList("maleNames", new String[]{"Akira", "Eiichi", "Entarou", "Gaku", "Gojirou", "Hachitarou", "Hajime", "Haruki", "Hideki", "Hiro", "Hitoshi", "Ichirou", "Itsuo", "Jin", "Kenichi", "Kentaro", "Mineo", "Mitsuru", "Noato", "Osamu", "Reijiro", "Renzo", "Saburo", "Shingo", "Shinjiro", "Shinya", "Shouji", "Tadashi", "Takuji", "Tai", "Toyotomi", "Tsuyoshi", "Yuichiro", "Yuijiro"});
		
		japaneseCulture.setVillagerTypes(new VillagerType[]{
				new VillagerType("japaneseBoy", "Danshi", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBoy0.png", "millenaire:textures/entities/japanese/japaneseBoy1.png"}, false, false, 0),
				new VillagerType("japaneseGirl", "Jou", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseGirl0.png", "millenaire:textures/entities/japanese/japaneseGirl1.png"}, false, false, 0),
				new VillagerType("japaneseBrewer", "Touji", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBrewer0.png"}, true, false, 0),
				new VillagerType("japaneseFarmerChief", "Chokan", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasant1.png"}, true, false, 0),
				new VillagerType("japaneseKuge", "Kokushi", 0, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseOverseer0.png", "millenaire:textures/entities/japanese/japaneseOverseer1.png"}, true, false, 0),
				new VillagerType("japaneseSamuraiGeneral", "Shukun", 0, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseWarriorMaster0.png"}, true, false, 0),
				new VillagerType("japaneseBrewerWife", "Kuramoto", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBrewerWoman0.png"}, false, false, 0),
				new VillagerType("japaneseBraveWoman", "Jojoufu", 1, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBraveWoman0.png", "millenaire:textures/entities/japanese/japaneseBraveWoman1.png"}, false, false, 32),
				new VillagerType("japaneseRichWoman", "Reifujin", 1, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("highCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseLadyWoman0.png", "millenaire:textures/entities/japanese/japaneseLadyWoman1.png"}, false, false, 0),
				new VillagerType("japaneseFarmerWife", "Tsuma", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasantWife0.png", "millenaire:textures/entities/japanese/japanesePeasantWife1.png", "millenaire:textures/entities/japanese/japanesePeasantWife2.png", "millenaire:textures/entities/japanese/japanesePeasantWife3.png"}, false, false, 0),
				new VillagerType("japaneseArmySmith", "Kaji", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseArmySmith0.png"}, false, false, 0),
				new VillagerType("japaneseChickenFarmer", "Youkeika", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasant0.png"}, false, false, 0),
				new VillagerType("japaneseCrafter", "Saikushi", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseCrafter0.png"}, false, false, 0),
				new VillagerType("japaneseFarmer", "Hyakushou", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseFarmer0.png"}, false, false, 0),
				new VillagerType("japaneseFisherman", "Ryoushi", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseFisherman0.png"}, false, false, 0),
				new VillagerType("japaneseInnkeeper", "Teishu", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBrewer0.png"}, false, false, 0),
				new VillagerType("japaneseInnkeeperWife", "Tsuma", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBrewerWoman0.png"}, false, false, 0),
				new VillagerType("japaneseInnkeeperServant", "Gejo", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasantWife0.png", "millenaire:textures/entities/japanese/japanesePeasantWife1.png"}, false, false, 0),
				new VillagerType("japaneseLumberman", "Kikori", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseLumberman0.png"}, false, false, 16),
				new VillagerType("japaneseMiner", "Koufu", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseMiner0.png"}, false, false, 0),
				new VillagerType("japaneseLoneMiner", "Koufu", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseMiner0.png"}, false, false, 0),
				new VillagerType("japaneseMerchant", "Chounin", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseMerchant0.png", "millenaire:textures/entities/japanese/japaneseMerchant1.png"}, false, false, 0),
				new VillagerType("japaneseFoodMerchant", "Shokuryo no ShÅ�nin", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasantWife0.png", "millenaire:textures/entities/japanese/japanesePeasantWife1.png"}, false, false, 0),
				new VillagerType("japaneseBlacksmithMerchant", "Kajiya no ShÅ�nin", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseCrafter0.png"}, false, false, 0),
				new VillagerType("japaneseMonk", "Souryo", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseMonk0.png"}, false, false, 0),
				new VillagerType("japanesePeasant", "Noufu", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasant0.png", "millenaire:textures/entities/japanese/japanesePeasant1.png"}, false, true, 0),
				new VillagerType("japanesePeasantWife", "Tsuma", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasantWife0.png", "millenaire:textures/entities/japanese/japanesePeasantWife1.png", "millenaire:textures/entities/japanese/japanesePeasantWife2.png", "millenaire:textures/entities/japanese/japanesePeasantWife3.png"}, false, false, 0),
				new VillagerType("japanesePainter", "Choukou", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseSculptor0.png"}, false, false, 0),
				new VillagerType("japaneseBlacksmith", "Kajiya", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseSmith0.png"}, false, false, 0),
				new VillagerType("japaneseSamurai", "Keibou", 0, japaneseCulture.nameLists.get("highCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseWarrior0.png", "millenaire:textures/entities/japanese/japaneseWarrior1.png"}, true, false, 32),
				new VillagerType("japaneseFemaleServant", "Gejo", 1, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("lowCasteFemaleNames"), new String[]{"millenaire:textures/entities/japanese/japanesePeasantWife0.png", "millenaire:textures/entities/japanese/japanesePeasantWife1.png"}, false, false, 0),
				new VillagerType("japaneseBandit", "Hito", 0, japaneseCulture.nameLists.get("lowCasteFamilyNames"), japaneseCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/japanese/japaneseBandit0.png"}, false, false, 0)
		});
		
		//Byzantine Initialization
		byzantineCulture = new MillCulture("byzantine").addNameList("familyNames", new String[]{"Philoponus", "Monachos", "Kinnamos", "Moschopoulos", "Kraikos", "Xenos", "Galanis", "Kandake", "Peleus", "Achilles", "Herodias", "Helios", "Amethea", "Demeter", "Eileithyia", "Eudoxia", "Sophronia", "Ligeia", "Pantagiota", "Rhea"})
				.addNameList("maleNames", new String[]{"Georgios", "Leo", "Nikephoros", "Eutocius", "Demetrius", "Philipos", "Sokrates", "Platon", "Alexandros", "Lisias", "Ilias", "Ikaros", "Thisseas", "Odysseus", "Egeas", "Iassonas", "Achilleas", "Menelaos", "Ioannis", "Iljios", "Jannis", "Demostenes", "Krateos", "Amphion"})
				.addNameList("femaleNames", new String[]{"Daphne", "Danae", "Medea", "Helena", "Elena", "Nephele", "Euphoria", "Ariadne", "Alkmene", "Eurydike", "Olympia", "Kassandra", "Athina", "Artemis", "Artemisisa", "Hestia", "Estia", "Antigone", "Alexandra", "Thalia", "Niki", "Nike", "Niobe", "Efgenia", "Ifigenia", "Ismene", "Xenia"});
		
		byzantineCulture.setVillagerTypes(new VillagerType[]{
				new VillagerType("byzantineBoy", "Neos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineBoy0.png"}, false, false, 0),
				new VillagerType("byzantineGirl", "Kore", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineGirl0.png"}, false, false, 0),
				new VillagerType("byzantineBaron", "Akrita", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineBaron0", "millenaire:textures/entities/byzantine/byzantineBaron1.png"}, true, false, 0),
				new VillagerType("byzantineCenturio", "Kentarios", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineCentaurio0.png", "millenaire:textures/entities/byzantine/byzantineCentaurio1.png"}, true, false, 0),
				new VillagerType("byzantinePatriarch", "Patriarches", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantinePatriarch0.png"}, true, false, 0),
				new VillagerType("byzantineRichWife", "Plousia Gynaika", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineRichWife0.png"}, false, false, 0),
				new VillagerType("byzantineArchitect", "Architekton", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineMiner0.png", "millenaire:textures/entities/byzantine/byzantineMiner1.png"}, false, true, 0),
				new VillagerType("byzantineArmySmith", "Sideras", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineSmith0.png", "millenaire:textures/entities/byzantine/byzantineSmith1.png"}, false, false, 0),
				new VillagerType("byzantineArtisan", "Technites", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineArtisan0.png"}, false, false, 0),
				new VillagerType("byzantineArtisanWife", "Technites Gynaika", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineArtisanWife0.png"}, false, false, 0),
				new VillagerType("byzantineFarmiller", "Agrotes", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFarmiller0.png", "millenaire:textures/entities/byzantine/byzantineFarmiller1.png"}, false, false, 0),
				new VillagerType("byzantineFarmillerWife", "Agrota", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineWife0.png", "millenaire:textures/entities/byzantine/byzantineWife1.png", "millenaire:textures/entities/byzantine/byzantineWife2.png"}, false, false, 0),
				new VillagerType("byzantineFisherman", "Psaras", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFarmer0.png", "millenaire:textures/entities/byzantine/byzantineFarmer1.png"}, false, false, 0),
				new VillagerType("byzantineWatchKeeper", "Kidemonas", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineKeeper0.png", "millenaire:textures/entities/byzantine/byzantineKeeper1.png", "millenaire:textures/entities/byzantine/byzantineKeeper2.png", "millenaire:textures/entities/byzantine/byzantineKeeper3.png"}, false, false, 32),
				new VillagerType("byzantineLighthouseKeeper", "Pharophylakas", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFarmiller0.png", "millenaire:textures/entities/byzantine/byzantineFarmiller1.png"}, false, false, 0),
				new VillagerType("byzantineLumberman", "Xylokopos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineLumberman0.png", "millenaire:textures/entities/byzantine/byzantineLumberman1.png"}, false, false, 16),
				new VillagerType("byzantineMerchant", "Emporos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineMerchant0.png"}, false, false, 0),
				new VillagerType("byzantineFoodMerchant", "Epicheirimatias Trophi", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFarmer0.png"}, false, false, 0),
				new VillagerType("byzantineMaterialMerchant", "Epicheirimatias Yliko", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineArtisan0.png"}, false, false, 0),
				new VillagerType("byzantineMiner", "Metallorychos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineMiner0.png", "millenaire:textures/entities/byzantine/byzantineMiner1.png", "millenaire:textures/entities/byzantine/byzantineMiner2.png"}, false, false, 0),
				new VillagerType("byzantinePriest", "Pastoras", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantinePapas0.png", "millenaire:textures/entities/byzantine/byzantinePapas1.png", "millenaire:textures/entities/byzantine/byzantinePapas2.png"}, false, false, 0),
				new VillagerType("byzantineShepherd", "Boskos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineShepherd0.png"}, false, false, 0),
				new VillagerType("byzantineShepherdWife", "Boskopoula", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineShepherdWife0.png"}, false, false, 0),
				new VillagerType("byzantineLoneShepherd", "Boskos", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineShepherd0.png"}, false, false, 0),
				new VillagerType("byzantineLoneShepherdWife", "Boskopoula", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineShepherdWife0.png"}, false, false, 0),
				new VillagerType("byzantineSilkFarmer", "Kalliergites", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFarmer0.png", "millenaire:textures/entities/byzantine/byzantineFarmer1.png"}, false, false, 0),
				new VillagerType("byzantineSilkFarmerWife", "Demiourgo", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineFrau0.png", "millenaire:textures/entities/byzantine/byzantineFrau1.png", "millenaire:textures/entities/byzantine/byzantineFrau2.png", "millenaire:textures/entities/byzantine/byzantineFrau3.png", "millenaire:textures/entities/byzantine/byzantineFrau4.png"}, false, false, 0),
				new VillagerType("byzantineSmith", "Sideras", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineSmith0.png", "millenaire:textures/entities/byzantine/byzantineSmith1.png"}, false, false, 0),
				new VillagerType("byzantineSoldier", "Stratiotes", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineSoldier0.png"}, false, false, 32),
				new VillagerType("byzantinePlayerSoldier", "Stratiotes", 0, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("maleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineSoldier0.png"}, false, false, 16),
				new VillagerType("byzantineWife", "Gynaika", 1, byzantineCulture.nameLists.get("familyNames"), byzantineCulture.nameLists.get("femaleNames"), new String[]{"millenaire:textures/entities/byzantine/byzantineWife0.png", "millenaire:textures/entities/byzantine/byzantineWife1.png", "millenaire:textures/entities/byzantine/byzantineWife2.png"}, false, false, 0)
		});
		/*
		final VillageTypes types = new VillageTypes(new VillageType[] {
			new VillageType("test1").setTier(1,
				new BuildingProject[] {
					new BuildingProject("grove2", 0),
					new BuildingProject("mine1", 0),
					new BuildingProject("house1", 0),
					new BuildingProject("house2", 1)
				}).setTier(2,
					new BuildingProject[] {
						new BuildingProject("mine1", 2),
						new BuildingProject("house1", 2)
				}).setStartingBuildings(new BuildingProject[] {new BuildingProject("townhall1", 0)}),//.setBuildingTypes(new String[]{"primary1", "primary2"}, new String[]{"secondary1", "secondary2"}, new String[]{"player1"}).setStartingBuildings(new String[] {"townhall1", "grove1", "mine1"}),
			new VillageType("test2").setTier(1,
				new BuildingProject[] {
					new BuildingProject("grove1", 0),
					new BuildingProject("mine1", 1),
					new BuildingProject("house1", 1),
					new BuildingProject("house2", 0)
				}).setTier(2,
					new BuildingProject[] {
						new BuildingProject("mine1", 3),
						new BuildingProject("house1", 2)
				}).setStartingBuildings(new BuildingProject[] {new BuildingProject("townhall2", 0)})//.setBuildingTypes(new String[]{"primary1", "primary2"}, new String[]{"secondary1", "secondary2"}, new String[]{"player1", "player2"}).setStartingBuildings(new String[] {"townhall2", "grove1", "grove2", "mine1"})
		});
		*/
		//normanCulture.exportVillages(types);
		
		normanCulture.loadVillageTypes();
	}

	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	public static class VillageType 
	{
		public String id;

		//public String[] primaryBuildings;
		//public String[] secondaryBuildings;
		//public String[] playerBuildings;
		
		private Map<Integer, BuildingProject[]> tiers = new HashMap<>();
		
		//First Building in this array should always be the TownHall
		public BuildingProject[] startingBuildings;
		
		public VillageType() {}
		
		public VillageType(String idIn) { id = idIn; }
		
		public VillageType setTier(int tier, BuildingProject[] buildings) {
			tiers.put(tier, buildings);
			return this;
		}
		
		public VillageType setBuildingTypes(String[] primaryIn, String[] secondaryIn, String[] playerIn)
		{
			//this.primaryBuildings = primaryIn;
			//this.secondaryBuildings = secondaryIn;
			//this.playerBuildings = playerIn;

			return this;
		}
		
		public VillageType setID(String id) {
			this.id = id;
			return this;
		}

		public VillageType setStartingBuildings(BuildingProject[] startIn)
		{
			this.startingBuildings = startIn;
			return this;
		}
		
		public String getVillageName() { return id; }
	}
}
