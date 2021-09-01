package app;

class Config
{
	boolean bench = false;
	Integer seed = new Integer(938758);
	Integer pows = new Integer(6);
	Integer reps = new Integer(5);
	Integer sites = 20;
	String input = null;
	String file = null;
	String outFile = null;

	private void handleParam(char param, String s)
	{
		switch (param)
		{
			case 'c':
			{
				sites = Integer.parseInt(s);
				break;
			}
			case 'p':
			{
				pows = Integer.parseInt(s);
				break;
			}
			case 'r':
			{
				reps = Integer.parseInt(s);
				break;
			}
			case 's':
			{
				seed = Integer.parseInt(s);
				break;
			}
		}
	}

	private char readParam(char param, String s)
	{
		switch (s)
		{
			case "-b":
			{
				bench = true;
				return '\0';
			}
			case "-c": return 'c';
			case "-p": return 'p';
			case "-r": return 'r';
			case "-s": return 's';
			default:
			{
				handleParam(param, s);
				return '\0';
			}
		}
	}

	Config(String[] args)
	{
		char param = '\0';
		for (int i = 0; i < args.length; i++)
			param = readParam(param, args[i]);
	}
}
