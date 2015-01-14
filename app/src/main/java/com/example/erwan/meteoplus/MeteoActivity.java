package com.example.erwan.meteoplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MeteoActivity extends ActionBarActivity  {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private TextView meteoInfo;
    private Document doc;
    private CheckBox mFavButton;
    private Button mFavorites;
    private Button mCities;

    private ArrayList<String> favorites;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo);
        Toast.makeText(this, "On create meteo activity", Toast.LENGTH_SHORT).show();
        meteoInfo = (TextView) findViewById(R.id.meteoInfo);

        mFavButton = (CheckBox) findViewById(R.id.fav_button);
        mFavorites = (Button) findViewById(R.id.favorites);
        mCities = (Button) findViewById(R.id.cities);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        restoreFavorites();

        mFavButton.setChecked(false);
        mFavButton.setVisibility(View.INVISIBLE);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavButton.isChecked()){
                    addItemToFavorites(getCityName());
                } else {
                    deleteItemFromFavorites(getCityName());
                }
            }
        });

        mFavorites.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(MeteoActivity.this, FavoritesActivity.class);
                intentMain.putStringArrayListExtra("favorites", favorites);
                startActivityForResult(intentMain, 1);
            }
        });

        mCities.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(MeteoActivity.this, CitiesActivity.class);
                startActivityForResult(intentMain, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            displayMeteo(data.getStringExtra("city_selected"));
        }
    }

    public void displayMeteo(String city) {
        // update the main content by replacing fragments
        mFavButton.setVisibility(View.VISIBLE);
        mFavButton.setChecked(false);
        getXml(city);
        meteoInfo.setText(getWeather() + "\nTempérature: " + getTemperature() + "\nHumidité: " + getHumidity() + " %\nPression: " + getPressure() + " hPa\nVent:\nVitesse: " + getWindSpeed() + "\nDirection: " + getWindDirection());

        if (checkItemInFavorites(city)){
            mFavButton.setChecked(true);
        } else {
            mFavButton.setChecked(false);
        }
    }

    public void addItemToFavorites(String name) {
        Toast.makeText(this, "Ville ajoutée aux favoris", Toast.LENGTH_SHORT).show();
        favorites.add(name);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, name);
        editor.commit();
    }

    public void deleteItemFromFavorites(String name) {
        Toast.makeText(this, "Ville supprimée des favoris", Toast.LENGTH_SHORT).show();
        boolean exist = false;
        int i = 0;
        while (!exist && i < favorites.size()) {
            if (favorites.get(i).equals(name)) {
                favorites.remove(i);
                SharedPreferences.Editor editor = sharedPref.edit();
                sharedPref.edit().remove(name);
                editor.commit();
                exist = true;
            }
            i++;
        }
    }

    public boolean checkItemInFavorites(String name) {
        boolean exist = false;
        int i = 0;
        while (!exist && i < favorites.size()) {
            if (favorites.get(i).equals(name)) {
                exist = true;
            }
            i++;
        }
        return exist;
    }

    public void restoreFavorites(){
        Map<String, String> map = (Map<String, String>) sharedPref.getAll();
        this.favorites = new ArrayList<String>(map.values());
    }

    /*
    Get infos xml
     */

    public void getXml(String city){

        // ajouter le réglage des unités de température et langue
        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&mode=xml&units=metric&lang=fr";

        RetrieveWeatherXml xml = new RetrieveWeatherXml();
        try {
            this.doc = xml.execute(urlCityMeteo).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getCityName(){

        NodeList entries = doc.getElementsByTagName("city");

        Element node = (Element) entries.item(0);
        return getString("name", node);

    }

    public String getTemperature(){

        NodeList entries = doc.getElementsByTagName("temperature");

        Element node = (Element) entries.item(0);
        return getString("value", node);

    }

    public String getWeather(){

        NodeList entries = doc.getElementsByTagName("weather");

        Element node = (Element) entries.item(0);
        return getString("value", node);

    }

    public String getHumidity(){

        NodeList entries = doc.getElementsByTagName("humidity");

        Element node = (Element) entries.item(0);
        return getString("value", node);

    }

    public String getPressure(){

        NodeList entries = doc.getElementsByTagName("pressure");

        Element node = (Element) entries.item(0);
        return getString("value", node);

    }

    public String getWindSpeed(){

        NodeList entries = doc.getElementsByTagName("speed");

        Element node = (Element) entries.item(0);
        return getString("value", node);

    }

    public String getWindDirection(){

        NodeList entries = doc.getElementsByTagName("direction");

        Element node = (Element) entries.item(0);
        return getString("name", node);

    }

    protected String getString(String nAttr, Element element) {

        // get a map containing the attributes of this node
        NamedNodeMap attributes = element.getAttributes();

        // get the number of nodes in this map
        int numAttrs = attributes.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            if(attrName.equals(nAttr)){
                return attrValue;
            }
        }

        return "Aucune infos";
    }
}
