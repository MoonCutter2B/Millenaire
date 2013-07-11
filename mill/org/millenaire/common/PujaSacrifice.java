package org.millenaire.common;

import java.util.Map;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;

public class PujaSacrifice implements IInventory {

	public static final int TOOL=1;
	public static final int ARMOUR=2;
	public static final int HELMET=3;
	public static final int BOOTS=4;
	public static final int SWORD_AXE=5;
	public static final int SWORD=6;
	public static final int BOW=7;
	public static final int UNBREAKABLE=8;

	public static class PrayerTarget {

		final public Enchantment enchantment;
		final public String mouseOver;
		final public int startX,startY,startXact,startYact;
		final public int toolType;

		public PrayerTarget(Enchantment enchantment,String mouseOver,int startX,int startY,int startXact,int startYact,int toolType) {
			this.enchantment=enchantment;
			this.mouseOver=mouseOver;
			this.startX=startX;
			this.startY=startY;
			this.startXact=startXact;
			this.startYact=startYact;
			this.toolType=toolType;
		}

		public boolean validForItem(Item item) {
			return PujaSacrifice.validForItem(toolType,item);
		}
	}

	public static boolean validForItem(int type,Item item) {
		if (type==TOOL) {
			return ((item instanceof ItemSpade) || (item instanceof ItemAxe) || (item instanceof ItemPickaxe));
		} else if (type==ARMOUR) {
			return (item instanceof ItemArmor);
		} else if (type==HELMET) {
			return (item instanceof ItemArmor && ((ItemArmor)item).armorType==0);
		} else if (type==BOOTS) {
			return (item instanceof ItemArmor && ((ItemArmor)item).armorType==3);
		} else if (type==SWORD_AXE) {
			return ((item instanceof ItemSword) || (item instanceof ItemAxe));
		} else if (type==SWORD) {
			return ((item instanceof ItemSword));
		} else if (type==BOW) {
			return ((item instanceof ItemBow));
		} else if (type==UNBREAKABLE) {
			return ((item instanceof ItemSword) || (item instanceof ItemArmor) || (item instanceof ItemBow));
		}
		return false;
	}

	public static PrayerTarget[] PUJA_TARGETS=new PrayerTarget[]{new PrayerTarget(Enchantment.efficiency,"pujas.god0",0,188,46,188,TOOL),
		new PrayerTarget(Enchantment.unbreaking,"pujas.god1",0,205,46,205,TOOL),
		new PrayerTarget(Enchantment.fortune,"pujas.god2",0,222,46,222,TOOL),
		new PrayerTarget(Enchantment.silkTouch,"pujas.god3",0,239,46,239,TOOL)};

	public static PrayerTarget[] MAYAN_TARGETS=new PrayerTarget[]{
		new PrayerTarget(Enchantment.protection,"mayan.god0",0,188,120,188,ARMOUR),
		new PrayerTarget(Enchantment.fireProtection,"mayan.god1",20,188,140,188,ARMOUR),
		new PrayerTarget(Enchantment.blastProtection,"mayan.god2",40,188,160,188,ARMOUR),
		new PrayerTarget(Enchantment.projectileProtection,"mayan.god3",60,188,180,188,ARMOUR),
		new PrayerTarget(Enchantment.thorns,"mayan.god4",80,188,200,188,ARMOUR),
		
		new PrayerTarget(Enchantment.respiration,"mayan.god5",100,188,120,188,HELMET),
		new PrayerTarget(Enchantment.aquaAffinity,"mayan.god6",0,208,120,208,HELMET),
		
		new PrayerTarget(Enchantment.featherFalling,"mayan.god7",20,208,140,208,BOOTS),
		
		new PrayerTarget(Enchantment.sharpness,"mayan.god8",40,208,160,208,SWORD_AXE),
		new PrayerTarget(Enchantment.smite,"mayan.god9",0,188,120,188,SWORD_AXE),
		new PrayerTarget(Enchantment.baneOfArthropods,"mayan.god10",80,188,200,188,SWORD_AXE),
				
		new PrayerTarget(Enchantment.knockback,"mayan.god11",60,208,180,208,SWORD),
		new PrayerTarget(Enchantment.fireAspect,"mayan.god12",20,188,140,188,SWORD),
		new PrayerTarget(Enchantment.looting,"mayan.god13",80,208,200,208,SWORD),
		
		new PrayerTarget(Enchantment.power,"mayan.god14",40,208,160,208,BOW),
		new PrayerTarget(Enchantment.punch,"mayan.god15",60,208,180,208,BOW),
		new PrayerTarget(Enchantment.flame,"mayan.god16",20,188,140,188,BOW),
		new PrayerTarget(Enchantment.infinity,"mayan.god17",80,208,200,208,BOW),
		
		new PrayerTarget(Enchantment.unbreaking,"mayan.god18",100,208,220,208,UNBREAKABLE)
	};

