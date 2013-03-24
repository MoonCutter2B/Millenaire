package org.millenaire.common;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.pathing.AStarPathing.Point2D;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;

public class Point {

	public static final Point read(NBTTagCompound nbttagcompound,String label) {
		double x,y,z;
		x=nbttagcompound.getDouble(label+"_xCoord");
		y=nbttagcompound.getDouble(label+"_yCoord");
		z=nbttagcompound.getDouble(label+"_zCoord");

		if ((x==0) && (y==0) && (z==0))
			return null;

		return new Point(x,y,z);
	}

	public final double x,y,z;

	public Point(AStarNode node) {
		x=node.x;
		y=node.y;
		z=node.z;
	}

	public Point(double i,double j,double k) {
		x=i;
		y=j;
		z=k;
	}

	public Point(Entity ent) {
		x=ent.posX;
		y=ent.posY;
		z=ent.posZ;
	}

	public Point(PathPoint pp) {
		x=pp.xCoord;
		y=pp.yCoord;
		z=pp.zCoord;
	}

	public Point(String s) {
		final String[] scoord=s.split("/");
		x=Double.parseDouble(scoord[0]);
		y=Double.parseDouble(scoord[1]);
		z=Double.parseDouble(scoord[2]);
	}

	public String approximateDistanceLongString(Point p) {
		int dist=(int) distanceTo(p);

		if (dist<950)
			return (Math.round(dist/100)*100)+" "+MLN.string("other.metre");
		else {
			dist=Math.round(dist/500);
			if ((dist%2)==0)
				return (dist/2)+" "+MLN.string("other.kilometre");
			else
				return ((dist-1)/2)+MLN.string("other.andhalf")+" "+MLN.string("other.kilometre");
		}
	}

	public String approximateDistanceShortString(Point p) {
		int dist=(int) distanceTo(p);

		if (dist<950)
			return (Math.round(dist/100)*100)+"m";
		else {
			dist=Math.round(dist/500);
			if ((dist%2)==0)
				return (dist/2)+"km";
			else
				return ((dist-1)/2)+".5 km";
		}
	}

	public String directionTo(Point p) {
		return directionTo(p,false);
	}

	public String directionTo(Point p,boolean prefixed) {
		String direction,prefix;

		if (prefixed) {
			prefix="other.tothe";
		} else {
			prefix="other.";
		}

		final int xdist=MathHelper.floor_double(p.x-x);
		final int zdist=MathHelper.floor_double(p.z-z);

		if (((Math.abs(xdist) > (Math.abs(zdist)*0.6)) && (Math.abs(xdist) < (Math.abs(zdist)*1.4)))
				|| ((Math.abs(zdist) > (Math.abs(xdist)*0.6)) && (Math.abs(zdist) < (Math.abs(xdist)*1.4)))) {

			if (zdist > 0) {
				direction=prefix+"south"+"-";
			} else {
				direction=prefix+"north"+"-";
			}

			if (xdist > 0) {
				direction+="east";
			} else {
				direction+="west";
			}
		} else {
			if (Math.abs(xdist) > Math.abs(zdist)) {
				if (xdist > 0) {
					direction=prefix+"east";
				} else {
					direction=prefix+"west";
				}
			} else {
				if (zdist > 0) {
					direction=prefix+"south";
				} else {
					direction=prefix+"north";
				}
			}
		}

		return direction;
	}

	public String directionToShort(Point p) {
		String direction;

		final int xdist=MathHelper.floor_double(p.x-x);
		final int zdist=MathHelper.floor_double(p.z-z);

		if (((Math.abs(xdist) > (Math.abs(zdist)*0.6)) && (Math.abs(xdist) < (Math.abs(zdist)*1.4)))
				|| ((Math.abs(zdist) > (Math.abs(xdist)*0.6)) && (Math.abs(zdist) < (Math.abs(xdist)*1.4)))) {

			if (zdist > 0) {
				direction=MLN.string("other.south_short");
			} else {
				direction=MLN.string("other.north_short");
			}

			if (xdist > 0) {
				direction+=MLN.string("other.east_short");
			} else {
				direction+=MLN.string("other.west_short");
			}
		} else {
			if (Math.abs(xdist) > Math.abs(zdist)) {
				if (xdist > 0) {
					direction=MLN.string("other.east_short");
				} else {
					direction=MLN.string("other.west_short");
				}
			} else {
				if (zdist > 0) {
					direction=MLN.string("other.south_short");
				} else {
					direction=MLN.string("other.north_short");
				}
			}
		}

		return direction;
	}

