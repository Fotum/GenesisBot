package org.fotum.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class Listener extends ListenerAdapter
{
	private final CommandManager manager;
	private final RoleOnEmoteHandler roleHandler;
	private final Logger logger = LoggerFactory.getLogger(Listener.class);
	
	Listener(CommandManager manager, RoleOnEmoteHandler roleHandler)
	{
		this.manager = manager;
		this.roleHandler = roleHandler;
	}
	
	@Override
	public void onReady(ReadyEvent event)
	{
		logger.info(String.format("Logged in as %#s", event.getJDA().getSelfUser()));
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		User author = event.getAuthor();
		String content = event.getMessage().getContentDisplay();
		
		if (event.isFromType(ChannelType.TEXT))
		{
			Guild guild = event.getGuild();
			TextChannel textChannel = event.getTextChannel();
			
			logger.info(String.format("(%s) [%s] <%#s>: %s", guild.getName(), textChannel.getName(), author, content));
		}
		else if (event.isFromType(ChannelType.PRIVATE))
		{
			logger.info(String.format("[PRIV] <%#s>: %s", author, content));
		}
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String rw = event.getMessage().getContentRaw();
		
		if (rw.equalsIgnoreCase(Constants.PREFIX + "shutdown") &&
				event.getAuthor().getIdLong() == Constants.OWNER)
		{
			this.shutdown(event.getJDA());
			return;
		}
		
		String prefix = Constants.PREFIXES.computeIfAbsent(event.getGuild().getIdLong(), (l) -> Constants.PREFIX);
		
		if (!event.getAuthor().isBot() && !event.getMessage().isWebhookMessage() && rw.startsWith(prefix))
		{
			manager.handleCommand(event);
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		if (!event.getUser().isBot() && event.isFromType(ChannelType.TEXT))
		{
			roleHandler.handle(event);
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event)
	{
		if (!event.getUser().isBot() && event.isFromType(ChannelType.TEXT))
		{
			roleHandler.handle(event);
		}
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event)
	{
		roleHandler.handleMsgDeletion(event);
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		logger.info(String.format("(%s) %s left the server", event.getGuild().getName(), event.getMember().getEffectiveName()));
	}
	
	private void shutdown(JDA jda)
	{
		Constants.savePrefixesToFile();
		jda.shutdown();
		System.exit(0);
	}
}
