package org.fotum.app.commands.moderation;

import java.util.List;

import org.fotum.app.Constants;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		Member member = event.getMember();
		Member self = event.getGuild().getSelfMember();
		List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
		
		if (args.isEmpty() || args.size() < 2)
		{
			channel.sendMessage("Missing arguments, correct usage: `" + Constants.PREFIX + this.getInvoke() + " <user> <reason>`").queue();
			return;
		}
		
		if (mentionedMembers.isEmpty())
		{
			channel.sendMessage("You have to mention a user that you want to ban").queue();
			return;
		}
		
		Member target = mentionedMembers.get(0);
		String reason = String.join("", args.subList(1, args.size()));
		
		if (!member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(target))
		{
			channel.sendMessage("You dont have permission to use this command").queue();
			return;
		}

		if (!self.hasPermission(Permission.BAN_MEMBERS) || !self.canInteract(target))
		{
			channel.sendMessage("I can't ban that user or I don't have the ban members permission").queue();
			return;
		}
		
		event.getGuild().ban(target, 1).reason(String.format("Ban by: %#s, with reason: %s", event.getAuthor(), reason)).queue();
		channel.sendMessage("Successfully banned " + target.getEffectiveName()).queue();
	}

	@Override
	public String getHelp()
	{
		return "Bans a user from the server\n" + 
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " <user> <reason>`";
	}

	@Override
	public String getInvoke()
	{
		return "ban";
	}
}
