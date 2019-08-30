varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp float min_color = min(min(color1.r,color1.g),color1.b);
    highp float max_color = max(max(color1.r,color1.g),color1.b);
    highp float floor_color =(min_color + max_color)*0.5;
    gl_FragColor = vec4(floor_color,floor_color,floor_color,1.0);
}