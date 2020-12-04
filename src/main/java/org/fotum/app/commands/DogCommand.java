package org.fotum.app.commands;

import java.util.List;

import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DogCommand implements ICommand
{

	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		WebUtils.ins.getJSONObject("https://random.dog/woof.json").async(
				(json) -> {
					String url = json.get("url").asText();
					MessageEmbed embed = EmbedCreator.getDefault().setImage(url).build();
					// #TODO: Make a permission check if the bot can send embeds
					event.getChannel().sendMessage(embed).queue();
				}
		);
	}

	@Override
	public String getHelp()
	{
		return "Shows you a random dog";
	}

	@Override
	public String getInvoke()
	{
		return "dog";
	}

}
