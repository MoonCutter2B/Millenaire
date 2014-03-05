package org.millenaire.common;


import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods;

public class ContainerTrade extends Container {

	public static class MerchantSlot extends Slot {

		public MillVillager merchant;
		public EntityPlayer player;
		public final Goods good;

		public MerchantSlot(MillVillager merchant,EntityPlayer player,Goods good,int xpos,int ypos) {
			super(null,-1, xpos, ypos);
			this.merchant=merchant;
			this.good=good;
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
		public IIcon getBackgroundIconIndex()
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
			return new ItemStack(good.item.getItem(), Math.min(merchant.getHouse().countGoods(good.item),99), good.item.meta);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack)
		{
			return true;
		}

		public String isProblem() {
			if (merchant.getHouse().countGoods(good.item) < 1)
				return MLN.string("ui.outofstock");
			final int playerMoney=MillCommonUtilities.countMoney(player.inventory);
			if (merchant.getCulture().goodsByItem.containsKey(good.item)) {
				if (playerMoney < good.getCalculatedSellingPrice(merchant))
					return MLN.string("ui.missingdeniers").replace("<0>", ""+(good.getCalculatedSellingPrice(merchant)-playerMoney));
			} else {
				MLN.error(null, "Unknown trade good: "+good);
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
			return good.getName();
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
		public IIcon getBackgroundIconIndex()
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
				return new ItemStack(good.item.getItem(), Math.min(building.countGoods(good.item.getItem(), good.item.meta),99), good.item.meta);
			else
				return new ItemStack(good.item.getItem(), Math.min(MillCommonUtilities.countChestItems(player.inventory, good.item.getItem(), good.item.meta),99), good.item.meta);
		}

		@Override
		public boolean isItemValid(ItemStack itemstack)
		{
			return true;
		}

		public String isProblem() {

			if (sellingSlot) {
				if ((building.countGoods(good.item.getItem(),good.item.meta) < 1) && (good.requiredTag != null) && !building.location.tags.contains(good.requiredTag))
					return MLN.string("ui.missingequipment")+": "+good.requiredTag;
				if ((building.countGoods(good.item.getItem(),good.item.meta) < 1) && !good.autoGenerate)
					return MLN.string("ui.outofstock");
				if ((building.getTownHall().getReputation(player.getDisplayName()) < good.minReputation))
					return MLN.string("ui.reputationneeded",building.culture.getReputationLevelLabel(good.minReputation));
				final int playerMoney=MillCommonUtilities.countMoney(player.inventory);
				if (playerMoney < good.getCalculatedSellingPrice(building,player))
					return MLN.string("ui.missingdeniers").replace("<0>", ""+(good.getCalculatedSellingPrice(building,player)-playerMoney));
			} else {
				if (MillCommonUtilities.countChestItems(player.inventory, good.item.getItem(), good.item.meta)==0)
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

		final Set<Goods> sellingGoods=building.getSellingGoods(player);

		int slotnb=0;

		if (sellingGoods!=null) {
			for (final Goods g : sellingGoods) {
				final int slotrow=slotnb/13;
				addSlotToContainer(new TradeSlot(building,player,true, g, 8+(18*(slotnb-(13*slotrow))), 32+(slotrow*18)));

				slotnb++;
			}
		}

		nbRowSelling=((slotnb)/13)+1;

		final Set<Goods> buyingGoods=building.getBuyingGoods(player);

		slotnb=0;

		if (buyingGoods!=null) {
			for (final Goods g : buyingGoods) {
				final int slotrow=slotnb/13;
				addSlotToContainer(new TradeSlot(building,player, false, g, 8+(18*(slotnb-(13*slotrow))), 86+(slotrow*18)));

				slotnb++;
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
		
		final Set<Goods> sellingGoods=merchant.merchantSells.keySet();

		if (sellingGoods!=null) {
			for (final Goods g : sellingGoods) {
				final int slotrow=slotnb/13;
				addSlotToContainer(new MerchantSlot(merchant,player, g, 8+(18*(slotnb-(13*slotrow))), 32+(slotrow*18)));

				slotnb++;
			}
		}

		nbRowSelling=((slotnb)/13)+1;
		
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
						if (playerMoney < (good.getCalculatedSellingPrice(building,player)*nbItems)) {
							nbItems=MathHelper.floor_double(playerMoney/good.getCalculatedSellingPrice(building,player));
						}
						if (!good.autoGenerate && (building.countGoods(good.item.getItem(),good.item.meta) < nbItems)) {
							nbItems=building.countGoods(good.item.getItem(),good.item.meta);
						}

						nbItems=MillCommonUtilities.putItemsInChest(player.inventory, good.item.getItem(),good.item.meta, nbItems);
						MillCommonUtilities.changeMoney(player.inventory, -good.getCalculatedSellingPrice(building,player)*nbItems,player);
						if (!good.autoGenerate) {
							building.takeGoods(good.item.getItem(),good.item.meta, nbItems);
						}

						building.adjustReputation(player, good.getCalculatedSellingPrice(building,player)*nbItems);

						building.getTownHall().adjustLanguage(player,nbItems);
					} else {

						if (MillCommonUtilities.countChestItems(player.inventory, good.item.getItem(),good.item.meta) < nbItems) {
							nbItems = MillCommonUtilities.countChestItems(player.inventory, good.item.getItem(),good.item.meta);
						}

						nbItems=building.storeGoods(good.item.getItem(),good.item.meta, nbItems);
						MillCommonUtilities.getItemsFromChest(player.inventory, good.item.getItem(),good.item.meta, nbItems);
						MillCommonUtilities.changeMoney(player.inventory, good.getCalculatedBuyingPrice(building,player)*nbItems,player);
						building.adjustReputation(player, good.getCalculatedBuyingPrice(building,player)*nbItems);
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
					if (playerMoney < (tslot.good.getCalculatedSellingPrice(merchant)*nbItems)) {
						nbItems=MathHelper.floor_double(playerMoney/tslot.good.getCalculatedSellingPrice(merchant));
					}
					if (merchant.getHouse().countGoods(tslot.good.item) < nbItems) {
						nbItems=merchant.getHouse().countGoods(tslot.good.item);
					}

					nbItems=MillCommonUtilities.putItemsInChest(player.inventory, tslot.good.item.getItem(), tslot.good.item.meta, nbItems);
					MillCommonUtilities.changeMoney(player.inventory, -tslot.good.getCalculatedSellingPrice(merchant)*nbItems,player);
					merchant.getHouse().takeGoods(tslot.good.item, nbItems);
					Mill.getMillWorld(player.worldObj).getProfile(player.getDisplayName()).adjustLanguage(merchant.getCulture().key, nbItems);
				}
				return slot.getStack();
			}
		}

		if (shiftPressed>0)
			return null;

		return super.slotClick(slotNb, buttonNumber, shiftPressed, player);
	}
}
