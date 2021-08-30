package app.fortune;

import app.vector.*;
import app.tree.*;

public class Event
{
	private final Vector pt;
	private final Vector s;
	final Tree<Arc>.Node arc;

	Event(Vector site)
	{
		this.pt = null;
		this.s = site;
		this.arc = null;
	}

	Event(Vector point, Vector site, Tree<Arc>.Node arc)
	{
		this.pt = point;
		this.s = site;
		this.arc = arc;
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
