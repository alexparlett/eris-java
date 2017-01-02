#version 330 core

uniform samplerCube diffuse;

in VertexData
{
	vec3 texcoords;
} iData;


out vec4 color;

void main()
{    
	color = texture(diffuse, iData.texcoords);
}