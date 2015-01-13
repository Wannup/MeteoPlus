package com.example.erwan.meteoplus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.concurrent.ExecutionException;


public class MeteoActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private TextView meteoInfo;
    private Document doc;
    private CheckBox mFavButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        meteoInfo = (TextView) findViewById(R.id.meteoInfo);

        mFavButton = (CheckBox) findViewById(R.id.fav_button);
        mFavButton.setChecked(false);
        mFavButton.setVisibility(View.INVISIBLE);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavButton.isChecked()){
                    mNavigationDrawerFragment.addItemToLateralMenu(getCityName());
                } else {
                    mNavigationDrawerFragment.deleteItemToLateralMenu(getCityName());
                }
            }
        });
       /* mFavButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                  // When favorite is checked/unchecked

              }
        });*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString(R.string.title_section1);
                break;
            case 2:
            //    mTitle = getString(R.string.title_section2);
                break;
            case 3:
            //    mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.meteo, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        if (mNavigationDrawerFragment.checkItemInLateralMenu(city)){
            mFavButton.setChecked(true);
        } else {
            mFavButton.setChecked(false);
        }
    }

        /**
         * A placeholder fragment containing a simple view.
         */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_meteo, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MeteoActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /*
    Get infos xml
     */

    public void getXml(String city){

        // ajouter réglage des unités de température et langue
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
            //System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
        }

        return "Aucune infos";
    }
}
