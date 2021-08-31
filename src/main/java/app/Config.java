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

	Config(String[] args)
	{
		char param = '\0';
		for (int i = 0; i < args.length; i++)
		{
			switch (args[i])
			{
			case "-b":
			{
				bench = true;
				param = '\0';
				break;
			}
			case "-c":
			{
				param = 'c';
				break;
			}
			case "-p":
			{
				param = 'p';
				break;
			}
			case "-r":
			{
				param = 'r';
				break;
			}
			case "-s":
			{
				param = 's';
				break;
			}
			default:
			{
				switch(param)
				{
				case 'c':
				{
					sites = Integer.parseInt(args[i]);
					break;
				}
				case 'p':
				{
					pows = Integer.parseInt(args[i]);
					break;
				}
				case 'r':
				{
					reps = Integer.parseInt(args[i]);
					break;
				}
				case 's':
				{
					seed = Integer.parseInt(args[i]);
					break;
				}
				}

				param = '\0';
			}
			}
		}
	}
}