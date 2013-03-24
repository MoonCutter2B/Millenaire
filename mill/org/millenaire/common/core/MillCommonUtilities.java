package org.millenaire.common.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;

import org.millenaire.common.Building;
import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.MillWorld;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.construction.BuildingPlan.BuildingBlock;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.MillAchievements;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;


public class MillCommonUtilities {

	public static class ExtFileFilter implements FilenameFilter {

		String ext=null;

		public ExtFileFilter(String ext) {
			this.ext=ext;
		}

		@Override
		public boolean accept(File file, String name) {

			if (!name.toLowerCase().endsWith("."+ext))
				return false;

			if (name.startsWith("."))
				return false;

			return true;
		}
	}

	private static class LogThread extends Thread {

		String url;

		public LogThread(String url) {
			this.url=url;
		}

		@Override
		public void run() {
			try {
				final InputStream stream=new URL(url).openStream();
				stream.close();
			} catch (final Exception e) {
			}

		}

	}

	public static class PrefixExtFileFilter implements FilenameFilter {

		String ext=null;
		String prefix=null;

		public PrefixExtFileFilter(String pref,String ext) {
			this.ext=ext;
			prefix=pref;
		}

		@Override
		public boolean accept(File file, String name) {

			if (!name.toLowerCase().endsWith("."+ext))
				return false;

			if (!name.toLowerCase().startsWith(prefix))
				return false;

			if (name.startsWith("."))
				return false;

			return true;
		}
	}

	public static class VillageInfo implements Comparable<VillageInfo> {
		public String textKey;
		public String[] values;
		public int distance;

		@Override
		public int compareTo(VillageInfo arg0) {
			return arg0.distance-distance;
		}
	}

	public static class VillageList {

		public Vector<Point> pos=new Vector<Point>();
		public Vector<String> names=new Vector<String>();
		public Vector<String> types=new Vector<String>();
		public Vector<String> cultures=new Vector<String>();

		public VillageList() {

		}

		public void addVillage(Point p,String name, String type,String culture) {
			pos.add(p);
			names.add(name);
			types.add(type);
			cultures.add(culture);
		}

		public void removeVillage(Point p) {

			int id=-1;

			for (int i=0;(i<pos.size()) && (id==-1);i++) {
				if (p.sameBlock(pos.get(i))) {
					id=i;
				}
			}

			if (id!=-1) {
				pos.remove(id);
				names.remove(id);
				types.remove(id);
				cultures.remove(id);
			}
		}

		public void reset() {
			pos.clear();
			names.clear();
			types.clear();
			cultures.clear();
		}

	}

	public static interface WeightedChoice {

		int getChoiceWeight(EntityPlayer player);

	}

	static public Random random=new Random();

	public static Icon getIcon(IconRegister register,String iconName) {
		return register.registerIcon(Mill.modId+":"+iconName+MLN.getTextSuffix());
	}

	public static boolean canStandInBlock(World world, Point p) {

		if (!AStarStatic.isPassableBlock(world, p.getiX(), p.getiY(), p.getiZ(), MillVillager.DEFAULT_JPS_CONFIG))
			return false;

		p=p.getAbove();

		if (!AStarStatic.isPassableBlock(world, p.getiX(), p.getiY(), p.getiZ(), MillVillager.DEFAULT_JPS_CONFIG))
			return false;

		p=p.getRelative(0, -2, 0);

		if (AStarStatic.isPassableBlock(world, p.getiX(), p.getiY(), p.getiZ(), MillVillager.DEFAULT_JPS_CONFIG))
			return false;

		return true;
	}

	static public boolean chanceOn(int i) {
		return getRandom().nextInt(i)==0;
	}

	static public void changeMoney(IInventory chest,int toChange,EntityPlayer player) {

		boolean hasPurse=false;

		for (int i=0;i<chest.getSizeInventory() && !hasPurse;i++) {
			final ItemStack stack = chest.getStackInSlot(i);
			if (stack !=null) {
				if (stack.itemID == Mill.purse.itemID) {					
					hasPurse=true;
				}
			}
		}

		if (hasPurse) {

			final int current_denier = getItemsFromChest(chest,Mill.denier.itemID,0,Integer.MAX_VALUE);
			final int current_denier_argent = getItemsFromChest(chest,Mill.denier_argent.itemID,0,Integer.MAX_VALUE);
			final int current_denier_or = getItemsFromChest(chest,Mill.denier_or.itemID,0,Integer.MAX_VALUE);

			int finalChange=current_denier_or*64*64+current_denier_argent*64+current_denier+toChange;

			for (int i=0;i<chest.getSizeInventory() && finalChange!=0;i++) {
				final ItemStack stack = chest.getStackInSlot(i);
				if (stack !=null) {
					if (stack.itemID == Mill.purse.itemID) {					
						int content=Mill.purse.totalDeniers(stack)+finalChange;

						if (content>=0) {
							Mill.purse.setDeniers(stack, player, content);
							finalChange=0;
						} else {
							Mill.purse.setDeniers(stack, player, 0);
							finalChange=content;
						}
					}
				}
			}
		} else {

			final int total = toChange+countMoney(chest);

			final int denier = total % 64;
			final int denier_argent = ((total-denier)/64) % 64;
			final int denier_or = (total-denier-(denier_argent*64))/(64*64);

			if (player!=null && denier_or>0) {
				player.addStat(MillAchievements.cresus, 1);
			}

			final int current_denier = countChestItems(chest,Mill.denier.itemID,0);
			final int current_denier_argent = countChestItems(chest,Mill.denier_argent.itemID,0);
			final int current_denier_or = countChestItems(chest,Mill.denier_or.itemID,0);

			if (MLN.WifeAI>=MLN.MAJOR) {
				MLN.major(null, "Putting: "+denier+"/"+denier_argent+"/"+denier_or+" replacing "+current_denier+"/"+current_denier_argent+"/"+current_denier_or);
			}

			if (denier < current_denier) {
				getItemsFromChest(chest,Mill.denier.itemID,0,current_denier-denier);
			} else if (denier > current_denier) {
				putItemsInChest(chest,Mill.denier.itemID,0,denier-current_denier);
			}

			if (denier_argent < current_denier_argent) {
				getItemsFromChest(chest,Mill.denier_argent.itemID,0,current_denier_argent-denier_argent);
			} else if (denier_argent > current_denier_argent) {
				putItemsInChest(chest,Mill.denier_argent.itemID,0,denier_argent-current_denier_argent);
			}

			if (denier_or < current_denier_or) {
				getItemsFromChest(chest,Mill.denier_or.itemID,0,current_denier_or-denier_or);
			} else if (denier_or > current_denier_or) {
				putItemsInChest(chest,Mill.denier_or.itemID,0,denier_or-current_denier_or);
			}

		}
	}

