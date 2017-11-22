package org.millenaire.building;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.millenaire.util.ItemRateWrapper;
import org.millenaire.util.ResourceLocationUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class BuildingTypes {
	
	private Map<ResourceLocation, BuildingType> buildingCache = new HashMap<ResourceLocation, BuildingType>();

	public static void cacheBuildingTypes() {
		
		BuildingType type = new BuildingType(new ResourceLocation("egypt:house"));
		type.itemrates.add(new ItemRateWrapper(new ResourceLocation("minecraft:cobblestone"), 2, 0, 20000));
		type.itemrates.add(new ItemRateWrapper(new ResourceLocation("minecraft:log"), 2, 0, 10000));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(type));
		File f = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + File.separator + "millenaire" + File.separator + "exports" + File.separator);
		File f1 = new File(f, "house.json");
		try {
			f.mkdirs();
			f1.createNewFile();
			String g = gson.toJson(type);
			FileWriter fw = new FileWriter(f1);
			fw.write(g);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class BuildingType {
		
		protected String identifier;
		protected List<ItemRateWrapper> itemrates = new ArrayList<ItemRateWrapper>();
		
		public BuildingType() {}
		
		public BuildingType(ResourceLocation cultureandname) {
			identifier = ResourceLocationUtil.getString(cultureandname);
		}
		
		public BuildingPlan loadBuilding() {
			return null;
		}
	}
}
