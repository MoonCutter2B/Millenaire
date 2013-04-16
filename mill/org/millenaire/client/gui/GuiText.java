package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.millenaire.common.MLN;

public abstract class GuiText extends GuiScreen {

	public static class Line {


		String text="";
		MillGuiButton[] buttons=null;
		MillGuiTextField textField=null;
		Vector<ItemStack> icons=null;
		boolean canCutAfter=true;
		boolean shadow=false;
		int margin=0;

		public Line () {
		}

		public Line (boolean canCutAfter) {
			this.canCutAfter=canCutAfter;
		}

		public Line (MillGuiButton b) {
			buttons=new MillGuiButton[]{b};
			canCutAfter=false;
		}

		public Line (MillGuiTextField tf) {
			textField=tf;
		}

		public Line (String s,MillGuiTextField tf) {
			textField=tf;
			if (s==null) {
				text="";
			} else {
				text=s;
				interpretTags();
			}
		}

		public Line (MillGuiButton b,MillGuiButton b2) {
			buttons=new MillGuiButton[]{b,b2};
			canCutAfter=false;
		}

		public Line (MillGuiButton b,MillGuiButton b2,MillGuiButton b3) {
			buttons=new MillGuiButton[]{b,b2,b3};
			canCutAfter=false;
		}

		public Line (Vector<ItemStack> icons,String s) {
			this.icons=icons;
			text=s;
			canCutAfter=false;

			if (icons!=null)
				margin=icons.size()*18;
		}

		public Line (String s) {
			if (s==null) {
				text="";
			} else {
				text=s;
				interpretTags();
			}
		}

		public Line (String s,boolean canCutAfter) {
			if (s==null) {
				text="";
			} else {
				text=s;
				interpretTags();
			}
			this.canCutAfter=canCutAfter;
		}

		public Line (String s,Line model,boolean first) {

			if (first)
				icons=model.icons;

			if (s==null) {
				text="";
			} else {
				text=s;
				interpretTags();
			}
			canCutAfter=model.canCutAfter;
			shadow=model.shadow;
			margin=model.margin;
		}

		public boolean empty() {
			return (text=="") && (buttons==null) && (textField==null);
		}

		private void interpretTags() {
			if (text.startsWith("<shadow>")) {
				shadow=true;
				text=text.replaceAll("<shadow>", "");
			}
			text=text.replaceAll(BLACK, "\2470");
			text=text.replaceAll(DARKBLUE, "\2471");
			text=text.replaceAll(DARKGREEN, "\2472");
			text=text.replaceAll(LIGHTBLUE, "\2473");
			text=text.replaceAll(DARKRED, "\2474");
			text=text.replaceAll(PURPLE, "\2475");
			text=text.replaceAll(ORANGE, "\2476");
			text=text.replaceAll(LIGHTGREY, "\2477");
			text=text.replaceAll(DARKGREY, "\2478");
			text=text.replaceAll(BLUE, "\2479");
			text=text.replaceAll(LIGHTGREEN, "\247a");
			text=text.replaceAll(CYAN, "\247b");
			text=text.replaceAll(LIGHTRED, "\247c");
			text=text.replaceAll(PINK, "\247d");
			text=text.replaceAll(YELLOW, "\247e");
			text=text.replaceAll(WHITE, "\247f");

		}
	}

	public static class MillGuiTextField extends GuiTextField {

		public final String fieldKey;

		public MillGuiTextField(FontRenderer par1FontRenderer, int par2,
				int par3, int par4, int par5,String fieldKey) {
			super(par1FontRenderer, par2, par3, par4, par5);
			this.fieldKey=fieldKey;
		}
	}

	public static class MillGuiButton extends GuiButton {


		public static final int HELPBUTTON = 2000;
		public static final int CHUNKBUTTON = 3000;
		public static final int CONFIGBUTTON = 4000;

		public MillGuiButton(int par1, int par2, int par3, int par4, int par5,
				String par6Str) {
			super(par1, par2, par3, par4, par5, par6Str);
		}

