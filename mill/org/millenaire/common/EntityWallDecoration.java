package org.millenaire.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;


public class EntityWallDecoration extends Entity  implements IEntityAdditionalSpawnData {

	public static enum EnumWallDecoration
	{
		Griffon("Griffon", 16, 16, 0, 0, NORMAN_TAPESTRY),
		Oiseau( "Oiseau", 16, 16, 16, 0, NORMAN_TAPESTRY),
		CorbeauRenard( "CorbeauRenard", 2*16, 16, 2*16, 0, NORMAN_TAPESTRY),
		Serment("Serment", 5*16, 3*16, 0, 16, NORMAN_TAPESTRY),
		MortHarold( "MortHarold", 4*16, 3*16, 5*16, 16, NORMAN_TAPESTRY),
		Drakar("Drakar", 6*16, 3*16, 9*16, 16, NORMAN_TAPESTRY),
		MontStMichel("MontStMichel", 3*16, 2*16, 0, 4*16, NORMAN_TAPESTRY),
		Bucherons("Bucherons", 3*16, 2*16, 3*16, 4*16, NORMAN_TAPESTRY),
		Cuisine("Cuisine", 3*16, 2*16, 6*16, 4*16, NORMAN_TAPESTRY),
		Flotte("Flotte", 15*16, 3*16, 0, 6*16, NORMAN_TAPESTRY),
		Chasse( "Chasse", 6*16, 3*16, 0, 9*16, NORMAN_TAPESTRY),
		Siege("Siege", 16*16, 3*16, 0, 12*16, NORMAN_TAPESTRY),

		Ganesh("Ganesh", 2*16, 3*16, 0, 0, INDIAN_STATUE),
		Kali("Kali", 2*16, 3*16, 2*16, 0, INDIAN_STATUE),
		Shiva("Shiva", 2*16, 3*16, 4*16, 0, INDIAN_STATUE),
		Osiyan("Osiyan", 2*16, 3*16, 6*16, 0, INDIAN_STATUE),
		Durga("Durga", 2*16, 3*16, 8*16, 0, INDIAN_STATUE),

		MayanTeal("MayanTeal", 2*16, 2*16, 0, 3*16, MAYAN_STATUE),
		MayanGold("MayanGold", 2*16, 2*16, 2*16, 3*16, MAYAN_STATUE),

		LargeJesus("LargeJesus", 2*16, 3*16, 0, 5*16, BYZANTINE_ICON_LARGE),
		LargeVirgin("LargeVirgin", 2*16, 3*16, 2*16, 5*16, BYZANTINE_ICON_LARGE),
		MediumVirgin1("MediumVirgin1", 2*16, 2*16, 0, 8*16, BYZANTINE_ICON_MEDIUM),
		MediumVirgin2("MediumVirgin2", 2*16, 2*16, 2*16, 8*16, BYZANTINE_ICON_MEDIUM),
		SmallJesus("SmallJesus", 16, 16, 0, 10*16, BYZANTINE_ICON_SMALL),
		SmallVirgin1("SmallVirgin1", 16, 16, 16, 10*16, BYZANTINE_ICON_SMALL),
		SmallVirgin2("SmallVirgin2", 16, 16, 2*16, 10*16, BYZANTINE_ICON_SMALL),
		SmallVirgin3("SmallVirgin3", 16, 16, 3*16, 10*16, BYZANTINE_ICON_SMALL);

		public static final int maxArtTitleLength = "SkullAndRoses".length();

		public final String title;
		public final int sizeX;
		public final int sizeY;
		public final int offsetX;
		public final int offsetY;
		public final int type;
		private EnumWallDecoration(String s1, int j, int k, int l, int i1, int type)
		{
			title = s1;
			sizeX = j;
			sizeY = k;
			offsetX = l;
			offsetY = i1;
			this.type=type;
		}
	}
	public static final int NORMAN_TAPESTRY=1;
	public static final int INDIAN_STATUE=2;

	public static final int MAYAN_STATUE=3;

	public static final int BYZANTINE_ICON_SMALL=4;
	public static final int BYZANTINE_ICON_MEDIUM=5;
	public static final int BYZANTINE_ICON_LARGE=6;

	public static EntityWallDecoration createTapestry(World world, Point p, int type) {
		final int orientation=guessOrientation(world,p);

		if (orientation==0) {
			p=p.getWest();
		} else if (orientation==1) {
			p=p.getSouth();
		} else if (orientation==2) {
			p=p.getEast();
		} else if (orientation==3) {
			p=p.getNorth();
		}

		return new EntityWallDecoration(world,p.getiX(),p.getiY(),p.getiZ(),orientation, type, true);
	}

