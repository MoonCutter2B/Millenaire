package org.millenaire.common;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;

public class MillWorldInfo implements Cloneable {

	public static class MillMapInfo {

		public static final byte WATER=1;
		public static final byte DANGER=2;
		public static final byte BUILDING_FORBIDDEN=3;
		public static final byte BUILDING_LOC=4;
		public static final byte TREE=5;
		public static final byte UNREACHABLE=6;
		public static final byte UNBUILDABLE=7;
		public static final byte OUTOFRANGE=8;
		public static final byte OTHER=9;
		public static final byte PATH=10;

		public static void readPacket(DataInputStream ds) {
			Point pos=null;
			try {
				pos = StreamReadWrite.readNullablePoint(ds);
			} catch (final IOException e) {
				MLN.printException(e);
				return;
			}

			final Building building=Mill.clientWorld.getBuilding(pos);

			if (building==null)
				return;

			final MillMapInfo minfo=new MillMapInfo(building);

			try {

				minfo.length=ds.readInt();
				minfo.width=ds.readInt();
				minfo.mapStartX=ds.readInt();
				minfo.mapStartZ=ds.readInt();

				minfo.data=new byte[minfo.length][];

				for (int x=0;x<minfo.length;x++) {
					minfo.data[x]=new byte[minfo.width];
					for (int z=0;z<minfo.width;z++) {
						minfo.data[x][z]=ds.readByte();
					}
				}

				building.mapInfo=minfo;

				if (MLN.Network>=MLN.DEBUG) {
					MLN.debug(null, "Receiving map info packet.");
				}

			} catch (final IOException e) {
				MLN.printException(e);
			}
		}

		public byte[][] data;
		public int width;

		public int length;

		public int mapStartX=0,mapStartZ=0;

		public Building townHall;

		private MillMapInfo(Building townHall) {
			this.townHall=townHall;
		}

		public MillMapInfo(Building townHall,MillWorldInfo winfo) {

			this.townHall=townHall;

			byte thRegionId=0;

			if (townHall.pathing!=null) {
				thRegionId=townHall.pathing.thRegion;
			}

			final Point centre=townHall.location.pos;
			final int relcentreX=centre.getiX()-winfo.mapStartX;
			final int relcentreZ=centre.getiZ()-winfo.mapStartZ;

			width=winfo.width;
			length=winfo.length;
			mapStartX=winfo.mapStartX;
			mapStartZ=winfo.mapStartZ;

			data=new byte[winfo.length][];

			for (int x=0;x<winfo.length;x++) {
				data[x]=new byte[winfo.width];
				for (int y=0;y<winfo.width;y++) {

					if (winfo.water[x][y]) {
						data[x][y]=WATER;
					} else if (winfo.danger[x][y]) {
						data[x][y]=DANGER;
					} else if (winfo.buildingForbidden[x][y]) {
						data[x][y]=BUILDING_FORBIDDEN;
					} else if (winfo.buildingLoc[x][y]) {
						data[x][y]=BUILDING_LOC;
					} else if (winfo.tree[x][y]) {
						data[x][y]=TREE;
					} else if (winfo.path[x][y]) {
						data[x][y]=PATH;
					} else if ((townHall.pathing!=null) && (townHall.pathing.regions[x][y]!=thRegionId)) {
						data[x][y]=UNREACHABLE;
					} else if (!winfo.canBuild[x][y]){
						data[x][y]=UNBUILDABLE;
					} else if (((Math.abs(relcentreX-x)>townHall.villageType.radius)) || (Math.abs(relcentreZ-y)>townHall.villageType.radius)) {
						data[x][y]=OUTOFRANGE;
					} else {
						data[x][y]=OTHER;
					}
				}
			}
		}

