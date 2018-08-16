#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

float near = 0.1;
float far  = 100.0;

float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0; // back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main() {
//    float depth = 1 - LinearizeDepth(gl_FragCoord.z) / far;
    vec3 color = texture2D(u_texture, v_texCoords).rgb * v_color.rgb;
    gl_FragColor = vec4(color, v_color.a);
}