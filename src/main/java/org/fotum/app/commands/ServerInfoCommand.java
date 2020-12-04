package org.fotum.app.commands;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ServerInfoCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		Guild guild = event.getGuild();
		
		String generalInfo = String.format(
				"**Owner**: %s\n**Region**: %s\n**Creation Date**: %s\n**Verification Level**: %s",
				guild.getOwner().getAsMention(),
				guild.getRegion(),
				guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
				this.convertVerificationLevel(guild.getVerificationLevel())
		);
		
		String memberInfo = String.format(
				"**Total Roles**: %s\n**Total Members**: %s\n**Online Members**: %s\n**Offline Members**: %s\n**Bot Count**: %s",
				guild.getRoleCache().size(),
				guild.getMemberCache().size(),
				guild.getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.ONLINE).count(),
				guild.getMemberCache().stream().filter((m) -> m.getOnlineStatus() == OnlineStatus.OFFLINE).count(),
				guild.getMemberCache().stream().filter((m) -> m.getUser().isBot()).count()
		);
		
		MessageEmbed embed = EmbedCreator.getDefault()
				.setTitle("Server info for " + guild.getName())
				.setThumbnail(guild.getIconUrl())
				.addField("General info", generalInfo, false)
				.addField("Member info", memberInfo, false)
				.build();
		
		event.getChannel().sendMessage(embed).queue();
	}

	@Override
	public String getHelp()
	{
		return "Shows information about the server";
	}

	@Override
	public String getInvoke()
	{
		return "serverinfo";
	}
	
	private String convertVerificationLevel(VerificationLevel lvl)
	{
		String[] names = lvl.name().toLowerCase().split("_");
		StringBuilder out = new StringBuilder();
		
		for (String name : names)
		{
			out.append(Character.toUpperCase(name.charAt(0)));
			out.append(name.substring(1));
			out.append(" ");
		}
		
		return out.toString().trim();
	}
}
