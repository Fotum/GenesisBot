package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import com.fasterxml.jackson.databind.JsonNode;

import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MemeCommand implements ICommand
{	
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		WebUtils.ins.getJSONObject("https://apis.duncte123.me/meme").async(
				(json) -> {
					JsonNode data = json.get("data");
					String url = data.get("image").asText();
					MessageEmbed embed = EmbedCreator.getDefault().setImage(url)
							.setTitle(data.get("title").asText(), data.get("url").asText())
							.build();
					// #TODO: Make a permission check if the bot can send embeds
					event.getChannel().sendMessage(embed).queue();
				}
		);
	}

	@Override
	public String getHelp()
	{
		return "Shows you a random meme";
	}

	@Override
	public String getInvoke()
	{
		return "meme";
	}

}
