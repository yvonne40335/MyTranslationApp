package com.example.mytranslationapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Main2Activity extends AppCompatActivity {

    protected static final String TAG = "Main2";
    private Button btnGoBack;
    private SimpleCursorAdapter dataAdapter;
    private DataAdapter mDbHelper;
    String s, wordInDB,recv;
    boolean update;
    TextView textView;
    MenuItem playMenu;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        update=false;

        btnGoBack = (Button)findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        recv = getIntent().getStringExtra("LOOKUP");
        s = recv.subSequence(1,recv.length()).toString();
        Log.v("main2",s);

        mDbHelper = new DataAdapter(Main2Activity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        if(recv.charAt(0)=='0'){
            //先判斷是否有開啟Wi-Fi，有開啟則回傳true沒有則回傳false
            if(mNetworkInfo != null)
            {
                Log.v("wifi","true");
                Thread t = new thread();
                t.start();
                try {
                    Thread.sleep(1000);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favmenu, menu);
        playMenu = menu.findItem(R.id.mybutton);
        displayResult();
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            doFavorites();
        }
        return super.onOptionsItemSelected(item);
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
            setTitle(testdata.getString(1));
            TextView vol = (TextView) findViewById(R.id.text_vocabulary);
            TextView exam = (TextView) findViewById(R.id.text_example);
            vol.setMovementMethod(ScrollingMovementMethod.getInstance());
            exam.setMovementMethod(ScrollingMovementMethod.getInstance());


            Resources res = getResources();
            String text = res.getString(R.string.dic_result, testdata.getString(1));
            //int length = testdata.getString(1).length();
            String textVol = text + "<br><br><font color=#cc0029>解釋：</font><br>" + testdata.getString(2);
            //String textVol = testdata.getString(1) + "\n\n解釋：\n" + testdata.getString(2);
            String textExam = "<font color=#cc0029>例句：</font><br>" + testdata.getString(3);

            //SpannableString msp = new SpannableString(textVol);
            //指定区域设置字体格式；包括字体样式、大小、颜色、背景颜色、下划线、删除线、上下标、链接等。
            //msp.setSpan(new AbsoluteSizeSpan(60), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //msp.setSpan(new ForegroundColorSpan(Color.parseColor("#cc0029")), length+2, length+5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            wordInDB = testdata.getString(1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                vol.setText(Html.fromHtml(textVol, Html.FROM_HTML_MODE_LEGACY));
                exam.setText(Html.fromHtml(textExam, Html.FROM_HTML_MODE_LEGACY));
            } else {
                vol.setText(Html.fromHtml(textVol));
                exam.setText(Html.fromHtml(textExam));

                //vol.setText(msp);
                //vol.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (mDbHelper.isFavorite(wordInDB)){
                //ImageView fav = (ImageView) findViewById(R.id.fav);
                //fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                playMenu.setIcon(R.drawable.ic_favorite_black_24dp);
            }

            mDbHelper.addHistoryData(testdata.getString(1));
        }
    }

    private void doFavorites(){
        if(!mDbHelper.isFavorite(wordInDB)){
            //ImageView fav = (ImageView) findViewById(R.id.mybutton);
            mDbHelper.addToFavorites(wordInDB);
            //fav.setImageResource(R.drawable.ic_favorite_black_24dp);
            playMenu.setIcon(R.drawable.ic_favorite_black_24dp);
            Toast.makeText(Main2Activity.this,"Successfully, Add favorite ",Toast.LENGTH_SHORT).show();
            update=true;
        }
        else {
            //ImageView fav = (ImageView) findViewById(R.id.mybutton);
            mDbHelper.removeFromFavorites(wordInDB);
            //fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            playMenu.setIcon(R.drawable.ic_favorite_border_black_24dp);
            Toast.makeText(Main2Activity.this,"Delete from favorite",Toast.LENGTH_SHORT).show();
            update=true;
        }
    }

    public void onDestroy() {

        super.onDestroy();
        mDbHelper.close();
    }

    public class doit extends AsyncTask<Void,Void,Void>{

        String words;
        @Override
        protected Void doInBackground(Void... param){
            try {
                String url = String.format("http://wordnetweb.princeton.edu/perl/webwn?s=%s",s);
                Document document = Jsoup.connect(url).get();
                Elements content_b = document.getElementsByTag("b");
                if(content_b.size()==0){
                    Elements content = document.select("a[href]");
                    if(content.size()>=5)
                        s = content.get(4).text();
                }
                else {
                    s = content_b.get(0).text();
                }

                //Toast toast = Toast.makeText(Main2Activity.this,s,Toast.LENGTH_SHORT);
            }catch (Exception e){e.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            super.onPostExecute(avoid);
            textView.setText(s);
        }
    }

    class thread extends Thread {
        public void run() {
            try {
                String address = "192.168.1.111";

                int servPort = 80;
                socket = new Socket(address, servPort);
                System.out.println("socket");
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                System.out.println("Connected!!");

                String s_send;
                if(s.endsWith("s")|s.endsWith("es")) {
                    s_send = "n " + s.substring(0, s.length());
                    Log.v("noun",s);
                }
                else {
                    s_send = "v " + s.substring(0, s.length());
                    Log.v("verb",s);
                }
                byte[] sendstr = new byte[s_send.length()];
                System.arraycopy(s_send.getBytes(), 0, sendstr, 0, s_send.length());
                out.write(sendstr);

                byte[] rebyte = new byte[50];
                in.read(rebyte);
                String tmp = new String(rebyte);
                Character prelen = tmp.charAt(0);
                if(prelen=='1'){
                    int len = Character.getNumericValue(tmp.charAt(1));
                    s = tmp.subSequence(2,len+2).toString();
                    Log.v("down",s);
                }
                else{
                    int len = Character.getNumericValue(tmp.charAt(1))*10+Character.getNumericValue(tmp.charAt(2));
                    s = tmp.subSequence(3,len+3).toString();
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

}
