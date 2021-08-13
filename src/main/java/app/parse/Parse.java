package app.parse;

import app.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.StringBuilder;

public class Parse
{
	/**
	 * Converts a list of vectors to a string.
	 * Each line contains x and y coordinates separated by a space.
	 * String ends in a blank line.
	 */
	public static String toStringl(List<Vector> sites)
	{
		StringBuilder sb = new StringBuilder();

		for (Vector v : sites)
			sb.append(v.x + " " + v.y + "\n");

		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Converts the specififed list to strings. Each vector list
	 * is separated by a blank line.
	 */
	public static String toStringll(List<List<Vector>> sites)
	{
		StringBuilder sb = new StringBuilder();
		for (List<Vector> l : sites)
			sb.append(toStringl(l));

		return sb.toString();
	}

	/**
	 * Parses a string representing a single list of sites.
	 */
	public static List<Vector> fromStringl(String s)
	{
		Scanner scan = new Scanner(s);
		List<Vector> sites = new ArrayList<>();
		while(scan.hasNextDouble())
		{
			double x = scan.nextDouble();
			double y = scan.nextDouble();
			sites.add(new Vector(x, y));
		}

		return sites;
	}

	/**
	 * Parses a string representing lists of sites.
	 */
	public static List<List<Vector>> fromStringll(String in)
	{
		String[] parts = in.split("\n\n");
		ArrayList<List<Vector>> res = new ArrayList<>();

		for (String s : parts)
			res.add(fromStringl(s));

		return res;
	}
}