	static public void changeMoneyObsolete(IInventory chest,int toChange,EntityPlayer player) {

		final int total = toChange+countMoney(chest);

		final int denier = total % 64;
		final int denier_argent = ((total-denier)/64) % 64;
		final int denier_or = (total-denier-(denier_argent*64))/(64*64);

		if (player!=null && denier_or>0) {
			player.addStat(MillAchievements.cresus, 1);
		}

		boolean placedInPurse=false;

		for (int i=0;i<chest.getSizeInventory();i++) {
			final ItemStack stack = chest.getStackInSlot(i);
			if (stack !=null) {
				if (stack.itemID == Mill.purse.itemID) {
					if (!placedInPurse) {
						Mill.purse.setDeniers(stack,player, denier, denier_argent, denier_or);
						placedInPurse=true;
					} else {
						Mill.purse.setDeniers(stack,player, 0, 0, 0);
					}
				}
			}
		}

		if (placedInPurse) {
			getItemsFromChest(chest,Mill.denier.itemID,0,Integer.MAX_VALUE);
			getItemsFromChest(chest,Mill.denier_argent.itemID,0,Integer.MAX_VALUE);
			getItemsFromChest(chest,Mill.denier_or.itemID,0,Integer.MAX_VALUE);
		} else {

			final int current_denier = countChestItems(chest,Mill.denier.itemID,0);
			final int current_denier_argent = countChestItems(chest,Mill.denier_argent.itemID,0);
			final int current_denier_or = countChestItems(chest,Mill.denier_or.itemID,0);

			if (MLN.WifeAI>=MLN.MAJOR) {
				MLN.major(null, "Putting: "+denier+"/"+denier_argent+"/"+denier_or+" replacing "+current_denier+"/"+current_denier_argent+"/"+current_denier_or);
			}

			if (denier < current_denier) {
				getItemsFromChest(chest,Mill.denier.itemID,0,current_denier-denier);
			} else if (denier > current_denier) {
				putItemsInChest(chest,Mill.denier.itemID,0,denier-current_denier);
			}

			if (denier_argent < current_denier_argent) {
				getItemsFromChest(chest,Mill.denier_argent.itemID,0,current_denier_argent-denier_argent);
			} else if (denier_argent > current_denier_argent) {
				putItemsInChest(chest,Mill.denier_argent.itemID,0,denier_argent-current_denier_argent);
			}

			if (denier_or < current_denier_or) {
				getItemsFromChest(chest,Mill.denier_or.itemID,0,current_denier_or-denier_or);
			} else if (denier_or > current_denier_or) {
				putItemsInChest(chest,Mill.denier_or.itemID,0,denier_or-current_denier_or);
			}
		}
	}

	public static int countBlocksAround(World world, int x, int y, int z, int rx, int ry, int rz) {
		int counter = 0;

		for (int i = x - rx; i <= (x + rx); i++) {
			for (int j = y - ry; j <= (y + ry); j++) {
				for (int k = z - rz; k <= (z + rz); k++) {
					//if (worldObj.getBlockId(i, j, k) != 0 && worldObj.getBlockId(i, j, k) != Block.snow.blockID)
					if (world.isBlockOpaqueCube(i, j, k)) {
						counter++;
					}
				}
			}
		}

		return counter;

	}

	static public int countChestItems(IInventory chest, int itemID, int meta) {
		if(chest==null)
			return 0;

		int nb=0;

		for (int i=0;i<chest.getSizeInventory();i++) {
			final ItemStack stack = chest.getStackInSlot(i);
			if ((stack !=null) && (stack.itemID == itemID) && ((meta == -1) || (stack.getItemDamage() < 0) || (stack.getItemDamage() == meta))) {
				nb+=stack.stackSize;
			}

		}
		return nb;
	}

	static public int countFurnaceItems(IInventory furnace, int itemID, int meta) {
		if(furnace==null)
			return 0;

		int nb=0;

		final ItemStack stack = furnace.getStackInSlot(2);
		if ((stack !=null) && (stack.itemID == itemID) && ((meta == -1) || (stack.getItemDamage() < 0) || (stack.getItemDamage() == meta))) {
			nb+=stack.stackSize;
		}

		return nb;
	}

