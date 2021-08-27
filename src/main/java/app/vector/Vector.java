package app.vector;

/**
 * 2d vector class with basic arithmetic.
 */
public class Vector
{
	public static final double PRECISION = 0.0000001;
	public final double x;
	public final double y;

	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double norm()
	{
		return norm(this);
	}

	public Vector normalize()
	{
		return normalize(this);
	}

	public Vector scale(double scalar)
	{
		return scale(scalar, this);
	}

	public Vector neg()
	{
		return neg(this);
	}

	public Vector add(Vector other)
	{
		return add(this, other);
	}

	public Vector sub(Vector other)
	{
		return sub(this, other);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || getClass() != o.getClass())
			return false;

		Vector other = (Vector) o;
		return distance(this, other) < PRECISION;
	}

	@Override
	public String toString()
	{
		return "(" + String.format("%.5f", x) + ", " + String.format("%.5f", y) + ")";
	}

	public static double dot(Vector a, Vector b)
	{
		return a.x * b.x
			+ a.y * b.y;
	}

	public static double norm(Vector v)
	{
		return Math.sqrt(dot(v, v));
	}

	public static Vector normalize(Vector v)
	{
		double n = v.norm();
		if (n == 0)
			return new Vector(0, 0);

		double s = 1.0 / n;
		return scale(s, v);
	}

	public static Vector scale(double scalar, Vector v)
	{
		return new Vector(scalar * v.x, scalar * v.y);
	}

	public static Vector neg(Vector v)
	{
		return scale(-1.0, v);
	}

	public static Vector add(Vector a, Vector b)
	{
		return new Vector(a.x + b.x, a.y + b.y);
	}

	public static Vector sub(Vector a, Vector b)
	{
		return new Vector(a.x - b.x, a.y - b.y);
	}

	public static double distance(Vector a, Vector b)
	{
		return norm(sub(a, b));
	}

	public static double angle(Vector a, Vector b)
	{
		double det = a.x * b.y - a.y * b.x;
		return Math.atan2(det, dot(a, b));
	}
}