	public String distanceDirectionShort(Point p) {
		return MLN.string("other.directionshort",directionToShort(p),""+(int)horizontalDistanceTo(p)+"m");
	}

	public double distanceTo(double px,double py, double pz)
	{
		final double d = px - x;
		final double d1 = py - y;
		final double d2 = pz - z;
		return MathHelper.sqrt_double((d * d) + (d1 * d1) + (d2 * d2));
	}

	public double distanceTo(Entity e) {
		return distanceTo(e.posX,e.posY,e.posZ);
	}

	public double distanceTo(Point p)
	{
		if (p==null)
			return -1;

		return distanceTo(p.x,p.y,p.z);
	}

	public double distanceToSquared(double px,double py, double pz)
	{
		final double d = px - x;
		final double d1 = py - y;
		final double d2 = pz - z;
		return ((d * d) + (d1 * d1) + (d2 * d2));
	}

	public double distanceToSquared(Entity e) {
		return distanceToSquared(e.posX,e.posY,e.posZ);
	}

	public double distanceToSquared(PathPoint pp) {
		return distanceToSquared(pp.xCoord,pp.yCoord,pp.zCoord);
	}

	public double distanceToSquared(Point p)
	{
		return distanceToSquared(p.x,p.y,p.z);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;

		if (!(o instanceof Point))
			return false;

		final Point p = (Point)o;

		return ((p.x==x) && (p.y==y) && (p.z==z));
	}

	public Point getAbove() {
		return new Point(x,y+1,z);
	}

	public Point getBelow() {
		return new Point(x,y-1,z);
	}

	public Block getBlock(World world) {
		return Block.blocksList[world.getBlockId(getiX(), getiY(), getiZ())];
	}