	static public int countMoney(IInventory chest) {

		int deniers=0;

		for (int i=0;i<chest.getSizeInventory();i++) {
			final ItemStack stack = chest.getStackInSlot(i);
			if (stack !=null) {
				if (stack.itemID == Mill.purse.itemID)
					deniers+=Mill.purse.totalDeniers(stack);
				else if (stack.itemID == Mill.denier.itemID) {
					deniers+=stack.stackSize;
				} else if (stack.itemID == Mill.denier_argent.itemID) {
					deniers+=stack.stackSize*64;
				} else if (stack.itemID == Mill.denier_or.itemID) {
					deniers+=stack.stackSize*64*64;
				}
			}

		}

		return deniers;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			final String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				final boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
					return false;
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static Point findRandomStandingPosAround(World world, Point dest) {

		if (dest==null)
			return null;

		for (int i=0;i<50;i++) {
			dest=dest.getRelative(5-MillCommonUtilities.randomInt(10), 5-MillCommonUtilities.randomInt(20), 5-MillCommonUtilities.randomInt(10));

			if (isBlockIdSolid(world.getBlockId(dest.getiX(), dest.getiY()-1, dest.getiZ())) &&
					!isBlockIdSolid(world.getBlockId(dest.getiX(), dest.getiY(), dest.getiZ())) &&
					!isBlockIdSolid(world.getBlockId(dest.getiX(), dest.getiY()+1, dest.getiZ())))
				return dest;
		}

		return null;
	}

	public static Point findTopNonPassableBlock(World world, int x, int z) {
		for (int y=255;y>0;y--) {
			if (!AStarStatic.isPassableBlock(world, x, y, z, MillVillager.DEFAULT_JPS_CONFIG))
				return new Point(x,y,z);
		}

		return null;
	}

	public static int findTopSoilBlock(World world, int x, int z) {

		int y = world.getTopSolidOrLiquidBlock(x, z);

		while ((y>-1) && !MillCommonUtilities.isBlockIdGround(world.getBlockId(x, y, z))) {
			y--;
		}

		if (y>254) {
			y=254;
		}

		return y+1;
	}

	public static Point findVerticalStandingPos(World world, Point dest) {

		if (dest==null)
			return null;

		int y=findTopSoilBlock(world,dest.getiX(),dest.getiZ());
		while ((y<250) && (isBlockIdSolid(world.getBlockId(dest.getiX(), y, dest.getiZ())) || isBlockIdSolid(world.getBlockId(dest.getiX(), y+1, dest.getiZ())))) {
			y++;
		}

		if (y==250)
			return null;

		return new Point(dest.getiX(), y, dest.getiZ());
	}

	static public BufferedWriter getAppendWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"UTF8"));
	}

	public static int getBlock(World world,Point p)
	{
		if((p.x < 0xfe17b800) || (p.z < 0xfe17b800) || (p.x >= 0x1e84800) || (p.z > 0x1e84800))
			return -1;
		if(p.y < 0)
			return -1;
		if(p.y >= 256)
			return -1;

		return world.getBlockId(p.getiX(), p.getiY(), p.getiZ());
	}

	public static int getBlockMeta(World world,Point p)
	{
		if((p.x < 0xfe17b800) || (p.z < 0xfe17b800) || (p.x >= 0x1e84800) || (p.z > 0x1e84800))
			return -1;
		if(p.y < 0)
			return -1;
		if(p.y >= 256)
			return -1;

		return world.getBlockMetadata(p.getiX(), p.getiY(), p.getiZ());
	}

	public static Vector<Point> getBlocksAround(World world, int[] blockIds, Point pos,
			int rx, int ry, int rz) {

		final Vector<Point> matches=new Vector<Point>();


		for (int i = pos.getiX() - rx; i <= (pos.getiX() + rx); i++) {
			for (int j = pos.getiY() - ry; j <= (pos.getiY() + ry); j++) {
				for (int k = pos.getiZ() - rz; k <= (pos.getiZ() + rz); k++) {
					for (int l=0;l<blockIds.length;l++)
						if (world.getBlockId(i, j, k) == blockIds[l]) {
							matches.add(new Point(i, j, k));
						}
				}
			}
		}

		return matches;
	}

	public static File getBuildingsDir(World worldObj) {

		final File saveDir = getWorldSaveDir(worldObj);

		final File millenaireDir = new File(saveDir, "millenaire");

		if (!millenaireDir.exists()) {
			millenaireDir.mkdir();
		}

		final File buildingsDir = new File(millenaireDir, "buildings");

		if (!buildingsDir.exists()) {
			buildingsDir.mkdir();
		}

		return buildingsDir;
	}

	public static Point getClosestBlock(World world, int[] blockIds, double x, double y, double z,
			int rx, int ry, int rz) {

		return getClosestBlock(world, blockIds, MathHelper.floor_double(x)
				, MathHelper.floor_double(y), MathHelper.floor_double(z),
				rx, ry, rz);
	}

	public static Point getClosestBlock(World world, int[] blockIds, Point pos,
			int rx, int ry, int rz) {
		return getClosestBlockMeta(world, blockIds, -1, pos,rx,ry,rz);

	}

	public static Point getClosestBlockMeta(World world, int[] blockIds, int meta, Point pos,
			int rx, int ry, int rz) {

		Point closest = null;
		double minDistance = 999999999;

		for (int i = pos.getiX() - rx; i <= (pos.getiX() + rx); i++) {
			for (int j = pos.getiY() - ry; j <= (pos.getiY() + ry); j++) {
				for (int k = pos.getiZ() - rz; k <= (pos.getiZ() + rz); k++) {
					for (int l=0;l<blockIds.length;l++)
						if (world.getBlockId(i, j, k) == blockIds[l]) {
							if ((meta == -1) || (world.getBlockMetadata(i, j, k)==meta)) {

								final Point temp = new Point(i, j, k);

								if ((closest == null)
										|| (temp.distanceTo(pos) < minDistance)) {
									closest = temp;
									minDistance = closest.distanceTo(pos);
								}
							}
						}
				}
			}
		}

		if(minDistance<999999999)
			return closest;
		else
			return null;
	}

	public static EntityItem getClosestItemVertical(World world,Point p,InvItem[] items,int radius,int vertical) {
		final List<Entity> list = getEntitiesWithinAABB(world, Entity.class, p, radius, vertical);

		double bestdist=Double.MAX_VALUE;
		EntityItem citem=null;

		for (final Entity ent : list) {
			if (ent.getClass() == EntityItem.class) {
				final EntityItem item = (EntityItem)ent;

				if (!item.isDead ) {
					for (final InvItem key : items) {
						if ((item.getEntityItem().itemID == key.id()) && (item.getEntityItem().getItemDamage() == key.meta)) {
							final double dist=item.getDistanceSq(p.x, p.y, p.z);
							if (dist < bestdist) {
								bestdist=dist;
								citem=item;
							}
						}
					}
				}
			}
		}

		if (citem==null)
			return null;
		else
			return citem;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public List<Entity> getEntitiesWithinAABB(World world, Class type, Point p, int hradius, int vradius ) {

		final AxisAlignedBB area=AxisAlignedBB.getBoundingBox(p.x, p.y, p.z, p.x + 1.0D, p.y + 1.0D, p.z + 1.0D).expand(hradius, vradius, hradius);
		return world.getEntitiesWithinAABB(type, area);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public List<Entity> getEntitiesWithinAABB(World world, Class type, Point pstart, Point pend ) {

		final AxisAlignedBB area=AxisAlignedBB.getBoundingBox(pstart.x, pstart.y, pstart.z, pend.x, pend.y , pend.z);
		return world.getEntitiesWithinAABB(type, area);
	}

	public static int getInvItemHashTotal(HashMap<InvItem,Integer> map) {
		int total=0;

		for (final InvItem key : map.keySet()) {
			total+=map.get(key);
		}
		return total;
	}


	static public int getItemsFromChest(IInventory chest, int itemID, int meta,int toTake) {
		if(chest==null)
			return 0;

		int nb=0;

		int maxSlot=chest.getSizeInventory()-1;

		if (chest instanceof InventoryPlayer)
		{
			maxSlot=maxSlot-4;//excluding the armour slots
		}

		for (int i=maxSlot;(i>=0) && (nb < toTake);i--) {
			final ItemStack stack = chest.getStackInSlot(i);


			if ((stack !=null) && (stack.itemID == itemID) && ((stack.getItemDamage() == meta) || (meta==-1))) {
				if (stack.stackSize <= (toTake-nb)) {
					nb+=stack.stackSize;
					chest.setInventorySlotContents(i, null);
				} else {
					chest.decrStackSize(i, toTake-nb);
					nb=toTake;
				}
			}
		}

		return nb;
	}

	static public int getItemsFromFurnace(IInventory furnace, int itemID, int toTake) {
		if(furnace==null)
			return 0;

		int nb=0;

		final ItemStack stack = furnace.getStackInSlot(2);
		if ((stack !=null) && (stack.itemID == itemID)) {
			if (stack.stackSize <= (toTake-nb)) {
				nb+=stack.stackSize;
				furnace.setInventorySlotContents(2, null);
			} else {
				furnace.decrStackSize(2, toTake-nb);
				nb=toTake;
			}
		}


		return nb;
	}

	static public int[] getJumpDestination(World world, int x,int y,int z) {

		if (!world.isBlockOpaqueCube(x, y, z) && !world.isBlockOpaqueCube(x, y+1, z))
			return new int[]{x,y,z};

		if (!world.isBlockOpaqueCube(x+1, y, z) && !world.isBlockOpaqueCube(x+1, y+1, z))
			return new int[]{x+1,y,z};

		if (!world.isBlockOpaqueCube(x-1, y, z) && !world.isBlockOpaqueCube(x-1, y+1, z))
			return new int[]{x-1,y,z};

		if (!world.isBlockOpaqueCube(x, y, z+1) && !world.isBlockOpaqueCube(x, y+1, z+1))
			return new int[]{x,y,z+1};

		if (!world.isBlockOpaqueCube(x, y, z-1) && !world.isBlockOpaqueCube(x, y+1, z-1))
			return new int[]{x,y,z-1};

		return null;
	}

	static public int getPointHash(int bid,int meta) {
		return (bid << 8)+meta;
	}

	public static int getPriceColour(int price) {
		if (price >= (64*64))
			return 0xFFE500;

		if (price >= 64)
			return 0xF0F0F0;

		return 0x9E492A;
	}

	public static int getPriceColourMC(int price) {
		if (price >= (64*64))
			return 14;

		if (price >= 64)
			return 15;

		return 6;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getPrivateValue(Class pclass, Object obj, int pos) throws IllegalArgumentException, SecurityException, NoSuchFieldException {

		return ModLoader.getPrivateValue(pclass, obj, pos);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getPrivateValue(Class pclass, Object obj, String nameMCP,String name) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		//	Log.debug(EntityCreature.class.getSimpleName());
		if (EntityCreature.class.getSimpleName().equals("EntityCreature"))
			return ModLoader.getPrivateValue(pclass, obj, nameMCP);
		else
			return ModLoader.getPrivateValue(pclass, obj, name);
	}


	static public Random getRandom() {
		if (random == null) {
			random=new Random();
		}

		return random;
	}

	static public BufferedReader getReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));
	}

	public static String getRelationName(int relation) {

		if (relation>=Building.RELATION_EXCELLENT)
			return "relation.excellent";
		if (relation>=Building.RELATION_VERYGOOD)
			return "relation.verygood";
		if (relation>=Building.RELATION_GOOD)
			return "relation.good";
		if (relation>=Building.RELATION_DECENT)
			return "relation.decent";
		if (relation>=Building.RELATION_FAIR)
			return "relation.fair";

		if (relation<=Building.RELATION_OPENCONFLICT)
			return "relation.openconflict";
		if (relation<=Building.RELATION_ATROCIOUS)
			return "relation.atrocious";
		if (relation<=Building.RELATION_VERYBAD)
			return "relation.verybad";
		if (relation<=Building.RELATION_BAD)
			return "relation.bad";
		if (relation<=Building.RELATION_CHILLY)
			return "relation.chilly";

		return "relation.neutral";
	}

	public static EntityPlayer getServerPlayer(World world,String playerName) {

		for (final Object o : world.playerEntities) {
			if (o instanceof EntityPlayer) {
				final EntityPlayer player=(EntityPlayer)o;

				if (player.username.equals(playerName))
					return player;
			}
		}

		return null;
	}

	public static Vector<EntityPlayer> getServerPlayers(World world) {
		@SuppressWarnings("unchecked")
		final
		Vector<EntityPlayer> players=new Vector<EntityPlayer>(world.playerEntities);
		return players;
	}

	public static UserProfile getServerProfile(World world,String name) {

		final MillWorld mw=Mill.getMillWorld(world);

		if (mw==null) {
			return null;
		}

		return mw.getProfile(name);
	}

	public static String getShortPrice(int price) {

		String res="";

		if (price >= (64*64)) {
			res=(int)Math.floor(price/(64*64))+"o ";
			price=price%(64*64);
		}
		if (price >= 64) {
			res+=(int)Math.floor(price/(64))+"a ";
			price=price%(64);
		}
		if (price>0) {
			res+=price+"d";
		}
		return res.trim();
	}

	public static WeightedChoice getWeightedChoice(@SuppressWarnings("rawtypes") List oChoices,EntityPlayer player) {

		@SuppressWarnings("unchecked")
		final
		List<WeightedChoice> choices=oChoices;

		int weightTotal=0;
		final Vector<Integer> weights=new Vector<Integer>();

		for (final WeightedChoice choice : choices) {
			weightTotal+=choice.getChoiceWeight(player);
			weights.add(choice.getChoiceWeight(player));
		}

		if (weightTotal<1)
			return null;

		final int random=MillCommonUtilities.randomInt(weightTotal);
		int count=0;

		for (int i=0;i<choices.size();i++) {
			count+=weights.get(i);
			if (random < count)
				return choices.get(i);
		}
		return null;
	}

	public static File getWorldSaveDir(World world) {
		final ISaveHandler isavehandler = world.getSaveHandler();

		if (isavehandler instanceof SaveHandler)
			return ((SaveHandler)isavehandler).getSaveDirectory();
		else
			return null;
	}

	static public BufferedWriter getWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
	}

	public static int guessSignMetaData(World world,Point p) {


		boolean northOpen=true,southOpen=true,eastOpen=true,westOpen=true;

		if (isBlockOpaqueCube(getBlock(world, p.getNorth()))) {
			northOpen=false;
		}
		if (isBlockOpaqueCube(getBlock(world, p.getEast()))) {
			eastOpen=false;
		}
		if (isBlockOpaqueCube(getBlock(world, p.getSouth()))) {
			southOpen=false;
		}
		if (isBlockOpaqueCube(getBlock(world, p.getWest()))) {
			westOpen=false;
		}


		if (!eastOpen)
			return 3;
		else if(!westOpen)
			return 2;
		else if(!southOpen)
			return 4;
		else if(!northOpen)
			return 5;
		else
			return 0;

	}

	public static boolean isBlockIdGround(int b) {

		if (b == Block.bedrock.blockID)
			return true;
		else if (b == Block.blockClay.blockID)
			return true;
		else if (b == Block.dirt.blockID)
			return true;
		else if (b == Block.grass.blockID)
			return true;
		else if (b == Block.gravel.blockID)
			return true;
		else if (b == Block.obsidian.blockID)
			return true;
		else if (b == Block.sand.blockID)
			return true;
		else if (b == Block.tilledField.blockID)
			return true;

		return false;
	}

	public static boolean isBlockIdGroundOrCeiling(int b) {

		if (b == Block.stone.blockID)
			return true;
		else if (b == Block.sandStone.blockID)
			return true;

		return false;
	}

	public static boolean isBlockIdLiquid(int b) {
		if (b<1)
			return false;

		if ((b==Block.waterStill.blockID) || (b==Block.waterMoving.blockID) || (b==Block.lavaStill.blockID)
				|| (b==Block.lavaMoving.blockID))
			return true;

		return false;
	}

	public static boolean isBlockIdSolid(int b) {
		if (b<1)
			return false;

		if ((Block.blocksList[b] != null) && Block.blocksList[b].isOpaqueCube())
			return true;
		if ((b==Block.glass.blockID) || (b==Block.thinGlass.blockID) || (b==Block.stoneSingleSlab.blockID)  || (b==Block.woodSingleSlab.blockID) || (b==Block.stairsCobblestone.blockID)
				|| (b==Block.stairsWoodOak.blockID) ||  (b==Block.stairsBrick.blockID)
				|| (b==Block.stairsNetherBrick.blockID) || (b==Block.stairsStoneBrick.blockID)
				|| (b==Block.fenceIron.blockID) || (b==Mill.paperWall.blockID))
			return true;

		return false;
	}

	public static int getBlockIdValidGround(int b,boolean surface) {

		if (b == Block.bedrock.blockID)
			return Block.dirt.blockID;
		else if (b == Block.stone.blockID && surface)
			return Block.dirt.blockID;
		else if (b == Block.stone.blockID && !surface)
			return Block.stone.blockID;
		else if (b == Block.dirt.blockID)
			return Block.dirt.blockID;
		else if (b == Block.grass.blockID)
			return Block.dirt.blockID;
		else if (b == Block.gravel.blockID)
			return Block.gravel.blockID;
		else if (b == Block.sand.blockID)
			return Block.sand.blockID;
		else if (b == Block.sandStone.blockID && surface)
			return Block.sand.blockID;
		else if (b == Block.sandStone.blockID && !surface)
			return Block.sandStone.blockID;

		return -1;
	}

	public static boolean isBlockOpaqueCube(int blockId)
	{
		final Block block = Block.blocksList[blockId];
		if(block == null)
			return false;
		else
			return block.isOpaqueCube();
	}

	static public String[] limitSignText(String[] lines) {
		for (int i=0;i<lines.length;i++) {
			String s=lines[i];
			if (s!=null) {
				if (s.length()>15) {
					s=s.substring(0, 15);
				}
			} else {
				s="";
			}
			lines[i]=s;
		}
		return lines;
	}

	public static void logInstance(World world) {

		long uid=0;

		if (!Mill.proxy.isTrueServer()) {
			final String login=Mill.proxy.getClientProfile().playerName;

			if (login.startsWith("Player")) {
				uid=-1;
			} else {
				uid=login.hashCode();
			}
		} else {
			uid=world.getWorldInfo().getSeed();
		}

		final String os=System.getProperty("os.name");

		String mode;

		if (Mill.proxy.isTrueServer()) {
			mode="s";
		} else {
			if (Mill.isDistantClient()) {
				mode="c";
			} else {
				mode="l";
			}
		}

		int totalexp=0;

		if (Mill.proxy.isTrueServer()) {
			if (!Mill.serverWorlds.isEmpty()) {
				for (final UserProfile p : Mill.serverWorlds.get(0).profiles.values()) {
					for (final Culture c : Culture.vectorCultures) {
						totalexp+=Math.abs(p.getCultureReputation(c.key));
					}
				}
			}

		} else {

			final UserProfile p=Mill.proxy.getClientProfile();
			if (p!=null) {
				for (final Culture c : Culture.vectorCultures) {
					totalexp+=Math.abs(p.getCultureReputation(c.key));
				}
			}

		}


		String lang="";

		if (MLN.mainLanguage!=null) {
			lang=MLN.mainLanguage.language;
		}

		int nbplayers=1;

		if (Mill.proxy.isTrueServer() && !Mill.serverWorlds.isEmpty()) {
			nbplayers=Mill.serverWorlds.get(0).profiles.size();
		}

		String url="http://millenaire.org/php/mlnuse.php?uid="+uid+"&mlnversion="+Mill.versionNumber
				+"&mode="+mode+"&lang="+lang+"&backuplang="+MLN.fallback_language+"&nbplayers="+nbplayers+"&os="+os+"&totalexp="+totalexp;

		url=url.replaceAll(" ", "%20");

		MLN.logPerformed=true;

		(new LogThread(url)).start();
	}

	public static void notifyBlock(World world,Point p)
	{
		world.notifyBlockChange(p.getiX()+1, p.getiY(), p.getiZ(), 0);
	}



	static public int[] packLong(long nb) {
		return new int[]{(int) (nb >> 32),(int) nb};
	}

	public static void playSound(World world,Point p,String sound, float volume, float pitch) {
		world.playSoundEffect(((float)p.x + 0.5F), ((float)p.y + 0.5F), ((float)p.z + 0.5F), sound, (volume+1)/2, pitch*0.8f);
	}

	public static void playSoundBlockBreaking(World world,Point p,Block b,float volume) {
		if ((b!=null) && (b.stepSound != null)) {
			playSound(world,p,b.stepSound.getBreakSound(),b.stepSound.stepSoundVolume*volume,b.stepSound.stepSoundPitch);
		}
	}

	public static void playSoundBlockPlaced(World world,Point p,Block b,float volume) {
		if ((b!=null) && (b.stepSound != null)) {
			playSound(world,p,b.stepSound.getPlaceSound(),b.stepSound.stepSoundVolume*volume,b.stepSound.stepSoundPitch);
		}
	}

	public static void playSoundByMillName(World world,Point p,String soundMill,float volume) {
		if (soundMill.equals("metal")) {
			playSoundBlockPlaced(world,p,Block.blockSteel,volume);
		} else if (soundMill.equals("wood")) {
			playSoundBlockPlaced(world,p,Block.wood,volume);
		} else if (soundMill.equals("wool")) {
			playSoundBlockPlaced(world,p,Block.cloth,volume);
		} else if (soundMill.equals("glass")) {
			playSoundBlockPlaced(world,p,Block.glass,volume);
		} else if (soundMill.equals("stone")) {
			playSoundBlockPlaced(world,p,Block.stone,volume);
		} else {
			MLN.printException("Tried to play unknown sound: "+soundMill, new Exception());
		}

	}

	static public boolean probability(double probability) {
		return getRandom().nextDouble()<probability;
	}

	static public int putItemsInChest(IInventory chest, int item, int toPut) {
		return putItemsInChest(chest,item,0,toPut);
	}

	static public int putItemsInChest(IInventory chest, int item, int meta, int toPut) {

		if(chest==null)
			return 0;

		int nb=0;


		int maxSlot=chest.getSizeInventory();

		if (chest instanceof InventoryPlayer)
		{
			maxSlot=maxSlot-4;//excluding the armour slots
		}


		for (int i=0;(i<maxSlot) && (nb < toPut);i++) {
			final ItemStack stack = chest.getStackInSlot(i);
			if ((stack != null) && (stack.itemID == item) && (stack.getItemDamage()==meta)) {
				//Log.major(chest, Log.TileEntityBuilding, "stack.getMaxStackSize(): "+stack.getMaxStackSize()+", stack.stackSize: "+stack.stackSize);
				if ((stack.getMaxStackSize()-stack.stackSize) >= (toPut-nb)) {
					stack.stackSize+=(toPut-nb);
					nb=toPut;
				} else {
					nb+=stack.getMaxStackSize()-stack.stackSize;
					stack.stackSize=stack.getMaxStackSize();
				}
			}
		}

		for (int i=0;(i<maxSlot) && (nb < toPut);i++) {
			ItemStack stack = chest.getStackInSlot(i);
			if (stack ==null) {
				stack=new ItemStack(item,1,meta);
				if ((toPut-nb) <= stack.getMaxStackSize()) {
					stack.stackSize=toPut-nb;
					nb=toPut;
				} else {
					stack.stackSize=stack.getMaxStackSize();
					nb+=stack.stackSize;
				}
				chest.setInventorySlotContents(i, stack);
			}
		}
		return nb;
	}

	static public int randomInt(int i) {
		return getRandom().nextInt(i);
	}

	static public long randomLong() {
		return getRandom().nextLong();
	}

	public static int readInteger(String line) throws Exception {
		int res=1;
		for (final String s : line.trim().split("\\*")) {
			res*=Integer.parseInt(s);
		}
		return res;
	}

	public static boolean setBlock(World world,Point p, int blockId)
	{
		return setBlock(world,p,blockId,true, false);
	}

	public static boolean setBlock(World world,Point p, int bid, boolean notify, boolean playSound)
	{
		if((p.x < 0xfe17b800) || (p.z < 0xfe17b800) || (p.x >= 0x1e84800) || (p.z > 0x1e84800))
			return false;
		if(p.y < 0)
			return false;
		if(p.y >= 256)
			return false;

		if (playSound && (bid==0)) {
			final int oldBid=world.getBlockId(p.getiX(), p.getiY(), p.getiZ());

			if (oldBid>0) {
				final Block block = Block.blocksList[oldBid];
				if ((block!=null) && (block.stepSound!=null)) {
					playSoundBlockBreaking(world,p,block,2.0f);
				}
			}
		}

		if (notify) {
			world.setBlock(p.getiX(), p.getiY(), p.getiZ(), bid,0,3);
		} else {
			world.setBlock(p.getiX(), p.getiY(), p.getiZ(), bid,0,2);
		}

		if (playSound && (bid>0)) {
			final Block block = Block.blocksList[bid];
			if ((block!=null) && (block.stepSound!=null)) {
				playSoundBlockPlaced(world,p,block,2.0f);
			}
		}

		return true;
	}

	public static boolean setBlockAndMetadata(World world,Point p,int bid, int metadata)
	{
		return setBlockAndMetadata(world,p,bid,metadata,true,false);
	}

	public static boolean setBlockAndMetadata(World world,int x,int y,int z,int bid, int metadata, boolean notify, boolean playSound)
	{
		if((x < 0xfe17b800) || (z < 0xfe17b800) || (x >= 0x1e84800) || (z > 0x1e84800))
			return false;
		if(y < 0)
			return false;
		if(y >= 256)
			return false;

		if (playSound && (bid==0)) {
			final int oldBid=world.getBlockId(x,y,z);

			if (oldBid>0) {
				final Block block = Block.blocksList[oldBid];
				if ((block!=null) && (block.stepSound!=null)) {
					playSoundBlockBreaking(world,new Point(x,y,z),block,2.0f);
				}

			}
		}

		if (notify) {
			world.setBlock(x,y,z, bid, metadata,3);
		} else {
			world.setBlock(x,y,z, bid, metadata,2);
		}

		if (playSound && (bid>0)) {
			final Block block = Block.blocksList[bid];
			if ((block!=null) && (block.stepSound!=null)) {
				playSoundBlockPlaced(world,new Point(x,y,z),block,2.0f);
			}
		}

		return true;
	}

	public static boolean setBlockAndMetadata(World world,Point p,int bid, int metadata, boolean notify, boolean playSound)
	{
		return setBlockAndMetadata(world,p.getiX(),p.getiY(),p.getiZ(),bid,metadata,notify,playSound);
	}

	public static boolean setBlockMetadata(World world,Point p, int metadata)
	{
		return setBlockMetadata(world,p,metadata,true);
	}

	public static boolean setBlockMetadata(World world,int x,int y,int z, int metadata, boolean notify)
	{
		if((x < 0xfe17b800) || (z < 0xfe17b800) || (x >= 0x1e84800) || (z > 0x1e84800))
			return false;
		if(y < 0)
			return false;
		if(y >= 256)
			return false;

		if (notify) {
			world.setBlockMetadataWithNotify(x,y,z, metadata,3);
		} else {
			world.setBlockMetadataWithNotify(x,y,z, metadata,2);
		}

		return true;
	}

	public static boolean setBlockMetadata(World world,Point p, int metadata, boolean notify)
	{

		return setBlockMetadata(world,p.getiX(),p.getiY(),p.getiZ(),metadata,notify);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setPrivateValue(Class pclass, Object obj, String nameMCP,String name, Object value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		//	Log.debug(EntityCreature.class.getSimpleName());
		if (EntityCreature.class.getSimpleName().equals("EntityCreature")) {
			ModLoader.setPrivateValue(pclass, obj, nameMCP, value);
		} else {
			ModLoader.setPrivateValue(pclass, obj, name, value);
		}
	}

	static public void spawnExp(World world, Point p, int nb) {

		for(int j = nb; j > 0;)
		{
			final int l = EntityXPOrb.getXPSplit(j);
			j -= l;
			world.spawnEntityInWorld(new EntityXPOrb(world, p.x+0.5, p.y+5, p.z+0.5, l));
		}

	}

	static  public EntityItem spawnItem(World world, Point p, ItemStack itemstack, float f) {
		final EntityItem entityitem = new EntityItem(world, p.x, p.y + f, p.z, itemstack);
		entityitem.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(entityitem);
		return entityitem;
	}

	static public void spawnMobsAround(World world, Point p,int radius,String mobType,int minNb,int extraNb) {

		int nb=minNb;
		if (extraNb>0) {
			nb+=MillCommonUtilities.randomInt(extraNb);
		}

		for(int i = 0; i < nb; i++)
		{
			final EntityLiving entityliving = (EntityLiving)EntityList.createEntityByName(mobType, world);
			if(entityliving == null) {
				continue;
			}

			boolean spawned=false;

			for (int j=0;(j<20) && !spawned;j++) {
				final double ex = p.x + (((world.rand.nextDouble()*2)-1) * radius);
				final double ey = (p.y + world.rand.nextInt(3)) - 1;
				final double ez = p.z + (((world.rand.nextDouble()*2)-1) * radius);
				entityliving.setLocationAndAngles(ex, ey, ez, world.rand.nextFloat() * 360F, 0.0F);

				if(entityliving.getCanSpawnHere()) {
					world.spawnEntityInWorld(entityliving);
					MLN.major(null, "Entering world: "+entityliving.getClass().getName());
					spawned=true;
				}
			}

			if (!spawned) {
				MLN.major(null, "No valid space found.");
			}

			entityliving.spawnExplosionParticle();
		}
	}



	static public void spawnMobsSpawner(World world, Point p,String mobType) {

		final EntityLiving entityliving = (EntityLiving)EntityList.createEntityByName(mobType, world);
		if(entityliving == null)
			return;

		final int x=MillCommonUtilities.randomInt(2)-1;
		final int z=MillCommonUtilities.randomInt(2)-1;


		final int ex = (int) (p.x + x);
		final int ey = (int) p.y;
		final int ez = (int) (p.z + z);

		if ((world.getBlockId(ex, ey, ez)>0) && (world.getBlockId(ex, ey+1, ez)>0))
			return;

		entityliving.setLocationAndAngles(ex, ey, ez, world.rand.nextFloat() * 360F, 0.0F);

		world.spawnEntityInWorld(entityliving);

		entityliving.spawnExplosionParticle();
	}

	public static String swapConfigBlockId(String s) {
		if (s.equals("block_wood_id")) {
			s=""+Mill.wood_decoration.blockID;
		} else if (s.equals("block_earth_id")) {
			s=""+Mill.earth_decoration.blockID;
		} else if (s.equals("block_stone_id")) {
			s=""+Mill.stone_decoration.blockID;
		} else if (s.equals("block_crops_id")) {
			s=""+Mill.crops.blockID;
		} else if (s.equals("block_panes_id")) {
			s=""+Mill.paperWall.blockID;
		} else if (s.equals("block_byzantine_tile_id")) {
			s=""+Mill.byzantine_tiles.blockID;
		} else if (s.equals("block_byzantine_tile_slab_id")) {
			s=""+Mill.byzantine_tile_slab.blockID;
		} else if (s.equals("block_byzantine_stone_tiles_id")) {
			s=""+Mill.byzantine_stone_tiles.blockID;
		} else if (s.equals("block_path_id")) {
			s=""+Mill.path.blockID;
		}
		return s;
	}

	static public long unpackLong(int nb1, int nb2) {

		return (long) nb1 << 32 | (nb2 & 0xFFFFFFFFL);
	}

	static public void generateHearts(Entity ent) {
		for (int var3 = 0; var3 < 7; ++var3)
		{
			double var4 = random.nextGaussian() * 0.02D;
			double var6 = random.nextGaussian() * 0.02D;
			double var8 = random.nextGaussian() * 0.02D;

			ent.worldObj.spawnParticle("heart", ent.posX + (double)(random.nextFloat() * ent.width * 2.0F) - (double)ent.width, ent.posY + 0.5D + (double)(random.nextFloat() * ent.height), ent.posZ + (double)(random.nextFloat() * ent.width * 2.0F) - (double)ent.width, var4, var6, var8);
		}
	}

	private static boolean attemptPathBuild(Building th,World world,Vector<BuildingBlock> pathPoints,Point p,int pathBid,int pathMeta) {
		int bid=p.getId(world);
		int bidAbove=p.getAbove().getId(world);
		int bidBelow=p.getBelow().getId(world);
		
		if (th.isPointProtectedFromPathBuilding(p))
			return false;

		if (p.getAbove().isBlockPassable(world) && (canPathBeBuiltHere(bid))) {
			pathPoints.add(new BuildingBlock(p,pathBid,pathMeta));
			return true;
		} if (p.getRelative(0,2,0).isBlockPassable(world) && canPathBeBuiltHere(bidAbove) && canPathBeBuiltHere(bid)) {
			pathPoints.add(new BuildingBlock(p,pathBid,pathMeta));
			pathPoints.add(new BuildingBlock(p.getAbove(),0,0));
			return true;
		} if (p.isBlockPassable(world) && p.getAbove().isBlockPassable(world) && canPathBeBuiltHere(bidBelow) ) {
			pathPoints.add(new BuildingBlock(p,pathBid,pathMeta));
			MLN.temp(null, "raising path at: "+p);
			return true;
		} 
		return false;
	}

	public static boolean canPathBeBuiltHere(int bid) {
		return (bid==Block.dirt.blockID || bid==Block.grass.blockID || bid==Block.sand.blockID || bid==Block.gravel.blockID || bid==Mill.path.blockID);
	}


	public static Vector<BuildingBlock> buildPath(Building th,ArrayList<AStarNode> path,int pathBid,int pathMeta,int pathWidth) {

		Vector<BuildingBlock> pathPoints=new Vector<BuildingBlock>();

		Point lastPoint=null;

		for (AStarNode node : path) {

			Point p=(new Point(node)).getBelow();
			
			attemptPathBuild(th,th.worldObj,pathPoints,p,pathBid,pathMeta);

			if (lastPoint!=null) {
				int dx=p.getiX()-lastPoint.getiX();
				int dz=p.getiZ()-lastPoint.getiZ();

				for (int i=0;i<2;i++) {
					
					//backward then forward (diagonals only)
					int direction=(i==0)?1:-1;
					
					Point secondPoint=null,secondPointAlternate=null,thirdPoint=null;

					//if path has width of 2, double-it when straight and attempt to triple it in diagonals
					if (pathWidth>1) {
						if (dx==0 && direction==1) {
							secondPoint=p.getRelative(direction, 0, 0);
							secondPointAlternate=p.getRelative(-direction, 0, 0);
						} else if (dz==0 && direction==1) {
							secondPoint=p.getRelative(0, 0, direction);
							secondPointAlternate=p.getRelative(0, 0, -direction);
						} else {
							secondPoint=p.getRelative(dx*direction, 0, 0);
							thirdPoint=p.getRelative(0, 0, dz*direction);
						}
					} else {//if path has width of one, double it in diagonals only
						if (dx!=0 && dz!=0) {
							secondPoint=p.getRelative(dx*direction, 0, 0);
							secondPointAlternate=p.getRelative(0, 0, dz*direction);
						}
					}

					if (secondPoint!=null) {
						boolean success=attemptPathBuild(th,th.worldObj,pathPoints,secondPoint,pathBid,pathMeta);

						if (!success && secondPointAlternate!=null)
							attemptPathBuild(th,th.worldObj,pathPoints,secondPointAlternate,pathBid,pathMeta);
					}

					if (thirdPoint!=null)
						attemptPathBuild(th,th.worldObj,pathPoints,thirdPoint,pathBid,pathMeta);
				}
			}	

			lastPoint=p;
		}

		return pathPoints;

	}

}
