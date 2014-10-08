package org.millenaire.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMillDecoration extends EntityPainting implements IEntityAdditionalSpawnData {

	public static enum EnumWallDecoration {
		Griffon("Griffon", 16, 16, 0, 0, NORMAN_TAPESTRY), Oiseau("Oiseau", 16, 16, 16, 0, NORMAN_TAPESTRY), CorbeauRenard("CorbeauRenard", 2 * 16, 16, 2 * 16, 0, NORMAN_TAPESTRY), Serment("Serment",
				5 * 16, 3 * 16, 0, 16, NORMAN_TAPESTRY), MortHarold("MortHarold", 4 * 16, 3 * 16, 5 * 16, 16, NORMAN_TAPESTRY), Drakar("Drakar", 6 * 16, 3 * 16, 9 * 16, 16, NORMAN_TAPESTRY), MontStMichel(
				"MontStMichel", 3 * 16, 2 * 16, 0, 4 * 16, NORMAN_TAPESTRY), Bucherons("Bucherons", 3 * 16, 2 * 16, 3 * 16, 4 * 16, NORMAN_TAPESTRY), Cuisine("Cuisine", 3 * 16, 2 * 16, 6 * 16,
				4 * 16, NORMAN_TAPESTRY), Flotte("Flotte", 15 * 16, 3 * 16, 0, 6 * 16, NORMAN_TAPESTRY), Chasse("Chasse", 6 * 16, 3 * 16, 0, 9 * 16, NORMAN_TAPESTRY), Siege("Siege", 16 * 16, 3 * 16,
				0, 12 * 16, NORMAN_TAPESTRY),

		Ganesh("Ganesh", 2 * 16, 3 * 16, 0, 0, INDIAN_STATUE), Kali("Kali", 2 * 16, 3 * 16, 2 * 16, 0, INDIAN_STATUE), Shiva("Shiva", 2 * 16, 3 * 16, 4 * 16, 0, INDIAN_STATUE), Osiyan("Osiyan",
				2 * 16, 3 * 16, 6 * 16, 0, INDIAN_STATUE), Durga("Durga", 2 * 16, 3 * 16, 8 * 16, 0, INDIAN_STATUE),

		MayanTeal("MayanTeal", 2 * 16, 2 * 16, 0, 3 * 16, MAYAN_STATUE), MayanGold("MayanGold", 2 * 16, 2 * 16, 2 * 16, 3 * 16, MAYAN_STATUE),

		LargeJesus("LargeJesus", 2 * 16, 3 * 16, 0, 5 * 16, BYZANTINE_ICON_LARGE), LargeVirgin("LargeVirgin", 2 * 16, 3 * 16, 2 * 16, 5 * 16, BYZANTINE_ICON_LARGE), MediumVirgin1("MediumVirgin1",
				2 * 16, 2 * 16, 0, 8 * 16, BYZANTINE_ICON_MEDIUM), MediumVirgin2("MediumVirgin2", 2 * 16, 2 * 16, 2 * 16, 8 * 16, BYZANTINE_ICON_MEDIUM), SmallJesus("SmallJesus", 16, 16, 0, 10 * 16,
				BYZANTINE_ICON_SMALL), SmallVirgin1("SmallVirgin1", 16, 16, 16, 10 * 16, BYZANTINE_ICON_SMALL), SmallVirgin2("SmallVirgin2", 16, 16, 2 * 16, 10 * 16, BYZANTINE_ICON_SMALL), SmallVirgin3(
				"SmallVirgin3", 16, 16, 3 * 16, 10 * 16, BYZANTINE_ICON_SMALL);

		public static final int maxArtTitleLength = "SkullAndRoses".length();

		public final String title;
		public final int sizeX;
		public final int sizeY;
		public final int offsetX;
		public final int offsetY;
		public final int type;

		private EnumWallDecoration(final String s1, final int j, final int k, final int l, final int i1, final int type) {
			title = s1;
			sizeX = j;
			sizeY = k;
			offsetX = l;
			offsetY = i1;
			this.type = type;
		}
	}

	public static final int NORMAN_TAPESTRY = 1;
	public static final int INDIAN_STATUE = 2;

	public static final int MAYAN_STATUE = 3;

	public static final int BYZANTINE_ICON_SMALL = 4;
	public static final int BYZANTINE_ICON_MEDIUM = 5;
	public static final int BYZANTINE_ICON_LARGE = 6;

	public static EntityMillDecoration createTapestry(final World world, Point p, final int type) {
		final int orientation = guessOrientation(world, p);

		if (orientation == 0) {
			p = p.getWest();
		} else if (orientation == 1) {
			p = p.getSouth();
		} else if (orientation == 2) {
			p = p.getEast();
		} else if (orientation == 3) {
			p = p.getNorth();
		}

		return new EntityMillDecoration(world, p.getiX(), p.getiY(), p.getiZ(), orientation, type, true);
	}

	public static int guessOrientation(final World world, final Point p) {
		final int i = p.getiX();
		final int j = p.getiY();
		final int k = p.getiZ();
		if (MillCommonUtilities.isBlockIdSolid(world.getBlock(i - 1, j, k))) {
			return 3;
		} else if (MillCommonUtilities.isBlockIdSolid(world.getBlock(i + 1, j, k))) {
			return 1;
		} else if (MillCommonUtilities.isBlockIdSolid(world.getBlock(i, j, k - 1))) {
			return 2;
		} else if (MillCommonUtilities.isBlockIdSolid(world.getBlock(i, j, k + 1))) {
			return 0;
		}
		return 0;
	}

	public EnumWallDecoration art;

	public double clientX, clientY, clientZ;

	public int type;

	public EntityMillDecoration(final World par1World) {
		super(par1World);
	}

	public EntityMillDecoration(final World world, final int type) {
		this(world);
		this.type = type;
	}

	public EntityMillDecoration(final World par1World, final int par2, final int par3, final int par4, final int par5) {
		this(par1World);
		this.posX = par2;
		this.posY = par3;
		this.posZ = par4;
	}

	public EntityMillDecoration(final World world, final int x, final int y, final int z, final int orientation, final int type, final boolean largestPossible) {
		this(world, x, y, z, orientation);

		this.type = type;

		posX = x;
		posY = y;
		posZ = z;

		final ArrayList<EnumWallDecoration> arraylist = new ArrayList<EnumWallDecoration>();
		final EnumWallDecoration aenumart[] = EnumWallDecoration.values();
		int maxSize = 0;
		for (final EnumWallDecoration enumart : aenumart) {
			if (enumart.type == type) {
				if (!largestPossible || enumart.sizeX * enumart.sizeY >= maxSize) {
					art = enumart;
					setDirection(orientation);
					if (onValidSurface()) {
						if (largestPossible && enumart.sizeX * enumart.sizeY > maxSize) {
							arraylist.clear();
						}
						arraylist.add(enumart);
						maxSize = enumart.sizeX * enumart.sizeY;
					}
				}
			}
		}

		if (arraylist.size() > 0) {
			art = arraylist.get(rand.nextInt(arraylist.size()));
		}

		if (MLN.LogBuildingPlan >= MLN.MAJOR) {
			MLN.major(this,
					"Creating wall decoration: " + x + "/" + y + "/" + z + "/" + orientation + "/" + type + "/" + largestPossible + ". Result: " + art.title + " picked amoung " + arraylist.size());
		}

		setDirection(orientation);
	}

	public EntityMillDecoration(final World world, final int i, final int j, final int k, final int l, final int type, final String s) {
		super(world, i, j, k, l);
		this.type = type;

		final EnumWallDecoration aenumart[] = EnumWallDecoration.values();
		final int i1 = aenumart.length;
		int j1 = 0;
		do {
			if (j1 >= i1) {
				break;
			}
			final EnumWallDecoration enumart = aenumart[j1];
			if (enumart.title.equals(s)) {
				art = enumart;
				break;
			}
			j1++;
		} while (true);
		setDirection(l);
	}

	/**
	 * Drop the item currently on this item frame.
	 */
	public void dropItemStack() {
		if (type == NORMAN_TAPESTRY) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.tapestry)));
		} else if (type == INDIAN_STATUE) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.indianstatue)));
		} else if (type == MAYAN_STATUE) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.mayanstatue)));
		} else if (type == BYZANTINE_ICON_SMALL) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconsmall)));
		} else if (type == BYZANTINE_ICON_MEDIUM) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconmedium)));
		} else if (type == BYZANTINE_ICON_LARGE) {
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconlarge)));
		}
	}

	public int func_82329_d() {
		return this.art.sizeX;
	}

	public int func_82330_g() {
		return this.art.sizeY;
	}

	@Override
	public void onUpdate() {

		if (worldObj.isRemote) {
			posX = clientX;
			posY = clientY;
			posZ = clientZ;
			motionY = 0;
		}

		super.onUpdate();
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound nbttagcompound) {

		type = nbttagcompound.getInteger("Type");
		final String s = nbttagcompound.getString("Motive");

		for (final EnumWallDecoration enumart : EnumWallDecoration.values()) {
			if (enumart.title.equals(s)) {
				art = enumart;
			}
		}

		if (art == null) {
			art = EnumWallDecoration.Griffon;
		}

		if (type == 0) {
			type = NORMAN_TAPESTRY;
		}

		if (nbttagcompound.hasKey("Direction")) {
			this.hangingDirection = nbttagcompound.getByte("Direction");
		} else {
			switch (nbttagcompound.getByte("Dir")) {
			case 0:
				this.hangingDirection = 2;
				break;
			case 1:
				this.hangingDirection = 1;
				break;
			case 2:
				this.hangingDirection = 0;
				break;
			case 3:
				this.hangingDirection = 3;
			}
		}

		this.posX = nbttagcompound.getInteger("TileX");
		this.posY = nbttagcompound.getInteger("TileY");
		this.posZ = nbttagcompound.getInteger("TileZ");
		this.setDirection(this.hangingDirection);
	}

	@Override
	public void readSpawnData(final ByteBuf ds) {
		final ByteBufInputStream data = new ByteBufInputStream(ds);

		try {
			type = data.readByte();
			hangingDirection = data.readByte();
			posX = data.readInt();
			posY = data.readInt();
			posZ = data.readInt();

			final String title = data.readUTF();

			for (final EnumWallDecoration enumart : EnumWallDecoration.values()) {
				if (enumart.title.equals(title)) {
					art = enumart;
				}
			}

			clientX = data.readDouble();
			clientY = data.readDouble();
			clientZ = data.readDouble();
			data.close();
		} catch (final IOException e) {
			MLN.printException("Exception for villager " + this, e);
		}

	}

	@Override
	public String toString() {
		return "Tapestry (" + art.title + ")";
	}

	@Override
	public void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("Type", type);
		nbttagcompound.setString("Motive", this.art.title);
		nbttagcompound.setByte("Direction", (byte) this.hangingDirection);
		nbttagcompound.setDouble("TileX", this.posX);
		nbttagcompound.setDouble("TileY", this.posY);
		nbttagcompound.setDouble("TileZ", this.posZ);

		switch (this.hangingDirection) {
		case 0:
			nbttagcompound.setByte("Dir", (byte) 2);
			break;
		case 1:
			nbttagcompound.setByte("Dir", (byte) 1);
			break;
		case 2:
			nbttagcompound.setByte("Dir", (byte) 0);
			break;
		case 3:
			nbttagcompound.setByte("Dir", (byte) 3);
		}
	}

	@Override
	public void writeSpawnData(final ByteBuf ds) {
		final ByteBufOutputStream data = new ByteBufOutputStream(ds);
		try {
			data.write(type);
			data.write(hangingDirection);
			data.writeDouble(posX);
			data.writeDouble(posY);
			data.writeDouble(posZ);
			data.writeUTF(art.title);
			data.writeDouble(posX);
			data.writeDouble(posY);
			data.writeDouble(posZ);
			data.close();
		} catch (final IOException e) {
			MLN.printException("Exception for painting " + this, e);
		}
	}
}
