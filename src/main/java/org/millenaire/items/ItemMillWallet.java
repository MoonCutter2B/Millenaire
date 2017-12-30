package org.millenaire.items;

import java.util.List;

import org.millenaire.CommonUtilities;
import org.millenaire.gui.MillAchievement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillWallet extends Item
{
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(playerIn.inventory.hasItem(MillItems.denier) || playerIn.inventory.hasItem(MillItems.denierArgent) || playerIn.inventory.hasItem(MillItems.denierOr))
		{
			addDenierToWallet(itemStackIn, playerIn);
		}
		else
		{
			emptyWallet(itemStackIn, playerIn);
		}
		
        return itemStackIn;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		if(stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			
			if(nbt.hasKey("DenierOr") && nbt.getInteger("DenierOr") > 0)
			{
				String or = nbt.getInteger("DenierOr") + "o ";
				String argent = nbt.getInteger("DenierArgent") + "a ";
				String denier = nbt.getInteger("Denier") + "d";

				tooltip.add(EnumChatFormatting.YELLOW + or + EnumChatFormatting.GRAY + argent + EnumChatFormatting.GOLD + denier);
			}
			else if(nbt.hasKey("DenierArgent") && nbt.getInteger("DenierArgent") > 0)
			{
				String argent = nbt.getInteger("DenierArgent") + "a ";
				String denier = nbt.getInteger("Denier") + "d";

				tooltip.add(EnumChatFormatting.GRAY + argent + EnumChatFormatting.GOLD + denier);
			}
			else
			{
				String denier = nbt.getInteger("Denier") + "d";

				tooltip.add(EnumChatFormatting.GOLD + denier);
			}
		}
    }

    private void addDenierToWallet(ItemStack stack, EntityPlayer playerIn)
	{
		if(stack.getItem() == this)
		{
			CommonUtilities.changeMoney(playerIn);
			
			int denier = 0;
			int argent = 0;
			int or = 0;
			
			for(int i = 0; i < playerIn.inventory.getSizeInventory(); i++)
			{
				if(playerIn.inventory.getStackInSlot(i) != null)
				{
					if(playerIn.inventory.getStackInSlot(i).getItem() == MillItems.denier)
					{
						denier += playerIn.inventory.getStackInSlot(i).stackSize;
						playerIn.inventory.removeStackFromSlot(i);
					}
					else if(playerIn.inventory.getStackInSlot(i).getItem() == MillItems.denierArgent)
					{
						argent += playerIn.inventory.getStackInSlot(i).stackSize;
						playerIn.inventory.removeStackFromSlot(i);
					}
					else if(playerIn.inventory.getStackInSlot(i).getItem() == MillItems.denierOr)
					{
						or += playerIn.inventory.getStackInSlot(i).stackSize;
						playerIn.inventory.removeStackFromSlot(i);
					}
				}
			}
			
			NBTTagCompound nbt;
			
			if(!stack.hasTagCompound())
			{
				nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
			}
			else
				nbt = stack.getTagCompound();
			
			denier += nbt.getInteger("Denier");
			argent += nbt.getInteger("DenierArgent");
			or += nbt.getInteger("DenierOr");
			
			if(or >= 1)
				playerIn.addStat(MillAchievement.cresus, 1);
			
			nbt.setInteger("Denier", denier);
			nbt.setInteger("DenierArgent", argent);
			nbt.setInteger("DenierOr", or);
		}
	}

    private void emptyWallet(ItemStack stack, EntityPlayer playerIn)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			
			if(nbt.hasKey("DenierOr") && nbt.getInteger("DenierOr") > 0)
			{
				ItemStack or = new ItemStack(MillItems.denierOr, nbt.getInteger("DenierOr"), 0);
				playerIn.inventory.addItemStackToInventory(or);
			}
			
			if(nbt.hasKey("DenierArgent") && nbt.getInteger("DenierArgent") > 0)
			{
				ItemStack argent = new ItemStack(MillItems.denierArgent, nbt.getInteger("DenierArgent"), 0);
				playerIn.inventory.addItemStackToInventory(argent);
			}
			
			if(nbt.hasKey("Denier") && nbt.getInteger("Denier") > 0)
			{
				ItemStack denier = new ItemStack(MillItems.denier, nbt.getInteger("Denier"), 0);
				playerIn.inventory.addItemStackToInventory(denier);
			}
			
			stack.setTagCompound(null);
		}
	}
}
