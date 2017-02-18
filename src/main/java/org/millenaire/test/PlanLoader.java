package org.millenaire.test;

import java.io.IOException;
import java.io.InputStream;

import org.millenaire.MillCulture;
import org.millenaire.building.BuildingPlan;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PlanLoader {

	public PlanLoader() {
		
	}
	
	//TODO: Can't really think of a need for this, but why not...
	public void preinitialize() {
		
	}
	
	public BuildingPlan loadSchematic(InputStream is) throws IOException {
		//Convert Stream to NBTTagCompound
		NBTTagCompound tag = CompressedStreamTools.readCompressed(is);
		
		short width, height, length;
		byte[] blocks;
		byte[] data;
		
		width = tag.getShort("Width");
		height = tag.getShort("Height");
		length = tag.getShort("Length");
		
		blocks = tag.getByteArray("Blocks");
		data = tag.getByteArray("Data");
		
		IBlockState[] states = new IBlockState[width-1*length-1*height-1];
		
		//turn block ids and data into blockstates.
		for(int i = 0; i < states.length; i++) {
			states[i] = Block.getBlockById(blocks[i]).getStateById(data[i]);
		}
		
		//turn into a 3D block array for use with BuildingPlan
		IBlockState[][][] organized = new IBlockState[width-1][height-1][length-1];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					organized[x][y][z] = states[(y*length+z)*width+x];
				}
			}
		}
		//TODO: make culture customizeable
		return new BuildingPlan(MillCulture.millDefault, 0).setLengthWidth(length, width).setHeightDepth(height, 0).setDistance(0, 1)
				.setOrientation(EnumFacing.getHorizontal(2)).setPlan(organized);
	}
}
