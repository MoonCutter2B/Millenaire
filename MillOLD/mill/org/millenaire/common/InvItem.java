package org.millenaire.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.millenaire.common.MLN.MillenaireException;
import org.millenaire.common.forge.Mill;

/**
 * An InvItem represents an item that can be stored in a villager's inventory,
 * and more generally is a "type" that can be stored/traded etc.
 * 
 * It is generally defined as a Minecraft item plus a meta value.
 */
public class InvItem implements Comparable<InvItem> {

	public static final int ANYENCHANTED = 1;
	public static final int ENCHANTEDSWORD = 2;

	final public Item item;
	final public Block block;
	final public ItemStack staticStack;
	final public ItemStack[] staticStackArray;
	final public int meta;
	final public int special;

	public InvItem(final Block block) throws MillenaireException {
		this(block, 0);
	}

	public InvItem(final Block block, final int meta) throws MillenaireException {
		this.block = block;
		this.item = Item.getItemFromBlock(block);
		this.meta = meta;
		staticStack = new ItemStack(item, 1, meta);
		staticStackArray = new ItemStack[] { staticStack };
		special = 0;

		checkValidity();
	}

	public InvItem(final int special) throws MillenaireException {
		this.special = special;
		staticStack = null;
		staticStackArray = new ItemStack[] { staticStack };
		item = null;
		block = null;
		meta = 0;

		checkValidity();
	}

	public InvItem(final Item item) throws MillenaireException {
		this(item, 0);
	}

	public InvItem(final Item item, final int meta) throws MillenaireException {
		this.item = item;
		if (Block.getBlockFromItem(item) != Blocks.air) {
			block = Block.getBlockFromItem(item);
		} else {
			block = null;
		}
		this.meta = meta;
		staticStack = new ItemStack(item, 1, meta);
		staticStackArray = new ItemStack[] { staticStack };
		special = 0;

		checkValidity();
	}

	public InvItem(final ItemStack is) throws MillenaireException {
		item = is.getItem();
		if (Block.getBlockFromItem(item) != Blocks.air) {
			block = Block.getBlockFromItem(item);
		} else {
			block = null;
		}
		if (is.getItemDamage() > 0) {
			meta = is.getItemDamage();
		} else {
			meta = 0;
		}
		staticStack = new ItemStack(item, 1, meta);
		staticStackArray = new ItemStack[] { staticStack };
		special = 0;

		checkValidity();
	}

	public InvItem(final String s) throws MillenaireException {
		special = 0;
		if (s.split("/").length > 2) {
			final int id = Integer.parseInt(s.split("/")[0]);

			if (Item.getItemById(id) == null) {
				MLN.printException("Tried creating InvItem with null id from string: " + s, new Exception());
				item = null;
			} else {
				item = Item.getItemById(id);
			}

			if (Block.getBlockById(id) == null) {
				block = null;
			} else {
				block = (Block) Block.blockRegistry.getObjectById(id);
			}

			meta = Integer.parseInt(s.split("/")[1]);
			staticStack = new ItemStack(item, 1, meta);
		} else {
			staticStack = null;
			item = null;
			block = null;
			meta = 0;
		}

		staticStackArray = new ItemStack[] { staticStack };

		checkValidity();
	}

	private void checkValidity() throws MillenaireException {
		if (block == Blocks.air) {
			throw new MillenaireException("Attempted to create an InvItem for air blocks.");
		}
		if (item == null && block == null && special == 0) {
			throw new MillenaireException("Attempted to create an empty InvItem.");
		}
	}

	@Override
	public int compareTo(final InvItem ii) {
		if (special > 0 || ii.special > 0) {
			return special - ii.special;
		}

		if (item == null || ii.item == null) {
			return special - ii.special;
		}

		return item.getUnlocalizedName().compareTo(ii.item.getUnlocalizedName()) + meta - ii.meta;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvItem)) {
			return false;
		}
		final InvItem other = (InvItem) obj;

		return other.item == item && other.meta == meta && other.special == special;
	}

	public Block getBlock() {
		return block;
	}

	public Item getItem() {
		return item;
	}

	public ItemStack getItemStack() {
		if (item == null) {
			return null;
		}
		return new ItemStack(item, 1, meta);
	}

	public String getName() {
		if (special == ANYENCHANTED) {
			return MLN.string("ui.anyenchanted");
		} else if (special == ENCHANTEDSWORD) {
			return MLN.string("ui.enchantedsword");
		} else if (meta == -1 && block == Blocks.log) {
			return MLN.string("ui.woodforplanks");
		} else if (meta == 0 && block == Blocks.log) {
			return MLN.string("ui.woodoak");
		} else if (meta == 1 && block == Blocks.log) {
			return MLN.string("ui.woodpine");
		} else if (meta == 2 && block == Blocks.log) {
			return MLN.string("ui.woodbirch");
		} else if (meta == 3 && block == Blocks.log) {
			return MLN.string("ui.woodjungle");
		} else if (meta == -1) {
			return Mill.proxy.getItemName(item, 0);
		} else {
			if (item != null) {
				return Mill.proxy.getItemName(item, meta);
			} else {
				MLN.printException(new MillenaireException("Trying to get the name of an invalid InvItem."));
				return "id:" + item + ";meta:" + meta;
			}
		}
	}

	public String getTranslationKey() {
		return "_item:" + Item.getIdFromItem(item) + ":" + meta;
	}

	@Override
	public int hashCode() {
		if (item == null) {
			return (meta << 8) + (special << 12);
		} else {
			return item.hashCode() + (meta << 8) + (special << 12);
		}
	}

	public boolean matches(final InvItem ii) {
		return ii.item == item && (ii.meta == meta || ii.meta == -1 || meta == -1);
	}

	@Override
	public String toString() {
		return getName() + "/" + meta;
	}
}