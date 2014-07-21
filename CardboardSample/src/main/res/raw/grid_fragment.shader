precision mediump float;
varying vec4 v_Color;
varying vec3 v_Grid;
varying float v_isFloor;

uniform vec3 u_LightPos;        // The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.

varying vec3 v_Position;        // Interpolated position for this fragment.
varying vec3 v_Normal;          // Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main() {
    float depth = gl_FragCoord.z / gl_FragCoord.w; // calculate world-space distance

    if (v_isFloor > 0.5) {
        if ((mod(abs(v_Grid[0]), 10.0) < 0.1) || (mod(abs(v_Grid[2]), 10.0) < 0.1)) {
            gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)
                    + min(1.0, depth / 90.0) * v_Color;
        } else {
            gl_FragColor = v_Color;
        }
    } else {
        gl_FragColor = v_Color;

        // Multiply the color by the diffuse illumination level and texture value to get final output color.
        gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));
    }
}