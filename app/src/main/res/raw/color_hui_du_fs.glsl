varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp float grey = (color1.r+color1.g+color1.b)/3.0;
    gl_FragColor = vec4(grey,grey,grey,1.0);
}