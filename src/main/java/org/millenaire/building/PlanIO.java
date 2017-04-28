package org.millenaire.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.millenaire.MillCulture;
import org.millenaire.Millenaire;
import org.millenaire.VillagerType;
import org.millenaire.networking.PacketSayTranslatedMessage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class PlanIO {

	//IBlockState[y][z][x]
	public static void exportBuilding(EntityPlayer player, World world, BlockPos startPoint) {
		try {
			TileEntitySign sign = (TileEntitySign)world.getTileEntity(startPoint);

			String buildingName = sign.signText[0].getUnformattedText();
			boolean saveSnow = (sign.signText[3].getUnformattedText().toLowerCase() == "snow");

			int buildingLevel = 0;
			
			if(sign.signText[1] != null && sign.signText[1].getUnformattedText().length() > 0) {
				buildingLevel = Integer.parseInt(sign.signText[1].getUnformattedText());
			}
			
			int startLevel = -1;

			if (sign.signText[2] != null && sign.signText[2].getUnformattedText().length() > 0) {
				startLevel = Integer.parseInt(sign.signText[2].getUnformattedText());
			}

			if(buildingName == null || buildingName.length() == 0) {
				player.addChatComponentMessage(new ChatComponentTranslation("message.error.exporting.noname"));
			}
			boolean foundEnd = false;
			int xEnd = startPoint.getX() + 1;
			while(!foundEnd && xEnd < startPoint.getX() + 257) {
				final IBlockState block = world.getBlockState(new BlockPos(xEnd, startPoint.getY(), startPoint.getZ()));

				if (block.getBlock() == Blocks.standing_sign) {
					foundEnd = true;
					break;
				}
				xEnd++;
			}
			if(!foundEnd) {
				player.addChatComponentMessage(new ChatComponentTranslation("message.error.exporting.xaxis"));
			}
			foundEnd = false;
			int zEnd = startPoint.getZ() + 1;
			while(!foundEnd && zEnd < startPoint.getZ() + 257) {
				final IBlockState block = world.getBlockState(new BlockPos(startPoint.getX(), startPoint.getY(), zEnd));

				if (block.getBlock() == Blocks.standing_sign) {
					foundEnd = true;
					break;
				}
				zEnd++;
			}
			if(!foundEnd) {
				player.addChatComponentMessage(new ChatComponentTranslation("message.error.exporting.zaxis"));
			}
			final int width = xEnd - startPoint.getX() - 1;
			final int length = zEnd - startPoint.getZ() - 1;

			boolean stop = false;
			int y = 0;
			
			final Map<Integer, IBlockState[][]> ex = new HashMap<Integer, IBlockState[][]>();
			
			while(!stop) {
				
				IBlockState[][] level = new IBlockState[width][length];
				
				boolean blockFound = false;
				
				for (int x = 0; x < width; x++) {
					for (int z = 0; z < length; z++) {
						IBlockState block = world.getBlockState(new BlockPos(x + startPoint.getX() + 1, y + startPoint.getY() + startLevel, z + startPoint.getZ() + 1));
						
						if(block.getBlock() != Blocks.air) {
							blockFound = true;
						}
						if(saveSnow || block.getBlock() != Blocks.snow) {
							level[x][z] = block;
						}
						else {
							level[x][z] = Blocks.air.getDefaultState();
						}
					}
				}
				
				if (blockFound) {
					ex.put(y, level);
				} else {
					stop = true;
				}

				y++;

				if (y + startPoint.getY() + startLevel >= 256) {
					stop = true;
				}
			}
			
			IBlockState[][][] ex2 = new IBlockState[ex.size()][length][width];
			
			for(int i = 0; i < ex.size(); i++) {
				IBlockState[][] level = ex.get(i);
				for(int x = 0; x < width; x++) {
					for(int z = 0; z < length; z++) {
						ex2[i][z][x] = level[x][z];
					} 
				}
			}
			
			exportToSchem(ex2, (short)width, (short)ex.size(), (short)length, (short)startLevel, buildingName, buildingLevel, new VillagerType[] {MillCulture.normanCulture.getVillagerType("normanMiner")}, new VillagerType[] {MillCulture.normanCulture.getVillagerType("normanLady")});
		}
		catch(Exception e) {
			System.out.println("ERROR! FIX ME NOW!");
			e.printStackTrace();
			player.addChatComponentMessage(new ChatComponentTranslation("message.error.exporting.unknown"));
		}
	}
	
	//Called only on the logical server
	public static void importBuilding(EntityPlayer player, BlockPos startPos) {
		try {
			TileEntitySign te = (TileEntitySign)player.getEntityWorld().getTileEntity(startPos);
			String name = te.signText[0].getUnformattedText();
			int level = 0;
			if(te.signText[1] != null && te.signText[1].getUnformattedText().length() > 0) {
				level = Integer.parseInt(te.signText[1].getUnformattedText());
			}
			
			if(name == null || name.length() == 0) {
				PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.exporting.noname");
				Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
			}
			
			World world = MinecraftServer.getServer().getEntityWorld();
			
			File schem = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + "//exports//" + name + "_A" + level + ".mlplan");
			if(!schem.exists()) {
				PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.importing.nofile");
				Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
			}
			FileInputStream fis = new FileInputStream(schem);
			
			BuildingPlan plan = loadSchematic(fis, MillCulture.millDefault, level);
			IBlockState[][][] blocks = plan.buildingArray;
			
			for(int x = 0; x < plan.width; x++) {
				for(int y = 0; y < plan.height; y++) {
					for(int z = 0; z < plan.length; z++) {
						System.out.println("setting block" + (x + startPos.getX()) +", " + (y + startPos.getY() + plan.depth) + ", " + (z + startPos.getZ()) + ":" + blocks[y][z][x]);
						world.setBlockState(new BlockPos(x + startPos.getX() + 1, y + startPos.getY() + plan.depth, z + startPos.getZ() + 1), blocks[y][z][x], 2);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.importing.unknown");
			Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
		}
	}

	public static BuildingPlan loadSchematic(InputStream is, MillCulture culture, int level) throws IOException {
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
			states[i] = Block.getBlockById(blocks[i]).getStateFromMeta(data[i]);
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

		return new BuildingPlan(culture, level).setNameAndType(name, MaleVillagers, new String[0]).setLengthWidth(length, width)
				.setHeightDepth(height, depth).setDistance(0, 1).setOrientation(EnumFacing.getHorizontal(2)).setPlan(organized);
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
	public static File exportToSchem(IBlockState[][][] blocks, short width, short height, short length, short depth, String name, int level, VillagerType[] maleVillagers, VillagerType[] femaleVillagers) {
		File f = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + "\\exports\\");
		if(!f.exists()) {
			f.mkdirs();
		}
		
		File f1 = new File(f, name + "_A" + level + ".mlplan");

		NBTTagCompound tag = new NBTTagCompound();

		byte[] blockids = new byte[width*height*length], data = new byte[width*height*length];

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					blockids[(y*length+z)*width+x] = (byte)Block.getIdFromBlock(blocks[y][z][x].getBlock());
					data[(y*length+z)*width+x] = (byte)blocks[y][z][x].getBlock().getMetaFromState(blocks[y][z][x]);
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
				MaleList.appendTag(tag2);
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
				FemaleList.appendTag(tag2);
			}
		}

		tag.setTag("MaleVillagers", MaleList);
		tag.setTag("FemaleVillagers", FemaleList);

		try {
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(f1));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return f;
	}
}