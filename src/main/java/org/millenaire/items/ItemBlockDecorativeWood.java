package org.millenaire.items;

import org.millenaire.blocks.BlockDecorativeWood;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDecorativeWood extends ItemBlock {

	public ItemBlockDecorativeWood(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public String getUnlocalizedName(ItemStack stack) {
		return ((BlockDecorativeWood) this.block).getUnlocalizedName(stack.getMetadata());
	}

	public int getMetadata(int damage) {
		return damage;
	}
}