		public MillGuiButton(String label,int id) {
			super(id,0,0,0,0,label);
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public void setHeight(int h) {
			height=h;
		}

		public void setWidth(int w) {
			width=w;
		}

	}
	public static final String WHITE = "<white>";
	public static final String YELLOW = "<yellow>";
	public static final String PINK = "<pink>";
	public static final String LIGHTRED = "<lightred>";
	public static final String CYAN = "<cyan>";
	public static final String LIGHTGREEN = "<lightgreen>";
	public static final String BLUE = "<blue>";
	public static final String DARKGREY = "<darkgrey>";
	public static final String LIGHTGREY = "<lightgrey>";
	public static final String ORANGE = "<orange>";
	public static final String PURPLE = "<purple>";
	public static final String DARKRED = "<darkred>";
	public static final String LIGHTBLUE = "<lightblue>";
	public static final String DARKGREEN = "<darkgreen>";

	public static final String DARKBLUE = "<darkblue>";

	public static final String BLACK = "<black>";

	public static final String LINE_HELP_GUI_BUTTON = "<help_gui_button>";
	public static final String LINE_CHUNK_GUI_BUTTON = "<chunk_gui_button>";
	public static final String LINE_CONFIG_GUI_BUTTON = "<config_gui_button>";
	protected int pageNum=0;

	protected Vector<Vector<Line>> descText=null;

	Vector<MillGuiTextField> textFields=new Vector<MillGuiTextField>();


	/** Stacks renderer. Icons, stack size, health, etc... */
	protected static RenderItem itemRenderer = new RenderItem();

	public GuiText() {

	}


	public Vector<Vector<Line>> adjustText(Vector<Vector<Line>> baseText) {
		final Vector<Vector<Line>> text=new Vector<Vector<Line>>();

		for (final Vector<Line> page : baseText) {

			Vector<Line> newPage=new Vector<Line>();

			for (final Line line : page) {
				if (line.buttons!=null || line.textField!=null) {
					newPage.add(line);
				} else {
					for (String l : line.text.split("<ret>")) {

						int lineSize=getLineSizeInPx()-line.margin;
						int lineSizeInChar=getLineWidthInChars(lineSize);

						boolean first=true;

						while (fontRenderer.getStringWidth(l)>lineSize) {
							int end = l.lastIndexOf(' ', lineSizeInChar);
							if (end<1) {
								end=lineSizeInChar;
							}
							if (end>=l.length()) {
								end=l.length()/2;
							}
							final String subLine=l.substring(0, end);
							l=l.substring(subLine.length()).trim();

							final int colPos=subLine.lastIndexOf('\247');

							if (colPos>-1) {//carrying over an open colour tag
								l=subLine.substring(colPos,colPos+2)+l;
							}

							newPage.add(new Line(subLine,line,first));

							first=false;
						}
						newPage.add(new Line(l,line,first));
					}
				}
			}

			while (newPage.size()>getPageSize()) {
				Vector<Line> newPage2=new Vector<Line>();

				int nblinetaken=0;

				for (int i=0;i<getPageSize();i++) {

					int blockSize=-1;

					for (int j=i;(j<newPage.size()) && (blockSize==-1);j++) {
						if (newPage.get(j).canCutAfter) {
							blockSize=j-i;
						}
					}

					if (blockSize==-1) {
						blockSize=newPage.size()-i;
					}

					if ((i+blockSize)>(getPageSize())) {
						break;
					}

					newPage2.add(newPage.get(i));
					nblinetaken++;
				}
				for (int i=0;i<nblinetaken;i++) {
					newPage.removeElementAt(0);
				}

				newPage2=clearEmptyLines(newPage2);

				if (newPage2!=null) {
					text.add(newPage2);
				}
			}

			newPage=clearEmptyLines(newPage);

			if (newPage!=null) {
				text.add(newPage);
			}
		}

		return text;
	}

