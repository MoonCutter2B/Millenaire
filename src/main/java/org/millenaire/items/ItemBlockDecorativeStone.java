package org.millenaire.items;

import org.millenaire.blocks.BlockDecorativeStone;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDecorativeStone extends ItemBlock
{

	public ItemBlockDecorativeStone(Block block) 
	{
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public String getUnlocalizedName(ItemStack stack)
    {
        return ((BlockDecorativeStone)this.block).getUnlocalizedName(stack.getMetadata());
    }
	
	public int getMetadata(int damage)
    {
        return damage;
    }
}
