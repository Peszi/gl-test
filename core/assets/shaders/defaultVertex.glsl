#version 330

attribute vec3 a_position;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_color;

varying vec4 v_color;

void main() {
    v_color = u_color;
    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projTrans * pos;
}