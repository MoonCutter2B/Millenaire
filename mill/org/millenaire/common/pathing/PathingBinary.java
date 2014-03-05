package org.millenaire.common.pathing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.World;

import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class PathingBinary {

	public static class CachedPath {
		PathKey key;
		List<PathPoint> path;

		CachedPath(List<PathPoint> path) {
			this.path=path;
			key=new PathKey(path.get(0),path.get(path.size()-1));
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final CachedPath other = (CachedPath) obj;
			return (key.equals(other.key));
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}
	public static class PathKey {
		static boolean log=false;
		PathPoint start,end;
		int hash;

		PathKey(PathPoint start, PathPoint end) {
			this.start=start;
			this.end=end;
			hash=(start.hashCode()+(end.hashCode()<<16));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj==this)
				return true;
			if (!(obj instanceof PathKey))
				return false;
			final PathKey p=(PathKey) obj;
			return ((hash==p.hash) && start.equals(p.start) && end.equals(p.end));
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public String toString() {
			return start+", "+end;
		}
	}
	private PathingSurface surface;
	private int surfaceXstart,surfaceYstart,surfaceZstart;

	private final World world;

	private final HashMap<PathKey,CachedPath> cache;

	private static final String EOL = System.getProperty("line.separator");

	public PathingBinary(World world) {
		this.world=world;
		cache=new HashMap<PathKey,CachedPath>();
	}

	public List<PathPoint> calculatePath(Building building, MillVillager villager, Point start,Point dest,boolean extraLog) throws Exception {

		long startTime=System.nanoTime();

		final PathKey key=new PathKey(start.getPathPoint(),dest.getPathPoint());

		if (cache.containsKey(key)) {
			if (cache.get(key)==null)
				return null;
			return cache.get(key).path;
		}

		short[] startCoord=new short[]{(short) (start.getiX()-surfaceXstart), (short) (building.getAltitude(start.getiX(),start.getiZ())-surfaceYstart-1), (short) (start.getiZ()-surfaceZstart)};
		short[] endCoord=new short[]{(short) (dest.getiX()-surfaceXstart), (short) (building.getAltitude(dest.getiX(),dest.getiZ())-surfaceYstart-1), (short) (dest.getiZ()-surfaceZstart)};

		startCoord=validatePoint(startCoord);
		if (startCoord==null) {
			if (MLN.LogConnections>=MLN.MAJOR) {
				MLN.major(this, "No valid start found from "+start+" to "+dest+" for "+villager+": "+(((double)(System.nanoTime()-startTime))/1000000));
			}
			cache.put(key, null);
			return null;
		}

		endCoord=validatePoint(endCoord);
		if (endCoord==null) {
			if (MLN.LogConnections>=MLN.MAJOR) {
				MLN.major(this, "No valid dest found from "+start+" to "+dest+" for "+villager+": "+(((double)(System.nanoTime()-startTime))/1000000));
			}
			cache.put(key, null);
			return null;
		}

		if (MLN.LogConnections>=MLN.MAJOR) {
			MLN.major(this,"Time to find start and end: "+(((double)(System.nanoTime()-startTime))/1000000));
		}
		startTime=System.nanoTime();

		if (MLN.DEV) {
			final File file = new File(Mill.proxy.getBaseDir(),"paths_"+this.hashCode()+".txt");
			final FileWriter writer=new FileWriter(file,true);
			writer.write(startCoord[0]+"/"+startCoord[1]+"/"+startCoord[2]+";"+endCoord[0]+"/"+endCoord[1]+"/"+endCoord[2]+EOL);
			writer.flush();
			writer.close();
		}

		final List<short[]> binaryPath=surface.getPath(startCoord,endCoord);

		if (MLN.DEV) {
			final File file = new File(Mill.proxy.getBaseDir(),"paths_"+this.hashCode()+".txt");
			final FileWriter writer=new FileWriter(file,true);
			writer.write("//result of getPath: "+(binaryPath==null?"null":binaryPath.size())+" time: "+(((double)(System.nanoTime()-startTime))/1000000)+EOL);
			writer.flush();
			writer.close();
		}

		if (MLN.LogConnections>=MLN.MAJOR) {
			MLN.major(this,"Time to calculate path from "+start+" to "+dest+" for "+villager+" with binary pathing (result: "+(binaryPath==null?"null":binaryPath.size())+"): "+(((double)(System.nanoTime()-startTime))/1000000));
		}

		if ((binaryPath==null) || (binaryPath.size()==0)) {
			cache.put(key, null);
			return null;
		}

		final Vector<PathPoint> path=new Vector<PathPoint>();

		for (final short[] p : binaryPath) {
			path.add(new PathPoint(p[0]+surfaceXstart,p[1]+surfaceYstart+1,p[2]+surfaceZstart));
		}

		cache.put(key, new CachedPath(path));

		for (int i=0;i<(path.size()-1);i++) {
			cache.put(new PathKey(path.get(i),path.get(path.size()-1)), new CachedPath(path.subList(i, path.size())));
		}

		return path;
	}

	public boolean isReady() {
		return (surface!=null);
	}

	public void updatePathing(Point centre, int hradius, int vradius) {
		long startTime = System.nanoTime();

		final PathingPathCalcTile[][][] region=new PathingPathCalcTile[hradius*2][vradius*2][hradius*2];

		surfaceXstart=centre.getiX()-hradius;
		surfaceYstart=centre.getiY()-vradius;
		surfaceZstart=centre.getiZ()-hradius;

		for (short i=0;i<region.length;i++) {
			for (short j=0;j<region[0].length;j++) {
				for (short k=0;k<region[0][0].length;k++) {
					final Block block=world.getBlock(surfaceXstart+i, surfaceYstart+j, surfaceZstart+k);
					//if (bid==Blocks.ladder.blockID) {
					//	region[i][j][k]=new MLPathingPathCalcTile(true, true, new short[]{i,j,k});
					//} else
					if ((block==Blocks.flowing_water) || (block==Blocks.water) || (block==Blocks.lava) || (block==Blocks.flowing_lava) || (block==Blocks.fence)) {
						region[i][j][k]=new PathingPathCalcTile(false, false, new short[]{i,j,k});
					} else if ((block==Blocks.air) || !block.isBlockNormalCube()) {
						region[i][j][k]=null;
					} else {
						region[i][j][k]=new PathingPathCalcTile(true, false, new short[]{i,j,k});
					}
				}
			}
		}

		if (MLN.LogConnections>=MLN.MAJOR) {
			MLN.major(this,"Time to generate region: "+(((double)(System.nanoTime()-startTime))/1000000));
		}

		startTime = System.nanoTime();

		surface=new PathingSurface(region, region[hradius][vradius][hradius]);

		if (MLN.LogConnections>=MLN.MAJOR) {
			MLN.major(this,"Time taken to compute surface: "+(((double)(System.nanoTime()-startTime))/1000000));
		}

		if (MLN.DEV) {

			final File file = new File(Mill.proxy.getBaseDir(),"region_"+this.hashCode()+".txt");

			try {
				final BufferedWriter writer=MillCommonUtilities.getWriter(file);
				writer.write(region.length+"/"+region[0].length+"/"+region[0][0].length+EOL);

				for (short j=0;j<region[0].length;j++) {
					writer.write(EOL);
					for (short i=0;i<region.length;i++) {
						String s="";
						for (short k=0;k<region[0][0].length;k++) {
							if (region[i][j][k]==null) {
								s+="-";
							} else if (region[i][j][k].ladder) {
								s+="l";
							} else if (region[i][j][k].isWalkable) {
								s+="w";
							} else {
								s+="x";
							}
						}
						writer.write(s+EOL);
					}
				}
				writer.flush();
				writer.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		cache.clear();
	}

	private short[] validatePoint(short[] p) {

		if (MLN.LogConnections>=MLN.MAJOR) {
			MLN.major(this, "Validating point: "+p[0]+"/"+p[1]+"/"+p[2]);
		}

		writeLine(p[0]+"/"+p[1]+"/"+p[2]);
		boolean res=surface.contains(p);
		writeLine("//result of surface.contains: "+res);

		if (res)
			return p;


		final short[] newP=new short[]{p[0],p[1],p[2]};

		for (short i=10;i>-10;i--) {
			newP[1]=(short) (p[1]+i);
			if (MLN.DEV) {
				writeLine(newP[0]+"/"+newP[1]+"/"+newP[2]);
			}
			res=surface.contains(newP);
			writeLine("//result of surface.contains: "+res);

			if (res) {
				if (MLN.LogConnections>=MLN.MAJOR) {
					MLN.major(this, "Found valid point. offset: "+i);
				}
				return newP;
			}
		}
		for (short i=-2;i<3;i++) {
			newP[0]=(short) (p[0]+i);
			for (short j=-2;j<3;j++) {
				newP[2]=(short) (p[2]+j);
				if ((i!=0) || (j!=0)) {
					for (short k=10;k>-10;k--) {
						//let's check that the block is solid and the two above are not:
						if (MillCommonUtilities.isBlockIdSolid(world.getBlock(p[0]+i, p[1]+k, p[2]+j))
								&& !MillCommonUtilities.isBlockIdSolid(world.getBlock(p[0]+i, p[1]+k+1, p[2]+j))
								&& !MillCommonUtilities.isBlockIdSolid(world.getBlock(p[0]+i, p[1]+k+2, p[2]+j))) {

							newP[1]=(short) (p[1]+k);
							if (MLN.DEV) {
								writeLine(newP[0]+"/"+newP[1]+"/"+newP[2]);
								res=surface.contains(newP);
								writeLine("//result of surface.contains: "+res);
							}

							if (res) {
								if (MLN.LogConnections>=MLN.MAJOR) {
									MLN.major(this, "Found valid point. offset: "+i+"/"+k+"/"+j);
								}
								return newP;
							}
						}
					}
				}
			}
		}
		return null;

	}

	private void writeLine(String s) {
		try {
			final File file = new File(Mill.proxy.getBaseDir(),"paths_"+this.hashCode()+".txt");
			final FileWriter writer=new FileWriter(file,true);
			writer.write(s+EOL);
			writer.flush();
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
