package com.example.androidpostsapp;

import android.app.Application;
import android.content.Context;

import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;

public class AppObject extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        try {
            Configuration.Builder builder = new Configuration.Builder();

            // Perform any other configuration steps (optional)
            builder.firebaseRootPath("prod");

            // Initialize the Chat SDK
            ChatSDK.initialize(context, builder.build(), FirebaseNetworkAdapter.class, BaseInterfaceAdapter.class);

            // File storage is needed for profile image upload and image messages
            FirebaseFileStorageModule.activate();



        } catch (ChatSDKException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
