package org.fotum.app.features.music;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayerManager
{
	private static PlayerManager INSTANCE;
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;
	
	private PlayerManager()
	{
		this.musicManagers = new HashMap<Long, GuildMusicManager>();
		
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		AudioSourceManagers.registerLocalSource(this.playerManager);
	}
	
	public synchronized GuildMusicManager getGuildMusicManager(Guild guild)
	{
		long guildId = guild.getIdLong();
		GuildMusicManager musicManager = this.musicManagers.get(guildId);
		
		if (musicManager == null)
		{
			musicManager = new GuildMusicManager(this.playerManager);
			musicManagers.put(guildId, musicManager);
		}
		
		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		
		return musicManager;
	}
	
	public void loadAndPlay(TextChannel channel, String trackUrl)
	{
		GuildMusicManager musicManager = this.getGuildMusicManager(channel.getGuild());
		
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
					@Override
					public void loadFailed(FriendlyException ex)
					{
						channel.sendMessage("Could not play: " + ex.getMessage()).queue();
					}

					@Override
					public void noMatches()
					{
						channel.sendMessage("Nothing found by " + trackUrl).queue();
					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist)
					{
						AudioTrack firstTrack = playlist.getSelectedTrack();
						
						if (firstTrack == null)
						{
							firstTrack = playlist.getTracks().remove(0);
						}
						
						channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
						play(musicManager, firstTrack);
						
						playlist.getTracks().forEach(musicManager.scheduler::queue);
					}

					@Override
					public void trackLoaded(AudioTrack track)
					{
						channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
						play(musicManager, track);
					}
			
				}
		);
	}
	
	private void play(GuildMusicManager musicManager, AudioTrack track)
	{
		musicManager.scheduler.queue(track);
	}
	
	public static synchronized PlayerManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new PlayerManager();
		}
		
		return INSTANCE;
	}
}
