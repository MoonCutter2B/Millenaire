package org.millenaire.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemRateWrapper {

	private String itemid;
	private int stacksize;
	private int meta;
	private int ticks;
	
	public ItemRateWrapper() {
		
	}
	
	public ItemRateWrapper(ResourceLocation item, int size, int meta, int rate) {
		this.itemid = ResourceLocationUtil.getString(item);
		this.stacksize = size;
		this.meta = meta;
		this.ticks = rate;
	}
	
	public ItemStack create() {
        Item item = ResourceLocationUtil.getItem(ResourceLocationUtil.getRL(itemid));
		return new ItemStack(item, stacksize, meta);
	}
}