package com.example.mytranslationapp;

import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
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

        s = getIntent().getStringExtra("LOOKUP");

        displayResult();
    }

    private void displayResult() {

        mDbHelper = new DataAdapter(Main2Activity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

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
        }
    }

    public void onDestroy() {

        super.onDestroy();
        mDbHelper.close();
    }
}
