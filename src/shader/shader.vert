#version 450 core

layout (location = 0) in vec2 coords;
layout (location = 1) uniform float zoomFactor;

void main()
{
	gl_PointSize = 5.0;
	vec2 center = vec2(0.5, 0.5);
	vec2 scaled = zoomFactor * (coords - center) + center;
	gl_Position = vec4(scaled * 2 - vec2(1, 1), 0.0, 1.0);
}
