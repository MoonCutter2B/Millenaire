package org.millenaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.millenaire.blocks.MillBlocks;
import org.millenaire.blocks.StoredPosition;
import org.millenaire.building.BuildingTypes;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MillCommand extends CommandBase
{
	@Override
	public int compareTo(ICommand arg0) { return 0; }

	@Override
	public String getCommandName() { return "mill"; }

	@Override
	public String getCommandUsage(ICommandSender sender) { return "mill <villages, loneBuildings, showBuildPoints>"; }

	@Override
	public List<String> getCommandAliases() { return new ArrayList<String>() {{ add("mill"); }}; }

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length != 1)
		{
			sender.addChatMessage(new ChatComponentText("invalid argument: use villages, loneBuildings, or showBuildPoints"));
			return;
		}
		
		if(args[0].equalsIgnoreCase("village"))
		{
			//Spit out direction and distance to all villages
			
			//test code. remove before command use.
			for(Entry ent : BuildingTypes.getCache().entrySet()) {
				sender.addChatMessage(new ChatComponentText(ent.getKey() + " - " + ent.getValue()));
			}
		}
		else if(args[0].equalsIgnoreCase("loneBuildings"))
		{
			//Spit out Distance and direction to all lone buildings
		}
		else if(args[0].equalsIgnoreCase("showBuildPoints"))
		{
			if(((StoredPosition)MillBlocks.storedPosition).getShowParticles())
			{
				((StoredPosition) MillBlocks.storedPosition).setShowParticles(false);
			}
			else
			{
				((StoredPosition) MillBlocks.storedPosition).setShowParticles(true);
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();

			if (FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().canSendCommands((player).getGameProfile()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) 
	{
		return getListOfStringsMatchingLastWord(args, new String[] {"village", "loneBuildings", "showBuildPoints"});
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) 
	{
		return false;
	}
}
