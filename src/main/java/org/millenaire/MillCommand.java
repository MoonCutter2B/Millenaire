package org.millenaire;

import java.util.ArrayList;
import java.util.List;

import org.millenaire.blocks.StoredPosition;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MillCommand implements ICommand {
	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "mill";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mill <villages, loneBuildings, showBuildPoints>";
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> output = new ArrayList<String>();
		output.add("mill");
		return output;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length > 1) {
			sender.addChatMessage(
					new ChatComponentText("invalid argument: use villages, loneBuildings, or showBuildPoints"));
			return;
		}

		if (args[0].equalsIgnoreCase("village")) {
			// Spit out direction and distance to all villages
		} else if (args[0].equalsIgnoreCase("loneBuildings")) {
			// Spit out Distance and direction to all lone buildings
		} else if (args[0].equalsIgnoreCase("showBuildPoints")) {
			if (((StoredPosition) StoredPosition.storedPosition).getShowParticles())
				((StoredPosition) StoredPosition.storedPosition).setShowParticles(false);
			else
				((StoredPosition) StoredPosition.storedPosition).setShowParticles(true);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender == null || !(sender.getCommandSenderEntity() instanceof EntityPlayer))
			return false;
		else {
			EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();

			if (FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
					.canSendCommands((player).getGameProfile()))
				return true;
			else
				return false;
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
