package org.fotum.app;

import java.util.Arrays;

import org.fotum.app.objects.BotUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class RoleOnEmoteHandler
{
	void handle(MessageReactionAddEvent event)
	{
		Member selfMember = event.getGuild().getSelfMember();
		if (!selfMember.hasPermission(Permission.MANAGE_ROLES))
		{
			return;
		}
		
		long guildId = event.getGuild().getIdLong();
		long channelId = event.getChannel().getIdLong();
		long messageId = event.getMessageIdLong();
		long userId = event.getMember().getIdLong();
		String emote = event.getReactionEmote().getAsReactionCode();
		
		String entryId = BotUtils.generateId(Arrays.asList(guildId, channelId, messageId));
		JSONObject dataObj = BotUtils.getDataFromJSON(entryId);
		if (dataObj == null)
		{
			return;
		}
		
		JSONObject emotesObj = dataObj.getJSONObject("data");
		JSONArray keys = emotesObj.names();
		for (int i = 0; i < keys.length(); i++)
		{
			String roleId = keys.getString(i);
			String emoteId = emotesObj.getString(roleId);
			Role role = event.getGuild().getRoleById(roleId.replaceAll("\\D", ""));
			if (emoteId.equalsIgnoreCase(emote) && !event.getGuild().getMemberById(userId).getRoles().contains(role))
			{
				event.getGuild().addRoleToMember(userId, role).queue();
				return;
			}
		}
	}
	
	void handle(MessageReactionRemoveEvent event)
	{
		Member selfMember = event.getGuild().getSelfMember();
		if (!selfMember.hasPermission(Permission.MANAGE_ROLES))
		{
			return;
		}
		
		long guildId = event.getGuild().getIdLong();
		long channelId = event.getChannel().getIdLong();
		long messageId = event.getMessageIdLong();
		long userId = event.getMember().getIdLong();
		String emote = event.getReactionEmote().getAsReactionCode();
		
		String entryId = BotUtils.generateId(Arrays.asList(guildId, channelId, messageId));
		JSONObject dataObj = BotUtils.getDataFromJSON(entryId);
		if (dataObj == null)
		{
			return;
		}
		
		JSONObject emotesObj = dataObj.getJSONObject("data");
		JSONArray keys = emotesObj.names();
		for (int i = 0; i < keys.length(); i++)
		{
			String roleId = keys.getString(i);
			Role role = event.getGuild().getRoleById(roleId.replaceAll("\\D", ""));
			if (emotesObj.getString(roleId).equalsIgnoreCase(emote) && event.getGuild().getMemberById(userId).getRoles().contains(role))
			{
				event.getGuild().removeRoleFromMember(userId, role).queue();
				return;
			}
		}
	}
	
	void handleMsgDeletion(GuildMessageDeleteEvent event)
	{
		long guildId = event.getGuild().getIdLong();
		long channelId = event.getChannel().getIdLong();
		long messageId = event.getMessageIdLong();
		
		String entryId = BotUtils.generateId(Arrays.asList(guildId, channelId, messageId));
		BotUtils.removeDataFromJSON(entryId);
	}
}
