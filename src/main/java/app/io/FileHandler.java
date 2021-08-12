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

	public static boolean write(String content, String path) {
		if (content == null || path == null)
		{
			return false;
		}

		try
		{
			Path p = Paths.get(path);
			byte[] bytes = content.getBytes();
			Files.write(p, bytes);
		} catch (IOException e)
		{
			return false;
		} catch (SecurityException e)
		{
			return false;
		}

		return true;
	}
}
