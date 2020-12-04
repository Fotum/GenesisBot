package org.fotum.app.commands.music;

import java.util.List;

import org.fotum.app.features.music.GuildMusicManager;
import org.fotum.app.features.music.PlayerManager;
import org.fotum.app.features.music.TrackScheduler;
import org.fotum.app.objects.ICommand;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		PlayerManager playerManager = PlayerManager.getInstance();
		GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
		TrackScheduler scheduler = musicManager.scheduler;
		AudioPlayer player = musicManager.player;
		
		if (player.getPlayingTrack() == null)
		{
			channel.sendMessage("The player isn't playing anything").queue();
			return;
		}
		
		scheduler.nextTrack();
		channel.sendMessage("Skipping the current track").queue();
	}

	@Override
	public String getHelp()
	{
		return "Skips the current song";
	}

	@Override
	public String getInvoke()
	{
		return "skip";
	}
}
