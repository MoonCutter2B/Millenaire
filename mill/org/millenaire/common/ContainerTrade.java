package org.millenaire.common;


import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

import org.millenaire.common.MillVillager.InvItem;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;

public class ContainerTrade extends Container {

	public static class MerchantSlot extends Slot {

		public MillVillager merchant;
		public EntityPlayer player;
		public InvItem item;

		public MerchantSlot(MillVillager merchant,EntityPlayer player,InvItem item,int xpos,int ypos) {
			super(null,-1, xpos, ypos);
			this.merchant=merchant;
			this.item=item;
			this.player=player;
		}

		@Override
		public ItemStack decrStackSize(int i)
		{
			return null;
		}

		public boolean func_25014_f()
		{
			return false;
		}

		@Override
		public Icon getBackgroundIconIndex()
		{
			return null;
		}

		@Override
		public boolean getHasStack()
		{
			return getStack() != null;
		}

		@Override
		public int getSlotStackLimit()
		{
			return 0;
		}

		@Override
		public ItemStack getStack() {
			return new ItemStack(item.id(), Math.min(merchant.getHouse().countGoods(item),99), item.meta);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack)
		{
			return true;
		}

		public String isProblem() {
			if (merchant.getHouse().countGoods(item) < 1)
				return MLN.string("ui.outofstock");
			final int playerMoney=MillCommonUtilities.countMoney(player.inventory);
			if (merchant.getCulture().goodsByItem.containsKey(item)) {
				if (playerMoney < merchant.getForeignMerchantPrice(item))
					return MLN.string("ui.missingdeniers").replace("<0>", ""+(merchant.getForeignMerchantPrice(item)-playerMoney));
			} else {
				MLN.error(null, "Unknown trade good: "+item);
			}
			return null;
		}

		@Override
		public void onSlotChanged()
		{

		}

		@Override
		public void putStack(ItemStack itemstack)
		{

		}

		@Override
		public String toString() {
			return item.getName();
		}
	}

	public static class TradeSlot extends Slot {

		public final Building building;

		public final EntityPlayer player;

		public final Goods good;

		public final boolean sellingSlot;

		public TradeSlot(Building building,EntityPlayer player,boolean sellingSlot, Goods good,int xpos,int ypos) {
			super(player.inventory,-1, xpos, ypos);
			this.building=building;
			this.good=good;
			this.player=player;
			this.sellingSlot=sellingSlot;
		}

		@Override
		public ItemStack decrStackSize(int i)
		{
			return null;
		}

		public boolean func_25014_f()
		{
			return false;
		}


		@Override
		public Icon getBackgroundIconIndex()
		{
			return null;
		}

		@Override
		public boolean getHasStack()
		{
			return getStack() != null;
		}

		@Override
		public int getSlotStackLimit()
		{
			return 0;
		}

		@Override
		public ItemStack getStack() {
			if (sellingSlot)
				return new ItemStack(good.item.id(), Math.min(building.countGoods(good.item.id(), good.item.meta),99), good.item.meta);
			else
				return new ItemStack(good.item.id(), Math.min(MillCommonUtilities.countChestItems(player.inventory, good.item.id(), good.item.meta),99), good.item.meta);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack)
		{
			return true;
		}

		public String isProblem() {

			if (sellingSlot) {
				if ((building.countGoods(good.item.id(),good.item.meta) < 1) && (good.requiredTag != null) && !building.location.tags.contains(good.requiredTag))
					return MLN.string("ui.missingequipment")+": "+good.requiredTag;
				if ((building.countGoods(good.item.id(),good.item.meta) < 1) && !good.autoGenerate)
					return MLN.string("ui.outofstock");
				if ((building.getTownHall().getReputation(player.username) < good.minReputation))
					return MLN.string("ui.reputationneeded",building.culture.getReputationLevel(good.minReputation).label);
				final int playerMoney=MillCommonUtilities.countMoney(player.inventory);
				if (playerMoney < good.getSellingPrice(building.getTownHall()))
					return MLN.string("ui.missingdeniers").replace("<0>", ""+(good.getSellingPrice(building.getTownHall())-playerMoney));
			} else {
				if (MillCommonUtilities.countChestItems(player.inventory, good.item.id(), good.item.meta)==0)
					return MLN.string("ui.noneininventory");
			}
			return null;
		}