	@SuppressWarnings("unchecked")
	public void buttonPagination() {

		if (descText==null)
			return;


		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		buttonList.clear();
		textFields.clear();

		int vpos=6;

		if (pageNum<descText.size()) {
			for (int cp=0;(cp<getPageSize()) && (cp<descText.get(pageNum).size());cp++) {

				final Line line=descText.get(pageNum).get(cp);
				if (line.buttons!=null) {

					if (line.buttons.length==1) {
						if (line.buttons[0]!=null) {
							line.buttons[0].xPosition=(xStart+(getXSize() / 2))-100;
							line.buttons[0].setWidth(200);
						}
					} else if (line.buttons.length==2) {
						if (line.buttons[0]!=null) {
							line.buttons[0].xPosition=(xStart+(getXSize() / 2))-100;
							line.buttons[0].setWidth(95);
						}
						if (line.buttons[1]!=null) {
							line.buttons[1].xPosition=xStart+(getXSize() / 2) + 5;
							line.buttons[1].setWidth(95);
						}
					} else if (line.buttons.length==3) {
						if (line.buttons[0]!=null) {
							line.buttons[0].xPosition=(xStart+(getXSize() / 2))-100;
							line.buttons[0].setWidth(60);
						}
						if (line.buttons[1]!=null) {
							line.buttons[1].xPosition=(xStart+(getXSize() / 2)) -30;
							line.buttons[1].setWidth(60);
						}
						if (line.buttons[2]!=null) {
							line.buttons[2].xPosition=xStart+(getXSize() / 2) +40;
							line.buttons[2].setWidth(60);
						}
					}

					for (int i=0;i<line.buttons.length;i++) {
						if (line.buttons[i]!=null) {
							line.buttons[i].yPosition=yStart+vpos;
							line.buttons[i].setHeight(20);
							buttonList.add(line.buttons[i]);
						}
					}
				} else if (line.textField!=null) {
					MillGuiTextField textField=new MillGuiTextField(fontRenderer,xStart+(getXSize() / 2) +40,yStart+vpos,95,20,line.textField.fieldKey);
					textField.setText(line.textField.getText());
					textField.setMaxStringLength(line.textField.getMaxStringLength());
					textField.setTextColor(-1);
					line.textField=textField;
					line.textField.func_82266_h(-1);
					line.textField.setEnableBackgroundDrawing(false);

					textFields.add(textField);
				}
				vpos+=10;
			}
		}
	}

	private Vector<Line> clearEmptyLines(Vector<Line> page) {
		final Vector<Line> clearedPage=new Vector<Line>();

		boolean nonEmptyLine=false;

		for (final Line line : page) {
			if (!line.empty()) {
				clearedPage.add(line);
				nonEmptyLine=true;
			} else {
				if (nonEmptyLine) {
					clearedPage.add(line);
				}
			}
		}

		if (clearedPage.size()>0)
			return clearedPage;
		else
			return null;

	}
	protected void closeWindow() {
		mc.displayGuiScreen(null);
		mc.setIngameFocus();
	}
	public Vector<Vector<Line>> convertAdjustText(Vector<Vector<String>> baseText) {

		final Vector<Vector<Line>> text=new Vector<Vector<Line>>();

		for (final Vector<String> page : baseText ) {

			final Vector<Line> newPage = new Vector<Line>();

			for (final String s : page) {
				if (s.equals(LINE_HELP_GUI_BUTTON)) {
					newPage.add(new Line(new MillGuiButton(MillGuiButton.HELPBUTTON, 0, 0, 0, 0, MLN.string("ui.helpbutton"))));
				} else if (s.equals(LINE_CHUNK_GUI_BUTTON)) {
					newPage.add(new Line(new MillGuiButton(MillGuiButton.CHUNKBUTTON, 0, 0, 0, 0, MLN.string("ui.chunkbutton"))));
				} else if (s.equals(LINE_CONFIG_GUI_BUTTON)) {
					newPage.add(new Line(new MillGuiButton(MillGuiButton.CONFIGBUTTON, 0, 0, 0, 0, MLN.string("ui.configbutton"))));
				} else {
					newPage.add(new Line(s,true));
				}
			}

			text.add(newPage);
		}

		return adjustText(text);
	}
	protected abstract void customDrawBackground(int i, int j, float f);

	protected abstract void customDrawScreen(int i, int j, float f);

