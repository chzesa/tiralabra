package app.vector;

/**
 * 2d ray class.
 */
public class Ray
{
	public final Vector origin;
	public final Vector direction;

	public Ray(Vector origin, Vector direction)
	{
		this.origin = origin;
		this.direction = direction;
	}

	public Vector intersect(Ray other)
	{
		return intersect(this, other);
	}

	/**
	 * Determines the intersection of two rays.
	 * @return Intersection point of the two rays, or null if they don't intersect.
	 */
	public static Vector intersect(Ray a, Ray b)
	{
		Vector aO = a.origin;
		Vector aD = a.direction;
		Vector bO = b.origin;
		Vector bD = b.direction;

		double det = bD.x * aD.y - bD.y * aD.x;
		if (det == 0)
			return null;

		double dX = bO.x - aO.x;
		double dY = bO.y - aO.y;

		double s = (dY * aD.x - dX * aD.y) / det;
		double t = (dY * bD.x - dX * bD.y) / det;
		if (s >= 0 && t >= 0)
			return aO.add(aD.scale(t));

		return null;
	}
}