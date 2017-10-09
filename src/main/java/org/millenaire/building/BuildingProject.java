package org.millenaire.building;

import java.util.ArrayList;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import org.millenaire.MillCulture;

public class BuildingProject
{
	/**
	 * Building's internal name
	 */
	public String ID;
	/**
	 * Pretty self-explainatory
	 */
	public int lvl;
	
	/**
	 * This is just here to make the JSON parser work correctly. USE OTHER CONSTRUCTOR!
	 */
	public BuildingProject() {
		
	}
	
	/**
	 * The best constructor
	 */
	public BuildingProject(String buildingID, int level) {
		this.ID = buildingID;
		this.lvl = level;
	}
}
