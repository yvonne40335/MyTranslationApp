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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_collection);
        setTitle("Favorites");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_fav:
                        Toast.makeText(FavCollection.this,"Fav",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_camera:
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
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

        RecyclerView rvVocabularies = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerAdapter adapter = new RecyclerAdapter(this.getAllData());
        rvVocabularies.setHasFixedSize(true);
        rvVocabularies.setAdapter(adapter);
        rvVocabularies.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvVocabularies.addItemDecoration(itemDecoration);
    }

    public List<Vocabulary> getAllData() {
        List<Vocabulary> list = new ArrayList<>();
        Cursor favdata = mDbHelper.getFavData();
        while (favdata.moveToNext()) {
            // int index2 = cursor.getColumnIndex(DataBaseHelper.NAME);
            String name = favdata.getString(1);
            Vocabulary word = new Vocabulary();
            word.setWord(name);
            list.add(word);
        }
        return list;
    }

    public void onDestroy() {

        super.onDestroy();
        mDbHelper.close();
    }

}
