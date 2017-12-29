package org.millenaire.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ResourceLocationUtil {

	@NotNull
	public static ResourceLocation getRL(String rl) { return new ResourceLocation(rl); }
	
	@NotNull
	public static String getString(ResourceLocation rl) { return rl.getResourceDomain() + ":" + rl.getResourcePath(); }
	
	public static Item getItem(ResourceLocation rl) { return Item.itemRegistry.getObject(rl); }
}
