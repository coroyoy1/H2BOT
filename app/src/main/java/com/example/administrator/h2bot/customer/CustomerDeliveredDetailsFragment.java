package com.example.administrator.h2bot.customer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.h2bot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerDeliveredDetailsFragment extends Fragment {


    public CustomerDeliveredDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_fragment_delivered_details, container, false);
    }

}
