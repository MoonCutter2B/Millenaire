package org.millenaire.building;

import java.io.IOException;
import java.io.InputStream;

import org.millenaire.MillCulture;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class PlanLoader {

	public PlanLoader() {

	}

	public void preinitialize() {

	}

	public BuildingPlan loadSchematic(InputStream is, MillCulture culture, int level) throws IOException {
		//Convert Stream to NBTTagCompound
		NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);

		//width = x-axis, height = y-axis, length = z-axis
		short width, height, length;
		byte[] blocks;
		byte[] data;

		width = nbt.getShort("Width");
		height = nbt.getShort("Height");
		length = nbt.getShort("Length"); 

		blocks = nbt.getByteArray("Blocks");
		data = nbt.getByteArray("Data");

		IBlockState[] states = new IBlockState[width-1*length-1*height-1];

		//turn block ids and data into blockstates.
		for(int i = 0; i < states.length; i++) {
			states[i] = Block.getBlockById(blocks[i]).getStateById(data[i]);
		}

		//turn into a 3D block array for use with BuildingPlan
		//in format [y][z][x]! IMPORTANT!
		IBlockState[][][] organized = new IBlockState[height-1][length-1][width-1];

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					organized[y][z][x] = states[(y*length+z)*width+x];
				}
			}
		}

		//load milleniare extra data
		short depth = nbt.getShort("startLevel");
		String name = nbt.getString("buildingName");
		byte[] maleVillagers = nbt.getByteArray("maleInhabitants");
		byte[] femaleVillagers = nbt.getByteArray("femaleInhabitants");
		
		String[] maleInhabitants = new String[maleVillagers.length], femaleInhabitants = new String[femaleVillagers.length];
		
		for(int i = 0; i < maleVillagers.length; i++) {
			maleInhabitants[i] = culture.getVillagerTypes()[maleVillagers[i]].id;
		}
		
		for(int i = 0; i < femaleVillagers.length; i++) {
			femaleInhabitants[i] = culture.getVillagerTypes()[femaleVillagers[i]].id;
		}

		return new BuildingPlan(culture, level).setNameAndType(name, maleInhabitants, femaleInhabitants).setLengthWidth(length, width).setHeightDepth(height, depth).setDistance(0, 1)
				.setOrientation(EnumFacing.getHorizontal(2)).setPlan(organized);
	}
}