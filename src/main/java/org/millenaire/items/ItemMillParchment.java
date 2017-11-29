package org.millenaire.items;

import org.millenaire.Millenaire;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMillParchment extends ItemWritableBook
{
	public String title;
	public String[] contents;
	
	public ItemMillParchment(String titleIn, String[] contentIn)
	{
		title = titleIn;
		contents = contentIn;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(worldIn.isRemote)
		{
			playerIn.openGui(Millenaire.instance, 0, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
		}
		
        return itemStackIn;
    }
}
