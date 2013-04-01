package org.millenaire.client.gui;

import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;

import org.millenaire.client.MillClientUtilities;
import org.millenaire.common.Building;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.forge.Mill;

public class DisplayActions {


	public static void displayControlledProjectGUI(EntityPlayer player,Building townHall) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiControlledBuildings(player,townHall));
	}


	public static void displayHelpGUI() {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiHelp());
	}
	
	public static void displayConfigGUI() {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiConfig());
	}
	
	public static void displayChunkGUI(EntityPlayer player,World world) {
		MillClientUtilities.displayChunkPanel(world,player);
	}


	public static void displayHireGUI(EntityPlayer player, MillVillager villager) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiHire(player,villager));
	}




	public static void displayInfoPanel(EntityPlayer player,World world) {
		MillClientUtilities.displayInfoPanel(world,player);
	}


	public static void displayNegationWandGUI(EntityPlayer player,Building townHall) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiNegationWand(player,townHall));
	}


	public static void displayNewBuildingProjectGUI(EntityPlayer player,
			Building townHall, Point pos) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiNewBuildingProject(player,townHall,pos));
	}




	public static void displayNewVillageGUI(EntityPlayer player,Point pos) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiNewVillage(player,pos));
	}

	public static void displayParchmentPanelGUI(EntityPlayer player,Vector<Vector<String>> pages,
			Building building, int mapType,
			boolean isParchment) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiPanelParchment(player,pages, building,mapType, isParchment));

	}


	public static void displayQuestGUI(EntityPlayer player, MillVillager villager) {
		final UserProfile profile=Mill.clientWorld.getProfile(player.username);
		if (profile.villagersInQuests.containsKey(villager.villager_id)) {
			ModLoader.getMinecraftInstance().displayGuiScreen(new GuiQuest(player,villager));
		}
	}


	public static void displayStartupOrError(EntityPlayer player,boolean error) {
		MillClientUtilities.displayStartupError(error);
	}


	public static void displayVillageBookGUI(EntityPlayer player,Point p) {
		MillClientUtilities.displayVillageBook(Mill.clientWorld.world, player, p);
	}



	public static void displayVillageChiefGUI(EntityPlayer player, MillVillager chief) {
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiVillageHead(player,chief));
	}
}
