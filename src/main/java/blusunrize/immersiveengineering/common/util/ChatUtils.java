package blusunrize.immersiveengineering.common.util;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.util.network.MessageNoSpamChatComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatUtils
{
	private static final int DELETION_ID = 3718126;
	private static int lastAdded;

	@SideOnly(Side.CLIENT)//Credit goes to WayOfFlowingTime
	public static void sendClientNoSpamMessages(IChatComponent[] messages)
	{
		GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		for(int i=DELETION_ID+messages.length-1; i<=lastAdded; i++)
			chat.deleteChatLine(i);
		for(int i=0; i<messages.length; i++)
			chat.printChatMessageWithOptionalDeletion(messages[i], DELETION_ID+i);
		lastAdded = DELETION_ID+messages.length-1;
	}

	public static void sendServerNoSpamMessages(EntityPlayer player, IChatComponent... messages)
	{
		if(messages.length>0 && player instanceof EntityPlayerMP)
			ImmersiveEngineering.packetHandler.sendTo(new MessageNoSpamChatComponents(messages), (EntityPlayerMP)player);
	}
}
