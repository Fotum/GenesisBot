package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.Constants;
import org.fotum.app.objects.BotUtils;
import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GetAPIPictureCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		String filter = String.join(",", args);
		String attachUrl = BotUtils.getPictureByFilter(filter);
		if (attachUrl != null)
		{
			MessageEmbed embed = EmbedCreator.getDefault().setImage(attachUrl).build();
			event.getChannel().sendMessage(embed).queue();
		}
	}

	@Override
	public String getHelp()
	{
		return "Searches for a picture by a given filter and shows it.\n" +
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " [filter|filter1,filter2,etc...]`";
	}

	@Override
	public String getInvoke()
	{
		return "get";
	}
}
