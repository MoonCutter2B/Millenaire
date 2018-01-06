package org.millenaire.items;

import java.util.List;

import org.millenaire.CommonUtilities;
import org.millenaire.Millenaire;
import org.millenaire.PlayerTracker;
import org.millenaire.blocks.BlockMillChest;
import org.millenaire.blocks.BlockMillCrops;
import org.millenaire.blocks.MillBlocks;
import org.millenaire.blocks.StoredPosition;
import org.millenaire.entities.EntityMillVillager;
import org.millenaire.entities.TileEntityMillChest;
import org.millenaire.networking.PacketExportBuilding;
import org.millenaire.networking.PacketImportBuilding;
import org.millenaire.networking.PacketSayTranslatedMessage;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMillWand extends Item
{
	ItemMillWand() { this.setMaxStackSize(1); }

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.getBlockState(pos).getBlock() == Blocks.standing_sign && worldIn.isRemote && this == MillItems.wandNegation) {
			PacketExportBuilding packet = new PacketExportBuilding(pos);
			Millenaire.simpleNetworkWrapper.sendToServer(packet);
			return true;
		}
		else if(worldIn.getBlockState(pos).getBlock() == Blocks.standing_sign && worldIn.isRemote && this == MillItems.wandSummoning) {
			PacketImportBuilding packet =  new PacketImportBuilding(pos);
			Millenaire.simpleNetworkWrapper.sendToServer(packet);
			return true;
		}
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(this == MillItems.wandNegation)
		{
			if(worldIn.getBlockState(pos).getBlock() == MillBlocks.villageStone)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
				nbt.setInteger("X", pos.getX());
				nbt.setInteger("Y", pos.getY());
				nbt.setInteger("Z", pos.getZ());

				if(worldIn.isRemote)
				{
					playerIn.openGui(Millenaire.instance, 2, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
				}
			}
		}

		if(this == MillItems.wandSummoning)
		{
			if(worldIn.getBlockState(pos).getBlock() == Blocks.gold_block)
			{
				if(!worldIn.isRemote)
				{	
					System.out.println("Gold Creation");
					Millenaire.simpleNetworkWrapper.sendTo(new PacketSayTranslatedMessage("message.notimplemented"), (EntityPlayerMP)playerIn);
					//Gui confirming action and desired village, then villageStone block is made and villageType assigned
				}
			}
			else if(worldIn.getBlockState(pos).getBlock() == Blocks.obsidian)
			{	
				System.out.println("Obsidian Creation");

				NBTTagCompound nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
				nbt.setInteger("X", pos.getX());
				nbt.setInteger("Y", pos.getY());
				nbt.setInteger("Z", pos.getZ());

				if(!worldIn.isRemote)
				{
					Millenaire.simpleNetworkWrapper.sendTo(new PacketSayTranslatedMessage("message.notimplemented"), (EntityPlayerMP)playerIn);
//					playerIn.openGui(Millenaire.instance, 4, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
				} 

			}
//			else if(worldIn.getBlockState(pos).getBlock() == Blocks.emerald_block)
//			{
//				if(!worldIn.isRemote)
//				{	
//					System.out.println("Emerald Creation");
//					worldIn.setBlockToAir(pos);
//					EntityMillVillager entity = new EntityMillVillager(worldIn, 100100, MillCulture.normanCulture);
//					System.out.println("cultured: " + entity.culture.cultureName);
//					entity = entity.setTypeAndGender(MillCulture.normanCulture.getVillagerType("normanGirl"), 1);
//					System.out.println(entity.getVillagerType());
//					entity.setChild();
//					entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
//					worldIn.spawnEntityInWorld(entity);
//				}
//				stack.stackSize--;
//				return true;
//			}
//			else if(worldIn.getBlockState(pos).getBlock() == Blocks.diamond_block)
//			{
//				if(!worldIn.isRemote)
//				{	
//					System.out.println("Diamond Creation");
//					worldIn.setBlockToAir(pos);
//					EntityMillVillager entity = new EntityMillVillager(worldIn, 100101, MillCulture.normanCulture);
//					System.out.println("cultured: ");
//					entity = entity.setTypeAndGender(MillCulture.normanCulture.getVillagerType("normanLady"), 1);
//					System.out.println(entity.getVillagerType());
//					entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
//					worldIn.spawnEntityInWorld(entity);
//				}
//				stack.stackSize--;
//				return true;
//			}
		}

		if(this == MillItems.wandCreative)
		{
			//Control whether or not you can plant crops
			if(worldIn.getBlockState(pos).getBlock() instanceof BlockMillCrops)
			{
				if(playerIn.isSneaking())
				{
					boolean hasCrop;

					//hasCrop = VillageTracker.get(worldIn).removePlayerUseCrop(playerIn, ((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos));
					hasCrop = PlayerTracker.get(playerIn).canPlayerUseCrop(((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getSeed());
					System.out.println((worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos).toString());

					if(worldIn.isRemote)
					{
						if(hasCrop)
						{
							playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can no longer plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
						}
						else
						{
                            playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " already could not plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
                        }
					}
				}
				else
				{
					boolean succeeded = false;
					if(!PlayerTracker.get(playerIn).canPlayerUseCrop(((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getSeed()))
					{
						//VillageTracker.get(worldIn).setPlayerUseCrop(playerIn, ((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getItem(worldIn, pos));
						PlayerTracker.get(playerIn).setCanUseCrop(((BlockMillCrops)worldIn.getBlockState(pos).getBlock()).getSeed(), true);
						succeeded = true;
					}

					if(worldIn.isRemote)
					{
						if(succeeded)
						{
                            playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can now plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
                        }
						else
						{
                            playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can already plant " + worldIn.getBlockState(pos).getBlock().getLocalizedName()));
                        }
					}
				}
			}
			//Allow you to plant all Crops
			else if(worldIn.getBlockState(pos).getBlock() == Blocks.cake)
			{
				if(!PlayerTracker.get(playerIn).canPlayerUseCrop(MillItems.grapes))
				{
                    PlayerTracker.get(playerIn).setCanUseCrop(MillItems.grapes, true);
                }

				if(!PlayerTracker.get(playerIn).canPlayerUseCrop(MillItems.maize))
				{
                    PlayerTracker.get(playerIn).setCanUseCrop(MillItems.maize, true);
                }

				if(!PlayerTracker.get(playerIn).canPlayerUseCrop(MillItems.rice))
				{
                    PlayerTracker.get(playerIn).setCanUseCrop(MillItems.rice, true);
                }

				if(!PlayerTracker.get(playerIn).canPlayerUseCrop(MillItems.turmeric))
				{
                    PlayerTracker.get(playerIn).setCanUseCrop(MillItems.turmeric, true);
                }

				if(worldIn.isRemote)
				{
					playerIn.addChatMessage(new ChatComponentText(playerIn.getDisplayNameString() + " can now plant everything"));
				}
			}
			//Lock and Unlock Chests
			else if(worldIn.getBlockState(pos).getBlock() instanceof BlockMillChest)
			{
				boolean isLocked = ((TileEntityMillChest)worldIn.getTileEntity(pos)).setLock();

				if(worldIn.isRemote)
				{
					if(isLocked)
					{
                        playerIn.addChatMessage(new ChatComponentText("Chest is now Locked"));
                    }
					else
					{
                        playerIn.addChatMessage(new ChatComponentText("Chest is now Unlocked"));
                    }
				}
			}
			else if(worldIn.getBlockState(pos).getBlock() instanceof StoredPosition)
			{
				if(playerIn.isSneaking())
				{
                    worldIn.setBlockToAir(pos);
                }
				else
				{
                    worldIn.setBlockState(pos, worldIn.getBlockState(pos).cycleProperty(StoredPosition.VARIANT));
                }
			}
			//Fixes All Denier in your inventory (if no specific block/entity is clicked)
			else
			{
				CommonUtilities.changeMoney(playerIn);
				if(worldIn.isRemote)
				{
                    playerIn.addChatMessage(new ChatComponentText("Fixing Denier in " + playerIn.getDisplayNameString() + "'s Inventory"));
                }
			}
		}

		if(this == MillItems.tuningFork)
		{
			IBlockState state = worldIn.getBlockState(pos);
			String output = state.getBlock().getUnlocalizedName() + " -";

			for(IProperty prop : state.getProperties().keySet())
			{
				//System.out.println(prop.getName());
				output = output.concat(" " + prop.getName() + ":" + state.getValue(prop).toString());
			}

			playerIn.addChatMessage(new ChatComponentText(output));
		}

		return false;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, EntityLivingBase entity)
	{
		if(stack.getItem() == MillItems.wandNegation && entity instanceof EntityMillVillager)
		{
			((EntityMillVillager)entity).isPlayerInteracting = true;

			NBTTagCompound nbt = new NBTTagCompound();
			player.getHeldItem().setTagCompound(nbt); 
			nbt.setInteger("ID", entity.getEntityId());

			if(player.worldObj.isRemote)
			{
				player.openGui(Millenaire.instance, 3, player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if(stack.getItem() == MillItems.wandCreative)
		{
            tooltip.add("�lCreative Mode ONLY");
        }
	}
}