	public static int guessOrientation(World world, Point p) {
		final int i=p.getiX();
		final int j=p.getiY();
		final int k=p.getiZ();
		if (MillCommonUtilities.isBlockIdSolid(world.getBlockId(i-1, j, k)))
			return 3;
		else if (MillCommonUtilities.isBlockIdSolid(world.getBlockId(i+1, j, k)))
			return 1;
		else if (MillCommonUtilities.isBlockIdSolid(world.getBlockId(i, j, k-1)))
			return 2;
		else if (MillCommonUtilities.isBlockIdSolid(world.getBlockId(i, j, k+1)))
			return 0;
		return 0;
	}

	public int type;

	private int tickCounter1;

	public int direction;

	public int xPosition;


	public int yPosition;

	public int zPosition;
	
	public double clientX,clientY,clientZ;

	public EnumWallDecoration art;

	public EntityWallDecoration(World world)
	{
		super(world);
		tickCounter1 = 0;
		direction = 0;
		yOffset = 0.0F;
		setSize(0.5F, 0.5F);
	}

	public EntityWallDecoration(World world,int type)
	{
		super(world);
		tickCounter1 = 0;
		direction = 0;
		yOffset = 0.0F;
		setSize(0.5F, 0.5F);
		this.type=type;
	}

	public EntityWallDecoration(World world, int x, int y, int z, int orientation, int type, boolean largestPossible)
	{
		this(world,type);
		xPosition = x;
		yPosition = y;
		zPosition = z;

		final ArrayList<EnumWallDecoration> arraylist = new ArrayList<EnumWallDecoration>();
		final EnumWallDecoration aenumart[] = EnumWallDecoration.values();
		int maxSize=0;
		for(final EnumWallDecoration enumart : aenumart) {
			if (enumart.type==type) {
				if (!largestPossible || ((enumart.sizeX*enumart.sizeY)>=maxSize)) {
					art = enumart;
					func_412_b(orientation);
					if(onValidSurface())
					{
						if (largestPossible && ((enumart.sizeX*enumart.sizeY)>maxSize)) {
							arraylist.clear();
						}
						arraylist.add(enumart);
						maxSize=enumart.sizeX*enumart.sizeY;
					}
				}
			}
		}

		if(arraylist.size() > 0)
		{
			art = arraylist.get(rand.nextInt(arraylist.size()));
		}

		if (MLN.LogBuildingPlan>=MLN.MAJOR) {
			MLN.major(this, "Creating wall decoration: "+x+"/"+y+"/"+z+"/"+orientation+"/"+type+"/"+largestPossible+". Result: "+art.title+" picked amoung "+arraylist.size());
		}

		func_412_b(orientation);
	}

