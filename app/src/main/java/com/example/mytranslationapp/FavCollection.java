package com.example.mytranslationapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class FavCollection extends AppCompatActivity {

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
    }
}
