package app.fortune;

import app.vector.*;

class Parabola
{
	final double x2;
	final double x;
	final double c;
	final boolean degenerate;

	Parabola(Vector focus, double directrix)
	{
		degenerate = Math.abs(focus.y - directrix) < Vector.PRECISION;

		double d = (2.0 * (focus.y - directrix));
		x2 = 1.0 / d;
		x = (-2.0 * focus.x) / d;
		c = focus.x * focus.x / d + (1.0 / 2.0) * (focus.y + directrix);
	}

	public String toString()
	{
		return "( " + x2 + "x^2 + " + x + "x + " + c + " )";
	}
}

public class Utils
{
	/**
	 * Given a vector defined by origin and destination, returns a perpendicular vector
	 * pointing to the right from the viewpoint of the vector.
	 * Used to generate the bisector direction vector of two adjacent sites.
	 */
	public static Vector bisector(Vector orig, Vector dst)
	{
		Vector d = dst.sub(orig);

		double a1 = d.x;
		double a2 = d.y;
		double a3 = 0;

		double b1 = 0;
		double b2 = 0;
		double b3 = 1;

		return new Vector(
			a2 * b3 - a3 * b2,
			a3 * b1 - a1 * b3
		).normalize();
	}

	/**
	 * Given a parabola defined by a focus and directrix, returns the y coordinate of the
	 * parabola at x.
	 */
	public static double parabolaY(Vector focus, double directrix, double x)
	{
		double a = focus.x;
		double b = focus.y;
		return (x - a) * (x - a) / (2.0 * (b - directrix)) + (b + directrix) / 2.0;
	}


	public static Vector parabolaPt(Vector focus, double directrix, double x)
	{
		return new Vector(x, parabolaY(focus, directrix, x));
	}

	/**
	 * Given two focuses and a directrix, returns the intersection points of the parabolas
	 * provided neither parabola is degenerate.
	 * Both parabolas are expected to be above the directrix.
	 */
	public static Vector[] parabolaIntersection(Vector focusA, Vector focusB, double directrix)
	{
		Parabola p1 = new Parabola(focusA, directrix);
		Parabola p2 = new Parabola(focusB, directrix);

		if (Math.abs(focusA.y - focusB.y) < Vector.PRECISION)
			return new Vector[] {
				parabolaPt(focusA, directrix, (focusA.x + focusB.x) / 2)
			};

		if (p1.degenerate && p2.degenerate)
		{
			return new Vector[] {};
		}
		else if (p1.degenerate)
		{
			return new Vector[]
			{
				parabolaPt(focusB, directrix, focusA.x)
			};
		}
		else if (p2.degenerate)
		{
			return new Vector[]
			{
				parabolaPt(focusA, directrix, focusB.x)
			};
		}

		double[] xCoords = solveQuadraticFn(p1.x2 - p2.x2, p1.x - p2.x, p1.c - p2.c);

		return new Vector[]
		{
			parabolaPt(focusA, directrix, xCoords[0]),
			parabolaPt(focusB, directrix, xCoords[1])
		};
	}

	public static double[] solveQuadraticFn(double a, double b, double c)
	{
		double sqrt = Math.sqrt(b * b - 4.0 * a * c);

		return new double[]
		{
			(-b + sqrt) / (2.0 * a),
			(-b - sqrt) / (2.0 * a)
		};
	}
}