	public EntityWallDecoration(World world, int i, int j, int k, int l, int type, String s)
	{
		this(world, type);
		xPosition = i;
		yPosition = j;
		zPosition = k;
		final EnumWallDecoration aenumart[] = EnumWallDecoration.values();
		final int i1 = aenumart.length;
		int j1 = 0;
		do
		{
			if(j1 >= i1)
			{
				break;
			}
			final EnumWallDecoration enumart = aenumart[j1];
			if(enumart.title.equals(s))
			{
				art = enumart;
				break;
			}
			j1++;
		} while(true);
		func_412_b(l);
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	{
		if(!isDead && !worldObj.isRemote)
		{
			setDead();
			setBeenAttacked();
			if (type==NORMAN_TAPESTRY) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.tapestry)));
			} else if (type==INDIAN_STATUE) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.indianstatue)));
			} else if (type==MAYAN_STATUE) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.mayanstatue)));
			} else if (type==BYZANTINE_ICON_SMALL) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconsmall)));
			} else if (type==BYZANTINE_ICON_MEDIUM) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconmedium)));
			} else if (type==BYZANTINE_ICON_LARGE) {
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconlarge)));

			}

		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	protected void entityInit()
	{
	}

	private float func_411_c(int par1)
	{
		if (par1 == 32)
			return 0.5F;

		return par1 != 64 ? 0.0F : 0.5F;
	}

	public void func_412_b(int par1)
	{
		direction = par1;
		prevRotationYaw = rotationYaw = par1 * 90;
		float f = art.sizeX;
		float f1 = art.sizeY;
		float f2 = art.sizeX;

		if ((par1 == 0) || (par1 == 2))
		{
			f2 = 0.5F;
		}
		else
		{
			f = 0.5F;
		}

		f /= 32F;
		f1 /= 32F;
		f2 /= 32F;
		float f3 = xPosition + 0.5F;
		float f4 = yPosition + 0.5F;
		float f5 = zPosition + 0.5F;
		final float f6 = 0.5625F;

		if (par1 == 0)
		{
			f5 -= f6;
		}

		if (par1 == 1)
		{
			f3 -= f6;
		}

		if (par1 == 2)
		{
			f5 += f6;
		}

		if (par1 == 3)
		{
			f3 += f6;
		}

		if (par1 == 0)
		{
			f3 -= func_411_c(art.sizeX);
		}

		if (par1 == 1)
		{
			f5 += func_411_c(art.sizeX);
		}

		if (par1 == 2)
		{
			f3 += func_411_c(art.sizeX);
		}

		if (par1 == 3)
		{
			f5 -= func_411_c(art.sizeX);
		}

		f4 += func_411_c(art.sizeY);
		setPosition(f3, f4, f5);
		final float f7 = -0.00625F;
		boundingBox.setBounds(f3 - f - f7, f4 - f1 - f7, f5 - f2 - f7, f3 + f + f7, f4 + f1 + f7, f5 + f2 + f7);
	}
	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {

		if (MLN.DEV) {
			MLN.major(this, "Type: "+type+", direction: "+direction);
		}

		return super.interact(par1EntityPlayer);
	}
	@Override
	public void onUpdate()
	{
		if((tickCounter1++ == 100) && !worldObj.isRemote)
		{
			tickCounter1 = 0;
			if(!onValidSurface())
			{
				setDead();

				if (type==NORMAN_TAPESTRY) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.tapestry)));
				} else if (type==INDIAN_STATUE) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.indianstatue)));
				} else if (type==MAYAN_STATUE) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.mayanstatue)));
				} else if (type==BYZANTINE_ICON_SMALL) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconsmall)));
				} else if (type==BYZANTINE_ICON_MEDIUM) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconmedium)));
				} else if (type==BYZANTINE_ICON_LARGE) {
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Mill.byzantineiconlarge)));
				}
			}
		}
		
		if (worldObj.isRemote) {
			posX=clientX;
			posY=clientY;
			posZ=clientZ;
		}
	}

	public boolean onValidSurface()
	{
		if (worldObj.getCollidingBoundingBoxes(this, boundingBox).size() > 0)
			return false;

		final int i = art.sizeX / 16;
		final int j = art.sizeY / 16;
		int k = xPosition;
		int l = yPosition;
		int i1 = zPosition;

		if (direction == 0)
		{
			k = MathHelper.floor_double(posX - (art.sizeX / 32F));
		}

		if (direction == 1)
		{
			i1 = MathHelper.floor_double(posZ - (art.sizeX / 32F));
		}

		if (direction == 2)
		{
			k = MathHelper.floor_double(posX - (art.sizeX / 32F));
		}

		if (direction == 3)
		{
			i1 = MathHelper.floor_double(posZ - (art.sizeX / 32F));
		}

		l = MathHelper.floor_double(posY - (art.sizeY / 32F));

		for (int j1 = 0; j1 < i; j1++)
		{
			for (int k1 = 0; k1 < j; k1++)
			{
				Material material;

				if ((direction == 0) || (direction == 2))
				{
					material = worldObj.getBlockMaterial(k + j1, l + k1, zPosition);
				}
				else
				{
					material = worldObj.getBlockMaterial(xPosition, l + k1, i1 + j1);
				}

				if (!material.isSolid())
					return false;
			}
		}

		@SuppressWarnings("rawtypes")
		final
		List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);

		for (int l1 = 0; l1 < list.size(); l1++)
		{
			if (list.get(l1) instanceof EntityPainting)
				return false;
		}

		return true;
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		direction = nbttagcompound.getByte("Dir");
		xPosition = nbttagcompound.getInteger("TileX");
		yPosition = nbttagcompound.getInteger("TileY");
		zPosition = nbttagcompound.getInteger("TileZ");
		type = nbttagcompound.getInteger("Type");
		final String s = nbttagcompound.getString("Motive");

		for(final EnumWallDecoration enumart : EnumWallDecoration.values())
		{
			if(enumart.title.equals(s))
			{
				art = enumart;
			}
		}

		if(art == null)
		{
			art = EnumWallDecoration.Griffon;
		}

		if (type==0) {
			type=NORMAN_TAPESTRY;
		}

		func_412_b(direction);
	}
	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		type=data.readByte();
		direction=data.readByte();
		xPosition=data.readInt();
		yPosition=data.readInt();
		zPosition=data.readInt();

		final String title=data.readUTF();

		for(final EnumWallDecoration enumart : EnumWallDecoration.values())
		{
			if(enumart.title.equals(title))
			{
				art = enumart;
			}
		}
		
		clientX=data.readDouble();
		clientY=data.readDouble();
		clientZ=data.readDouble();
	}

	@Override
	public String toString() {
		return "Tapestry ("+art.title+")";
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setByte("Dir", (byte)direction);
		nbttagcompound.setString("Motive", art.title);
		nbttagcompound.setInteger("TileX", xPosition);
		nbttagcompound.setInteger("TileY", yPosition);
		nbttagcompound.setInteger("TileZ", zPosition);
		nbttagcompound.setInteger("Type", type);
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.write(type);
		data.write(direction);
		data.writeInt(xPosition);
		data.writeInt(yPosition);
		data.writeInt(zPosition);
		data.writeUTF(art.title);
		data.writeDouble(posX);
		data.writeDouble(posY);
		data.writeDouble(posZ);
	}

}
