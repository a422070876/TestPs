package com.hyq.hm.testps;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 海米 on 2017/8/16.
 */

public class BitmapRenderer {

    public static int[] fsIds = {
            R.raw.color_bian_an_fs,R.raw.color_bian_liang_fs,R.raw.color_zheng_pian_die_di_fs,
            R.raw.color_lv_se_fs,R.raw.color_yan_se_jia_shen_fs,R.raw.color_yan_se_jian_dan_fs,
            R.raw.color_xian_xing_jia_shen_fs,R.raw.color_xian_xing_jian_dan_fs,R.raw.color_die_jia_fs,
            R.raw.color_qiang_guang_fs,R.raw.color_rou_guang_fs,R.raw.color_liang_guang_fs,
            R.raw.color_dian_guang_fs,R.raw.color_xian_xing_guang_fs,R.raw.color_shi_se_hun_he_fs,
            R.raw.color_pai_chu_fs,R.raw.color_cha_zhi_fs
    };

    public static String[] names = {
            "变暗","变亮","正片叠底","滤色","颜色加深",
            "颜色减淡","线性加深","线性减淡","叠加","强光",
            "柔光","亮光","点光","线性光","实色混合",
            "排除","差值"
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureVertexBuffer;
    private int[] programIds;
    private int[] aPositionHandle;
    private int[] uTextureSamplerHandle;
    private int[] sTextureSamplerHandle;
    private int[] aTextureCoordHandle;
    private int[] textures;
    private String vertexShader;
    private String[] fragmentShaders;
    public BitmapRenderer(Context context){
        final float[] vertexData = {
                1f, -1f, 0f,
                -1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };


        final float[] textureVertexData = {
                1f, 0f,
                0f, 0f,
                1f, 1f,
                0f, 1f
        };
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);

        programIds = new int[fsIds.length];
        aPositionHandle = new int[fsIds.length];
        uTextureSamplerHandle = new int[fsIds.length];
        sTextureSamplerHandle = new int[fsIds.length];
        aTextureCoordHandle = new int[fsIds.length];
        fragmentShaders = new String[fsIds.length];
        vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vs);
        for (int i = 0; i < fsIds.length; i++){
            fragmentShaders[i] = ShaderUtils.readRawTextFile(context, fsIds[i]);
        }


    }
    private int width,height;
    public void initShader(Bitmap[] bitmaps){
        for (int i = 0; i < fsIds.length; i++){
            programIds[i] = ShaderUtils.createProgram(vertexShader, fragmentShaders[i]);
            aPositionHandle[i] = GLES20.glGetAttribLocation(programIds[i], "aPosition");
            uTextureSamplerHandle[i] = GLES20.glGetUniformLocation(programIds[i], "uTexture");
            sTextureSamplerHandle[i] = GLES20.glGetUniformLocation(programIds[i], "sTexture");
            aTextureCoordHandle[i] = GLES20.glGetAttribLocation(programIds[i], "aTexCoord");
        }



        width = bitmaps[0].getWidth();
        height = bitmaps[0].getHeight();
        textures = new int[bitmaps.length];
        GLES20.glGenTextures(textures.length, textures, 0);
        int i = 0;
        for (int texture : textures){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmaps[i],0);
            i++;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
    private void viewportSize(int screenWidth,int screenHeight,int videoWidth,int videoHeight) {
        int left, top, viewWidth, viewHeight;
        float sh = screenWidth * 1.0f / screenHeight;
        float vh = videoWidth * 1.0f / videoHeight;
        if (sh < vh) {
            left = 0;
            viewWidth = screenWidth;
            viewHeight = (int) (videoHeight * 1.0f / videoWidth * viewWidth);
            top = (screenHeight - viewHeight) / 2;
        } else {
            top = 0;
            viewHeight = screenHeight;
            viewWidth = (int) (videoWidth * 1.0f / videoHeight * viewHeight);
            left = (screenWidth - viewWidth) / 2;
        }
        GLES20.glViewport(left, top, viewWidth, viewHeight);
    }
    public void drawFrame(int screenWidth,int screenHeight,int index){
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        viewportSize(screenWidth,screenHeight,width,height);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(programIds[index]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
        GLES20.glUniform1i(uTextureSamplerHandle[index],0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[1]);
        GLES20.glUniform1i(sTextureSamplerHandle[index],1);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionHandle[index]);
        GLES20.glVertexAttribPointer(aPositionHandle[index], 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle[index]);
        GLES20.glVertexAttribPointer(aTextureCoordHandle[index], 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

}
