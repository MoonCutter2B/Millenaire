package org.millenaire.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationUtil {

	public static ResourceLocation getRL(String rl) {
		return new ResourceLocation(rl);
	}
	
	public static String getString(ResourceLocation rl) {
		return rl.getResourceDomain() + ":" + rl.getResourcePath();
	}
	
	public static Item getItem(ResourceLocation rl) {
        return (Item)Item.itemRegistry.getObject(rl);
	}
}
