package org.fotum.app.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fotum.app.Constants;
import org.fotum.app.objects.BotUtils;
import org.fotum.app.objects.ICommand;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class RemoveRoleOnEmoteCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		Member member = event.getMember();
		Member selfMember = event.getGuild().getSelfMember();
		List<TextChannel> channels = event.getMessage().getMentionedChannels();
		
		if (args.isEmpty())
		{
			channel.sendMessage("Correct usage is: `" + Constants.PREFIX + this.getInvoke() + " [#channel] [messageid]`").queue();
			return;
		}
		
		if (!member.hasPermission(Permission.MANAGE_ROLES))
		{
			channel.sendMessage("You need the `Manage Roles` permission to use this command").queue();
			return;
		}
		
		if (!selfMember.hasPermission(Permission.MANAGE_ROLES))
		{
			channel.sendMessage("I need the `Manage Roles` permission to use this command").queue();
			return;
		}
		
		if (channels.isEmpty())
		{
			channel.sendMessage("Missing arguments").queue();
			return;
		}
		
		String command = String.join(" ", args);
		
		long guildId = event.getGuild().getIdLong();
		long channelId = channels.get(0).getIdLong();
		long messageId = Long.valueOf(command.split("\\s+", 3)[1]);
		
		String entryId = BotUtils.generateId(Arrays.asList(guildId, channelId, messageId));
		JSONObject emotesToRemove = BotUtils.removeDataFromJSON(entryId);
		
		if (emotesToRemove != null)
		{
			JSONArray remDataKeys = emotesToRemove.names();
			for (int i = 0; i < remDataKeys.length(); i++)
			{
				String key = remDataKeys.getString(i);
				String emote = emotesToRemove.getString(key);
				
				try
				{
					event.getGuild().getTextChannelById(channelId).removeReactionById(messageId, emote).queue();
				}
				catch (InsufficientPermissionException ex)
				{
					ex.printStackTrace();
				}
			}
			
			event.getChannel().sendMessage("Successfully removed").queue(
					(message) -> message.delete().queueAfter(5, TimeUnit.SECONDS)
			);
		}
	}

	@Override
	public String getHelp()
	{
		return "Disables message reactions to give user a role\n"
				+ "Usage: `" + Constants.PREFIX + this.getInvoke() + " [#channel] [messageid]`";
	}

	@Override
	public String getInvoke()
	{
		return "remroleonemote";
	}
}