	public static int PUJA_DURATION = 30;

	public int getOfferingValue(ItemStack is) {

		if (type==PUJA)
			return getOfferingValuePuja(is);

		if (type==MAYAN)
			return getOfferingValueMayan(is);

		return 0;
	}

	public int getOfferingValueMayan(ItemStack is) {

		if (is.itemID==Item.skull.itemID)
			return 64*64;

		if (is.itemID==Item.ghastTear.itemID)
			return 64*6;

		if (is.itemID==Item.blazeRod.itemID)
			return 64;

		if (is.itemID==Mill.cacauhaa.itemID)
			return 64;

		if (is.itemID==Item.chickenRaw.itemID)
			return 1;

		if (is.itemID==Item.beefRaw.itemID)
			return 1;

		if (is.itemID==Item.porkRaw.itemID)
			return 1;

		if (is.itemID==Item.fishRaw.itemID)
			return 1;

		if (is.itemID==Item.leather.itemID)
			return 1;

		if (is.itemID==Item.dyePowder.itemID && is.getItemDamage()==0)
			return 1;

		if (is.itemID==Item.slimeBall.itemID)
			return 1;

		if (is.itemID==Item.rottenFlesh.itemID)
			return 2;

		if (is.itemID==Item.bone.itemID)
			return 2;

		if (is.itemID==Item.magmaCream.itemID)
			return 4;

		if (is.itemID==Item.gunpowder.itemID)
			return 4;

		if (is.itemID==Item.spiderEye.itemID)
			return 4;

		if (is.itemID==Item.enderPearl.itemID)
			return 6;

		return 0;
	}

