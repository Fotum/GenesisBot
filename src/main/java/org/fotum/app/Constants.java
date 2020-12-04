package org.fotum.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Constants
{
	public static final String PREFIX = "~";
	public static final long OWNER = 217576948195524608L;
	public static final Map<Long, String> PREFIXES = new HashMap<Long, String>();
	public static final String SETTINGS_LOC = System.getProperty("user.dir") + File.separator + "settings";
	
	static void initConstants()
	{
		File settingsDir = new File(Constants.SETTINGS_LOC);
		if (!settingsDir.exists())
		{
			settingsDir.mkdirs();
		}
	}
	
	static void savePrefixesToFile()
	{
		if (Constants.PREFIXES.isEmpty())
		{
			return;
		}

		File mapConfig = new File(Constants.SETTINGS_LOC + File.separator + "mapconfig.dat");
		try (FileOutputStream fos = new FileOutputStream(mapConfig);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
			)
		{
			oos.writeObject(Constants.PREFIXES);
			oos.flush();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	static void loadPrefixesFromFile()
	{
		File mapConfig = new File(Constants.SETTINGS_LOC + File.separator + "mapconfig.dat");
		if (!mapConfig.exists())
		{
			return;
		}
		
		try (FileInputStream fis = new FileInputStream(mapConfig);
				ObjectInputStream ois = new ObjectInputStream(fis);
			)
		{
			Constants.PREFIXES.putAll((HashMap<Long, String>) ois.readObject());
		}
		catch (IOException | ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}
}
