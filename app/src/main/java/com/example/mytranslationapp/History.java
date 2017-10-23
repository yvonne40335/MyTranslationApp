package com.example.mytranslationapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class History extends AppCompatActivity {

    private Button btnDelete;
    private static DataAdapter mDbHelper;
    private RecyclerAdapter adapter;
    List<Vocabulary> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");

        mDbHelper = new DataAdapter(History.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_his);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_his:
                        Toast.makeText(History.this,"History",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(History.this,"Gallery",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        final RecyclerView rvVocabularies = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(this.getHistoryData());
        rvVocabularies.setHasFixedSize(true);
        rvVocabularies.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        rvVocabularies.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvVocabularies.addItemDecoration(itemDecoration);
        ItemTouchHelper.Callback callback = new RecycleItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvVocabularies);

        btnDelete = (Button)findViewById(R.id.btn_dalete);
        btnDelete.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                mDbHelper.removeHistory();
                data = getHistoryData();
                adapter.notifyRecycler(data);
            }
        });
    }

    public List<Vocabulary> getHistoryData() {
        List<Vocabulary> list = new ArrayList<>();
        Cursor hisdata = mDbHelper.getHisData();
        while (hisdata.moveToNext()) {
            String name = hisdata.getString(1);
            Vocabulary word = new Vocabulary(Vocabulary.WORD_TYPE);
            word.setWord(name);
            list.add(word);
        }
        return list;
    }

    public static void remove(String word)
    {
        mDbHelper.removeOneHistory(word);
    }
    /*private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT){
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir){
                deleteItem(viewHolder.getAdapterPosition());
            }
        }
        return simpleItemTouchCallback;
    }*/


    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

}
