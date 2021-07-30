package app.vector;

/**
 * 2d vector class with basic arithmetic.
 */
public class Vector
{
	public static final double PRECISION = 0.00001;
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

		Vector other = (Vector)o;
		return Math.abs(x - other.x) < PRECISION
			&& Math.abs(y - other.y) < PRECISION;
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
		double s = 1 / v.norm();
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
		return add(a, neg(b));
	}

	public static double distance(Vector a, Vector b)
	{
		return norm(sub(a, b));
	}
}