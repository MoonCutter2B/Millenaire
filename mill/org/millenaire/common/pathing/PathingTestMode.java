package org.millenaire.common.pathing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class PathingTestMode {

	public static void main(String[] args) {

		File file = new File(args[0]);
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

			String s=reader.readLine();
			final PathingPathCalcTile[][][] region=new PathingPathCalcTile[Integer.parseInt(s.split("/")[0])][Integer.parseInt(s.split("/")[1])][Integer.parseInt(s.split("/")[2])];
			s=reader.readLine();//blacnk line
			short j=0,i=0,line=0;
			while ((s=reader.readLine())!=null) {
				line++;
				if (s.length()==0) {
					j++;
					i=0;
					System.out.println("New level on line "+line+": "+j);
				} else {
					for (short k=0;k<s.length();k++) {
						if (s.charAt(k)=='x') {
							region[i][j][k]=new PathingPathCalcTile(false, false, new short[]{i,j,k});
						} else if (s.charAt(k)=='w') {
							region[i][j][k]=new PathingPathCalcTile(true, false, new short[]{i,j,k});
						} else if (s.charAt(k)=='l') {
							region[i][j][k]=new PathingPathCalcTile(true, true, new short[]{i,j,k});
						} else {
							region[i][j][k]=null;
						}
					}
					i++;
				}
			}

			reader.close();	
			
			final PathingSurface surface=new PathingSurface(region, region[region.length/2][region[0].length/2][region[0][0].length/2]);

			System.out.println("Surface loaded.");

			file = new File(args[1]);
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			while ((s=reader.readLine().trim())!=null) {
				if (!s.startsWith("//")) {
					if (s.split(";").length>1) {
						final short[] start=new short[]{(short) Integer.parseInt(s.split(";")[0].split("/")[0]),(short) Integer.parseInt(s.split(";")[0].split("/")[1]),(short) Integer.parseInt(s.split(";")[0].split("/")[2])};
						final short[] end=new short[]{(short) Integer.parseInt(s.split(";")[1].split("/")[0]),(short) Integer.parseInt(s.split(";")[1].split("/")[1]),(short) Integer.parseInt(s.split(";")[1].split("/")[2])};
						System.out.println("Calling getPath for: "+start[0]+"/"+start[1]+"/"+start[2]+" to "+end[0]+"/"+end[1]+"/"+end[2]);

						final long startTime=System.nanoTime();
						final List<short[]> binaryPath=surface.getPath(start,end);
						System.out.println("Time to calculate path (result: "+(binaryPath==null?"null":binaryPath.size())+"): "+(((double)(System.nanoTime()-startTime))/1000000));
					} else {
						final short[] p=new short[]{(short) Integer.parseInt(s.split("/")[0]),(short) Integer.parseInt(s.split("/")[1]),(short) Integer.parseInt(s.split("/")[2])};
						System.out.println("Calling contains for: "+p[0]+"/"+p[1]+"/"+p[2]);

						final long startTime=System.nanoTime();
						final boolean contains=surface.contains(p);
						System.out.println("Time to validate point (result: "+contains+"): "+(((double)(System.nanoTime()-startTime))/1000000));
					}
				}
			}

			reader.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
