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
        if(color2.r > 0.5)
            rgba.r = color1.r*(1.0 - color2.r)+sqrt(color1.r)*(2.0*color2.r - 1.0);
        else
            rgba.r = 2.0 * color1.r * color2.r + color1.r * color1.r *(1.0 - 2.0*color2.r);
        if(color2.g > 0.5)
            rgba.g = color1.g*(1.0 - color2.g)+sqrt(color1.g)*(2.0*color2.g - 1.0);
        else
            rgba.g = 2.0 * color1.g * color2.g + color1.g * color1.g *(1.0 - 2.0*color2.g);
        if(color2.b > 0.5)
            rgba.b = color1.b*(1.0 - color2.b)+sqrt(color1.b)*(2.0*color2.b - 1.0);
        else
            rgba.b = 2.0 * color1.b * color2.b + color1.b * color1.b *(1.0 - 2.0*color2.b);
        rgba.a = 1.0;
        gl_FragColor = rgba;
    }
}