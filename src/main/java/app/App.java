package app;

import app.io.*;
import app.vector.Vector;
import app.fortune.*;
import app.parse.Parse;
import app.tree.*;

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
	Vector center = new Vector(0.5, 0.5);
	Fortune fortune;
	boolean regenerate = true;
	boolean auto = true;
	boolean next = false;
	boolean pause = false;
	boolean move = false;
	Vector lastCursorPos = null;
	float zoomFactor = 1.0f;
	Generator gen;
	float[] edges;
	float[] rays;
	float[] coords;
	float[] rayOrigins;
	Vector topLeft = screenPointToWorldPoint(new Vector(0, 0));
	Vector bottomRight = screenPointToWorldPoint(new Vector(1, 1));

	App(Generator gen)
	{
		this.gen = gen;
	}

	public void run()
	{
		initWindow();
		GL.createCapabilities();
		initOpengl();
		loop();
	}

	void saveDataset()
	{
		FileHandler.write(Parse.toStringl(sites), "sites_" + java.time.LocalDateTime.now().toString() + ".txt");
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

		glfwSetScrollCallback(window, (window, xoffset, yoffset) ->
		{
			zoomFactor *= yoffset < 0 ? 0.8f : 1.2f;
			cursorMoved = true;
		});

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
		{
			if (action != GLFW_RELEASE)
				return;

			if (key == GLFW_KEY_ESCAPE)
				glfwSetWindowShouldClose(window, true);
			if (key == GLFW_KEY_SPACE)
			{
				cursorMoved = true;
				regenerate = true;
			}
			if (key == GLFW_KEY_A)
				auto = !auto;
			if (key == GLFW_KEY_S)
				saveDataset();
			if (key == GLFW_KEY_D)
				debug = !debug;
			if (key == GLFW_KEY_N)
				next = true;
			if (key == GLFW_KEY_P)
				pause = !pause;
			if (key == GLFW_KEY_C)
				center = new Vector(0.5, 0.5);
			if (key == GLFW_KEY_V)
			{
				try
				{
					if (new Validator(sites, new Fortune(sites).processAll()).result())
						System.out.println("Ok result.");
					else
						System.out.println("Invalid result.");
				}
				catch (Exception e)
				{
					System.out.println("Invalid result.");
					System.out.println(e);
					e.printStackTrace();
				}
			}
			if (key == GLFW_KEY_1)
				numSites = 1;
			if (key == GLFW_KEY_2)
				numSites = 2;
			if (key == GLFW_KEY_3)
				numSites = 3;
			if (key == GLFW_KEY_4)
				numSites = 4;
			if (key == GLFW_KEY_5)
				numSites = 5;
			if (key == GLFW_KEY_6)
				numSites = 6;
			if (key == GLFW_KEY_7)
				numSites = 7;
			if (key == GLFW_KEY_8)
				numSites = 8;
			if (key == GLFW_KEY_9)
				numSites = 9;
			if (key == GLFW_KEY_0)
				numSites = 10;

			if (key == GLFW_KEY_EQUAL)
				numSites *= 10;

			if (key == GLFW_KEY_MINUS)
				numSites /= 10;
		});

		glfwSetCursorPosCallback(window, (window, x, y) ->
		{
			if (auto) return;
			cursorX = x;
			cursorY = y;
			cursorMoved = true;
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) ->
		{
			if (button != GLFW_MOUSE_BUTTON_3)
				return;
			if (action == GLFW_PRESS)
				lastCursorPos = null;
			move = action != GLFW_RELEASE;
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

	void setCenter(float x, float y)
	{
		glUniform2f(2, x, y);
	}

	void setColor(float r, float g, float b, float a)
	{
		glUniform4f(0, r, g, b, a);
	}

	void setZoomFactor(float z)
	{
		glUniform1f(1, z);
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
		double inc = 2.0 / windowX * (bottomRight.x - topLeft.x);
		float[] coords = new float[
			4 * ((int) Math.ceil(Math.abs(xMax - xMin) / inc) + 1)
		];
		int i = 0;

		while (xMin < xMax)
		{
			double x1, x2, y1, y2;
			x1 = xMin;
			x2 = Math.min(x1 + inc, xMax);
			y1 = Utils.parabolaY(focus, directrix, x1);
			y2 = Utils.parabolaY(focus, directrix, x2);

			coords[i * 4 + 0] = (float) x1;
			coords[i * 4 + 1] = (float) y1;

			coords[i * 4 + 2] = (float) x2;
			coords[i * 4 + 3] = (float) y2;

			xMin += inc;
			i++;
		}

		drawLines(coords);
	}

	void extractSiteCoords()
	{
		int i = 0;
		coords = new float[sites.size() * 2];
		for (Vector site : sites)
		{
			coords[i * 2] = (float) site.x;
			coords[i * 2 + 1] = (float) site.y;
			i++;
		}
	}

	void print(String s)
	{
		if (debug)
			System.out.println(s);
	}

	void drawQueuedCircleEvents()
	{
		fortune.queue.forEach(e ->
		{
			if (!e.isSiteEvent())
				drawPoints(new float[] {(float) e.point().x, (float) e.point().y});
		});
	}

	void drawBeachline()
	{
		double inc = 2.0 / windowX * (bottomRight.x - topLeft.x);
		List<Vector> coords = new ArrayList<>();
		List<Arc> arcs = new ArrayList<>();
		fortune.beach.forEach(v -> arcs.add((Arc) v));

		double x = topLeft.x;
		for (Arc arc : arcs)
		{
			x = Math.min(x, Math.max(arc.left(fortune.sweepLine()).x, topLeft.x));
			double limit = arc.right(fortune.sweepLine()).x;
			limit = Math.min(limit, bottomRight.x);
			while (x < limit)
			{
				Vector a = Utils.parabolaPt(arc.site, fortune.sweepLine(), x);
				Vector b = Utils.parabolaPt(arc.site, fortune.sweepLine(), Math.min(x + inc, limit));
				coords.add(a);
				coords.add(b);
				x += inc;
			}
		}

		float[] arr = new float[coords.size() * 2];
		int i = 0;
		for (Vector point : coords)
		{
			arr[i * 2 + 0] = (float) point.x;
			arr[i * 2 + 1] = (float) point.y;
			i++;
		}

		drawLines(arr);
	}

	void drawAllParabolas()
	{
		for (Vector site : sites)
		{
			if (Math.abs(site.y - fortune.sweepLine()) < Vector.PRECISION
				|| site.y < fortune.sweepLine())
				continue;

			drawParabola(site, fortune.sweepLine(), topLeft.x, bottomRight.x);
		}
	}

	void drawBeachlineVerticals()
	{
		fortune.beach.forEach(v ->
		{
			Arc arc = (Arc) v;
			float xMin = (float) arc.left(fortune.sweepLine()).x;

			if (xMin == Double.NEGATIVE_INFINITY)
				xMin = 0;

			drawLines(new float[] {xMin, (float) topLeft.y, xMin, (float) bottomRight.y});
		});
	}

	void drawBoundaries()
	{
		fortune.beach.forEach(v ->
		{
			Arc arc = (Arc) v;
			if (arc.left == null)
				return;

			Vector origin = arc.left.ray.origin;
			Vector direction = arc.left.ray.direction.normalize();
			float x1, x2, y1, y2;
			x1 = (float) origin.x;
			x2 = x1 + (float) direction.x * 100;
			y1 = (float) origin.y;
			y2 = y1 + (float) direction.y * 100;

			drawLines(new float[]{x1, y1, x2, y2});
		});
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

	Vector screenPointToWorldPoint(Vector p)
	{
		return p.sub(new Vector(0.5, 0.5))
			.scale(1.0f / zoomFactor)
			.add(center);
	}

	void printInfo()
	{
		processResult();
		if (fortune.beach == null)
			return;

		fortune.beach.forEach(v ->
		{
			Arc arc = (Arc) v;
			print("[" + arc.left(fortune.sweepLine()).x
				+ ", "
				+ arc.right(fortune.sweepLine()).x
				+ "]: " + arc.site);
		});
		print("Queue size: " + fortune.queue.size());
		print("Beachline size: " + fortune.beach.size());
	}

	void loop()
	{
		double cursorPos = 0;
		setCenter((float) center.x, (float) center.y);

		while (!glfwWindowShouldClose(window))
		{
			Vector cPos = screenPointToWorldPoint(
				new Vector(cursorX / windowX, 1.0 - cursorY / windowY)
			);

			topLeft = screenPointToWorldPoint(new Vector(0, 0));
			bottomRight = screenPointToWorldPoint(new Vector(1, 1));
			if (viewportChanged)
			{
				viewportChanged = false;
				glViewport(0, 0, windowX, windowY);
			}

			if (move && cursorMoved)
			{
				if (lastCursorPos == null)
				{
					lastCursorPos = cPos;
				}
				else
				{
					center = center.add(cPos.sub(lastCursorPos));
					lastCursorPos = cPos;
				}

				setCenter((float) center.x, (float) center.y);
			}

			if (regenerate)
			{
				regenerate = false;
				sites = gen.next();
				if (sites == null)
					sites = gen.next(numSites, 0.05, 0.95, 0.05, 0.95);
				extractSiteCoords();
				fortune = new Fortune(sites);
			}

			if (next)
			{
				next = false;
				try
				{
					fortune.process();
				}
				catch (Error e)
				{
					print(e.toString());
					e.printStackTrace();
				}
				processResult();
				printInfo();
			}

			if (auto && !pause)
			{
				cursorY += 2.0f;
			}

			if ((cursorMoved || auto) && !pause)
			{
				cursorMoved = false;
				if (cPos.y > cursorPos)
					fortune = new Fortune(sites);

				cursorPos = cPos.y;

				try
				{
					fortune.processTo(cursorPos);
				}
				catch (Error e)
				{
					print(e.toString());
					e.printStackTrace();
				}
				processResult();
				printInfo();
			}

			if (auto && fortune.peek() == null || cursorY >= 2 * windowY)
			{
				regenerate = true;
				cursorY = -2.0f;
			}

			glClear(GL_COLOR_BUFFER_BIT);
			setZoomFactor(zoomFactor);

			setColor(1.0f, 0.0f, 0.0f, 1.0f);
			drawPoints(coords);

			setColor(0.0f, 0.0f, 0.0f, 1.0f);
			drawLines(new float[] {(float) topLeft.x, (float) cursorPos, (float) bottomRight.x, (float) cursorPos});

			if (fortune.beach != null)
			{
				
				setColor(1.0f, 0.0f, 0.0f, 1.0f);
				drawBeachline();

				// setColor(1.0f, 0.0f, 0.0f, 0.2f);
				// drawAllParabolas();

				// setColor(0.0f, 0.0f, 0.0f, 0.1f);
				// drawBeachlineVerticals();

				

				setColor(0.0f, 0.0f, 1.0f, 1.0f);
				drawQueuedCircleEvents();

				setColor(0.0f, 0.0f, 0.0f, 1.0f);
				drawLines(edges);

				setColor(0.0f, 0.0f, 0.0f, 0.3f);
				drawLines(rays);
				// drawPoints(rayOrigins);	
			}

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{
		Config conf = new Config(args);

		if (conf.bench)
		{
			Benchmark.run(conf);
			return;
		}

		App app = new App(new Generator(conf.input, conf.seed));
		app.numSites = conf.sites;
		app.run();
	}
}
