varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    gl_FragColor = vec4(1.0 - color1.r,1.0 - color1.g,1.0 - color1.b,1.0);
}