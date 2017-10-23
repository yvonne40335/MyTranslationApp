package com.example.mytranslationapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavCollection extends AppCompatActivity {

    private DataAdapter mDbHelper;
    SideBar sidebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_collection);
        setTitle("Favorites");

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_fav);
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
                        Toast.makeText(FavCollection.this,"Fav",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_camera:
                        Intent intentCa = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intentCa);
                        finish();
                        break;
                    case R.id.action_gallery:
                        Toast.makeText(FavCollection.this,"Gallery",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        mDbHelper = new DataAdapter(FavCollection.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        final RecyclerView rvVocabularies = (RecyclerView) findViewById(R.id.recyclerView);
        final RecyclerAdapter adapter = new RecyclerAdapter(this.getAllData());
        rvVocabularies.setHasFixedSize(true);
        rvVocabularies.setAdapter(adapter);
        rvVocabularies.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvVocabularies.addItemDecoration(itemDecoration);

        sidebar = (SideBar) findViewById(R.id.sidebar);
        sidebar.setOnLetterTouchListener(new SideBar.OnLetterTouchListener() {
            @Override
            public void OnTouchLetterChange(String letterTouched) {
                int pos = adapter.getLetterPosition(letterTouched);
                Log.v("favcollection",letterTouched);
                if (pos!=-1){
                    //rvVocabularies.smoothScrollToPosition(pos);
                    ((LinearLayoutManager)rvVocabularies.getLayoutManager()).scrollToPositionWithOffset(pos,0);
                }
            }
        });
    }

    public List<Vocabulary> getAllData() {
        List<Vocabulary> list = new ArrayList<>();
        Cursor favdata;
        if(mDbHelper==null)
        {
            mDbHelper.createDatabase();
            mDbHelper.open();
        }
        favdata = mDbHelper.getFavData();
        String alphabet="0",tmp;
        while (favdata.moveToNext()) {
            String name = favdata.getString(1);
            tmp = Character.toString(name.charAt(0)).toUpperCase();
            /*if(alphabet.equals("A")){
                alphabet=tmp;
                Log.v("getalldata",tmp);
            }*/
            if(!tmp.equals(alphabet)){
                Log.v("fav",alphabet);
                alphabet=tmp;
                Vocabulary word1 = new Vocabulary(Vocabulary.WORD_TYPE);
                Vocabulary word2 = new Vocabulary(Vocabulary.ALPHABET_TYPE);
                word2.setWord(alphabet);
                list.add(word2);
                word1.setWord(name);
                list.add(word1);
                Log.v("fav",Integer.toString(word2.type));
            }
            else{
                Vocabulary word = new Vocabulary(Vocabulary.WORD_TYPE);
                word.setWord(name);
                Log.v("getalldata",name);
                list.add(word);
            }
            //Log.v("fav",Character.toString(alphabet));
        }
        return list;
    }

    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

}
