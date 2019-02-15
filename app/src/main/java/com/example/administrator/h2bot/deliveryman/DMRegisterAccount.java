package com.example.administrator.h2bot.deliveryman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.h2bot.R;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DMRegisterAccount extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText firstNameDM, lastNameDM, addressDM, contactNoDM, emailDM, passwordDM, confirmPassDM;
    Button registerDM, addPhotoBDM;
    CircleImageView imageView;
    Uri uri;
    StorageTask uploadTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_dm_register, container, false);

        firstNameDM = view.findViewById(R.id.RegisterFullNameDM);
        lastNameDM = view.findViewById(R.id.RegisterLastNameDM);
        contactNoDM = view.findViewById(R.id.RegisterContactDM);
        emailDM = view.findViewById(R.id.RegisterAddressDM);
        passwordDM = view.findViewById(R.id.RegisterPasswordDM);
        confirmPassDM = view.findViewById(R.id.ConfirmPasswordDM);
        addressDM = view.findViewById(R.id.RegisterEmailAddressDM);

        addPhotoBDM = view.findViewById(R.id.addPhotoDM);
        registerDM = view.findViewById(R.id.RegisterSignUpDM);
        imageView = view.findViewById(R.id.imageDM);


        addPhotoBDM.setOnClickListener(this);
        registerDM.setOnClickListener(this);

        return view;
    }

    public void GettingData()
    {
        final String firstNameString = firstNameDM.getText().toString();
        final String lastNameString = lastNameDM.getText().toString();
        final String contactString = contactNoDM.getText().toString();
        final String emailString = emailDM.getText().toString();
        final String passwordString = passwordDM.getText().toString();
        final String confirmString = confirmPassDM.getText().toString();
        final String addressString = addressDM.getText().toString();

        final String uriString = uri.toString();

        DMRegisterDocument dmRegisterDocument = new DMRegisterDocument();
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container_ws, dmRegisterDocument)
                .addToBackStack(null)
                .commit();
        Bundle args = new Bundle();
        args.putString("DMFirstName", firstNameString);

    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data.getData() != null)
        {
            uri = data.getData();
            Picasso.get().load(uri).into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addPhotoDM:
                openGallery();
                break;
            case R.id.RegisterSignUpDM:
                break;
        }
    }
}
