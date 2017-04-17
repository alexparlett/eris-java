#version 330 core

uniform vec4 outlineColor;

out vec4 color;

void main()
{    
    color = outlineColor;
}