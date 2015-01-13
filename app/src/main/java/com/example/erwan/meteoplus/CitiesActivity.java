package com.example.erwan.meteoplus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class CitiesActivity extends ActionBarActivity {

   ListView lvList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        setTitle("Villes suggérées");

        Toast.makeText(this, "On create villes activity", Toast.LENGTH_SHORT).show();

        lvList = (ListView)findViewById(R.id.city_list);
        final String[] listStrings = {"Paris","London","Madrid","New York","Sydney","Lyon","Moscow","Brest","Berlin","Tokyo","Montreal","Los Angeles","Las Vegas","Hong Kong","Mexico","Miami","Washington","Roma","Lille","Versailles"};
        lvList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listStrings));

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("city_selected", listStrings[position]);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }
}
