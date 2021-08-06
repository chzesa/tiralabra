#version 450 core

layout (location = 0) in vec2 coords;

void main()
{
	gl_PointSize = 5.0;
	gl_Position = vec4(coords * 2 - vec2(1, 1), 0.0, 1.0);
}
