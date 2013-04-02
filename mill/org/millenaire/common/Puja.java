package org.millenaire.common;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class Puja implements IInventory {

	public static int PUJA_DURATION = 30;

	public static Enchantment[] enchantments=new Enchantment[]{Enchantment.efficiency,Enchantment.unbreaking,Enchantment.fortune,Enchantment.silkTouch};

	public static int getOfferingValue(ItemStack is) {
		if (is.itemID==Item.diamond.itemID)
			return 64*6;

		if (is.itemID==Item.bucketMilk.itemID)
			return 64*2;

		if (is.itemID==Item.appleGold.itemID)
			return 64+32;

		if (is.itemID==Item.ingotGold.itemID)
			return 64;

		if (is.itemID==Mill.rice.itemID)
			return 32;

		if (is.itemID==Mill.rasgulla.itemID)
			return 20;

		if ((is.itemID==Block.plantRed.blockID) || (is.itemID==Block.plantYellow.blockID))
			return 16;

		if ((is.itemID==Block.tallGrass.blockID) || (is.itemID==Item.appleRed.itemID))
			return 8;

		if ((is.itemID==Block.cloth.blockID) && (is.getItemDamage()==0))
			return 8;

		if (is.itemID==Item.melon.itemID)
			return 4;

		return 0;
	}

	private ItemStack items[];

	public Enchantment enchantmentTarget=enchantments[0];
	public int offeringProgress=0;
	public int offeringNeeded=1;
	public short pujaProgress=0;
	public Building temple=null;
	public MillVillager priest=null;

	public Puja(Building temple)
	{
		this.temple=temple;
		items = new ItemStack[getSizeInventory()];
	}

	public Puja(Building temple,NBTTagCompound tag)
	{
		this.temple=temple;
		readFromNBT(tag);
	}

	public void calculateOfferingsNeeded() {

		offeringNeeded=0;

		if ((items[4]==null) || (enchantmentTarget==null))
			return;

		final ItemStack tool=items[4];

		if (EnchantmentHelper.getEnchantmentLevel(enchantmentTarget.effectId, tool)
				>=enchantmentTarget.getMaxLevel())
			return;

		int nbother=0;
		if (tool.isItemEnchanted()) {
			final NBTTagList nbttaglist = tool.getEnchantmentTagList();
			nbother=nbttaglist.tagCount();
		}

		final int currentLevel=EnchantmentHelper.getEnchantmentLevel(enchantmentTarget.effectId, tool);

		if (currentLevel>0) {
			nbother--;
		}

		if (nbother>=3)
			return;

		int cost=50+(enchantmentTarget.getMinEnchantability(currentLevel+1)*enchantmentTarget.getMinEnchantability(currentLevel+1));

		cost=cost*(nbother+1);

		if (MLN.LogPujas>=MLN.MINOR) {
			MLN.minor(this, "Offering needed: "+cost);
		}

		offeringNeeded=cost;
	}

	public boolean canPray() {

		if (offeringNeeded<=offeringProgress)
			return false;

		if (items[0]==null)
			return false;

		return true;
	}

	public void changeEnchantment(int i) {

		if (enchantmentTarget==enchantments[i])
			return;

		enchantmentTarget=enchantments[i];
		offeringProgress=0;
		calculateOfferingsNeeded();
	}

	@Override
	public void closeChest()
	{
	}

	private void completeOffering() {

		final int currentlevel=EnchantmentHelper.getEnchantmentLevel(enchantmentTarget.effectId, items[4]);

		if (currentlevel==0) {
			items[4].addEnchantment(enchantmentTarget, 1);
		} else {
			final NBTTagList enchList = items[4].getEnchantmentTagList();

			for (int i = 0; i < enchList.tagCount(); i++)
			{
				final short id = ((NBTTagCompound)enchList.tagAt(i)).getShort("id");

				if (id == enchantmentTarget.effectId)
				{
					((NBTTagCompound)enchList.tagAt(i)).setShort("lvl", (short) (currentlevel+1));
				}
			}

		}

		offeringProgress=0;
		calculateOfferingsNeeded();

		temple.getTownHall().requestSave("Puja offering complete");

	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int nb)
	{
		if (items[slot] != null)
		{
			if (items[slot].stackSize <= nb)
			{
				final ItemStack itemstack = items[slot];
				items[slot] = null;
				return itemstack;
			}

			final ItemStack itemstack1 = items[slot].splitStack(nb);

			if (items[slot].stackSize == 0)
			{
				items[slot] = null;
			}

			return itemstack1;
		} else
			return null;
	}

	private void endPuja() {
		final ItemStack offer=items[0];

		if (offer==null)
			return;

		final int offerValue=getOfferingValue(offer);

		offeringProgress+=offerValue;

		offer.stackSize--;

		if (offer.stackSize==0) {
			items[0]=null;
		}

		if (offeringProgress>=offeringNeeded) {
			completeOffering();
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Returns the name of the inventory.
	 */
	@Override
	public String getInvName()
	{
		return MLN.string("pujas.invanme");
	}

	public int getOfferingProgressScaled(int scale) {
		if (offeringNeeded==0)
			return 0;

		return ((offeringProgress)*scale)/offeringNeeded;
	}

	public int getPujaProgressScaled(int scale) {
		return ((pujaProgress)*scale)/PUJA_DURATION;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return 5;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return items[par1];
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (items[par1] != null)
		{
			final ItemStack itemstack = items[par1];
			items[par1] = null;
			return itemstack;
		} else
			return null;
	}

	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void onInventoryChanged() {

	}

	@Override
	public void openChest()
	{
	}

	public boolean performPuja(MillVillager priest) {

		this.priest=priest;

		if (pujaProgress==0) {

			final boolean success=startPuja();

			if (success) {
				pujaProgress=1;
			}

			return success;
		} else {
			if (pujaProgress>=PUJA_DURATION) {

				endPuja();

				pujaProgress=0;
				return canPray();
			} else {
				pujaProgress+=1;
				return canPray();
			}
		}
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		final NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
		items = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			final NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			final byte byte0 = nbttagcompound.getByte("Slot");

			if ((byte0 >= 0) && (byte0 < items.length))
			{
				items[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}

		final int enchId=par1NBTTagCompound.getShort("enchantmentTarget");

		if (enchId>0) {
			enchantmentTarget = Enchantment.enchantmentsList[enchId];
		}

		if (MLN.LogPujas>=MLN.MINOR) {
			MLN.minor(this, "Reading enchantmentTarget: "+enchId+", "+enchantmentTarget);
		}

		offeringProgress = par1NBTTagCompound.getShort("offeringProgress");
		pujaProgress = par1NBTTagCompound.getShort("pujaProgress");


		if (enchantmentTarget==null) {
			enchantmentTarget=Enchantment.efficiency;
		}


		calculateOfferingsNeeded();
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		items[par1] = par2ItemStack;

		if ((par2ItemStack != null) && (par2ItemStack.stackSize > getInventoryStackLimit()))
		{
			par2ItemStack.stackSize = getInventoryStackLimit();
		}
	}

	private boolean startPuja() {
		int money=MillCommonUtilities.countMoney(this);

		if (money==0)
			return false;

		if ((offeringNeeded==0) || (offeringProgress>=offeringNeeded))
			return false;

		if (items[0]==null)
			return false;

		money-=8;

		final int denier = money % 64;
		final int denier_argent = ((money-denier)/64) % 64;
		final int denier_or = (money-denier-(denier_argent*64))/(64*64);

		if (denier==0) {
			items[1]=null;
		} else {
			items[1]=new ItemStack(Mill.denier,denier);
		}

		if (denier_argent==0) {
			items[2]=null;
		} else {
			items[2]=new ItemStack(Mill.denier_argent,denier_argent);
		}

		if (denier_or==0) {
			items[3]=null;
		} else {
			items[3]=new ItemStack(Mill.denier_or,denier_or);
		}

		return true;
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{

		if (enchantmentTarget!=null) {
			par1NBTTagCompound.setShort("enchantmentTarget", (short)enchantmentTarget.effectId);
		}

		if (MLN.LogPujas>=MLN.MINOR) {
			MLN.minor(this, "Writing enchantmentTarget: "+enchantmentTarget.effectId+", "+enchantmentTarget);
		}

		par1NBTTagCompound.setShort("offeringProgress", (short)offeringProgress);
		par1NBTTagCompound.setShort("pujaProgress", pujaProgress);
		final NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null)
			{
				final NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				items[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		par1NBTTagCompound.setTag("Items", nbttaglist);
	}
}
