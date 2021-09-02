package app.ui;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import app.vector.*;
import app.parse.*;

public class Generator
{
	Random rand;
	List<List<Vector>> input = new ArrayList<>();
	int index = 0;

	Generator(String s)
	{
		if (s != null && !s.equals(""))
			input = Parse.fromStringll(s);
		rand = new Random();
	}

	Generator(String s, int seed)
	{
		if (s != null && !s.equals(""))
			input = Parse.fromStringll(s);
		rand = new Random(seed);
	}

	Generator(int seed)
	{
		rand = new Random(seed);
	}

	Generator()
	{
		rand = new Random();
	}

	List<Vector> next(int n, double xMin, double xMax, double yMin, double yMax)
	{
		List<Vector> ret = new ArrayList<>();
		double dY = yMax - yMin;
		double dX = xMax - xMin;
		while (n-- > 0)
			ret.add(new Vector(
				rand.nextDouble() * dX + xMin,
				rand.nextDouble() * dY + yMin
			));

		return ret;
	}

	List<Vector> next()
	{
		if (input.isEmpty())
			return null;

		List<Vector> ret = input.get(index);
		index = (index + 1) % input.size();

		return ret;
	}
}
