package org.millenaire.building;

import java.util.ArrayList;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import org.millenaire.MillCulture;

public class BuildingProject
{
	public static BuildingPlan normanCommunauteA0;
	
	public static void preinitialize()
	{
		normanCommunauteA0 = new BuildingPlan(MillCulture.normanCulture, 0).setNameAndType("Communauté", new String[]{"normanGuildMaster"}, new String[0]).setLengthWidth(11, 13).setHeightDepth(13, -6).setArea(3).setDistance(0, 1).setOrientation(EnumFacing.getHorizontal(2)).setPlan(
				new IBlockState[][][]{{{}}});
	}
}