		@Override
		public void onSlotChanged()
		{

		}
		@Override
		public void putStack(ItemStack itemstack)
		{

		}
		@Override
		public String toString() {
			return good.name+(sellingSlot?MLN.string("ui.selling"):MLN.string("ui.buying"));
		}
	}

	private Building building;
	private MillVillager merchant;

	public int nbRowSelling=0,nbRowBuying=0;

	public ContainerTrade(EntityPlayer player, Building building) {

		this.building = building;

		final Vector<Goods> sellingGoods=building.getSellingGoods();

		int slotnb=0;

		if (sellingGoods!=null) {
			for (final Goods g : sellingGoods) {
				if (g.getSellingPrice(building.getTownHall()) > 0) {
					final int slotrow=slotnb/13;
					addSlotToContainer(new TradeSlot(building,player,true, g, 8+(18*(slotnb-(13*slotrow))), 32+(slotrow*18)));

					slotnb++;
				}
			}
		}

		nbRowSelling=((slotnb)/13)+1;

		final Vector<Goods> buyingGoods=building.getBuyingGoods();

		slotnb=0;

		if (buyingGoods!=null) {
			for (final Goods g : buyingGoods) {

				if (g.getBuyingPrice(building.getTownHall()) > 0) {

					final int slotrow=slotnb/13;
					addSlotToContainer(new TradeSlot(building,player, false, g, 8+(18*(slotnb-(13*slotrow))), 86+(slotrow*18)));

					slotnb++;
				} else {
					if (MLN.Selling>=MLN.MAJOR) {
						MLN.major(this, "Removing trade good "+g.name+" for having price "+g.getBuyingPrice(building.getTownHall()));
					}
				}
			}
		}

		nbRowBuying=((slotnb)/13)+1;

		for(int l = 0; l < 3; l++)
		{
			for(int k1 = 0; k1 < 9; k1++)
			{
				addSlotToContainer(new Slot(player.inventory, k1 + (l * 9) + 9, 8 + (k1 * 18) + 36, 103 + (l * 18) + 37));
			}

		}

		for(int i1 = 0; i1 < 9; i1++)
		{
			addSlotToContainer(new Slot(player.inventory, i1, 8 + (i1 * 18) + 36, 161 + 37));
		}
	}

