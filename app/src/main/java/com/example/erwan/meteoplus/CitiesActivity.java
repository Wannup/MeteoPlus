package com.example.erwan.meteoplus;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class CitiesActivity extends ActionBarActivity {

    ListView lvList = null;
    android.widget.SearchView sView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        setTitle(getResources().getString(R.string.title_activity_cities));

        lvList = (ListView)findViewById(R.id.city_list);
        final String[] listStrings = {"Paris","London","Madrid","New York","Sydney","Lyon","Moscow","Brest","Berlin","Tokyo","Montreal","Los Angeles","Las Vegas","Hong Kong","Mexico","Miami","Washington","Roma","Lille","Versailles"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listStrings);
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("city_selected", listStrings[position]);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        sView = (android.widget.SearchView) findViewById(R.id.searchView);
        sView.setSubmitButtonEnabled(true);
        sView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cities_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent();
                intent.putExtra("city_selected", s);
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }
}
