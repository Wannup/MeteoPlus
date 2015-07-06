package com.example.erwan.meteoplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MeteoPrevisionActivity extends FragmentActivity {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    private Date currentDate;
    private DayTime currentDayTime;
    MeteoMutiple meteoMutiple;

    private String cityDisplay;
    // Informations récupérées sur OpenWeather
    private Document doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_prevision);

        Bundle b = getIntent().getExtras();
        if(b.containsKey("city")) {
            this.cityDisplay = b.getString("city");
            this.meteoMutiple = this.getWeatherByCityForFiveDay(this.cityDisplay);
        }

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }

    public void getXmlWithCityForFiveDay(String city){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

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

    public MeteoMutiple getWeatherByCityForFiveDay (String city) {
        this.getXmlWithCityForFiveDay(city);
        MeteoMutiple meteoMutiple = new MeteoMutiple(this);
        NodeList entries = doc.getElementsByTagName("time");
        DayTime previousDayTime = null;
        Date todayDate = null;
        String temperature = "";
        String weather = "";
        String humidity = "";
        String pressure = "";
        String speed = "";
        String direction = "";
        String min = "";
        String max = "";
        for (int i = 0 ; i < entries.getLength() ; i++) {
            Node node = entries.item(i);
            NamedNodeMap nodeMap = node.getAttributes();
            Node from = nodeMap.getNamedItem("from");
            Date fromDate = this.getDate(from);
            Node to = nodeMap.getNamedItem("to");
            Date toDate = this.getDate(to);
            DayTime dayTime = DayTime.getDayTime(fromDate, toDate);
            System.out.println("dayTime : " + dayTime);
            if (previousDayTime != null && previousDayTime != dayTime) {
                Meteo meteo = new Meteo(city, this);
                meteo.setTemperature(temperature);
                meteo.setDirection(direction);
                meteo.setSpeed(speed);
                meteo.setPressure(pressure);
                meteo.setHumidity(humidity);
                meteo.setWeather(weather);
                if (todayDate == null || previousDayTime == DayTime.NUIT) {
                    todayDate = fromDate;
                }
                meteoMutiple.setMeteo(todayDate, previousDayTime, meteo);
                if (this.currentDate == null) {
                    this.currentDate = fromDate;
                }
                if (this.currentDayTime == null) {
                    this.currentDayTime = previousDayTime;
                }
            }
            previousDayTime = dayTime;
            NodeList nodeList = node.getChildNodes();
            for (int j = 0 ; j < nodeList.getLength() ; j++) {
                Node child = nodeList.item(j);
                if (child.getNodeName().equals("temperature")) {
                    NamedNodeMap attributes = child.getAttributes();
                    Node value = attributes.getNamedItem("value");
                    temperature = value.getNodeValue();
                    System.out.println("value : " + temperature);
                    Node minNode = attributes.getNamedItem("min");
                    min = minNode.getNodeValue();
                    System.out.println("min : " + min);
                    Node maxNode = attributes.getNamedItem("max");
                    max = maxNode.getNodeValue();
                    System.out.println("max : " + max);
                }
            }
        }
        return meteoMutiple;
    }

    private Date getDate (Node node) {
        try {
            String fromValue = node.getNodeValue();
            System.out.println("From : " + fromValue);
            fromValue = fromValue.replace("T", " ");
            Date fromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromValue);
            return fromDate;
        } catch (ParseException e) {
            return null;
        }
    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            MeteoPrevisionFragment fragment = new MeteoPrevisionFragment();
            currentDayTime = meteoMutiple.getDayTime(currentDate, i);
            fragment.put(currentDate, currentDayTime, meteoMutiple.getMeteo(currentDate, currentDayTime), MeteoPrevisionActivity.this);
            return fragment;
        }

        @Override
        public int getCount() {
            return meteoMutiple.size(currentDate);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }



