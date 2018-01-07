package org.millenaire.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.millenaire.MillConfig;
import org.millenaire.Millenaire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class MillGuiFactory implements IModGuiFactory
{

	@Override
	public void initialize(Minecraft minecraftInstance) { System.out.println("GuiFactory Loaded"); }

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() { return MillConfigGui.class; }

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) { return null; }

	public static class MillConfigGui extends GuiConfig
	{
		public MillConfigGui(GuiScreen parentScreen)
		{
			super(parentScreen, getConfigElements(), Millenaire.MODID, false, false, I18n.format("gui.millConfig.mainTitle"));
		}
		
		private static List<IConfigElement> getConfigElements()
		{
			List<IConfigElement> list = new ArrayList<>();
			
			list.add(new DummyCategoryElement("UI Options", "gui.millConfig.ctgy.uiOptions", CategoryEntryUIOptions.class));
			list.add(new DummyCategoryElement("World Generation Options", "gui.millConfig.ctgy.worldGen", CategoryEntryWorldGen.class));
			list.add(new DummyCategoryElement("Village Behavior Options", "gui.millConfig.ctgy.villageBehavior", CategoryEntryVillageBev.class));
			
			return list;
		}
		
		public static class CategoryEntryUIOptions extends CategoryEntry
		{
			public CategoryEntryUIOptions(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}
			
			@Override
			protected GuiScreen buildChildScreen()
			{
				Configuration configuration = MillConfig.getConfig();
				ConfigElement catBase = new ConfigElement(configuration.getCategory(MillConfig.CATEGORYUIOPTIONS));
				List<IConfigElement> propertiesOnThisScreen = catBase.getChildElements();
				String windowTitle = configuration.toString();
				
				return new GuiConfig(this.owningScreen, propertiesOnThisScreen, this.owningScreen.modID, MillConfig.CATEGORYUIOPTIONS, 
						this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
						this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}
		}
		
		public static class CategoryEntryWorldGen extends CategoryEntry
		{
			public CategoryEntryWorldGen(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}
			
			@Override
			protected GuiScreen buildChildScreen()
			{
				Configuration configuration = MillConfig.getConfig();
				ConfigElement catBase = new ConfigElement(configuration.getCategory(MillConfig.CATEGORYWORLDGEN));
				List<IConfigElement> propertiesOnThisScreen = catBase.getChildElements();
				String windowTitle = configuration.toString();
				
				return new GuiConfig(this.owningScreen, propertiesOnThisScreen, this.owningScreen.modID, MillConfig.CATEGORYWORLDGEN, 
						this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
						this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}
		}
		
		public static class CategoryEntryVillageBev extends CategoryEntry
		{
			public CategoryEntryVillageBev(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}
			
			@Override
			protected GuiScreen buildChildScreen()
			{
				Configuration configuration = MillConfig.getConfig();
				ConfigElement catBase = new ConfigElement(configuration.getCategory(MillConfig.CATEGORYVILLAGEBEV));
				List<IConfigElement> propertiesOnThisScreen = catBase.getChildElements();
				String windowTitle = configuration.toString();
				
				return new GuiConfig(this.owningScreen, propertiesOnThisScreen, this.owningScreen.modID, MillConfig.CATEGORYVILLAGEBEV, 
						this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
						this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
			}
		}
	}
}
