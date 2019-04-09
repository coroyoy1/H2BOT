package com.example.administrator.h2bot.customer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.CustomerToMerchantNotifModel;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.TransactionNoModel;
import com.example.administrator.h2bot.models.UserFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.util.DateTime;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.codec.binary.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.administrator.h2bot.customer.CustomerMapFragment.EXTRA_stationID;
import static com.example.administrator.h2bot.customer.CustomerMapFragment.EXTRA_stationName;

public class CustomerChatbotActivity extends AppCompatActivity {
    private static final String TAG = CustomerChatbotActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;
    private ImageView sendBtn;
    Dialog dialog;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference notifRef, merchantDeviceTokenRef;
    FirebaseAuth myAuth;
    TextView stationNameTv;
    ArrayList<UserFile> userFile;
    ArrayList<TransactionNoModel> totalTransactionNo;
    ArrayList<OrderFileModel> orderFile;
    ArrayList<CustomerToMerchantNotifModel> customerNotifRef;
    String stationId;
    String device_token_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity_chatbot);

        Intent intent = getIntent();
        String stationName = intent.getStringExtra(EXTRA_stationName);
        stationNameTv = findViewById(R.id.stationNameTv);
        stationNameTv.setText(stationName + "'s Assistant");

        dialog = new Dialog(this);
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);
        myAuth = FirebaseAuth.getInstance();
        notifRef = db.getReference("Notification");
        merchantDeviceTokenRef = db.getReference("User_Account_File");
        userFile = new ArrayList<>();
        totalTransactionNo = new ArrayList<TransactionNoModel>();
        orderFile = new ArrayList<OrderFileModel>();
        customerNotifRef = new ArrayList<CustomerToMerchantNotifModel>();

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        // Java V2
        initV2Chatbot();

        // Send request to initiate chat before user types input
        initiateChatbot();
    }

    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();


            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(CustomerChatbotActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");

            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            new RequestJavaV2Task(CustomerChatbotActivity.this, session, sessionsClient, queryInput).execute();
        }
    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(CustomerChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(CustomerChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    public void callbackV2(DetectIntentResponse detectIntentResponse) {
        if (detectIntentResponse != null) {
            // process aiResponse here
            String botReply = detectIntentResponse.getQueryResult().getFulfillmentText();
            Log.d(TAG, "V2 Bot Reply: " + botReply);
            if (botReply.equalsIgnoreCase("Sorry, the products of this station has not been setup. Please order again later. Have a nice day ahead!")) {
                queryEditText.setText("You can't reply on this conversation.");
                queryEditText.setEnabled(false);
                sendBtn.setEnabled(false);
            } else if (botReply.equalsIgnoreCase("Communication error or check your internet connection.")) {
                queryEditText.setText("You can't reply on this conversation.");
                queryEditText.setEnabled(false);
                sendBtn.setEnabled(false);
            } else if (botReply.equalsIgnoreCase("Okay, see you soon.")) {
                queryEditText.setText("You can't reply on this conversation.");
                queryEditText.setEnabled(false);
                sendBtn.setEnabled(false);
            }
//            else if (botReply.equalsIgnoreCase("Your order is now on validation. We will notify you for more details.")) {
//                Button okayBtn;
//                dialog.setContentView(R.layout.customer_chatbot_order_info_popup);
//                dialog.setCanceledOnTouchOutside(false);
//                okayBtn = dialog.findViewById(R.id.okayBtn);
//
//
//                okayBtn.setOnClickListener(new Vie+w.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        startActivity(new Intent(CustomerChatbotActivity.this, CustomerMainActivity.class));
//                    }
//                });
//                this.dialog.show();
//            }

            if (botReply.equalsIgnoreCase("Okay. See you soon!")) {
                startActivity(new Intent(this, CustomerMainActivity.class));
            }
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("Communication error or check your internet connection.", BOT);
        }
    }

    private void initiateChatbot() {
        DatabaseReference userFileRef = db.getReference("User_File");
        FirebaseUser get_UId = myAuth.getCurrentUser();
        String get_id = get_UId.getUid();
        Intent intent = getIntent();
        stationId = intent.getStringExtra(EXTRA_stationID);

        userFileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UserFile user = data.getValue(UserFile.class);
                    if (user.getUser_getUID().equals(get_id)) {
                        userFile.add(user);
                        String customerId = user.getUser_getUID();
                        String msg = stationId + " " + customerId;
                        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
                        new RequestJavaV2Task(CustomerChatbotActivity.this, session, sessionsClient, queryInput).execute();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}