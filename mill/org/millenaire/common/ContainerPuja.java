package org.millenaire.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import org.millenaire.common.building.Building;
import org.millenaire.common.forge.Mill;

public class ContainerPuja extends Container {

	public static class MoneySlot extends Slot {

		PujaSacrifice shrine;

		public MoneySlot(final PujaSacrifice shrine, final int par2,
				final int par3, final int par4) {
			super(shrine, par2, par3, par4);
			this.shrine = shrine;
		}

		@Override
		public boolean isItemValid(final ItemStack is) {
			return is.getItem() == Mill.denier
					|| is.getItem() == Mill.denier_or
					|| is.getItem() == Mill.denier_argent;
		}

		@Override
		public void onSlotChanged() {
			if (!shrine.temple.worldObj.isRemote) {
				shrine.temple.getTownHall().requestSave(
						"Puja money slot changed");
			}
			super.onSlotChanged();
		}

	}

	public static class OfferingSlot extends Slot {

		PujaSacrifice shrine;

		public OfferingSlot(final PujaSacrifice shrine, final int par2,
				final int par3, final int par4) {
			super(shrine, par2, par3, par4);
			this.shrine = shrine;
		}

		@Override
		public boolean isItemValid(final ItemStack par1ItemStack) {
			return shrine.getOfferingValue(par1ItemStack) > 0;
		}

		@Override
		public void onSlotChanged() {
			if (!shrine.temple.worldObj.isRemote) {
				shrine.temple.getTownHall().requestSave(
						"Puja offering slot changed");
			}
			super.onSlotChanged();
		}
	}

	public static class ToolSlot extends Slot {

		PujaSacrifice shrine;

		public ToolSlot(final PujaSacrifice shrine, final int par2,
				final int par3, final int par4) {
			super(shrine, par2, par3, par4);
			this.shrine = shrine;
		}

		@Override
		public boolean isItemValid(final ItemStack is) {
			final Item item = is.getItem();

			if (shrine.type == PujaSacrifice.MAYAN) {
				return item instanceof ItemSword || item instanceof ItemArmor
						|| item instanceof ItemBow || item instanceof ItemAxe;
			}

			return item instanceof ItemSpade || item instanceof ItemAxe
					|| item instanceof ItemPickaxe;
		}

		@Override
		public void onSlotChanged() {

			shrine.calculateOfferingsNeeded();

			if (!shrine.temple.worldObj.isRemote) {
				shrine.temple.getTownHall().requestSave(
						"Puja tool slot changed");
			}

			super.onSlotChanged();
		}

	}

	EntityPlayer player;

	PujaSacrifice shrine;

	ToolSlot slotTool;

	public ContainerPuja(final EntityPlayer player, final Building temple) {

		try {

			this.player = player;

			shrine = temple.pujas;

			slotTool = new ToolSlot(temple.pujas, 4, 86, 37);

			addSlotToContainer(new OfferingSlot(temple.pujas, 0, 26, 19));
			addSlotToContainer(new MoneySlot(temple.pujas, 1, 8, 55));
			addSlotToContainer(new MoneySlot(temple.pujas, 2, 26, 55));
			addSlotToContainer(new MoneySlot(temple.pujas, 3, 44, 55));
			addSlotToContainer(slotTool);

			for (int i = 0; i < 3; i++) {
				for (int k = 0; k < 9; k++) {
					addSlotToContainer(new Slot(player.inventory,
							k + i * 9 + 9, 8 + k * 18, 106 + i * 18));
				}
			}

			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18,
						164));
			}
		} catch (final Exception e) {
			MLN.printException("Exception in ContainerPuja(): ", e);
		}

	}

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer,
			final int stackID) {
		ItemStack itemstack = null;
		final Slot slot = (Slot) this.inventorySlots.get(stackID);

		if (slot != null && slot.getHasStack()) {
			final ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (stackID == 4)// tool
			{
				if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (stackID > 4)// normal inv
			{
				if (itemstack1.getItem() == Mill.denier
						|| itemstack1.getItem() == Mill.denier_argent
						|| itemstack1.getItem() == Mill.denier_or) {
					if (!this.mergeItemStack(itemstack1, 1, 4, false)) {
						return null;
					}
				} else if (shrine.getOfferingValue(itemstack1) > 0) {
					if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				} else if (slotTool.isItemValid(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
						return null;
					}
				} else if (stackID >= 5 && stackID < 32) {
					if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				} else if (stackID >= 32 && stackID < 41
						&& !this.mergeItemStack(itemstack1, 5, 32, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}
