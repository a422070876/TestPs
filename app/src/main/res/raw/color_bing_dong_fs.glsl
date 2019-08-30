varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp float r = (color1.r - color1.g - color1.b)*3.0*0.5;
    highp float g = (color1.g - color1.r - color1.b)*3.0*0.5;
    highp float b = (color1.b - color1.g - color1.r)*3.0*0.5;
    r = r < 0.0 ? 0.0 : r > 1.0 ? 1.0 : r;
    g = g < 0.0 ? 0.0 : g > 1.0 ? 1.0 : g;
    b = b < 0.0 ? 0.0 : b > 1.0 ? 1.0 : b;
    gl_FragColor = vec4(r,g,b,1.0);
}