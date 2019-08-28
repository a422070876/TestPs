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
        highp vec4 rgba;
        if(color1.r + color2.r >= 1.0)
            rgba.r = 1.0;
        else
            rgba.r = 0.0;
        if(color1.g + color2.g >= 1.0)
            rgba.g = 1.0;
        else
            rgba.g = 0.0;
        if(color1.b + color2.b >= 1.0)
            rgba.b = 1.0;
        else
            rgba.b = 0.0;
        rgba.a = 1.0;
        gl_FragColor = rgba;
    }
}