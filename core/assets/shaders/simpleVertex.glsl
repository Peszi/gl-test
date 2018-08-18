attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec3 u_cameraPos;
uniform vec4 u_color;

varying vec4 v_position;
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_position = u_worldTrans * vec4(a_position, 1.0);

//    float transition = min(length(u_cameraPos - v_position.xyz), 100.0) / 100.0;
//    v_color = vec4(mix(vec3(1f, 0f, 0f), vec3(0f, 0f, 1f), transition) * u_color, u_color.a);
    v_color = u_color + vec4(u_cameraPos, 0.1) * 0.0001;
    v_texCoords = vec2(a_texCoord0.x, 1 - a_texCoord0.y);

    gl_Position = u_projTrans * v_position;
}