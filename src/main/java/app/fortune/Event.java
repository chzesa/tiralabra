package app.fortune;

import app.vector.*;

public class Event
{
	private final Vector pt;
	private final Vector s;

	Event(Vector site)
	{
		this.pt = null;
		this.s = site;
	}

	Event(Vector point, Vector site)
	{
		this.pt = point;
		this.s = site;
	}

	public boolean isSiteEvent()
	{
		return pt == null;
	}

	public Vector site()
	{
		return s;
	}

	public Vector point()
	{
		if (isSiteEvent())
			return s;
		return pt;
	}
}