	public void decrementPage() {

		if (descText==null)
			return;

		if (pageNum>0) {
			pageNum--;
		}
		buttonPagination();
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {

		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(getPNGPath());
		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;
		drawTexturedModalRect(xStart, yStart, 0, 0, getXSize(), getYSize());

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		customDrawBackground(i,j,f);

		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef(xStart, yStart, 0.0F);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896 /*GL_LIGHTING*/);
		GL11.glDisable(2929 /*GL_DEPTH_TEST*/);

		if (descText!=null) {
			int vpos=6;

			if (pageNum<descText.size()) {
				for (int cp=0;(cp<getPageSize()) && (cp<descText.get(pageNum).size());cp++) {



					if (descText.get(pageNum).get(cp).shadow) {
						fontRenderer.drawStringWithShadow(descText.get(pageNum).get(cp).text, getTextXStart()+
								descText.get(pageNum).get(cp).margin,vpos, 0x101010);
					} else {
						fontRenderer.drawString(descText.get(pageNum).get(cp).text, getTextXStart()+descText.get(pageNum).get(cp).margin,vpos, 0x101010);
					}

					vpos+=10;
				}
			}

			fontRenderer.drawString((pageNum+1)+"/"+getNbPage(), (getXSize()/2)-10,getYSize()-10, 0x101010);

			vpos=6;

			this.zLevel = 100.0F;
			itemRenderer.zLevel = 100.0F;

			if (pageNum<descText.size()) {
				for (int cp=0;(cp<getPageSize()) && (cp<descText.get(pageNum).size());cp++) {
					if (descText.get(pageNum).get(cp).icons!=null) {
						for (int ic=0;ic<descText.get(pageNum).get(cp).icons.size();ic++) {
							ItemStack icon=descText.get(pageNum).get(cp).icons.get(ic);

							GL11.glEnable(GL11.GL_DEPTH_TEST);
							itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, icon, getTextXStart()+18*ic, vpos);
							itemRenderer.renderItemStack(this.fontRenderer, this.mc.renderEngine, icon, getTextXStart()+18*ic, vpos, null);
						}
					}

					vpos+=10;
				}
			}

			itemRenderer.zLevel = 0.0F;
			this.zLevel = 0.0F;
			
			customDrawScreen(i,j,f);
		}


		GL11.glPopMatrix();
		super.drawScreen(i, j, f);
		GL11.glEnable(2896 /*GL_LIGHTING*/);
		GL11.glEnable(2929 /*GL_DEPTH_TEST*/);

		GL11.glDisable(GL11.GL_LIGHTING);

		for (MillGuiTextField textField : textFields) {
			textField.drawTextBox();
		}
	}

	public abstract int getLineSizeInPx();

	protected int getNbPage() {
		return descText.size();
	}

	public abstract int getPageSize();

	public abstract String getPNGPath();

	public int getTextXStart() {
		return 8;
	}

	public abstract int getXSize();

	public abstract int getYSize();

	public void incrementPage() {

		if (descText==null)
			return;

		if (pageNum<(getNbPage()-1)) {
			pageNum++;
		}
		buttonPagination();
	}

	public abstract void initData();

	private int getLineWidthInChars(int lineWidthInPx) {
		String testLine="a";

		while (fontRenderer.getStringWidth(testLine)<lineWidthInPx) {
			testLine+="a";
		}

		return testLine.length()-1;
	}

	@Override
	public void initGui() {
		super.initGui();

		initData();

		buttonPagination();
	}

	@Override
	protected void keyTyped(char c, int i)
	{

		boolean keyTyped=false;
		for (MillGuiTextField textField : textFields) {
			if (textField.textboxKeyTyped(c, i))
			{
				keyTyped=true;
				handleTextFieldPress(textField);
			}
		}

		if(!keyTyped && i == 1) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	protected void handleTextFieldPress(MillGuiTextField textField) {

	}

	@Override
	protected void mouseClicked(int i, int j, int k) {

		final int xStart = (width - getXSize()) / 2;
		final int yStart = (height - getYSize()) / 2;

		final int ai = i-xStart;
		final int aj = j-yStart;

		if ((aj>(getYSize()-14)) && (aj<getYSize())) {
			if ((ai>0) && (ai<33)) {
				decrementPage();
			} else if ((ai>(getXSize()-33)) && (ai<getXSize())) {
				incrementPage();
			}
		}

		for (MillGuiTextField textField : textFields) {
			textField.mouseClicked(i, j, k);
		}

		super.mouseClicked(i, j, k);
	}

}
