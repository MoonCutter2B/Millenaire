package org.millenaire;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MillPacket implements IMessage
{
	private int eventID;
	private boolean messageIsValid;
	
	// for use by the message handler only.
	public MillPacket()
	{
		messageIsValid = false;
	}
	
	public MillPacket(int IDin)
	{
		eventID = IDin;
		messageIsValid = true;
	}
	
	public boolean isMessageValid() 
	{
		return messageIsValid;
	}
	
	public int getID()
	{
		return eventID;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		try
		{
			eventID = buf.readInt();
		}
		catch(IndexOutOfBoundsException ioe)
		{
			System.err.println("Exception while reading MillPacket: " + ioe);
		}
		messageIsValid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		if(!messageIsValid)
			return;
		
		buf.writeInt(eventID);
	}

}
