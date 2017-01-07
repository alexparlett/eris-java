#version 330 core

in VertexData
{
	vec2 texcoords;
} iData;

uniform sampler2D diffuse;

out vec4 color;

void main()
{    
	color = texture(diffuse, iData.texcoords);
}