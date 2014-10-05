package org.millenaire.common;

import io.netty.buffer.ByteBufInputStream;

import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import org.millenaire.common.building.Building;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;

/**
 * Client-side data only used to show the village map
 * 
 * (created from data from WorldInfo sent by server)
 * 
 * @author cedricdj
 * 
 */
public class MillMapInfo {

	public static final byte WATER = 1;
	public static final byte DANGER = 2;
	public static final byte BUILDING_FORBIDDEN = 3;
	public static final byte BUILDING_LOC = 4;
	public static final byte TREE = 5;
	public static final byte UNREACHABLE = 6;
	public static final byte UNBUILDABLE = 7;
	public static final byte OUTOFRANGE = 8;
	public static final byte OTHER = 9;
	public static final byte PATH = 10;

	public static void readPacket(final ByteBufInputStream ds) {
		Point pos = null;
		try {
			pos = StreamReadWrite.readNullablePoint(ds);
		} catch (final IOException e) {
			MLN.printException(e);
			return;
		}

		final Building building = Mill.clientWorld.getBuilding(pos);

		if (building == null) {
			return;
		}

		final MillMapInfo minfo = new MillMapInfo(building);

		try {

			minfo.length = ds.readInt();
			minfo.width = ds.readInt();
			minfo.mapStartX = ds.readInt();
			minfo.mapStartZ = ds.readInt();

			minfo.data = new byte[minfo.length][];

			for (int x = 0; x < minfo.length; x++) {
				minfo.data[x] = new byte[minfo.width];
				for (int z = 0; z < minfo.width; z++) {
					minfo.data[x][z] = ds.readByte();
				}
			}

			building.mapInfo = minfo;

			if (MLN.LogNetwork >= MLN.DEBUG) {
				MLN.debug(null, "Receiving map info packet.");
			}

		} catch (final IOException e) {
			MLN.printException(e);
		}
	}

	public byte[][] data;
	public int width;

	public int length;

	public int mapStartX = 0, mapStartZ = 0;

	public Building townHall;

	private MillMapInfo(final Building townHall) {
		this.townHall = townHall;
	}

	public MillMapInfo(final Building townHall, final MillWorldInfo winfo) {

		this.townHall = townHall;

		byte thRegionId = 0;

		if (townHall.pathing != null) {
			thRegionId = townHall.pathing.thRegion;
		}

		final Point centre = townHall.location.pos;
		final int relcentreX = centre.getiX() - winfo.mapStartX;
		final int relcentreZ = centre.getiZ() - winfo.mapStartZ;

		width = winfo.width;
		length = winfo.length;
		mapStartX = winfo.mapStartX;
		mapStartZ = winfo.mapStartZ;

		data = new byte[winfo.length][];

		for (int x = 0; x < winfo.length; x++) {
			data[x] = new byte[winfo.width];
			for (int y = 0; y < winfo.width; y++) {

				if (winfo.water[x][y]) {
					data[x][y] = WATER;
				} else if (winfo.danger[x][y]) {
					data[x][y] = DANGER;
				} else if (winfo.buildingForbidden[x][y]) {
					data[x][y] = BUILDING_FORBIDDEN;
				} else if (winfo.buildingLoc[x][y]) {
					data[x][y] = BUILDING_LOC;
				} else if (winfo.tree[x][y]) {
					data[x][y] = TREE;
				} else if (winfo.path[x][y]) {
					data[x][y] = PATH;
				} else if (townHall.pathing != null
						&& townHall.pathing.regions[x][y] != thRegionId) {
					data[x][y] = UNREACHABLE;
				} else if (!winfo.canBuild[x][y]) {
					data[x][y] = UNBUILDABLE;
				} else if (Math.abs(relcentreX - x) > townHall.villageType.radius
						|| Math.abs(relcentreZ - y) > townHall.villageType.radius) {
					data[x][y] = OUTOFRANGE;
				} else {
					data[x][y] = OTHER;
				}
			}
		}
	}

	public void sendMapInfoPacket(final EntityPlayer player) {
		final DataOutput ds = ServerSender.getNewByteBufOutputStream();

		try {

			ds.write(ServerReceiver.PACKET_MAPINFO);
			StreamReadWrite.writeNullablePoint(townHall.getPos(), ds);

			ds.writeInt(length);
			ds.writeInt(width);
			ds.writeInt(mapStartX);
			ds.writeInt(mapStartZ);

			for (int x = 0; x < length; x++) {
				for (int z = 0; z < width; z++) {
					ds.writeByte(data[x][z]);
				}
			}

		} catch (final IOException e) {
			MLN.printException(this + ": Error in sendUpdatePacket", e);
		}

		ServerSender.createAndSendPacketToPlayer(ds, player);
	}
}