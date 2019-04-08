package com.example.administrator.h2bot.models;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.administrator.h2bot.R;

public class TimePickerStartingTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT,this,hour,minute,false);
        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        String status = "AM";
        if(hourOfDay > 11)
        {
            status = "PM";
        }
        int hour_of_12_hour_format;

        if(hourOfDay > 11){
            hour_of_12_hour_format = hourOfDay - 12;
        }
        else {
            hour_of_12_hour_format = hourOfDay;
        }


        TextView tv = getActivity().findViewById(R.id.startTimeTextView);
        String minuteValue = String.format("%02d", minute);
        tv.setText(hour_of_12_hour_format + " : " + minuteValue + " " + status);
    }
}