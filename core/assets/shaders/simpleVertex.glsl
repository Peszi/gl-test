#version 330

attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec3 u_cameraPos;
uniform vec4 u_color;
uniform float u_time;

varying vec4 v_position;
varying vec4 v_color;
varying vec2 v_texCoords;
varying float v_time;

void main() {

    vec3 tmp = a_position + normalize(a_position) * (sin(u_time) / 2.0) * 0.02;
    v_position = u_worldTrans * vec4(tmp, 1.0);

     v_time = u_time * 10 + length(v_position);

//    float transition = min(length(u_cameraPos - v_position.xyz), 100.0) / 100.0;
//    v_color = vec4(mix(vec3(1f, 0f, 0f), vec3(0f, 0f, 1f), transition) * u_color, u_color.a);
    v_color = u_color + vec4(u_cameraPos, 0.1) * 0.0001;
    v_texCoords = vec2(a_texCoord0.x, 1 - a_texCoord0.y);

    gl_Position = u_projTrans * v_position;
}