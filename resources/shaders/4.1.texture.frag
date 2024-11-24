#version 330 core
out vec4 fragColor;

in vec3 ourColor;
in vec2 texCoord;

// texture sampler
uniform sampler2D ourTexture;

void main()
{
    fragColor = texture(ourTexture, texCoord) * vec4(ourColor, 1.0);
}