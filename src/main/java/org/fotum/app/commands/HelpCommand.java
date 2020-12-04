package org.fotum.app.commands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fotum.app.CommandManager;
import org.fotum.app.Constants;
import org.fotum.app.objects.EmbedCreator;
import org.fotum.app.objects.ICommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand implements ICommand
{
	private final CommandManager manager;
	
	public HelpCommand(CommandManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void handle(List<String> args, GuildMessageReceivedEvent event)
	{
		List<ICommand> visible = manager.getCommands().stream()
									.filter(
										(command) -> {
											String cmd = command.getInvoke();
											return (cmd.equalsIgnoreCase("eval") ||
													cmd.equalsIgnoreCase("remroleonemote") ||
													cmd.equalsIgnoreCase("addroleonemote")
													) ? false : true;
									})
									.collect(Collectors.toList());
		if (args.isEmpty())
		{
			this.generateAndSendEmbed(event, visible);
			return;
		}
		
		String joined = String.join("", args);
		ICommand command = visible.contains(manager.getCommand(joined)) ? manager.getCommand(joined) : null;

		if (command == null)
		{
			event.getChannel().sendMessage("The command `" + joined + "` does not exist\n" + 
					"Use `" + Constants.PREFIX + this.getInvoke() + "` for a list of commands").queue();
			return;
		}
		
		String message = "Command help for `" + command.getInvoke() + "`\n" + command.getHelp();
		event.getChannel().sendMessage(message).queue();
	}

	@Override
	public String getHelp()
	{
		return "Shows a list of all the commands.\n" +
				"Usage: `" + Constants.PREFIX + this.getInvoke() + " [command]`";
	}

	@Override
	public String getInvoke()
	{
		return "help";
	}

	private void generateAndSendEmbed(GuildMessageReceivedEvent event, List<ICommand> visible)
	{
		EmbedBuilder builder = EmbedCreator.getDefault().setTitle("A list of all my commands:");
		
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
		visible.stream()
			.sorted(Comparator.comparing(ICommand::getInvoke))
			.forEach(
				(command) -> descriptionBuilder.append("`").append(command.getInvoke()).append("`\n")
			);

		event.getChannel().sendMessage(builder.build()).queue();
		builder.clear();
	}
}
