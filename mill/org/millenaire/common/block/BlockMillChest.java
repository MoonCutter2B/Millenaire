package org.millenaire.common.block;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.millenaire.client.network.ClientSender;
import org.millenaire.common.Point;
import org.millenaire.common.TileEntityMillChest;
import org.millenaire.common.TileEntityMillChest.InventoryMillLargeChest;
import org.millenaire.common.forge.Mill;

public class BlockMillChest extends BlockChest {

	public static ContainerChest createContainer(final World world,
			final int i, final int j, final int k,
			final EntityPlayer entityplayer) {
		final TileEntityMillChest lockedchest = (TileEntityMillChest) world
				.getTileEntity(i, j, k);

		final IInventory chest = getInventory(lockedchest, world, i, j, k);

		return new ContainerChest(entityplayer.inventory, chest);
	}

	// Copied from BlockChest
	private static boolean func_149953_o(final World p_149953_0_,
			final int p_149953_1_, final int p_149953_2_, final int p_149953_3_) {
		final Iterator iterator = p_149953_0_.getEntitiesWithinAABB(
				EntityOcelot.class,
				AxisAlignedBB.getBoundingBox(p_149953_1_, p_149953_2_ + 1,
						p_149953_3_, p_149953_1_ + 1, p_149953_2_ + 2,
						p_149953_3_ + 1)).iterator();
		EntityOcelot entityocelot;

		do {
			if (!iterator.hasNext()) {
				return false;
			}

			final Entity entity = (Entity) iterator.next();
			entityocelot = (EntityOcelot) entity;
		} while (!entityocelot.isSitting());

		return true;
	}

	public static IInventory getInventory(
			final TileEntityMillChest lockedchest, final World world,
			final int i, final int j, final int k) {

		final String largename = lockedchest.getInvLargeName();

		IInventory chest = lockedchest;

		final Block block = world.getBlock(i, j, k);

		if (world.getBlock(i - 1, j, k) == block) {
			chest = new InventoryLargeChest(largename,
					(TileEntityChest) world.getTileEntity(i - 1, j, k), chest);
		}
		if (world.getBlock(i + 1, j, k) == block) {
			chest = new InventoryLargeChest(largename, chest,
					(TileEntityChest) world.getTileEntity(i + 1, j, k));
		}
		if (world.getBlock(i, j, k - 1) == block) {
			chest = new InventoryLargeChest(largename,
					(TileEntityChest) world.getTileEntity(i, j, k - 1), chest);
		}
		if (world.getBlock(i, j, k + 1) == block) {
			chest = new InventoryLargeChest(largename, chest,
					(TileEntityChest) world.getTileEntity(i, j, k + 1));
		}

		return chest;
	}

	public BlockMillChest() {
		super(2);
	}

	public BlockMillChest(final int blockID) {
		super(0);
		this.setCreativeTab(Mill.tabMillenaire);
	}

	@Override
	public void breakBlock(final World p_149749_1_, final int p_149749_2_,
			final int p_149749_3_, final int p_149749_4_,
			final Block p_149749_5_, final int p_149749_6_) {

	}

	@Override
	public TileEntity createNewTileEntity(final World world,
			final int p_149915_2_) {
		return new TileEntityMillChest();
	}

	@Override
	public TileEntity createTileEntity(final World world, final int metadata) {
		return new TileEntityMillChest();
	}

