package com.example.erwan.meteoplus;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MeteoPrevisionFragment extends Fragment {

    private Date date;
    private DayTime dayTime;
    private Meteo meteo;
    private Activity activity;

    public void put (Date date, DayTime dayTime, Meteo meteo, Activity activity) {
        this.date = date;
        this.dayTime = dayTime;
        this.meteo = meteo;
        /*TextView textViewDate = (TextView) activity.findViewById(R.id.textViewDate);
        ImageView imageViewWeather = (ImageView) activity.findViewById(R.id.imageView);
        TextView textViewTemperature = (TextView) activity.findViewById(R.id.textViewTemperature);
        textViewDate.setText(dayTime.toString());
        imageViewWeather.setImageResource(R.drawable.sund);
        textViewTemperature.setText(meteo.getTemperature());*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        Log.v("MyFragment", "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MyFragment", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("MyFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_meteo_prevision, container, false);
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        ImageView imageViewWeather = (ImageView) view.findViewById(R.id.imageView);
        TextView textViewTemperature = (TextView) view.findViewById(R.id.textViewTemperature);
        textViewDate.setText(dayTime.toString());
        imageViewWeather.setImageResource(R.drawable.sund);
        textViewTemperature.setText(meteo.getTemperature());
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("MyFragment", "onDestroy");
    }

}
