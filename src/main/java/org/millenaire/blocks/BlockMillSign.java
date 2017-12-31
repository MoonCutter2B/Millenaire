package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityMillSign;
import org.millenaire.items.ItemMillSign;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillSign extends BlockWallSign
{
	BlockMillSign()
	{
		super();

		this.setBlockUnbreakable();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return null; }
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	@Override
	public int getRenderType() { return -1; }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileEntityMillSign(); }
}
