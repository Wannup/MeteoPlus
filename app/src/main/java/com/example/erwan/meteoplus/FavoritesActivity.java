package com.example.erwan.meteoplus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class FavoritesActivity extends Activity {

    ListView favoritesList;
    private ArrayList<String> favorites;
    private ArrayAdapter<String> aadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle("Villes favorites");

        favoritesList = (ListView)findViewById(R.id.favoritesView);

        this.favorites = getIntent().getStringArrayListExtra("favorites");

        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("city_selected", favorites.get(position));
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        this.aadapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                this.favorites);
        favoritesList.setAdapter(aadapter);
    }




}