	@Override
	public IInventory func_149951_m(final World p_149951_1_,
			final int p_149951_2_, final int p_149951_3_, final int p_149951_4_) {
		final TileEntityMillChest lockedchest = (TileEntityMillChest) p_149951_1_
				.getTileEntity(p_149951_2_, p_149951_3_, p_149951_4_);

		IInventory chest = lockedchest;

		if (lockedchest == null) {
			return null;
		} else if (p_149951_1_.isSideSolid(p_149951_2_, p_149951_3_ + 1,
				p_149951_4_, DOWN)) {
			return null;
		} else if (func_149953_o(p_149951_1_, p_149951_2_, p_149951_3_,
				p_149951_4_)) {
			return null;
		} else if (p_149951_1_.getBlock(p_149951_2_ - 1, p_149951_3_,
				p_149951_4_) == this
				&& (p_149951_1_.isSideSolid(p_149951_2_ - 1, p_149951_3_ + 1,
						p_149951_4_, DOWN) || func_149953_o(p_149951_1_,
						p_149951_2_ - 1, p_149951_3_, p_149951_4_))) {
			return null;
		} else if (p_149951_1_.getBlock(p_149951_2_ + 1, p_149951_3_,
				p_149951_4_) == this
				&& (p_149951_1_.isSideSolid(p_149951_2_ + 1, p_149951_3_ + 1,
						p_149951_4_, DOWN) || func_149953_o(p_149951_1_,
						p_149951_2_ + 1, p_149951_3_, p_149951_4_))) {
			return null;
		} else if (p_149951_1_.getBlock(p_149951_2_, p_149951_3_,
				p_149951_4_ - 1) == this
				&& (p_149951_1_.isSideSolid(p_149951_2_, p_149951_3_ + 1,
						p_149951_4_ - 1, DOWN) || func_149953_o(p_149951_1_,
						p_149951_2_, p_149951_3_, p_149951_4_ - 1))) {
			return null;
		} else if (p_149951_1_.getBlock(p_149951_2_, p_149951_3_,
				p_149951_4_ + 1) == this
				&& (p_149951_1_.isSideSolid(p_149951_2_, p_149951_3_ + 1,
						p_149951_4_ + 1, DOWN) || func_149953_o(p_149951_1_,
						p_149951_2_, p_149951_3_, p_149951_4_ + 1))) {
			return null;
		} else {
			final String largename = lockedchest.getInvLargeName();

			if (p_149951_1_.getBlock(p_149951_2_ - 1, p_149951_3_, p_149951_4_) == this) {
				chest = new InventoryMillLargeChest(largename,
						(TileEntityChest) p_149951_1_.getTileEntity(
								p_149951_2_ - 1, p_149951_3_, p_149951_4_),
						(IInventory) lockedchest);
			}

			if (p_149951_1_.getBlock(p_149951_2_ + 1, p_149951_3_, p_149951_4_) == this) {
				chest = new InventoryMillLargeChest(largename,
						(IInventory) lockedchest,
						(TileEntityChest) p_149951_1_.getTileEntity(
								p_149951_2_ + 1, p_149951_3_, p_149951_4_));
			}

			if (p_149951_1_.getBlock(p_149951_2_, p_149951_3_, p_149951_4_ - 1) == this) {
				chest = new InventoryMillLargeChest(largename,
						(TileEntityChest) p_149951_1_.getTileEntity(
								p_149951_2_, p_149951_3_, p_149951_4_ - 1),
						(IInventory) lockedchest);
			}

			if (p_149951_1_.getBlock(p_149951_2_, p_149951_3_, p_149951_4_ + 1) == this) {
				chest = new InventoryMillLargeChest(largename,
						(IInventory) lockedchest,
						(TileEntityChest) p_149951_1_.getTileEntity(
								p_149951_2_, p_149951_3_, p_149951_4_ + 1));
			}

			return chest;
		}
	}

	@Override
	public boolean onBlockActivated(final World world, final int i,
			final int j, final int k, final EntityPlayer entityplayer,
			final int par6, final float par7, final float par8, final float par9) {
		if (world.isRemote) {
			ClientSender.activateMillChest(entityplayer, new Point(i, j, k));
		}
		return true;
	}

	@Override
	public void onBlockClicked(final World world, final int i, final int j,
			final int k, final EntityPlayer entityplayer) {

		if (entityplayer.inventory.getCurrentItem() != null
				&& entityplayer.inventory.getCurrentItem().getItem() != Mill.summoningWand) {
			super.onBlockClicked(world, i, j, k, entityplayer);
		}

	}

	@Override
	public int quantityDropped(final Random random) {
		return 0;
	}

	public boolean unifyMillChests(final World p_149952_1_,
			final int p_149952_2_, final int p_149952_3_, final int p_149952_4_) {
		return p_149952_1_.getBlock(p_149952_2_, p_149952_3_, p_149952_4_) != this ? false
				: p_149952_1_.getBlock(p_149952_2_ - 1, p_149952_3_,
						p_149952_4_) == this ? true
						: p_149952_1_.getBlock(p_149952_2_ + 1, p_149952_3_,
								p_149952_4_) == this ? true : p_149952_1_
								.getBlock(p_149952_2_, p_149952_3_,
										p_149952_4_ - 1) == this ? true
								: p_149952_1_.getBlock(p_149952_2_,
										p_149952_3_, p_149952_4_ + 1) == this;
	}

}
