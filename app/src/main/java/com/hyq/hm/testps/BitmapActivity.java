package com.hyq.hm.testps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.nio.ByteBuffer;

public class BitmapActivity extends AppCompatActivity {
    private Handler testHandler;
    private HandlerThread testThread;

    private EGLUtils eglUtils;

    private Bitmap[] bitmaps;

    private BitmapRenderer bitmapRenderer;
    private int screenWidth,screenHeight;

    private Handler imageHandler;
    private HandlerThread imageThread;
    private ImageReader imageReader;
    private int imageWidth,imageHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        bitmaps = new Bitmap[2];
        bitmaps[0] = BitmapFactory.decodeResource(getResources(),R.drawable.ic_car);
        bitmaps[1] = Bitmap.createBitmap( bitmaps[0].getWidth(), bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_pkq);
        Canvas canvas = new Canvas(bitmaps[1]);
        int left = 3500;
        int top = 1300;
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new RectF(left,top,left+bitmap.getWidth(),top+bitmap.getHeight()),null);
        final ImageView imageView = findViewById(R.id.image_view);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                final Bitmap bitmap = bitmapBianAn(bitmaps[0],bitmaps[1]);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("============","setImageBitmap");
//                        imageView.setImageBitmap(bitmap);
//                    }
//                });
//            }
//        }.start();
//

        imageWidth = bitmaps[0].getWidth()/5;
        imageHeight = bitmaps[0].getHeight()/5;
        final Rect src = new Rect(0,0,imageWidth,imageHeight);

        final RectF dst = new RectF(0,0,imageWidth,imageHeight);
        testThread = new HandlerThread("testThread");
        testThread.start();
        testHandler = new Handler(testThread.getLooper());
        eglUtils = new EGLUtils();
        bitmapRenderer = new BitmapRenderer(this);


        imageThread = new HandlerThread("ImageThread");
        imageThread.start();
        imageHandler = new Handler(imageThread.getLooper());

        imageReader = ImageReader.newInstance(imageWidth,imageHeight, PixelFormat.RGBA_8888,1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                if(image != null){
                    int width = image.getWidth();
                    int height = image.getHeight();
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;
                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmp);
                    canvas.drawBitmap(bitmap, src, dst, null);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bmp);
                        }

                    });
                    image.close();
                }
            }
        },imageHandler);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(BitmapActivity.this,android.R.layout.simple_list_item_1,BitmapRenderer.names);
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
                        bitmapRenderer.drawFrame(imageWidth,imageHeight,position);
                        eglUtils.swap();
                        isDraw = false;
                    }
                });
            }
        });

        testHandler.post(new Runnable() {
            @Override
            public void run() {
                eglUtils.initEGL(imageReader.getSurface());
                bitmapRenderer.initShader(bitmaps);
                bitmapRenderer.drawFrame(imageWidth,imageHeight,0);
                eglUtils.swap();
            }
        });

    }

   private Bitmap bitmapBianAn(Bitmap bitmap1,Bitmap bitmap2){
        Bitmap bitmap = Bitmap.createBitmap(bitmap1.getWidth(),bitmap1.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0 ; x < bitmap.getWidth();x++){
            for (int y = 0;y < bitmap.getHeight();y++){
                int color1 = bitmap1.getPixel(x,y);
                Log.d("============","x = "+ x +": y = "+y);
                if(x >= 3500 && y >= 1300 &&
                        x < 3500 + bitmap2.getWidth() && y < 1300 + bitmap2.getHeight() ){
                    int color2 = bitmap2.getPixel(x - 3500,y - 1300);
                    int a = Color.alpha(color2);
                    if(a == 0){
                        bitmap.setPixel(x,y,color1);
                    }else{
                        int r1 = Color.red(color1);
                        int g1 = Color.green(color1);
                        int b1 = Color.blue(color1);

                        int r2 = Color.red(color2);
                        int g2 = Color.green(color2);
                        int b2 = Color.blue(color2);
                        if(a != 255){
                            int r = (int) (mix(r1/255.0f,r2/255.0f,a/255.0f)*255.0f);
                            int g = (int) (mix(g1/255.0f,g2/255.0f,a/255.0f)*255.0f);
                            int b = (int) (mix(b1/255.0f,b2/255.0f,a/255.0f)*255.0f);
                            bitmap.setPixel(x,y,Color.argb(255,r,g,b));
                        }else{
                            int r = Math.min(r1,r2);
                            int g = Math.min(g1,g2);
                            int b = Math.min(b1,b2);
                            bitmap.setPixel(x,y,Color.argb(255,r,g,b));
                        }
                    }
                }else{
                    bitmap.setPixel(x,y,color1);
                }
            }
        }
        return bitmap;
   }
    private float mix(float x,float y,float a){
        return x*(1.0f-a) +y*a;
    }
}
