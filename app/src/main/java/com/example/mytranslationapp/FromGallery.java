package com.example.mytranslationapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FromGallery extends AppCompatActivity {

    PhotoCropView mCropView;
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    private ProgressDialog progress;
    private Bitmap bitmap;
    private int ocrX,ocrY,ocrWidth,ocrHeight;
    MenuItem playMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_gallery);

        Intent intent = new Intent();
        //開啟Pictures畫面Type設定為image
        intent.setType("image/*");
        //使用Intent.ACTION_GET_CONTENT這個Action 會開啟選取圖檔視窗讓您選取手機內圖檔
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //取得相片後返回本畫面
        startActivityForResult(intent, 1);

        setTitle("Gallery");

        mCropView = (PhotoCropView) findViewById(R.id.test);
        mCropView.setLocationListener(new PhotoCropView.onLocationListener() {
            @Override
            public void locationRect(int startX, int startY, int endX, int endY) {
                Log.v("cropplease","[ "+startX+"--"+startY+"--"+endX+"--"+endY+" ]");
                ocrX = startX;
                ocrY = startY;
                ocrWidth = endX-startX;
                ocrHeight = endY-startY;
            }
        });

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_gallery);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_his:
                        Intent intentHis = new Intent(getBaseContext(), History.class);
                        startActivity(intentHis);
                        finish();
                        break;
                    case R.id.action_fav:
                        Intent intentFav = new Intent(getBaseContext(), FavCollection.class);
                        startActivity(intentFav);
                        finish();
                        break;
                    case R.id.action_camera:
                        Intent intentCa = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intentCa);
                        finish();
                        break;
                    case R.id.action_gallery:
                        Toast.makeText(FromGallery.this,"Gallery",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        datapath = getFilesDir()+ "/tesseract/";

        //make sure training data has been copied
        checkFile(new File(datapath + "tessdata/"));
        //initialize Tesseract API
        String lang = "eng";
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

        progress = new ProgressDialog(FromGallery.this);
        progress.setMessage("Please Wait Loading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        //hiding default app icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.goback_actionbar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    //取得相片後返回的監聽式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //當使用者按下確定後
        if (resultCode == RESULT_OK) {
            //取得圖檔的路徑位置
            Uri uri = data.getData();
            //寫log
            Log.e("uri", uri.toString());
            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //取得圖片控制項ImageView
                ImageView imageView = (ImageView) findViewById(R.id.iv01);
                // 將Bitmap設定到ImageView
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void toggleMenu(View view){
        Intent intentGa = new Intent(getBaseContext(), FromGallery.class);
        startActivity(intentGa);
        finish();
    }

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

    public void processImage(View view){
        String OCRresult = null;
        if (bitmap != null)
        {
            mTess.setImage(bitmap);
            mTess.setRectangle(ocrX,ocrY+100,ocrWidth,ocrHeight);
            OCRresult = mTess.getUTF8Text();


            String[] tokens = OCRresult.split(" ");
            Log.v("gallery",tokens[0]);
            if(tokens[0].matches("[(]?[a-zA-Z]+[,|.|)]?")) {

                String result = tokens[0].replaceAll("[.,]","");
                TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView_Gallery);
                OCRTextView.setText(result);
                OCRTextView.setVisibility(View.VISIBLE);

                final String lookup = result.toLowerCase();
                OCRTextView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        progress.show();
                        new Thread(){
                            public void run(){
                                try{
                                    Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                    intent.putExtra("LOOKUP", "0"+lookup);
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
            else
            {
                tokens[0]="";
                Toast.makeText(FromGallery.this, "請再試一次", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