	public int getOfferingValuePuja(ItemStack is) {
		if (is.itemID==Item.diamond.itemID)
			return 64*6;

		if (is.itemID==Item.bucketMilk.itemID)
			return 64*2;

		if (is.itemID==Item.appleGold.itemID)
			return 64+32;

		if (is.itemID==Item.ingotGold.itemID)
			return 64;

		if (is.itemID==Mill.rice.itemID)
			return 8;	

		if (is.itemID==Mill.rasgulla.itemID)
			return 64;

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

	public PrayerTarget currentTarget=null;
	public int offeringProgress=0;
	public int offeringNeeded=1;
	public short pujaProgress=0;
	public Building temple=null;
	public MillVillager priest=null;
	public short type=0;

	public static final short PUJA=0;
	public static final short MAYAN=1;

	public PujaSacrifice(Building temple,short type)
	{
		this.temple=temple;
		items = new ItemStack[getSizeInventory()];
		this.type=type;
	}

	public PujaSacrifice(Building temple,NBTTagCompound tag)
	{
		this.temple=temple;

		if (temple.location.tags.contains(Building.tagSacrifices))
			type=MAYAN;

		readFromNBT(tag);
	}

	public Vector<PrayerTarget> getTargets() {

		if (items[4]==null)
			return new Vector<PrayerTarget>();

		if (type==PUJA) {

			Vector<PrayerTarget> targets=new Vector<PrayerTarget>();

			for (PrayerTarget t : PUJA_TARGETS) {
				if (t.validForItem(items[4].getItem())) {
					targets.add(t);
				}
			}

			return targets;
		} else if (type==MAYAN) {
			Vector<PrayerTarget> targets=new Vector<PrayerTarget>();

			for (PrayerTarget t : MAYAN_TARGETS) {
				if (t.validForItem(items[4].getItem())) {
					targets.add(t);
				}
			}

			return targets;
		}



		return new Vector<PrayerTarget>();
	}

	public void calculateOfferingsNeeded() {

		offeringNeeded=0;

		if ((items[4]==null) || (currentTarget==null))
			return;

		final ItemStack tool=items[4];

		if (EnchantmentHelper.getEnchantmentLevel(currentTarget.enchantment.effectId, tool)
				>=currentTarget.enchantment.getMaxLevel())
			return;
		
		if (!currentTarget.enchantment.canApply(tool))
			return;

		int nbother=0;
		if (tool.isItemEnchanted()) {
			final NBTTagList nbttaglist = tool.getEnchantmentTagList();
			nbother=nbttaglist.tagCount();
			
			@SuppressWarnings("unchecked")
			Map<Integer,Integer> existingEnchantments = EnchantmentHelper.getEnchantments(tool);
			
			for (int enchId : existingEnchantments.keySet()) {
				if (enchId != currentTarget.enchantment.effectId && !Enchantment.enchantmentsList[enchId].canApplyTogether(currentTarget.enchantment))
					return;
			}
		}

		final int currentLevel=EnchantmentHelper.getEnchantmentLevel(currentTarget.enchantment.effectId, tool);

		if (currentLevel>0) {
			nbother--;
		}

		int cost=50+(currentTarget.enchantment.getMinEnchantability(currentLevel+1)*10);

		cost=cost*(nbother/2+1);

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

		if (currentTarget==getTargets().get(i))
			return;

		currentTarget=getTargets().get(i);
		offeringProgress=0;
		calculateOfferingsNeeded();
	}

	@Override
	public void closeChest()
	{
	}

	private void completeOffering() {

		final int currentlevel=EnchantmentHelper.getEnchantmentLevel(currentTarget.enchantment.effectId, items[4]);

		if (currentlevel==0) {
			items[4].addEnchantment(currentTarget.enchantment, 1);
		} else {
			final NBTTagList enchList = items[4].getEnchantmentTagList();

			for (int i = 0; i < enchList.tagCount(); i++)
			{
				final short id = ((NBTTagCompound)enchList.tagAt(i)).getShort("id");

				if (id == currentTarget.enchantment.effectId)
				{
					((NBTTagCompound)enchList.tagAt(i)).setShort("lvl", (short) (currentlevel+1));
				}
			}

		}

		offeringProgress=0;
		calculateOfferingsNeeded();

		temple.getTownHall().requestSave("Puja/sacrifice offering complete");

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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
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

			for (PrayerTarget t : getTargets()) {
				if (t.enchantment.effectId==enchId) {
					currentTarget=t;
				}
			}
		}

		if (MLN.LogPujas>=MLN.MINOR) {
			MLN.minor(this, "Reading enchantmentTarget: "+enchId+", "+currentTarget);
		}

		offeringProgress = par1NBTTagCompound.getShort("offeringProgress");
		pujaProgress = par1NBTTagCompound.getShort("pujaProgress");

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
		if (currentTarget!=null) {
			par1NBTTagCompound.setShort("enchantmentTarget", (short)currentTarget.enchantment.effectId);

			if (MLN.LogPujas>=MLN.MINOR) {
				MLN.minor(this, "Writing enchantmentTarget: "+currentTarget.enchantment.effectId+", "+currentTarget);
			}
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
