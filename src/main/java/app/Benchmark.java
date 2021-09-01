package app;
import app.fortune.Fortune;

class Benchmark
{
	static int width = 15;
	static void col(String prefix, int value, String suffix)
	{
		String ret = "";
		int pad = width - prefix.length() - suffix.length() - Integer.toString(value).length();
		while (pad-- > 0)
			ret += " ";

		ret += prefix + value + suffix;
		System.out.print(ret);
	}

	static void col(String prefix)
	{
		String ret = "";
		int pad = width - prefix.length();
		while (pad-- > 0)
			ret += " ";

		ret += prefix;
		System.out.print(ret);
	}

	static void printResult(int count, int iter, int dur)
	{
		col("", count, "");
		col("", iter, "");
		col("", dur, "ms");
		System.out.println("");
	}

	static long test(int n, Generator gen)
	{
		long t0 = java.lang.System.nanoTime();
		Fortune f = new Fortune(gen.next(n, 0, 1, 0, 1));
		f.processAll();
		long t1 = java.lang.System.nanoTime();
		return t1 - t0;
	}

	static void run(Config conf)
	{
		Generator gen = new Generator(conf.seed);
		col("input size");
		col("iteration");
		col("time");
		System.out.println("");

		long[] totals = new long[conf.pows + 1];

		for (int i = 1; i <= conf.pows; i++)
		{
			int n = (int) Math.pow(10, i);
			for (int j = 0; j < conf.reps; j++)
			{
				long result = test(n, gen);
				totals[i] += result;
				printResult(n, j + 1, (int) (result / 1000000.0));
			}
		}

		System.out.println("");
		col("input size");
		col("average time");
		System.out.println("");
		for (int i = 1; i <= conf.pows; i++)
		{
			col("", (int) Math.pow(10, i), "");
			col("", (int) ((double) totals[i] / conf.reps / 1000000.0), "ms");
			System.out.println("");
		}
	}
}
