package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.millenaire.common.MLN;
import org.millenaire.common.MillConfig;
import org.millenaire.common.forge.Mill;

public class GuiConfig extends GuiText {

	public static class ConfigButton extends MillGuiButton {

		public MillConfig config;

		public ConfigButton(MillConfig config) {
			super(0, 0,0,0,0, config.getLabel());
			this.config=config;

			refreshLabel();
		}

		public void refreshLabel() {
			this.displayString=config.getLabel()+": "+config.getStringValue();
		}
	}

	public static class ConfigPageButton extends MillGuiButton {
		public int pageId;

		public ConfigPageButton(int pageId) {
			super(0, 0,0,0,0, MLN.string(MLN.configPageTitles.get(pageId)));
			this.pageId=pageId;
		}
	}

	int pageId=-1;

	@Override
	protected void customDrawBackground(int i, int j, float f) {

	}

	@Override
	protected void customDrawScreen(int i, int j, float f) {

	}

	@Override
	public int getLineSizeInPx() {
		return 247;
	}

	@Override
	public int getPageSize() {
		return 19;
	}

	ResourceLocation background=new ResourceLocation(Mill.modId,"/textures/gui/ML_config.png");
	
	@Override
	public ResourceLocation getPNGPath() {
		return background;
	}

	@Override
	public int getXSize() {
		return 256;
	}

	@Override
	public int getYSize() {
		return 220;
	}

	@Override
	public void initData() {
		descText=getData();
	}

	private Vector<Vector<Line>> getHomepageData() {

		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();

		Vector<Line> text=new Vector<Line>();

		text.add(new Line(DARKBLUE+MLN.string("config.pagetitle"),false));
		text.add(new Line("",false));

		for (int i=0;i<MLN.configPages.size();i++) {
			text.add(new Line(new ConfigPageButton(i)));
			text.add(new Line(false));
			text.add(new Line());
		}

		pages.add(text);

		return adjustText(pages);
	}

	private Vector<Vector<Line>> getData() {		
		if (pageId==-1) {
			return getHomepageData();
		} else {
			return getPageData();
		}
	}


	private Vector<Vector<Line>> getPageData() {
		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();

		Vector<Line> text=new Vector<Line>();

		text.add(new Line(DARKBLUE+MLN.string(MLN.configPageTitles.get(pageId)),false));
		text.add(new Line());

		if (MLN.configPageDesc.get(pageId)!=null) {
			text.add(new Line(MLN.string(MLN.configPageDesc.get(pageId)),false));
			text.add(new Line());
		}

		for (int j=0;j<MLN.configPages.get(pageId).size();j++) {

			MillConfig config=MLN.configPages.get(pageId).get(j);

			if (config.displayConfig || (config.displayConfigDev && MLN.DEV)) {

				if (config.getDesc().length()>0)
					text.add(new Line(config.getDesc(),false));

				if (config.hasTextField()) {
					MillGuiTextField textField=new MillGuiTextField(fontRendererObj, 0, 0, 0, 0, config.key);
					textField.setText(config.getStringValue());
					textField.setMaxStringLength(config.strLimit);
					textField.setTextColor(-1);
					text.add(new Line(config.getLabel()+":",textField));
					text.add(new Line(false));
					text.add(new Line());
				} else {
					text.add(new Line(new ConfigButton(config)));
					text.add(new Line(false));
					text.add(new Line());
				}
			}
		}

		pages.add(text);

		return adjustText(pages);
	}

	@Override
	protected void handleTextFieldPress(MillGuiTextField textField) {
		if (MLN.configs.containsKey(textField.fieldKey)) {
			MillConfig config=MLN.configs.get(textField.fieldKey);
			config.setValueFromString(textField.getText(), false);
			MLN.writeConfigFile();
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
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

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton instanceof ConfigButton) {
			ConfigButton configButton=(ConfigButton)guibutton;

			int valPos=-1;

			for (int i=0;i<configButton.config.getPossibleVals().length;i++) {				
				Object o =configButton.config.getPossibleVals()[i];
				if (o.equals(configButton.config.getValue())) {
					valPos=i;
				}
			}

			valPos++;

			if (valPos>=configButton.config.getPossibleVals().length)
				valPos=0;

			configButton.config.setValue(configButton.config.getPossibleVals()[valPos]);

			configButton.refreshLabel();

			MLN.writeConfigFile();
		} else if (guibutton instanceof ConfigPageButton) {
			ConfigPageButton configPageButton=(ConfigPageButton)guibutton;

			pageId=configPageButton.pageId;

			pageNum=0;

			descText=getData();
			buttonPagination();
		}
	}

	@Override
	protected void keyTyped(char c, int i)
	{

		if(i == 1) {
			if (pageId==-1) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			} else {
				pageId=-1;
				pageNum=0;

				descText=getData();
				buttonPagination();
			}
		} else {
			super.keyTyped(c, i);
		}
	}
}
