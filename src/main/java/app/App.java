package app;

import app.io.*;
import app.vector.Vector;
import app.fortune.*;
import app.parse.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.lang.StringBuilder;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class App
{
	long window;
	int windowX = 720;
	int windowY = 720;
	boolean viewportChanged = true;

	double cursorX = 0;
	double cursorY = 0;
	boolean cursorMoved = true;
	boolean debug = false;

	int numSites = 8;
	List<Vector> sites;
	Fortune fortune;
	boolean regenerate = true;
	boolean auto = true;
	float[] edges;
	float[] rays;
	float[] coords;
	float[] rayOrigins;

	public void run()
	{
		initWindow();
		GL.createCapabilities();
		initOpengl();
		loop();
	}

	void saveDataset()
	{
		FileHandler.write(Parse.toStringl(sites), "sites_"+ java.time.LocalDateTime.now().toString() + ".txt");
	}

	void initWindow()
	{
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new RuntimeException("GLFW init failed");

		glfwDefaultWindowHints();

		window = glfwCreateWindow(windowX, windowY, "Voronoi", 0, 0);
		if (window == 0)
			throw new RuntimeException("GLFW Window creation failed");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
		{
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
			if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE)
			{
				cursorMoved = true;
				regenerate = true;
			}
			if (key == GLFW_KEY_A && action == GLFW_RELEASE)
				auto = !auto;
			if (key == GLFW_KEY_S && action == GLFW_RELEASE)
				saveDataset();
			if (key == GLFW_KEY_D && action == GLFW_RELEASE)
				debug = !debug;
			if (key == GLFW_KEY_V && action == GLFW_RELEASE)
			{
				try
				{
					if (new Validator(sites, new Fortune(sites).processAll()).result())
						System.out.println("Ok result.");
					else
						System.out.println("Invalid result.");
				}
				catch(Exception e)
				{
					System.out.println("Invalid result.");
					System.out.println(e);
					e.printStackTrace();
				}
			}
			if (key == GLFW_KEY_1 && action == GLFW_RELEASE)
				numSites = 1;
			if (key == GLFW_KEY_2 && action == GLFW_RELEASE)
				numSites = 2;
			if (key == GLFW_KEY_3 && action == GLFW_RELEASE)
				numSites = 3;
			if (key == GLFW_KEY_4 && action == GLFW_RELEASE)
				numSites = 4;
			if (key == GLFW_KEY_5 && action == GLFW_RELEASE)
				numSites = 5;
			if (key == GLFW_KEY_6 && action == GLFW_RELEASE)
				numSites = 6;
			if (key == GLFW_KEY_7 && action == GLFW_RELEASE)
				numSites = 7;
			if (key == GLFW_KEY_8 && action == GLFW_RELEASE)
				numSites = 8;
			if (key == GLFW_KEY_9 && action == GLFW_RELEASE)
				numSites = 9;
			if (key == GLFW_KEY_0 && action == GLFW_RELEASE)
				numSites = 10;
		});

		glfwSetCursorPosCallback(window, (window, x, y) ->
		{
			if (auto) return;
			cursorX = x;
			cursorY = y;
			cursorMoved = true;
		});

		glfwSetWindowSizeCallback(window, (window, width, height) ->
		{
			windowX = width;
			windowY = height;
			viewportChanged = true;
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	int createShader(int type, String code)
	{
		int shader = glCreateShader(type);
		if (shader == 0)
			throw new RuntimeException("Shader creation failed");

		glShaderSource(shader, code);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
			throw new RuntimeException("Shader compilation failed: "
				+ glGetShaderInfoLog(shader, 512));

		return shader;
	}

	void initOpengl()
	{
		String str = FileHandler.readToString("src/shader/shader.vert");
		int vertexShader = createShader(GL_VERTEX_SHADER, str);
		str = FileHandler.readToString("src/shader/shader.frag");
		int fragShader = createShader(GL_FRAGMENT_SHADER, str);

		int prog = glCreateProgram();
		glAttachShader(prog, vertexShader);
		glAttachShader(prog, fragShader);
		glLinkProgram(prog);

		if (glGetProgrami(prog, GL_LINK_STATUS) == GL_FALSE)
			throw new RuntimeException("Program linking failed");

		glUseProgram(prog);

		int buffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, buffer);

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		glEnable(GL_PROGRAM_POINT_SIZE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_SCISSOR_TEST);
	}

	void setColor(float r, float g, float b, float a)
	{
		glUniform4f(0, r, g, b, a);
	}

	void drawPoints(float[] coords)
	{
		glBufferData(GL_ARRAY_BUFFER, coords, GL_DYNAMIC_DRAW);
		glDrawArrays(GL_POINTS, 0, coords.length / 2);
	}

	void drawLines(float[] coords)
	{
		glBufferData(GL_ARRAY_BUFFER, coords, GL_DYNAMIC_DRAW);
		glDrawArrays(GL_LINES, 0, coords.length / 2);
	}

	void drawParabola(Vector focus, double directrix, double xMin, double xMax)
	{
		double delta = 2.0 / windowX;
		float[] coords = new float[
			4 * ((int) Math.ceil(Math.abs(xMax - xMin) / delta) + 1)
		];
		int i = 0;

		while (xMin < xMax)
		{
			double x1, x2, y1, y2;
			x1 = xMin;
			x2 = Math.min(x1 + delta, xMax);
			y1 = Utils.parabolaY(focus, directrix, x1);
			y2 = Utils.parabolaY(focus, directrix, x2);

			coords[i * 4 + 0] = (float) x1;
			coords[i * 4 + 1] = (float) y1;

			coords[i * 4 + 2] = (float) x2;
			coords[i * 4 + 3] = (float) y2;

			xMin += delta;
			i++;
		}

		drawLines(coords);
	}

	static List<Vector> genSites(int count)
	{
		Random rand = new Random();
		List<Vector> ret = new ArrayList<>();
		while (count-- > 0)
			ret.add(new Vector(
				rand.nextDouble() * 0.9f + 0.05f,
				rand.nextDouble() * 0.9f + 0.05f
			));

		return ret;
	}

	void generateDataset()
	{
		sites = genSites(numSites);

		int i = 0;
		coords = new float[numSites * 2];
		for (Vector site : sites)
		{
			coords[i * 2] = (float) site.x;
			coords[i * 2 + 1] = (float) site.y;
			i++;
		}

		try
		{
			Validator valid = new Validator(sites, new Fortune(sites).processAll());
			if (valid.result())
			{
				System.out.println("Incorrect voronoi diagram generated from dataset.");
				saveDataset();
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
			saveDataset();
		}
	}

	void print(String s)
	{
		if (debug)
			System.out.println(s);
	}

	void drawQueuedCircleEvents()
	{
		for (Event e : fortune.queue)
		{
			if (!e.isSiteEvent())
				drawPoints(new float[] {(float) e.point().x, (float) e.point().y});
		}
	}

	void drawBeachline()
	{
		for (ISortable sort : fortune.beach)
		{
			Arc arc = (Arc) sort;
			double xMin = arc.left(fortune.sweepLine()).x;

			if (xMin == Double.NEGATIVE_INFINITY)
				xMin = 0;

			double xMax = 1.0;
			Arc next = (Arc) fortune.beach.higher(sort);
			if (next != null)
			{
				xMax = next.left(fortune.sweepLine()).x;
				if (next.left(fortune.sweepLine()).x
					< sort.left(fortune.sweepLine()).x)
					throw new RuntimeException("asd");
			}

			drawParabola(arc.site, fortune.sweepLine(), xMin, xMax);
			// drawLines(new float[] {(float)xMin, 1.0f, (float)xMin, 0.0f});
		}
	}

	void drawAllParabolas()
	{
		for (Vector site : sites)
		{
			if (Math.abs(site.y - fortune.sweepLine()) < Vector.PRECISION
				|| site.y < fortune.sweepLine())
				continue;

			drawParabola(site, fortune.sweepLine(), 0, 1);
		}
	}

	void drawBeachlineVerticals()
	{
		for (ISortable sort : fortune.beach)
		{
			Arc arc = (Arc) sort;
			float xMin = (float) arc.left(fortune.sweepLine()).x;

			if (xMin == Double.NEGATIVE_INFINITY)
				xMin = 0;

			drawLines(new float[] {xMin, 1.0f, xMin, 0.0f});
		}
	}

	void drawBoundaries()
	{
		for (ISortable sort : fortune.beach)
		{
			Arc arc = (Arc) sort;
			if (arc.left == null)
				continue;

			Vector origin = arc.left.ray.origin;
			Vector direction = arc.left.ray.direction.normalize();
			float x1, x2, y1, y2;
			x1 = (float) origin.x;
			x2 = x1 + (float) direction.x * 100;
			y1 = (float) origin.y;
			y2 = y1 + (float) direction.y * 100;

			drawLines(new float[]{x1, y1, x2, y2});
		}
	}

	Fortune.Result processResult()
	{
		int i = 0;
		Fortune.Result result = fortune.result();

		edges = new float[result.edges.size() * 4];

		for (Fortune.Edge edge : result.edges)
		{
			Vector a = edge.a;
			Vector b = edge.b;
			print(a + " to " + b);
			edges[i * 4 + 0] = (float) a.x;
			edges[i * 4 + 1] = (float) a.y;

			edges[i * 4 + 2] = (float) b.x;
			edges[i * 4 + 3] = (float) b.y;
			i++;
		}

		rays = new float[result.rays.size() * 4];

		i = 0;
		for (Fortune.Edge edge : result.rays)
		{
			Vector a = edge.a;
			Vector b = edge.b;
			rays[i * 4 + 0] = (float) a.x;
			rays[i * 4 + 1] = (float) a.y;

			b = b.normalize().scale(100).add(a);
			rays[i * 4 + 2] = (float) b.x;
			rays[i * 4 + 3] = (float) b.y;
			i++;
		}

		rayOrigins = new float[result.rays.size() * 2];
		i = 0;
		for (Fortune.Edge edge : result.rays)
		{
			Vector a = edge.a;
			rayOrigins[i * 2 + 0] = (float) a.x;
			rayOrigins[i * 2 + 1] = (float) a.y;
		}

		return result;
	}

	void loop()
	{
		double cursorPos = 0;

		while (!glfwWindowShouldClose(window))
		{
			if (viewportChanged)
			{
				viewportChanged = false;
				glViewport(0, 0, windowX, windowY);
			}

			if (regenerate)
			{
				regenerate = false;
				generateDataset();
				fortune = new Fortune(sites);
			}

			if (auto)
			{
				cursorMoved = true;
				cursorY += 2.0f;
			}

			fortune.debug = debug;
			if (cursorMoved)
			{
				cursorMoved = false;
				double h = 1.0 - cursorY / windowY;
				if (h > cursorPos)
					fortune = new Fortune(sites);

				cursorPos = h;

				try
				{
					fortune.processTo(cursorPos);
					processResult();
					fortune.beach.forEach(sort ->
					{
						Arc arc = (Arc) sort;
						Arc next = (Arc) fortune.beach.higher(arc);
						if (next != null)
							print("[" + arc.left(fortune.sweepLine()).x
								+ ", "
								+ next.left(fortune.sweepLine()).x
								+ "]: " + arc.site);
						else
							print("[" + arc.left(fortune.sweepLine()).x
								+ ", Infinity]: " + arc.site);
					});
					print("Queue size: " + fortune.queue.size());
					print("Beachline size: " + fortune.beach.size());
				}
				catch (Exception e)
				{
					print(e.toString());
				}
			}

			if (auto && fortune.peek() == null || cursorY >= 2 * windowY)
			{
				regenerate = true;
				cursorY = -2.0f;
			}

			glClear(GL_COLOR_BUFFER_BIT);

			setColor(0.0f, 0.0f, 0.0f, 1.0f);
			drawLines(new float[] {0.0f, (float) cursorPos, 1.0f, (float) cursorPos});

			setColor(1.0f, 0.0f, 0.0f, 1.0f);
			drawBeachline();

			// setColor(1.0f, 0.0f, 0.0f, 0.2f);
			// drawAllParabolas();

			// setColor(0.0f, 0.0f, 0.0f, 0.1f);
			// drawBeachlineVerticals();

			setColor(1.0f, 0.0f, 0.0f, 1.0f);
			drawPoints(coords);

			setColor(0.0f, 0.0f, 1.0f, 1.0f);
			drawQueuedCircleEvents();

			setColor(0.0f, 0.0f, 0.0f, 1.0f);
			drawLines(edges);

			setColor(0.0f, 0.0f, 0.0f, 0.3f);
			drawLines(rays);
			// drawPoints(rayOrigins);

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	static void genOkData(int count, int numSites)
	{
		List<List<Vector>> res = new ArrayList<>();

		while (count > 0)
		{
			List<Vector> sites = genSites(numSites);
			try
			{
				Validator valid = new Validator(sites, new Fortune(sites).processAll());
				if (valid.result())
				{
					res.add(sites);
					count--;
				}
				else
				{
					System.out.println("Invalid result");
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}

		FileHandler.write(Parse.toStringll(res), "ok_data.txt");
	}

	static void genBadData(int count, int numSites)
	{
		List<List<Vector>> res = new ArrayList<>();

		while (count > 0)
		{
			List<Vector> sites = genSites(numSites);
			try
			{
				Validator valid = new Validator(sites, new Fortune(sites).processAll());
				if (!valid.result())
				{
					res.add(sites);
					count--;
				}

			}
			catch(Exception e)
			{
				res.add(sites);
				count--;
			}
		}

		FileHandler.write(Parse.toStringll(res), "bad_data.txt");
	}

	public static void main(String[] args)
	{
		new App().run();
	}
}
