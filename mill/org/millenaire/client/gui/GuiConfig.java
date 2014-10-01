package org.millenaire.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.millenaire.common.MLN;
import org.millenaire.common.MillConfig;
import org.millenaire.common.forge.Mill;

public class GuiConfig extends GuiText {

	public static class ConfigButton extends MillGuiButton {

		public MillConfig config;

		public ConfigButton(final MillConfig config) {
			super(0, 0, 0, 0, 0, config.getLabel());
			this.config = config;

			refreshLabel();
		}

		public void refreshLabel() {
			this.displayString = config.getLabel() + ": "
					+ config.getStringValue();
		}
	}

	public static class ConfigPageButton extends MillGuiButton {
		public int pageId;

		public ConfigPageButton(final int pageId) {
			super(0, 0, 0, 0, 0, MLN.string(MLN.configPageTitles.get(pageId)));
			this.pageId = pageId;
		}
	}

	int pageId = -1;

	ResourceLocation background = new ResourceLocation(Mill.modId,
			"textures/gui/ML_config.png");

	@Override
	protected void actionPerformed(final GuiButton guibutton) {
		if (guibutton instanceof ConfigButton) {
			final ConfigButton configButton = (ConfigButton) guibutton;

			int valPos = -1;

			for (int i = 0; i < configButton.config.getPossibleVals().length; i++) {
				final Object o = configButton.config.getPossibleVals()[i];
				if (o.equals(configButton.config.getValue())) {
					valPos = i;
				}
			}

			valPos++;

			if (valPos >= configButton.config.getPossibleVals().length) {
				valPos = 0;
			}

			configButton.config
					.setValue(configButton.config.getPossibleVals()[valPos]);

			configButton.refreshLabel();

			MLN.writeConfigFile();
		} else if (guibutton instanceof ConfigPageButton) {
			final ConfigPageButton configPageButton = (ConfigPageButton) guibutton;

			pageId = configPageButton.pageId;

			pageNum = 0;

			descText = getData();
			buttonPagination();
		}
	}

	@Override
	protected void customDrawBackground(final int i, final int j, final float f) {

	}

	@Override
	protected void customDrawScreen(final int i, final int j, final float f) {

	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	private List<List<Line>> getData() {
		if (pageId == -1) {
			return getHomepageData();
		} else {
			return getPageData();
		}
	}

	private List<List<Line>> getHomepageData() {

		final List<List<Line>> pages = new ArrayList<List<Line>>();

		final List<Line> text = new ArrayList<Line>();

		text.add(new Line(DARKBLUE + MLN.string("config.pagetitle"), false));
		text.add(new Line("", false));

		for (int i = 0; i < MLN.configPages.size(); i++) {
			text.add(new Line(new ConfigPageButton(i)));
			text.add(new Line(false));
			text.add(new Line());
		}

		pages.add(text);

		return adjustText(pages);
	}

	@Override
	public int getLineSizeInPx() {
		return 247;
	}

	private List<List<Line>> getPageData() {
		final List<List<Line>> pages = new ArrayList<List<Line>>();

		final List<Line> text = new ArrayList<Line>();

		text.add(new Line(DARKBLUE
				+ MLN.string(MLN.configPageTitles.get(pageId)), false));
		text.add(new Line());

		if (MLN.configPageDesc.get(pageId) != null) {
			text.add(new Line(MLN.string(MLN.configPageDesc.get(pageId)), false));
			text.add(new Line());
		}

		for (int j = 0; j < MLN.configPages.get(pageId).size(); j++) {

			final MillConfig config = MLN.configPages.get(pageId).get(j);

			if (config.displayConfig || config.displayConfigDev && MLN.DEV) {

				if (config.getDesc().length() > 0) {
					text.add(new Line(config.getDesc(), false));
				}

				if (config.hasTextField()) {
					final MillGuiTextField textField = new MillGuiTextField(
							fontRendererObj, 0, 0, 0, 0, config.key);
					textField.setText(config.getStringValue());
					textField.setMaxStringLength(config.strLimit);
					textField.setTextColor(-1);
					text.add(new Line(config.getLabel() + ":", textField));
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
	public int getPageSize() {
		return 19;
	}

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
	protected void handleTextFieldPress(final MillGuiTextField textField) {
		if (MLN.configs.containsKey(textField.fieldKey)) {
			final MillConfig config = MLN.configs.get(textField.fieldKey);
			config.setValueFromString(textField.getText(), false);
			MLN.writeConfigFile();
		}
	}

	@Override
	public void initData() {
		descText = getData();
	}

	@Override
	protected void keyTyped(final char c, final int i) {

		if (i == 1) {
			if (pageId == -1) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			} else {
				pageId = -1;
				pageNum = 0;

				descText = getData();
				buttonPagination();
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {

	}
}
