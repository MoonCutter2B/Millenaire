package org.millenaire.village;

import java.util.UUID;

import org.millenaire.VillageGeography;

import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

public class Village {

	private UUID uuid;
	private VillageBuilding[] buildings;
	private BlockPos mainBlock;
	private VillageGeography geo;
	
	private Village(BlockPos b) {
		this.setPos(b);
		this.uuid = UUID.randomUUID();
		this.geo = new VillageGeography();
	}
	
	/**
	 * FOR USE BY VILLAGE TRACKER ONLY
	 */
	@Deprecated()
	public Village() {

	}
	
	/**
	 * FOR USE BY VILLAGE TRACKER ONLY
	 */
	@Deprecated
	public void setPos(BlockPos pos) { mainBlock = pos; }

	public UUID getUUID() { return uuid; }
	
	public BlockPos getPos() { return mainBlock; }
	
	@NotNull
	public static Village createVillage(BlockPos VSPos) { return new Village(VSPos); }
}