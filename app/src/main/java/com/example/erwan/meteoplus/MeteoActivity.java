package com.example.erwan.meteoplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MeteoActivity extends Activity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    // Informations récupérées sur OpenWeather
    private Document doc;

    // Meteo
    private Meteo meteo;

    // Elements graphiques
    private TextView meteoInfo;
    private CheckBox mFavButton;
    private Button mFavorites;
    private Button mCities;
    private Button previsionButton;
    private ImageView weatherImg;
    private TextView temperature;
    private TextView lastModified;

    // Latitude et longitude
    private double latitude = 0;
    private double longitude = 0;

    // Liste de favoris
    private ArrayList<String> favorites;

    // Sauvegarde des favoris
    private SharedPreferences sharedPref;

    // Ville affichée
    private String cityDisplay;

    //reload time in minutes
    private final static int RELOAD_TIME = 60;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo);

        meteoInfo = (TextView) findViewById(R.id.meteoInfo);
        weatherImg = (ImageView) findViewById(R.id.weatherImg);
        mFavButton = (CheckBox) findViewById(R.id.fav_button);
        mFavorites = (Button) findViewById(R.id.favorites);
        mCities = (Button) findViewById(R.id.cities);
        previsionButton = (Button) findViewById(R.id.previsionButton);
        temperature = (TextView) findViewById(R.id.tempView);
        lastModified = (TextView) findViewById(R.id.textViewLastModified);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.container);
        Date date = new Date();
        DayTime dayTime = DayTime.getDayTime(date, date);
        layout.setBackground(getResources().getDrawable(Utils.getBackground(dayTime)));

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        restoreFavorites();

        mFavButton.setChecked(false);
        mFavButton.setVisibility(View.INVISIBLE);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavButton.isChecked()){
                    addItemToFavorites(meteo.getName());
                } else {
                    deleteItemFromFavorites(meteo.getName());
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

        previsionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeteoActivity.this, MeteoPrevisionActivity.class);
                intent.putExtra("city", cityDisplay);
                startActivity(intent);
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
                }
            }
        };
        myLocation = new MyLocation();

        if(cityDisplay==null) {
            myLocation.getLocation(getApplicationContext(), locationResult);
            if(longitude == 0 || latitude == 0){
                meteoInfo.setText(getResources().getString(R.string.error_gps));
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Affichage de la Météo
    public void displayMeteo(String city, double lat, double lon) {
        this.meteo = null;
        this.lastModified.setVisibility(View.INVISIBLE);
        if(!city.equals("0")) {
            this.cityDisplay = city;
            meteo = new Meteo(city, this);
            if (meteo.exist()) {
                meteo.load();
                if (!meteo.isValid(RELOAD_TIME) && isNetworkAvailable()) {
                    getXmlWithCity(city);
                    if (this.doc != null) {
                        meteo.delete();
                        this.loadMeteo();
                        meteo.save();
                    } else {
                        this.cityNotExists(city);
                    }
                } else {
                    this.lastModified.setVisibility(View.VISIBLE);
                    this.lastModified.setText(getResources().getString(R.string.modify_the, meteo.getDate()));
                }
            } else if(isNetworkAvailable()) {
                getXmlWithCity(city);
                if (this.doc != null) {
                    this.loadMeteo();
                    meteo.save();
                } else {
                    this.cityNotExists(city);
                }
            } else {
                this.noInternetConnexion();
            }
        } else {
            if(isNetworkAvailable()) {
                getXmlWithLocation(lat, lon);
                if (this.doc != null) {
                    meteo = new Meteo(getString("name", getNode("city")), this);
                    this.loadMeteo();
                    meteo.save();
                } else {
                    this.cityNotExists(city);
                }
            } else {
                this.noInternetConnexion();
            }
        }

        if (meteo != null) {
            mFavButton.setVisibility(View.VISIBLE);
            previsionButton.setVisibility(View.VISIBLE);
            mFavButton.setChecked(false);
            // Affichage de l'image
            weatherImg.setImageResource(Utils.getImageByWeather(meteo.getWeather()));

            // Affichage de la température
            if (meteo.getTemperature().charAt(0) == '-' && meteo.getTemperature().charAt(1) != '0') {
                temperature.setText(meteo.getTemperature().charAt(0) + " " + meteo.getTemperature().substring(1) + meteo.getUnits());
            } else {
                temperature.setText(meteo.getTemperature() + " " + meteo.getUnits());
                /*if (meteo.getTemperature().charAt(1) == '0') {
                    temperature.setText(meteo.getTemperature().charAt(1) + " " + meteo.getUnits());
                } else {
                    temperature.setText(meteo.getTemperature().charAt(0) + " " + meteo.getUnits());
                }*/
            }

            meteoInfo.setText(Html.fromHtml("<b>" + meteo.getName() + "<br /><br /></br>" +
                    "<small>Humidité: " + meteo.getHumidity() + " %<br /><br />" +
                    "Pression: " + meteo.getPressure() + " hPa<br /><br />" +
                    "Vitesse du vent: " + meteo.getSpeed() + " m/s</small>"));

            if (checkItemInFavorites(city)) {
                mFavButton.setChecked(true);
            } else {
                mFavButton.setChecked(false);
            }
        }
    }

    private void noInternetConnexion () {
        mFavButton.setVisibility(View.INVISIBLE);
        previsionButton.setVisibility(View.INVISIBLE);
        weatherImg.setImageResource(0);
        temperature.setText("");
        meteoInfo.setText(getResources().getString(R.string.no_internet_connexion));
        meteo = null;
    }

    private void cityNotExists (String name) {
        mFavButton.setVisibility(View.INVISIBLE);
        previsionButton.setVisibility(View.INVISIBLE);
        weatherImg.setImageResource(0);
        temperature.setText("");
        meteoInfo.setText(getResources().getString(R.string.city_not_exists, name));
        meteo = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sharedPref.edit();
        sharedPref.getAll().clear();

        // Sauvegarde des favoris pour qu'ils soit disponible à la prochaine ouverture
        for(int i=0; i<favorites.size();i++){
            editor.putString(favorites.get(i), favorites.get(i));
            editor.apply();
        }
    }

    public void addItemToFavorites(String name) {
        Toast.makeText(this, getResources().getString(R.string.city_add_to_favorites), Toast.LENGTH_SHORT).show();
        favorites.add(name);
    }

    public void deleteItemFromFavorites(String name) {
        Toast.makeText(this, getResources().getString(R.string.city_remove_from_favorites), Toast.LENGTH_SHORT).show();
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
        } catch (InterruptedException | ExecutionException e) {
            this.doc = null;
        }
    }

    public void getXmlWithLocation(double lat, double lon){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

        RetrieveWeatherXml xml = new RetrieveWeatherXml();
        try {
            this.doc = xml.execute(urlCityMeteo).get();
        } catch (InterruptedException | ExecutionException e) {
            this.doc = null;
        }
    }

    private void loadMeteo () {
        this.meteo.setTemperature(getString("value", getNode("temperature")));
        this.meteo.setWeather(getString("icon", getNode("weather")));
        this.meteo.setHumidity(getString("value", getNode("humidity")));
        this.meteo.setPressure(getString("value", getNode("pressure")));
        this.meteo.setSpeed(getString("value", getNode("speed")));
        this.meteo.setDirection(getString("name", getNode("direction")));
        this.meteo.setUnits(getResources().getString(R.string.celsius));
    }

    public Element getNode (String name) {
        NodeList entries = doc.getElementsByTagName(name);
        return (Element) entries.item(0);
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

        return getResources().getString(R.string.no_information);
    }
}
