package com.example.mytranslationapp;

import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    protected static final String TAG = "Main2";
    private Button btnGoBack;
    private SimpleCursorAdapter dataAdapter;
    private DataAdapter mDbHelper;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnGoBack = (Button)findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        ImageView fav = (ImageView)findViewById(R.id.fav);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavorites();
            }
        });

        s = getIntent().getStringExtra("LOOKUP");

        mDbHelper = new DataAdapter(Main2Activity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        displayResult();
    }

    private void displayResult() {
        Cursor testdata = mDbHelper.getTestData(s);
        if(testdata.getCount()<=0){
            Log.v("Main2","null");
            Toast toast = Toast.makeText(Main2Activity.this,"找不到此單字",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            finish();
        }
        else {
            TextView vol = (TextView) findViewById(R.id.text_vocabulary);
            TextView exam = (TextView) findViewById(R.id.text_example);
            vol.setMovementMethod(ScrollingMovementMethod.getInstance());
            exam.setMovementMethod(ScrollingMovementMethod.getInstance());
            String textVol = testdata.getString(1) + "<br><br><font color=#cc0029>解釋：</font><br>" + testdata.getString(2);
            String textExam = "<font color=#cc0029>例句：</font><br>" + testdata.getString(3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                vol.setText(Html.fromHtml(textVol, Html.FROM_HTML_MODE_LEGACY));
                exam.setText(Html.fromHtml(textExam, Html.FROM_HTML_MODE_LEGACY));
            } else {
                vol.setText(Html.fromHtml(textVol));
                exam.setText(Html.fromHtml(textExam));
            }
            if (mDbHelper.isFavorite(s)){
                ImageView fav = (ImageView) findViewById(R.id.fav);
                fav.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        }
    }

    private void doFavorites(){
        if(!mDbHelper.isFavorite(s)){
            ImageView fav = (ImageView) findViewById(R.id.fav);
            mDbHelper.addToFavorites(s);
            fav.setImageResource(R.drawable.ic_favorite_black_24dp);
            Toast.makeText(Main2Activity.this,"Successfully, Add favorite ",Toast.LENGTH_SHORT).show();
        }
        else {
            ImageView fav = (ImageView) findViewById(R.id.fav);
            mDbHelper.removeFromFavorites(s);
            fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            Toast.makeText(Main2Activity.this,"Delete from favorite",Toast.LENGTH_SHORT).show();
        }
    }

    public void onDestroy() {

        super.onDestroy();
        mDbHelper.close();
    }
}