		public void sendMapInfoPacket(EntityPlayer player) {
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			final DataOutputStream ds = new DataOutputStream(bytes);

			try {

				ds.write(ServerReceiver.PACKET_MAPINFO);
				StreamReadWrite.writeNullablePoint(townHall.getPos(), ds);

				ds.writeInt(length);
				ds.writeInt(width);
				ds.writeInt(mapStartX);
				ds.writeInt(mapStartZ);

				for (int x=0;x<length;x++) {
					for (int z=0;z<width;z++) {
						ds.writeByte(data[x][z]);
					}
				}

			} catch (final IOException e) {
				MLN.printException(this+": Error in sendUpdatePacket", e);
			}

			final Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = ServerReceiver.PACKET_CHANNEL;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;

			ServerSender.sendPacketToPlayer(packet, player.username);
		}
	}

	public class UpdateThread extends Thread {

		int x;
		int z;

		@Override
		public void run() {
			updateChunk(x,z);

		}

	}
	private static final int MAP_MARGIN = 10;

	private static final int BUILDING_MARGIN = 5;

	private static final int VALID_HEIGHT_DIFF = 10;

	public static final int UPDATE_FREQUENCY = 1000;

	public static boolean[][] booleanArrayDeepClone(boolean[][] source) {

		final boolean[][] target=new boolean[source.length][];

		for (int i=0;i<source.length;i++) {
			target[i]=source[i].clone();
		}

		return target;
	}
	public static byte[][] byteArrayDeepClone(byte[][] source) {

		final byte[][] target=new byte[source.length][];

		for (int i=0;i<source.length;i++) {
			target[i]=source[i].clone();
		}

		return target;
	}
	static public boolean isForbiddenBlockForConstruction(int bid) {
		return ((bid == Block.waterStill.blockID) || (bid == Block.waterMoving.blockID) || (bid == Block.ice.blockID)
				|| (bid == Block.lavaMoving.blockID) || (bid == Block.lavaStill.blockID) ||
				(bid == Block.planks.blockID) || (bid == Block.cobblestone.blockID) || (bid == Block.brick.blockID)
				|| (bid == Block.chest.blockID) || (bid == Block.glass.blockID)
				|| (bid == Mill.earth_decoration.blockID)
				|| (bid == Mill.stone_decoration.blockID)
				|| (bid == Mill.wood_decoration.blockID));
	}
	public static short[][] shortArrayDeepClone(short[][] source) {

		final short[][] target=new short[source.length][];

		for (int i=0;i<source.length;i++) {
			target[i]=source[i].clone();
		}

		return target;
	}
	public int length=0;
	public int width=0;
	public int chunkStartX=0,chunkStartZ=0;
	public int mapStartX=0,mapStartZ=0;
	public int yBaseline=0;
	public short[][] topGround;
	public short[][] constructionHeight;
	public short[][] spaceAbove;
	public boolean[][] danger;

	public boolean[][] buildingLoc;

	public boolean[][] canBuild;

	public boolean[][] buildingForbidden;

	public boolean[][] water;

	public boolean[][] tree;
	public boolean[][] buildTested=null;

	public boolean[][] topAdjusted;

	public boolean[][] path;

	public int frequency=10;

	private Vector<BuildingLocation> buildingLocations=new Vector<BuildingLocation>();
	public BuildingLocation locationIP;

	public int nbLoc=0;

	public World world;

	public int lastUpdatedX,lastUpdatedZ;

	private int updateCounter;

	public MillWorldInfo() {

	}

	@Override
	@SuppressWarnings("unchecked")
	public MillWorldInfo clone()  throws CloneNotSupportedException  {
		final MillWorldInfo o=(MillWorldInfo) super.clone();
		o.topGround=shortArrayDeepClone(topGround);
		o.constructionHeight=shortArrayDeepClone(constructionHeight);
		o.spaceAbove=shortArrayDeepClone(spaceAbove);
		o.danger=booleanArrayDeepClone(danger);
		o.buildingLoc=booleanArrayDeepClone(buildingLoc);
		o.canBuild=booleanArrayDeepClone(canBuild);
		o.buildingForbidden=booleanArrayDeepClone(buildingForbidden);
		o.water=booleanArrayDeepClone(water);
		o.tree=booleanArrayDeepClone(tree);
		o.path=booleanArrayDeepClone(path);
		o.buildingLocations=(Vector<BuildingLocation>) buildingLocations.clone();
		return o;
	}

