package org.millenaire.blocks;

import java.util.Random;

import org.millenaire.Millenaire;
import org.millenaire.entities.TileEntityMillSign;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillSign extends BlockSign
{
	public BlockMillSign()
	{
		super();

		this.setBlockUnbreakable();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityMillSign();
	}

	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	//Declarations
	public static Block blockMillSign;

	public static void preinitialize()
	{
		blockMillSign = new BlockMillChest().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("blockMillSign");
		GameRegistry.registerBlock(blockMillSign, "blockMillSign");

		GameRegistry.registerTileEntity(TileEntityMillSign.class, "tileEntityMillSign");
	}

	@SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(Item.getItemFromBlock(blockMillSign), 0, new ModelResourceLocation("minecraft:sign", "inventory"));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMillSign.class, new TileEntitySignRenderer());
	}
}
