package com.inscripts.cometchatpulse.demo;

import android.app.Application;
import android.widget.Toast;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;

public class CometApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        CometChat.init(this, StringContract.AppDetails.APP_ID, new CometChat.InitListener() {
            @Override
            public void onResult(CometChatException e) {

                if (e == null) {

                    Toast.makeText(CometApplication.this, "SetUp Complete", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CometApplication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
