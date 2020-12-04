package org.fotum.app.objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.fotum.app.Constants;
import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BotUtils
{	
	public static JSONObject addDataToJSON(GuildMessageReceivedEvent event, long guildId, long channelId, long messageId, List<String> params)
	{
		File emoOptionsFile = new File(Constants.SETTINGS_LOC + File.separator + "emoroleopts.dat");
		JSONObject mainObject = null;
		if (emoOptionsFile.exists())
		{
			try
			{
				String content = new String(Files.readAllBytes(emoOptionsFile.toPath()));
				mainObject = new JSONObject(content);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			mainObject = new JSONObject();
			mainObject.put("name", "options");
			mainObject.put("length", 0);
			mainObject.put("data", new JSONArray());
		}
		
		JSONObject optionData = new JSONObject();
		params.stream()
			.forEach(
				(parameter) -> {
					String[] pars = parameter.split("\\s+");
					optionData.put(pars[1], pars[0].replaceAll("[<>]", "").replaceFirst("[:]", ""));
			});
		
		// Calculate entry ID to determine weather we should insert new entry or update old
		String newEntryId = BotUtils.generateId(Arrays.asList(guildId, channelId, messageId));
		
		JSONArray currData = mainObject.getJSONArray("data");
		int newDataIx = BotUtils.getDataIndex(currData, newEntryId);
		if (newDataIx != -1)
		{
			currData.getJSONObject(newDataIx).put("data", optionData);
		}
		else
		{
			JSONObject emoOption = new JSONObject();
			emoOption.put("entry_id", newEntryId);
			emoOption.put("guild_id", guildId);
			emoOption.put("channel_id", channelId);
			emoOption.put("message_id", messageId);
			emoOption.put("data", optionData);

			currData.put(emoOption);
			mainObject.put("length", currData.length());
			mainObject.put("data", currData);
		}
		
		try (FileWriter writer = new FileWriter(emoOptionsFile))
		{
			writer.write(mainObject.toString(4));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		return optionData;
	}
	
	public static JSONObject getDataFromJSON(String entryId)
	{
		File emoOptionsFile = new File(Constants.SETTINGS_LOC + File.separator + "emoroleopts.dat");
		if (!emoOptionsFile.exists())
		{
			return null;
		}
		
		JSONObject mainObject = null;
		try
		{
			String content = new String(Files.readAllBytes(emoOptionsFile.toPath()));
			mainObject = new JSONObject(content);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		JSONArray jsonData = mainObject.getJSONArray("data");
		int dataIx = BotUtils.getDataIndex(jsonData, entryId);
		if (dataIx != -1)
		{
			return jsonData.getJSONObject(dataIx);
		}
		
		return null;
	}
	
	public static JSONObject removeDataFromJSON(String entryId)
	{
		File emoOptionsFile = new File(Constants.SETTINGS_LOC + File.separator + "emoroleopts.dat");
		if (!emoOptionsFile.exists())
		{
			return null;
		}
		
		JSONObject mainObject = null;
		try
		{
			String content = new String(Files.readAllBytes(emoOptionsFile.toPath()));
			mainObject = new JSONObject(content);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		JSONArray jsonData = mainObject.getJSONArray("data");
		int remDataIx = BotUtils.getDataIndex(jsonData, entryId);
		if (remDataIx != -1)
		{
			JSONObject delObject = new JSONObject(jsonData.getJSONObject(remDataIx).toString());
			jsonData.remove(remDataIx);
			mainObject.put("length", (mainObject.getInt("length") - 1));
			
			try (FileWriter writer = new FileWriter(emoOptionsFile))
			{
				writer.write(mainObject.toString(4));
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			
			return delObject.getJSONObject("data");
		}
		
		return null;
	}

	public static String generateId(List<Long> args)
	{
		StringBuilder concat = new StringBuilder();
		for (Long l : args)
		{
			concat.append(String.valueOf(l));
		}
		
		return UUID.nameUUIDFromBytes(concat.toString().getBytes()).toString();
	}
	
	public static String getPictureByFilter(String filter)
	{
		List<String> filtArr = Arrays.asList(filter.split(","));
		return BotUtils.getPictureByFilter(filtArr);
	}
	
	public static String getPictureByFilter(List<String> filter)
	{
		if (filter.isEmpty())
		{
			return null;
		}
		
		HttpURLConnection con = null;
		URL result = null;
		String filterStr = String.join(",", filter);
		try
		{
			URL obj = new URL("https://source.unsplash.com/800x600/?" + filterStr);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				result = con.getURL();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				con.disconnect();
			}
			con = null;
		}
		
		return result.toString();
	}
	
	private static int getDataIndex(JSONArray data, String entryId)
	{
		if (data == null || data.isEmpty())
		{
			return -1;
		}
		
		if (entryId == null || entryId.isBlank())
		{
			return -1;
		}
		
		for (int i = 0; i < data.length(); i++)
		{
			JSONObject dataObject = data.getJSONObject(i);
			if (dataObject.getString("entry_id").equals(entryId))
			{
				return i;
			}
		}
		
		return -1;
	}
}
