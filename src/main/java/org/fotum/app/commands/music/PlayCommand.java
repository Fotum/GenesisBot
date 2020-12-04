package org.fotum.app.commands.music;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nullable;

import org.fotum.app.Constants;
import org.fotum.app.config.Config;
import org.fotum.app.features.music.PlayerManager;
import org.fotum.app.objects.ICommand;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayCommand implements ICommand
{
	private final YouTube youTube;
	
	public PlayCommand()
	{
		YouTube temp = null;
		
		try
		{
			temp = new YouTube.Builder(
						GoogleNetHttpTransport.newTrustedTransport(),
						JacksonFactory.getDefaultInstance(),
						null
					).setApplicationName("Genesis discord bot")
					.build();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		youTube = temp;
	}
	
	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		TextChannel channel = event.getChannel();

		if (args.isEmpty())
		{
			channel.sendMessage("Please provide some arguments").queue();
			return;
		}

		String input = String.join(" ", args);

		if (!isUrl(input))
		{
			String ytSearched = searchYoutube(input);
			
			if (ytSearched == null)
			{
				channel.sendMessage("Youtube returned no results").queue();
				return;
			}
			
			input = ytSearched;
		}

		PlayerManager manager = PlayerManager.getInstance();
		manager.loadAndPlay(event.getChannel(), input);
	}

	@Override
	public String getHelp()
	{
		return "Plays a song\n" +
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " <song url>`";
	}

	@Override
	public String getInvoke()
	{
		return "play";
	}
	
	@Nullable
	private String searchYoutube(String input)
	{
		try
		{
			List<SearchResult> results = youTube.search()
					.list("id,snippet")
					.setQ(input)
					.setMaxResults(1L)
					.setType("video")
					.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
					.setKey(Config.getInstance().getString("youtubekey"))
					.execute()
					.getItems();
			
			if (!results.isEmpty())
			{
				String videoId = results.get(0).getId().getVideoId();
				return "https://www.youtube.com/watch?v=" + videoId;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}

	private boolean isUrl(String input)
	{
		try
		{
			new URL(input);
			return true;
		}
		catch (MalformedURLException ex)
		{
			return false;
		}
	}
}
