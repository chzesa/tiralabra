package app.fortune;

import app.vector.*;

class Event
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

	boolean isSiteEvent()
	{
		return pt == null;
	}

	Vector site()
	{
		return s;
	}

	Vector point()
	{
		if (isSiteEvent())
			return s;
		return pt;
	}
}