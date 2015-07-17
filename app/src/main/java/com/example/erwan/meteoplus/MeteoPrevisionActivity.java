package com.example.erwan.meteoplus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MeteoPrevisionActivity extends FragmentActivity {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    private List<Date> dates;
    private Date currentDate;
    private DayTime currentDayTime;
    private MeteoMutiple meteoMutiple;
    private Fragment currentFragment;

    private String cityDisplay;
    // Informations récupérées sur OpenWeather
    private Document doc;

    //reload time in minutes
    private final static int RELOAD_TIME = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_prevision);
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
        }
        this.dates = new ArrayList<>();
        Bundle b = getIntent().getExtras();
        if(b.containsKey("city")) {
            this.cityDisplay = b.getString("city");
            this.meteoMutiple = new MeteoMutiple(this, this.cityDisplay);
            if (this.meteoMutiple.exist()) {
                this.meteoMutiple.load();
                if (!this.meteoMutiple.isValid(RELOAD_TIME) && isNetworkAvailable()) {
                    this.meteoMutiple = this.getWeatherByCityForFiveDay(this.cityDisplay);
                    this.meteoMutiple.save();
                } else {
                    this.dates = this.meteoMutiple.getDates();
                    this.currentDate = this.dates.get(0);
                }
                this.init();
            } else {
                if (isNetworkAvailable()) {
                    this.meteoMutiple = this.getWeatherByCityForFiveDay(this.cityDisplay);
                    this.meteoMutiple.save();
                    this.init();
                } else {
                    TextView textViewText = (TextView) findViewById(R.id.textViewText);
                    textViewText.setText(getResources().getString(R.string.no_internet_connexion));
                    TextView textViewCity = (TextView) findViewById(R.id.textViewCity);
                    textViewCity.setText(this.cityDisplay);
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "fragment", currentFragment);
    }

    private void init () {
        TextView textViewText = (TextView) findViewById(R.id.textViewText);
        textViewText.setVisibility(View.INVISIBLE);
        TextView textViewCity = (TextView) findViewById(R.id.textViewCity);
        textViewCity.setText(this.cityDisplay);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Date date : dates) {
            calendar.setTime(date);
            list.add(Utils.getStringDay(calendar.get(Calendar.DAY_OF_WEEK), this));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,list);
        // Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDate = dates.get(position);
                mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.pager);
                mViewPager.setAdapter(mDemoCollectionPagerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }

    // On vérifie que l'on est connecté à internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getXmlWithCityForFiveDay(String city){

        String urlCityMeteo= "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&mode=xml&units=metric&lang=fr&APPID=cd3f1c67011fafe48586264c65fb5d8f";

        RetrieveWeatherXml xml = new RetrieveWeatherXml();
        try {
            this.doc = xml.execute(urlCityMeteo).get();
        } catch (InterruptedException | ExecutionException e) {
            this.doc = null;
        }
    }

    public MeteoMutiple getWeatherByCityForFiveDay (String city) {
        this.getXmlWithCityForFiveDay(city);
        MeteoMutiple meteoMutiple = new MeteoMutiple(this, city);
        NodeList entries = doc.getElementsByTagName("time");
        DayTime previousDayTime;
        Date todayDate = null;
        Double temperature = 0.0;
        String weather = "";
        String humidity = "";
        String pressure = "";
        String speed = "";
        String direction = "";
        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        int nbValue = 0;
        Node node = entries.item(0);
        NamedNodeMap nodeMap = node.getAttributes();
        Node from = nodeMap.getNamedItem("from");
        Date fromDate = this.getDate(from);
        Node to = nodeMap.getNamedItem("to");
        Date toDate = this.getDate(to);
        previousDayTime = DayTime.getDayTime(fromDate, toDate);
        todayDate = fromDate;
        this.dates.add(todayDate);
        NodeList nodeList = node.getChildNodes();
        for (int j = 0 ; j < nodeList.getLength() ; j++) {
            Node child = nodeList.item(j);
            if (child.getNodeName().equals("temperature")) {
                NamedNodeMap attributes = child.getAttributes();
                Node value = attributes.getNamedItem("value");
                temperature += Double.parseDouble(value.getNodeValue());
                Node minNode = attributes.getNamedItem("min");
                double minTmp = Double.parseDouble(minNode.getNodeValue());
                if (minTmp < min) {
                    min = minTmp;
                }
                Node maxNode = attributes.getNamedItem("max");
                double maxTmp = Double.parseDouble(maxNode.getNodeValue());
                if (maxTmp > max) {
                    max = maxTmp;
                }
                nbValue++;
            } else if (child.getNodeName().equals("symbol")) {
                NamedNodeMap attributes = child.getAttributes();
                Node value = attributes.getNamedItem("var");
                weather = value.getNodeValue();
            }
        }
        for (int i = 1 ; i < entries.getLength() ; i++) {
            node = entries.item(i);
            nodeMap = node.getAttributes();
            from = nodeMap.getNamedItem("from");
            fromDate = this.getDate(from);
            to = nodeMap.getNamedItem("to");
            toDate = this.getDate(to);
            DayTime dayTime = DayTime.getDayTime(fromDate, toDate);
            if (previousDayTime  != dayTime) {
                Meteo meteo = new Meteo(city, this);
                temperature = temperature / nbValue;
                meteo.setTemperature(String.valueOf(temperature.intValue()));
                meteo.setMin(String.valueOf(min.intValue()));
                meteo.setMax(String.valueOf(max.intValue()));
                meteo.setDirection(direction);
                meteo.setSpeed(speed);
                meteo.setPressure(pressure);
                meteo.setHumidity(humidity);
                meteo.setWeather(weather);
                meteo.setUnits(getResources().getString(R.string.celsius));
                meteoMutiple.setMeteo(todayDate, previousDayTime, meteo);
                if (dayTime == DayTime.NUIT) {
                    todayDate = fromDate;
                    this.dates.add(todayDate);
                }
                if (this.currentDate == null) {
                    this.currentDate = todayDate;
                }
                if (this.currentDayTime == null) {
                    this.currentDayTime = previousDayTime;
                }
                temperature = 0.0;
                min = Double.MAX_VALUE;
                max = Double.MIN_VALUE;
                nbValue = 0;
            }
            previousDayTime = dayTime;
            nodeList = node.getChildNodes();
            for (int j = 0 ; j < nodeList.getLength() ; j++) {
                Node child = nodeList.item(j);
                if (child.getNodeName().equals("temperature")) {
                    NamedNodeMap attributes = child.getAttributes();
                    Node value = attributes.getNamedItem("value");
                    temperature += Double.parseDouble(value.getNodeValue());
                    Node minNode = attributes.getNamedItem("min");
                    double minTmp = Double.parseDouble(minNode.getNodeValue());
                    if (minTmp < min) {
                        min = minTmp;
                    }
                    Node maxNode = attributes.getNamedItem("max");
                    double maxTmp = Double.parseDouble(maxNode.getNodeValue());
                    if (maxTmp > max) {
                        max = maxTmp;
                    }
                    nbValue++;
                } else if (child.getNodeName().equals("symbol")) {
                    NamedNodeMap attributes = child.getAttributes();
                    Node value = attributes.getNamedItem("var");
                    weather = value.getNodeValue();
                }
            }
        }
        return meteoMutiple;
    }

    private Date getDate (Node node) {
        try {
            String fromValue = node.getNodeValue();
            fromValue = fromValue.replace("T", " ");
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromValue);
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
            fragment.put(currentDate, currentDayTime, meteoMutiple.getMeteo(currentDate, currentDayTime));
            currentFragment = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return meteoMutiple.size(currentDate);
        }
    }

}