	public TileEntityBrewingStand getBrewingStand(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityBrewingStand))
			return (TileEntityBrewingStand)ent;

		return null;
	}

	public TileEntityChest getChest(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityChest))
			return (TileEntityChest)ent;

		return null;
	}

	public String getChunkString() {
		return getChunkX()+"/"+getChunkZ();
	}

	public int getChunkX() {
		return getiX() >> 4;
	}

	public int getChunkZ() {
		return getiZ() >> 4;
	}

	public TileEntityDispenser getDispenser(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityDispenser))
			return (TileEntityDispenser)ent;

		return null;
	}

	public Point getEast() {
		return new Point(x,y,z-1);
	}

	public TileEntityFurnace getFurnace(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityFurnace))
			return (TileEntityFurnace)ent;

		return null;
	}

	public int getId(World world) {
		return world.getBlockId(getiX(), getiY(), getiZ());
	}

	public String getIntString() {
		return getiX()+"/"+getiY()+"/"+getiZ();
	}

	public int getiX() {
		return MathHelper.floor_double(x);
	}

	public int getiY() {
		return MathHelper.floor_double(y);
	}

	public int getiZ() {
		return MathHelper.floor_double(z);
	}

	public int getMeta(World world) {
		return world.getBlockMetadata(getiX(), getiY(), getiZ());
	}

	public TileEntityMillChest getMillChest(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityMillChest))
			return (TileEntityMillChest)ent;

		return null;
	}

	public Point getNorth() {
		return new Point(x-1,y,z);
	}

	public Point2D getP2D() {
		return new Point2D(getiX(),getiZ());
	}

	public TileEntityPanel getPanel(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntityPanel))
			return (TileEntityPanel)ent;

		return null;
	}

	public short[] getPathingCoord(int xoffset,int yoffset,int zoffset) {
		return new short[]{(short) (x-xoffset),(short) (y-yoffset),(short) (z-zoffset)};
	}


	public PathPoint getPathPoint() {
		return new PathPoint((int)x,(int)y,(int)z);
	}

	public String getPathString() {
		return getiX()+"_"+getiY()+"_"+getiZ();
	}

	public Point getRelative(double dx,double dy,double dz) {
		return new Point(x+dx,y+dy,z+dz);
	}

	public TileEntitySign getSign(World world) {
		final TileEntity ent=world.getBlockTileEntity(getiX(),getiY() , getiZ());

		if ((ent != null) && (ent instanceof TileEntitySign))
			return (TileEntitySign)ent;

		return null;
	}

	public Point getSouth() {
		return new Point(x+1,y,z);
	}

	public TileEntity getTileEntity(World world) {
		return world.getBlockTileEntity(getiX(),getiY() , getiZ());
	}

	public Point getWest() {
		return new Point(x,y,z+1);
	}

	@Override
	public int hashCode() {
		return (int)(x+((int)y<<8)+((int)z<<16));
	}

	public double horizontalDistanceTo(ChunkCoordinates cc) {
		return horizontalDistanceTo(cc.posX,cc.posZ);
	}

	public double horizontalDistanceTo(double px, double pz)
	{
		final double d = px - x;
		final double d2 = pz - z;
		return MathHelper.sqrt_double((d * d) + (d2 * d2));
	}

	public double horizontalDistanceTo(Entity e) {
		return horizontalDistanceTo(e.posX,e.posZ);
	}

	public double horizontalDistanceTo(PathPoint p)
	{
		if (p==null)
			return 0;

		return horizontalDistanceTo(p.xCoord,p.zCoord);
	}

	public double horizontalDistanceTo(Point p)
	{
		if (p==null)
			return 0;

		return horizontalDistanceTo(p.x,p.z);
	}

	public double horizontalDistanceToSquared(double px, double pz)
	{
		final double d = px - x;
		final double d2 = pz - z;
		return ((d * d) + (d2 * d2));
	}

	public double horizontalDistanceToSquared(Entity e) {
		return horizontalDistanceToSquared(e.posX,e.posZ);
	}

	public double horizontalDistanceToSquared(Point p)
	{
		return horizontalDistanceTo(p.x,p.z);
	}

	public boolean isBlockPassable(World world) {
		return AStarStatic.isPassableBlock(world, getiX(), getiY(), getiZ(), MillVillager.DEFAULT_JPS_CONFIG);
	}

	public int manhattanDistance(Point p) {
		return (int) (Math.abs(x-p.x)+Math.abs(z-p.z));
	}

	public boolean sameBlock(PathPoint p) {
		if (p==null)
			return false;

		return ((getiX()==p.xCoord) &&  (getiY()==p.yCoord) && (getiZ()==p.zCoord));
	}

	public boolean sameBlock(Point p) {
		if (p==null)
			return false;

		return ((getiX()==p.getiX()) &&  (getiY()==p.getiY()) && (getiZ()==p.getiZ()));
	}

	public boolean sameHorizontalBlock(PathPoint p) {
		if (p==null)
			return false;

		return ((getiX()==p.xCoord) && (getiZ()==p.zCoord));
	}

	public boolean sameHorizontalBlock(Point p) {
		if (p==null)
			return false;

		return ((getiX()==p.getiX()) && (getiZ()==p.getiZ()));
	}

	public void setBlock(World world,int bid,int meta,boolean notify,boolean sound) {
		MillCommonUtilities.setBlockAndMetadata(world, this, bid, meta, notify, sound);
	}

	public int squareRadiusDistance(Point p) {
		return (int) (Math.max(Math.abs(x-p.x),Math.abs(z-p.z)));
	}

	@Override
	public String toString() {
		return (Math.round(x*100)/100)+"/"+(Math.round(y*100)/100)+"/"+(Math.round(z*100)/100);
	}

	public void write(NBTTagCompound nbttagcompound,String label) {
		nbttagcompound.setDouble(label+"_xCoord", x);
		nbttagcompound.setDouble(label+"_yCoord", y);
		nbttagcompound.setDouble(label+"_zCoord", z);
	}

}
