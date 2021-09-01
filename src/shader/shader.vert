#version 450 core

layout (location = 0) in vec2 coords;
layout (location = 1) uniform float zoomFactor;
layout (location = 2) uniform vec2 center;

void main()
{
	gl_PointSize = 5.0;
	vec2 scaled = (coords - center) * zoomFactor + vec2(0.5, 0.5);
	gl_Position = vec4(scaled * 2 - vec2(1, 1), 0.0, 1.0);
}
