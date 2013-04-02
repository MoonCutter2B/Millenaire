package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.client.gui.GuiButton;

import org.lwjgl.input.Keyboard;
import org.millenaire.common.MLN;
import org.millenaire.common.MillConfig;

public class GuiConfig extends GuiText {

	public static class ConfigButton extends MillGuiButton {

		public MillConfig config;

		public ConfigButton(MillConfig config) {
			super(0, 0,0,0,0, config.getLabel());
			this.config=config;

			refreshLabel();
		}

		public void refreshLabel() {
			this.displayString=config.getLabel()+": "+config.getValue();
		}
	}

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

	@Override
	public String getPNGPath() {
		return "/graphics/gui/ML_parchment.png";
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


	private Vector<Vector<Line>> getData() {
		final Vector<Vector<Line>> pages = new Vector<Vector<Line>>();

		Vector<Line> text=new Vector<Line>();

		text.add(new Line(DARKBLUE+MLN.string("config.pagetitle"),false));
		text.add(new Line("",false));

		boolean firstPage=true;

		for (int i=0;i<MLN.configPages.size();i++) {

			if (!firstPage) {
				pages.add(text);
				text=new Vector<Line>();
				firstPage=false;
			}

			text.add(new Line(DARKBLUE+MLN.string(MLN.configPageTitles.get(i)),false));
			text.add(new Line());

			if (MLN.configPageDesc.get(i)!=null) {
				text.add(new Line(MLN.string(MLN.configPageDesc.get(i)),false));
				text.add(new Line());
			}


			for (int j=0;j<MLN.configPages.get(i).size();j++) {

				MillConfig config=MLN.configPages.get(i).get(j);

				if (config.displayConfig || (config.displayConfigDev && MLN.DEV)) {

					if (config.getDesc().length()>0)
						text.add(new Line(config.getDesc(),false));

					if (config.hasTextField()) {
						MillGuiTextField textField=new MillGuiTextField(fontRenderer, 0, 0, 0, 0, 0);
						textField.setText(config.getValue().toString());
						textField.setMaxStringLength(config.strLimit);
						text.add(new Line(textField));
						text.add(new Line(false));
						text.add(new Line());
					} else {
						text.add(new Line(new ConfigButton(config)));
						text.add(new Line(false));
						text.add(new Line());
					}
				}
			}
		}


		pages.add(text);

		return adjustText(pages);
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
		}
	}

}
