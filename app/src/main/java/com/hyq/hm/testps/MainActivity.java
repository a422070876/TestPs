package com.hyq.hm.testps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private Handler testHandler;
    private HandlerThread testThread;

    private EGLUtils eglUtils;

    private Bitmap[] bitmaps;
    private BitmapRenderer bitmapRenderer;
    private int screenWidth,screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmaps = new Bitmap[2];
        bitmaps[0] = BitmapFactory.decodeResource(getResources(),R.drawable.ic_car);
        bitmaps[1] = Bitmap.createBitmap(bitmaps[0].getWidth(),bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_pkq);
        Canvas canvas = new Canvas(bitmaps[1]);
        int left = 3500;
        int top = 1300;
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new RectF(left,top,left+bitmap.getWidth(),top+bitmap.getHeight()),null);
        testThread = new HandlerThread("testThread");
        testThread.start();
        testHandler = new Handler(testThread.getLooper());
        eglUtils = new EGLUtils();

        bitmapRenderer = new BitmapRenderer(this);

        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, int format, int width, int height) {
                screenWidth = width;
                screenHeight = height;
                testHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        eglUtils.initEGL(holder.getSurface());
                        bitmapRenderer.initShader(bitmaps);
                        bitmapRenderer.drawFrame(screenWidth,screenHeight,0);
                        eglUtils.swap();
                    }
                });

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,BitmapRenderer.names);
        ListView listview= findViewById(R.id.list_view);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean isDraw = false;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(isDraw){
                    return;
                }
                isDraw = true;
                testHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        bitmapRenderer.drawFrame(screenWidth,screenHeight,position);
                        eglUtils.swap();
                        isDraw = false;
                    }
                });
            }
        });
    }
}
