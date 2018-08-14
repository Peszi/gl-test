attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_color;

varying vec4 v_position;
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = u_color;
    v_texCoords = vec2(a_texCoord0.x, 1 - a_texCoord0.y);

    v_position = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projTrans * v_position;
}