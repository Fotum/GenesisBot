package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.Constants;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements ICommand
{

	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		event.getChannel().sendMessage("Pong!").queue((message) ->
			message.editMessageFormat("Gateway ping is %sms", event.getJDA().getGatewayPing()).queue()
		);
	}

	@Override
	public String getHelp()
	{
		return "Pong!\n" +
				"Usage: `" + Constants.PREFIX + getInvoke() + "`";
	}

	@Override
	public String getInvoke()
	{
		return "ping";
	}
	
}
