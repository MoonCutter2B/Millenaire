package org.millenaire.client.forge;

import java.io.File;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.util.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.ModelFemaleAsymmetrical;
import org.millenaire.client.ModelFemaleSymmetrical;
import org.millenaire.client.RenderMillVillager;
import org.millenaire.client.RenderWallDecoration;
import org.millenaire.client.TileEntityMillChestRenderer;
import org.millenaire.client.network.ClientSender;
import org.millenaire.client.texture.TextureAmuletAlchemist;
import org.millenaire.client.texture.TextureAmuletVishnu;
import org.millenaire.client.texture.TextureAmuletYddrasil;
import org.millenaire.common.EntityWallDecoration;
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
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

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
			baseDir=new File(new File(Minecraft.getMinecraftDir(),"mods"), "millenaire");
		}

		return baseDir;
	}

	@Override
	public UserProfile getClientProfile() {
		if (Mill.clientWorld.profiles.containsKey(Mill.proxy.getTheSinglePlayer().username))
			return Mill.clientWorld.profiles.get(Mill.proxy.getTheSinglePlayer().username);

		final UserProfile profile=new UserProfile(Mill.clientWorld, Mill.proxy.getTheSinglePlayer().username, Mill.proxy.getTheSinglePlayer().username);
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
			customDir=new File(new File(Minecraft.getMinecraftDir(),"mods"), "millenaire-custom");
		}

		return customDir;
	}

	@Override
	public String getItemName(int id, int meta) {
		if ((id<1) || (id>=Item.itemsList.length)) {
			try {
				throw new MillenaireException("Invalid item id: "+id);
			} catch (final Exception e) {
				MLN.printException(e);
			}
			return null;
		}

		if (meta==-1) {
			meta=0;
		}

		if (Item.itemsList[id]==null) {
			MLN.error(null, "Looked for name of null item: "+id);
			return MLN.string("error.unknownitem");
		}

		return StringTranslate.getInstance().translateNamedKey(Item.itemsList[id].getUnlocalizedName(new ItemStack(id,1,meta)));
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
		return ModLoader.getMinecraftInstance().thePlayer;
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
		final Minecraft minecraft=ModLoader.getMinecraftInstance();

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
		TickRegistry.registerTickHandler(new ClientTickHandler(EnumSet.of(TickType.CLIENT)), Side.CLIENT);
	}

	@Override
	public void registerGraphics() {
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericMale.class, new RenderMillVillager(new ModelBiped(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericAsymmFemale.class, new RenderMillVillager(new ModelFemaleAsymmetrical(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericSymmFemale.class, new RenderMillVillager(new ModelFemaleSymmetrical(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(MillVillager.MLEntityGenericZombie.class, new RenderBiped(new ModelZombie(),0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityWallDecoration.class, new RenderWallDecoration());

		
		//ModLoader.addAnimation(new TextureVishnuAmulet(ModLoader.getMinecraftInstance()));
		//ModLoader.addAnimation(new TextureAlchemistAmulet(ModLoader.getMinecraftInstance()));
		//ModLoader.addAnimation(new TextureYddrasilAmulet(ModLoader.getMinecraftInstance()));


	}

	@Override
	public void registerTileEntities() {
		ModLoader.registerTileEntity(TileEntityMillChest.class, "ml_TileEntityBuilding", new TileEntityMillChestRenderer());
		ModLoader.registerTileEntity(TileEntityPanel.class, "ml_TileEntityPanel");
	}

	@Override
	public void sendChatAdmin(String s) {
		s=s.trim();
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage(s);
	}

	@Override
	public void sendLocalChat(EntityPlayer player,char colour, String s) {
		s=s.trim();
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage("\247"+colour+s);
	}

	@Override
	public void setTextureIds() {
		Mill.normanArmourId = ModLoader.addArmor("ML_norman");
		Mill.japaneseWarriorBlueArmourId = ModLoader.addArmor("ML_japanese_warrior_blue");
		Mill.japaneseWarriorRedArmourId = ModLoader.addArmor("ML_japanese_warrior_red");
		Mill.japaneseGuardArmourId = ModLoader.addArmor("ML_japanese_guard");
		Mill.byzantineArmourId = ModLoader.addArmor("ML_byzantine");
	}

	@Override
	public void updateBowIcon(ItemMillenaireBow bow,EntityPlayer entityplayer) {
		MillClientUtilities.updateBowIcon(bow, entityplayer);
	}

	@Override
	public void declareAmuletTextures(IconRegister iconRegister) {
		TextureMap textureMap=(TextureMap)iconRegister;
		
		textureMap.setTextureEntry(Mill.modId+":amulet_alchemist"+MLN.getTextSuffix(), new TextureAmuletAlchemist(Mill.modId+":amulet_alchemist"+MLN.getTextSuffix()));
		textureMap.setTextureEntry(Mill.modId+":amulet_vishnu"+MLN.getTextSuffix(), new TextureAmuletVishnu(Mill.modId+":amulet_vishnu"+MLN.getTextSuffix()));
		textureMap.setTextureEntry(Mill.modId+":amulet_yggdrasil"+MLN.getTextSuffix(), new TextureAmuletYddrasil(Mill.modId+":amulet_yggdrasil"+MLN.getTextSuffix()));
		
	}
	
	
	

}
