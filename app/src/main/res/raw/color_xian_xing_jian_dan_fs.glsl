varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;
uniform highp sampler2D uTexture;
void main() {
    highp vec4 color1 = texture2D(uTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    highp vec4 color2 = texture2D(sTexture, vec2(vTexCoord.x,1.0-vTexCoord.y));
    if(color2.a == 0.0){
        gl_FragColor = color1;
    }else if(color2.a != 1.0){
        gl_FragColor = mix(color1,color2,color2.a);
    }else{
        highp vec4 rgba = color1 + color2;
        gl_FragColor = vec4(rgba.rgb,1.0);
    }
}