	public ContainerTrade(EntityPlayer player, MillVillager merchant) {

		this.merchant = merchant;

		int slotnb=0;

		for (final InvItem key : merchant.vtype.foreignMerchantStock.keySet()) {
			if (merchant.getCulture().goodsByItem.containsKey(key)) {
				if (merchant.getHouse().countGoods(key)>0) {
					if (slotnb<13) {
						addSlotToContainer(new MerchantSlot(merchant,player, key, 8+(18*slotnb), 32));
					} else {
						addSlotToContainer(new MerchantSlot(merchant,player, key, 8+(18*(slotnb-13)), 50));
					}
					slotnb++;
				}
			}
		}


		for(int l = 0; l < 3; l++)
		{
			for(int k1 = 0; k1 < 9; k1++)
			{
				addSlotToContainer(new Slot(player.inventory, k1 + (l * 9) + 9, 8 + (k1 * 18) + 36, 103 + (l * 18) + 37));
			}

		}

		for(int i1 = 0; i1 < 9; i1++)
		{
			addSlotToContainer(new Slot(player.inventory, i1, 8 + (i1 * 18) + 36, 161 + 37));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true ;
	}

	@Override
	public ItemStack slotClick(int slotNb, int buttonNumber, int shiftPressed, EntityPlayer player) {

		if ((slotNb>=0) && (slotNb<inventorySlots.size())) {

			final Slot slot = (Slot) inventorySlots.get(slotNb);

			if ((slot != null) && (slot instanceof TradeSlot)) {
				final TradeSlot tslot = (TradeSlot) slot;
				final Goods good=tslot.good;

				int nbItems=1;

				if (shiftPressed>0) {
					nbItems=64;
				} else if (buttonNumber==1) {
					nbItems=8;
				}

				if (tslot.isProblem()==null) {

					final int playerMoney=MillCommonUtilities.countMoney(player.inventory);

					if (tslot.sellingSlot) {
						if (playerMoney < (good.getSellingPrice(building.getTownHall())*nbItems)) {
							nbItems=MathHelper.floor_double(playerMoney/good.getSellingPrice(building.getTownHall()));
						}
						if (!good.autoGenerate && (building.countGoods(good.item.id(),good.item.meta) < nbItems)) {
							nbItems=building.countGoods(good.item.id(),good.item.meta);
						}

						nbItems=MillCommonUtilities.putItemsInChest(player.inventory, good.item.id(),good.item.meta, nbItems);
						MillCommonUtilities.changeMoney(player.inventory, -good.getSellingPrice(building.getTownHall())*nbItems,player);
						if (!good.autoGenerate) {
							building.takeGoods(good.item.id(),good.item.meta, nbItems);
						}

						building.adjustReputation(player, good.getSellingPrice(building.getTownHall())*nbItems);

						building.getTownHall().adjustLanguage(player,nbItems);
					} else {

						if (MillCommonUtilities.countChestItems(player.inventory, good.item.id(),good.item.meta) < nbItems) {
							nbItems = MillCommonUtilities.countChestItems(player.inventory, good.item.id(),good.item.meta);
						}

						nbItems=building.storeGoods(good.item.id(),good.item.meta, nbItems);
						MillCommonUtilities.getItemsFromChest(player.inventory, good.item.id(),good.item.meta, nbItems);
						MillCommonUtilities.changeMoney(player.inventory, good.getBuyingPrice(building.getTownHall())*nbItems,player);
						building.adjustReputation(player, good.getBuyingPrice(building.getTownHall())*nbItems);
						building.getTownHall().adjustLanguage(player,nbItems);

					}
				}

				return slot.getStack();
			} else if ((slot != null) && (slot instanceof MerchantSlot)) {
				final MerchantSlot tslot = (MerchantSlot) slot;
				int nbItems=1;

				if (shiftPressed>0) {
					nbItems=64;
				} else if (buttonNumber==1) {
					nbItems=8;
				}

				if (tslot.isProblem()==null) {

					final int playerMoney=MillCommonUtilities.countMoney(player.inventory);
					if (playerMoney < (merchant.getForeignMerchantPrice(tslot.item)*nbItems)) {
						nbItems=MathHelper.floor_double(playerMoney/merchant.getForeignMerchantPrice(tslot.item));
					}
					if (merchant.getHouse().countGoods(tslot.item) < nbItems) {
						nbItems=merchant.getHouse().countGoods(tslot.item);
					}

					nbItems=MillCommonUtilities.putItemsInChest(player.inventory, tslot.item.id(), tslot.item.meta, nbItems);
					MillCommonUtilities.changeMoney(player.inventory, -merchant.getForeignMerchantPrice(tslot.item)*nbItems,player);
					merchant.getHouse().takeGoods(tslot.item, nbItems);
					Mill.getMillWorld(player.worldObj).getProfile(player.username).adjustLanguage(merchant.getCulture().key, nbItems);
				}
				return slot.getStack();
			}
		}

		if (shiftPressed>0)
			return null;

		return super.slotClick(slotNb, buttonNumber, shiftPressed, player);
	}
}
