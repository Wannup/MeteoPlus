package com.example.erwan.meteoplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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

    // Informations récupérées sur OpenWeather
    private Document doc;

    // Elements graphiques
    private TextView meteoInfo;
    private CheckBox mFavButton;
    private Button mFavorites;
    private Button mCities;
    private ImageView weatherImg;
    private TextView temperature;

    // Latitude et longitude
    private double latitude = 0;
    private double longitude = 0;

    // Liste de favoris
    private ArrayList<String> favorites;

    // Sauvegarde des favoris
    private SharedPreferences sharedPref;

    // Etat de l'orientation du téléphone
    private int orientation;

    // Ville affichée
    private String cityDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_meteo_landscape);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_meteo);
        }

        meteoInfo = (TextView) findViewById(R.id.meteoInfo);
        weatherImg = (ImageView) findViewById(R.id.weatherImg);
        mFavButton = (CheckBox) findViewById(R.id.fav_button);
        mFavorites = (Button) findViewById(R.id.favorites);
        mCities = (Button) findViewById(R.id.cities);
        temperature = (TextView) findViewById(R.id.tempView);

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

        if(savedInstanceState != null){
            if(savedInstanceState.getString("actualCity") != null) {
                this.cityDisplay = savedInstanceState.getString("actualCity");
                displayMeteo(cityDisplay, latitude, longitude);
            }
        }

        MyLocation myLocation;
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    displayMeteo("0", latitude, longitude);
                    //displayMeteo("0", 48.853409, 2.3488);
                }
            }
        };
        myLocation = new MyLocation();

        if(cityDisplay==null) {
            myLocation.getLocation(getApplicationContext(), locationResult);
            if(longitude == 0 || latitude == 0){
                meteoInfo.setText("Données GPS éronées");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("actualCity", this.cityDisplay);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            displayMeteo(data.getStringExtra("city_selected"), latitude, longitude);
        }
    }

    // On vérifie que l'on est connecté à internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Affichage de la Météo
    public void displayMeteo(String city, double lat, double lon) {

        if(isNetworkAvailable()) {
            mFavButton.setVisibility(View.VISIBLE);
            mFavButton.setChecked(false);

            Log.w("log", city);
            Log.w("log", "" + lat);
            Log.w("log", "" + lon);

            // Récupération des informations via le nom de la ville ou la location
            if(!city.equals("0")) {
                getXmlWithCity(city);
            } else {
                getXmlWithLocation(lat, lon);
            }

            if(doc != null) {

                // Affichage de l'image
                switch (getWeather()) {
                    case "01d":
                        weatherImg.setImageResource(R.drawable.sund);
                        break;

                    case "01n":
                        weatherImg.setImageResource(R.drawable.moonn);
                        break;

                    case "02d":
                        weatherImg.setImageResource(R.drawable.suncloud);
                        break;

                    case "02n":
                        weatherImg.setImageResource(R.drawable.mooncloud);
                        break;

                    case "03d":
                        weatherImg.setImageResource(R.drawable.cloud);
                        break;

                    case "03n":
                        weatherImg.setImageResource(R.drawable.cloud);
                        break;

                    case "04d":
                        weatherImg.setImageResource(R.drawable.darkcloud);
                        break;

                    case "04n":
                        weatherImg.setImageResource(R.drawable.darkcloud);
                        break;

                    case "09d":
                        weatherImg.setImageResource(R.drawable.rain);
                        break;

                    case "09n":
                        weatherImg.setImageResource(R.drawable.rain);
                        break;

                    case "10d":
                        weatherImg.setImageResource(R.drawable.suncloudrain);
                        break;

                    case "10n":
                        weatherImg.setImageResource(R.drawable.mooncloudrain);
                        break;

                    case "11d":
                        weatherImg.setImageResource(R.drawable.lightning);
                        break;

                    case "11n":
                        weatherImg.setImageResource(R.drawable.lightning);
                        break;

                    case "13d":
                        weatherImg.setImageResource(R.drawable.snow);
                        break;

                    case "13n":
                        weatherImg.setImageResource(R.drawable.snow);
                        break;

                    case "50d":
                        weatherImg.setImageResource(R.drawable.fog);
                        break;

                    case "50n":
                        weatherImg.setImageResource(R.drawable.fog);
                        break;
                    default:
                        weatherImg.setImageResource(0);
                        break;
                }

                // Affichage de la température
                if (getTemperature().charAt(0) == '-' && getTemperature().charAt(1) != '0') {
                    temperature.setText(getTemperature().charAt(0) + " " + getTemperature().charAt(1) + " °C");
                } else {
                    if (getTemperature().charAt(1) == '0') {
                        temperature.setText(getTemperature().charAt(1) + " °C");
                    } else {
                        temperature.setText(getTemperature().charAt(0) + " °C");
                    }
                }

                meteoInfo.setText(Html.fromHtml("<b>" + getCityName() + "<br /><br /></br><small>Humidité: " + getHumidity() + " %<br /><br />Pression: " + getPressure() + " hPa<br /><br />Vitesse du vent: " + getWindSpeed() + " m/s</small>"));

                if (checkItemInFavorites(city)) {
                    mFavButton.setChecked(true);
                } else {
                    mFavButton.setChecked(false);
                }
            }else{
                mFavButton.setVisibility(View.INVISIBLE);
                weatherImg.setImageResource(0);
                temperature.setText("");
                meteoInfo.setText("Impossible de localiser.");
            }
        } else {
            mFavButton.setVisibility(View.INVISIBLE);
            weatherImg.setImageResource(0);
            temperature.setText("");
            meteoInfo.setText("Aucune connexion internet.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedPref.edit();
        sharedPref.getAll().clear();

        // Sauvegarde des favoris pour qu'ils soit disponible à la prochaine ouverture
        for(int i=0; i<favorites.size();i++){
            editor.putString(favorites.get(i), favorites.get(i));
            editor.commit();
        }
    }

    public void addItemToFavorites(String name) {
        Toast.makeText(this, "Ville ajoutée aux favoris", Toast.LENGTH_SHORT).show();
        favorites.add(name);
    }

    public void deleteItemFromFavorites(String name) {
        Toast.makeText(this, "Ville supprimée des favoris", Toast.LENGTH_SHORT).show();
        boolean exist = false;
        int i = 0;
        while (!exist && i < favorites.size()) {
            if (favorites.get(i).equals(name)) {
                favorites.remove(i);
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
    Récupération des infos via xml
     */

    public void getXmlWithCity(String city){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

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

    public void getXmlWithLocation(double lat, double lon){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

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

        this.cityDisplay = getString("name", node);
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
        return getString("icon", node);

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
