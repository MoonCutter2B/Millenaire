package org.millenaire.common.item;

import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.Building;
import org.millenaire.common.MLN;
import org.millenaire.common.Point;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods.ItemText;
import org.millenaire.common.network.ServerSender;


public class ItemParchment extends ItemText {


	public static final int villagers=1;
	public static final int buildings=2;
	public static final int items=3;
	public static final int villageBook=4;
	public static final int indianVillagers=5;
	public static final int indianBuildings=6;
	public static final int indianItems=7;
	public static final int mayanVillagers=9;
	public static final int mayanBuildings=10;
	public static final int mayanItems=11;
	public static final int japaneseVillagers=16;
	public static final int japaneseBuildings=17;
	public static final int japaneseItems=18;

	public static final int sadhu=15;



	private final int[] textsId;

	public ItemParchment(String iconName,int t) {
		this(iconName,new int[]{t});
		this.setCreativeTab(Mill.tabMillenaire);
	}

	public ItemParchment(String iconName,int[] tIds) {
		super(iconName);
		textsId=tIds;
		maxStackSize = 1;
	}

	private void displayVillageBook(EntityPlayer player, ItemStack is) {

		if (player.worldObj.isRemote)
			return;

		if (is.getItemDamage()>=Mill.getMillWorld(player.worldObj).villagesList.pos.size()) {
			ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"panels.invalidid");
			return;
		}

		final Point p = Mill.getMillWorld(player.worldObj).villagesList.pos.get(is.getItemDamage());

		final Chunk chunk=player.worldObj.getChunkFromChunkCoords(p.getChunkX(), p.getChunkZ());

		if (!chunk.isChunkLoaded) {
			ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"panels.toofar");
			return;
		}

		final Building townHall=Mill.getMillWorld(player.worldObj).getBuilding(p);

		if (townHall==null) {
			ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"panels.recordsnotloaded");
			return;
		}

		if (!townHall.isActive) {
			ServerSender.sendTranslatedSentence(player,MLN.ORANGE,"panels.toofar");
			return;
		}

		ServerSender.displayVillageBookGUI(player, p);

	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world,
			EntityPlayer entityplayer) {

		if (textsId[0]==villageBook) {
			if (!world.isRemote && (textsId[0]==villageBook)) {


				displayVillageBook(entityplayer,itemstack);
				return itemstack;
			} else
				return itemstack;
		}

		if (world.isRemote) {
			if (textsId.length==1) {
				final Vector<Vector<String>> parchment=MLN.getParchment(textsId[0]);
				if (parchment!=null) {
					DisplayActions.displayParchmentPanelGUI(entityplayer,parchment, null, 0, true);
				} else {
					Mill.proxy.localTranslatedSentence(entityplayer,MLN.ORANGE,"panels.notextfound",""+textsId[0]);
				}
			} else {

				final Vector<Vector<String>> combinedText=new Vector<Vector<String>>();

				for (int i=0;i<textsId.length;i++) {
					final Vector<Vector<String>> parchment=MLN.getParchment(textsId[i]);
					if (parchment!=null) {
						for (final Vector<String> page : parchment) {
							combinedText.add(page);
						}
					}
				}
				DisplayActions.displayParchmentPanelGUI(entityplayer,combinedText, null, 0, true);
			}
		}

		return itemstack;
	}
}
