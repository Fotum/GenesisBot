package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.objects.BotUtils;
import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TeaCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		String attachUrl = BotUtils.getPictureByFilter("tea");
		if (attachUrl != null)
		{
			MessageEmbed embed = EmbedCreator.getDefault().setImage(attachUrl).build();
			event.getChannel().sendMessage(embed).queue();
		}
	}

	@Override
	public String getHelp()
	{
		return "Shows you random tea image";
	}

	@Override
	public String getInvoke()
	{
		return "tea";
	}
}
