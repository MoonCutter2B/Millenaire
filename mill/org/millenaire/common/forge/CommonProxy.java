package org.millenaire.common.forge;

import java.io.File;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

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
	
	public void testTextureSize() {
		
	}
	
	public boolean isTrueClient() {
		return Mill.serverWorlds.isEmpty();
	}

	public IGuiHandler createGuiHandler() {
		return new CommonGuiHandler();
	}

	public File getBaseDir() {
		if (baseDir==null) {
			baseDir=new File(new File(new File("."),"mods"), "millenaire");
		}

		return baseDir;
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
		if (customDir==null) {
			customDir=new File(new File(new File("."),"mods"), "millenaire-custom");
		}

		return customDir;
	}

	public String getItemName(Item item, int meta) {
		return "";
	}

	public File getLogFile() {
		return new File(getBaseDir(), "millenaire-server.log");
	}

	public String getQuestKeyName() {
		return "";
	}

	public EntityPlayer getTheSinglePlayer() {
		return null;
	}

	public void handleClientGameUpdate() {

	}

	public void handleClientLogin() {

	}

	public boolean isTrueServer() {
		return true;
	}

	public void loadKeyDefaultSettings() {

	}

	public int loadKeySetting(String value) {
		return 0;
	}
	
	public String getKeyString(int value) {
		return "";
	}

	public void loadLanguages() {
		MLN.loadLanguages(null);
	}

	public void localTranslatedSentence(EntityPlayer player, char colour, String code,
			String... values) {

	}

	public String logPrefix() {
		return "SRV ";
	}

	public void preloadTextures() {

	}

	public void registerForgeClientClasses() {

	}

	public void registerGraphics() {

	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityMillChest.class, "ml_TileEntityBuilding");
		GameRegistry.registerTileEntity(TileEntityPanel.class, "ml_TileEntityPanel");
	}

	public void sendChatAdmin(String s) {

	}
	
	public void sendChatAdmin(String s, EnumChatFormatting colour) {

	}

	public void sendLocalChat(EntityPlayer player, char colour, String s) {


	}

	public void setTextureIds() {

	}

	public void updateBowIcon(ItemMillenaireBow bow, EntityPlayer entityplayer) {

	}
	
	public void declareAmuletTextures(IIconRegister iconRegister) {
		
		
		
	}

	public String getSinglePlayerName() {
		return null;
	}

	public void initNetwork() {
		Mill.millChannel.register(new ServerReceiver());
	}
	
}
