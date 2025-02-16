package org.fotum.app.commands.music;

import java.util.List;

import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		AudioManager audioManager = event.getGuild().getAudioManager();
		
		if (!audioManager.isConnected())
		{
			channel.sendMessage("I'm not connected to a voice channel").queue();
			return;
		}
		
		VoiceChannel voiceChannel = audioManager.getConnectedChannel();
		
		if (!voiceChannel.getMembers().contains(event.getMember()))
		{
			channel.sendMessage("You have to be in the same voice channel as me to use this command").queue();
			return;
		}
		
		audioManager.closeAudioConnection();
		channel.sendMessage("Disconnected from your channel").queue();
	}

	@Override
	public String getHelp()
	{
		return "Makes the bot leave your channel";
	}

	@Override
	public String getInvoke()
	{
		return "leave";
	}

}
