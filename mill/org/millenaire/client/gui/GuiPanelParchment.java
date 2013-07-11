package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.BuildingLocation;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.MillWorld;
import org.millenaire.common.MillWorldInfo.MillMapInfo;
import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;

public class GuiPanelParchment extends GuiText {

	public static final int VILLAGE_MAP = 1;
	public static final int CHUNK_MAP = 2;

	private boolean isParchment=false;
	private int mapType=0;
	private Building townHall=null;

	private final EntityPlayer player;
	private final Vector<Vector<String>> fullText;
	private final Vector<Vector<Line>> fullTextLines;

	public GuiPanelParchment(EntityPlayer player,Vector<Vector<String>> fullText,Building townHall,int mapType, boolean isParchment) {
		super();

		this.mapType=mapType;

		this.townHall=townHall;
		this.isParchment=isParchment;
		this.player=player;
		this.fullText=fullText;
		fullTextLines=null;
	}

	public GuiPanelParchment(EntityPlayer player,Building townHall,Vector<Vector<Line>> fullTextLines,int mapType, boolean isParchment) {
		super();

		this.mapType=mapType;

		this.townHall=townHall;
		this.isParchment=isParchment;
		this.player=player;
		this.fullTextLines=fullTextLines;
		fullText=null;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {

		if (guibutton instanceof MillGuiButton) {

			final MillGuiButton gb=(MillGuiButton)guibutton;


			if (gb.id==MillGuiButton.HELPBUTTON) {
				DisplayActions.displayHelpGUI();
			} else if (gb.id==MillGuiButton.CHUNKBUTTON) {
				DisplayActions.displayChunkGUI(player,player.worldObj);
			} else if (gb.id==MillGuiButton.CONFIGBUTTON) {
				DisplayActions.displayConfigGUI();
			}
		}

		super.actionPerformed(guibutton);
	}

	@Override
	protected void customDrawBackground(int i, int j, float f) {


	}


	@Override
	public void customDrawScreen(int i, int j, float f)
	{
		if ((mapType == VILLAGE_MAP) && (pageNum==0) && (townHall!=null) && (townHall.mapInfo!=null)) {
			drawVillageMap(i,j);
		} else if (mapType==CHUNK_MAP && (pageNum==0)) {
			drawChunkMap(i,j);
		}
	}

	private void drawPixel(int x, int y, int colour) {
		drawGradientRect(x, y, x+1, y+1, colour, colour);
	}

	@Override
	public int getLineSizeInPx() {
		return 195;
	}

	@Override
	public int getPageSize() {
		return 19;
	}
	
	ResourceLocation backgroundParchment=new ResourceLocation(Mill.modId,"/textures/gui/ML_parchment.png");
	ResourceLocation backgroundPanel=new ResourceLocation(Mill.modId,"/textures/gui/ML_panel.png");

	@Override
	public ResourceLocation getPNGPath() {
		if (isParchment)
			return backgroundParchment;
		else
			return backgroundPanel;
	}

	@Override
	public int getXSize() {
		return 204;
	}

	@Override
	public int getYSize() {
		return 220;
	}

	@Override
	public void initData() {
		if (fullText!=null)
			descText=convertAdjustText(fullText);

		if (fullTextLines!=null)
			descText=adjustText(fullTextLines);

		if ((mapType==VILLAGE_MAP) && (townHall!=null)) {
			ClientSender.requestMapInfo(townHall);
		}
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen()
	{

	}

	private void drawVillageMap(int i,int j) {

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		final MillMapInfo minfo=townHall.mapInfo;

		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		final int startX=(getXSize()-minfo.length)/2;
		final int startY=(getYSize()-minfo.width)/2;

		drawGradientRect(startX-2,startY-2, startX+minfo.length+2, startY+minfo.width+2,0x20000000, 0x20000000);

		BuildingLocation locHover=null;
		MillVillager villagerHover=null;


		byte thRegionId=-1;

		if (townHall.pathing!=null) {
			thRegionId=townHall.pathing.thRegion;
		}

		for (int x=0;x<minfo.length;x++) {
			for (int y=0;y<minfo.width;y++) {

				int colour=0;

				final BuildingLocation bl=townHall.getLocationAtCoord(new Point(x+minfo.mapStartX,0,y+minfo.mapStartZ));

				final byte groundType=minfo.data[x][y];

				if (bl != null) {
					if (bl==townHall.buildingLocationIP) {
						colour=0x40ff00ff;
					} else if (bl.level<0) {
						colour=0x40000060;
					} else {
						final Building b=bl.getBuilding(townHall.worldObj);
						if ((b==null) || (thRegionId==-1) || b.isReachableFromRegion(thRegionId)) {
							colour=0x400000ff;
						} else {
							colour=0x40ff0000;
						}
					}
				} else if (groundType==MillMapInfo.WATER) {
					colour=0xaa3030ff;
				} else if (groundType==MillMapInfo.DANGER) {
					colour=0x40ff0000;
				} else if (groundType==MillMapInfo.BUILDING_FORBIDDEN) {
					colour=0x40ffff00;
				} else if (groundType==MillMapInfo.BUILDING_LOC) {
					colour=0x40FF8040;
				} else if (groundType==MillMapInfo.TREE) {
					colour=0x1000ff00;
				} else if (groundType==MillMapInfo.PATH) {
					colour=0x2000ff00;
				} else if (groundType==MillMapInfo.UNREACHABLE) {
					colour=0x40ff5050;
				} else if (groundType==MillMapInfo.UNBUILDABLE){
					colour=0x80000000;
				} else if (groundType==MillMapInfo.OUTOFRANGE) {
					colour=0x4099ff99;
				} else {
					colour=0x4000ff00;
				}

				drawPixel(startX+x,startY+y, colour);

				if (((xStart+startX+x) == i) && ((yStart+startY+y) == j) && (bl != null)) {
					locHover=bl;
				}
			}
		}
		for (final MillVillager villager : townHall.villagers) {

			final int mapPosX=(int) (villager.posX-minfo.mapStartX);
			final int mapPosZ=(int) (villager.posZ-minfo.mapStartZ);

			if ((mapPosX>0) && (mapPosZ>0) && (mapPosX<minfo.length) && (mapPosZ<minfo.width)) {

				if (villager.isChild()) {
					drawGradientRect((startX+mapPosX)-1,(startY+mapPosZ)-1,
							startX+mapPosX+1, startY+mapPosZ+1,0xA0ffff00, 0xA0ffff00);
				} else if ((villager.getRecord()!=null) && villager.getRecord().raidingVillage) {
					drawGradientRect((startX+mapPosX)-1,(startY+mapPosZ)-1,
							startX+mapPosX+1, startY+mapPosZ+1,0xA0000000, 0xA0000000);
				} else if (villager.gender==MillVillager.MALE) {
					drawGradientRect((startX+mapPosX)-1,(startY+mapPosZ)-1,
							startX+mapPosX+1, startY+mapPosZ+1,0xA000ffff, 0xA000ffff);
				} else {
					drawGradientRect((startX+mapPosX)-1,(startY+mapPosZ)-1,
							startX+mapPosX+1, startY+mapPosZ+1,0xA0ff0000, 0xA0ff0000);
				}

				final int screenPosX=xStart+startX+mapPosX;
				final int screenPosY=yStart+startY+mapPosZ;

				if ((screenPosX > (i-2)) && (screenPosX < (i+2)) && (screenPosY > (j-2)) && (screenPosY < (j+2))) {
					villagerHover=villager;
				}
			}

		}

		if (villagerHover != null) {
			int stringlength=fontRenderer.getStringWidth(villagerHover.getName());
			stringlength=Math.max(stringlength,fontRenderer.getStringWidth(villagerHover.getNativeOccupationName()));

			final boolean gameString=((villagerHover.getGameOccupationName(player.username)!=null) && (villagerHover.getGameOccupationName(player.username).length()>0));

			if (gameString) {
				stringlength=Math.max(stringlength, fontRenderer.getStringWidth(villagerHover.getGameOccupationName(player.username)));

				drawGradientRect((i+10) - 3 - xStart, (j+10) - 3 - yStart, (i+10 + stringlength + 3)- xStart, (j+10 + 33) - yStart, 0xc0000000, 0xc0000000);
				fontRenderer.drawString(villagerHover.getName(), (i+10)- xStart, (j+10)- yStart, 0x909090);
				fontRenderer.drawString(villagerHover.getNativeOccupationName(), (i+10)- xStart, ((j+10)- yStart)+11, 0x909090);
				fontRenderer.drawString(villagerHover.getGameOccupationName(player.username), (i+10)- xStart, ((j+10)- yStart)+22, 0x909090);
			} else {
				drawGradientRect((i+10) - 3 - xStart, (j+10) - 3 - yStart, (i+10 + stringlength + 3)- xStart, (j+10 + 22) - yStart, 0xc0000000, 0xc0000000);
				fontRenderer.drawString(villagerHover.getName(), (i+10)- xStart, (j+10)- yStart, 0x909090);
				fontRenderer.drawString(villagerHover.getNativeOccupationName(), (i+10)- xStart, ((j+10)- yStart)+11, 0x909090);
			}
		} else if (locHover != null) {

			final Building b=locHover.getBuilding(townHall.worldObj);

			final boolean unreachable=(b!=null) && (townHall.pathing!=null) && !b.isReachableFromRegion(townHall.pathing.thRegion);

			int stringlength;

			String nativeString;

			if (unreachable) {
				stringlength=fontRenderer.getStringWidth(locHover.getPlan().nativeName+" - "+MLN.string("panels.unreachablebuilding"));
				nativeString=locHover.getPlan().nativeName+" - "+MLN.string("panels.unreachablebuilding");
			} else {
				stringlength=fontRenderer.getStringWidth(locHover.getPlan().nativeName);
				nativeString=locHover.getPlan().nativeName;
			}

			int nblines=1;

			final boolean gameString=((locHover.getPlan().getGameName()!=null) && (locHover.getPlan().getGameName().length()>0));

			if (gameString) {
				stringlength=Math.max(stringlength, fontRenderer.getStringWidth(locHover.getPlan().getGameName()));
				nblines++;
			}

			final Vector<String> effects=locHover.getBuildingEffects(townHall.worldObj);

			nblines+=effects.size();

			for (final String s : effects) {
				stringlength=Math.max(stringlength, fontRenderer.getStringWidth(s));
			}

			drawGradientRect(i - 3 - xStart, j - 3 - yStart, (i + stringlength + 3)- xStart, (j + (11*nblines)) - yStart, 0xc0000000, 0xc0000000);
			fontRenderer.drawString(nativeString, i- xStart, j- yStart, 0x909090);

			int pos=1;

			if (gameString) {
				fontRenderer.drawString(locHover.getPlan().getGameName(), i- xStart, (j- yStart)+11, 0x909090);
				pos++;
			}

			for (final String s : effects) {
				fontRenderer.drawString(s, i- xStart, (j- yStart)+(11*pos), 0x909090);
				pos++;
			}
		}

		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);

	}

	public static final int chunkMapSizeInBlocks=80*16;

	private void drawChunkMap(int i,int j) {

		if (Mill.serverWorlds.isEmpty())
			return;

		final int windowXstart = (width - getXSize()) / 2;
		final int windowYstart = (height - getYSize()) / 2;

		World world=Mill.serverWorlds.firstElement().world;
		MillWorld mw=Mill.serverWorlds.firstElement();

		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		final int startX=(getXSize()-chunkMapSizeInBlocks/8)/2;
		final int startY=(getYSize()-chunkMapSizeInBlocks/8)/2;

		int posXstart=((int)player.chunkCoordX*16-chunkMapSizeInBlocks/2);
		int posZstart=((int)player.chunkCoordZ*16-chunkMapSizeInBlocks/2);


		int mouseX=(i-startX-windowXstart)/2*16+posXstart;
		int mouseZ=(j-startY-windowYstart)/2*16+posZstart;

		drawGradientRect(startX-2,startY-2, startX+chunkMapSizeInBlocks/8+2, startY+chunkMapSizeInBlocks/8+2,0x20000000, 0x20000000);

		Vector<String> labels=new Vector<String>();

		for (int x=posXstart;x<posXstart+chunkMapSizeInBlocks;x+=16) {
			for (int z=posZstart;z<posZstart+chunkMapSizeInBlocks;z+=16) {

				int colour=0;
				if (!world.getChunkProvider().chunkExists(x/16, z/16)) {
					colour=0x40111111;
				} else {
					Chunk chunk=world.getChunkProvider().provideChunk(x/16, z/16);
					if (chunk.isChunkLoaded) {
						colour=0xc000ff00;
					} else {
						colour=0xc0ff0000;
					}
					drawPixel(startX+(x-posXstart)/8,startY+(z-posZstart)/8, colour);

					if (mouseX==x && mouseZ==z)
						labels.add(MLN.string("chunk.chunkcoords",""+x/16+"/"+z/16));
				}

			}
		}

		//copy to avoid ConcurrentModificationException
		Vector<Building> buildings=new Vector<Building>(mw.allBuildings());

		for (Building b : buildings) {
			if (b.isTownhall && b.winfo!=null && b.villageType!=null) {
				for (int x=b.winfo.mapStartX;x<b.winfo.mapStartX+b.winfo.length;x+=16) {
					for (int z=b.winfo.mapStartZ;z<b.winfo.mapStartZ+b.winfo.width;z+=16) {
						if (x>=posXstart && x<=posXstart+chunkMapSizeInBlocks
								&& z>=posZstart && z<=posZstart+chunkMapSizeInBlocks) {
							int colour;

							if (b.villageType.lonebuilding)
								colour=0xf0990099;
							else
								colour=0xf00000ff;

							drawPixel(startX+(x-posXstart)/8+1,startY+(z-posZstart)/8+1, colour);

							if (mouseX==x && mouseZ==z)
								labels.add(MLN.string("chunk.village",b.getVillageQualifiedName()));
						}
					}
				}
			}
		}

		boolean labelForced=false;

		for (ChunkCoordIntPair cc : ForgeChunkManager.getPersistentChunksFor(world).keys()) {
			if (cc.chunkXPos*16>=posXstart && cc.chunkXPos*16<=posXstart+chunkMapSizeInBlocks
					&& cc.chunkZPos*16>=posZstart && cc.chunkZPos*16<=posZstart+chunkMapSizeInBlocks) {
				drawPixel(startX+(cc.chunkXPos*16-posXstart)/8,startY+(cc.chunkZPos*16-posZstart)/8+1, 0xf0ffffff);

				if (mouseX==cc.chunkXPos*16 && mouseZ==cc.chunkZPos*16 && !labelForced) {
					labels.add(MLN.string("chunk.chunkforced"));
					labelForced=true;
				}
			}
		}

		if (!labels.isEmpty()) {
			int stringlength=0;

			for (String s : labels) {
				int w=fontRenderer.getStringWidth(s);
				if (w>stringlength)
					stringlength=w;
			}


			drawGradientRect(i - 3 - windowXstart + 10, j - 3 - windowYstart, (i + stringlength + 3) - windowXstart + 10 , (j + 11*labels.size()) - windowYstart, 0xc0000000, 0xc0000000);

			for (int si=0;si<labels.size();si++) {
				fontRenderer.drawString(labels.get(si), i- windowXstart + 10, j- windowYstart + 11*(si), 0x909090);
			}

		}

		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);

	}


}
