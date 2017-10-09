package org.millenaire.items;

import org.millenaire.Millenaire;
import org.millenaire.Reference;
import org.millenaire.blocks.BlockMillSign;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillSign extends Item
{
	public ItemMillSign()
	{
		this.setCreativeTab(Millenaire.tabMillenaire);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (side == EnumFacing.DOWN)
		{
			return false;
		}
		else if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
		{
			return false;
		}
		else
		{
			pos = pos.offset(side);

			if (!playerIn.canPlayerEdit(pos, side, stack))
			{
				return false;
			}
			else if (worldIn.isRemote)
			{
				return true;
			}
			else
			{
				worldIn.setBlockState(pos, BlockMillSign.blockMillSign.getDefaultState().withProperty(BlockMillSign.FACING, side)/*Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, side)*/, 3);


				--stack.stackSize;
				TileEntity tileentity = worldIn.getTileEntity(pos);

				return true;
			}
		}
	}

	//////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	//Declarations
	public static Item itemMillSign;

	public static void preinitialize()
	{
		itemMillSign = new ItemMillSign().setCreativeTab(Millenaire.tabMillenaire).setUnlocalizedName("itemMillSign");
		GameRegistry.registerItem(itemMillSign, "itemMillSign");
	}

	@SideOnly(Side.CLIENT)
	public static void render()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		renderItem.getItemModelMesher().register(itemMillSign, 0, new ModelResourceLocation(Reference.MOD_ID + ":blockMillSign", "inventory"));
	}
}