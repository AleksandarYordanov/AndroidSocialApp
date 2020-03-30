package com.example.androidpostsapp.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpostsapp.R;
import com.example.androidpostsapp.models.AppUser;
import com.example.androidpostsapp.storage.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.firebase.wrappers.UserWrapper;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUserAdapterViewHolder> {

    private List<AppUser> mData;
    private LayoutInflater mInflater;
    private StorageReference mStorageRef;
    private Context context;


    public SearchUsersAdapter(List<AppUser> mData, Context context) {
        this.mData = mData;

        this.mInflater = LayoutInflater.from(context);
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.context = context;
    }

    @NonNull
    @Override
    public SearchUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_search_layout, parent, false);
        return new SearchUserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapterViewHolder holder, int position) {
        AppUser appUser = mData.get(position);
        holder.username.setText(appUser.getUsername());
        holder.gender.setText(appUser.getGender());
        holder.birthday.setText(appUser.getBirthday());



        StorageReference fileRef = mStorageRef.child(appUser.getImageURL());

        GlideApp.with(context)
                .load(fileRef)
                .into(holder.profilePicture);

        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                UserWrapper wrapper = UserWrapper.initWithEntityId(appUser.getId());
                wrapper.metaOn();
                wrapper.onlineOn();
                User user = wrapper.getModel();
                List<User> users = new ArrayList<>();
                users.add(user);






                Disposable d = ChatSDK.thread().createThread(users)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            // Runs when process completed with error or success
                        })
                        .subscribe(thread -> {
                            // When the thread is created
                            ChatSDK.ui().startChatActivityForID(context, thread.getEntityID());
                        }, throwable -> {
                            // If there is an error

                        });





            }
        });
    }


    public void startPrivateChatWithUser (User user) {




//
//        ChatSDK.thread().createThread(users)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((thread, throwable) -> {
//                    if (throwable == null) {
//                        ChatSDK.ui().startChatActivityForID(context, thread.getEntityID());
//                    } else {
//                        Log.i("!!!",  throwable.toString());
//                    }
//
//                });



//        Thread thread =  ChatSDK.ui().gett;
//
//
//
//        ChatSDK.ui().startChatActivityForID(context,thread.getEntityID());
//        System.out.println();
        //  ChatSDK.ui().startChatActivityForID(context, threadId);



    }





    @Override
    public int getItemCount() {
        return mData.size();
    }
}