/*
    // Informations récupérées sur OpenWeather
    private Document doc;

    // Elements graphiques
    private TextView meteoInfo;
    private CheckBox mFavButton;
    private Button mFavorites;
    private Button mCities;
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

    // Etat de l'orientation du téléphone
    private int orientation;

    // Ville affichée
    private String cityDisplay;

    //reload time in minutes
    private final static int RELOAD_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_prevision);

        meteoInfo = (TextView) findViewById(R.id.meteoInfo);
        weatherImg = (ImageView) findViewById(R.id.weatherImg);
        mFavButton = (CheckBox) findViewById(R.id.fav_button);
        mFavorites = (Button) findViewById(R.id.favorites);
        mCities = (Button) findViewById(R.id.cities);
        temperature = (TextView) findViewById(R.id.tempView);
        lastModified = (TextView) findViewById(R.id.textViewLastModified);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        restoreFavorites();

        mFavButton.setChecked(false);
        mFavButton.setVisibility(View.INVISIBLE);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavButton.isChecked()){
                    //addItemToFavorites(meteo.getName());
                } else {
                    //deleteItemFromFavorites(meteo.getName());
                }
            }
        });

        mFavorites.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(MeteoPrevisionActivity.this, FavoritesActivity.class);
                intentMain.putStringArrayListExtra("favorites", favorites);
                startActivityForResult(intentMain, 1);
            }
        });

        mCities.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(MeteoPrevisionActivity.this, CitiesActivity.class);
                startActivityForResult(intentMain, 1);
            }
        });

        if(savedInstanceState != null){
            if(savedInstanceState.getString("actualCity") != null) {
                this.cityDisplay = savedInstanceState.getString("actualCity");
                this.getWeatherByCityForFiveDay(this.cityDisplay);
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
            this.getWeatherByCityForFiveDay(data.getStringExtra("city_selected"));
        }
    }

    // On vérifie que l'on est connecté à internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
/*
    public void getXmlWithCityForFiveDay(String city){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

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

    public Element getNode (String name) {
        NodeList entries = doc.getElementsByTagName(name);
        Element node = (Element) entries.item(0);
        return node;
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

    public MeteoMutiple getWeatherByCityForFiveDay (String city) {
        this.getXmlWithCityForFiveDay(city);
        MeteoMutiple meteoMutiple = new MeteoMutiple(this);
        NodeList entries = doc.getElementsByTagName("time");
        DayTime previousDayTime = null;
        String temperature = "";
        String weather = "";
        String humidity = "";
        String pressure = "";
        String speed = "";
        String direction = "";
        String min = "";
        String max = "";
        for (int i = 0 ; i < entries.getLength() ; i++) {
            Node node = entries.item(i);
            NamedNodeMap nodeMap = node.getAttributes();
            Node from = nodeMap.getNamedItem("from");
            Date fromDate = this.getDate(from);
            Node to = nodeMap.getNamedItem("to");
            Date toDate = this.getDate(to);
            DayTime dayTime = DayTime.getDayTime(fromDate, toDate);
            if (previousDayTime == null) {
                previousDayTime = dayTime;
            } else if (previousDayTime != dayTime) {
                Meteo meteo = new Meteo(city, this);
                meteo.setTemperature(temperature);
                meteo.setDirection(direction);
                meteo.setSpeed(speed);
                meteo.setPressure(pressure);
                meteo.setHumidity(humidity);
                meteo.setWeather(weather);
                meteoMutiple.setMeteo(fromDate, previousDayTime, meteo);
            }
            previousDayTime = dayTime;
            NodeList nodeList = node.getChildNodes();
            for (int j = 0 ; j < nodeList.getLength() ; j++) {
                Node child = nodeList.item(j);
                if (child.getNodeName().equals("temperature")) {
                    NamedNodeMap attributes = child.getAttributes();
                    Node value = attributes.getNamedItem("value");
                    temperature = value.getNodeValue();
                    System.out.println("value : " + temperature);
                    Node minNode = attributes.getNamedItem("min");
                    min = minNode.getNodeValue();
                    System.out.println("min : " + min);
                    Node maxNode = attributes.getNamedItem("max");
                    max = maxNode.getNodeValue();
                    System.out.println("max : " + max);
                }
            }
        }
        return meteoMutiple;
    }

    private Date getDate (Node node) {
        try {
            String fromValue = node.getNodeValue();
            System.out.println("From : " + fromValue);
            fromValue = fromValue.replace("T", " ");
            Date fromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromValue);
            return fromDate;
        } catch (ParseException e) {
            return null;
        }
    }
    */
}
