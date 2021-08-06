package app.io;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler
{
	public static String readToString(String path)
	{
		byte[] bytes;
		try
		{
			Path p = Paths.get(path);
			bytes = Files.readAllBytes(p);
		}
		catch (IOException e)
		{
			return "";
		}
		catch (SecurityException e)
		{
			return "";
		}

		return new String(bytes);
	}
}