	private void createWorldInfo(Vector<BuildingLocation> locations,BuildingLocation blIP,int pstartX,int pstartZ,int endX,int endZ) throws MillenaireException {

		if (MLN.WorldInfo>=MLN.MINOR) {
			MLN.minor(this, "Creating world info: "+pstartX+"/"+pstartZ+"/"+endX+"/"+endZ);
		}


		chunkStartX=(pstartX >> 4);
		chunkStartZ=(pstartZ >> 4);
		mapStartX=(chunkStartX << 4);
		mapStartZ=(chunkStartZ << 4);

		length=(((endX >> 4)+1) << 4)-mapStartX;
		width=(((endZ >> 4)+1) << 4)-mapStartZ;

		frequency=(int) Math.max(((UPDATE_FREQUENCY*1.0)/((length*width)/256)),10);

		if (frequency==0)
			throw new MillenaireException("Null frequency in createWorldInfo.");


		if (MLN.WorldInfo>=MLN.MAJOR) {
			MLN.major(this, "Creating world info: "+mapStartX+"/"+mapStartZ+"/"+length+"/"+width);
		}


		topGround=new short[length][width];
		constructionHeight=new short[length][width];
		spaceAbove=new short[length][width];
		danger=new boolean[length][width];
		buildingLoc=new boolean[length][width];
		buildingForbidden=new boolean[length][width];
		canBuild=new boolean[length][width];
		buildTested=new boolean[length][width];
		water=new boolean[length][width];
		tree=new boolean[length][width];
		path=new boolean[length][width];
		topAdjusted=new boolean[length][width];

		buildingLocations=new Vector<BuildingLocation>();

		for (int i=0;i<length;i++) {
			for (int j=0;j<width;j++) {
				buildingLoc[i][j]=false;
				canBuild[i][j]=false;
			}
		}

		for (final BuildingLocation location : locations) {
			registerBuildingLocation(location);

		}

		locationIP=blIP;
		if (locationIP != null) {
			registerBuildingLocation(locationIP);
		}

		for (int i=0;i<length;i+=16) {
			for (int j=0;j<width;j+=16) {
				updateChunk(i,j);
			}
		}
		lastUpdatedX=0;
		lastUpdatedZ=0;
	}

	public BuildingLocation getLocationAtCoord(Point p) {
		if ((locationIP!=null) && locationIP.isInside(p))
			return locationIP;

		for (final BuildingLocation bl : buildingLocations) {
			if (bl.isInside(p))
				return bl;
		}

		return null;
	}

	public boolean isConstructionOrLoggingForbiddenHere(Point p) {

		if ((p.getiX()<mapStartX) || (p.getiZ()<mapStartZ)
				|| (p.getiX()>=(mapStartX+length)) || (p.getiZ()>=(mapStartZ+width)))
			return false;

		return buildingForbidden[p.getiX()-mapStartX][p.getiZ()-mapStartZ];
	}

	private void registerBuildingLocation(BuildingLocation bl) {

		if (MLN.WorldInfo>=MLN.MAJOR) {
			MLN.major(this,"Registering building location: "+bl);
		}

		buildingLocations.add(bl);

		final int sx=Math.max(bl.minxMargin-mapStartX,0);
		final int sz=Math.max(bl.minzMargin-mapStartZ,0);
		final int ex=Math.min(bl.maxxMargin-mapStartX,length+1);
		final int ez=Math.min(bl.maxzMargin-mapStartZ,width+1);

		for (int i=sx;i<ex;i++) {
			for (int j=sz;j<ez;j++) {
				buildingLoc[i][j]=true;
			}
		}
	}

	public void removeBuildingLocation(BuildingLocation bl) {
		for (final BuildingLocation l : buildingLocations) {
			if (l.isLocationSamePlace(bl)) {
				buildingLocations.remove(l);
				break;
			}
		}

		final int sx=Math.max(bl.minxMargin-mapStartX,0);
		final int sz=Math.max(bl.minzMargin-mapStartZ,0);
		final int ex=Math.min(bl.maxxMargin-mapStartX,length);
		final int ez=Math.min(bl.maxzMargin-mapStartZ,width);

		for (int i=sx;i<ex;i++) {
			for (int j=sz;j<ez;j++) {
				buildingLoc[i][j]=false;
			}
		}
	}

