package org.millenaire.common.forge;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

import org.millenaire.common.InvItem;
import org.millenaire.common.MLN;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.UserProfile;
import org.millenaire.common.item.Goods.ItemMillenaireBow;
import org.millenaire.common.network.ServerReceiver;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

	protected File baseDir = null;

	protected File customDir = null;

	public void checkTextureSize() {

	}

	public IGuiHandler createGuiHandler() {
		return new CommonGuiHandler();
	}

	public void declareAmuletTextures(final IIconRegister iconRegister) {

	}

	public File getBaseDir() {
		if (baseDir == null) {
			baseDir = new File(new File(new File("."), "mods"), "millenaire");
		}

		return baseDir;
	}

	public String getBlockName(final Block block, final int meta) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserProfile getClientProfile() {
		return null;
	}

	public File getConfigFile() {
		return new File(getBaseDir(), "config-server.txt");
	}

	public File getCustomConfigFile() {
		return new File(getCustomDir(), "config-server-custom.txt");
	}

	public File getCustomDir() {
		if (customDir == null) {
			customDir = new File(new File(new File("."), "mods"), "millenaire-custom");
		}

		return customDir;
	}

	public String getInvItemName(final InvItem iv) {
		return "";
	}

	public String getItemName(final Item item, final int meta) {
		return "";
	}

	public String getKeyString(final int value) {
		return "";
	}

	public File getLogFile() {
		return new File(getBaseDir(), "millenaire-server.log");
	}

	public String getQuestKeyName() {
		return "";
	}

	public String getSinglePlayerName() {
		return null;
	}

	public EntityPlayer getTheSinglePlayer() {
		return null;
	}

	public void handleClientGameUpdate() {

	}

	public void handleClientLogin() {

	}

	public void initNetwork() {
		Mill.millChannel.register(new ServerReceiver());
	}

	public boolean isTrueClient() {
		return Mill.serverWorlds.isEmpty();
	}

	public boolean isTrueServer() {
		return true;
	}

	public void loadKeyDefaultSettings() {

	}

	public int loadKeySetting(final String value) {
		return 0;
	}

	public void loadLanguages() {
		MLN.loadLanguages(null);
	}

	public void localTranslatedSentence(final EntityPlayer player, final char colour, final String code, final String... values) {

	}

	public String logPrefix() {
		return "SRV ";
	}

	public void preloadTextures() {

	}

	public void refreshClientResources() {
		// TODO Auto-generated method stub

	}

	public void registerForgeClientClasses() {

	}

	public void registerGraphics() {

	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityMillChest.class, "ml_TileEntityBuilding");
		GameRegistry.registerTileEntity(TileEntityPanel.class, "ml_TileEntityPanel");
	}

	public void sendChatAdmin(final String s) {

	}

	public void sendChatAdmin(final String s, final EnumChatFormatting colour) {

	}

	public void sendLocalChat(final EntityPlayer player, final char colour, final String s) {

	}

	public void setTextureIds() {

	}

	public void testTextureSize() {

	}

	public void updateBowIcon(final ItemMillenaireBow bow, final EntityPlayer entityplayer) {

	}

}
