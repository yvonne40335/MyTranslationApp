package com.example.mytranslationapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback{
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private Camera.Size mBestPictureSize;
    private Camera.Size mBestPreviewSize;
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    private static final String TAG = "MainActivity";
    private FrameLayout ll;
    private Button btncollection;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = (FrameLayout) findViewById(R.id.mainLayout);

        /****** add red rectangle to set the ocr region *******/
        DrawCaptureRect mDraw = new DrawCaptureRect(MainActivity.this, 150,250,400,150, Color.RED);
        //在一個activity上面添加额外的content
        addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews();

        /****** initialize tesseract ******/
        datapath = getFilesDir()+ "/tesseract/";
        checkFile(new File(datapath + "tessdata/"));  //make sure training data has been copied
        String lang = "eng";  //initialize Tesseract API
        mTess = new TessBaseAPI();
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_OSD_ONLY);
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz");
        mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST,"()'");
        mTess.init(datapath, lang);

        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Please Wait Loading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_fav:
                        Intent intent = new Intent(getBaseContext(), FavCollection.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.action_camera:
                        Toast.makeText(MainActivity.this,"Camera",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_gallery:
                        Toast.makeText(MainActivity.this,"Gallery",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        /*btncollection = (Button)findViewById(R.id.btn_collection);
        btncollection.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(), FavCollection.class);
                startActivity(intent);
            }
        });*/

        /*btnsearch = (Button)findViewById(R.id.btn_search);
        btnsearch.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                String lookup = "yacht";
                intent.putExtra("LOOKUP", lookup);
                startActivity(intent);
            }
        });*/
    }

    /****** for red rectangle ******/
    class DrawCaptureRect extends View
    {
        private int mcolorfill;
        private int mleft, mtop, mwidth, mheight;
        public DrawCaptureRect(Context context, int left, int top, int width, int height, int colorfill) {
            super(context);
            // TODO Auto-generated constructor stub
            this.mcolorfill = colorfill;
            this.mleft = left;
            this.mtop = top;
            this.mwidth = width;
            this.mheight = height;
        }
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            Paint mpaint = new Paint();
            mpaint.setColor(mcolorfill);
            mpaint.setStyle(Paint.Style.FILL);
            mpaint.setStrokeWidth(5.0f);
            canvas.drawLine(mleft, mtop, mleft+mwidth, mtop, mpaint);
            canvas.drawLine(mleft+mwidth, mtop, mleft+mwidth, mtop+mheight, mpaint);
            canvas.drawLine(mleft, mtop, mleft, mtop+mheight, mpaint);
            canvas.drawLine(mleft, mtop+mheight, mleft+mwidth, mtop+mheight, mpaint);
            super.onDraw(canvas);
        }

    }



    /****** use camera preview to do ocr, thus have to convert byte array to bitmap ******/
    ByteArrayOutputStream baos;
    byte[] rawImage;
    Bitmap bitmap;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.setOneShotPreviewCallback(MainActivity.this);
        //處理data
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//獲取尺寸,格式轉換的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG圖片的質量[0-100],100最高
        rawImage = baos.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
    }

    private void initViews() {
        mSurfaceView = (SurfaceView) this.findViewById(R.id.svPreview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /****** surfaceview for camera ******/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        if (Build.VERSION.SDK_INT >= 8)
            camera.setDisplayOrientation(90);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }

        final float ratio = (float) mSurfaceView.getWidth() / mSurfaceView.getHeight();

        Camera.Parameters parameters = camera.getParameters();
        // 設置pictureSize
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        if (mBestPictureSize == null) {
            mBestPictureSize =findBestPictureSize(pictureSizes, parameters.getPictureSize(), ratio);
        }
        parameters.setPictureSize(mBestPictureSize.width, mBestPictureSize.height);

        // 設置previewSize
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        if (mBestPreviewSize == null) {
            mBestPreviewSize = findBestPreviewSize(previewSizes, parameters.getPreviewSize(),
                    mBestPictureSize, ratio);
        }
        parameters.setPreviewSize(mBestPreviewSize.width, mBestPreviewSize.height);

        //setSurfaceViewSize(mBestPreviewSize);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 取得相機參數
        Camera.Parameters parameters = camera.getParameters();

        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPreviewSize(640, 480);
        camera.setOneShotPreviewCallback(MainActivity.this);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.setOneShotPreviewCallback(null);
        camera.release();
        camera = null;
    }

    /****** 設定讓preview size 和surface size 比例相同，才不會變形 ******/
    private void setSurfaceViewSize(Camera.Size size) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        params.height = mSurfaceView.getWidth() * size.width / size.height;
        mSurfaceView.setLayoutParams(params);
    }

    /**
     * 找到短邊比長邊大於所接受的最小比例的最大尺寸
     *
     * @param sizes       支持的尺寸列表
     * @param defaultSize 默認大小
     * @param minRatio    相機圖片短邊比長邊所接受的最小比例
     * @return 返回計算之后的尺寸
     */
    private Camera.Size findBestPictureSize(List<Camera.Size> sizes, Camera.Size defaultSize, float minRatio) {
        final int MIN_PIXELS = 320 * 480;

        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            //移除不滿足比例的尺寸
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }
            //移除太小的尺寸
            if (size.width * size.height < MIN_PIXELS) {
                it.remove();
            }
        }

        // 返回符合條件中最大尺寸的一個
        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }
        // 沒得選，默認
        return defaultSize;
    }

    /**
     * @param sizes
     * @param defaultSize
     * @param pictureSize 圖片的大小
     * @param minRatio preview短邊比長邊所接受的最小比例
     * @return
     */
    private Camera.Size findBestPreviewSize(List<Camera.Size> sizes, Camera.Size defaultSize,
                                            Camera.Size pictureSize, float minRatio) {
        final int pictureWidth = pictureSize.width;
        final int pictureHeight = pictureSize.height;
        boolean isBestSize = (pictureHeight / (float)pictureWidth) > minRatio;
        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }

            // 找到同樣的比例，直接返回
            if (isBestSize && size.width * pictureHeight == size.height * pictureWidth) {
                return size;
            }
        }

        // 未找到同樣的比例的，返回尺寸最大的
        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }

        // 沒得選，默認
        return defaultSize;
    }

    private static void sortSizes(List<Camera.Size> sizes) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                return b.height * b.width - a.height * a.width;
            }
        });
    }

    /****** camera origin setting is horizontal, so we have to rotate it 90 degree ******/
    public Bitmap rotationBitmap(Bitmap picture) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(picture,picture.getWidth(),picture.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        return rotatedBitmap;
    }

    /****** doing ocr, we have to copy traindata ******/
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    /****** when click, have to rotate bitmap、set bitmap to do ocr、display the result、recycle the bitmap ******/
    public void processImage(View view){

        bitmap = rotationBitmap(bitmap);

        String OCRresult = null;
        if (bitmap != null)
        {
            mTess.setImage(bitmap);
            mTess.setRectangle(350,450,400,150);
            OCRresult = mTess.getUTF8Text();


            String[] tokens = OCRresult.split(" ");
            if(!tokens[0].matches("[a-zA-Z]*")) {
                tokens[0]="";
                Toast.makeText(MainActivity.this, "請再試一次", Toast.LENGTH_SHORT).show();
            }

            TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
            OCRTextView.setText(tokens[0]);
            OCRTextView.setVisibility(View.VISIBLE);

            /*Properties props = new Properties();
            props.put("annotators","tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            Annotation document = new Annotation(tokens[0]);
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            for(CoreMap sentence: sentences){
                for(CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)){
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    Log.v("Main",word+"\t"+lemma);
                }
            }*/



            final String lookup = tokens[0];
            OCRTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    progress.show();
                    new Thread(){
                        public void run(){
                            try{
                                Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                intent.putExtra("LOOKUP", lookup);
                                startActivity(intent);
                                sleep(2000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                progress.dismiss();
                            }
                        }
                    }.start();
                }});

        }

        if(bitmap != null && !bitmap.isRecycled()){
            // 回收並且設為null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();

    }
}

