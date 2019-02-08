package com.example.administrator.h2bot;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddItemMerchant extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "AddItemMerchant.class";
    Button UploadAPhotoButton,AddItemButton;
    ImageView ItemImage;
    EditText ItemNameEditText,PriceEditText,QualityEditText;
    Spinner waterTypeSpinner;
    private ProgressBar mProgressBar;
    String currentuser;
    Uri mImageUri;
    private String imageUrl;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_item_merchant, container, false);

        //Button
        UploadAPhotoButton = view.findViewById(R.id.UploadAPhotoButton);
        AddItemButton = view.findViewById(R.id.AddItemButton);

        //ImageView
        ItemImage = view.findViewById(R.id.ItemImage);

        //EditView
        ItemNameEditText = view.findViewById(R.id.ItemNameEditText);
        PriceEditText = view.findViewById(R.id.PriceEditText);
        QualityEditText = view.findViewById(R.id.QualityEditText);

        //Spinner
        waterTypeSpinner = view.findViewById(R.id.waterTypeSpinner);
        String[] arraySpinner = new String[]{
                "Mineral", "Distilled", "Purified", "Alkaline"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(adapter);


        //ProgressBar
        mProgressBar = view.findViewById(R.id.progress_bar);

        //design
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("userproduct");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User Items");

        PriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        QualityEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        PriceEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        QualityEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());

        UploadAPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });
        AddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadItem();
                }
            }
        });
        return view;
    }

    private void uploadItem() {
        String itemname = ItemNameEditText.getText().toString().trim();
        String itemprice = PriceEditText.getText().toString().trim();
        String itemquality = QualityEditText.getText().toString().trim();
        if (mImageUri != null) {
            if(itemname.isEmpty()){
                ItemNameEditText.setError("Required!");
                ItemNameEditText.requestFocus();
                return;
            }
            if(itemprice.isEmpty()){
                PriceEditText.setError("Required!");
                PriceEditText.requestFocus();
                return;
            }
            if(itemquality.isEmpty()){
                QualityEditText.setError("Required!");
                QualityEditText.requestFocus();
                return;
            }
            final String uuid = UUID.randomUUID().toString();
            StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uuid).child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String stringUri = uri.toString();
                                    Random rand = new Random();
                                    int randomize = rand.nextInt(1000) + 1;
                                    String numberStore =  Integer.toString(randomize);
                                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                    MerchantGetterSetter upload = new MerchantGetterSetter(
                                            stringUri,
                                            ItemNameEditText.getText().toString().trim(),
                                            waterTypeSpinner.getSelectedItem().toString(),
                                            PriceEditText.getText().toString().trim(),
                                            QualityEditText.getText().toString().trim(),
                                            currentuser,numberStore, uuid
                                    );
                                    Picasso.get().load(R.drawable.ic_menu_camera).into(ItemImage);
                                    ItemNameEditText.setText("");
                                    PriceEditText.setText("");
                                    QualityEditText.setText("");
                                    String uploadId = currentuser;
                                    mDatabaseRef.child(uploadId).child(uuid).setValue(upload);
                                }
                            });

//                            mDatabaseRef.child(uploadId).child(System.currentTimeMillis()
//                                    + "." + getFileExtension(mImageUri)).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void openGalery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(ItemImage);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

}
