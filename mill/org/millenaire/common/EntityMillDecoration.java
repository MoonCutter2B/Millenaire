package org.millenaire.common;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMillDecoration extends EntityPainting  implements IEntityAdditionalSpawnData
{

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

	public EnumWallDecoration art;

	public double clientX,clientY,clientZ;
	
	public static EntityMillDecoration createTapestry(World world, Point p, int type) {
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

		return new EntityMillDecoration(world,p.getiX(),p.getiY(),p.getiZ(),orientation, type, true);
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
	
	public EntityMillDecoration(World par1World)
    {
        super(par1World);
    }

    public EntityMillDecoration(World par1World, int par2, int par3, int par4, int par5)
    {
        this(par1World);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
    }

	public EntityMillDecoration(World world,int type)
	{
		this(world);
		this.type=type;
	}

	
	
	public EntityMillDecoration(World world, int x, int y, int z, int orientation, int type, boolean largestPossible)
	{
		this(world,x,y,z,orientation);
		
		this.type=type;

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
					setDirection(orientation);
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

		setDirection(orientation);
	}

	public EntityMillDecoration(World world, int i, int j, int k, int l, int type, String s)
	{
		super(world,i,j,k,l);
		this.type=type;


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
		setDirection(l);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setInteger("Type", type);
		nbttagcompound.setString("Motive", this.art.title);
		nbttagcompound.setByte("Direction", (byte)this.hangingDirection);
		nbttagcompound.setInteger("TileX", this.xPosition);
		nbttagcompound.setInteger("TileY", this.yPosition);
		nbttagcompound.setInteger("TileZ", this.zPosition);

        switch (this.hangingDirection)
        {
            case 0:
            	nbttagcompound.setByte("Dir", (byte)2);
                break;
            case 1:
            	nbttagcompound.setByte("Dir", (byte)1);
                break;
            case 2:
            	nbttagcompound.setByte("Dir", (byte)0);
                break;
            case 3:
            	nbttagcompound.setByte("Dir", (byte)3);
        }
	}

		
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
	
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

		if (nbttagcompound.hasKey("Direction"))
        {
            this.hangingDirection = nbttagcompound.getByte("Direction");
        }
        else
        {
            switch (nbttagcompound.getByte("Dir"))
            {
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

        this.xPosition = nbttagcompound.getInteger("TileX");
        this.yPosition = nbttagcompound.getInteger("TileY");
        this.zPosition = nbttagcompound.getInteger("TileZ");
        this.setDirection(this.hangingDirection);
	}

	@Override
	public String toString() {
		return "Tapestry ("+art.title+")";
	}
	


	/**
	 * Drop the item currently on this item frame.
	 */
	public void dropItemStack()
	{
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

	public int func_82329_d()
    {
        return this.art.sizeX;
    }

    public int func_82330_g()
    {
        return this.art.sizeY;
    }
    
    @Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.write(type);
		data.write(hangingDirection);
		data.writeInt(xPosition);
		data.writeInt(yPosition);
		data.writeInt(zPosition);
		data.writeUTF(art.title);
		data.writeDouble(posX);
		data.writeDouble(posY);
		data.writeDouble(posZ);
	}
    
    @Override
	public void readSpawnData(ByteArrayDataInput data) {
		type=data.readByte();
		hangingDirection=data.readByte();
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
	public void onUpdate() {
		
		if (worldObj.isRemote) {
			posX=clientX;
			posY=clientY;
			posZ=clientZ;
			motionY=0;
		}
		
		super.onUpdate();
	}
}
