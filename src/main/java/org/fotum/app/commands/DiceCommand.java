package org.fotum.app.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.fotum.app.Constants;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiceCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		int sides = 6;
		int dices = 1;
		
		TextChannel channel = event.getChannel();
		
		if (!args.isEmpty())
		{
			String[] arg = args.get(0).toLowerCase().split("d");
			dices = Integer.parseInt(arg[0].strip());
			if (arg.length > 1)
			{
				sides = Integer.parseInt(arg[1].strip());
			}
		}
		
		if (sides <= 1)
		{
			channel.sendMessage("The minimum sides is 2").queue();
			return;
		}
		
		if (sides > 100)
		{
			channel.sendMessage("The maximum sides is 100").queue();
			return;
		}
		
		if (dices > 50)
		{
			channel.sendMessage("The maximum dices is 50").queue();
			return;
		}
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder builder = new StringBuilder()
				.append("Results:\n");
		
		for (int i = 0; i < dices; i++)
		{
			builder.append("\uD83C\uDFB2 #")
				.append(i + 1)
				.append(": **")
				.append(random.nextInt(1, sides + 1))
				.append("**\n");
		}
		
		channel.sendMessage(builder.toString()).queue();
	}

	@Override
	public String getHelp()
	{
		return "Rolls a dice\n"
				+ "Usage: `" + Constants.PREFIX + this.getInvoke() + " [dices]d[sides]`";
	}

	@Override
	public String getInvoke()
	{
		return "r";
	}
}
