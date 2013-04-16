package org.millenaire.client;

import java.util.List;
import java.util.Vector;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.GuiPanelParchment;
import org.millenaire.client.gui.GuiText;
import org.millenaire.client.gui.GuiText.Line;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Building;
import org.millenaire.common.Culture;
import org.millenaire.common.MLN;
import org.millenaire.common.MillVillager;
import org.millenaire.common.Point;
import org.millenaire.common.Quest.QuestInstance;
import org.millenaire.common.TileEntityPanel;
import org.millenaire.common.TileEntityPanel.PanelPacketInfo;
import org.millenaire.common.UserProfile;
import org.millenaire.common.core.DevModUtilities;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;
import org.millenaire.common.item.Goods.ItemMillenaireBow;
import org.millenaire.common.network.ServerReceiver;

import cpw.mods.fml.client.FMLClientHandler;

public class MillClientUtilities {

	private static long lastPing=0;
	private static long lastFreeRes=0;


	public static void checkTextSize() {
		final int texture=ModLoader.getMinecraftInstance().renderEngine.getTexture("/textures/blocks/blockGold.png");

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		final int textSize = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

		if (textSize!=MLN.textureSize) {
			MLN.textureSize=textSize;
			ModLoader.getMinecraftInstance().renderEngine.refreshTextures();
		}
	}

