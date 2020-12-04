package org.fotum.app.commands.music;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.fotum.app.features.music.GuildMusicManager;
import org.fotum.app.features.music.PlayerManager;
import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		PlayerManager playerManager = PlayerManager.getInstance();
		GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
		BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
		
		if (queue.isEmpty())
		{
			channel.sendMessage("The queue is empty").queue();
			return;
		}
		
		int trackCount = Math.min(queue.size(), 20);
		List<AudioTrack> tracks = new ArrayList<AudioTrack>(queue);
		EmbedBuilder builder = EmbedCreator.getDefault().setTitle("Current Queue (Total: " + queue.size() + ")");
		
		for (int i = 0; i < trackCount; i++)
		{
			AudioTrack track = tracks.get(i);
			AudioTrackInfo info = track.getInfo();
			
			builder.appendDescription(String.format("%s - %s\n", info.title, info.author));
		}
		
		channel.sendMessage(builder.build()).queue();
	}

	@Override
	public String getHelp()
	{
		return "Shows the current queue for the music player";
	}

	@Override
	public String getInvoke()
	{
		return "queue";
	}
}
