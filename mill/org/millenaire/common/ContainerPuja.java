package org.millenaire.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods.ItemMillenaireAxe;


public class ContainerPuja extends Container {

	public static class MoneySlot extends Slot {

		Puja shrine;

		public MoneySlot(Puja shrine,  int par2, int par3,
				int par4) {
			super(shrine, par2, par3, par4);
			this.shrine=shrine;
		}

		@Override
		public boolean isItemValid(ItemStack is) {
			return ((is.itemID==Mill.denier.itemID)
					|| (is.itemID==Mill.denier_or.itemID)
					|| (is.itemID==Mill.denier_argent.itemID));
		}

		@Override
		public void onSlotChanged() {
			shrine.temple.getTownHall().requestSave("Puja money slot changed");
			super.onSlotChanged();
		}


	}

	public static class OfferingSlot extends Slot {

		Puja shrine;

		public OfferingSlot(Puja shrine,  int par2, int par3,
				int par4) {
			super(shrine, par2, par3, par4);
			this.shrine=shrine;
		}

		@Override
		public boolean isItemValid(ItemStack par1ItemStack) {
			return Puja.getOfferingValue(par1ItemStack)>0;
		}

		@Override
		public void onSlotChanged() {
			shrine.temple.getTownHall().requestSave("Puja offering slot changed");
			super.onSlotChanged();
		}
	}

	public static class ToolSlot extends Slot {

		Puja shrine;

		public ToolSlot(Puja shrine, int par2, int par3,
				int par4) {
			super(shrine, par2, par3, par4);
			this.shrine=shrine;
		}

		@Override
		public boolean isItemValid(ItemStack is) {
			final Item item=Item.itemsList[is.itemID];

			return ((item instanceof ItemSpade) || (item instanceof ItemAxe) || (item instanceof ItemPickaxe) || (item instanceof ItemMillenaireAxe));
		}

		@Override
		public void onSlotChanged() {

			shrine.calculateOfferingsNeeded();

			shrine.temple.getTownHall().requestSave("Puja tool slot changed");

			super.onSlotChanged();
		}


	}

	EntityPlayer player;

	public ContainerPuja(EntityPlayer player, Building temple) {

		this.player=player;

		addSlotToContainer(new OfferingSlot(temple.pujas, 0, 26, 19));
		addSlotToContainer(new MoneySlot(temple.pujas, 1, 8, 55));
		addSlotToContainer(new MoneySlot(temple.pujas, 2, 26, 55));
		addSlotToContainer(new MoneySlot(temple.pujas, 3, 44, 55));
		addSlotToContainer(new ToolSlot(temple.pujas, 4, 86, 37));

		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 9; k++)
			{
				addSlotToContainer(new Slot(player.inventory, k + (i * 9) + 9, 8 + (k * 18), 106 + (i * 18)));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			addSlotToContainer(new Slot(player.inventory, j, 8 + (j * 18), 164));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
}