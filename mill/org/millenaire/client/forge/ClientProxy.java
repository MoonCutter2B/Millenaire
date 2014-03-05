package org.millenaire.client.forge;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.ModelFemaleAsymmetrical;
import org.millenaire.client.ModelFemaleSymmetrical;
import org.millenaire.client.RenderMillVillager;
import org.millenaire.client.RenderWallDecoration;
import org.millenaire.client.TileEntityMillChestRenderer;
import org.millenaire.client.network.ClientReceiver;
import org.millenaire.client.network.ClientSender;
import org.millenaire.client.texture.TextureAmuletAlchemist;
import org.millenaire.client.texture.TextureAmuletVishnu;
import org.millenaire.client.texture.TextureAmuletYddrasil;
import org.millenaire.common.EntityMillDecoration;
import org.millenaire.common.MLN;
import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.MillVillager;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.UserProfile;
import org.millenaire.common.forge.CommonProxy;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods.ItemMillenaireBow;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void checkTextureSize() {
		MillClientUtilities.checkTextSize();
	}

	@Override
	public void testTextureSize() {
		MillClientUtilities.checkTextSize();
	}

	@Override
	public IGuiHandler createGuiHandler() {
		return new ClientGuiHandler();
	}

	@Override
	public File getBaseDir() {

		if (baseDir==null) {
			baseDir=new File(new File(Minecraft.getMinecraft().mcDataDir,"mods"), "millenaire");
		}

		return baseDir;
	}

	@Override
	public UserProfile getClientProfile() {
		if (Mill.clientWorld.profiles.containsKey(Mill.proxy.getTheSinglePlayer().getDisplayName()))
			return Mill.clientWorld.profiles.get(Mill.proxy.getTheSinglePlayer().getDisplayName());

		final UserProfile profile=new UserProfile(Mill.clientWorld, Mill.proxy.getTheSinglePlayer().getDisplayName(), Mill.proxy.getTheSinglePlayer().getDisplayName());
		Mill.clientWorld.profiles.put(profile.key, profile);
		return profile;
	}

	@Override
	public File getConfigFile() {
		return new File(getBaseDir(), "config.txt");
	}

	@Override
	public File getCustomConfigFile() {
		return new File(getCustomDir(), "config-custom.txt");
	}

	@Override
	public File getCustomDir() {

		if (customDir==null) {
			customDir=new File(new File(Minecraft.getMinecraft().mcDataDir,"mods"), "millenaire-custom");
		}

		return customDir;
	}

	@Override
	public String getItemName(Item item, int meta) {
		if ((item==null)) {
			try {
				throw new MillenaireException("Truing to get the name of a null item.");
			} catch (final Exception e) {
				MLN.printException(e);
			}
			return null;
		}

		if (meta==-1) {
			meta=0;
		}
		
		return new ItemStack(item,1,meta).getDisplayName();
	}

	@Override
	public File getLogFile() {
		return new File(getBaseDir(), "millenaire.log");
	}

	@Override
	public String getQuestKeyName() {
		return Keyboard.getKeyName(MLN.keyInfoPanelList);
	}

	@Override
	public EntityPlayer getTheSinglePlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public String getSinglePlayerName() {
		return Minecraft.getMinecraft().thePlayer.getDisplayName();
	}

	@Override
	public void handleClientGameUpdate() {

		MillClientUtilities.handleKeyPress(Mill.clientWorld.world);

		if ((Mill.clientWorld.world.getWorldTime()%20)==0) {
			Mill.clientWorld.clearPanelQueue();
		}

		//in case the game language has changed
		loadLanguages();
	}

	@Override
	public void handleClientLogin() {
		ClientSender.sendVersionInfo();
		ClientSender.sendAvailableContent();
	}


	@Override
	public boolean isTrueServer() {
		return false;
	}

	@Override
	public void loadKeyDefaultSettings() {
		MLN.keyVillageList=Keyboard.KEY_V;
		MLN.keyInfoPanelList=Keyboard.KEY_M;
		MLN.keyAggressiveEscorts=Keyboard.KEY_G;
	}

	@Override
	public int loadKeySetting(String value) {
		return Keyboard.getKeyIndex(value.toUpperCase());
	}

	public String getKeyString(int value) {
		return Keyboard.getKeyName(value);
	}

	@Override
	public void loadLanguages() {
		final Minecraft minecraft=Minecraft.getMinecraft();

		MLN.loadLanguages(minecraft.gameSettings.language);
	}

	@Override
	public void localTranslatedSentence(EntityPlayer player, char colour, String code,
			String... values) {

		for (int i=0;i<values.length;i++) {
			values[i]=MLN.unknownString(values[i]);
		}

		sendLocalChat(player,colour,MLN.string(code,values));
	}

	@Override
	public String logPrefix() {
		return "CLIENT ";
	}

	@Override
	public void preloadTextures() {

	}

	@Override
	public void registerForgeClientClasses() {
		FMLCommonHandler.instance().bus().register(new ClientTickHandler()); 
	}

	@Override
	public void registerGraphics() {
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericMale.class, new RenderMillVillager(new ModelBiped(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericAsymmFemale.class, new RenderMillVillager(new ModelFemaleAsymmetrical(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericSymmFemale.class, new RenderMillVillager(new ModelFemaleSymmetrical(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericZombie.class, new RenderBiped(new ModelZombie(),0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMillDecoration.class, new RenderWallDecoration());

		
		//ModLoader.addAnimation(new TextureVishnuAmulet(Minecraft.getMinecraft()));
		//ModLoader.addAnimation(new TextureAlchemistAmulet(Minecraft.getMinecraft()));
		//ModLoader.addAnimation(new TextureYddrasilAmulet(Minecraft.getMinecraft()));


	}

	@Override
	public void registerTileEntities() {
		//GameRegistry.registerTileEntity(TileEntityMillChest.class, "ml_TileEntityBuilding", new TileEntityMillChestRenderer());
		GameRegistry.registerTileEntity(TileEntityMillChest.class, "ml_TileEntityBuilding");
		GameRegistry.registerTileEntity(TileEntityPanel.class, "ml_TileEntityPanel");
	}

	@Override
	public void sendChatAdmin(String s) {
		s=s.trim();
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(s));
	}
	
	@Override
	public void sendChatAdmin(String s, EnumChatFormatting colour) {
		s=s.trim();
		ChatComponentText cc=new ChatComponentText(s);
		cc.getChatStyle().setColor(colour);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(cc);
	}

	@Override
	public void sendLocalChat(EntityPlayer player,char colour, String s) {
		s=s.trim();
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("\247"+colour+s));
	}

	@Override
	public void setTextureIds() {
		
		Mill.normanArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_norman");
		Mill.japaneseWarriorBlueArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_japanese_warrior_blue");
		Mill.japaneseWarriorRedArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_japanese_warrior_red");
		Mill.japaneseGuardArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_japanese_guard");
		Mill.byzantineArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_byzantine");
		Mill.mayanQuestArmourId = RenderingRegistry.addNewArmourRendererPrefix("ML_mayan_quest");
	}

	@Override
	public void updateBowIcon(ItemMillenaireBow bow,EntityPlayer entityplayer) {
		MillClientUtilities.updateBowIcon(bow, entityplayer);
	}

	@Override
	public void declareAmuletTextures(IIconRegister iconRegister) {
		TextureMap textureMap=(TextureMap)iconRegister;
		
		textureMap.setTextureEntry(Mill.modId+":amulet_alchemist"+MLN.getTextSuffix(), new TextureAmuletAlchemist(Mill.modId+":amulet_alchemist"+MLN.getTextSuffix()));
		textureMap.setTextureEntry(Mill.modId+":amulet_vishnu"+MLN.getTextSuffix(), new TextureAmuletVishnu(Mill.modId+":amulet_vishnu"+MLN.getTextSuffix()));
		textureMap.setTextureEntry(Mill.modId+":amulet_yggdrasil"+MLN.getTextSuffix(), new TextureAmuletYddrasil(Mill.modId+":amulet_yggdrasil"+MLN.getTextSuffix()));
		
	}
	
	@Override
	public void initNetwork() {
		Mill.millChannel.register(new ClientReceiver());
	}
	

}
