package org.millenaire.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.millenaire.common.MLN;
import org.millenaire.common.core.MillCommonUtilities;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.Goods.ItemText;

public class ItemPurse extends ItemText {

	private static final String ML_PURSE_DENIER = "ml_Purse_denier";
	private static final String ML_PURSE_DENIERARGENT = "ml_Purse_denierargent";
	private static final String ML_PURSE_DENIEROR = "ml_Purse_denieror";
	private static final String ML_PURSE_RAND = "ml_Purse_rand";

	public ItemPurse(int par1,String iconName) {
		super(par1,iconName);
	}
	
	public void setDeniers(ItemStack purse,EntityPlayer player,int amount) {
		
		final int denier = amount % 64;
		final int denier_argent = ((amount-denier)/64) % 64;
		final int denier_or = (amount-denier-(denier_argent*64))/(64*64);
		
		setDeniers(purse,player,denier,denier_argent,denier_or);
		
	}

	public void setDeniers(ItemStack purse,EntityPlayer player,int denier,int denierargent,int denieror) {
		if (purse.stackTagCompound == null)
		{
			purse.setTagCompound(new NBTTagCompound());
		}

		purse.stackTagCompound.setInteger(ML_PURSE_DENIER, denier);
		purse.stackTagCompound.setInteger(ML_PURSE_DENIERARGENT, denierargent);
		purse.stackTagCompound.setInteger(ML_PURSE_DENIEROR, denieror);
		
		purse.stackTagCompound.setInteger(ML_PURSE_RAND,player.worldObj.isRemote?0:1);

		setItemName(purse);
	}

	private void setItemName(ItemStack purse) {
		if (purse.stackTagCompound == null) {
			purse.setItemName(MLN.string("item.purse"));
		} else {
			int deniers=purse.stackTagCompound.getInteger(ML_PURSE_DENIER);
			int denierargent=purse.stackTagCompound.getInteger(ML_PURSE_DENIERARGENT);
			int denieror=purse.stackTagCompound.getInteger(ML_PURSE_DENIEROR);

			String label="";

			if (denieror!=0) {
				label="\247"+MLN.YELLOW+denieror+"o ";
			}
			if (denierargent!=0) {
				label+="\247"+MLN.WHITE+denierargent+"a ";
			}
			if (deniers!=0 || label.length()==0) {
				label+="\247"+MLN.ORANGE+deniers+"d";
			}

			label.trim();

			purse.setItemName("\247"+MLN.WHITE+MLN.string("item.purse")+": "+label);		
		}

	}

	public int totalDeniers(ItemStack purse) {
		if (purse.stackTagCompound == null)
			return 0;

		int deniers=purse.stackTagCompound.getInteger(ML_PURSE_DENIER);
		int denierargent=purse.stackTagCompound.getInteger(ML_PURSE_DENIERARGENT);
		int denieror=purse.stackTagCompound.getInteger(ML_PURSE_DENIEROR);

		return deniers+denierargent*64+denieror*64*64;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack purse, World world,
			EntityPlayer player) {

		if (totalDeniers(purse)>0) {
			removeDeniersFromPurse(purse,player);
		} else {
			storeDeniersInPurse(purse,player);
		}

		return super.onItemRightClick(purse, world, player);
	}

	private void removeDeniersFromPurse(ItemStack purse,
			EntityPlayer player) {
		if (purse.stackTagCompound != null) {
			int deniers=purse.stackTagCompound.getInteger(ML_PURSE_DENIER);
			int denierargent=purse.stackTagCompound.getInteger(ML_PURSE_DENIERARGENT);
			int denieror=purse.stackTagCompound.getInteger(ML_PURSE_DENIEROR);

			int result=MillCommonUtilities.putItemsInChest(player.inventory, Mill.denier.itemID, deniers);
			purse.stackTagCompound.setInteger(ML_PURSE_DENIER, deniers-result);

			result=MillCommonUtilities.putItemsInChest(player.inventory, Mill.denier_argent.itemID, denierargent);
			purse.stackTagCompound.setInteger(ML_PURSE_DENIERARGENT, denierargent-result);

			result=MillCommonUtilities.putItemsInChest(player.inventory, Mill.denier_or.itemID, denieror);
			purse.stackTagCompound.setInteger(ML_PURSE_DENIEROR, denieror-result);
			
			purse.stackTagCompound.setInteger(ML_PURSE_RAND,player.worldObj.isRemote?0:1);

			setItemName(purse);
		}
	}

	private void storeDeniersInPurse(ItemStack purse,
			EntityPlayer player) {

		int deniers=MillCommonUtilities.getItemsFromChest(player.inventory, Mill.denier.itemID, 0, Integer.MAX_VALUE);
		int denierargent=MillCommonUtilities.getItemsFromChest(player.inventory, Mill.denier_argent.itemID, 0, Integer.MAX_VALUE);
		int denieror=MillCommonUtilities.getItemsFromChest(player.inventory, Mill.denier_or.itemID, 0, Integer.MAX_VALUE);

		int total=totalDeniers(purse)+deniers+denierargent*64+denieror*64*64;

		final int new_denier = total % 64;
		final int new_denier_argent = ((total-new_denier)/64) % 64;
		final int new_denier_or = (total-new_denier-(new_denier_argent*64))/(64*64);

		setDeniers(purse,player,new_denier,new_denier_argent,new_denier_or);
	}




}