	public static void displayChunkPanel(World world,EntityPlayer player) {

		final Vector<Vector<String>> pages=new Vector<Vector<String>>();

		Vector<String> page= new Vector<String>();

		page.add(MLN.string("chunk.chunkmap"));

		pages.add(page);

		page= new Vector<String>();

		page.add(MLN.string("chunk.caption"));

		page.add(MLN.string(""));
		page.add(MLN.string("chunk.captiongeneral"));
		page.add(MLN.string("chunk.captiongreen"));
		page.add(MLN.string("chunk.captionblue"));
		page.add(MLN.string("chunk.captionpurple"));
		page.add(MLN.string("chunk.captionwhite"));
		page.add(MLN.string(""));
		page.add(MLN.string("chunk.playerposition",(int)player.posX+"/"+(int)player.posZ));
		page.add(MLN.string(""));
		page.add(MLN.string("chunk.settings",""+MLN.KeepActiveRadius,""+ForgeChunkManager.getMaxTicketLengthFor(Mill.modId)));
		page.add(MLN.string(""));
		page.add(MLN.string("chunk.explanations"));

		pages.add(page);

		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiPanelParchment(player,pages, null,GuiPanelParchment.CHUNK_MAP, true));
	}
	
	public static void displayTradeHelp(Building shop,EntityPlayer player) {
		
		final Vector<Vector<Line>> pages=new Vector<Vector<Line>>();
		Vector<Line> page= new Vector<Line>();
		
		page.add(new Line(MLN.DARKBLUE+MLN.string("tradehelp.title",shop.getNativeBuildingName())));
		page.add(new Line(""));
		
		page.add(new Line(MLN.DARKBLUE+MLN.string("tradehelp.goodssold")));
		page.add(new Line(""));
		
		Vector<Goods> goods=shop.getSellingGoods();

		if (goods!=null) {
			String lastDesc=null;
			Vector<ItemStack> stacks=new Vector<ItemStack>();
			for (final Goods g : goods) {
				if (g.getSellingPrice(shop.getTownHall()) > 0) {
					if (lastDesc!=null && !lastDesc.equals(g.desc)) {
						page.add(new Line(stacks,MLN.string(lastDesc)));
						stacks.clear();
					}
					stacks.add(new ItemStack(g.item.item,1));
					lastDesc=g.desc;
				}
			}
			if (lastDesc!=null) {
				page.add(new Line(stacks,MLN.string(lastDesc)));
				stacks.clear();
			}
		}
		
		page.add(new Line(MLN.DARKBLUE+MLN.string("tradehelp.goodsbought")));
		page.add(new Line(""));
		
		goods=shop.getBuyingGoods();

		if (goods!=null) {
			String lastDesc=null;
			Vector<ItemStack> stacks=new Vector<ItemStack>();
			for (final Goods g : goods) {
				if (g.getBuyingPrice(shop.getTownHall()) > 0) {
					if (lastDesc!=null && !lastDesc.equals(g.desc)) {
						page.add(new Line(stacks,MLN.string(lastDesc)));
						stacks.clear();
					}
					stacks.add(new ItemStack(g.item.item,1));
					lastDesc=g.desc;
				}
			}
			if (lastDesc!=null) {
				page.add(new Line(stacks,MLN.string(lastDesc)));
				stacks.clear();
			}
		}
		
		pages.add(page);
		
		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiPanelParchment(player,null,pages, 0, true));
	}


	public static void displayInfoPanel(World world,EntityPlayer player) {

		final Vector<Vector<String>> pages=new Vector<Vector<String>>();
		Vector<String> page= new Vector<String>();


		page.add(GuiText.LINE_HELP_GUI_BUTTON);
		page.add("");
		page.add("");
		
		page.add(GuiText.LINE_CONFIG_GUI_BUTTON);
		page.add("");
		page.add("");

		if (!Mill.serverWorlds.isEmpty()) {
			page.add(GuiText.LINE_CHUNK_GUI_BUTTON);
			page.add("");
			page.add("");
		}

		page.add(MLN.string("info.culturetitle"));
		page.add("");

		for (final Culture culture : Culture.vectorCultures) {

			page.add(MLN.string("info.culture",culture.getCultureGameName()));
			page.add(MLN.string("info.culturereputation",culture.getReputationString()));
			if (MLN.languageLearning) {
				page.add(MLN.string("info.culturelanguage",culture.getLanguageLevelString()));
			}
			page.add("");
		}

		pages.add(page);
		page=new Vector<String>();

		page.add(MLN.string("quest.creationqueststatus"));
		page.add("");

		for (final String s : Mill.proxy.getClientProfile().getWorldQuestStatus()) {
			page.add(s);
		}

		page.add("");

		page.add(MLN.string("quest.questlist"));
		page.add("");

		boolean questShown=false;

		final UserProfile profile=Mill.proxy.getClientProfile();

		for (final QuestInstance qi : profile.questInstances) {
			final String s=qi.getListing(profile);
			if (s!=null) {
				questShown=true;

				page.add(s);

				long timeLeft=(qi.currentStepStart+(qi.getCurrentStep().duration*1000))-world.getWorldTime();
				timeLeft=Math.round(timeLeft/1000);

				if (timeLeft==0) {
					page.add(MLN.string("quest.lessthananhourleft"));
				} else {
					page.add(MLN.string("quest.timeremaining")+": "+timeLeft+" "+MLN.string("quest.hours"));
				}
			}
		}

		if (!questShown) {
			page.add(MLN.string("quest.noquestsvisible"));
		}

		pages.add(page);

		ModLoader.getMinecraftInstance().displayGuiScreen(new GuiPanelParchment(player,pages, null,0, true));
	}

	public static void displayPanel(World world,EntityPlayer player,Point p) {

		final TileEntityPanel panel=p.getPanel(world);

		if ((panel==null) || (panel.buildingPos==null))
			return;

		final Building building=Mill.clientWorld.getBuilding(panel.buildingPos);

		if (building==null)
			return;

		final Vector<Vector<String>> fullText=panel.getFullText(player);

		if (fullText != null) {
			DisplayActions.displayParchmentPanelGUI(player,fullText, building,
					panel.getMapType(), false);
		}
	}

	public static void displayStartupText(boolean error) {
		if (error) {
			Mill.proxy.sendChatAdmin(MLN.string("startup.loadproblem",Mill.version));
			Mill.proxy.sendChatAdmin(MLN.string("startup.checkload"));
			MLN.error(null, "There was an error when trying to load "+Mill.version+".");
		} else {
			if (MLN.displayStart) {
				
				String bonus="";
				
				if (MLN.bonusEnabled)
					bonus=" "+MLN.COLOUR+MLN.BLUE+MLN.string("startup.bonus");
				
				Mill.proxy.sendChatAdmin(MLN.string("startup.millenaireloaded",Mill.version,Keyboard.getKeyName(MLN.keyVillageList))+bonus);
			}
			if (MLN.VillageRadius>70) {
				Mill.proxy.sendChatAdmin(MLN.string("startup.radiuswarning"));
			}
		}
	}

	public static void displayVillageBook(World world,EntityPlayer player,Point p) {

		final Building townHall=Mill.clientWorld.getBuilding(p);

		if (townHall==null)
			return;

		final Vector<Vector<String>> pages=new Vector<Vector<String>>();
		final Vector<String> page= new Vector<String>();

		page.add(MLN.string("panels.villagescroll")+": "+townHall.getVillageQualifiedName());
		page.add("");
		pages.add(page);

		Vector<Vector<String>> newPages=TileEntityPanel.generateEtatCivil(player,townHall);

		for (final Vector<String> newPage : newPages) {
			pages.add(newPage);
		}

		newPages=TileEntityPanel.generateConstructions(player,townHall);
		for (final Vector<String> newPage : newPages) {
			pages.add(newPage);
		}

		newPages=TileEntityPanel.generateProjects(player,townHall);
		for (final Vector<String> newPage : newPages) {
			pages.add(newPage);
		}

		newPages=TileEntityPanel.generateResources(player,townHall);
		for (final Vector<String> newPage : newPages) {
			pages.add(newPage);
		}

		newPages=TileEntityPanel.generateMarketGoods(townHall);
		for (final Vector<String> newPage : newPages) {
			pages.add(newPage);
		}

		DisplayActions.displayParchmentPanelGUI(player, pages, townHall, TileEntityPanel.VILLAGE_MAP, true);
	}

	public static void handleKeyPress(World world) {

		final Minecraft minecraft=FMLClientHandler.instance().getClient();

		if(minecraft.currentScreen != null)
			return;


		final EntityPlayer player=minecraft.thePlayer;

		if ((System.currentTimeMillis()-lastPing) > 2000) {

			if (player.dimension==0) {

				if (Keyboard.isKeyDown(MLN.keyVillageList)) {
					ClientSender.displayVillageList(false);
					lastPing=System.currentTimeMillis();
				}

				//TODO: make it work in SP
				//				if (!world.isRemote && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_G)) {
				//					if (MLN.generateVillages) {
				//						MLN.generateVillages=false;
				//						Mill.proxy.sendChatAdmin(MLN.string("command.nogenerate").replace("<0>", Mill.version));
				//					} else {
				//						MLN.generateVillages=true;
				//						Mill.proxy.sendChatAdmin(MLN.string("command.willgenerate").replace("<0>", Mill.version));
				//					}
				//					Mill.clientWorld.saveWorldConfig();
				//					lastPing=System.currentTimeMillis();
				//				}

				if (!world.isRemote && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_P)) {
					if (!MillVillager.usingCustomPathing) {
						MillVillager.usingCustomPathing=true;
						Mill.proxy.sendChatAdmin(MLN.string("command.astarpathing"));
					} else {
						MillVillager.usingCustomPathing=false;
						Mill.proxy.sendChatAdmin(MLN.string("command.minecraftpathing"));
					}
					lastPing=System.currentTimeMillis();
				}
				
				if (Keyboard.isKeyDown(MLN.keyAggressiveEscorts)) {
					final boolean stance=!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));

					@SuppressWarnings("rawtypes")
					final List list = world.getEntitiesWithinAABB(MillVillager.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D).expand(16D, 8D, 16D));

					for (final Object o :list) {
						final MillVillager villager=(MillVillager)o;

						if (player.username.equals(villager.hiredBy)) {
							villager.aggressiveStance=stance;
						}
					}

					lastPing=System.currentTimeMillis();
				}

				if (Keyboard.isKeyDown(MLN.keyInfoPanelList)) {
					displayInfoPanel(world,player);
					lastPing=System.currentTimeMillis();
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
					MLN.jpsPathing=!MLN.jpsPathing;
					if (MLN.jpsPathing) {
						Mill.proxy.sendChatAdmin("Chemins JPS / JPS pathing");
					} else {
						Mill.proxy.sendChatAdmin("Chemins classiques / classic pathing");
					}
					lastPing=System.currentTimeMillis();
				}

				if (MLN.DEV) {

					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_R) && ((System.currentTimeMillis()-lastFreeRes)>5000)) {
						DevModUtilities.fillInFreeGoods(player);
						lastFreeRes=System.currentTimeMillis();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
						player.setPosition(player.posX+10000, player.posY+10, player.posZ);

						lastPing=System.currentTimeMillis();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
						player.setPosition(player.posX-10000, player.posY+10, player.posZ);

						lastPing=System.currentTimeMillis();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
						ClientSender.displayVillageList(true);
						lastPing=System.currentTimeMillis();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						ClientSender.devCommand(ServerReceiver.DEV_COMMAND_TOGGLE_AUTO_MOVE);
						lastPing=System.currentTimeMillis();
					}

					if (Keyboard.isKeyDown(Keyboard.KEY_Y) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						Mill.proxy.sendChatAdmin("Sending test path request.");
						ClientSender.devCommand(ServerReceiver.DEV_COMMAND_TEST_PATH);
						lastPing=System.currentTimeMillis();
					}


					//TODO make it work in SP
					//					if (Keyboard.isKeyDown(Keyboard.KEY_Q) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					//
					//						Mill.controller().getProfile(player.username).testQuests(world);
					//						lastPing=System.currentTimeMillis();
					//					}

					if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
						Mill.clientWorld.displayTagActionData(player);
						lastPing=System.currentTimeMillis();
					}
				}
			}
		}
	}



	public static void updateBowIcon(ItemMillenaireBow bow,EntityPlayer entityplayer) {
		final ItemStack itemstack1 = entityplayer.inventory.getCurrentItem();
		if (entityplayer.isUsingItem() && (itemstack1.itemID == bow.itemID)) {
			final int k = itemstack1.getMaxItemUseDuration() - entityplayer.getItemInUseCount();
			if(k >= 18) {
				bow.setBowIcon(3);
			} else if (k > 13) {
				bow.setBowIcon(2);
			} else if (k > 0) {
				bow.setBowIcon(1);
			} else {
				bow.setBowIcon(0);
			}
		} else {
			bow.setBowIcon(0);
		}
	}

	public static void updatePanel(Point p,String[][] lines,int type,Point buildingPos,long villager_id) {

		if (lines==null)
			return;

		final TileEntityPanel panel=p.getPanel(Mill.clientWorld.world);

		if (panel==null) {//panel is probably not loaded
			final PanelPacketInfo pinfo=new PanelPacketInfo(p, lines, buildingPos, type, villager_id);
			Mill.clientWorld.panelPacketInfos.add(pinfo);
			return;
		}

		panel.panelType=type;
		panel.buildingPos=buildingPos;
		panel.villager_id=villager_id;

		for (int i=0;(i<lines.length) && (i<panel.signText.length);i++) {
			panel.signText[i]=MLN.string(lines[i]);
		}
	}

	public static void putVillagerSentenceInChat(MillVillager v) {
		
		if (v.dialogueTargetFirstName!=null && !v.dialogueChat) {
			return;
		}

		int radius=0;
		
		if (Mill.proxy.isTrueClient()) {
			radius=MLN.VillagersSentenceInChatDistanceClient;
		} else {
			radius=MLN.VillagersSentenceInChatDistanceSP;
		}
		
		EntityPlayer player=Mill.proxy.getTheSinglePlayer();
		
		if (v.getPos().distanceTo(player)>radius) {
			return;
		}
		
		String gameSpeech=MillCommonUtilities.getVillagerSentence(v,player.username,false);
		String nativeSpeech=MillCommonUtilities.getVillagerSentence(v,player.username,true);				
		
		if (nativeSpeech!=null || gameSpeech!=null) {
			
			String s;
			if (v.dialogueTargetFirstName!=null) {
				s=MLN.string("other.chattosomeone",v.getName(),v.dialogueTargetFirstName+" "+v.dialogueTargetLastName)+": ";
			} else {
				s=v.getName()+": ";
			}
			
			
			if (nativeSpeech!=null) {
				s+="\247"+MLN.BLUE+nativeSpeech;
			}
			
			if (nativeSpeech!=null && gameSpeech!=null) {
				s+=" ";
			}
			
			if (gameSpeech!=null) {
				s+="\247"+MLN.DARKRED+gameSpeech;
			}
			
			Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(),v.dialogueColour, s);
		}

		
	}


}
