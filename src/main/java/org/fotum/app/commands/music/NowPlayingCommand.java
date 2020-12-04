package org.fotum.app.commands.music;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.fotum.app.features.music.GuildMusicManager;
import org.fotum.app.features.music.PlayerManager;
import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NowPlayingCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();
		PlayerManager playerManager = PlayerManager.getInstance();
		GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
		AudioPlayer player = musicManager.player;
		
		if (player.getPlayingTrack() == null)
		{
			channel.sendMessage("The player isn't playing any song").queue();
			return;
		}
		
		AudioTrackInfo info = player.getPlayingTrack().getInfo();
		MessageEmbed embed = EmbedCreator.getDefault()
				.setTitle(null)
				.setDescription(String.format(
								"**Playing** [%s](%s)\n%s %s - %s",
								info.title,
								info.uri,
								player.isPaused() ? "⏹️" : "▶️",
								this.formatTime(player.getPlayingTrack().getPosition()),
								this.formatTime(player.getPlayingTrack().getDuration())
		)).build();

		channel.sendMessage(embed).queue();
	}

	@Override
	public String getHelp()
	{
		return "Shws the currently playing track";
	}

	@Override
	public String getInvoke()
	{
		return "np";
	}
	
	private String formatTime(long timeInMillis)
	{
		final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
		final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
