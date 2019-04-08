package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.h2bot.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class WSCustomerAffiliateFragment extends Fragment {


    private CircleImageView imageCustomer, imageAffiliate;
    private TextView customerOrderNo, customerName, customerContactNo, customerWaterType, customerQuantity,
    customerDeliveryPriceGallon, customerAddress, customerDateToDeliver, customerService, customerTotalPrice,
    affiliateName, affilateContact, affiliateEmail, affiliateAddress, stationPoints, status;
    private Button back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_customer_affiliate, container, false);
        displayText(view);
        return view;
    }

    private void displayText(View view)
    {
        //TextView
        customerOrderNo = view.findViewById(R.id.orderNoAFF);
        customerName = view.findViewById(R.id.customerAFF);
        customerContactNo = view.findViewById(R.id.contactNoAFF);
        customerWaterType = view.findViewById(R.id.waterTypeAFF);
        customerQuantity = view.findViewById(R.id.itemQuantityAFF);
        customerDeliveryPriceGallon = view.findViewById(R.id.deliveryPPGAFF);
        customerAddress = view.findViewById(R.id.addressAFF);
        customerDateToDeliver = view.findViewById(R.id.datedeliveredAFF);
        customerService = view.findViewById(R.id.serviceAFF);
        customerTotalPrice = view.findViewById(R.id.totalPriceAFF);
        affiliateName = view.findViewById(R.id.affiliateNameaffAFF);
        affilateContact = view.findViewById(R.id.contactNoaffAFF);
        affiliateEmail = view.findViewById(R.id.emailaffAFF);
        affiliateAddress = view.findViewById(R.id.addressaffAFF);
        stationPoints = view.findViewById(R.id.pointsaffAFF);

        //Image View
        imageCustomer = view.findViewById(R.id.imageViewCustomerAFF);
        imageAffiliate = view.findViewById(R.id.imageViewaffAFF);

        //Status and Button
        status = view.findViewById(R.id.statusAFF);
        back = view.findViewById(R.id.backbuttonAFF);
    }
}
