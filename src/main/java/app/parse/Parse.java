package app.parse;

import app.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.StringBuilder;

public class Parse
{
	public static String toStringl(List<Vector> sites)
	{
		StringBuilder sb = new StringBuilder();

		for (Vector v : sites)
			sb.append(v.x + " " + v.y + "\n");

		sb.append("\n");

		return sb.toString();
	}

	public static String toStringll(List<List<Vector>> sites)
	{
		StringBuilder sb = new StringBuilder();
		for (List<Vector> l : sites)
			sb.append(toStringl(l));

		return sb.toString();
	}

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

	public static List<List<Vector>> fromStringll(String in)
	{
		String[] parts = in.split("\n\n");
		ArrayList<List<Vector>> res = new ArrayList<>();

		for (String s : parts)
			res.add(fromStringl(s));

		return res;
	}
}