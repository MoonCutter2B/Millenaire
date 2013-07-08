package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.millenaire.common.MLN;
import org.millenaire.common.forge.Mill;


public class GuiHelp extends GuiText {

	public static final int NB_CHAPTERS=13;

	int helpDisplayed=1;

	public GuiHelp() {
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{

	}


	@Override
	protected void customDrawBackground(int mouseX, int mouseY, float f) {

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		for (int i=0;i<7;i++) {
			if ((helpDisplayed-1)!=i) {
				final int extraFirstRow=(i==0?1:0);
				drawGradientRect(xStart,(yStart-extraFirstRow)+(32*i)+1, xStart+32, yStart+(32*i)+32,0xa0000000, 0xa0000000);
			}
			if ((helpDisplayed-8)!=i) {
				final int extraFirstRow=(i==0?1:0);
				drawGradientRect(xStart+224,(yStart-extraFirstRow)+(32*i)+1, xStart+32+224, yStart+(32*i)+32,0xa0000000, 0xa0000000);
			}
		}


		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);

	}

	@Override
	protected void customDrawScreen(int mouseX, int mouseY, float f) {

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		mouseX=mouseX-xStart;
		mouseY=mouseY-yStart;

		if ((mouseX>0) && (mouseX<32)) {
			final int pos=(mouseY)/32;
			if ((pos>=0) && (pos<NB_CHAPTERS)) {
				final int stringlength=fontRenderer.getStringWidth(MLN.string("help.tab_"+(pos+1)));
				drawGradientRect((mouseX+10) - 3, (mouseY+10) - 3, (mouseX+10 + stringlength + 3), (mouseY+10 + 14), 0xc0000000, 0xc0000000);
				fontRenderer.drawString(MLN.string("help.tab_"+(pos+1)), mouseX+10, mouseY+10, 0x909090);
			}
		}

		if ((mouseX>224) && (mouseX<256)) {
			final int pos=(mouseY)/32;
			if ((pos>=0) && (pos<(NB_CHAPTERS-7))) {
				final int stringlength=fontRenderer.getStringWidth(MLN.string("help.tab_"+(pos+8)));
				drawGradientRect((mouseX+10) - 3, (mouseY+10) - 3, (mouseX+10 + stringlength + 3), (mouseY+10 + 14), 0xc0000000, 0xc0000000);
				fontRenderer.drawString(MLN.string("help.tab_"+(pos+8)), mouseX+10, mouseY+10, 0x909090);
			}
		}

		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);

	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	@Override
	public int getLineSizeInPx() {
		return 180;
	}

	@Override
	public int getPageSize() {
		return 20;
	}

	ResourceLocation background=new ResourceLocation(Mill.modId,"/graphics/gui/ML_help.png");

	@Override
	public ResourceLocation getPNGPath() {
		return background;
	}

	@Override
	public int getTextXStart() {
		return 36;
	}

	@Override
	public int getXSize() {
		return 256;
	}

	@Override
	public int getYSize() {
		return 224;
	}

	@Override
	public void initData() {

		final Vector<Vector<String>> baseText=MLN.getHelp(helpDisplayed);

		if (baseText!=null) {
			descText=convertAdjustText(baseText);
		} else {
			descText=new Vector<Vector<Line>>();

			final Vector<Line> page=new Vector<Line>();
			page.add(new Line("Il n'y a malheuresement pas d'aide disponible dans votre langue."));
			page.add(new Line(""));
			page.add(new Line("Unfortunately there is no help available in your language."));
			descText.add(page);
			descText=adjustText(descText);
		}
	}


	@Override
	protected void mouseClicked(int mouseX, int mouseY, int k) {
		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		final int ai = mouseX-xStart;
		final int aj = mouseY-yStart;

		if ((aj>(getYSize()-14)) && (aj<getYSize())) {
			if ((ai>36) && (ai<64)) {
				decrementPage();
			} else if ((ai>(getXSize()-64)) && (ai<(getXSize()-36))) {
				incrementPage();
			}
		}

		if ((ai>0) && (ai<32)) {
			int pos=(aj)/32;
			if ((pos>=0) && (pos<NB_CHAPTERS)) {
				pos++;

				pageNum=0;

				if (pos!=helpDisplayed) {
					helpDisplayed=pos;
					initData();
				}
			}
		}

		if ((ai>224) && (ai<256)) {
			int pos=(aj)/32;
			if ((pos>=0) && (pos<(NB_CHAPTERS-7))) {
				pos+=8;

				pageNum=0;

				if (pos!=helpDisplayed) {
					helpDisplayed=pos;
					initData();
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, k);
	}

}

