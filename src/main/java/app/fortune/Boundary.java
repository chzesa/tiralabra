package app.fortune;

import app.vector.*;

public class Boundary
{
	public final Ray ray;
	public final Vector siteA;
	public final Vector siteB;

	Boundary(Ray ray, Vector siteA, Vector siteB)
	{
		this.ray = ray;
		this.siteA = siteA;
		this.siteB = siteB;
	}

	@Override
	public String toString()
	{
		return "(Boundary " + ray + " bounded by " + siteA + " & " + siteB + ")";
	}
}
