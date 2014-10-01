package org.millenaire.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.millenaire.client.MillClientUtilities;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.UserProfile;
import org.millenaire.common.building.Building;
import org.millenaire.common.forge.Mill;

public class DisplayActions {

	public static void displayChunkGUI(final EntityPlayer player,
			final World world) {
		MillClientUtilities.displayChunkPanel(world, player);
	}

	public static void displayConfigGUI() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiConfig());
	}

	public static void displayControlledMilitaryGUI(final EntityPlayer player,
			final Building townHall) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiControlledMilitary(player, townHall));
	}

	public static void displayControlledProjectGUI(final EntityPlayer player,
			final Building townHall) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiControlledProjects(player, townHall));
	}

	public static void displayHelpGUI() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiHelp());
	}

	public static void displayHireGUI(final EntityPlayer player,
			final MillVillager villager) {
		Minecraft.getMinecraft()
				.displayGuiScreen(new GuiHire(player, villager));
	}

	public static void displayInfoPanel(final EntityPlayer player,
			final World world) {
		MillClientUtilities.displayInfoPanel(world, player);
	}

	public static void displayNegationWandGUI(final EntityPlayer player,
			final Building townHall) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiNegationWand(player, townHall));
	}

	public static void displayNewBuildingProjectGUI(final EntityPlayer player,
			final Building townHall, final Point pos) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiNewBuildingProject(player, townHall, pos));
	}

	public static void displayNewVillageGUI(final EntityPlayer player,
			final Point pos) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiNewVillage(player, pos));
	}

	public static void displayParchmentPanelGUI(final EntityPlayer player,
			final List<List<String>> pages, final Building building,
			final int mapType, final boolean isParchment) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiPanelParchment(player, pages, building, mapType,
						isParchment));

	}

	public static void displayQuestGUI(final EntityPlayer player,
			final MillVillager villager) {
		final UserProfile profile = Mill.clientWorld.getProfile(player
				.getDisplayName());
		if (profile.villagersInQuests.containsKey(villager.villager_id)) {
			Minecraft.getMinecraft().displayGuiScreen(
					new GuiQuest(player, villager));
		}
	}

	public static void displayStartupOrError(final EntityPlayer player,
			final boolean error) {
		MillClientUtilities.displayStartupText(error);
	}

	public static void displayVillageBookGUI(final EntityPlayer player,
			final Point p) {
		MillClientUtilities.displayVillageBook(Mill.clientWorld.world, player,
				p);
	}

	public static void displayVillageChiefGUI(final EntityPlayer player,
			final MillVillager chief) {
		Minecraft.getMinecraft().displayGuiScreen(
				new GuiVillageHead(player, chief));
	}
}
