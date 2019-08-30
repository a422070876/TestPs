varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp float r = abs(color1.g - color1.b + color1.g + color1.r) * color1.r;
    highp float g = abs(color1.b - color1.g + color1.b + color1.r) * color1.r;
    highp float b = abs(color1.b - color1.g + color1.b + color1.r) * color1.g;
    gl_FragColor = vec4(r,g,b,1.0);
}