package org.fotum.app;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.fotum.app.commands.CatCommand;
import org.fotum.app.commands.CoffeeCommand;
import org.fotum.app.commands.DiceCommand;
import org.fotum.app.commands.DogCommand;
import org.fotum.app.commands.GetAPIPictureCommand;
import org.fotum.app.commands.HelpCommand;
import org.fotum.app.commands.MemeCommand;
import org.fotum.app.commands.PingCommand;
import org.fotum.app.commands.ServerInfoCommand;
import org.fotum.app.commands.TeaCommand;
import org.fotum.app.commands.UptimeCommand;
import org.fotum.app.commands.UserInfoCommand;
import org.fotum.app.commands.admin.SetPrefixCommand;
import org.fotum.app.commands.moderation.AddRoleOnEmoteCommand;
import org.fotum.app.commands.moderation.BanCommand;
import org.fotum.app.commands.moderation.ClearCommand;
import org.fotum.app.commands.moderation.KickCommand;
import org.fotum.app.commands.moderation.RemoveRoleOnEmoteCommand;
import org.fotum.app.commands.moderation.UnbanCommand;
import org.fotum.app.commands.music.JoinCommand;
import org.fotum.app.commands.music.LeaveCommand;
import org.fotum.app.commands.music.NowPlayingCommand;
import org.fotum.app.commands.music.PlayCommand;
import org.fotum.app.commands.music.QueueCommand;
import org.fotum.app.commands.music.SkipCommand;
import org.fotum.app.commands.music.StopCommand;
import org.fotum.app.commands.owner.EvalCommand;
import org.fotum.app.config.Config;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandManager
{
	private final Map<String, ICommand> commands = new HashMap<String, ICommand>();
	
	CommandManager()
	{
		if (Config.getInstance().getBoolean("loadcommands"))
		{
			System.out.println("Loading commands");
			
			this.addCommand(new PingCommand());
			this.addCommand(new HelpCommand(this));
			this.addCommand(new CatCommand());
			this.addCommand(new DogCommand());
			this.addCommand(new TeaCommand());
			this.addCommand(new CoffeeCommand());
			this.addCommand(new GetAPIPictureCommand());
			this.addCommand(new MemeCommand());
			this.addCommand(new UserInfoCommand());
			this.addCommand(new KickCommand());
			this.addCommand(new BanCommand());
			this.addCommand(new UnbanCommand());
			this.addCommand(new SetPrefixCommand());
			this.addCommand(new UptimeCommand());
			this.addCommand(new EvalCommand());
			this.addCommand(new ClearCommand());
			this.addCommand(new DiceCommand());
			
			this.addCommand(new AddRoleOnEmoteCommand());
			this.addCommand(new RemoveRoleOnEmoteCommand());

			this.addCommand(new JoinCommand());
			this.addCommand(new LeaveCommand());
			this.addCommand(new PlayCommand());
			this.addCommand(new StopCommand());
			this.addCommand(new QueueCommand());
			this.addCommand(new SkipCommand());
			this.addCommand(new NowPlayingCommand());
			
			this.addCommand(new ServerInfoCommand());
		}
	}
	
	public Collection<ICommand> getCommands()
	{
		return this.commands.values();
	}
	
	public ICommand getCommand(String name)
	{
		return this.commands.get(name);
	}
	
	void handleCommand(GuildMessageReceivedEvent event)
	{
		final String prefix = Constants.PREFIXES.get(event.getGuild().getIdLong());
		final String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
		final String invoke = split[0].toLowerCase();
		
		if (commands.containsKey(invoke))
		{
			final List<String> args = Arrays.asList(split).subList(1, split.length);
			
			event.getChannel().sendTyping().queue();
			commands.get(invoke).handle(args, event);
		}
	}

	private void addCommand(ICommand command)
	{
		if (!this.commands.containsKey(command.getInvoke()))
		{
			this.commands.put(command.getInvoke(), command);
		}
	}
}
