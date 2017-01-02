#version 330 core

layout (location = 0) in vec3 position;

uniform mat4 view;
uniform mat4 projection;

out VertexData
{
   vec3 texcoords;
} oData;

void main()
{
	vec4 pos = projection * view * vec4(position, 1.f);
	gl_Position = pos.xyww;
	oData.texcoords = -position;
}