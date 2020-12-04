package org.fotum.app.commands.admin;

import java.util.List;

import org.fotum.app.Constants;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetPrefixCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		Member member = event.getMember();
		TextChannel channel = event.getChannel();
		
		if (!member.hasPermission(Permission.MANAGE_SERVER))
		{
			channel.sendMessage("You need the Manage Server permission to use this command").queue();
			return;
		}
		
		if (args.isEmpty())
		{
			channel.sendMessage("Usage: `" + Constants.PREFIX + this.getInvoke() + " <prefix>`").queue();
			return;
		}
		
		String newPrefix = args.get(0);
		
		Constants.PREFIXES.put(event.getGuild().getIdLong(), newPrefix);
		channel.sendMessage("The new prefix has been set to `" + newPrefix + "`").queue();
	}

	@Override
	public String getHelp()
	{
		return "Sets the prefix for this server\n" +
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " <prefix>`";
	}

	@Override
	public String getInvoke()
	{
		return "setprefix";
	}
}
