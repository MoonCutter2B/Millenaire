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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class PlanIO {

	public static final String FILE_VERSION = "2";
	
	//IBlockState[y][z][x]
	public static void exportBuilding(EntityPlayer player, BlockPos startPoint) {
		try {
			TileEntitySign sign = (TileEntitySign)player.getEntityWorld().getTileEntity(startPoint);

			String buildingName = sign.signText[0].getUnformattedText();
			boolean saveSnow = (sign.signText[3].getUnformattedText().toLowerCase() == "snow");

			int buildingLevel = 1;

			if(sign.signText[1] != null && sign.signText[1].getUnformattedText().length() > 0) {
				buildingLevel = Integer.parseInt(sign.signText[1].getUnformattedText());
			}
			
			if(buildingLevel <= 0) {
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.exporting.level0");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
			}

			int startLevel = -1;

			if (sign.signText[2] != null && sign.signText[2].getUnformattedText().length() > 0) {
				startLevel = Integer.parseInt(sign.signText[2].getUnformattedText());
			}

			if(buildingName == null || buildingName.length() == 0) {
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.exporting.noname");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
				throw new Exception("Ahhh!");
			}
			boolean foundEnd = false;
			int xEnd = startPoint.getX() + 1;
			while(!foundEnd && xEnd < startPoint.getX() + 257) {
				final IBlockState block = player.getEntityWorld().getBlockState(new BlockPos(xEnd, startPoint.getY(), startPoint.getZ()));

				if (block.getBlock() == Blocks.standing_sign) {
					foundEnd = true;
					break;
				}
				xEnd++;
			}
			if(!foundEnd) {
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.exporting.xaxis");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
				throw new Exception("Ahhh!");
			}
			foundEnd = false;
			int zEnd = startPoint.getZ() + 1;
			while(!foundEnd && zEnd < startPoint.getZ() + 257) {
				final IBlockState block = player.getEntityWorld().getBlockState(new BlockPos(startPoint.getX(), startPoint.getY(), zEnd));

				if (block.getBlock() == Blocks.standing_sign) {
					foundEnd = true;
					break;
				}
				zEnd++;
			}
			if(!foundEnd) {
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.exporting.zaxis");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
				throw new Exception("Ahhh!");
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
						IBlockState block = player.getEntityWorld().getBlockState(new BlockPos(x + startPoint.getX() + 1, y + startPoint.getY() + startLevel, z + startPoint.getZ() + 1));

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

			exportToSchem(ex2, (short)width, (short)ex.size(), (short)length, (short)startLevel, buildingName, buildingLevel, new VillagerType[] {MillCulture.normanCulture.getVillagerType("normanMiner")}, new VillagerType[] {MillCulture.normanCulture.getVillagerType("normanLady")}, player);
		}
		catch(Exception e) {
			e.printStackTrace();
			PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.unknown");
			Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
			PacketSayTranslatedMessage packet2 = new PacketSayTranslatedMessage("message.notcompleted");
			Millenaire.simpleNetworkWrapper.sendTo(packet2, (EntityPlayerMP)player);
		}
	}

	//Called only on the logical server
	public static void importBuilding(EntityPlayer player, BlockPos startPos) {
		try {
			TileEntitySign te = (TileEntitySign)player.getEntityWorld().getTileEntity(startPos);
			String name = te.signText[0].getUnformattedText();
			int level = 1;
			if(te.signText[1] != null && te.signText[1].getUnformattedText().length() > 0) {
				level = Integer.parseInt(te.signText[1].getUnformattedText());
			}

			if(name == null || name.length() == 0) {
				PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.exporting.noname");
				Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.notcompleted");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
				return;
			}

			World world = MinecraftServer.getServer().getEntityWorld();

			File schem = getBuildingFile(name);
			if(!schem.exists()) {
				PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.importing.nofile");
				Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
				PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.notcompleted");
				Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
				return;
			}
			FileInputStream fis = new FileInputStream(schem);

			BuildingPlan plan = loadSchematic(fis, MillCulture.millDefault, level);
			IBlockState[][][] blocks = plan.buildingArray;

			for(int x = 0; x < plan.width; x++) {
				for(int y = 0; y < plan.height; y++) {
					for(int z = 0; z < plan.length; z++) {
						world.setBlockState(new BlockPos(x + startPos.getX() + 1, y + startPos.getY() + plan.depth, z + startPos.getZ() + 1), blocks[y][z][x], 2);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			PacketSayTranslatedMessage message = new PacketSayTranslatedMessage("message.error.unknown");
			Millenaire.simpleNetworkWrapper.sendTo(message, (EntityPlayerMP)player);
		}
	}

	public static BuildingPlan loadSchematic(InputStream is, MillCulture culture, int level) throws IOException {
		//Convert Stream to NBTTagCompound
		NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);

		//width = x-axis, height = y-axis, length = z-axis
		short width, height, length;
		int[] blocks = {};
		int[] data = {};
		
		String version = nbt.getString("Version");

		width = nbt.getShort("Width");
		height = nbt.getShort("Height");
		length = nbt.getShort("Length"); 

		//blocks = nbt.getByteArray("Blocks");
		NBTTagList list = nbt.getTagList("level_" + level, Constants.NBT.TAG_COMPOUND);
//		if(version == "0.2") {
//			byte[] blockArray = list.getCompoundTagAt(0).getByteArray("Blocks");
//			byte[] dataArray = list.getCompoundTagAt(0).getByteArray("Data");
//			
//			blocks = new int[blockArray.length];
//			data = new int[dataArray.length];
//			
//			for(int x = 0; x <= blockArray.length; x++) {
//				blocks[x] = (int)blockArray[x];
//				data[x] = (int)dataArray[x];
//			}
//		}
//		else if(version == "2") {
			String blockdata = list.getCompoundTagAt(0).getString("BlockData");
			System.out.println(blockdata);
			String[] split = blockdata.split(";");
			blocks = new int[split.length];
			data = new int[split.length];
			for(int i = 0; i <= split.length -1; i++) {
				String s = split[i];
				String[] s1 = s.split(":");
				System.out.println(s);
				blocks[i] = Integer.parseInt(s1[0]);
				data[i] = Integer.parseInt(s1[1]);
			}
//		}
//		else {
//			System.out.println("Argg!");
//			return null;
//		}
		

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

	public static NBTTagCompound getBuildingTag(final String name) {
		try {
			File f1 = getBuildingFile(name);
			if(!f1.exists()) 
				return new NBTTagCompound();
			else {
				FileInputStream fis = new FileInputStream(f1);
				return CompressedStreamTools.readCompressed(fis);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return new NBTTagCompound();
		}

	}
	
	public static File getBuildingFile(final String name) {
		File f = new File(MinecraftServer.getServer().getDataDirectory().getAbsolutePath() + File.separator + "millenaire" + File.separator + "exports" + File.separator);
		if(!f.exists())
			f.mkdirs();

		File f1 = new File(f, name + ".mlplan");
		return f1;
	}
	
	private static boolean valid(short width, short height, short length, short depth, NBTTagCompound tag) {
		boolean valid = true;
		if(tag.getShort("Width") != width && tag.getShort("Width") != 0) {
			valid = false;
		}
		else if(tag.getShort("Height") != height && tag.getShort("Height") != 0) {
			valid = false;
		}
		else if(tag.getShort("Length") != length && tag.getShort("Length") != 0) {
			valid = false;
		}
		else if(tag.getShort("StartLevel") != depth && tag.getShort("StartLevel") != 0) {
			valid = false;
		}
		return valid;
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
	 * @throws Exception 
	 */
	public static File exportToSchem(IBlockState[][][] blocks, short width, short height, short length, short depth, String name, int level, VillagerType[] maleVillagers, VillagerType[] femaleVillagers, EntityPlayer player) throws Exception {
		File f1 = getBuildingFile(name);

		NBTTagCompound tag = getBuildingTag(name);
		
		if(!valid(width, height, length, depth, tag)) {
			PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.error.exporting.dimensions");
			Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
			throw new Exception("Ahhh!");
		}

		byte[] blockids = new byte[width*height*length], data = new byte[width*height*length];
		
		String blocklist = "";
		String[] s = new String[width*height*length];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					s[(y*length+z)*width+x] = Block.getIdFromBlock(blocks[y][z][x].getBlock()) + ":" + blocks[y][z][x].getBlock().getMetaFromState(blocks[y][z][x]) + ";";
					
					//blocklist += Block.getIdFromBlock(blocks[y][z][x].getBlock()) + ":" + blocks[y][z][x].getBlock().getMetaFromState(blocks[y][z][x]) + ";";
					
					//blockids[(y*length+z)*width+x] = (byte)Block.getIdFromBlock(blocks[y][z][x].getBlock());
					//data[(y*length+z)*width+x] = (byte)blocks[y][z][x].getBlock().getMetaFromState(blocks[y][z][x]);
				}
			}
		}
		
		for(String s1 : s) {
			blocklist += s1;
		}

		NBTTagList LevelTagComp = new NBTTagList();
		NBTTagCompound tag2 = new NBTTagCompound();
		tag2.setString("BlockData", blocklist);
		//tag2.setByteArray("Blocks", blockids);
		//tag2.setByteArray("Data", data);
		LevelTagComp.appendTag(tag2);

		tag.setTag("level_" + level, LevelTagComp);

		tag.setString("Version", FILE_VERSION);
		
		tag.setShort("Width", width);
		tag.setShort("Height", height);
		tag.setShort("Length", length);
		tag.setShort("StartLevel", depth);
		tag.setString("BuildingName", name);

		NBTTagList MaleList = new NBTTagList();
		for(int i = 0; i < maleVillagers.length; i++)
		{
			String s3 = maleVillagers[i].id;
			if(s3 != null)
			{
				NBTTagCompound tag3 = new NBTTagCompound();
				tag3.setString("" + i, s3);
				MaleList.appendTag(tag3);
			}
		}

		NBTTagList FemaleList = new NBTTagList();
		for(int i = 0; i < femaleVillagers.length; i++)
		{
			String s4 = femaleVillagers[i].id;
			if(s4 != null)
			{
				NBTTagCompound tag3 = new NBTTagCompound();
				tag3.setString("" + i, s4);
				FemaleList.appendTag(tag3);
			}
		}

		tag.setTag("MaleVillagers", MaleList);
		tag.setTag("FemaleVillagers", FemaleList);

		try {
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(f1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PacketSayTranslatedMessage packet = new PacketSayTranslatedMessage("message.completed");
		Millenaire.simpleNetworkWrapper.sendTo(packet, (EntityPlayerMP)player);
		return f1;
	}
}