	public boolean update(World world,Vector<BuildingLocation> locations,BuildingLocation blIP,Point centre,int radius) throws MillenaireException {

		this.world=world;
		this.yBaseline=centre.getiY();
		locationIP=blIP;

		if ((buildingLocations!=null) && (buildingLocations.size()>0) && (buildingLocations.size()==locations.size())) {
			buildingLocations=locations;
			updateNextChunk();
			return false;
		}

		int startX=centre.getiX(),startZ=centre.getiZ(),endX=centre.getiX(),endZ=centre.getiZ();

		BuildingLocation blStartX=null,blStartZ=null,blEndX=null,blEndZ=null;

		for (final BuildingLocation location : locations) {
			if (location != null) {
				if ((location.pos.getiX()-(location.length/2))<startX) {
					startX=location.pos.getiX()-(location.length/2);
					blStartX=location;
				}
				if ((location.pos.getiX()+(location.length/2))>endX) {
					endX=location.pos.getiX()+(location.length/2);
					blEndX=location;
				}
				if ((location.pos.getiZ()-(location.width/2))<startZ) {
					startZ=location.pos.getiZ()-(location.width/2);
					blStartZ=location;
				}
				if ((location.pos.getiZ()+(location.width/2))>endZ) {
					endZ=location.pos.getiZ()+(location.width/2);
					blEndZ=location;
				}
			}
		}

		if (blIP != null) {
			if ((blIP.pos.getiX()-(blIP.length/2))<startX) {
				startX=blIP.pos.getiX()-(blIP.length/2);
				blStartX=blIP;
			}
			if ((blIP.pos.getiX()+(blIP.length/2))>endX) {
				endX=blIP.pos.getiX()+(blIP.length/2);
				blEndX=blIP;
			}
			if ((blIP.pos.getiZ()-(blIP.width/2))<startZ) {
				startZ=blIP.pos.getiZ()-(blIP.width/2);
				blStartZ=blIP;
			}
			if ((blIP.pos.getiZ()+(blIP.width/2))>endZ) {
				endZ=blIP.pos.getiZ()+(blIP.width/2);
				blEndZ=blIP;
			}
		}

		if (MLN.WorldInfo>=MLN.MAJOR) {

			MLN.major(this, "WorldInfo Centre: "+centre);

			if ((startX-BUILDING_MARGIN)<(centre.getiX()-radius-MAP_MARGIN)) {
				MLN.major(this, "Pushing startX down by "+(startX-BUILDING_MARGIN-(centre.getiX()-radius-MAP_MARGIN))+" due to "+blStartX);
			} else {
				MLN.major(this, "Using default value of "+(centre.getiX()-radius-MAP_MARGIN)+" for startX");
			}

			if ((startZ-BUILDING_MARGIN)<(centre.getiZ()-radius-MAP_MARGIN)) {
				MLN.major(this, "Pushing startZ down by "+(startZ-BUILDING_MARGIN-(centre.getiZ()-radius-MAP_MARGIN))+" due to "+blStartZ);
			} else {
				MLN.major(this, "Using default value of "+(centre.getiZ()-radius-MAP_MARGIN)+" for startZ");
			}

			if ((endX+BUILDING_MARGIN)>(centre.getiX()+radius+MAP_MARGIN)) {
				MLN.major(this, "Pushing endX up by "+((endX+BUILDING_MARGIN)-(centre.getiX()+radius+MAP_MARGIN))+" due to "+blEndX);
			} else {
				MLN.major(this, "Using default value of "+(centre.getiX()+radius+MAP_MARGIN)+" for endX");
			}

			if ((endZ+BUILDING_MARGIN)>(centre.getiZ()+radius+MAP_MARGIN)) {
				MLN.major(this, "Pushing endZ up by "+((endZ+BUILDING_MARGIN)-(centre.getiZ()+radius+MAP_MARGIN))+" due to "+blEndZ);
			} else {
				MLN.major(this, "Using default value of "+(centre.getiZ()+radius+MAP_MARGIN)+" for endZ");
			}

		}

		startX=Math.min(startX-BUILDING_MARGIN, centre.getiX()-radius-MAP_MARGIN);
		startZ=Math.min(startZ-BUILDING_MARGIN, centre.getiZ()-radius-MAP_MARGIN);
		endX=Math.max(endX+BUILDING_MARGIN, centre.getiX()+radius+MAP_MARGIN);
		endZ=Math.max(endZ+BUILDING_MARGIN, centre.getiZ()+radius+MAP_MARGIN);

		if (MLN.WorldInfo>=MLN.MAJOR) {
			MLN.major(this, "Values: "+startX+"/"+startZ+"/"+endX+"/"+endZ);
		}

		final int chunkStartXTemp=(startX >> 4);
		final int chunkStartZTemp=(startZ >> 4);
		final int mapStartXTemp=(chunkStartXTemp << 4);
		final int mapStartZTemp=(chunkStartZTemp << 4);
		final int lengthTemp=(((endX >> 4)+1) << 4)-mapStartXTemp;
		final int widthTemp=(((endZ >> 4)+1) << 4)-mapStartZTemp;

		if (MLN.WorldInfo>=MLN.MAJOR) {
			MLN.major(this, "Values after chunks: "+mapStartXTemp+"/"+mapStartZTemp+"/"+(mapStartXTemp+lengthTemp)+"/"+(mapStartZTemp+widthTemp));
		}

		if ((lengthTemp != length) || (widthTemp != width)) {
			createWorldInfo(locations,blIP,startX,startZ,endX,endZ);
			return true;
		} else {

			buildingLocations=new Vector<BuildingLocation>();
			for (final BuildingLocation location : locations) {
				registerBuildingLocation(location);
			}

			updateNextChunk();
			return false;
		}
	}

