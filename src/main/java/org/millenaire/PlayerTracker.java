package org.millenaire;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerTracker implements IExtendedEntityProperties
{
	private final static String IDENTIFIER = "Millenaire.PlayerInfo";

	private final EntityPlayer player;
	
	private Map<Item, Boolean> playerCropKnowledge = new HashMap<>();

	private PlayerTracker(EntityPlayer player) { this.player = player; }

	/**
	 * Used to register these extended properties for the player during EntityConstructing event
	 * This method is for convenience only; it will make your code look nicer
	 */
	public static  void register(EntityPlayer player)
	{
		player.registerExtendedProperties(PlayerTracker.IDENTIFIER, new PlayerTracker(player));
	}

	/**
	 * Returns ExtendedPlayer properties for player
	 * This method is for convenience only; it will make your code look nicer
	 */
	public static PlayerTracker get(EntityPlayer player)
	{
		return (PlayerTracker) player.getExtendedProperties(IDENTIFIER);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		NBTTagCompound cropKnowledge = new NBTTagCompound();

		for(Item i : playerCropKnowledge.keySet()) {
			cropKnowledge.setBoolean(Item.itemRegistry.getNameForObject(i).toString(), playerCropKnowledge.get(i));
		}

		properties.setTag("cropKnowledge", cropKnowledge);
		compound.setTag(IDENTIFIER, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(IDENTIFIER);
		NBTTagCompound cropKnowledge = properties.getCompoundTag("cropKnowledge");
		
		for(String s : cropKnowledge.getKeySet()) {
			Item i = Item.getByNameOrId(s);
			Boolean canPlant = cropKnowledge.getBoolean(s);
			playerCropKnowledge.put(i, canPlant);
			System.out.println(i + " " + canPlant);
		}
	}

	@Override
	public void init(Entity entity, World world)
	{
	}
	
	public void setCanUseCrop(Item cropIn, boolean canUse) { playerCropKnowledge.put(cropIn, canUse); }

	public boolean canPlayerUseCrop(Item cropIn) {
		if(playerCropKnowledge.containsKey(cropIn)) {
			return playerCropKnowledge.get(cropIn);
		}

        return false;
	}
}