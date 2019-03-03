package com.example.administrator.h2bot.dealer;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.R;


public class WPBusinessInfoFragment extends Fragment {
    Dialog dialog;
    public WPBusinessInfoFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wpbusiness_info, container, false);
        dialog = new Dialog(getActivity());
        return view;
    }


}