	private void updateChunk(int startX,int startZ) {

		//We have to test not just for this chunk but the surrounding ones also as we need to do some operations that involve
		//neighbouring blocks
		for (int i=-1;i<2;i++) {
			for (int j=-1;j<2;j++) {
				if (!world.getChunkProvider().chunkExists(((startX+mapStartX) >> 4) + i, ((startZ+mapStartZ) >>4) + j)) {
					if (MLN.WorldInfo>=MLN.DEBUG) {
						MLN.debug(this, "Chunk is not loaded.");
					}
					return;
				}
			}
		}


		final Chunk chunk=world.getChunkFromBlockCoords(startX+mapStartX, startZ+mapStartZ);

		if (MLN.WorldInfo>=MLN.DEBUG) {
			MLN.debug(this, "Updating chunk: "+startX+"/"+startZ+"/"+yBaseline+"/"+chunk.xPosition+"/"+chunk.zPosition);
		}



		for (int i=0;i<16;i++) {
			for (int j=0;j<16;j++) {



				final short miny=(short) Math.max(yBaseline-25, 1);
				final short maxy=(short) Math.min(yBaseline+25, 255);

				final int mx=i+startX;
				final int mz=j+startZ;

				canBuild[mx][mz]=false;
				buildingForbidden[mx][mz]=false;
				water[mx][mz]=false;
				topAdjusted[mx][mz]=false;

				short y=maxy;

				int bid;

				short ceilingSize=0;
				int tbid=chunk.getBlockID(i,y,j);

				while ((y>=miny) && !MillCommonUtilities.isBlockIdGround(tbid)) {
					if (MillCommonUtilities.isBlockIdGroundOrCeiling(tbid)) {
						ceilingSize++;
					} else {
						ceilingSize=0;
					}

					y--;

					if (ceilingSize>3) {
						break;
					}

					tbid=chunk.getBlockID(i,y,j);
				}

				constructionHeight[mx][mz]=y;

				boolean heightDone=false;


				if ((y<=maxy) && (y>1)) {
					bid=chunk.getBlockID(i, y,j);
				} else {
					bid=-1;
				}

				boolean onground=true;//used to continue looking for surface if starting in water
				short lastLiquid=-1;

				while ((bid>-1) && (MillCommonUtilities.isBlockIdSolid(bid) || MillCommonUtilities.isBlockIdLiquid(bid) || !onground)) {

					if (bid==Block.wood.blockID) {
						heightDone=true;
					} else if (!heightDone) {//everything solid but wood counts
						constructionHeight[mx][mz]++;
					} else {
						heightDone=true;
					}

					if (isForbiddenBlockForConstruction(bid)) {
						buildingForbidden[mx][mz]=true;
					}

					if (MillCommonUtilities.isBlockIdLiquid(bid)) {
						onground=false;
						lastLiquid=y;
					} else if (MillCommonUtilities.isBlockIdSolid(bid)) {
						onground=true;
					}

					y++;

					if ((y<=maxy) && (y>1)) {
						bid=chunk.getBlockID(i, y,j);
					} else {
						bid=-1;
					}
				}

				if (onground==false) {
					y=lastLiquid;
				}

				while ((y<=maxy) && (y>1) && !(!MillCommonUtilities.isBlockIdSolid(chunk.getBlockID(i, y,j)) && !MillCommonUtilities.isBlockIdSolid(chunk.getBlockID(i, y+1,j)))) {
					y++;
				}

				y=(byte) Math.max(1, y);

				topGround[mx][mz]=y;
				spaceAbove[mx][mz]=0;

				final int soilbid=chunk.getBlockID(i,y-1,j);
				bid=chunk.getBlockID(i,y,j);

				if ((bid==Block.waterMoving.blockID) || (bid==Block.waterStill.blockID)) {
					water[mx][mz]=true;
				}

				if ((soilbid==Block.wood.blockID)) {
					tree[mx][mz]=true;
				} else {
					tree[mx][mz]=false;
				}

				if (soilbid==Mill.path.blockID || soilbid==Mill.pathSlab.blockID) {
					path[mx][mz]=true;
				} else {
					path[mx][mz]=false;
				}

				boolean blocked=false;

				if (!(bid==Block.fence.blockID) && !MillCommonUtilities.isBlockIdSolid(bid) && (bid != Block.waterMoving.blockID) &&
						(bid != Block.waterStill.blockID)) {
					spaceAbove[mx][mz]=1;
				} else {
					blocked=true;
				}

				if ((bid == Block.lavaMoving.blockID) || (bid == Block.lavaStill.blockID)) {
					if (MLN.WorldInfo>=MLN.DEBUG) {
						MLN.debug(this, "Found danger: "+bid);
					}
					danger[mx][mz]=true;
				} else {
					danger[mx][mz]=false;
					for (final int id : MLN.forbiddenBlocks) {
						if (id == bid) {
							danger[mx][mz]=true;
						}
						if (soilbid == bid) {
							danger[mx][mz]=true;
						}
					}
				}

				if (!danger[mx][mz] && !buildingLoc[mx][mz]) {
					if ((constructionHeight[mx][mz]>(yBaseline-VALID_HEIGHT_DIFF)) && (constructionHeight[mx][mz]<(yBaseline+VALID_HEIGHT_DIFF)) ) {
						canBuild[mx][mz]=true;
					}
				}

				if (isForbiddenBlockForConstruction(bid)) {
					buildingForbidden[mx][mz]=true;
				}

				y++;

				while ((y<maxy) && (y>0)) {
					bid=chunk.getBlockID(i,y,j);

					if (!blocked && (spaceAbove[mx][mz]<3) && !MillCommonUtilities.isBlockIdSolid(bid)) {
						spaceAbove[mx][mz]++;
					} else {
						blocked=true;
					}

					if (isForbiddenBlockForConstruction(bid)) {
						buildingForbidden[mx][mz]=true;
					}

					y++;
				}

				if (buildingForbidden[mx][mz]) {
					canBuild[mx][mz]=false;
				}
			}
		}

		/*
		 * New method: attempt to "bridge" gaps in topground (especially doorways)
		 * 
		 * First, gaps one block large, possibly with difference in level up to 2
		 */

		boolean gapFilled=true;

		while (gapFilled) {
			gapFilled=false;
			for (int i=-5;i<21;i++) {
				for (int j=-5;j<21;j++) {
					final int mx=i+startX;
					final int mz=j+startZ;

					if ((mz>=0) && (mz<width)) {
						if ((mx>1) && (mx<(length-1))) {
							if ((Math.abs(topGround[mx-1][mz]-topGround[mx+1][mz])<2) && (((topGround[mx-1][mz]+2)<topGround[mx][mz])
									|| ((topGround[mx+1][mz]+2)<topGround[mx][mz]))) {

								final short ntg=topGround[mx-1][mz];
								final boolean samesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg, startZ+mapStartZ+j));
								final boolean belowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-1, startZ+mapStartZ+j));
								final boolean below2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-2, startZ+mapStartZ+j));
								final boolean abovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+1, startZ+mapStartZ+j));
								final boolean above2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+2, startZ+mapStartZ+j));
								final boolean above3solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+3, startZ+mapStartZ+j));


								//check if same level works
								if ((Math.abs(topGround[mx-1][mz]-topGround[mx+1][mz])<2) && belowsolid && !samesolid && !abovesolid) {
									//MLN.temp(this, i+"/"+j+" Vert 1 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=ntg;
									if (!above2solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								} else if ((topGround[mx+1][mz]<=topGround[mx-1][mz]) && below2solid && !belowsolid && !samesolid && !abovesolid) {
									//MLN.temp(this, i+"/"+j+" Vert 1 space down: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=(short) (ntg-1);
									if (!abovesolid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								} else if ((topGround[mx+1][mz]>=topGround[mx-1][mz]) &&samesolid && !abovesolid && !above2solid) {
									//MLN.temp(this, i+"/"+j+" Vert 1 space up: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=(short) (ntg+1);
									if (!above3solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								}
							}
						}
					}
					if ((mx>=0) && (mx<length)) {
						if ((mz>1) && (mz<(width-1))) {
							if ((Math.abs(topGround[mx][mz-1]-topGround[mx][mz+1])<3) && (((topGround[mx][mz-1]+2)<topGround[mx][mz])
									|| ((topGround[mx][mz+1]+2)<topGround[mx][mz]))) {

								final short ntg=topGround[mx][mz-1];
								final boolean samesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg, startZ+mapStartZ+j));
								final boolean belowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-1, startZ+mapStartZ+j));
								final boolean below2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-2, startZ+mapStartZ+j));
								final boolean abovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+1, startZ+mapStartZ+j));
								final boolean above2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+2, startZ+mapStartZ+j));
								final boolean above3solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+3, startZ+mapStartZ+j));


								//check if same level works
								if ((Math.abs(topGround[mx][mz-1]-topGround[mx][mz+1])<2) && belowsolid && !samesolid && !abovesolid) {
									//MLN.temp(this, i+"/"+j+" Hor 1 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=ntg;
									if (!above2solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								} else if ((topGround[mx][mz+1]<=topGround[mx][mz-1]) && below2solid && !belowsolid && !samesolid && !abovesolid) {
									//MLN.temp(this, i+"/"+j+" Hor 1 space down: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=(short) (ntg-1);
									if (!abovesolid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								} else if ((topGround[mx][mz+1]>=topGround[mx][mz-1]) &&samesolid && !abovesolid && !above2solid) {
									//MLN.temp(this, i+"/"+j+" Hor 1 space up: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=(short) (ntg+1);
									if (!above3solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								}
							}
						}
					}
				}
			}

			/*
			 * Then, gaps two blocks large, on the same level (for instance, passage between a double-size wall)
			 */
			for (int i=-5;i<21;i++) {
				for (int j=-5;j<21;j++) {
					final int mx=i+startX;
					final int mz=j+startZ;

					if ((mz>=0) && (mz<width)) {
						if ((mx>1) && (mx<(length-2))) {
							if ((topGround[mx-1][mz]==topGround[mx+2][mz]) && (topGround[mx-1][mz]<topGround[mx][mz])
									&& (topGround[mx-1][mz]<topGround[mx+1][mz])) {

								final short ntg=topGround[mx-1][mz];
								final boolean samesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg, startZ+mapStartZ+j));
								final boolean belowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-1, startZ+mapStartZ+j));
								final boolean abovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+1, startZ+mapStartZ+j));
								final boolean above2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+2, startZ+mapStartZ+j));

								//using the world obj because we might be beyond the chunk
								final boolean nextsamesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i+1, ntg, startZ+mapStartZ+j));
								final boolean nextbelowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i+1, ntg-1, startZ+mapStartZ+j));
								final boolean nextabovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i+1, ntg+1, startZ+mapStartZ+j));
								final boolean nextabove2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i+1, ntg+2, startZ+mapStartZ+j));

								//check if same level works
								if (belowsolid && nextbelowsolid && !samesolid && !nextsamesolid && !abovesolid && !nextabovesolid ) {
									//MLN.temp(this, i+"/"+j+" Vert 2 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=ntg;
									topGround[mx+1][mz]=ntg;
									if (!above2solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}

									if (!nextabove2solid) {
										spaceAbove[mx+1][mz]=3;
									} else {
										spaceAbove[mx+1][mz]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								}
							}
						}
					}
					if ((mx>=0) && (mx<length)) {
						if ((mz>1) && (mz<(width-2))) {
							if ((topGround[mx][mz-1]==topGround[mx][mz+2]) && (topGround[mx][mz-1]<topGround[mx][mz])
									&& (topGround[mx][mz-1]<topGround[mx][mz+1])) {

								final short ntg=topGround[mx][mz-1];
								final boolean samesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg, startZ+mapStartZ+j));
								final boolean belowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-1, startZ+mapStartZ+j));
								final boolean abovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+1, startZ+mapStartZ+j));
								final boolean above2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+2, startZ+mapStartZ+j));

								//using the world obj because we might be beyond the chunk
								final boolean nextsamesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg, startZ+mapStartZ+j+1));
								final boolean nextbelowsolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg-1, startZ+mapStartZ+j+1));
								final boolean nextabovesolid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+1, startZ+mapStartZ+j+1));
								final boolean nextabove2solid=MillCommonUtilities.isBlockIdSolid(world.getBlockId(startX+mapStartX+i, ntg+2, startZ+mapStartZ+j+1));

								//check if same level works
								if (belowsolid && nextbelowsolid && !samesolid && !nextsamesolid && !abovesolid && !nextabovesolid ) {
									//MLN.temp(this, i+"/"+j+" Hor 2 space: "+topGround[mx][mz]+" to "+ntg);
									topGround[mx][mz]=ntg;
									topGround[mx][mz+1]=ntg;
									if (!above2solid) {
										spaceAbove[mx][mz]=3;
									} else {
										spaceAbove[mx][mz]=2;
									}

									if (!nextabove2solid) {
										spaceAbove[mx][mz+1]=3;
									} else {
										spaceAbove[mx][mz+1]=2;
									}
									gapFilled=true;
									topAdjusted[mx][mz]=true;
								}
							}
						}
					}
				}
			}
		}


		for (int i=0;i<16;i++) {
			for (int j=0;j<16;j++) {

				final int mx=i+startX;
				final int mz=j+startZ;

				if (danger[mx][mz]) {
					for (int k=-2;k<3;k++) {
						for (int l=-2;l<3;l++) {
							if ((k>=0) && (l>=0) && (k<length) && (l<width)) {
								spaceAbove[mx][mz]=0;
							}
						}
					}
				}
			}
		}
	}

	public void updateNextChunk() {

		updateCounter = ((updateCounter+1) % (frequency));

		if (updateCounter != 0)
			return;

		lastUpdatedX++;
		if ((lastUpdatedX*16) >= length) {
			lastUpdatedX=0;
			lastUpdatedZ++;
		}

		if ((lastUpdatedZ*16) >= width) {
			lastUpdatedZ=0;
		}

		final UpdateThread thread=new UpdateThread();
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.x=lastUpdatedX << 4;
		thread.z=lastUpdatedZ << 4;

		thread.start();
	}
}
