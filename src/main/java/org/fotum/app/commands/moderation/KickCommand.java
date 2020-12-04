package org.fotum.app.commands.moderation;

import java.util.List;

import org.fotum.app.Constants;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class KickCommand implements ICommand
{

	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		Member member = event.getMember();
		Member self = event.getGuild().getSelfMember();
		List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
		
		if (args.isEmpty() || mentionedMembers.isEmpty())
		{
			channel.sendMessage("Missing arguments").queue();
			return;
		}
		
		Member target = mentionedMembers.get(0);
		String reason = String.join("", args.subList(1, args.size()));
		
		if (!member.hasPermission(Permission.KICK_MEMBERS) || !member.canInteract(target))
		{
			channel.sendMessage("You dont have permission to use this command").queue();
			return;
		}
		
		if (!self.hasPermission(Permission.KICK_MEMBERS) || !self.canInteract(target))
		{
			channel.sendMessage("I can't kick that user or I don't have the kick members permission").queue();
			return;
		}
		
		event.getGuild().kick(target, String.format("Kick by: %#s, with reason: %s", event.getAuthor(), reason)).queue();
		channel.sendMessage("Successfully kicked " + target.getEffectiveName()).queue();
	}

	@Override
	public String getHelp()
	{
		return "Kicks a user off the server\n" + 
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " <user> <reason>`";
	}

	@Override
	public String getInvoke()
	{
		return "kick";
	}
	
}
