package org.millenaire.village;

import java.util.Random;
import java.util.UUID;

import org.millenaire.MillCulture;
import org.millenaire.MillCulture.VillageType;
import org.millenaire.VillageGeography;
import org.millenaire.VillageTracker;
import org.millenaire.building.BuildingLocation;
import org.millenaire.building.BuildingPlan;
import org.millenaire.building.BuildingProject;
import org.millenaire.building.PlanIO;
import org.millenaire.entities.EntityMillVillager;
import org.millenaire.pathing.MillPathNavigate;
import org.millenaire.util.ResourceLocationUtil;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Village {

	private UUID uuid;
	private BlockPos mainBlock;
	private VillageGeography geo;
	private VillageType type;
	private MillCulture culture;
	private World world;
	private BuildingLocation[] buildings;
	
	private Village(BlockPos b, World worldIn, VillageType typeIn, MillCulture cultureIn) {
		this.setPos(b);
		this.uuid = UUID.randomUUID();
		this.geo = new VillageGeography();
		this.world = worldIn;
		this.type = typeIn;
		this.culture = cultureIn;
	}
	
	/**
	 * FOR USE BY VILLAGE TRACKER ONLY
	 */
	@Deprecated()
	public Village() {}
	
	/**
	 * FOR USE BY VILLAGE TRACKER ONLY
	 */
	@Deprecated
	public void setPos(BlockPos pos) { mainBlock = pos; }
	
	public VillageType getType() { return type; }

	public UUID getUUID() { return uuid; }
	
	public BlockPos getPos() { return mainBlock; }
	
	public boolean setupVillage() {
		try {
			for(BuildingProject proj : type.startingBuildings) {
				BuildingPlan p = PlanIO.loadSchematic(PlanIO.getBuildingTag(ResourceLocationUtil.getRL(proj.ID).getResourcePath(), culture, true), culture, proj.lvl);
				
				EntityMillVillager v = new EntityMillVillager(world, 1, culture);
				world.spawnEntityInWorld(v);
				
				BuildingLocation loc = p.findBuildingLocation(geo, new MillPathNavigate(v, world), mainBlock, 80, new Random(), p.buildingOrientation);
				PlanIO.placeBuilding(p, loc, world);
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Village createVillage(BlockPos VSPos, World world, VillageType typeIn, MillCulture cultureIn) {
		Village v = new Village(VSPos, world, typeIn, cultureIn);
		VillageTracker.get(world).registerVillage(v.getUUID(), v);;
		return v; 
	}
}