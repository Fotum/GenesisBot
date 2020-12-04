package org.fotum.app.commands.music;

import java.util.List;

import org.fotum.app.features.music.GuildMusicManager;
import org.fotum.app.features.music.PlayerManager;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand implements ICommand
{
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		PlayerManager playerManager = PlayerManager.getInstance();
		GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
		
		musicManager.scheduler.getQueue().clear();
		musicManager.player.stopTrack();
		musicManager.player.setPaused(false);
		
		event.getChannel().sendMessage("Stopping the player and clearing the queue").queue();
	}

	@Override
	public String getHelp()
	{
		return "Stops the music player";
	}

	@Override
	public String getInvoke()
	{
		return "stop";
	}
}
