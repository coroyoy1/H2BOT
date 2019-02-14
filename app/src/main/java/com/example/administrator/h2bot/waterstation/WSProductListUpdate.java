package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.administrator.h2bot.R;

public class WSProductListUpdate extends Fragment implements View.OnClickListener {

    Spinner dropdown;
    Button backBut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlistintentupdate, container, false);
        dropdown = view.findViewById(R.id.spinnerUpItem);
        String[] typesProd = new String[]{"Mineral", "Distilled", "Purified", "Alkaline"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,typesProd);
        dropdown.setAdapter(adapter);
        backBut = view.findViewById(R.id.backUpItem);
        backBut.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backUpItem:
                WSProductListFragment additem = new WSProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_ws, additem).addToBackStack(null).commit();
                break;

        }
    }
}
