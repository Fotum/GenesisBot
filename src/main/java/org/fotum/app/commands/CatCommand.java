package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CatCommand implements ICommand
{

	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		WebUtils.ins.scrapeWebPage("https://api.thecatapi.com/api/images/get?format=xml&results_per_page=1").async(
				(document) -> {
					String url = document.getElementsByTag("url").first().html();
					MessageEmbed embed = EmbedCreator.getDefault().setImage(url).build();
					// #TODO: Make a permission check if the bot can send embeds
					event.getChannel().sendMessage(embed).queue();
				}
		);
	}

	@Override
	public String getHelp()
	{
		return "Shows you a random cat";
	}

	@Override
	public String getInvoke()
	{
		return "cat";
	}

}
