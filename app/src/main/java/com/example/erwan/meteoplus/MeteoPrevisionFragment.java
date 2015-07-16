package com.example.erwan.meteoplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MeteoPrevisionFragment extends Fragment {

    private Date date;
    private DayTime dayTime;
    private Meteo meteo;
    private Activity activity;

    public void put (Date date, DayTime dayTime, Meteo meteo) {
        this.date = date;
        this.dayTime = dayTime;
        this.meteo = meteo;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.date = (Date) savedInstanceState.getSerializable("date");
            this.dayTime = (DayTime) savedInstanceState.getSerializable("dayTime");
            this.meteo = (Meteo) savedInstanceState.getSerializable("meteo");
        }
        View view = inflater.inflate(R.layout.fragment_meteo_prevision, container, false);
        TextView textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        ImageView imageViewWeather = (ImageView) view.findViewById(R.id.imageView);
        TextView textViewTemperature = (TextView) view.findViewById(R.id.textViewTemperature);
        TextView textViewMin = (TextView) view.findViewById(R.id.textViewMin);
        TextView textViewMax = (TextView) view.findViewById(R.id.textViewMax);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String day = Utils.getStringDay(calendar.get(Calendar.DAY_OF_WEEK), this.activity);
        textViewDate.setText(day + " (" + dayTime.toString(this.activity) + ")");
        imageViewWeather.setImageResource(Utils.getImageByWeather(meteo.getWeather()));
        textViewTemperature.setText(meteo.getTemperature() + " " + meteo.getUnits());
        textViewMin.setText(this.activity.getResources().getString(R.string.min, meteo.getMin(), meteo.getUnits()));
        textViewMax.setText(this.activity.getResources().getString(R.string.max, meteo.getMax(), meteo.getUnits()));
        view.setBackground(getResources().getDrawable(Utils.getBackground(dayTime)));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("date", this.date);
        savedInstanceState.putSerializable("dayTime", this.dayTime);
        savedInstanceState.putSerializable("meteo", this.meteo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
