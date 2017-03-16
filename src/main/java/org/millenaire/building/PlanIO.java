package org.millenaire.building;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.millenaire.MillCulture;
import org.millenaire.VillagerType;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class PlanIO {

	public PlanIO() {

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

		IBlockState[] states = new IBlockState[width*length*height];

		//turn block ids and data into blockstates.
		for(int i = 0; i < states.length; i++) {
			states[i] = Block.getBlockById(blocks[i]).getStateById(data[i]);
		}

		//turn into a 3D block array for use with BuildingPlan
		//in format [y][z][x]! IMPORTANT!
		IBlockState[][][] organized = new IBlockState[height][length][width];

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					organized[y][z][x] = states[(y*length+z)*width+x];
				}
			}
		}

		//load milleniare extra data
		short depth = nbt.getShort("StartLevel");
		String name = nbt.getString("BuildingName");

		NBTTagList MaleList = nbt.getTagList("MaleVillagers", Constants.NBT.TAG_COMPOUND);

		String[] MaleVillagers = new String[MaleList.tagCount()];

		for(int i = 0; i < MaleList.tagCount(); i++)
		{
			NBTTagCompound tag = MaleList.getCompoundTagAt(i);
			MaleVillagers[i] = tag.getString("" + i);
		}

		NBTTagList FemaleList = nbt.getTagList("FemaleVillagers", Constants.NBT.TAG_COMPOUND);

		String[] FemaleVillagers = new String[FemaleList.tagCount()];

		for(int i = 0; i < FemaleList.tagCount(); i++)
		{
			NBTTagCompound tag = FemaleList.getCompoundTagAt(i);
			MaleVillagers[i] = tag.getString("" + i);
		}

		return new BuildingPlan(culture, level).setNameAndType(name, MaleVillagers, new String[0]).setLengthWidth(length, width).setHeightDepth(height, depth).setDistance(0, 1)
				.setOrientation(EnumFacing.getHorizontal(2)).setPlan(organized);
	}

	/**
	 * Exports the IBlockState[y][z][x] to a file
	 * 
	 * @param blocks the blocks to export
	 * @param width the width (x-axis)
	 * @param height the height (y-axis)
	 * @param length the length (z-axis)
	 * @param depth the depth of the build
	 * @param name the name of the building
	 * @param maleVillagers the list of male villagers in the building
	 * @param femaleVillagers the list of female villagers in the building
	 * @return the file that is outputted to disk
	 */
	public File exportBuilding(IBlockState[][][] blocks, short width, short height, short length, short depth, String name, VillagerType[] maleVillagers, VillagerType[] femaleVillagers) {
		File f = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + "\\exports\\" + name + ".mlplan");
		if(!f.exists()) {
			f.mkdirs();
		}

		NBTTagCompound tag = new NBTTagCompound();

		byte[] blockids = new byte[width*height*length], data = new byte[width*height*length];

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					blockids[(y*length+z)*width+x] = (byte)Block.getIdFromBlock(blocks[y][z][x].getBlock());
					data[(y*length+z)*width+x] = (byte)blocks[x][y][z].getBlock().getMetaFromState(blocks[y][z][x]);
				}
			}
		}

		tag.setByteArray("Blocks", blockids);
		tag.setByteArray("Data", data);

		tag.setShort("Width", width);
		tag.setShort("Height", height);
		tag.setShort("Length", length);
		tag.setShort("StartLevel", depth);
		tag.setString("BuildingName", name);
		
		NBTTagList MaleList = new NBTTagList();
		for(int i = 0; i < maleVillagers.length; i++)
		{
			String s = maleVillagers[i].id;
			if(s != null)
			{
				NBTTagCompound tag2 = new NBTTagCompound();
				tag2.setString("" + i, s);
				MaleList.appendTag(tag);
			}
		}
		
		NBTTagList FemaleList = new NBTTagList();
		for(int i = 0; i < femaleVillagers.length; i++)
		{
			String s = femaleVillagers[i].id;
			if(s != null)
			{
				NBTTagCompound tag2 = new NBTTagCompound();
				tag2.setString("" + i, s);
				FemaleList.appendTag(tag);
			}
		}

		tag.setTag("MaleVillagers", MaleList);
		tag.setTag("FemaleVillagers", FemaleList);
		
		try {
			CompressedStreamTools.write(tag, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return f;
	}
}