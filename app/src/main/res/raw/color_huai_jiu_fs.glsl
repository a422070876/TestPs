varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp float r = 0.393 * color1.r + 0.769 * color1.g + 0.189 * color1.b;
    highp float g = 0.349 * color1.r + 0.686 * color1.g + 0.168 * color1.b;
    highp float b = 0.272 * color1.r + 0.534 * color1.g + 0.131 * color1.b;
    r = r < 0.0 ? 0.0 : r > 1.0 ? 1.0 : r;
    g = g < 0.0 ? 0.0 : g > 1.0 ? 1.0 : g;
    b = b < 0.0 ? 0.0 : b > 1.0 ? 1.0 : b;
    gl_FragColor = vec4(r,g,b,1.0);
}